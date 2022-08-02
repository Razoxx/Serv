package instances;

import events.TreasuresOfTheHerald.TreasuresOfTheHerald;
import gnu.trove.map.hash.TIntObjectHashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledFuture;
import l2p.commons.lang.reference.HardReference;
import l2p.commons.lang.reference.HardReferences;
import l2p.commons.threading.RunnableImpl;
import l2p.gameserver.Config;
import l2p.gameserver.ThreadPoolManager;
import l2p.gameserver.instancemanager.ReflectionManager;
import l2p.gameserver.listener.actor.OnDeathListener;
import l2p.gameserver.listener.actor.player.OnPlayerPartyLeaveListener;
import l2p.gameserver.listener.actor.player.OnTeleportListener;
import l2p.gameserver.model.Creature;
import l2p.gameserver.model.Party;
import l2p.gameserver.model.Player;
import l2p.gameserver.model.Zone;
import l2p.gameserver.model.entity.Reflection;
import l2p.gameserver.model.instances.NpcInstance;
import l2p.gameserver.serverpackets.ExCubeGameAddPlayer;
import l2p.gameserver.serverpackets.ExCubeGameChangePoints;
import l2p.gameserver.serverpackets.ExCubeGameCloseUI;
import l2p.gameserver.serverpackets.ExCubeGameEnd;
import l2p.gameserver.serverpackets.ExCubeGameExtendedChangePoints;
import l2p.gameserver.serverpackets.ExCubeGameRemovePlayer;
import l2p.gameserver.serverpackets.ExShowScreenMessage;
import l2p.gameserver.serverpackets.L2GameServerPacket;
import l2p.gameserver.serverpackets.Revive;
import l2p.gameserver.scripts.Functions;
import l2p.gameserver.tables.SkillTable;
import l2p.gameserver.utils.Location;
import org.apache.commons.lang3.mutable.MutableInt;


public class TreasuresOfTheHeraldInstance extends Reflection {

    private static final int BOX_ID = 18822;
    private static final int BOSS_ID = 25655;
    private static final int SCORE_BOX = Config.EVENT_TREASURES_OF_THE_HERALD_SCORE_BOX;
    private static final int SCORE_BOSS = Config.EVENT_TREASURES_OF_THE_HERALD_SCORE_BOSS;
    private static final int SCORE_KILL = Config.EVENT_TREASURES_OF_THE_HERALD_SCORE_KILL;
    private static final int SCORE_DEATH = Config.EVENT_TREASURES_OF_THE_HERALD_SCORE_DEATH;
    private int eventTime = Config.EVENT_TREASURES_OF_THE_HERALD_TIME;
    private long bossSpawnTime = 600000L;
    private boolean active = false;
    private Party team1;
    private Party team2;
    private List<HardReference<Player>> bothTeams = new CopyOnWriteArrayList();
    private TIntObjectHashMap<MutableInt> score = new TIntObjectHashMap();
    private int team1Score = 0;
    private int team2Score = 0;
    private long startTime;
    private ScheduledFuture<?> _bossSpawnTask;
    private ScheduledFuture<?> _countDownTask;
    private ScheduledFuture<?> _battleEndTask;
    private DeathListener _deathListener = new DeathListener();
    private TeleportListener _teleportListener = new TeleportListener();
    private PlayerPartyLeaveListener _playerPartyLeaveListener = new PlayerPartyLeaveListener();
    private Zone zonebattle;
    private Zone zonepvp;
    private Zone zonepeace1;
    private Zone peace1;
    private Zone zonepeace2;
    private Zone peace2;

    public void setTeam1(Party party1) {
        this.team1 = party1;
    }

    public void setTeam2(Party party2) {
        this.team2 = party2;
    }

    public void start() {
        this.zonepvp = getZone("[gvg_battle_zone]");
        this.peace1 = getZone("[gvg_1_peace]");
        this.peace2 = getZone("[gvg_2_peace]");

        Location[] boxes = {new Location(142696, 139704, -15264, 0), new Location(142696, 145944, -15264, 0), new Location(145784, 142824, -15264, 0), new Location(145768, 139704, -15264, 0), new Location(145768, 145944, -15264, 0), new Location(141752, 142760, -15624, 0), new Location(145720, 142008, -15880, 0), new Location(145720, 143640, -15880, 0), new Location(139592, 142824, -15264, 0)};

        for (int i = 0; i < boxes.length; i++) {
            addSpawnWithoutRespawn(18822, boxes[i], 0);
        }
        addSpawnWithoutRespawn(35423, new Location(139640, 139736, -15264), 0);
        addSpawnWithoutRespawn(35426, new Location(139672, 145896, -15264), 0);

        this._bossSpawnTask = ThreadPoolManager.getInstance().schedule(new BossSpawn(), this.bossSpawnTime);
        this._countDownTask = ThreadPoolManager.getInstance().schedule(new CountingDown(), (this.eventTime - 1) * 1000L);
        this._battleEndTask = ThreadPoolManager.getInstance().schedule(new BattleEnd(), (this.eventTime - 6) * 1000L);

        for (Player member : this.team1.getPartyMembers()) {
            this.bothTeams.add(member.getRef());
            member.addListener(this._deathListener);
            member.addListener(this._teleportListener);
            member.addListener(this._playerPartyLeaveListener);
        }

        for (Player member : this.team2.getPartyMembers()) {
            this.bothTeams.add(member.getRef());
            member.addListener(this._deathListener);
            member.addListener(this._teleportListener);
            member.addListener(this._playerPartyLeaveListener);
        }

        this.startTime = (System.currentTimeMillis() + this.eventTime * 1000L);

        ExCubeGameChangePoints initialPoints = new ExCubeGameChangePoints(this.eventTime, this.team1Score, this.team2Score);
        ExCubeGameCloseUI cui = new ExCubeGameCloseUI();

        for (Player tm : HardReferences.unwrap(this.bothTeams)) {
            this.score.put(tm.getObjectId(), new MutableInt());

            tm.setCurrentCp(tm.getMaxCp());
            tm.setCurrentHp(tm.getMaxHp(), false);
            tm.setCurrentMp(tm.getMaxMp());
            ExCubeGameExtendedChangePoints clientSetUp = new ExCubeGameExtendedChangePoints(this.eventTime, this.team1Score, this.team2Score, isRedTeam(tm), tm, 0);
            tm.sendPacket(clientSetUp);
            tm.sendActionFailed();
            tm.sendPacket(initialPoints);
            tm.sendPacket(cui);
            broadCastPacketToBothTeams(new ExCubeGameAddPlayer(tm, isRedTeam(tm)));
        }

        this.active = true;
    }

    private void broadCastPacketToBothTeams(L2GameServerPacket packet) {
        for (Player tm : HardReferences.unwrap(this.bothTeams)) {
            tm.sendPacket(packet);
        }
    }

    private boolean isActive() {
        return this.active;
    }

    private boolean isRedTeam(Player player) {
        return this.team2.containsMember(player);
    }

    private void end() {
        this.active = false;

        startCollapseTimer(60000L);

        paralyzePlayers();
        ThreadPoolManager.getInstance().schedule(new Finish(), 55000L);

        if (this._bossSpawnTask != null) {
            this._bossSpawnTask.cancel(false);
            this._bossSpawnTask = null;
        }
        if (this._countDownTask != null) {
            this._countDownTask.cancel(false);
            this._countDownTask = null;
        }
        if (this._battleEndTask != null) {
            this._battleEndTask.cancel(false);
            this._battleEndTask = null;
        }

        boolean isRedWinner = false;

        isRedWinner = getRedScore() >= getBlueScore();

        ExCubeGameEnd end = new ExCubeGameEnd(isRedWinner);
        broadCastPacketToBothTeams(end);

        reward(isRedWinner ? this.team2 : this.team1);
        TreasuresOfTheHerald.updateWinner(isRedWinner ? this.team2.getPartyLeader() : this.team1.getPartyLeader());

        this.zonepvp.setActive(false);
        this.peace1.setActive(false);
        this.peace2.setActive(false);
    }

    private void reward(Party party) {
        for (Player member : party.getPartyMembers()) {
            member.sendMessage("Ваша группа выиграла турнир Treasures of the Herald, лидер группы добавлен в рейтинг победителей.");
            member.setFame(member.getFame() + 500, "Treasures of the Herald");
            Functions.addItem(member, Config.EVENT_TREASURES_OF_THE_HERALD_ITEM_ID, Config.EVENT_TREASURES_OF_THE_HERALD_ITEM_COUNT);
        }
    }

    private synchronized void changeScore(int teamId, int toAdd, int toSub, boolean subbing, boolean affectAnotherTeam, Player player) {
        int timeLeft = (int) ((this.startTime - System.currentTimeMillis()) / 1000L);
        if (teamId == 1) {
            if (subbing) {
                this.team1Score -= toSub;
                if (this.team1Score < 0) {
                    this.team1Score = 0;
                }
                if (affectAnotherTeam) {
                    this.team2Score += toAdd;
                    broadCastPacketToBothTeams(new ExCubeGameExtendedChangePoints(timeLeft, this.team1Score, this.team2Score, true, player, getPlayerScore(player)));
                }
                broadCastPacketToBothTeams(new ExCubeGameExtendedChangePoints(timeLeft, this.team1Score, this.team2Score, false, player, getPlayerScore(player)));
            } else {
                this.team1Score += toAdd;
                if (affectAnotherTeam) {
                    this.team2Score -= toSub;
                    if (this.team2Score < 0) {
                        this.team2Score = 0;
                    }
                    broadCastPacketToBothTeams(new ExCubeGameExtendedChangePoints(timeLeft, this.team1Score, this.team2Score, true, player, getPlayerScore(player)));
                }
                broadCastPacketToBothTeams(new ExCubeGameExtendedChangePoints(timeLeft, this.team1Score, this.team2Score, false, player, getPlayerScore(player)));
            }
        } else if (teamId == 2) {
            if (subbing) {
                this.team2Score -= toSub;
                if (this.team2Score < 0) {
                    this.team2Score = 0;
                }
                if (affectAnotherTeam) {
                    this.team1Score += toAdd;
                    broadCastPacketToBothTeams(new ExCubeGameExtendedChangePoints(timeLeft, this.team1Score, this.team2Score, false, player, getPlayerScore(player)));
                }
                broadCastPacketToBothTeams(new ExCubeGameExtendedChangePoints(timeLeft, this.team1Score, this.team2Score, true, player, getPlayerScore(player)));
            } else {
                this.team2Score += toAdd;
                if (affectAnotherTeam) {
                    this.team1Score -= toSub;
                    if (this.team1Score < 0) {
                        this.team1Score = 0;
                    }
                    broadCastPacketToBothTeams(new ExCubeGameExtendedChangePoints(timeLeft, this.team1Score, this.team2Score, false, player, getPlayerScore(player)));
                }
                broadCastPacketToBothTeams(new ExCubeGameExtendedChangePoints(timeLeft, this.team1Score, this.team2Score, true, player, getPlayerScore(player)));
            }
        }
    }

    private void addPlayerScore(Player player) {
        MutableInt points = (MutableInt) this.score.get(player.getObjectId());
        points.increment();
    }

    public int getPlayerScore(Player player) {
        MutableInt points = (MutableInt) this.score.get(player.getObjectId());
        return points.intValue();
    }

    public void paralyzePlayers() {
        for (Player tm : HardReferences.unwrap(this.bothTeams)) {
            if (tm.isDead()) {
                tm.setCurrentHp(tm.getMaxHp(), true);
                tm.broadcastPacket(new L2GameServerPacket[]{new Revive(tm)});
            } else {
                tm.setCurrentHp(tm.getMaxHp(), false);
            }
            tm.setCurrentMp(tm.getMaxMp());
            tm.setCurrentCp(tm.getMaxCp());

            tm.getEffectList().stopEffect(1411);
            tm.block();
        }
    }

    public void unParalyzePlayers() {
        for (Player tm : HardReferences.unwrap(this.bothTeams)) {
            tm.unblock();
            removePlayer(tm, true);
        }
    }

    private void cleanUp() {
        this.team1 = null;
        this.team2 = null;
        this.bothTeams.clear();
        this.team1Score = 0;
        this.team2Score = 0;
        this.score.clear();
    }

    public void resurrectAtBase(Player player) {
        if (player.isDead()) {
            player.setCurrentHp(0.7D * player.getMaxHp(), true);

            player.broadcastPacket(new L2GameServerPacket[]{new Revive(player)});
        }
        player.altOnMagicUseTimer(player, SkillTable.getInstance().getInfo(5660, 2));
        Location pos;
        if (this.team1.containsMember(player)) {
            pos = Location.findPointToStay(TreasuresOfTheHerald.TEAM1_LOC, 0, 150, getGeoIndex());
        } else {
            pos = Location.findPointToStay(TreasuresOfTheHerald.TEAM2_LOC, 0, 150, getGeoIndex());
        }
        player.teleToLocation(pos, this);
    }

    private void removePlayer(Player player, boolean legalQuit) {
        this.bothTeams.remove(player.getRef());

        broadCastPacketToBothTeams(new ExCubeGameRemovePlayer(player, isRedTeam(player)));
        player.removeListener(this._deathListener);
        player.removeListener(this._teleportListener);
        player.removeListener(this._playerPartyLeaveListener);
        player.leaveParty();
        if (!legalQuit) {
            player.sendPacket(new ExCubeGameEnd(false));
        }
        player.teleToLocation(Location.findPointToStay(TreasuresOfTheHerald.RETURN_LOC, 0, 150, ReflectionManager.DEFAULT.getGeoIndex()), 0);
    }

    private void teamWithdraw(Party party) {
        if (party == this.team1) {
            for (Player player : this.team1.getPartyMembers()) {
                removePlayer(player, false);
            }
            Player player = this.team2.getPartyLeader();
            changeScore(2, 200, 0, false, false, player);
        } else {
            for (Player player : this.team2.getPartyMembers()) {
                removePlayer(player, false);
            }
            Player player = this.team1.getPartyLeader();
            changeScore(1, 200, 0, false, false, player);
        }

        broadCastPacketToBothTeams(new ExShowScreenMessage("Команда соперника покинула поле боя в полном составе. Конец сражения.", 4000, ExShowScreenMessage.ScreenMessageAlign.MIDDLE_CENTER, true));
        end();
    }

    private int getBlueScore() {
        return this.team1Score;
    }

    private int getRedScore() {
        return this.team2Score;
    }

    @Override
    public NpcInstance addSpawnWithoutRespawn(int npcId, Location loc, int randomOffset) {
        NpcInstance npc = super.addSpawnWithoutRespawn(npcId, loc, randomOffset);
        npc.addListener(this._deathListener);
        return npc;
    }

    private class PlayerPartyLeaveListener
            implements OnPlayerPartyLeaveListener {

        private PlayerPartyLeaveListener() {
        }

        @Override
        public void onPartyLeave(Player player) {
            if (!TreasuresOfTheHeraldInstance.this.isActive()) {
                return;
            }
            Party party = player.getParty();

            if (party.getMemberCount() >= 3) {
                TreasuresOfTheHeraldInstance.this.removePlayer(player, false);
                return;
            }

            TreasuresOfTheHeraldInstance.this.teamWithdraw(party);
        }
    }

    private class TeleportListener
            implements OnTeleportListener {

        private TeleportListener() {
        }

        @Override
        public void onTeleport(Player player, int x, int y, int z, Reflection reflection) {
            if ((TreasuresOfTheHeraldInstance.this.zonepvp.checkIfInZone(x, y, z, reflection)) || (TreasuresOfTheHeraldInstance.this.peace1.checkIfInZone(x, y, z, reflection)) || (TreasuresOfTheHeraldInstance.this.peace2.checkIfInZone(x, y, z, reflection))) {
                return;
            }
            TreasuresOfTheHeraldInstance.this.removePlayer(player, false);
            player.sendMessage("Вы досрочно покинули зону битвы и были дисквалифицированы.");
        }
    }

    public class Finish extends RunnableImpl {

        public Finish() {
        }

        @Override
        public void runImpl()
                throws Exception {
            TreasuresOfTheHeraldInstance.this.unParalyzePlayers();
            TreasuresOfTheHeraldInstance.this.cleanUp();
        }
    }

    public class BattleEnd extends RunnableImpl {

        public BattleEnd() {
        }

        @Override
        public void runImpl()
                throws Exception {
            TreasuresOfTheHeraldInstance.this.broadCastPacketToBothTeams(new ExShowScreenMessage("Время битвы истекло. Телепортация через 1 минуту.", 4000, ExShowScreenMessage.ScreenMessageAlign.BOTTOM_RIGHT, true));
            TreasuresOfTheHeraldInstance.this.end();
        }
    }

    public class CountingDown extends RunnableImpl {

        public CountingDown() {
        }

        @Override
        public void runImpl()
                throws Exception {
            TreasuresOfTheHeraldInstance.this.broadCastPacketToBothTeams(new ExShowScreenMessage("До конца сражения осталась 1 минута", 4000, ExShowScreenMessage.ScreenMessageAlign.MIDDLE_CENTER, true));
        }
    }

    public class BossSpawn extends RunnableImpl {

        public BossSpawn() {
        }

        @Override
        public void runImpl()
                throws Exception {
            TreasuresOfTheHeraldInstance.this.broadCastPacketToBothTeams(new ExShowScreenMessage("Появился Охранник Сокровищ Геральда", 5000, ExShowScreenMessage.ScreenMessageAlign.MIDDLE_CENTER, true));
            TreasuresOfTheHeraldInstance.this.addSpawnWithoutRespawn(25655, new Location(147304, 142824, -15864, 32768), 0);
            TreasuresOfTheHeraldInstance.this.openDoor(24220042);
        }
    }

    private class DeathListener
            implements OnDeathListener {

        private DeathListener() {
        }

        @Override
        public void onDeath(Creature self, Creature killer) {
            if (!TreasuresOfTheHeraldInstance.this.isActive()) {
                return;
            }

            if ((self.getReflection() != killer.getReflection()) || (self.getReflection() != TreasuresOfTheHeraldInstance.this)) {
                return;
            }
            if ((self.isPlayer()) && (killer.isPlayable())) {
                if ((TreasuresOfTheHeraldInstance.this.team1.containsMember(self.getPlayer())) && (TreasuresOfTheHeraldInstance.this.team2.containsMember(killer.getPlayer()))) {
                    TreasuresOfTheHeraldInstance.this.addPlayerScore(killer.getPlayer());
                    TreasuresOfTheHeraldInstance.this.changeScore(1, TreasuresOfTheHeraldInstance.SCORE_KILL, TreasuresOfTheHeraldInstance.SCORE_DEATH, true, true, killer.getPlayer());
                } else if ((TreasuresOfTheHeraldInstance.this.team2.containsMember(self.getPlayer())) && (TreasuresOfTheHeraldInstance.this.team1.containsMember(killer.getPlayer()))) {
                    TreasuresOfTheHeraldInstance.this.addPlayerScore(killer.getPlayer());
                    TreasuresOfTheHeraldInstance.this.changeScore(2, TreasuresOfTheHeraldInstance.SCORE_KILL, TreasuresOfTheHeraldInstance.SCORE_DEATH, true, true, killer.getPlayer());
                }
                TreasuresOfTheHeraldInstance.this.resurrectAtBase(self.getPlayer());
            } else if ((self.isPlayer()) && (!killer.isPlayable())) {
                TreasuresOfTheHeraldInstance.this.resurrectAtBase(self.getPlayer());
            } else if ((self.isNpc()) && (killer.isPlayable())) {
                if (self.getNpcId() == 18822) {
                    if (TreasuresOfTheHeraldInstance.this.team1.containsMember(killer.getPlayer())) {
                        TreasuresOfTheHeraldInstance.this.changeScore(1, TreasuresOfTheHeraldInstance.SCORE_BOX, 0, false, false, killer.getPlayer());
                    } else if (TreasuresOfTheHeraldInstance.this.team2.containsMember(killer.getPlayer())) {
                        TreasuresOfTheHeraldInstance.this.changeScore(2, TreasuresOfTheHeraldInstance.SCORE_BOX, 0, false, false, killer.getPlayer());
                    }
                } else if (self.getNpcId() == 25655) {
                    if (TreasuresOfTheHeraldInstance.this.team1.containsMember(killer.getPlayer())) {
                        TreasuresOfTheHeraldInstance.this.changeScore(1, TreasuresOfTheHeraldInstance.SCORE_BOSS, 0, false, false, killer.getPlayer());
                    } else if (TreasuresOfTheHeraldInstance.this.team2.containsMember(killer.getPlayer())) {
                        TreasuresOfTheHeraldInstance.this.changeScore(2, TreasuresOfTheHeraldInstance.SCORE_BOSS, 0, false, false, killer.getPlayer());
                    }
                    TreasuresOfTheHeraldInstance.this.broadCastPacketToBothTeams(new ExShowScreenMessage("Охранник Сокровищ Геральда погиб от руки " + killer.getName(), 5000, ExShowScreenMessage.ScreenMessageAlign.MIDDLE_CENTER, true));
                    TreasuresOfTheHeraldInstance.this.end();
                }
            }
        }
    }
}
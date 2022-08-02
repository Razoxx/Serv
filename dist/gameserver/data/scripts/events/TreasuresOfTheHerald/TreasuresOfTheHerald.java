package events.TreasuresOfTheHerald;

import instances.TreasuresOfTheHeraldInstance;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledFuture;
import l2p.commons.dbutils.DbUtils;
import l2p.commons.lang.reference.HardReference;
import l2p.commons.lang.reference.HardReferences;
import l2p.commons.threading.RunnableImpl;
import l2p.commons.util.Rnd;
import l2p.gameserver.Announcements;
import l2p.gameserver.Config;
import l2p.gameserver.ThreadPoolManager;
import l2p.gameserver.data.xml.holder.InstantZoneHolder;
import l2p.gameserver.data.xml.holder.ResidenceHolder;
import l2p.gameserver.database.DatabaseFactory;
import l2p.gameserver.instancemanager.ServerVariables;
import l2p.gameserver.model.Player;
import l2p.gameserver.model.base.TeamType;
import l2p.gameserver.model.entity.olympiad.Olympiad;
import l2p.gameserver.model.entity.residence.Castle;
import l2p.gameserver.scripts.Functions;
import l2p.gameserver.scripts.ScriptFile;
import l2p.gameserver.templates.InstantZone;
import l2p.gameserver.utils.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TreasuresOfTheHerald extends Functions
        implements ScriptFile {

    private static final Logger _log = LoggerFactory.getLogger(TreasuresOfTheHerald.class);
    public static final Location TEAM1_LOC = new Location(139736, 145832, -15264);
    public static final Location TEAM2_LOC = new Location(139736, 139832, -15264);
    public static final Location RETURN_LOC = new Location(43816, -48232, -822);
    public static final int[] everydayStartTime = {21, 30, 0};
    private static boolean _active = false;
    private static boolean _isRegistrationActive = false;
    private static int _minLevel = Config.EVENT_TREASURES_OF_THE_HERALD_MIN_LEVEL;
    private static int _maxLevel = Config.EVENT_TREASURES_OF_THE_HERALD_MAX_LEVEL;
    private static int _groupsLimit = Config.EVENT_TREASURES_OF_THE_HERALD_MAX_GROUP;
    private static int _minPartyMembers = Config.EVENT_TREASURES_OF_THE_HERALD_MINIMUM_PARTY_MEMBER;
    private static long regActiveTime = 600000L;
    private static ScheduledFuture<?> _globalTask;
    private static ScheduledFuture<?> _regTask;
    private static ScheduledFuture<?> _countdownTask1;
    private static ScheduledFuture<?> _countdownTask2;
    private static ScheduledFuture<?> _countdownTask3;
    private static List<HardReference<Player>> leaderList = new CopyOnWriteArrayList();

    @Override
    public void onLoad() {
        if (Config.EVENT_TREASURES_OF_THE_HERALD_ENABLE) {
            _log.info("Loaded Event: Treasures of the Herald");
            initTimer();
        }
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    private static void initTimer() {
        long day = 86400000L;
        Calendar ci = Calendar.getInstance();
        ci.set(11, everydayStartTime[0]);
        ci.set(12, everydayStartTime[1]);
        ci.set(13, everydayStartTime[2]);

        long delay = ci.getTimeInMillis() - System.currentTimeMillis();
        if (delay < 0L) {
            delay += day;
        }
        if (_globalTask != null) {
            _globalTask.cancel(true);
        }
        _globalTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Launch(), delay, day);
    }

    private static boolean canBeStarted() {
        for (Castle c : ResidenceHolder.getInstance().getResidenceList(Castle.class)) {
            if ((c.getSiegeEvent() != null) && (c.getSiegeEvent().isInProgress())) {
                return false;
            }
        }
        return true;
    }

    private static boolean isActive() {
        return _active;
    }

    public static void activateEvent() {
        if ((!isActive()) && (canBeStarted())) {
            _regTask = ThreadPoolManager.getInstance().schedule(new RegTask(), regActiveTime);
            if (regActiveTime > 120000L) {
                if (regActiveTime > 300000L) {
                    _countdownTask3 = ThreadPoolManager.getInstance().schedule(new Countdown(5), regActiveTime - 300000L);
                }
                _countdownTask1 = ThreadPoolManager.getInstance().schedule(new Countdown(2), regActiveTime - 120000L);
                _countdownTask2 = ThreadPoolManager.getInstance().schedule(new Countdown(1), regActiveTime - 60000L);
            }
            ServerVariables.set("TreasuresOfTheHerald", "on");
            _log.info("Event 'Treasures of the Herald' activated.");
            Announcements.getInstance().announceToAll("Регистрация на турнир Treasures of the Herald началась! Community Board(Alt+B) -> Эвенты -> Treasures of the Herald (регистрация группы, описание)");
            Announcements.getInstance().announceToAll(new StringBuilder().append("Заявки принимаются в течение ").append(regActiveTime / 60000L).append(" минут").toString());
            _active = true;
            _isRegistrationActive = true;
        }
    }

    public static void deactivateEvent() {
        if (isActive()) {
            stopTimers();
            ServerVariables.unset("TreasuresOfTheHerald");
            _log.info("Event 'Treasures of the Herald' canceled.");
            Announcements.getInstance().announceToAll("Турнир Treasures of the Herald отменен");
            _active = false;
            _isRegistrationActive = false;
            leaderList.clear();
        }
    }

    public void showStats() {
        Player player = getSelf();
        if (!player.getPlayerAccess().IsEventGm) {
            return;
        }
        if (!isActive()) {
            player.sendMessage("Treasures of the Herald event is not launched");
            return;
        }

        StringBuilder string = new StringBuilder();
        String refresh = "<button value=\"Refresh\" action=\"bypass -h scripts_events.TreasuresOfTheHerald.TreasuresOfTheHerald:showStats\" width=60 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">";
        String start = "<button value=\"Start Now\" action=\"bypass -h scripts_events.TreasuresOfTheHerald.TreasuresOfTheHerald:startNow\" width=60 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">";
        int i = 0;

        if (!leaderList.isEmpty()) {
            for (Player leader : HardReferences.unwrap(leaderList)) {
                if (!leader.isInParty()) {
                    continue;
                }
                string.append("*").append(leader.getName()).append("*").append(" | group members: ").append(leader.getParty().getMemberCount()).append("\n\n");
                i++;
            }
            show(new StringBuilder().append("There are ").append(i).append(" group leaders who registered for the event:\n\n").append(string).append("\n\n").append(refresh).append("\n\n").append(start).toString(), player, null, new Object[0]);
        } else {
            show(new StringBuilder().append("There are no participants at the time\n\n").append(refresh).toString(), player, null, new Object[0]);
        }
    }

    public void startNow() {
        Player player = getSelf();
        if (!player.getPlayerAccess().IsEventGm) {
            return;
        }
        if ((!isActive()) || (!canBeStarted())) {
            player.sendMessage("Treasures of the Herald event is not launched");
            return;
        }

        prepare();
    }

    public void addGroup() {
        Player player = getSelf();
        if (player == null) {
            return;
        }
        if (!_isRegistrationActive) {
            player.sendMessage("Турнир Treasures of the Herald неактивен.");
            return;
        }

        if (leaderList.contains(player.getRef())) {
            player.sendMessage("Вы уже зарегистрировались на турнир Treasures of the Herald");
            return;
        }

        if (!player.isInParty()) {
            player.sendMessage("Вы не состоите в группе и не можете подать заявку");
            return;
        }

        if (!player.getParty().isLeader(player)) {
            player.sendMessage("Только лидер группы может подать заявку");
            return;
        }
        if (player.getParty().isInCommandChannel()) {
            player.sendMessage("Чтобы участвовать в турнире вы должны покинуть Командный Канал");
            return;
        }

        if (leaderList.size() >= _groupsLimit) {
            player.sendMessage("Достигнут лимит количества групп для участия в турнире. Заявка отклонена");
            return;
        }

        String[] abuseReason = {"не находится в игре", "не находится в группе", "состоит в неполной группе. Минимальное кол-во членов группы - 6.", "не является лидером группы, подававшей заявку", "не соответствует требованиям уровней для турнира", "использует ездовое животное, что противоречит требованиям турнира", "находится в дуэли, что противоречит требованиям турнира", "принимает участие в другом эвенте, что противоречит требованиям турнира", "находится в списке ожидания Олимпиады или принимает участие в ней", "находится в состоянии телепортации, что противоречит требованиям турнира", "находится в Dimensional Rift, что противоречит требованиям турнира", "обладает Проклятым Оружием, что противоречит требованиям турнира", "не находится в мирной зоне", "находится в режиме обозревания"};

        for (Player eachmember : player.getParty().getPartyMembers()) {
            int abuseId = checkPlayer(eachmember, false);
            if (abuseId != 0) {
                player.sendMessage(new StringBuilder().append("Игрок ").append(eachmember.getName()).append(" ").append(abuseReason[(abuseId - 1)]).toString());
                return;
            }
        }

        leaderList.add(player.getRef());
        player.getParty().broadcastMessageToPartyMembers("Ваша группа внесена в список ожидания. Пожалуйста, не регистрируйтесь в других ивентах и не участвуйте в дуэлях до начала турнира. Полный список требований турнира в Community Board (Alt+B)");
    }

    private static void stopTimers() {
        if (_regTask != null) {
            _regTask.cancel(false);
            _regTask = null;
        }
        if (_countdownTask1 != null) {
            _countdownTask1.cancel(false);
            _countdownTask1 = null;
        }
        if (_countdownTask2 != null) {
            _countdownTask2.cancel(false);
            _countdownTask2 = null;
        }
        if (_countdownTask3 != null) {
            _countdownTask3.cancel(false);
            _countdownTask3 = null;
        }
    }

    private static void prepare() {
        checkPlayers();
        shuffleGroups();

        if (isActive()) {
            stopTimers();
            ServerVariables.unset("TreasuresOfTheHerald");
            _active = false;
            _isRegistrationActive = false;
        }

        if (leaderList.size() < 2) {
            leaderList.clear();
            Announcements.getInstance().announceToAll("Турнир Treasures of the Herald отменен из-за недостатка участников");
            return;
        }

        Announcements.getInstance().announceToAll("Treasures of the Herald: Прием заявок завершен. Запуск турнира.");
        start();
    }

    private static int checkPlayer(Player player, boolean doCheckLeadership) {
        if (!player.isOnline()) {
            return 1;
        }
        if (!player.isInParty()) {
            return 2;
        }
        if ((doCheckLeadership) && ((player.getParty() == null) || (!player.getParty().isLeader(player)))) {
            return 4;
        }
        if ((player.getParty() == null) || (player.getParty().getMemberCount() < _minPartyMembers)) {
            return 3;
        }
        if ((player.getLevel() < _minLevel) || (player.getLevel() > _maxLevel)) {
            return 5;
        }
        if (player.isMounted()) {
            return 6;
        }
        if (player.isInDuel()) {
            return 7;
        }
        if (player.getTeam() != TeamType.NONE) {
            return 8;
        }
        if ((player.getOlympiadGame() != null) || (Olympiad.isRegistered(player))) {
            return 9;
        }
        if (player.isTeleporting()) {
            return 10;
        }
        if (player.getParty().isInDimensionalRift()) {
            return 11;
        }
        if (player.isCursedWeaponEquipped()) {
            return 12;
        }
        if (!player.isInPeaceZone()) {
            return 13;
        }
        if (player.isInObserverMode()) {
            return 14;
        }
        return 0;
    }

    private static void shuffleGroups() {
        if (leaderList.size() % 2 != 0) {
            int rndindex = Rnd.get(leaderList.size());
            Player expelled = (Player) ((HardReference) leaderList.remove(rndindex)).get();
            if (expelled != null) {
                expelled.sendMessage("При формировании списка участников турнира ваша группа была отсеяна. Приносим извинения, попробуйте в следующий раз.");
            }
        }

        for (int i = 0; i < leaderList.size(); i++) {
            int rndindex = Rnd.get(leaderList.size());
            leaderList.set(i, leaderList.set(rndindex, leaderList.get(i)));
        }
    }

    private static void checkPlayers() {
        for (Player player : HardReferences.unwrap(leaderList)) {

            if (checkPlayer(player, true) != 0) {
                leaderList.remove(player.getRef());
                continue;
            }

            for (Player partymember : player.getParty().getPartyMembers()) {
                if (checkPlayer(partymember, false) != 0) {
                    player.sendMessage("Ваша группа была дисквалифицирована и снята с участия в турнире так как один или более членов группы нарушил условия участия");
                    leaderList.remove(player.getRef());
                    break;
                }
            }
        }
        Player player;
    }

    public static void updateWinner(Player winner) {
        Connection con = null;
        PreparedStatement statement = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement("INSERT INTO event_data(charId, score) VALUES (?,1) ON DUPLICATE KEY UPDATE score=score+1");
            statement.setInt(1, winner.getObjectId());
            statement.execute();
        } catch (Exception e) {
        } finally {
            DbUtils.closeQuietly(con, statement);
        }
    }

    private static void start() {
        int instancedZoneId = 504;
        InstantZone iz = InstantZoneHolder.getInstance().getInstantZone(instancedZoneId);
        if (iz == null) {
            _log.warn(new StringBuilder().append("Treasures of the Herald: InstanceZone : ").append(instancedZoneId).append(" not found!").toString());
            return;
        }

        for (int i = 0; i < leaderList.size(); i += 2) {
            Player team1Leader = (Player) ((HardReference) leaderList.get(i)).get();
            Player team2Leader = (Player) ((HardReference) leaderList.get(i + 1)).get();

            TreasuresOfTheHeraldInstance r = new TreasuresOfTheHeraldInstance();
            r.setTeam1(team1Leader.getParty());
            r.setTeam2(team2Leader.getParty());
            r.init(iz);
            r.setReturnLoc(RETURN_LOC);

            for (Player member : team1Leader.getParty().getPartyMembers()) {
                Functions.unRide(member);
                Functions.unSummonPet(member, true);
                member.setTransformation(0);
                member.setInstanceReuse(instancedZoneId, System.currentTimeMillis());
                member.dispelBuffs();

                member.teleToLocation(Location.findPointToStay(TEAM1_LOC, 0, 150, r.getGeoIndex()), r);
            }

            for (Player member : team2Leader.getParty().getPartyMembers()) {
                Functions.unRide(member);
                Functions.unSummonPet(member, true);
                member.setTransformation(0);
                member.setInstanceReuse(instancedZoneId, System.currentTimeMillis());
                member.dispelBuffs();

                member.teleToLocation(Location.findPointToStay(TEAM2_LOC, 0, 150, r.getGeoIndex()), r);
            }

            r.start();
        }

        leaderList.clear();
        _log.info("Treasures of the Herald: Event started successfuly.");
    }

    public static class Launch extends RunnableImpl {

        @Override
        public void runImpl() {
            TreasuresOfTheHerald.activateEvent();
        }
    }

    public static class Countdown extends RunnableImpl {

        int _timer;

        public Countdown(int timer) {
            this._timer = timer;
        }

        @Override
        public void runImpl()
                throws Exception {
            Announcements.getInstance().announceToAll("До конца приема заявок на турнир Treasures of the Herald осталось " + Integer.toString(this._timer) + " мин.");
        }
    }

    public static class RegTask extends RunnableImpl {

        @Override
        public void runImpl()
                throws Exception {
            TreasuresOfTheHerald.prepare();
        }
    }
}
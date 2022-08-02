package events.TvT;

import gnu.trove.map.hash.TIntObjectHashMap;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledFuture;
import l2p.commons.threading.RunnableImpl;
import l2p.commons.util.Rnd;
import l2p.gameserver.Announcements;
import l2p.gameserver.Config;
import l2p.gameserver.ThreadPoolManager;
import l2p.gameserver.data.xml.holder.ResidenceHolder;
import l2p.gameserver.geodata.GeoEngine;
import l2p.gameserver.instancemanager.ReflectionManager;
import l2p.gameserver.instancemanager.ServerVariables;
import l2p.gameserver.listener.actor.OnDeathListener;
import l2p.gameserver.listener.actor.player.OnPlayerExitListener;
import l2p.gameserver.listener.actor.player.OnTeleportListener;
import l2p.gameserver.listener.zone.OnZoneEnterLeaveListener;
import l2p.gameserver.model.Creature;
import l2p.gameserver.model.GameObject;
import l2p.gameserver.model.GameObjectsStorage;
import l2p.gameserver.model.Party;
import l2p.gameserver.model.Player;
import l2p.gameserver.model.Skill;
import l2p.gameserver.model.Territory;
import l2p.gameserver.model.Zone;
import l2p.gameserver.model.Zone.ZoneType;
import l2p.gameserver.model.actor.listener.CharListenerList;
import l2p.gameserver.model.base.TeamType;
import l2p.gameserver.model.entity.Reflection;
import l2p.gameserver.model.entity.events.impl.DuelEvent;
import l2p.gameserver.model.entity.olympiad.Olympiad;
import l2p.gameserver.model.entity.residence.Castle;
import l2p.gameserver.model.entity.residence.Residence;
import l2p.gameserver.model.instances.DoorInstance;
import l2p.gameserver.model.items.LockType;
import l2p.gameserver.scripts.Functions;
import l2p.gameserver.scripts.ScriptFile;
import l2p.gameserver.serverpackets.ExCubeGameAddPlayer;
import l2p.gameserver.serverpackets.ExCubeGameChangePoints;
import l2p.gameserver.serverpackets.ExCubeGameEnd;
import l2p.gameserver.serverpackets.ExCubeGameExtendedChangePoints;
import l2p.gameserver.serverpackets.ExCubeGameRemovePlayer;
import l2p.gameserver.serverpackets.ExCubeGameTeamList;
import l2p.gameserver.serverpackets.ExShowScreenMessage;
import l2p.gameserver.serverpackets.ExShowScreenMessage.ScreenMessageAlign;
import l2p.gameserver.serverpackets.L2GameServerPacket;
import l2p.gameserver.serverpackets.Revive;
import l2p.gameserver.serverpackets.ShowBoard;
import l2p.gameserver.serverpackets.components.ChatType;
import l2p.gameserver.serverpackets.components.CustomMessage;
import l2p.gameserver.tables.SkillTable;
import l2p.gameserver.templates.DoorTemplate;
import l2p.gameserver.templates.ZoneTemplate;
import l2p.gameserver.utils.Location;
import l2p.gameserver.utils.PositionUtils;
import l2p.gameserver.utils.ReflectionUtils;
import org.apache.commons.lang3.mutable.MutableInt;
import org.napile.primitive.maps.IntObjectMap;
import org.napile.primitive.maps.impl.HashIntObjectMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TvT extends Functions implements ScriptFile, OnDeathListener, OnTeleportListener, OnPlayerExitListener {

    private static final Logger _log = LoggerFactory.getLogger(TvT.class);

    private static ScheduledFuture<?> _startTask;

    private static final int[] doors = new int[]{24190001, 24190002, 24190003, 24190004};

    private static Map<Player, Integer> _redTeamPoints = new ConcurrentHashMap<Player, Integer>();
    private static Map<Player, Integer> _blueTeamPoints = new ConcurrentHashMap<Player, Integer>();

    private static List<Player> live_list1 = new CopyOnWriteArrayList<Player>();
    private static List<Player> live_list2 = new CopyOnWriteArrayList<Player>();

    private static int[][] mage_buffs = new int[Config.EVENT_TvTMageBuffs.length][2];
    private static int[][] fighter_buffs = new int[Config.EVENT_TvTFighterBuffs.length][2];
    private static long _startedTime = 0;

    private static int[][] rewards = new int[Config.EVENT_TvTRewards.length][2];

    private static Map<Player, Location> playerRestoreCoord = new LinkedHashMap<Player, Location>();

    private static Map<Player, String> boxes = new LinkedHashMap<Player, String>();

    private static boolean _isRegistrationActive = false;
    private static int _status = 0;
    private static int _time_to_start;
    private static int _category;
    private static int _minLevel;
    private static int _maxLevel;
    private static int _autoContinue = 0;
    private static boolean _active = false;
    private static Skill buff;

    private static Reflection reflection = ReflectionManager.TVT_EVENT;

    private static ScheduledFuture<?> _endTask;

    private static Zone _zone;
    private static Zone _zone1;
    private static Zone _zone2;
    private static Zone _zone3;
    private static Zone _zone4;
    private static Zone _zone5;
    //new 
    private static Zone _zone6;
    private static Zone _zone7;
    private static Zone _zone8;
    private static Zone _zone9;
    private static Zone _zone10;
    private static Zone _myZone = null;
    private static Territory territory = null;
    private static Map<Integer, Integer> _pScore = new HashMap<Integer, Integer>();
    private static Map<String, ZoneTemplate> _zones = new HashMap<String, ZoneTemplate>();
    private static IntObjectMap<DoorTemplate> _doors = new HashIntObjectMap<DoorTemplate>();
    private static ZoneListener _zoneListener = new ZoneListener();

    private static int bluePoints = 0;
    private static int redPoints = 0;

    private static TIntObjectHashMap<MutableInt> score = new TIntObjectHashMap<MutableInt>();

    @Override
    public void onLoad() {
        CharListenerList.addGlobal(this);

        _zones.put("[hellbound_quarry_tvt]", ReflectionUtils.getZone("[hellbound_quarry_tvt]").getTemplate());
        _zones.put("[hellbound_quarry_tvt]", ReflectionUtils.getZone("[hellbound_quarry_tvt]").getTemplate());
        _zones.put("[hellbound_quarry_tvt]", ReflectionUtils.getZone("[hellbound_quarry_tvt]").getTemplate());
        _zones.put("[hellbound_quarry_tvt]", ReflectionUtils.getZone("[hellbound_quarry_tvt]").getTemplate());
        _zones.put("[hellbound_quarry_tvt]", ReflectionUtils.getZone("[hellbound_quarry_tvt]").getTemplate());
        _zones.put("[hellbound_quarry_tvt]", ReflectionUtils.getZone("[hellbound_quarry_tvt]").getTemplate());
        //new 
        _zones.put("[hellbound_quarry_tvt]", ReflectionUtils.getZone("[hellbound_quarry_tvt]").getTemplate());
        _zones.put("[hellbound_quarry_tvt]", ReflectionUtils.getZone("[hellbound_quarry_tvt]").getTemplate());
        _zones.put("[hellbound_quarry_tvt]", ReflectionUtils.getZone("[hellbound_quarry_tvt]").getTemplate());
        //new
        _zones.put("[hellbound_quarry_tvt]", ReflectionUtils.getZone("[hellbound_quarry_tvt]").getTemplate());
        _zones.put("[hellbound_quarry_tvt]", ReflectionUtils.getZone("[hellbound_quarry_tvt]").getTemplate());
        for (final int doorId : doors) {
            _doors.put(doorId, ReflectionUtils.getDoor(doorId).getTemplate());
        }

        int geoIndex = GeoEngine.NextGeoIndex(19, 25, reflection.getId());
        //   reflection.setGeoIndex(geoIndex);
        reflection.init(_doors, _zones);

        _zone = reflection.getZone("[hellbound_quarry_tvt]");
        _zone1 = reflection.getZone("[hellbound_quarry_tvt]");
        _zone2 = reflection.getZone("[hellbound_quarry_tvt]");
        _zone3 = reflection.getZone("[hellbound_quarry_tvt]");
        _zone4 = reflection.getZone("[hellbound_quarry_tvt]");
        _zone5 = reflection.getZone("[hellbound_quarry_tvt]");
        //new
        _zone6 = reflection.getZone("[hellbound_quarry_tvt]");
        _zone7 = reflection.getZone("[hellbound_quarry_tvt]");
        _zone8 = reflection.getZone("[hellbound_quarry_tvt]");
        //new
        _zone9 = reflection.getZone("[hellbound_quarry_tvt]");
        _zone10 = reflection.getZone("[hellbound_quarry_tvt]");
        _active = ServerVariables.getString("TvT", "off").equalsIgnoreCase("on");
        if (isActive()) {
            scheduleEventStart();
        }

        _zone.addListener(_zoneListener);
        _zone1.addListener(_zoneListener);
        _zone2.addListener(_zoneListener);
        _zone3.addListener(_zoneListener);
        _zone4.addListener(_zoneListener);
        _zone5.addListener(_zoneListener);
        _zone6.addListener(_zoneListener);
        _zone7.addListener(_zoneListener);
        _zone8.addListener(_zoneListener);
        _zone9.addListener(_zoneListener);
        _zone10.addListener(_zoneListener);

        int i = 0;

        if (Config.EVENT_TvTBuffPlayers && Config.EVENT_TvTMageBuffs.length != 0) {
            for (String skill : Config.EVENT_TvTMageBuffs) {
                String[] splitSkill = skill.split(",");
                mage_buffs[i][0] = Integer.parseInt(splitSkill[0]);
                mage_buffs[i][1] = Integer.parseInt(splitSkill[1]);
                i++;
            }
        }

        i = 0;

        if (Config.EVENT_TvTBuffPlayers && Config.EVENT_TvTMageBuffs.length != 0) {
            for (String skill : Config.EVENT_TvTFighterBuffs) {
                String[] splitSkill = skill.split(",");
                fighter_buffs[i][0] = Integer.parseInt(splitSkill[0]);
                fighter_buffs[i][1] = Integer.parseInt(splitSkill[1]);
                i++;
            }
        }

        i = 0;
        if (Config.EVENT_TvTRewards.length != 0) {
            for (String reward : Config.EVENT_TvTRewards) {
                String[] splitReward = reward.split(",");
                rewards[i][0] = Integer.parseInt(splitReward[0]);
                rewards[i][1] = Integer.parseInt(splitReward[1]);
                i++;
            }
        }

        _log.info("Loaded Event: TvT");
    }

    @Override
    public void onReload() {
        if (_startTask != null) {
            _startTask.cancel(false);
            _startTask = null;
        }
    }

    @Override
    public void onShutdown() {
        onReload();
    }

    private static long getStarterTime() {
        return _startedTime;
    }

    private static boolean isActive() {
        return _active;
    }

    public void activateEvent() {
        Player player = getSelf();
        if (!player.getPlayerAccess().IsEventGm) {
            return;
        }

        if (!isActive()) {
            if (_startTask == null) {
                scheduleEventStart();
            }
            ServerVariables.set("TvT", "on");
            _log.info("Event 'TvT' activated.");
            Announcements.getInstance().announceByCustomMessage("scripts.events.TvT.AnnounceEventStarted", null);
        } else {
            player.sendMessage("Event 'TvT' already active.");
        }

        _active = true;

        show("admin/events/events.htm", player);
    }

    public void deactivateEvent() {
        Player player = getSelf();
        if (!player.getPlayerAccess().IsEventGm) {
            return;
        }

        if (isActive()) {
            if (_startTask != null) {
                _startTask.cancel(false);
                _startTask = null;
            }
            ServerVariables.unset("TvT");
            _log.info("Event 'TvT' deactivated.");
            Announcements.getInstance().announceByCustomMessage("scripts.events.TvT.AnnounceEventStoped", null);
        } else {
            player.sendMessage("Event 'TvT' not active.");
        }

        _active = false;

        show("admin/events/events.htm", player);
    }

    public static boolean isRunned() {
        return _isRegistrationActive || _status > 0;
    }

    public static int getMinLevelForCategory(int category) {
        switch (category) {
            case 1:
                return 20;
            case 2:
                return 30;
            case 3:
                return 40;
            case 4:
                return 52;
            case 5:
                return 62;
            case 6:
                return 76;
        }
        return 0;
    }

    public static int getMaxLevelForCategory(int category) {
        switch (category) {
            case 1:
                return 29;
            case 2:
                return 39;
            case 3:
                return 51;
            case 4:
                return 61;
            case 5:
                return 75;
            case 6:
                return 85;
        }
        return 0;
    }

    public static int getCategory(int level) {
        if (level >= 20 && level <= 29) {
            return 1;
        } else if (level >= 30 && level <= 39) {
            return 2;
        } else if (level >= 40 && level <= 51) {
            return 3;
        } else if (level >= 52 && level <= 61) {
            return 4;
        } else if (level >= 62 && level <= 75) {
            return 5;
        } else if (level >= 76) {
            return 6;
        }
        return 0;
    }

    public void start(String[] var) {
        Player player = getSelf();
        if (var.length != 2) {
            show(new CustomMessage("common.Error", player), player);
            return;
        }

        Integer category;
        Integer autoContinue;
        try {
            category = Integer.valueOf(var[0]);
            autoContinue = Integer.valueOf(var[1]);
        } catch (Exception e) {
            show(new CustomMessage("common.Error", player), player);
            return;
        }

        _category = category;
        _autoContinue = autoContinue;

        if (_category == -1) {
            _minLevel = 1;
            _maxLevel = 85;
        } else {
            _minLevel = getMinLevelForCategory(_category);
            _maxLevel = getMaxLevelForCategory(_category);
        }

        if (_endTask != null) {
            show(new CustomMessage("common.TryLater", player), player);
            return;
        }

        _status = 0;
        _isRegistrationActive = true;
        _time_to_start = Config.EVENT_TvTTime;

        live_list1 = new CopyOnWriteArrayList<Player>();
        live_list2 = new CopyOnWriteArrayList<Player>();

        playerRestoreCoord = new LinkedHashMap<Player, Location>();

        String[] param = {
            String.valueOf(_time_to_start),
            String.valueOf(_minLevel),
            String.valueOf(_maxLevel)
        };
        sayToAll("scripts.events.TvT.AnnouncePreStart", param);

        executeTask("events.TvT.TvT", "question", new Object[0], 10000);
        executeTask("events.TvT.TvT", "announce", new Object[0], 60000);
    }

    public static void sayToAll(String address, String[] replacements) {
        Announcements.getInstance().announceByCustomMessage(address, replacements, ChatType.CRITICAL_ANNOUNCE);
    }

    public static void question() {
        for (Player player : GameObjectsStorage.getAllPlayersForIterate()) {
            if (player != null && !player.isDead() && player.getLevel() >= _minLevel && player.getLevel() <= _maxLevel && player.getReflection().isDefault() && !player.isInOlympiadMode() && !player.isInObserverMode()) {
                player.scriptRequest(new CustomMessage("scripts.events.TvT.AskPlayer", player).toString(), "events.TvT.TvT:addPlayer", new Object[0]);
            }
        }
    }

    public static void announce() {
        if (_time_to_start > 1) {
            _time_to_start--;
            String[] param = {
                String.valueOf(_time_to_start),
                String.valueOf(_minLevel),
                String.valueOf(_maxLevel)
            };
            sayToAll("scripts.events.TvT.AnnouncePreStart", param);
            executeTask("events.TvT.TvT", "announce", new Object[0], 60000);
        } else {
            if (_redTeamPoints.isEmpty() || _blueTeamPoints.isEmpty() || _redTeamPoints.size() < Config.EVENT_TvTMinPlayerInTeam || _blueTeamPoints.size() < Config.EVENT_TvTMinPlayerInTeam) {
                sayToAll("scripts.events.TvT.AnnounceEventCancelled", null);
                _isRegistrationActive = false;
                _status = 0;
                boxes.clear();
                executeTask("events.TvT.TvT", "autoContinue", new Object[0], 10000);
            } else {
                _status = 1;
                _isRegistrationActive = false;
                sayToAll("scripts.events.TvT.AnnounceEventStarting", null);
                executeTask("events.TvT.TvT", "prepare", new Object[0], 5000);
            }
        }
    }

    public void addPlayer() {
        Player player = getSelf();
        if (player == null || !checkPlayer(player, true) || !checkDualBox(player)) {
            return;
        }

        int team = 0, size1 = _redTeamPoints.size(), size2 = _blueTeamPoints.size();

        if (size1 == Config.EVENT_TvTMaxPlayerInTeam && size2 == Config.EVENT_TvTMaxPlayerInTeam) {
            show(new CustomMessage("scripts.events.TvT.CancelledCount", player), player);
            _isRegistrationActive = false;
            return;
        }

        if (!Config.EVENT_TvTAllowMultiReg) {
            if ("IP".equalsIgnoreCase(Config.EVENT_TvTCheckWindowMethod)) {
                boxes.put(player, player.getIP());
            }
            if ("HWid".equalsIgnoreCase(Config.EVENT_TvTCheckWindowMethod)) {
                boxes.put(player, player.getNetConnection().getHWID());
            }
        }

        if (size1 > size2) {
            team = 2;
        } else if (size1 < size2) {
            team = 1;
        } else {
            team = Rnd.get(1, 2);
        }

        if (team == 1) {
            _redTeamPoints.put(player, 0);
            live_list1.add(player);
            show(new CustomMessage("scripts.events.TvT.Registered", player), player);
        } else if (team == 2) {
            _blueTeamPoints.put(player, 0);
            live_list2.add(player);
            show(new CustomMessage("scripts.events.TvT.Registered", player), player);
        } else {
            _log.info("WTF??? Command id 0 in TvT...");
        }
    }

    public static boolean checkPlayer(Player player, boolean first) {

        if (first && (!_isRegistrationActive || player.isDead())) {
            show(new CustomMessage("scripts.events.Late", player), player);
            return false;
        }

        if (first && (_redTeamPoints.containsKey(player) || _blueTeamPoints.containsKey(player))) {
            show(new CustomMessage("scripts.events.TvT.Cancelled", player), player);
            if (_redTeamPoints.containsKey(player)) {
                _redTeamPoints.remove(player);
            }
            if (_blueTeamPoints.containsKey(player)) {
                _blueTeamPoints.remove(player);
            }
            if (live_list1.contains(player)) {
                live_list1.remove(player);
            }
            if (live_list2.contains(player)) {
                live_list2.remove(player);
            }
            if (boxes.containsKey(player)) {
                boxes.remove(player);
            }
            return false;
        }

        if (player.getLevel() < _minLevel || player.getLevel() > _maxLevel) {
            show(new CustomMessage("scripts.events.TvT.CancelledLevel", player), player);
            return false;
        }

        if (player.isMounted()) {
            show(new CustomMessage("scripts.events.TvT.Cancelled", player), player);
            return false;
        }

        if (player.isInDuel()) {
            show(new CustomMessage("scripts.events.TvT.CancelledDuel", player), player);
            return false;
        }

        if (player.isInPvPEvent()) {
            show(new CustomMessage("scripts.events.TvT.CancelledOtherEvent", player), player);
            return false;
        }

        if (player.getTeam() != TeamType.NONE) {
            show(new CustomMessage("scripts.events.TvT.CancelledOtherEvent", player), player);
            return false;
        }

        if (player.isCursedWeaponEquipped()) {
            show(new CustomMessage("scripts.events.CaptureTheFlag.Cancelled", player), player);
            return false;
        }

        if (player.getOlympiadGame() != null || first && Olympiad.isRegistered(player)) {
            show(new CustomMessage("scripts.events.TvT.CancelledOlympiad", player), player);
            return false;
        }

        if (player.isInParty() && player.getParty().isInDimensionalRift()) {
            show(new CustomMessage("scripts.events.TvT.CancelledOtherEvent", player), player);
            return false;
        }

        if (player.isInObserverMode()) {
            show(new CustomMessage("scripts.event.TvT.CancelledObserver", player), player);
            return false;
        }

        if (player.isTeleporting()) {
            show(new CustomMessage("scripts.events.TvT.CancelledTeleport", player), player);
            return false;
        }

        if (player.getKarma() > 0) {
            show(new CustomMessage("scripts.events.TvT.CancelledTeleport", player), player);
            return false;
        }
        return true;
    }

    public static void prepare() {
        for (DoorInstance door : reflection.getDoors()) {
            door.openMe();
        }

        for (Zone z : reflection.getZones()) {
            z.setType(ZoneType.peace_zone);
        }

        cleanPlayers();
        clearArena();
        executeTask("events.TvT.TvT", "ressurectPlayers", new Object[0], 1000);
        executeTask("events.TvT.TvT", "healPlayers", new Object[0], 2000);
        executeTask("events.TvT.TvT", "paralyzePlayers", new Object[0], 3000);
        executeTask("events.TvT.TvT", "teleportPlayersToColiseum", new Object[0], 4000);
        executeTask("events.TvT.TvT", "buffPlayers", new Object[0], 5000);
        executeTask("events.TvT.TvT", "go", new Object[0], 60000);

        sayToAll("scripts.events.TvT.AnnounceFinalCountdown", null);
    }

    public static void go() {
        _status = 2;
        blockItems();
        upParalyzePlayers();
        checkLive();
        clearArena();
        _pScore.clear();
        bluePoints = 0;
        redPoints = 0;
        sayToAll("scripts.events.TvT.AnnounceFight", null);
        for (Zone z : reflection.getZones()) {
            z.setType(ZoneType.battle_zone);
        }
        _endTask = executeTask("events.TvT.TvT", "endBattle", new Object[0], 360000); //test
        _startedTime = System.currentTimeMillis() + 360000;

        final ExCubeGameChangePoints initialPoints = new ExCubeGameChangePoints(360, bluePoints, redPoints);
        ExCubeGameExtendedChangePoints clientSetUp;

        for (Player player : getPlayers(_redTeamPoints)) {
            _redTeamPoints.put(player, 0);
            clientSetUp = new ExCubeGameExtendedChangePoints(360, bluePoints, redPoints, true, player, 0);
            player.sendPacket(initialPoints);
            player.sendPacket(clientSetUp);
            player.sendPacket(new ExCubeGameAddPlayer(player, true));
        }

        for (Player player : getPlayers(_blueTeamPoints)) {
            _blueTeamPoints.put(player, 0);
            clientSetUp = new ExCubeGameExtendedChangePoints(360, bluePoints, redPoints, false, player, 0);
            player.sendPacket(clientSetUp);
            player.sendPacket(initialPoints);
            player.sendPacket(new ExCubeGameAddPlayer(player, false));
        }

        if (Config.EVENT_TvTAllowParty) {
            addParty(_redTeamPoints);
            addParty(_blueTeamPoints);
        }
    }

    public static void addParty(Map<Player, Integer> Team) {
        List<Player> list = new ArrayList<Player>();
        for (Player player : getPlayers(Team)) {
            if (player != null) {
                list.add(player);
                player.leaveParty();
            }
        }

        if (list.size() <= 1) {
            return;
        }

        Player leader = list.get(0);
        if (leader == null) {
            return;
        }

        Party party = new Party(leader, 0);
        leader.setParty(party);

        for (Player player : list) {
            if (player != leader) {
                player.joinParty(party);
            }
        }
    }

    public static void endBattle() {
        _status = 0;
        removeAura();
        for (Zone z : reflection.getZones()) {
            z.setType(ZoneType.peace_zone);
        }
        boxes.clear();

        if (bluePoints > redPoints) {
            sayToAll("scripts.events.TvT.AnnounceFinishedBlueWins", null);
            giveItemsToWinner(false, true, 1);
        } else if (bluePoints < redPoints) {
            sayToAll("scripts.events.TvT.AnnounceFinishedRedWins", null);
            giveItemsToWinner(true, false, 1);
        } else if (bluePoints == redPoints) {
            sayToAll("scripts.events.TvT.AnnounceFinishedDraw", null);
            giveItemsToWinner(true, true, 0.5);
        }

        sayToAll("scripts.events.TvT.AnnounceEnd", null);
        executeTask("events.TvT.TvT", "end", new Object[0], 30000);
        _isRegistrationActive = false;
        if (_endTask != null) {
            _endTask.cancel(false);
            _endTask = null;
        }
        boolean _isRedWinner = bluePoints < redPoints;
        final ExCubeGameEnd end = new ExCubeGameEnd(_isRedWinner);
        broadCastPacketToTeam(end);

        bluePoints = 0;
        redPoints = 0;
        _startedTime = 0;
        _pScore.clear();
        _myZone = null;
        territory = null;
    }

    public static void end() {
        unlockItems();
        executeTask("events.TvT.TvT", "ressurectPlayers", new Object[0], 1000);
        executeTask("events.TvT.TvT", "healPlayers", new Object[0], 2000);
        executeTask("events.TvT.TvT", "teleportPlayers", new Object[0], 3000);
        executeTask("events.TvT.TvT", "autoContinue", new Object[0], 10000);
    }

    public void autoContinue() {
        live_list1.clear();
        live_list2.clear();
        _redTeamPoints.clear();
        _blueTeamPoints.clear();

        if (_autoContinue > 0) {
            if (_autoContinue >= 6) {
                _autoContinue = 0;
                return;
            }
            start(new String[]{
                "" + (_autoContinue + 1),
                "" + (_autoContinue + 1)
            });
        } else {
            scheduleEventStart();
        }
    }

    public static void giveItemsToWinner(boolean team1, boolean team2, double rate) {
        if (team1) {
            for (Player player : getPlayers(_redTeamPoints)) {
                for (int[] reward : rewards) {
                    addItem(player, reward[0], Math.round((Config.EVENT_TvTrate ? player.getLevel() : 1) * reward[1] * rate));
                }
            }
        }
        if (team2) {
            for (Player player : getPlayers(_blueTeamPoints)) {
                for (int[] reward : rewards) {
                    addItem(player, reward[0], Math.round((Config.EVENT_TvTrate ? player.getLevel() : 1) * reward[1] * rate));
                }
            }
        }
    }

    public static void teleportPlayersToColiseum() {
        switch (Rnd.get(1, 11)) {
            case 1:
                _myZone = _zone;
                break;
            case 2:
                _myZone = _zone1;
                break;
            case 3:
                _myZone = _zone2;
                break;
            case 4:
                _myZone = _zone3;
                break;
            case 5:
                _myZone = _zone4;
                break;
            case 6:
                _myZone = _zone5;
                break;
            case 7:
                _myZone = _zone6;
                break;
            case 8:
                _myZone = _zone7;
                break;
            case 9:
                _myZone = _zone8;
                break;
            case 10:
                _myZone = _zone9;
                break;
            case 11:
                _myZone = _zone10;
                break;
            default:
                _myZone = _zone;
        }
        territory = _myZone.getTerritory();

        for (Player player : getPlayers(_redTeamPoints)) {
            unRide(player);

            if (!Config.EVENT_TvTAllowSummons) {
                unSummonPet(player, true);
            }

            DuelEvent duel = player.getEvent(DuelEvent.class);
            if (duel != null) {
                duel.abortDuel(player);
            }

            playerRestoreCoord.put(player, new Location(player.getX(), player.getY(), player.getZ()));

            player.teleToLocation(Territory.getRandomLoc(territory), reflection);
            player.setIsInTVT(true);
            player.sendPacket(new ShowBoard());

            if (!Config.EVENT_TvTAllowBuffs) {
                player.getEffectList().stopAllEffects();
                if (player.getPet() != null) {
                    player.getPet().getEffectList().stopAllEffects();
                }
            }
        }

        for (Player player : getPlayers(_blueTeamPoints)) {
            unRide(player);

            if (!Config.EVENT_TvTAllowSummons) {
                unSummonPet(player, true);
            }

            playerRestoreCoord.put(player, new Location(player.getX(), player.getY(), player.getZ()));

            player.teleToLocation(Territory.getRandomLoc(territory), reflection);
            player.setIsInTVT(true);
            player.sendPacket(new ShowBoard());

            if (!Config.EVENT_TvTAllowBuffs) {
                player.getEffectList().stopAllEffects();
                if (player.getPet() != null) {
                    player.getPet().getEffectList().stopAllEffects();
                }
            }
        }
    }

    public static void teleportPlayers() {
        for (Player player : getPlayers(_redTeamPoints)) {
            if (player == null || !playerRestoreCoord.containsKey(player)) {
                continue;
            }
            player.teleToLocation(playerRestoreCoord.get(player), ReflectionManager.DEFAULT);
        }

        for (Player player : getPlayers(_blueTeamPoints)) {
            if (player == null || !playerRestoreCoord.containsKey(player)) {
                continue;
            }
            player.teleToLocation(playerRestoreCoord.get(player), ReflectionManager.DEFAULT);
        }

        playerRestoreCoord.clear();
    }

    public static void paralyzePlayers() {
        for (Player player : getPlayers(_redTeamPoints)) {
            if (player == null) {
                continue;
            }
            player.getEffectList().stopEffect(Skill.SKILL_MYSTIC_IMMUNITY);
            if (!player.isParalyzed()) {
                player.startParalyzed();
                if (player.getPet() != null) {
                    player.getPet().startParalyzed();
                }
            }
        }
        for (Player player : getPlayers(_blueTeamPoints)) {

            if (player == null) {
                continue;
            }
            player.getEffectList().stopEffect(Skill.SKILL_MYSTIC_IMMUNITY);
            if (!player.isParalyzed()) {
                player.startParalyzed();
                if (player.getPet() != null) {
                    player.getPet().startParalyzed();
                }
            }
        }
    }

    public static void upParalyzePlayers() {
        for (Player player : getPlayers(_redTeamPoints)) {
            if (player == null) {
                continue;
            }
            if (player.isParalyzed()) {
                player.stopParalyzed();
                if (player.getPet() != null) {
                    player.getPet().stopParalyzed();
                }
            }
        }
        for (Player player : getPlayers(_blueTeamPoints)) {
            if (player == null) {
                continue;
            }
            if (player.isParalyzed()) {
                player.stopParalyzed();
                if (player.getPet() != null) {
                    player.getPet().stopParalyzed();
                }
            }
        }
    }

    public static void ressurectPlayers() {
        for (Player player : getPlayers(_redTeamPoints)) {
            if (player.isDead()) {
                player.restoreExp();
                player.setCurrentCp(player.getMaxCp());
                player.setCurrentHp(player.getMaxHp(), true);
                player.setCurrentMp(player.getMaxMp());
                player.broadcastPacket(new Revive(player));
            }
        }
        for (Player player : getPlayers(_blueTeamPoints)) {
            if (player.isDead()) {
                player.restoreExp();
                player.setCurrentCp(player.getMaxCp());
                player.setCurrentHp(player.getMaxHp(), true);
                player.setCurrentMp(player.getMaxMp());
                player.broadcastPacket(new Revive(player));
            }
        }
    }

    public static void healPlayers() {
        for (Player player : getPlayers(_redTeamPoints)) {
            player.setCurrentHpMp(player.getMaxHp(), player.getMaxMp());
            player.setCurrentCp(player.getMaxCp());
        }
        for (Player player : getPlayers(_blueTeamPoints)) {
            player.setCurrentHpMp(player.getMaxHp(), player.getMaxMp());
            player.setCurrentCp(player.getMaxCp());
        }
    }

    public static void cleanPlayers() {
        for (Player player : getPlayers(_redTeamPoints)) {
            if (!checkPlayer(player, false)) {
                removePlayer(player);
            }
        }
        for (Player player : getPlayers(_blueTeamPoints)) {
            if (!checkPlayer(player, false)) {
                removePlayer(player);
            }
        }
    }

    public static void checkLive() {
        List<Player> new_live_list1 = new CopyOnWriteArrayList<Player>();
        List<Player> new_live_list2 = new CopyOnWriteArrayList<Player>();

        for (Player player : live_list1) {
            if (player != null) {
                new_live_list1.add(player);
            }
        }

        for (Player player : live_list2) {
            if (player != null) {
                new_live_list2.add(player);
            }
        }

        live_list1 = new_live_list1;
        live_list2 = new_live_list2;

        for (Player player : getPlayersA(live_list1)) {
            if (!player.isDead() && !player.isLogoutStarted()) {
                player.setTeam(TeamType.RED);
            } else {
                loosePlayer(player);
            }
        }

        for (Player player : getPlayersA(live_list2)) {
            if (!player.isDead() && !player.isLogoutStarted()) {
                player.setTeam(TeamType.BLUE);
            } else {
                loosePlayer(player);
            }
        }

        if (live_list1.size() < 1 || live_list2.size() < 1) {
            endBattle();
        }
    }

    public static void removeAura() {
        for (Player player : getPlayersA(live_list1)) {
            player.setTeam(TeamType.NONE);
            if (player.getPet() != null) {
                player.getPet().setTeam(TeamType.NONE);
            }
            player.setIsInTVT(false);
        }
        for (Player player : getPlayersA(live_list2)) {
            player.setTeam(TeamType.NONE);
            if (player.getPet() != null) {
                player.getPet().setTeam(TeamType.NONE);
            }
            player.setIsInTVT(false);
        }
    }

    public static void clearArena() {
        if (_myZone == null) {
            return;
        }
        for (GameObject obj : _myZone.getObjects()) {
            if (obj != null) {
                Player player = obj.getPlayer();
                if (player != null && !live_list1.contains(player) && !live_list2.contains(player)) {
                    player.teleToLocation(147451, 46728, -3410, ReflectionManager.DEFAULT);
                }
            }
        }
    }

    @Override
    public void onDeath(Creature self, Creature killer) {
        if (_status > 1 && self != null && self.isPlayer() && self.getTeam() != TeamType.NONE && (live_list1.contains(self) || live_list2.contains(self))) {
            checkKillsAndAnnounce(killer.getPlayer());
            increasePlayerPoints(killer.getPlayer(), killer.getTeam());
            _pScore.remove(self.getPlayer().getObjectId());
            executeTask("events.TvT.TvT", "resurrectAtBase", new Object[]{self}, 1 * 1000);
        }

    }

    public synchronized void increasePlayerPoints(Player player, TeamType team) {
        if (player == null) {
            return;
        }

        if (team == TeamType.RED) {
            int points = getPlayerPoints(player, true) + 1;
            _redTeamPoints.put(player, points);
            redPoints++;
        } else if (team == TeamType.BLUE) {
            int points = getPlayerPoints(player, false) + 1;
            _blueTeamPoints.put(player, points);
            bluePoints++;
        }

        boolean isRed = player.getTeam() == TeamType.RED;
        int timeLeft = (int) ((getStarterTime() - System.currentTimeMillis()) / 1000);
        ExCubeGameChangePoints changePoints = new ExCubeGameChangePoints(timeLeft, bluePoints, redPoints);
        ExCubeGameExtendedChangePoints secretPoints = new ExCubeGameExtendedChangePoints(timeLeft, bluePoints, redPoints, isRed, player, getPlayerPoints(player, isRed));

        broadCastPacketToTeam(changePoints);
        broadCastPacketToTeam(secretPoints);
    }

    public int getPlayerPoints(Player player, boolean isRed) {
        if (!_redTeamPoints.containsKey(player) && !_blueTeamPoints.containsKey(player)) {
            return 0;
        }

        if (isRed) {
            return _redTeamPoints.get(player);
        } else {
            return _blueTeamPoints.get(player);
        }
    }

    private static void checkKillsAndAnnounce(Player player) {
        int score = 0;
        int score2 = 0;
        if (_pScore.get(player.getObjectId()) != null) {
            score = _pScore.get(player.getObjectId());
        }
        _pScore.put(player.getObjectId(), score + 1);

        String text = "";

        switch (_pScore.get(player.getObjectId())) {
            case 0:
            case 1:
            case 2:
                return;
            case 3:
                text = "" + player.getName() + ": Killing Spree";
                break;
            case 4:
                text = "" + player.getName() + ": Rampage";
                break;
            case 5:
                text = "" + player.getName() + ": Unstoppable";
                break;
            case 6:
                text = "" + player.getName() + ": Dominating";
                break;
            case 7:
                text = "" + player.getName() + ": Godlike";
                break;
            case 8:
                text = "" + player.getName() + ": Legendary";
                break;
            case 9:
                text = "" + player.getName() + ": Arena Master";
                break;
            case 15:
                text = "" + player.getName() + ": Best Player";
                break;
            default:
                return;
        }
        broadCastPacketToTeam(new ExShowScreenMessage(text, 3000, ScreenMessageAlign.BOTTOM_RIGHT, true));
    }

    public static void resurrectAtBase(Creature self) {
        Player player = self.getPlayer();
        if (player == null) {
            return;
        }
        if (player.getTeam() == TeamType.NONE) {
            return;
        }
        if (player.isDead()) {
            player.setCurrentCp(player.getMaxCp());
            player.setCurrentHp(player.getMaxHp(), true);
            player.setCurrentMp(player.getMaxMp());
            player.broadcastPacket(new Revive(player));
            player.teleToLocation(Territory.getRandomLoc(territory), reflection);
            buffPlayer(player);
        }
    }

    public static void buffPlayer(Player player) {
        if (player.isMageClass()) {
            mageBuff(player);
        } else {
            fighterBuff(player);
        }
    }

    @Override
    public void onTeleport(Player player, int x, int y, int z, Reflection reflection) {
    }

    @Override
    public void onPlayerExit(Player player) {
        if (player == null) {
            return;
        }

        if (player.getTeam() == TeamType.NONE) {
            return;
        }

        if (_status == 0 && _isRegistrationActive && player.getTeam() != TeamType.NONE && (live_list1.contains(player) || live_list2.contains(player))) {
            removePlayer(player);
            return;
        }

        if (_status == 1 && (live_list1.contains(player) || live_list2.contains(player))) {
            player.teleToLocation(playerRestoreCoord.get(player), ReflectionManager.DEFAULT);
            removePlayer(player);
            return;
        }

        if (_status > 1 && player.getTeam() != TeamType.NONE && (live_list1.contains(player) || live_list2.contains(player))) {
            removePlayer(player);
            checkLive();
        }
    }

    private static class ZoneListener implements OnZoneEnterLeaveListener {

        @Override
        public void onZoneEnter(Zone zone, Creature cha) {
        }

        @Override
        public void onZoneLeave(Zone zone, Creature cha) {
            if (cha == null) {
                return;
            }
            Player player = cha.getPlayer();
            if (_status > 1 && player != null && player.getTeam() != TeamType.NONE && (live_list1.contains(player) || live_list2.contains(player))) {
                double angle = PositionUtils.convertHeadingToDegree(cha.getHeading()); // СѓРіРѕР» РІ РіСЂР°РґСѓСЃР°С…
                double radian = Math.toRadians(angle - 90); // СѓРіРѕР» РІ СЂР°РґРёР°РЅР°С…
                int x = (int) (cha.getX() + 250 * Math.sin(radian));
                int y = (int) (cha.getY() - 250 * Math.cos(radian));
                int z = cha.getZ();
                player.teleToLocation(x, y, z, reflection);
            }
        }
    }

    private static void loosePlayer(Player player) {
        if (player != null) {
            live_list1.remove(player);
            live_list2.remove(player);
            player.setTeam(TeamType.NONE);
            show(new CustomMessage("scripts.events.TvT.YouLose", player), player);
        }
    }

    private static void removePlayer(Player player) {
        if (player != null) {
            if (live_list1.contains(player)) {
                live_list1.remove(player);
            }
            if (live_list2.contains(player)) {
                live_list2.remove(player);
            }

            if (_redTeamPoints.containsKey(player)) {
                _redTeamPoints.remove(player);
            }
            if (_blueTeamPoints.containsKey(player)) {
                _blueTeamPoints.remove(player);
            }

            playerRestoreCoord.remove(player);
            player.setIsInTVT(false);
            boolean isRed = player.getTeam() == TeamType.RED;

            if (!Config.EVENT_TvTAllowMultiReg) {
                boxes.remove(player);
            }

            broadCastPacketToTeam(new ExCubeGameRemovePlayer(player, isRed));

            player.setTeam(TeamType.NONE);
            player.getInventory().unlock();
        }
    }

    private static List<Player> getPlayers(Map<Player, Integer> list) {
        List<Player> result = new ArrayList<Player>();
        for (Player player : list.keySet()) {
            if (player != null) {
                result.add(player);
            }
        }
        return result;
    }

    private static List<Player> getPlayersA(List<Player> list) {
        List<Player> result = new ArrayList<Player>();
        for (Player player : list) {
            if (player != null) {
                result.add(player);
            }
        }
        return result;
    }

    public static void buffPlayers() {

        for (Player player : getPlayers(_redTeamPoints)) {
            if (player.isMageClass()) {
                mageBuff(player);
            } else {
                fighterBuff(player);
            }
        }

        for (Player player : getPlayers(_blueTeamPoints)) {
            if (player.isMageClass()) {
                mageBuff(player);
            } else {
                fighterBuff(player);
            }
        }
        final ExCubeGameTeamList tl = new ExCubeGameTeamList(getPlayers(_redTeamPoints), getPlayers(_blueTeamPoints), 1);
        broadCastPacketToTeam(tl);
    }

    public void scheduleEventStart() {
        try {
            Calendar currentTime = Calendar.getInstance();
            Calendar nextStartTime = null;
            Calendar testStartTime = null;

            for (String timeOfDay : Config.EVENT_TvTStartTime) {
                testStartTime = Calendar.getInstance();
                testStartTime.setLenient(true);

                String[] splitTimeOfDay = timeOfDay.split(":");

                testStartTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(splitTimeOfDay[0]));
                testStartTime.set(Calendar.MINUTE, Integer.parseInt(splitTimeOfDay[1]));

                if (testStartTime.getTimeInMillis() < currentTime.getTimeInMillis()) {
                    testStartTime.add(Calendar.DAY_OF_MONTH, 1);
                }

                if (nextStartTime == null || testStartTime.getTimeInMillis() < nextStartTime.getTimeInMillis()) {
                    nextStartTime = testStartTime;
                }

                if (_startTask != null) {
                    _startTask.cancel(false);
                    _startTask = null;
                }
                _startTask = ThreadPoolManager.getInstance().schedule(new StartTask(), nextStartTime.getTimeInMillis() - System.currentTimeMillis());

            }

            currentTime = null;
            nextStartTime = null;
            testStartTime = null;

        } catch (Exception e) {
            _log.warn("TvT: Error figuring out a start time. Check TvTEventInterval in config file.");
        }
    }

    public static void mageBuff(Player player) {
        for (int i = 0; i < mage_buffs.length; i++) {
            buff = SkillTable.getInstance().getInfo(mage_buffs[i][0], mage_buffs[i][1]);
            if (buff == null) {
                continue;
            }
            buff.getEffects(player, player, false, false);
        }
        player.setCurrentHp(player.getMaxHp(), true);
        player.setCurrentMp(player.getMaxMp());
        player.setCurrentCp(player.getMaxCp());
    }

    public static void fighterBuff(Player player) {
        for (int i = 0; i < fighter_buffs.length; i++) {
            buff = SkillTable.getInstance().getInfo(fighter_buffs[i][0], fighter_buffs[i][1]);
            if (buff == null) {
                continue;
            }
            buff.getEffects(player, player, false, false);
        }
        player.setCurrentHp(player.getMaxHp(), true);
        player.setCurrentMp(player.getMaxMp());
        player.setCurrentCp(player.getMaxCp());
    }

    private static boolean checkDualBox(Player player) {
        if (!Config.EVENT_TvTAllowMultiReg) {
            if ("IP".equalsIgnoreCase(Config.EVENT_TvTCheckWindowMethod)) {
                if (boxes.containsValue(player.getIP())) {
                    show(new CustomMessage("scripts.events.TvT.CancelledBox", player), player);
                    return false;
                }
            } else if ("HWid".equalsIgnoreCase(Config.EVENT_TvTCheckWindowMethod)) {
                if (boxes.containsValue(player.getNetConnection().getHWID())) {
                    show(new CustomMessage("scripts.events.TvT.CancelledBox", player), player);
                    return false;
                }
            }
        }
        return true;
    }

    public class StartTask extends RunnableImpl {

        @Override
        public void runImpl() {
            if (!_active) {
                return;
            }

            if (isPvPEventStarted()) {
                _log.info("TvT not started: another event is already running");
                return;
            }

            for (Residence c : ResidenceHolder.getInstance().getResidenceList(Castle.class)) {
                if (c.getSiegeEvent() != null && c.getSiegeEvent().isInProgress()) {
                    _log.debug("TvT not started: CastleSiege in progress");
                    return;
                }
            }

            if (Config.EVENT_TvTCategories) {
                start(new String[]{"1", "1"});
            } else {
                start(new String[]{"-1", "-1"});
            }
        }
    }

    public static void broadCastPacketToTeam(L2GameServerPacket packet) {
        ArrayList<Player> team = new ArrayList<Player>(12);
        team.addAll(getPlayers(_redTeamPoints));
        team.addAll(getPlayers(_blueTeamPoints));

        for (Player p : team) {
            p.sendPacket(packet);
        }
    }

    public static void blockItems() {
        for (Player player : getPlayers(_redTeamPoints)) {
            if (player == null) {
                continue;
            }

            player.getInventory().lockItems(LockType.INCLUDE, Config.EVENT_TvTBlockItems);
        }
        for (Player player : getPlayers(_blueTeamPoints)) {
            if (player == null) {
                continue;
            }

            player.getInventory().lockItems(LockType.INCLUDE, Config.EVENT_TvTBlockItems);
        }
    }

    public static void unlockItems() {
        for (Player player : getPlayers(_redTeamPoints)) {
            if (player == null) {
                continue;
            }

            player.getInventory().unlock();
        }
        for (Player player : getPlayers(_blueTeamPoints)) {
            if (player == null) {
                continue;
            }

            player.getInventory().unlock();
        }
    }
}

package events.LuckNPC;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import l2p.commons.threading.RunnableImpl;
import l2p.commons.util.Rnd;
import l2p.gameserver.Announcements;
import l2p.gameserver.Config;
import l2p.gameserver.ThreadPoolManager;
import l2p.gameserver.instancemanager.ServerVariables;
import l2p.gameserver.listener.actor.player.OnPlayerEnterListener;
import l2p.gameserver.model.Player;
import l2p.gameserver.model.SimpleSpawner;
import l2p.gameserver.model.actor.listener.CharListenerList;
import l2p.gameserver.scripts.Functions;
import static l2p.gameserver.scripts.Functions.IsActive;
import static l2p.gameserver.scripts.Functions.SpawnNPCs;
import static l2p.gameserver.scripts.Functions.deSpawnNPCs;
import l2p.gameserver.scripts.ScriptFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author deprecat
 */
public class LuckNPC extends Functions implements ScriptFile, OnPlayerEnterListener {

    private static final int EVENT_MANAGER_ID = Config.EVENT_LuckNPCManagerId;
    private static final String _name = "LuckNPC";
    private static ScheduledFuture<?> _startTask;
    private static boolean _active = false;
    private static ScheduledFuture<?> _stopTask;
    private static List<SimpleSpawner> _spawns = new ArrayList<SimpleSpawner>();
    private static final Logger _log = LoggerFactory.getLogger(LuckNPC.class);
    private static int[][] EVENT_MANAGERS;
    private static int[][] EVENT_REWARD;
    private static int[][] EVENT_POINTS;

    @Override
    public void onLoad() {
        if (!Config.EVENT_LuckNPC_ENABLED) {
            return;
        }
        CharListenerList.addGlobal(this);
        _active = ServerVariables.getString("LuckNPC", "off").equalsIgnoreCase("on");

        if (isActive()) {
            scheduleEventStart();
            scheduleEventStop();
        }

        if (Config.EVENT_LuckNPLoc.length > 0) {
            EVENT_MANAGERS = new int[Config.EVENT_LuckNPLoc.length][3];
        }
        if (Config.EVENT_LuckNPCReward.length > 0) {
            EVENT_REWARD = new int[Config.EVENT_LuckNPCReward.length][3];
        }
        if (Config.EVENT_LuckNPCPoints.length > 0) {
            EVENT_POINTS = new int[Config.EVENT_LuckNPCPoints.length][3];
        }

        int i = 0;

        if (Config.EVENT_LuckNPLoc.length > 0) {
            for (String skill : Config.EVENT_LuckNPLoc) {
                String[] splitSkill = skill.split(",");
                EVENT_MANAGERS[i][0] = Integer.parseInt(splitSkill[0]);
                EVENT_MANAGERS[i][1] = Integer.parseInt(splitSkill[1]);
                EVENT_MANAGERS[i][2] = Integer.parseInt(splitSkill[2]);
                i++;
            }
        }
        i = 0;

        if (Config.EVENT_LuckNPCReward.length != 0) {
            for (String skill : Config.EVENT_LuckNPCReward) {
                String[] splitSkill = skill.split(",");
                EVENT_REWARD[i][0] = Integer.parseInt(splitSkill[0]);
                EVENT_REWARD[i][1] = Integer.parseInt(splitSkill[1]);
                EVENT_REWARD[i][2] = Integer.parseInt(splitSkill[2]);
                i++;
            }
        }

        i = 0;

        if (Config.EVENT_LuckNPCPoints.length != 0) {
            for (String skill : Config.EVENT_LuckNPCPoints) {
                String[] splitSkill = skill.split(",");
                EVENT_POINTS[i][0] = Integer.parseInt(splitSkill[0]);
                EVENT_POINTS[i][1] = Integer.parseInt(splitSkill[1]);
                EVENT_POINTS[i][2] = Integer.parseInt(splitSkill[2]);
                i++;
            }
        }
        _log.info("Loaded Event: Luck NPC");
    }

    /**
     * Читает статус эвента из базы.
     */
    protected static boolean isActive() {
        return IsActive(_name);
    }

    /**
     * Спавнит эвент менеджеров
     */
    protected void spawnEventManagers() {
        SpawnNPCs(EVENT_MANAGER_ID, EVENT_MANAGERS, _spawns);
    }

    /**
     * Удаляет спавн эвент менеджеров
     */
    protected void unSpawnEventManagers() {
        deSpawnNPCs(_spawns);
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

    public void activateEvent() {
        Player player = getSelf();
        if (!player.getPlayerAccess().IsEventGm) {
            return;
        }
        if (!isActive()) {
            if (_startTask == null) {
                scheduleEventStart();
            }
            ServerVariables.set("LuckNPC", "on");
            _log.info("Event 'LuckNPC' activated.");
            Announcements.getInstance().announceByCustomMessage("scripts.events.LuckNPC.AnnounceEventStarted", null);
        } else {
            player.sendMessage("Event 'LuckNPC' already active.");
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
            ServerVariables.unset("LuckNPC");
            _log.info("Event 'LuckNPC' deactivated.");
            Announcements.getInstance().announceByCustomMessage("scripts.events.LuckNPC.AnnounceEventStoped", null);
        } else {
            player.sendMessage("Event 'LuckNPC' not active.");
        }
        _active = false;

        show("admin/events/events.htm", player);
    }

    public void scheduleEventStart() {
        int i = 0;
        try {
            Calendar currentTime = Calendar.getInstance();
            Calendar nextStartTime = null;
            Calendar testStartTime = null;

            for (String timeOfDay : Config.EVENT_LuckNPCStartTime) {
                testStartTime = Calendar.getInstance();
                testStartTime.setLenient(true);

                String[] splitTimeOfDay = timeOfDay.split(":");

                testStartTime.set(11, Integer.parseInt(splitTimeOfDay[0]));
                testStartTime.set(12, Integer.parseInt(splitTimeOfDay[1]));

                if (testStartTime.getTimeInMillis() < currentTime.getTimeInMillis()) {
                    testStartTime.add(5, 1);
                }
                if ((nextStartTime == null) || (testStartTime.getTimeInMillis() < nextStartTime.getTimeInMillis())) {
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
            _log.warn("LuckNPC: Error figuring out a start time. Check LuckNPCStartTime in config file.");
        }
    }

    public void scheduleEventStop() {
        try {
            Calendar currentTime = Calendar.getInstance();
            Calendar nextStartTime = null;
            Calendar testStartTime = null;

            for (String timeOfDay : Config.EVENT_LuckNPCStopTime) {
                testStartTime = Calendar.getInstance();
                testStartTime.setLenient(true);

                String[] splitTimeOfDay = timeOfDay.split(":");

                testStartTime.set(11, Integer.parseInt(splitTimeOfDay[0]));
                testStartTime.set(12, Integer.parseInt(splitTimeOfDay[1]));

                if (testStartTime.getTimeInMillis() < currentTime.getTimeInMillis()) {
                    testStartTime.add(5, 1);
                }
                if ((nextStartTime == null) || (testStartTime.getTimeInMillis() < nextStartTime.getTimeInMillis())) {
                    nextStartTime = testStartTime;
                }
                if (_stopTask != null) {
                    _stopTask.cancel(false);
                    _stopTask = null;
                }
                _stopTask = ThreadPoolManager.getInstance().schedule(new StopTask(), nextStartTime.getTimeInMillis() - System.currentTimeMillis());
            }

            currentTime = null;
            nextStartTime = null;
            testStartTime = null;
        } catch (Exception e) {
            _log.warn("LuckNPC: Error figuring out a stop time. Check LuckNPCStartTime in config file.");
        }
    }

    @Override
    public void onPlayerEnter(Player player) {
        if (_active) {
            Announcements.getInstance().announceToPlayerByCustomMessage(player, "scripts.events.LuckNPC.AnnounceEventStarted", null);
        }
    }

    public class StartTask extends RunnableImpl {

        public StartTask() {
        }

        @Override
        public void runImpl() {
            if (!LuckNPC._active) {
                return;
            }
            spawnEventManagers();
        }
    }

    public class StopTask extends RunnableImpl {

        public StopTask() {
        }

        @Override
        public void runImpl() {
            if (!LuckNPC._active) {
                return;
            }
            unSpawnEventManagers();
        }
    }

    public void getGift() {

        if (!Config.EVENT_LuckNPC_ENABLED || !LuckNPC._active) {
            return;
        }
        Player player = getSelf();

        if (Rnd.chance(Config.EVENT_LuckNPCChance)) {

            for (int[] item : EVENT_REWARD) {
                if (Rnd.chance(item[2])) {
                    Functions.addItem(player, item[0], item[1]);
                }
            }
        } else {
            int locPoint = Rnd.get(EVENT_POINTS.length);
            player.teleToLocation(EVENT_POINTS[locPoint][0], EVENT_POINTS[locPoint][1], EVENT_POINTS[locPoint][2], 0);
        }
    }
}

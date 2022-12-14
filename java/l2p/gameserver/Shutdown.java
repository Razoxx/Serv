package l2p.gameserver;

import java.util.Timer;
import java.util.TimerTask;

import l2p.commons.net.nio.impl.SelectorThread;
import l2p.commons.time.cron.SchedulingPattern;
import l2p.commons.time.cron.SchedulingPattern.InvalidPatternException;
import l2p.gameserver.database.DatabaseFactory;
import l2p.gameserver.instancemanager.CoupleManager;
import l2p.gameserver.instancemanager.CursedWeaponsManager;
import l2p.gameserver.instancemanager.ExchangeBroker;
import l2p.gameserver.instancemanager.games.FishingChampionShipManager;
import l2p.gameserver.model.GameObjectsStorage;
import l2p.gameserver.model.Player;
import l2p.gameserver.model.entity.Hero;
import l2p.gameserver.model.entity.SevenSigns;
import l2p.gameserver.model.entity.SevenSignsFestival.SevenSignsFestival;
import l2p.gameserver.model.entity.olympiad.OlympiadDatabase;
import l2p.gameserver.loginservercon.AuthServerCommunication;
import l2p.gameserver.network.GameClient;
import l2p.gameserver.serverpackets.components.SystemMsg;
import l2p.gameserver.serverpackets.SystemMessage2;
import l2p.gameserver.scripts.Scripts;
import l2p.gameserver.utils.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Shutdown extends Thread {

    private static final Logger _log = LoggerFactory.getLogger(Shutdown.class);
    public static final int SHUTDOWN = 0;
    public static final int RESTART = 2;
    public static final int NONE = -1;
    private static final Shutdown _instance = new Shutdown();

    public static final Shutdown getInstance() {
        return _instance;
    }
    private Timer counter;
    private int shutdownMode;
    private int shutdownCounter;

    private class ShutdownCounter extends TimerTask {

        @Override
        public void run() {
            switch (shutdownCounter) {
                case 1800:
                    Announcements.getInstance().announceByCustomMessage("THE_SERVER_WILL_BE_COMING_DOWN_IN_S1_MINUTES", new String[]{String.valueOf(shutdownCounter / 60)});
                    break;
                case 1200:
                    Announcements.getInstance().announceByCustomMessage("THE_SERVER_WILL_BE_COMING_DOWN_IN_S1_MINUTES", new String[]{String.valueOf(shutdownCounter / 60)});
                    break;
                case 900:
                case 600:
                    Announcements.getInstance().announceByCustomMessage("THE_SERVER_WILL_BE_COMING_DOWN_IN_S1_MINUTES", new String[]{String.valueOf(shutdownCounter / 60)});
                    break;
                case 300:
                    Announcements.getInstance().announceByCustomMessage("THE_SERVER_WILL_BE_COMING_DOWN_IN_S1_MINUTES", new String[]{String.valueOf(shutdownCounter / 60)});
                    break;
                case 240:
                case 180:
                case 120:
                case 60:
                    Announcements.getInstance().announceByCustomMessage("THE_SERVER_WILL_BE_COMING_DOWN_IN_S1_MINUTES", new String[]{String.valueOf(shutdownCounter / 60)});
                    break;
                case 30:
                    Announcements.getInstance().announceToAll(new SystemMessage2(SystemMsg.THE_SERVER_WILL_BE_COMING_DOWN_IN_S1_SECONDS__PLEASE_FIND_A_SAFE_PLACE_TO_LOG_OUT).addInteger(shutdownCounter));
                    break;
                case 20:
                case 10:
                    Announcements.getInstance().announceToAll(new SystemMessage2(SystemMsg.THE_SERVER_WILL_BE_COMING_DOWN_IN_S1_SECONDS__PLEASE_FIND_A_SAFE_PLACE_TO_LOG_OUT).addInteger(shutdownCounter));
                    break;
                case 5:
                    Announcements.getInstance().announceToAll(new SystemMessage2(SystemMsg.THE_SERVER_WILL_BE_COMING_DOWN_IN_S1_SECONDS__PLEASE_FIND_A_SAFE_PLACE_TO_LOG_OUT).addInteger(shutdownCounter));
                    break;
                case 0:
                    switch (shutdownMode) {
                        case SHUTDOWN:
                            Runtime.getRuntime().exit(SHUTDOWN);
                            break;
                        case RESTART:
                            Runtime.getRuntime().exit(RESTART);
                            break;
                    }
                    cancel();
                    return;
            }

            shutdownCounter--;
        }
    }

    private Shutdown() {
        setName(getClass().getSimpleName());
        setDaemon(true);

        shutdownMode = NONE;
    }

    /**
     * ?????????? ?? ???????????????? ???? ????????????????????.
     *
     * @return ?????????? ?? ???????????????? ???? ???????????????????? ??????????????, -1 ???????? ???????????????????? ????
     * ??????????????????????????
     */
    public int getSeconds() {
        return shutdownMode == NONE ? -1 : shutdownCounter;
    }

    /**
     * ?????????? ????????????????????.
     *
     * @return <code>SHUTDOWN</code> ?????? <code>RESTART</code>, ????????
     * <code>NONE</code>, ???????? ???????????????????? ???? ??????????????????????????.
     */
    public int getMode() {
        return shutdownMode;
    }

    /**
     * ?????????????????????????? ???????????????????? ?????????????? ?????????? ???????????????????????? ???????????????????? ??????????????.
     *
     * @param time ?????????? ?? ?????????????? <code>hh:mm</code>
     * @param shutdownMode  <code>SHUTDOWN</code> ?????? <code>RESTART</code>
     */
    public synchronized void schedule(int seconds, int shutdownMode) {
        if (seconds < 0) {
            return;
        }

        if (counter != null) {
            counter.cancel();
        }

        this.shutdownMode = shutdownMode;
        this.shutdownCounter = seconds;

        _log.info("Scheduled server " + (shutdownMode == SHUTDOWN ? "shutdown" : "restart") + " in " + Util.formatTime(seconds) + ".");

        counter = new Timer("ShutdownCounter", true);
        counter.scheduleAtFixedRate(new ShutdownCounter(), 0, 1000L);
    }

    /**
     * ?????????????????????????? ???????????????????? ?????????????? ???? ???????????????????????? ??????????.
     *
     * @param time ?????????? ?? ?????????????? cron
     * @param shutdownMode <code>SHUTDOWN</code> ?????? <code>RESTART</code>
     */
    public void schedule(String time, int shutdownMode) {
        SchedulingPattern cronTime;
        try {
            cronTime = new SchedulingPattern(time);
        } catch (InvalidPatternException e) {
            return;
        }

        int seconds = (int) (cronTime.next(System.currentTimeMillis()) / 1000L - System.currentTimeMillis() / 1000L);
        schedule(seconds, shutdownMode);
    }

    /**
     * ???????????????? ?????????????????????????????? ???????????????????? ??????????????.
     */
    public synchronized void cancel() {
        shutdownMode = NONE;
        if (counter != null) {
            counter.cancel();
        }
        counter = null;
    }

    @Override
    public void run() {
        System.out.println("Shutting down LS/GS communication...");
        AuthServerCommunication.getInstance().shutdown();

        System.out.println("Shutting down scripts...");
        Scripts.getInstance().shutdown();

        System.out.println("Disconnecting players...");
        disconnectAllPlayers();

        System.out.println("Saving data...");
        saveData();

        try {
            System.out.println("Shutting down thread pool...");
            ThreadPoolManager.getInstance().shutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("Shutting down selector...");
        if (GameServer.getInstance() != null) {
            for (SelectorThread<GameClient> st : GameServer.getInstance().getSelectorThreads()) {
                try {
                    st.shutdown();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        try {
            System.out.println("Shutting down database communication...");
            DatabaseFactory.getInstance().shutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("Shutdown finished.");
    }

    private void saveData() {
        try {
            // Seven Signs data is now saved along with Festival data.
            if (!SevenSigns.getInstance().isSealValidationPeriod()) {
                SevenSignsFestival.getInstance().saveFestivalData(false);
                System.out.println("SevenSignsFestival: Data saved.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            SevenSigns.getInstance().saveSevenSignsData(0, true);
            System.out.println("SevenSigns: Data saved.");
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (Config.ENABLE_OLYMPIAD) {
            try {
                OlympiadDatabase.save();
                System.out.println("Olympiad: Data saved.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (Config.ALLOW_WEDDING) {
            try {
                CoupleManager.getInstance().store();
                System.out.println("CoupleManager: Data saved.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        try {
            FishingChampionShipManager.getInstance().shutdown();
            System.out.println("FishingChampionShipManager: Data saved.");
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            Hero.getInstance().shutdown();
            System.out.println("Hero: Data saved.");
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (Config.COMMUNITYBOARD_EXCHANGE_ENABLED) {
            try {
                ExchangeBroker.getInstance().saveItems();
                System.out.println("ExchangeBrokerManager: Data saved,");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (Config.ALLOW_CURSED_WEAPONS) {
            try {
                CursedWeaponsManager.getInstance().saveData();
                System.out.println("CursedWeaponsManager: Data saved,");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void disconnectAllPlayers() {
        for (Player player : GameObjectsStorage.getAllPlayersForIterate()) {
            try {
                player.restart();
            } catch (Exception e) {
                System.out.println("Error while disconnecting: " + player + "!");
                e.printStackTrace();
            }
        }
    }
}

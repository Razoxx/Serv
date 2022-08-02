package ftGuard;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;
import l2p.commons.configuration.ExProperties;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ftConfig {

    private static final Logger _log = LoggerFactory.getLogger(ftGuard.class);
    public static final String FIRST_GUARD_FILE = "config/FirstGuard.ini";
    public static boolean ALLOW_GUARD_SYSTEM;
    public static boolean SHOW_PROTECTION_INFO_IN_CLIENT;
    public static boolean SHOW_NAME_SERVER_IN_CLIENT;
    public static boolean SHOW_ONLINE_IN_CLIENT;
    public static boolean SHOW_SERVER_TIME_IN_CLIENT;
    public static boolean SHOW_REAL_TIME_IN_CLIENT;
    public static boolean SHOW_PING_IN_CLIENT;
    public static long TIME_REFRESH_SPECIAL_STRING;
    public static int PositionXProtectionInfoInClient;
    public static int PositionYProtectionInfoInClient;
    public static String NameServerInfoInClient;
    public static int PositionXNameServerInfoInClient;
    public static int PositionYNameServerInfoInClient;
    public static int PositionXOnlineInClient;
    public static int PositionYOnlineInClient;
    public static int PositionXServerTimeInClient;
    public static int PositionYServerTimeInClient;
    public static int PositionXRealTimeInClient;
    public static int PositionYRealTimeInClient;
    public static int PositionXPingInClient;
    public static int PositionYPingInClient;
    public static int GET_CLIENT_HWID;
    //public static String GUARD_VERSION;
    //public static boolean GUARD_ENABLE_DATABASE_LOG;
    //public static int GUARD_LOG_SERVER_ID;
    //public static String DATABASE_DATASOURCE;
    private static final File CONFIG_FILE = new File(FIRST_GUARD_FILE);
    private static final AtomicLong lastModified = new AtomicLong(CONFIG_FILE.lastModified());

    public static final void load() {
        _log.info("Loading First Guard configuration...");
        ExProperties guardSettings = load(FIRST_GUARD_FILE);

        //DEBUG = Boolean.parseBoolean(properties.getProperty("Debug", "False"));

        ALLOW_GUARD_SYSTEM = guardSettings.getProperty("AllowGuardSystem", false);
        SHOW_PROTECTION_INFO_IN_CLIENT = guardSettings.getProperty("ShowProtectionInfoInClient", false);
        SHOW_NAME_SERVER_IN_CLIENT = guardSettings.getProperty("ShowNameServerInfoInClient", false);
        SHOW_ONLINE_IN_CLIENT = guardSettings.getProperty("ShowOnlineInClient", false);
        SHOW_SERVER_TIME_IN_CLIENT = guardSettings.getProperty("ShowServerTimeInClient", false);
        SHOW_REAL_TIME_IN_CLIENT = guardSettings.getProperty("ShowRealTimeInClient", false);
        SHOW_PING_IN_CLIENT = guardSettings.getProperty("ShowPingInClient", false);
        TIME_REFRESH_SPECIAL_STRING = guardSettings.getProperty("TimeRefreshStringToClient", 1000);
        NameServerInfoInClient = guardSettings.getProperty("NameServerInfoInClient", "Test");
        PositionXProtectionInfoInClient = guardSettings.getProperty("PositionXProtectionInfoInClient", 320);
        PositionYProtectionInfoInClient = guardSettings.getProperty("PositionYProtectionInfoInClient", 10);
        PositionXNameServerInfoInClient = guardSettings.getProperty("PositionXNameServerInfoInClient", 320);
        PositionYNameServerInfoInClient = guardSettings.getProperty("PositionYNameServerInfoInClient", 25);
        PositionXOnlineInClient = guardSettings.getProperty("PositionXOnlineInClient", 320);
        PositionYOnlineInClient = guardSettings.getProperty("PositionYOnlineInClient", 40);
        PositionXServerTimeInClient = guardSettings.getProperty("PositionXServerTimeInClient", 320);
        PositionYServerTimeInClient = guardSettings.getProperty("PositionYServerTimeInClient", 55);
        PositionXRealTimeInClient = guardSettings.getProperty("PositionXRealTimeInClient", 320);
        PositionYRealTimeInClient = guardSettings.getProperty("PositionYRealTimeInClient", 70);
        PositionXPingInClient = guardSettings.getProperty("PositionXPingInClient", 320);
        PositionYPingInClient = guardSettings.getProperty("PositionYPingInClient", 85);
        GET_CLIENT_HWID = guardSettings.getProperty("UseClientHWID", 2);
    }

    public static ExProperties load(String filename) {
        return load(new File(filename));
    }

    public static ExProperties load(File file) {
        ExProperties result = new ExProperties();

        try {
            result.load(file);
        } catch (IOException e) {
            _log.error("Error loading config : " + file.getName() + "!");
        }

        return result;
    }

    public static void reload() {
        long modified = CONFIG_FILE.lastModified();

        if (lastModified.getAndSet(modified) == modified) {
            return;
        }
        load();
    }
}
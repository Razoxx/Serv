package ftGuard;

import ftGuard.utils.Util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ftGuard {

    private static final Logger _log = LoggerFactory.getLogger(ftGuard.class);

    public static void Init() {
        ftConfig.load();
        _log.info("************[ Protection System: Start Loading ]*************");
        _log.info("************[ Protection System: Off ]*************");
        _log.info("************[ Protection System: Finish Loading ]*************");
    }

    public static boolean isProtectionOn() {
        return false;
    }

    public static String getHwid(String hwid) {
        return Util.asHwidString(hwid);
    }

    public static byte[] getKey(byte[] key) {
        return key;
    }
}
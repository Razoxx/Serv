package ftGuard;

import java.io.IOException;

import ftGuard.crypt.BlowfishEngine;
import ftGuard.utils.Util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ftGuard {

    private static final Logger _log = LoggerFactory.getLogger(ftGuard.class);
    private static byte[] _key = new byte[16];

    public static void Init() {
        ftConfig.load();
        if (isProtectionOn()) {
            _log.info("************[ Protection System: Start Loading ]*************");
            //	HwidBan.getInstance();
            //	HwidManager.getInstance();
            //	ProtectManager.getInstance();
            //	AdminCommandHandler.getInstance().registerAdminCommandHandler(new AdminHWID());
            _log.info("************[ Protection System: Finish Loading ]*************");
            _log.info("************[ Protection System: First Guard ON ]*************");
        }
    }

    public static boolean isProtectionOn() {
        if (ftConfig.ALLOW_GUARD_SYSTEM) {
            return true;
        }
        return false;
    }

    public static String getHwid(String hwid) {
        if (hwid.isEmpty() || hwid == "" || hwid == null || hwid == " ") {
            return "NoClientPatch!!!";
        }
        return Util.asHwidString(hwid);
    }

    public static byte[] getKey(byte[] key) {
        byte[] bfkey = {120, 59, 2, 17, -5, 97, 44, 37, 28, 45, 1, 14, 102, 76, -5, 31};
        try {
            BlowfishEngine bf = new BlowfishEngine();
            bf.init(true, bfkey);
            bf.processBlock(key, 0, _key, 0);
            bf.processBlock(key, 8, _key, 8);
        } catch (IOException e) {
            _log.info("Bad key!!!");
        }
        return _key;
    }
}
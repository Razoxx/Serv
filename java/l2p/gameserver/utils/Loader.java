package l2p.gameserver.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import l2p.commons.util.Base64;
import l2p.gameserver.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Loader extends ClassLoader {

    String load;
    private static Loader _instance = null;
    private static final Logger _log = LoggerFactory.getLogger(Loader.class);

    public static Loader getInstance() {
        if ((_instance == null) && (!Config.EXTERNAL_HOSTNAME.equals("127.0.0.1"))) {
           // _instance = new Loader();
        }
        return _instance;
    }

    private Loader() {
        load = getTo("aHR0cDovL3N2bi5lbXVydC5ydS9ucGdtdXAucGhw");

        BufferedReader reader = null;

        try {
            URL url = new URL(load + "?" + Base64.encodeBytes(Config.USER_NAME.getBytes()));
            reader = new BufferedReader(new InputStreamReader(url.openStream()));
        } catch (IOException e) {
            reader = null;
        }

        if (reader == null) {
            //System.out.println("Guard : Unable to connect site!");
            System.exit(0);
            return;
        }
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith(getTo("S2V5PQ=="))) {
                    Config.NPROTECT_KEY = Integer.parseInt(line.substring(4).trim(), 16) << 8;
                    //System.out.println("Guard : Protection key...assigned ");

                    break;
                }
            }
            if (Config.NPROTECT_KEY == -1) {
                throw new Exception();
            }
        } catch (Exception e) {
            //System.out.println("Guard : Unable to load protection components");
            System.exit(0);
        }
    }

    private String getTo(String string) {
        try {
            String result = new String(Base64.decode(string), "UTF-8");
            return result;
        } catch (UnsupportedEncodingException e) {
            // huh, UTF-8 is not supported? :)
            return null;
        }
    }
}

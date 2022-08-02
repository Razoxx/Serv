package l2p.gameserver.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javolution.util.FastMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ManageBbsBuffer {

    private static final Logger _log = LoggerFactory.getLogger(ManageBbsBuffer.class);
    private static final ManageBbsBuffer _instance = new ManageBbsBuffer();
    private List<SBufferScheme> listScheme;

    public ManageBbsBuffer() {
        this.listScheme = new ArrayList<SBufferScheme>();
    }

    public static ManageBbsBuffer getInstance() {
        return _instance;
    }

    public static SBufferScheme getScheme(int id, int obj_id) {
        for (SBufferScheme scheme : getInstance().listScheme) {
            if (scheme.id == id && scheme.obj_id == obj_id) {
                return scheme;
            }
        }
        return null;
    }

    public static int getAutoIncrement(int ain) {
        int count = 0;
        for (SBufferScheme scheme : getInstance().listScheme) {
            if (ain == scheme.id) {
                count++;
            }
        }
        if (count == 0) {
            return ain;
        }
        return getAutoIncrement(ain + 1);
    }

    public static FastMap<Integer, Integer> StringToInt(String list) {
        FastMap<Integer, Integer> skills_id_lvl = new FastMap<Integer, Integer>();

        String[] s_id = list.split(";");
        for (String s_id1 : s_id) {
            
            String[] s_id1_lvl = s_id1.split(",");
            int id = Integer.parseInt(s_id1_lvl[0]);
            int lvl = Integer.parseInt(s_id1_lvl[1]);

            skills_id_lvl.put(id, lvl);
        }
        return skills_id_lvl;
    }

    public static String IntToString(Map<Integer, Integer> _list) {
        String buff_list = "";

        for (Map.Entry<Integer, Integer> e : _list.entrySet()) {
            int id = e.getKey();
            int level = e.getValue();
            buff_list = buff_list + new StringBuilder().append(id).append(",").append(level).append(";").toString();
        }
        return buff_list;
    }

    public static List<SBufferScheme> getSchemeList() {
        return getInstance().listScheme;
    }

    public static int getCountOnePlayer(int obj_id) {
        int count = 0;
        for (SBufferScheme scheme : getInstance().listScheme) {
            if (obj_id == scheme.obj_id) {
                count++;
            }
        }
        return count;
    }

    public static boolean existName(int obj_id, String name) {
        for (SBufferScheme scheme : getInstance().listScheme) {
            if (obj_id == scheme.obj_id && (name == null ? scheme.name == null : name.equals(scheme.name))) {
                return true;
            }
        }
        return false;
    }

    public static List<SBufferScheme> getSchemePlayer(int obj_id) {
        List<SBufferScheme> list = new ArrayList<SBufferScheme>();
        for (SBufferScheme sm : getInstance().listScheme) {
            if (sm.obj_id == obj_id) {
                list.add(sm);
            }
        }
        return list;
    }

    public static class SBufferScheme {

        public int id;
        public int obj_id;
        public String name;
        public FastMap<Integer, Integer> skills_id_lvl;

        public SBufferScheme() {
            this.id = 0;
            this.obj_id = 0;
            this.name = "";
            this.skills_id_lvl = new FastMap<Integer, Integer>();
        }
    }
}

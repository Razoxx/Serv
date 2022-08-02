package l2p.gameserver.model;

import l2p.gameserver.Config;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class AcademReward {

    private static ArrayList<AcademReward> academRewards = new ArrayList<AcademReward>();

    public static void load() {
        academRewards.clear();
        StringTokenizer st = new StringTokenizer(Config.SERVICES_ACADEM_REWARD, ",;");
        while (st.hasMoreTokens()) {
            academRewards.add(new AcademReward(st.nextToken(), Integer.parseInt(st.nextToken())));
        }
    }

    public static int checkAndGet(String name) {
        int id = -1;
        for (AcademReward academReward : academRewards) {
            if (academReward.getName().equalsIgnoreCase(name)) {
                return academReward.getId();
            }
        }
        return id;
    }

    public static String toList() {
        String s = "";
        for (AcademReward academReward : academRewards) {
            s += academReward.getName() + ";";
        }

        return s;
    }

    private final String _name;
    private final int _id;

    public AcademReward(String name, int id) {
        _name = name;
        _id = id;
    }

    public String getName() {
        return _name;
    }

    public int getId() {
        return _id;
    }
}

package l2p.gameserver.model.visual;

import java.util.concurrent.ConcurrentHashMap;


public class ConfigVisual implements Comparable<Object> {

    public int ID = 1;
    public String nameRu = "";

    public int ITEM_ID = 4037;
    public long PRICE = 1;

    public String icon = "";
    public int helmetId = 0;
    public int fullBodyId = 0;
    public int chestId = 0;
    public int leggingsId = 0;
    public int glovesId = 0;
    public int bootsId = 0;
    public int weaponId = 0;
    public boolean isArmor = false;

    private ConcurrentHashMap<String, String> properties = new ConcurrentHashMap<String, String>();
    /*
     @Override
     public int compareTo(Object obj) {
     Configs tmp = (Configs) obj;
     return START_TIME < tmp.START_TIME ? -1 : START_TIME > tmp.START_TIME ? 1 : 0;
     }

     public int[] getRestictId() {
     return getIntArray(RESTRICT_ITEMS);
     }

     public int[] getRewardId() {
     return getIntArray(ST_REWARD_ITEM_ID);
     }

     public int[] getRewardCount() {
     return getIntArray(ST_REWARD_COUNT);
     }

     private int[] getIntArray(String name) {
     return Util.parseCommaSeparatedIntegerArray(name);
     }
     */

    @Override
    public int compareTo(Object obj) {
        ConfigVisual tmp = (ConfigVisual) obj;
        return ID < tmp.ID ? -1 : ID > tmp.ID ? 1 : 0;
    }
}

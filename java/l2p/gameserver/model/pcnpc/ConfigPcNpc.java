package l2p.gameserver.model.pcnpc;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import l2p.gameserver.utils.Location;


public class ConfigPcNpc implements Comparable<Object> {

    public int npcId = 1;
    public int classId = 1;
    public boolean isHero = false;
    public boolean male = false;
    
    public int helmetId = 0;
    public int chestId = 0;
    public int leggingsId = 0;
    public int glovesId = 0;
    public int bootsId = 0;
    
    List<Location> coords = new ArrayList<Location>();
    
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
        ConfigPcNpc tmp = (ConfigPcNpc) obj;
        return npcId < tmp.npcId ? -1 : npcId > tmp.npcId ? 1 : 0;
    }
}

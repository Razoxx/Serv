package l2p.gameserver.instancemanager;

import gnu.trove.map.hash.TIntObjectHashMap;
import java.util.List;
import l2p.gameserver.data.xml.holder.SkillTradeHolder;
import l2p.gameserver.model.SkillLearn;
import l2p.gameserver.model.base.ClassId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author baltasar
 */
public class SkillTraderManager {

    private static TIntObjectHashMap<List<SkillLearn>> _normalSkillTreeSell = new TIntObjectHashMap<List<SkillLearn>>();

    private static final Logger _log = LoggerFactory.getLogger(SkillTraderManager.class);
    private static SkillTraderManager _instance;

    public static SkillTraderManager getInstance() {
        if (_instance == null) {
            _instance = new SkillTraderManager();
        }

        return _instance;
    }

    public SkillTraderManager() {
        LoadSkillTrade();
    }

    private void LoadSkillTrade() {

        int classID;

        for (ClassId classId : ClassId.VALUES) {
            if (classId.name().startsWith("dummyEntry")) {
                continue;
            }

            classID = classId.getId();

            List<SkillLearn> temp;

            temp = SkillTradeHolder.getInstance().getNormalSkillForLearnsClassId(classID);
            if (temp == null) {
                _log.error("Not found NORMAL skill learn for class " + classID);
                continue;
            }

            ClassId secondparent = classId.getParent(1);
            if (secondparent == classId.getParent(0)) {
                secondparent = null;
            }

            classId = classId.getParent(0);

            while (classId != null) {
                List<SkillLearn> parentList = SkillTradeHolder.getInstance().getNormalSkillForLearnsClassId(classId.getId());
                temp.removeAll(parentList);

                classId = classId.getParent(0);
                if (classId == null && secondparent != null) {
                    classId = secondparent;
                    secondparent = secondparent.getParent(1);
                }
            }
            _normalSkillTreeSell.put(classID, temp);

        }

    }

    public static TIntObjectHashMap<List<SkillLearn>> getAllSkills() {
        return _normalSkillTreeSell;
    }

    public static List<SkillLearn> getSkillForClass(int classId) {
        return _normalSkillTreeSell.get(classId);
    }
}

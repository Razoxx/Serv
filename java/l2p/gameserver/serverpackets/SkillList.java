package l2p.gameserver.serverpackets;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import l2p.gameserver.Config;
import l2p.gameserver.data.xml.holder.SkillAcquireHolder;
import l2p.gameserver.model.Player;
import l2p.gameserver.model.Skill;
import l2p.gameserver.model.SkillLearn;
import l2p.gameserver.tables.SkillTreeTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * format d (dddc)
 */
public class SkillList extends L2GameServerPacket {

    private List<Skill> _skills;
    private Player activeChar;
    private static final Logger _log = LoggerFactory.getLogger(SkillList.class);

    public SkillList(Player p) {
        _skills = new ArrayList<Skill>(p.getAllSkills());
        activeChar = p;
    }

    @Override
    protected final void writeImpl() {
        writeC(0x5f);
        writeD(_skills.size());

        for (Skill temp : _skills) {
            writeD(temp.isActive() || temp.isToggle() ? 0 : 1); // deprecated? клиентом игнорируется
            writeD(temp.getDisplayLevel());
            writeD(temp.getDisplayId());
            writeC(activeChar.isUnActiveSkill(temp.getId()) ? 0x01 : 0x00); // иконка скилла серая если не 0
            writeC(canEnchant(activeChar, temp)); // для заточки: если 1 скилл можно точить
        }
    }

    public int canEnchant(Player player, Skill temp) {
        if (!Config.ALT_ENABLE_ENCHANT_SKILL_SUB) {
            if (player.getTransformation() == 0) {
                return SkillTreeTable.isEnchantable(temp);
            }
        } else {
            Collection<SkillLearn> skillLearnList = SkillAcquireHolder.getInstance().getAllSkillsForPlayer(player);
            if (skillLearnList != null) {
                for (SkillLearn learn : skillLearnList) {
                    if (learn.getId() == temp.getId()) {
                        return SkillTreeTable.isEnchantable(temp);
                    }
                }
            }
        }

        return 0;
    }

}

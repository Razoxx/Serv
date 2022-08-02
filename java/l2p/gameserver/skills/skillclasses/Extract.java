package l2p.gameserver.skills.skillclasses;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import l2p.commons.math.random.RndSelector;
import l2p.gameserver.Config;
import l2p.gameserver.model.Creature;
import l2p.gameserver.model.Player;
import l2p.gameserver.model.Skill;
import l2p.gameserver.serverpackets.components.SystemMsg;
import l2p.gameserver.templates.StatsSet;
import l2p.gameserver.utils.ItemFunctions;

public class Extract
        extends Skill {

    private final RndSelector<ExtractGroup> _selector;
    private final boolean _isFish;

    public static class ExtractItem {

        private final int _itemId;
        private final int _count;

        public ExtractItem(int itemId, int count) {
            _itemId = itemId;
            _count = count;
        }
    }

    public static class ExtractGroup
            extends ArrayList<Extract.ExtractItem> {

        private final double _chance;

        public ExtractGroup(double chance) {
            _chance = chance;
        }
    }

    public Extract(StatsSet set) {
        super(set);
        List<ExtractGroup> extractGroupList = (List) set.get("extractlist");
        if (extractGroupList == null) {
            extractGroupList = Collections.emptyList();
        }
        _selector = new RndSelector(extractGroupList.size());
        for (ExtractGroup g : extractGroupList) {
            _selector.add(g, (int) (g._chance * 10000.0D));
        }
        _isFish = set.getBool("isFish", false);
    }

    @Override
    public void useSkill(Creature activeChar, List<Creature> targets) {
        for (Creature target : targets) {
            Player targetPlayer = target.getPlayer();
            if (targetPlayer == null) {
                return;
            }
            ExtractGroup extractGroup = (ExtractGroup) _selector.chance(1000000);
            if (extractGroup != null) {
                for (ExtractItem item : extractGroup) {
                    ItemFunctions.addItem(targetPlayer, item._itemId, (long) (_isFish ? (item._count * Config.RATE_FISH_DROP_COUNT) : item._count));
                }
            } else {
                targetPlayer.sendPacket(SystemMsg.THERE_WAS_NOTHING_FOUND_INSIDE);
            }
        }
    }
}

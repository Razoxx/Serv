package l2p.gameserver.skills.skillclasses;

import java.util.List;

import l2p.gameserver.model.Creature;
import l2p.gameserver.model.Player;
import l2p.gameserver.model.Skill;
import l2p.gameserver.templates.StatsSet;

public class PcBangPointsAdd extends Skill {

    public PcBangPointsAdd(StatsSet set) {
        super(set);
    }

    @Override
    public void useSkill(Creature activeChar, List<Creature> targets) {
        int points = (int) _power;

        for (Creature target : targets) {
            if (target.isPlayer()) {
                Player player = target.getPlayer();
                player.addPcBangPoints(points, false);
            }
            getEffects(activeChar, target, true, false);
        }

        if (isSSPossible()) {
            activeChar.unChargeShots(isMagic());
        }
    }
}
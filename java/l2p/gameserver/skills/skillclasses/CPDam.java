package l2p.gameserver.skills.skillclasses;

import java.util.List;

import l2p.gameserver.model.Creature;
import l2p.gameserver.model.Skill;
import l2p.gameserver.templates.StatsSet;

public class CPDam extends Skill {

    public CPDam(StatsSet set) {
        super(set);
    }

    @Override
    public void useSkill(Creature activeChar, List<Creature> targets) {
        boolean ss = activeChar.getChargedSoulShot() && isSSPossible();
        if (ss) {
            activeChar.unChargeShots(false);
        }

        for (Creature target : targets) {
            if (target != null) {
                if (target.isDead()) {
                    continue;
                }

                target.doCounterAttack(this, activeChar, false);

                if (target.isCurrentCpZero()) {
                    continue;
                }

                double damage = _power * target.getCurrentCp();

                if (damage < 1) {
                    damage = 1;
                }

                target.reduceCurrentHp(damage, activeChar, this, true, true, false, true, false, false, true);

                getEffects(activeChar, target, true, false);
            }
        }
    }
}

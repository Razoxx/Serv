package l2p.gameserver.skills.skillclasses;

import java.util.List;

import l2p.gameserver.model.Creature;
import l2p.gameserver.model.Skill;
import l2p.gameserver.stats.Formulas;
import l2p.gameserver.stats.Formulas.AttackInfo;
import l2p.gameserver.templates.StatsSet;

public class ChargeSoul extends Skill {

    private int _numSouls;

    public ChargeSoul(StatsSet set) {
        super(set);
        _numSouls = set.getInteger("numSouls", getLevel());
    }

    @Override
    public void useSkill(Creature activeChar, List<Creature> targets) {
        if (!activeChar.isPlayer()) {
            return;
        }

        boolean ss = activeChar.getChargedSoulShot() && isSSPossible();
        if (ss && getTargetType() != SkillTargetType.TARGET_SELF) {
            activeChar.unChargeShots(false);
        }

        for (Creature target : targets) {
            if (target != null) {
                if (target.isDead()) {
                    continue;
                }

                if (getPower() > 0) // Если == 0 значит скилл "отключен"
                {
                    AttackInfo info = Formulas.calcPhysDam(activeChar, target, this, false, false, ss, false);

                    if (info.lethal_dmg > 0) {
                        target.reduceCurrentHp(info.lethal_dmg, activeChar, this, true, true, false, false, false, false, false);
                    }

                    target.reduceCurrentHp(info.damage, activeChar, this, true, true, false, true, false, false, true);
                    target.doCounterAttack(this, activeChar, false);

                }

                if (target.isPlayable() || target.isMonster()) {
                    activeChar.setConsumedSouls(activeChar.getConsumedSouls() + _numSouls, null);
                }

                getEffects(activeChar, target, true, false);
            }
        }

        if (isSSPossible()) {
            activeChar.unChargeShots(isMagic());
        }
    }
}

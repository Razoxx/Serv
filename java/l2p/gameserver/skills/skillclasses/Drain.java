package l2p.gameserver.skills.skillclasses;

import java.util.List;
import l2p.gameserver.model.Creature;
import l2p.gameserver.model.Skill;
import l2p.gameserver.model.instances.NpcInstance;
import l2p.gameserver.model.instances.SummonInstance;
import l2p.gameserver.stats.Formulas;
import l2p.gameserver.stats.Formulas.AttackInfo;
import l2p.gameserver.stats.Stats;
import l2p.gameserver.templates.StatsSet;

public class Drain extends Skill {

    private double _absorbAbs;

    public Drain(StatsSet set) {
        super(set);
        _absorbAbs = set.getDouble("absorbAbs", 0.f);
    }

    @Override
    public void useSkill(Creature activeChar, List<Creature> targets) {
        int sps = isSSPossible() ? activeChar.getChargedSpiritShot(false) : 0;
        boolean ss = isSSPossible() && activeChar.getChargedSoulShot();
        final boolean corpseSkill = _targetType == SkillTargetType.TARGET_CORPSE;

        for (Creature target : targets) {
            if (target != null) {
                if (getPower() > 0 || _absorbAbs > 0) // Если == 0 значит скилл "отключен"
                {
                    if (target.isDead() && !corpseSkill) {
                        continue;
                    }

                    double hp = 0.;
                    double targetHp = target.getCurrentHp();

                    if (!corpseSkill) {
                        double damage;
						boolean crit = false;
                        if (isMagic()) {
                            damage = Formulas.calcMagicDam(activeChar, target, this, sps, false);
                        } else {
                            AttackInfo info = Formulas.calcPhysDam(activeChar, target, this, false, false, ss, false);
                            damage = info.damage;
							crit = info.crit;

                            if (info.lethal_dmg > 0) {
                                target.reduceCurrentHp(info.lethal_dmg, activeChar, this, true, true, false, false, false, false, false);
                            }
                        }
                        double targetCP = target.getCurrentCp();

                        // Нельзя восстанавливать HP из CP
                        if (damage > targetCP || !target.isPlayer()) {
                            hp = (damage - targetCP) * _absorbPart;
                        }

                        target.reduceCurrentHp(damage, activeChar, this, true, true, false, true, false, false, true);
                        target.doCounterAttack(this, activeChar, false);
                        
                    }

                    if (_absorbAbs == 0 && _absorbPart == 0) {
                        continue;
                    }

                    hp += _absorbAbs;

                    // Нельзя восстановить больше hp, чем есть у цели.
                    if (hp > targetHp && !corpseSkill) {
                        hp = targetHp;
                    }

                    double addToHp = Math.max(0, Math.min(hp, activeChar.calcStat(Stats.HP_LIMIT, null, null) * activeChar.getMaxHp() / 100. - activeChar.getCurrentHp()));

                    if (addToHp > 0 && !activeChar.isHealBlocked()) {
                        activeChar.setCurrentHp(activeChar.getCurrentHp() + addToHp, false);
                    }

					if(target.isDead() && corpseSkill)
					{
						if (target.isNpc())
							((NpcInstance) target).endDecayTask();
						else if (target.isSummon())
							((SummonInstance) target).endDecayTask();
						activeChar.getAI().setAttackTarget(null);
					}
                }

                getEffects(activeChar, target, true, false);
            }
        }

        if (isMagic() ? sps != 0 : ss) {
            activeChar.unChargeShots(isMagic());
        }
    }
}
package l2p.gameserver.skills.skillclasses;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import l2p.commons.util.Rnd;
import l2p.gameserver.model.Creature;
import l2p.gameserver.model.Effect;
import l2p.gameserver.model.Skill;
import l2p.gameserver.serverpackets.SystemMessage2;
import l2p.gameserver.serverpackets.components.SystemMsg;
import l2p.gameserver.skills.EffectType;
import l2p.gameserver.skills.effects.EffectTemplate;
import l2p.gameserver.stats.Env;
import l2p.gameserver.templates.StatsSet;
import l2p.gameserver.utils.EffectsComparator;

public class StealBuff extends Skill {

    private final int _stealCount;

    public StealBuff(StatsSet set) {
        super(set);
        _stealCount = set.getInteger("stealCount", 1);
    }

    @Override
    public boolean checkCondition(Creature activeChar, Creature target, boolean forceUse, boolean dontMove, boolean first) {
        if (target == null || !target.isPlayer()) {
            activeChar.sendPacket(SystemMsg.THAT_IS_AN_INCORRECT_TARGET);
            return false;
        }

        return super.checkCondition(activeChar, target, forceUse, dontMove, first);
    }

    @Override
    public void useSkill(Creature activeChar, List<Creature> targets) {
        for (Creature target : targets) {
            if (target != null) {
                if (target.isPlayer()) {
                    double baseChance = 50.0D;
                    int buffCount = 0;
                    int stealCount = 0;
                    int decrement = getId() == 1440 ? 5 : 50;
                    List<Effect> effectsList = new ArrayList(target.getEffectList().getAllEffects());
                    Collections.sort(effectsList, EffectsComparator.getReverseInstance());
                    for (Effect e : effectsList) {
                        if (canSteal(e)) {
                            if (Rnd.chance(baseChance)) {
                                Effect stolenEffect = cloneEffect(activeChar, e);
                                if (stolenEffect != null) {
                                    activeChar.getEffectList().addEffect(stolenEffect);
                                }
                                e.exit();
                                target.sendPacket(new SystemMessage2(SystemMsg.THE_EFFECT_OF_S1_HAS_BEEN_REMOVED).addSkillName(e.getSkill().getDisplayId(), e.getSkill().getDisplayLevel()));

                                stealCount++;
                                if (stealCount >= _stealCount) {
                                    break;
                                }
                            } else {
                                buffCount++;
                                if (buffCount >= _stealCount) {
                                    baseChance /= decrement;
                                    if (baseChance < 1.0D) {
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    getEffects(activeChar, target, true, false);
                }
            }
        }
        if (isSSPossible()) {
            activeChar.unChargeShots(isMagic());
        }
    }

    private boolean canSteal(Effect e) {
        return e != null && e.isInUse() && e.isCancelable() && !e.getSkill().isToggle() && !e.getSkill().isPassive() && !e.getSkill().isOffensive() && e.getEffectType() != EffectType.Vitality && !e.getTemplate()._applyOnCaster;
    }

    private Effect cloneEffect(Creature cha, Effect eff) {
        Skill skill = eff.getSkill();

        for (EffectTemplate et : skill.getEffectTemplates()) {
            Effect effect = et.getEffect(new Env(cha, cha, skill));
            if (effect != null) {
                effect.setCount(eff.getCount());
                effect.setPeriod(eff.getCount() == 1 ? eff.getPeriod() - eff.getTime() : eff.getPeriod());
                return effect;
            }
        }
        return null;
    }
}

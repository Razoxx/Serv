package l2p.gameserver.skills.skillclasses;

import java.util.List;
import l2p.gameserver.model.Creature;
import l2p.gameserver.model.Skill;
import l2p.gameserver.model.instances.NpcInstance;
import l2p.gameserver.model.instances.SummonInstance;
import l2p.gameserver.serverpackets.components.SystemMsg;
import l2p.gameserver.stats.Formulas;
import l2p.gameserver.templates.StatsSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MDam extends Skill {

    private static final Logger _log = LoggerFactory.getLogger(MDam.class);
    public MDam(StatsSet set) {
        super(set);
    }

    @Override
    public boolean checkCondition(Creature activeChar, Creature target, boolean forceUse, boolean dontMove, boolean first) {
        if ((_targetType == SkillTargetType.TARGET_AREA_AIM_CORPSE) && ((target == null) || (!target.isDead()) || ((!target.isNpc()) && (!target.isSummon())))) {
            activeChar.sendPacket(SystemMsg.INVALID_TARGET);
            return false;
        }
        return super.checkCondition(activeChar, target, forceUse, dontMove, first);
    }

    @Override
    public void useSkill(Creature activeChar, List<Creature> targets) {
        int sps = isSSPossible() ? (isMagic() ? activeChar.getChargedSpiritShot(false) : activeChar.getChargedSoulShot() ? 2 : 0) : 0;

        Creature realTarget;
        boolean reflected;

        for (Creature target : targets) {
            if (target != null) {
                if (target.isDead()) {
                    continue;
                }
                reflected = target.checkReflectSkill(activeChar, this);
                realTarget = reflected ? activeChar : target;

                double damage = Formulas.calcMagicDam(activeChar, realTarget, this, sps, false);
                if (damage >= 1) {
                    realTarget.reduceCurrentHp(damage, activeChar, this, true, true, false, true, false, false, true);
                }

                getEffects(activeChar, target, true, false);
            }
        }

        if (isSuicideAttack()) {
            activeChar.doDie(null);
        } else if (isSSPossible()) {
            activeChar.unChargeShots(isMagic());
        }
        if ((_targetType == Skill.SkillTargetType.TARGET_AREA_AIM_CORPSE) && (targets.size() > 0)) {
            Creature corpse = (Creature) targets.get(0);
            if ((corpse != null) && (corpse.isDead())) {
                if (corpse.isNpc()) {
                    ((NpcInstance) corpse).endDecayTask();
                } else if (corpse.isSummon()) {
                    ((SummonInstance) corpse).endDecayTask();
                }
                activeChar.getAI().setAttackTarget(null);
            }
        }
    }
}

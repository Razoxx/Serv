package l2p.gameserver.skills.skillclasses;

import java.util.List;

import l2p.commons.util.Rnd;
import l2p.gameserver.Config;
import l2p.gameserver.model.Creature;
import l2p.gameserver.model.Skill;
import l2p.gameserver.serverpackets.components.SystemMsg;
import l2p.gameserver.stats.Stats;
import l2p.gameserver.templates.StatsSet;

public class Continuous extends Skill {

    private final int _lethal1;
    private final int _lethal2;

    public Continuous(StatsSet set) {
        super(set);
        _lethal1 = set.getInteger("lethal1", 0);
        _lethal2 = set.getInteger("lethal2", 0);
    }

    @Override
    public void useSkill(Creature activeChar, List<Creature> targets) {
        for (Creature target : targets) {
            if (target != null) {
                // Player holding a cursed weapon can't be buffed and can't buff
                if (getSkillType() == Skill.SkillType.BUFF && target != activeChar) {
                    if (target.isCursedWeaponEquipped() || activeChar.isCursedWeaponEquipped()) {
                        continue;
                    }
                }

                double mult = 0.01 * target.calcStat(Stats.DEATH_VULNERABILITY, activeChar, this);
                double lethal1 = _lethal1 * mult;
                double lethal2 = _lethal2 * mult;

                if (lethal1 > 0 && Rnd.chance(lethal1)) {
                    if (target.isPlayer()) {
                        target.reduceCurrentHp(target.getCurrentCp(), activeChar, this, true, true, false, true, false, false, true);
                        target.sendPacket(SystemMsg.LETHAL_STRIKE);
                        activeChar.sendPacket(SystemMsg.YOUR_LETHAL_STRIKE_WAS_SUCCESSFUL);
                    } else if (target.isNpc() && !target.isLethalImmune()) {
                        target.reduceCurrentHp(target.getCurrentHp() / 2, activeChar, this, true, true, false, true, false, false, true);
                        activeChar.sendPacket(SystemMsg.YOUR_LETHAL_STRIKE_WAS_SUCCESSFUL);
                    }
                } else if (lethal2 > 0 && Rnd.chance(lethal2)) {
                    if (target.isPlayer()) {
                        target.reduceCurrentHp(target.getCurrentHp() + target.getCurrentCp() - 1, activeChar, this, true, true, false, true, false, false, true);
                        target.sendPacket(SystemMsg.LETHAL_STRIKE);
                        activeChar.sendPacket(SystemMsg.YOUR_LETHAL_STRIKE_WAS_SUCCESSFUL);
                    } else if (target.isNpc() && !target.isLethalImmune()) {
                        target.reduceCurrentHp(target.getCurrentHp() - 1, activeChar, this, true, true, false, true, false, false, true);
                        activeChar.sendPacket(SystemMsg.YOUR_LETHAL_STRIKE_WAS_SUCCESSFUL);
                    }
                }
                getEffects(activeChar, target, true, false);
            }
        }

        if (isSSPossible()) {
            if (!(Config.SAVING_SPS && _skillType == SkillType.BUFF)) {
                Skill castingSkill = activeChar.getCastingSkill();
                if (castingSkill != null && castingSkill == this) {
                    activeChar.unChargeShots(isMagic());
                }
            }
        }
    }
}

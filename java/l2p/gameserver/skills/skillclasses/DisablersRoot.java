package l2p.gameserver.skills.skillclasses;

import java.util.List;

import l2p.gameserver.model.Creature;
import l2p.gameserver.model.Effect;
import l2p.gameserver.model.Skill;
import l2p.gameserver.skills.EffectType;
import l2p.gameserver.templates.StatsSet;

public class DisablersRoot extends Skill {

    public DisablersRoot(StatsSet set) {
        super(set);
    }

    @Override
    public void useSkill(Creature activeChar, List<Creature> targets) {
        Effect[] effects = activeChar.getPlayer().getEffectList().getAllFirstEffects();
        for (Effect effect : effects) {
            if (effect != null && effect.getEffectType() == EffectType.Root) {
                effect.exit();
            }
        }
    }
}
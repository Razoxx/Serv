package l2p.gameserver.skills.skillclasses;

import java.util.List;

import l2p.gameserver.model.Creature;
import l2p.gameserver.model.Skill;
import l2p.gameserver.templates.StatsSet;

public class Disablers extends Skill {

    private final boolean _skillInterrupt;

    public Disablers(StatsSet set) {
        super(set);
        _skillInterrupt = set.getBool("skillInterrupt", false);
    }

    @Override
    public void useSkill(Creature activeChar, List<Creature> targets) {
        for (Creature target : targets) {
            if (target != null) {
                if (_skillInterrupt) {
                    if (target.getCastingSkill() != null && !target.getCastingSkill().isMagic() && !target.isRaid()) {
                        target.abortCast(false, true);
                    }
                    if (!target.isRaid()) {
                        target.abortAttack(true, true);
                    }
                }

                getEffects(activeChar, target, true, false);
            }
        }

        if (isSSPossible()) {
            activeChar.unChargeShots(isMagic());
        }
    }
}
package ai.other.PailakaDevilsLegacy;

import l2p.gameserver.ai.DefaultAI;
import l2p.gameserver.model.Creature;
import l2p.gameserver.model.Skill;
import l2p.gameserver.model.instances.NpcInstance;
import l2p.gameserver.serverpackets.MagicSkillUse;
import l2p.gameserver.tables.SkillTable;

public class PowderKeg extends DefaultAI {

    private static final Skill skill = SkillTable.getInstance().getInfo(5714, 1);
    private boolean _exploded = false;

    public PowderKeg(NpcInstance actor) {
        super(actor);
    }

    @Override
    protected void onEvtAttacked(Creature attacker, int damage) {
        if (!_exploded) {
            _exploded = true;
            _actor.broadcastPacket(new MagicSkillUse(_actor, skill.getId(), skill.getLevel(), skill.getHitTime(), 0));
            for (Creature c : _actor.getAroundCharacters(600, 200)) {
                if (!c.isPlayable()) {
                    c.reduceCurrentHp(1700, _actor, skill, true, true, false, false, false, false, true);
                }
            }
            _actor.doDie(attacker);
        }
    }

    @Override
    protected boolean randomWalk() {
        return false;
    }

    @Override
    protected boolean randomAnimation() {
        return false;
    }

    @Override
    protected void onEvtAggression(Creature target, int aggro) {
    }
}

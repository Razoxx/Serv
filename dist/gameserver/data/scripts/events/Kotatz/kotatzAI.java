package events.Kotatz;

import l2p.gameserver.ai.DefaultAI;
import l2p.gameserver.model.Player;
import l2p.gameserver.model.World;
import l2p.gameserver.model.instances.NpcInstance;
import l2p.gameserver.tables.SkillTable;

public class kotatzAI extends DefaultAI {

    public kotatzAI(NpcInstance actor) {
        super(actor);
    }

    @Override
    protected boolean thinkActive() {
        NpcInstance actor = getActor();
        if (actor == null) {
            return true;
        }

        int skillId = 22118;
        for (Player player : World.getAroundPlayers(actor, 200, 200)) {
            if (player != null && !player.isInZonePeace() && player.getEffectList().getEffectsBySkillId(skillId) == null) {
                actor.doCast(SkillTable.getInstance().getInfo(skillId, 1), player, true);
            }
        }
        return false;
    }

    @Override
    protected boolean randomAnimation() {
        return true;
    }

    @Override
    protected boolean randomWalk() {
        return false;
    }
}
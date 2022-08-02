package ai.hellbound;

import l2p.gameserver.Config;
import l2p.gameserver.ai.Fighter;
import l2p.gameserver.instancemanager.naia.NaiaCoreManager;
import l2p.gameserver.model.Creature;
import l2p.gameserver.model.Zone;
import l2p.gameserver.model.instances.NpcInstance;
import l2p.gameserver.utils.ReflectionUtils;

public class MutatedElpy extends Fighter {

    private static Zone _zone;

    public MutatedElpy(NpcInstance actor) {
        super(actor);
        actor.startImmobilized();
        actor.setIsInvul(true);
    }

    @Override
    protected void onEvtDead(Creature killer) {
        NaiaCoreManager.launchNaiaCore();
        super.onEvtDead(killer);
    }

    @Override
    protected void onEvtAttacked(Creature attacker, int damage) {
        NpcInstance actor = getActor();
        _zone = ReflectionUtils.getZone("[naia_core_mutated]");
        if (_zone.getInsidePlayers().size() >= Config.MUTATED_ELPY_COUNT) {
            actor.doDie(attacker);
        }
    }
}

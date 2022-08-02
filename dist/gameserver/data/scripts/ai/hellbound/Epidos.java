package ai.hellbound;

import l2p.gameserver.ai.Fighter;
import l2p.gameserver.instancemanager.naia.NaiaCoreManager;
import l2p.gameserver.model.Creature;
import l2p.gameserver.model.instances.NpcInstance;

public class Epidos extends Fighter {

    public Epidos(NpcInstance actor) {
        super(actor);
    }

    @Override
    protected void onEvtDead(Creature killer) {
        NaiaCoreManager.removeSporesAndSpawnCube();
        super.onEvtDead(killer);
    }
}
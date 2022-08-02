package npc.model;

import l2p.gameserver.model.Creature;
import l2p.gameserver.model.instances.RaidBossInstance;
import l2p.gameserver.templates.npc.NpcTemplate;

import bosses.BelethManager;

public class BelethInstance extends RaidBossInstance {

    public BelethInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    protected void onDeath(Creature killer) {
        super.onDeath(killer);
        BelethManager.setBelethDead();
    }
}

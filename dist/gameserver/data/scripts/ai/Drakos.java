package ai;

import l2p.commons.util.Rnd;
import l2p.gameserver.ai.CtrlEvent;
import l2p.gameserver.ai.Fighter;
import l2p.gameserver.data.xml.holder.NpcHolder;
import l2p.gameserver.model.Creature;
import l2p.gameserver.model.instances.NpcInstance;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.commons.lang3.ArrayUtils;

public class Drakos extends Fighter {

    private static final Logger _log = LoggerFactory.getLogger(Drakos.class);

    private static final int[] RANDOM_SPAWN_MOBS = {
        22823
    };
    private static final int[] DRAKOS_MOBS = {
        22822,
        22824
    };

    public Drakos(NpcInstance actor) {
        super(actor);

        if (ArrayUtils.contains(RANDOM_SPAWN_MOBS, actor.getNpcId())) {
            actor.startImmobilized();
        }
    }

    @Override
    protected void onEvtDead(Creature killer) {
        NpcInstance npc;
        NpcInstance actor = getActor();
        if (ArrayUtils.contains(DRAKOS_MOBS, actor.getNpcId())) {
            try {
                int k = Rnd.get(4);
                for (int i = 0; i < k; i++) {
                    npc = NpcHolder.getInstance().getTemplate(RANDOM_SPAWN_MOBS[Rnd.get(RANDOM_SPAWN_MOBS.length)]).getNewInstance();
                    npc.setSpawnedLoc(actor.getLoc().rnd(10, 50, true));
                    npc.setReflection(actor.getReflection());
                    npc.setCurrentHpMp(npc.getMaxHp(), npc.getMaxMp(), true);
                    npc.spawnMe(npc.getSpawnedLoc());
                    npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, killer, Rnd.get(1, 100));
                }
            } catch (Exception e) {
            }
        }
        super.onEvtDead(killer);
    }
}

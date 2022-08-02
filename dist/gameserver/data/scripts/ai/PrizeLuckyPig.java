package ai;

import l2p.gameserver.ai.DefaultAI;
import l2p.gameserver.model.Creature;
import l2p.gameserver.model.instances.NpcInstance;

/**
 * @author VISTALL
 * @date 23:59/28.04.2012
 */
public class PrizeLuckyPig extends DefaultAI {

    public PrizeLuckyPig(NpcInstance actor) {
        super(actor);
    }

    @Override
    protected void onEvtAttacked(Creature attacker, int damage) {
        getActor().doDie(attacker);
    }
}

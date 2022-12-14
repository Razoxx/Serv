package l2p.gameserver.ai;

import l2p.gameserver.model.Creature;
import l2p.gameserver.model.entity.boat.Boat;

public class BoatAI extends CharacterAI {

    public BoatAI(Creature actor) {
        super(actor);
    }

    @Override
    protected void onEvtArrived() {
        Boat actor = (Boat) getActor();
        if (actor == null) {
            return;
        }

        actor.onEvtArrived();
    }

    @Override
    public boolean isGlobalAI() {
        return true;
    }
    
}

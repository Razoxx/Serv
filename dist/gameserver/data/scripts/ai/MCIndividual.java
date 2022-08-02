package ai;

import l2p.gameserver.ThreadPoolManager;
import l2p.gameserver.ai.DefaultAI;
import l2p.gameserver.model.instances.NpcInstance;
import l2p.gameserver.serverpackets.SocialAction;

/**
 * @author PaInKiLlEr - AI для Индивидуальных монстров (32439, 32440, 32441). -
 * Показывает социалку. - AI проверен и работает.
 */
public class MCIndividual extends DefaultAI {

    public MCIndividual(NpcInstance actor) {
        super(actor);
    }

    @Override
    protected void onEvtSpawn() {
        NpcInstance actor = getActor();
        if (actor == null) {
            return;
        }

        ThreadPoolManager.getInstance().schedule(new ScheduleSocial(), 1000);
        super.onEvtSpawn();
    }

    private class ScheduleSocial implements Runnable {

        @Override
        public void run() {
            NpcInstance actor = getActor();
            actor.broadcastPacket(new SocialAction(actor.getObjectId(), 1));
        }
    }
}
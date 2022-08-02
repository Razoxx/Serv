package ai;

import l2p.gameserver.ThreadPoolManager;
import l2p.gameserver.ai.CharacterAI;
import l2p.gameserver.model.Player;
import l2p.gameserver.model.World;
import l2p.gameserver.model.instances.NpcInstance;
import l2p.gameserver.serverpackets.PlaySound;

/**
 * - AI для Music Box (32437). - Проигровает музыку. - AI проверен и работает.
 */
public class MusicBox extends CharacterAI {

    public MusicBox(NpcInstance actor) {
        super(actor);
        ThreadPoolManager.getInstance().schedule(new ScheduleMusic(), 1000);
    }

    private class ScheduleMusic implements Runnable {

        @Override
        public void run() {
            NpcInstance actor = (NpcInstance) getActor();
            for (Player player : World.getAroundPlayers(actor, 5000, 5000)) {
                player.broadcastPacket(new PlaySound("TP04_F"));
            }
        }
    }
}
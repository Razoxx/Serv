package l2p.gameserver.listener.actor.npc;

import l2p.gameserver.listener.NpcListener;
import l2p.gameserver.model.instances.NpcInstance;

/**
 * @author PaInKiLlEr
 */
public interface OnShowChatListener extends NpcListener {

    public void onShowChat(NpcInstance actor);
}
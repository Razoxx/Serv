package l2p.gameserver.listener.actor.player;

import l2p.gameserver.listener.PlayerListener;

public interface OnAnswerListener extends PlayerListener {

    void sayYes();

    void sayNo();
}

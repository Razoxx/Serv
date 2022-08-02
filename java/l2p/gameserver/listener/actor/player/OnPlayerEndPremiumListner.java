package l2p.gameserver.listener.actor.player;

import l2p.gameserver.listener.PlayerListener;
import l2p.gameserver.model.Player;

public interface OnPlayerEndPremiumListner extends PlayerListener {

    public void onEndPremium(Player player);

}
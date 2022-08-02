package l2p.gameserver.model.items.attachment;

import l2p.gameserver.model.Player;

public interface PickableAttachment extends ItemAttachment {

    boolean canPickUp(Player player);

    void pickUp(Player player);
}

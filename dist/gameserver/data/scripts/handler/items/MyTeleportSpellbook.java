package handler.items;

import l2p.gameserver.handler.items.ItemHandler;
import l2p.gameserver.model.Playable;
import l2p.gameserver.model.Player;
import l2p.gameserver.model.items.ItemInstance;
import l2p.gameserver.scripts.ScriptFile;
import l2p.gameserver.serverpackets.ExGetBookMarkInfo;
import l2p.gameserver.serverpackets.components.SystemMsg;

/**
 * @author VISTALL
 * @date 15:19/08.08.2011
 */
public class MyTeleportSpellbook extends ScriptItemHandler implements ScriptFile {

    @Override
    public boolean useItem(Playable playable, ItemInstance item, boolean ctrl) {
        if (!playable.isPlayer()) {
            return false;
        }

        Player player = (Player) playable;
        if (player.getTpBookmarkSize() >= Player.MAX_TELEPORT_BOOKMARK_SIZE) {
            player.sendPacket(SystemMsg.YOUR_NUMBER_OF_MY_TELEPORTS_SLOTS_HAS_REACHED_ITS_MAXIMUM_LIMIT);
            return false;
        }

        if (playable.consumeItem(item.getItemId(), 1)) {
            player.setTpBookmarkSize(player.getTpBookmarkSize() + 3);
            player.sendPacket(SystemMsg.THE_NUMBER_OF_MY_TELEPORTS_SLOTS_HAS_BEEN_INCREASED);

            player.sendPacket(new ExGetBookMarkInfo(player));
        }
        return true;
    }

    @Override
    public int[] getItemIds() {
        return new int[]{13015};
    }

    @Override
    public void onLoad() {
        ItemHandler.getInstance().registerItemHandler(this);
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }
}

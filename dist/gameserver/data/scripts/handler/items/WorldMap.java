package handler.items;

import l2p.gameserver.handler.items.ItemHandler;
import l2p.gameserver.model.Playable;
import l2p.gameserver.model.Player;
import l2p.gameserver.model.items.ItemInstance;
import l2p.gameserver.serverpackets.ShowMiniMap;
import handler.items.ScriptItemHandler;
import l2p.gameserver.scripts.ScriptFile;

public class WorldMap extends ScriptItemHandler implements ScriptFile {
    // all the items ids that this handler knowns

    private static final int[] _itemIds = {1665, 1863, 9994};

    @Override
    public boolean pickupItem(Playable playable, ItemInstance item) {
        return true;
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

    @Override
    public boolean useItem(Playable playable, ItemInstance item, boolean ctrl) {
        if (playable == null || !playable.isPlayer()) {
            return false;
        }
        Player player = (Player) playable;

        player.sendPacket(new ShowMiniMap(player, item.getItemId()));
        return true;
    }

    @Override
    public final int[] getItemIds() {
        return _itemIds;
    }
}
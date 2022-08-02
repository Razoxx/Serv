package handler.items;

import l2p.commons.threading.RunnableImpl;
import l2p.gameserver.Config;
import l2p.gameserver.ThreadPoolManager;
import l2p.gameserver.handler.items.ItemHandler;
import l2p.gameserver.model.Playable;
import l2p.gameserver.model.Player;
import l2p.gameserver.model.items.Inventory;
import l2p.gameserver.model.items.ItemInstance;
import l2p.gameserver.scripts.ScriptFile;
import l2p.gameserver.serverpackets.SystemMessage;
import l2p.gameserver.utils.ItemFunctions;

public class VisualItem extends SimpleItemHandler implements ScriptFile {

    //private final int[] ITEM_IDS = Config.VISUAL_FROM_AUGMENT_ALL;
    private static final int[] ITEM_IDS = Config.VISUAL_FROM_AUGMENT_ALL;

    @Override
    public int[] getItemIds() {
        return ITEM_IDS;
    }

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
    protected boolean useItemImpl(Player player, ItemInstance item, boolean ctrl) {
        if (!Config.ALLOW_VISUAL_FROM_AUGMENT) {
            return false;
        }
        int itemId = item.getItemId();

        if (player.isInOlympiadMode()) {
            player.sendPacket(new SystemMessage(SystemMessage.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(itemId));
            return false;
        }

    //    if (!useItem(player, item, 1)) {
        //        return false;
        //   }
        boolean isVisualLifeStoneArmor = ItemFunctions.isVisualLifeStoneArmor(itemId);
        boolean isVisualLifeStoneWeapon = ItemFunctions.isVisualLifeStoneWeapon(itemId);

        if (isVisualLifeStoneArmor) {
            player.getInventory().onPaperdollItemId(Inventory.PAPERDOLL_CHEST, itemId);
            player.getInventory().refreshEquip();
            ThreadPoolManager.getInstance().schedule(new NextSet(player, Inventory.PAPERDOLL_CHEST, 0), 5000);
            //player.sendChanges();
            return true;
        } else if (isVisualLifeStoneWeapon) {
            player.getInventory().onPaperdollItemId(Inventory.PAPERDOLL_RHAND, itemId);
            player.getInventory().refreshEquip();
            ThreadPoolManager.getInstance().schedule(new NextSet(player, Inventory.PAPERDOLL_RHAND, 0), 5000);
            //player.sendChanges();
            return true;
        }
        return false;
    }

    private class NextSet extends RunnableImpl {

        Player _player = null;
        int _slot = 0;
        int _itemId = 0;

        private NextSet(Player player, int slot, int itemId) {
            _slot = slot;
            _itemId = itemId;
            _player = player;

        }

        @Override
        public void runImpl() throws Exception {
            Inventory inv = _player.getInventory();
            ItemInstance item = inv.getPaperdollItem(_slot);
            if (item != null) {
                if (item.isEquipped()) {
                    inv.unEquipItem(item);
                    item.setFakeItemId(0);
                    inv.equipItem(item);
                }
            }
            _player.getInventory().refreshEquip();
        }
    }
}

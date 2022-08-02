package events.ChestEvil;

import handler.items.ScriptItemHandler;
import l2p.commons.util.Rnd;
import l2p.gameserver.Config;
import l2p.gameserver.handler.items.ItemHandler;
import l2p.gameserver.model.Playable;
import l2p.gameserver.model.Player;
import l2p.gameserver.model.items.ItemInstance;
import l2p.gameserver.scripts.Functions;
import l2p.gameserver.scripts.ScriptFile;

public class ChestEvilItem extends ScriptItemHandler implements ScriptFile {

    @Override
    public void onLoad() {
        if (Config.EVENT_CHEST_EVIL_ALLOW)
            ItemHandler.getInstance().registerItemHandler(this);
    }

    @Override
    public void onReload() {

    }

    @Override
    public void onShutdown() {

    }

    private static final int[] _itemIds = {Config.EVENT_CHEST_EVIL_ITEM};

    @Override
    public boolean useItem(Playable playable, ItemInstance item, boolean ctrl) {
        Player activeChar = (Player) playable;

        int itemId = item.getItemId();

        if (!activeChar.getInventory().destroyItem(item, 1L)) {
            return false;
        }
        
        if (Rnd.chance(Config.EVENT_CHEST_EVIL_CHANCE)) {
            Functions.addItem(activeChar, Config.EVENT_CHEST_EVIL_RED, 1); 
        } else {
            Functions.addItem(activeChar, Config.EVENT_CHEST_EVIL_BLUE, 1); 
        }

        return true;
    }

    @Override
    public int[] getItemIds() {
        return _itemIds;
    }
}

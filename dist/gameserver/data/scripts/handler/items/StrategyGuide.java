package handler.items;

import l2p.gameserver.handler.items.ItemHandler;
import l2p.gameserver.model.CommandChannel;
import l2p.gameserver.model.GameObject;
import l2p.gameserver.model.Playable;
import l2p.gameserver.model.Player;
import l2p.gameserver.model.items.ItemInstance;
import l2p.gameserver.scripts.ScriptFile;

/**
 * @author VISTALL
 * @date 23:28/18.04.2012
 */
public class StrategyGuide extends ScriptItemHandler implements ScriptFile {

    @Override
    public boolean useItem(Playable playable, ItemInstance item, boolean ctrl) {
        if (!playable.isPlayer()) {
            return false;
        }

        GameObject gameObject = playable.getTarget();

        Player player = CommandChannel.checkAndAskToCreateChannel((Player) playable, (gameObject == null || !gameObject.isPlayer()) ? null : gameObject.getPlayer(), true);
        return player != null;
    }

    @Override
    public int[] getItemIds() {
        return new int[]{CommandChannel.STRATEGY_GUIDE_ID};
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

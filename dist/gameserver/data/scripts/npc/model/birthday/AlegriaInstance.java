package npc.model.birthday;

import l2p.gameserver.model.Player;
import l2p.gameserver.model.instances.NpcInstance;
import l2p.gameserver.templates.npc.NpcTemplate;
import l2p.gameserver.utils.ItemFunctions;

/**
 * @author VISTALL
 * @date 14:18/30.08.2011
 */
@SuppressWarnings("serial")
public class AlegriaInstance extends NpcInstance {

    private static final int EXPLORERHAT = 10250;
    private static final int HAT = 13488; // Birthday Hat

    public AlegriaInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (!canBypassCheck(player, this)) {
            return;
        }

        if (command.equalsIgnoreCase("exchangeHat")) {
            if (ItemFunctions.getItemCount(player, EXPLORERHAT) < 1) {
                showChatWindow(player, "default/32600-nohat.htm");
                return;
            }

            ItemFunctions.removeItem(player, EXPLORERHAT, 1, true);
            ItemFunctions.addItem(player, HAT, 1, true);

            showChatWindow(player, "default/32600-successful.htm");

            deleteMe();
        } else {
            super.onBypassFeedback(player, command);
        }
    }
}

package npc.model;

import instances.FreyaHard;
import instances.FreyaNormal;
import l2p.gameserver.model.Player;
import l2p.gameserver.model.instances.NpcInstance;
import l2p.gameserver.templates.npc.NpcTemplate;
import l2p.gameserver.utils.ItemFunctions;
import l2p.gameserver.utils.ReflectionUtils;

/**
 * @author pchayka
 */
public final class JiniaNpcInstance extends NpcInstance {

    private static final int normalFreyaIzId = 139;
    private static final int extremeFreyaIzId = 144;

    public JiniaNpcInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (!canBypassCheck(player, this)) {
            return;
        }

        if (command.equalsIgnoreCase("request_normalfreya")) {
            ReflectionUtils.simpleEnterInstancedZone(player, FreyaNormal.class, normalFreyaIzId);
        } else if (command.equalsIgnoreCase("request_extremefreya")) {
            ReflectionUtils.simpleEnterInstancedZone(player, FreyaHard.class, extremeFreyaIzId);
        } else if (command.equalsIgnoreCase("request_stone")) {
            if (player.getInventory().getCountOf(15469) > 0 || player.getInventory().getCountOf(15470) > 0) {
                showChatWindow(player, 4);
            } else if (player.getQuestState("_10286_ReunionWithSirra") == null || !player.getQuestState("_10286_ReunionWithSirra").isCompleted()) {
                ItemFunctions.addItem(player, 15470, 1);
                showChatWindow(player, 5);
            } else {
                ItemFunctions.addItem(player, 15469, 1);
                showChatWindow(player, 5);
            }
        } else {
            super.onBypassFeedback(player, command);
        }
    }
}

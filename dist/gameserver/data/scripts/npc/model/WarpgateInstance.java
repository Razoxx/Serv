package npc.model;

import l2p.gameserver.Config;
import l2p.gameserver.instancemanager.HellboundManager;
import l2p.gameserver.model.Player;
import l2p.gameserver.model.instances.NpcInstance;
import l2p.gameserver.templates.npc.NpcTemplate;

/**
 * @author pchayka
 */
public class WarpgateInstance extends NpcInstance {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public WarpgateInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (!canBypassCheck(player, this)) {
            return;
        }

        if (command.startsWith("enter_hellbound")) {
            if ((HellboundManager.getHellboundLevel() != 0 && (player.isQuestCompleted("_130_PathToHellbound") || player.isQuestCompleted("_133_ThatsBloodyHot"))) || Config.HELLBOUND_ON) {
                player.setVar("EnterUrban", 0, -1);
                player.setVar("EnterHellbound", 1, -1);
                player.teleToLocation(-11272, 236464, -3248);
            } else {
                showChatWindow(player, "default/32318-1.htm");
            }
        } else {
            super.onBypassFeedback(player, command);
        }
    }
}

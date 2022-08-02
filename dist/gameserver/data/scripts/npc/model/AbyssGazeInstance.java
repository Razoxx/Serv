package npc.model;

import l2p.gameserver.instancemanager.SoIManager;
import l2p.gameserver.model.Player;
import l2p.gameserver.model.instances.NpcInstance;
import l2p.gameserver.templates.npc.NpcTemplate;
import l2p.gameserver.utils.ReflectionUtils;
import instances.HeartInfinityAttack;
import instances.HeartInfinityDefence;

/**
 * @author pchayka
 */
public final class AbyssGazeInstance extends NpcInstance {

    private static final int ekimusIzId = 121;
    private static final int hoidefIzId = 122;

    public AbyssGazeInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (!canBypassCheck(player, this)) {
            return;
        }

        if (command.startsWith("request_permission")) {
            if (SoIManager.getCurrentStage() == 2 || SoIManager.getCurrentStage() == 5) {
                showChatWindow(player, "default/32540-2.htm");
            } else if (SoIManager.getCurrentStage() == 3 && SoIManager.isSeedOpen()) {
                showChatWindow(player, "default/32540-3.htm");
            } else {
                showChatWindow(player, "default/32540-1.htm");
            }
        } else if (command.equalsIgnoreCase("request_ekimus")) {
            if (SoIManager.getCurrentStage() == 2) {
                ReflectionUtils.simpleEnterInstancedZone(player, HeartInfinityAttack.class, ekimusIzId);
            }
        } else if (command.equalsIgnoreCase("enter_seed")) {
            if (SoIManager.getCurrentStage() == 3) {
                SoIManager.teleportInSeed(player);
            }
        } else if (command.equalsIgnoreCase("hoi_defence")) {
            if (SoIManager.getCurrentStage() == 5) {
                ReflectionUtils.simpleEnterInstancedZone(player, HeartInfinityDefence.class, hoidefIzId);
            }
        } else {
            super.onBypassFeedback(player, command);
        }
    }
}

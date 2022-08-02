package npc.model;

import l2p.gameserver.model.Player;
import l2p.gameserver.model.instances.NpcInstance;
import l2p.gameserver.templates.npc.NpcTemplate;
import l2p.gameserver.utils.ReflectionUtils;
import instances.CrystalCaverns;

public class CoralGardenGateInstance extends NpcInstance {

    private static final long serialVersionUID = -1L;

    public CoralGardenGateInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (!canBypassCheck(player, this)) {
            return;
        }

        if (command.equalsIgnoreCase("request_coralg")) {
            ReflectionUtils.simpleEnterInstancedZone(player, CrystalCaverns.class, 10);
        } else {
            super.onBypassFeedback(player, command);
        }
    }
}

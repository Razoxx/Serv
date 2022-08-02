package npc.model;

import l2p.gameserver.model.Player;
import l2p.gameserver.model.instances.NpcInstance;
import l2p.gameserver.templates.npc.NpcTemplate;
import l2p.gameserver.utils.ReflectionUtils;

/**
 * @author pchayka
 */
public final class GruffManInstance extends NpcInstance {

    private static final int elcardiaIzId = 158;

    public GruffManInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (!canBypassCheck(player, this)) {
            return;
        }

        if (command.equalsIgnoreCase("elcardia_enter")) {
            ReflectionUtils.simpleEnterInstancedZone(player, elcardiaIzId);
        } else {
            super.onBypassFeedback(player, command);
        }
    }
}

package npc.model;

import java.util.Map;
import l2p.commons.util.Rnd;
import l2p.gameserver.instancemanager.DimensionalRiftManager;
import l2p.gameserver.instancemanager.DimensionalRiftManager.DimensionalRiftRoom;
import l2p.gameserver.model.Party;
import l2p.gameserver.model.Player;
import l2p.gameserver.model.entity.DelusionChamber;
import l2p.gameserver.model.entity.Reflection;
import l2p.gameserver.model.instances.NpcInstance;
import l2p.gameserver.serverpackets.components.SystemMsg;
import l2p.gameserver.templates.npc.NpcTemplate;
import l2p.gameserver.utils.ReflectionUtils;

/**
 * @author pchayka
 */
public final class DelustionGatekeeperInstance extends NpcInstance {

    public DelustionGatekeeperInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (!canBypassCheck(player, this)) {
            return;
        }

        if (command.startsWith("enterDC")) {
            int izId = Integer.parseInt(command.substring(8));
            int type = izId - 120;
            Map<Integer, DimensionalRiftRoom> rooms = DimensionalRiftManager.getInstance().getRooms(type);
            if (rooms == null) {
                player.sendPacket(SystemMsg.SYSTEM_ERROR);
                return;
            }
            // TODO: DS: move to simpleEnterInstance
            Reflection r = player.getActiveReflection();
            if (r != null) {
                if (ReflectionUtils.canReenterInstance(player, izId)) {
                    player.teleToLocation(r.getTeleportLoc(), r);
                }
            } else if (ReflectionUtils.canEnterInstance(player, izId)) {
                Party party = player.getParty();
                if (party != null) {
                    new DelusionChamber(party, type, Rnd.get(1, rooms.size() - 1));
                }
            }
        } else {
            super.onBypassFeedback(player, command);
        }
    }
}

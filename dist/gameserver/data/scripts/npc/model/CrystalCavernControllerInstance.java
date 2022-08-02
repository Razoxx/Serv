package npc.model;

import l2p.gameserver.instancemanager.ReflectionManager;
import l2p.gameserver.model.Player;
import l2p.gameserver.model.instances.NpcInstance;
import l2p.gameserver.serverpackets.components.NpcString;
import l2p.gameserver.scripts.Functions;
import l2p.gameserver.templates.npc.NpcTemplate;
import l2p.gameserver.utils.ItemFunctions;
import l2p.gameserver.utils.Location;
import bosses.BaylorManager;
import instances.CrystalCaverns;

/**
 * @author pchayka
 */
public class CrystalCavernControllerInstance extends NpcInstance {

    private static final long serialVersionUID = -1L;

    public CrystalCavernControllerInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public String getHtmlPath(int npcId, int val, Player player) {
        String htmlpath = null;
        if (val == 0) {
            if (player.isInParty() && player.getParty().getPartyLeader() == player) {
                if (getNpcId() == 32280) {
                    htmlpath = "default/32280-2.htm";
                } else if (getNpcId() == 32278) {
                    htmlpath = "default/32278.htm";
                } else if (getNpcId() == 32276) {
                    htmlpath = "default/32276.htm";
                } else if (getNpcId() == 32279) {
                    htmlpath = "default/32279.htm";
                } else if (getNpcId() == 32277) {
                    htmlpath = "default/32277.htm";
                }
            } else {
                htmlpath = "default/32280-1.htm";
            }
        } else {
            htmlpath = "default/32280-1.htm";
        }
        return htmlpath;
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (!canBypassCheck(player, this)) {
            return;
        }
		if (checkForDominionWard(player))
			return;

        if (command.equalsIgnoreCase("request_emerald")) {
            ((CrystalCaverns) getReflection()).notifyEmeraldRequest();
        } else if (command.equalsIgnoreCase("request_coral")) {
            ((CrystalCaverns) getReflection()).notifyCoralRequest();
        } else if (command.equalsIgnoreCase("request_baylor")) {
            int state = BaylorManager.canIntoBaylorLair(player);
            if (state == 1 || state == 2) {
                showChatWindow(player, "default/32276-1.htm");
                return;
            } else if (state == 4) {
                showChatWindow(player, "default/32276-2.htm");
                return;
            } else if (state == 3) {
                showChatWindow(player, "default/32276-3.htm");
                return;
            }
            if (player.isInParty()) {
                for (Player p : player.getParty().getPartyMembers()) {
                    if (ItemFunctions.getItemCount(p, 9695) < 1) {
                        Functions.npcSay(this, NpcString.S1_YOU_DONT_HAVE_BLUE_CRYSTAL, p.getName());
                        return;
                    }
                    if (ItemFunctions.getItemCount(p, 9696) < 1) {
                        Functions.npcSay(this, NpcString.S1_YOU_DONT_HAVE_RED_CRYSTAL, p.getName());
                        return;
                    }
                    if (ItemFunctions.getItemCount(p, 9697) < 1) {
                        Functions.npcSay(this, NpcString.S1_YOU_DONT_HAVE_CLEAR_CRYSTAL, p.getName());
                        return;
                    }
                    if (!isInRange(p, 400)) {
                        Functions.npcSay(this, NpcString.S1_IF_YOU_ARE_TOO_FAR_AWAY_FROM_ME__I_CANT_LET_YOU_GO, p.getName());
                        return;
                    }
                }
                ItemFunctions.addItem(player, 10015, 1, true);
                for (Player p : player.getParty().getPartyMembers()) {
                    ItemFunctions.removeItem(p, 9695, 1, true);
                    ItemFunctions.removeItem(p, 9696, 1, true);
                    ItemFunctions.removeItem(p, 9697, 1, true);
                    p.teleToLocation(new Location(153526, 142172, -12736));
                }
                BaylorManager.entryToBaylorLair(player);
                deleteMe();
            }
        } else if (command.equalsIgnoreCase("request_parme")) {
            player.teleToLocation(new Location(153736, 142008, -9744));
        } else if (command.equalsIgnoreCase("request_exit")) {
            if (getReflection().getInstancedZoneId() == 10) {
                player.teleToLocation(getReflection().getInstancedZone().getReturnCoords(), ReflectionManager.DEFAULT);
            }
        } else {
            super.onBypassFeedback(player, command);
        }
    }
}

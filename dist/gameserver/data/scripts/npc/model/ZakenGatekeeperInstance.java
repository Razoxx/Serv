package npc.model;

import instances.ZakenDay;
import instances.ZakenDay83;
import instances.ZakenNight;

import l2p.gameserver.model.Player;
import l2p.gameserver.model.instances.NpcInstance;
import l2p.gameserver.templates.npc.NpcTemplate;
import l2p.gameserver.utils.ReflectionUtils;

/**
 * @author pchayka
 */
public final class ZakenGatekeeperInstance extends NpcInstance {

    private static final int nightZakenIzId = 114;
    private static final int dayZakenIzId = 133;
    private static final int ultraZakenIzId = 135;

    public ZakenGatekeeperInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (!canBypassCheck(player, this)) {
            return;
        }

        if (command.equalsIgnoreCase("request_nightzaken")) {
            ReflectionUtils.simpleEnterInstancedZone(player, ZakenNight.class, nightZakenIzId);
        } else if (command.equalsIgnoreCase("request_dayzaken")) {
            ReflectionUtils.simpleEnterInstancedZone(player, ZakenDay.class, dayZakenIzId);
        } else if (command.equalsIgnoreCase("request_ultrazaken")) {
            ReflectionUtils.simpleEnterInstancedZone(player, ZakenDay83.class, ultraZakenIzId);
        } else {
            super.onBypassFeedback(player, command);
        }
    }
}

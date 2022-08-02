package zones;

import l2p.gameserver.listener.zone.OnZoneEnterLeaveListener;
import l2p.gameserver.model.Creature;
import l2p.gameserver.model.Player;
import l2p.gameserver.model.Zone;
import l2p.gameserver.scripts.ScriptFile;
import l2p.gameserver.utils.ReflectionUtils;

/**
 *
 * @author Junky Funky
 */
public class BelethForbidden implements ScriptFile {

    private static ZoneListener _zoneListener;

    @Override
    public void onLoad() {
        _zoneListener = new ZoneListener();
        Zone zone = ReflectionUtils.getZone("[20_24_water1]");
        zone.addListener(_zoneListener);
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public class ZoneListener implements OnZoneEnterLeaveListener {

        @Override
        public void onZoneEnter(Zone zone, Creature cha) {
            if (!cha.isPlayable()) {
                return;
            }
            Player player = cha.getPlayer();
            if (player != null) {
                player.sendMessage("You swam too deep");
                cha.teleToLocation(147450, 27120, -2208);
            }
        }

        @Override
        public void onZoneLeave(Zone zone, Creature cha) {
        }
    }
}

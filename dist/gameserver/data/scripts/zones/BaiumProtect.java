package zones;

import l2p.gameserver.listener.zone.OnZoneEnterLeaveListener;
import l2p.gameserver.model.Creature;
import l2p.gameserver.model.Player;
import l2p.gameserver.model.Zone;
import l2p.gameserver.scripts.ScriptFile;
import l2p.gameserver.utils.ReflectionUtils;

public class BaiumProtect implements ScriptFile {

    private static ZoneListener _zoneListener;

    @Override
    public void onLoad() {
        _zoneListener = new ZoneListener();
        Zone zone = ReflectionUtils.getZone("[Baium_Protect]");
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
            Player player = cha.getPlayer();

            if (player == null) {
                return;
            }

            if (player.getPlayerAccess().CanTeleport)
                return;
            
            if (player.getVar("EnterBaium") != null) {
                if (player.getVarLong("EnterBaium") != 0) {
                    return;
                } else {
                    player.sendMessage(player.isLangRus() ? "Вы не можете находиться здесь." : "You can not be here.");
                    player.teleToLocation(147450, 27120, -2208);
                }
            } else {
                player.sendMessage(player.isLangRus() ? "Вы не можете находиться здесь." : "You can not be here.");
                player.teleToLocation(147450, 27120, -2208);
            }
        }

        @Override
        public void onZoneLeave(Zone zone, Creature cha) {
        }
    }
}

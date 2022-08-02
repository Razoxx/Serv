package zones;

import l2p.gameserver.listener.zone.OnZoneEnterLeaveListener;
import l2p.gameserver.model.Creature;
import l2p.gameserver.model.Player;
import l2p.gameserver.model.Zone;
import l2p.gameserver.scripts.ScriptFile;
import l2p.gameserver.utils.ReflectionUtils;

public class AntharasProtect implements ScriptFile {

    private static ZoneListener _zoneListener;

    @Override
    public void onLoad() {
        _zoneListener = new ZoneListener();
        Zone zone = ReflectionUtils.getZone("[Antharas_Protect]");
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
            
            if (player.getVar("EnterAntharas") != null) {
                if (player.getVarLong("EnterAntharas") != 0) {
                    return;
                } else {
                    player.sendMessage(player.isLangRus() ? "Вы не можете находиться здесь." : "You can not be here.");
                    player.teleToLocation(82698, 148638, -3473);
                }
            } else {
                player.sendMessage(player.isLangRus() ? "Вы не можете находиться здесь." : "You can not be here.");
                player.teleToLocation(82698, 148638, -3473);
            }
        }

        @Override
        public void onZoneLeave(Zone zone, Creature cha) {
        }
    }
}

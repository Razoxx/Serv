package l2p.gameserver.model.entity.boat;

import l2p.gameserver.model.Player;
import l2p.gameserver.serverpackets.ExAirShipInfo;
import l2p.gameserver.serverpackets.ExGetOffAirShip;
import l2p.gameserver.serverpackets.ExGetOnAirShip;
import l2p.gameserver.serverpackets.ExMoveToLocationAirShip;
import l2p.gameserver.serverpackets.ExMoveToLocationInAirShip;
import l2p.gameserver.serverpackets.ExStopMoveAirShip;
import l2p.gameserver.serverpackets.ExStopMoveInAirShip;
import l2p.gameserver.serverpackets.ExValidateLocationInAirShip;
import l2p.gameserver.serverpackets.L2GameServerPacket;
import l2p.gameserver.templates.CharTemplate;
import l2p.gameserver.utils.Location;

/**
 * @author VISTALL
 * @date 17:45/26.12.2010
 */
public class AirShip extends Boat {

    public AirShip(int objectId, CharTemplate template) {
        super(objectId, template);
    }

    @Override
    public L2GameServerPacket infoPacket() {
        return new ExAirShipInfo(this);
    }

    @Override
    public L2GameServerPacket movePacket() {
        return new ExMoveToLocationAirShip(this);
    }

    @Override
    public L2GameServerPacket inMovePacket(Player player, Location src, Location desc) {
        return new ExMoveToLocationInAirShip(player, this, src, desc);
    }

    @Override
    public L2GameServerPacket stopMovePacket() {
        return new ExStopMoveAirShip(this);
    }

    @Override
    public L2GameServerPacket inStopMovePacket(Player player) {
        return new ExStopMoveInAirShip(player);
    }

    @Override
    public L2GameServerPacket startPacket() {
        return null;
    }

    @Override
    public L2GameServerPacket checkLocationPacket() {
        return null;
    }

    @Override
    public L2GameServerPacket validateLocationPacket(Player player) {
        return new ExValidateLocationInAirShip(player);
    }

    @Override
    public L2GameServerPacket getOnPacket(Player player, Location location) {
        return new ExGetOnAirShip(player, this, location);
    }

    @Override
    public L2GameServerPacket getOffPacket(Player player, Location location) {
        return new ExGetOffAirShip(player, this, location);
    }

    @Override
    public boolean isAirShip() {
        return true;
    }

    @Override
    public void oustPlayers() {
        for (Player player : _players) {
            oustPlayer(player, getReturnLoc(), true);
        }
    }
}

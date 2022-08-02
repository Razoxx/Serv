package l2p.gameserver.serverpackets;

import l2p.gameserver.model.Player;
import l2p.gameserver.utils.Location;

/**
 * format dddddd
 */
public class TargetSelected extends L2GameServerPacket {

    private int _objectId;
    private int _targetId;
    private int _heading;
    private Location _loc;

    public TargetSelected(Player player, int targetId, Location loc) {
        _objectId = player.getObjectId();
        _targetId = targetId;
        _loc = loc;
        _heading = player.getHeading();
    }

    @Override
    protected final void writeImpl() {
        writeC(0x23);
        writeD(_objectId);
        writeD(_targetId);
        writeD(_loc.x);
        writeD(_loc.y);
        writeD(_loc.z);
        writeD(_heading);
    }
}
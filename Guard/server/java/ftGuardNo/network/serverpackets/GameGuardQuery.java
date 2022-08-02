package ftGuard.network.serverpackets;

import l2p.gameserver.serverpackets.L2GameServerPacket;

public final class GameGuardQuery extends L2GameServerPacket {

    @Override
    protected final void writeImpl() {
    }

    @Override
    public String getType() {
        return "[S] 74 GameGuardQuery";
    }
}
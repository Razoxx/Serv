package ftGuard.network.clientpackets;

import l2p.gameserver.clientpackets.L2GameClientPacket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameGuardReply extends L2GameClientPacket {

    private static final Logger _log = LoggerFactory.getLogger(GameGuardReply.class);

    @Override
    protected void readImpl() {
    }

    @Override
    protected void runImpl() {
    }

    @Override
    public String getType() {
        return "[C] CB GameGuardReply";
    }
}
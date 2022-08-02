package ftGuard.network.clientpackets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2p.gameserver.network.GameClient;
import l2p.gameserver.clientpackets.L2GameClientPacket;

public class GameGuardReply extends L2GameClientPacket {

    private static final Logger _log = LoggerFactory.getLogger(GameGuardReply.class);
    private int _dx;

    @Override
    protected void readImpl() {
        _dx = readC();
    }

    @Override
    protected void runImpl() {
        GameClient client = getClient();
        if (_dx == 104) {
            client.setGameGuardOk(true);
        } else {
            client.setGameGuardOk(false);
        }

    }

    @Override
    public String getType() {
        return "[C] CB GameGuardReply";
    }
}
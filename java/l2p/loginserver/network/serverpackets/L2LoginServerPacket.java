package l2p.loginserver.network.serverpackets;

import l2p.loginserver.network.L2LoginClient;
import l2p.commons.net.nio.impl.SendablePacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class L2LoginServerPacket extends SendablePacket<L2LoginClient> {

    private static final Logger _log = LoggerFactory.getLogger(L2LoginServerPacket.class);

    @Override
    public final boolean write() {
        try {
            writeImpl();
            return true;
        } catch (Exception e) {
            _log.error("Client: " + getClient() + " - Failed writing: " + getClass().getSimpleName() + "!", e);
        }
        return false;
    }

    protected abstract void writeImpl();
}

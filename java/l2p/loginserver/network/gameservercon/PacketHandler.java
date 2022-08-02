package l2p.loginserver.network.gameservercon;

import java.nio.ByteBuffer;

import l2p.loginserver.network.gameservercon.gspackets.AuthRequest;
import l2p.loginserver.network.gameservercon.gspackets.BonusRequest;
import l2p.loginserver.network.gameservercon.gspackets.ChangeAccessLevel;
import l2p.loginserver.network.gameservercon.gspackets.ChangeAllowedHwid;
import l2p.loginserver.network.gameservercon.gspackets.ChangeAllowedIp;
import l2p.loginserver.network.gameservercon.gspackets.ChangePassword;
import l2p.loginserver.network.gameservercon.gspackets.OnlineStatus;
import l2p.loginserver.network.gameservercon.gspackets.PingResponse;
import l2p.loginserver.network.gameservercon.gspackets.PlayerAuthRequest;
import l2p.loginserver.network.gameservercon.gspackets.PlayerInGame;
import l2p.loginserver.network.gameservercon.gspackets.PlayerLogout;
import l2p.loginserver.network.gameservercon.gspackets.SetAccountInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PacketHandler {

    private static Logger _log = LoggerFactory.getLogger(PacketHandler.class);

    public static ReceivablePacket handlePacket(GameServer gs, ByteBuffer buf) {
        ReceivablePacket packet = null;

        int id = buf.get() & 0xff;

        if (!gs.isAuthed()) {
            switch (id) {
                case 0x00:
                    packet = new AuthRequest();
                    break;
                default:
                    _log.error("Received unknown packet: " + Integer.toHexString(id));
            }
        } else {
            switch (id) {
                case 0x01:
                    packet = new OnlineStatus();
                    break;
                case 0x02:
                    packet = new PlayerAuthRequest();
                    break;
                case 0x03:
                    packet = new PlayerInGame();
                    break;
                case 0x04:
                    packet = new PlayerLogout();
                    break;
                case 0x05:
                    packet = new SetAccountInfo();
                    break;
                case 0x07:
                    packet = new ChangeAllowedIp();
                    break;
                case 0x08:
                    packet = new ChangePassword();
                    break;
                case 0x09:
                    packet = new ChangeAllowedHwid();
                    break;
                case 0x10:
                    packet = new BonusRequest();
                    break;
                case 0x11:
                    packet = new ChangeAccessLevel();
                    break;
                case 0xff:
                    packet = new PingResponse();
                    break;
                default:
                    _log.error("Received unknown packet: " + Integer.toHexString(id));
            }
        }

        return packet;
    }
}
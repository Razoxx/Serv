package l2p.gameserver.clientpackets;

import l2p.gameserver.model.Player;
import l2p.gameserver.serverpackets.components.HtmlMessage;
import l2p.gameserver.utils.BypassStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestLinkHtml extends L2GameClientPacket {

    private static final Logger _log = LoggerFactory.getLogger(RequestLinkHtml.class);
    //Format: cS
    private String _link;

    @Override
    protected void readImpl() {
        _link = readS();
    }

    @Override
    protected void runImpl() {

        Player player = getClient().getActiveChar();
        if (player == null) {
            return;
        }

        if (_link.contains("..") || !_link.endsWith(".htm")) {
            _log.warn("[RequestLinkHtml] hack? link contains prohibited characters: '" + _link + "', skipped");
            return;
        }

        BypassStorage.ValidBypass bp = player.getBypassStorage().validate(_link);
        if (bp == null) {
            _log.warn(" RequestLinkHtml: Unexpected link : " + _link + "!");
            return;
        }

        try {
            HtmlMessage msg = player.getLastNpc() == null ? new HtmlMessage(0) : new HtmlMessage(player, player.getLastNpc());
            msg.setFile(this._link);
            player.sendPacket(msg);
        } catch (Exception e) {
            _log.warn("Bad RequestLinkHtml: ", e);
        }
    }
}

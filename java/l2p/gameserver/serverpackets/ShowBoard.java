package l2p.gameserver.serverpackets;

import java.util.List;

import l2p.gameserver.model.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShowBoard extends L2GameServerPacket {

    private static final Logger _log = LoggerFactory.getLogger(ShowBoard.class);
    private String _htmlCode;
    private String _id;
    private List<String> _arg;
    private String _addFav = "";

    private static final String[] DIRECT_BYPASS = {"bypass _bbshome", "bypass _bbsgetfav", "bypass _bbsloc", "bypass _bbsclan", "bypass _bbsmemo", "bypass _maillist_0_1_0_", "bypass _friendlist_0_"};
    private String _html;
    private String _fav;
    private int _showBoard = 1; // 1 show, 0 hide

    public static void separateAndSend(String html, Player player) {
        String fav = "";
        if (player.getSessionVar("add_fav") != null) {
            fav = "bypass _bbsaddfav_List";
        }
        player.getBypassStorage().parseHtml(html, true);
        if (html.length() < 8180) {
            player.sendPacket(new ShowBoard("101", html, fav));
            player.sendPacket(new ShowBoard("102", "", fav));
            player.sendPacket(new ShowBoard("103", "", fav));
        } else if (html.length() < 8180 * 2) {
            player.sendPacket(new ShowBoard("101", html.substring(0, 8180), fav));
            player.sendPacket(new ShowBoard("102", html.substring(8180, html.length()), fav));
            player.sendPacket(new ShowBoard("103", "", fav));
        } else if (html.length() < 8180 * 3) {
            player.sendPacket(new ShowBoard("101", html.substring(0, 8180), fav));
            player.sendPacket(new ShowBoard("102", html.substring(8180, 16360), fav));
            player.sendPacket(new ShowBoard("103", html.substring(8180 * 2, html.length()), fav));
        } else {
            throw new IllegalArgumentException("Html is too long!");
        }
    }

    public static void separateAndSend(String html, List<String> arg, Player player) {
        String fav = "";
        if (player.getSessionVar("add_fav") != null) {
            fav = "bypass _bbsaddfav_List";
        }
        player.setLastNpc(null);
        player.getBypassStorage().parseHtml(html, true);
        if (html.length() < 8180) {
            player.sendPacket(new ShowBoard("1001", html, fav));
            player.sendPacket(new ShowBoard("1002", arg, fav));
        } else {
            throw new IllegalArgumentException("Html is too long!");
        }
    }

    private ShowBoard(String id, String html, String fav) {
        _html = (id + "\b");
        if (html != null) {
            _html += html;
        }
        _fav = fav;
    }

    private ShowBoard(String id, List<String> arg, String fav) {
        _html = (id + "\b");
        for (String a : arg) {
            _html = (_html + a + " \b");
        }
    }

    /**
     * Hides the community board
     */
    public ShowBoard() {
        _showBoard = 0;
        _html = "";
    }

    @Override
    protected final void writeImpl() {
        writeC(0x7b);
        writeC(_showBoard);
        for (String bbsBypass : DIRECT_BYPASS) {
            writeS(bbsBypass);
        }
        writeS(_fav);
        writeS(_html);
    }

}

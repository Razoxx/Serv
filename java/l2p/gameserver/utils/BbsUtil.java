package l2p.gameserver.utils;

import l2p.gameserver.Config;
import l2p.gameserver.data.htm.HtmCache;
import l2p.gameserver.model.Player;

public class BbsUtil {

    public static String htmlAll(String htm, Player player) {
        String html_all = HtmCache.getInstance().getHtml(Config.BBS_HOME_DIR + "block/allpages.htm", player);
        String html_menu = HtmCache.getInstance().getHtml(Config.BBS_HOME_DIR + "block/menu.htm", player);
        String html_copy = HtmCache.getInstance().getHtml(Config.BBS_HOME_DIR + "block/copyright.htm", player);
        html_all = html_all.replace("%main_menu%", html_menu);
        html_all = html_all.replace("%balans%", "Ваш баланс: <font color=\"LEVEL\">" + player.getBalans() + " CRD</font> <br");
        html_all = html_all.replace("%body_page%", htm);
        html_all = html_all.replace("%copyright%", html_copy);
        html_all = html_all.replace("%copyrightsym%", "©");
        return html_all;
    }
}
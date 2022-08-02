package services.community.masteriopack.bbsbuffer;

import l2p.gameserver.Config;
import l2p.gameserver.data.htm.HtmCache;
import l2p.gameserver.model.Player;

/**
 * Class is used in BBSBufferHtm class.
 *
 * @author Masterio
 *
 */
public class BBSBufferHtmFile {

    public static class MainPage {

        /**
         * This method should be used first.
         *
         * @return
         */
        public static String getBody(Player activeChar) {
            return HtmCache.getInstance().getHtml(Config.BBS_HOME_DIR + "pages/bbsbuffer/mainpage/body.htm", activeChar);
        }

        /**
         * Returns simple select menu.
         *
         * @return
         */
        public static String getInsideDefault(Player activeChar) {
            return HtmCache.getInstance().getHtml(Config.BBS_HOME_DIR + "pages/bbsbuffer/mainpage/inside_default.htm", activeChar);
        }

        /**
         * Returns container for buff category select and scheme list.
         *
         * @return
         */
        public static String getInsideAllInOne(Player activeChar) {
            return HtmCache.getInstance().getHtml(Config.BBS_HOME_DIR + "pages/bbsbuffer/mainpage/inside_all_in_one.htm", activeChar);
        }

    }

    public static class BuffManager {

        /**
         * This method should be used first.
         *
         * @return
         */
        public static String getBody(Player activeChar) {
            return HtmCache.getInstance().getHtml(Config.BBS_HOME_DIR + "pages/bbsbuffer/buffmanager/body.htm", activeChar);
        }

        public static String getInside(Player activeChar) {
            return HtmCache.getInstance().getHtml(Config.BBS_HOME_DIR + "pages/bbsbuffer/buffmanager/inside.htm", activeChar);
        }

        public static String getListItem(Player activeChar) {
            return HtmCache.getInstance().getHtml(Config.BBS_HOME_DIR + "pages/bbsbuffer/buffmanager/list_item.htm", activeChar);
        }

        public static String getAutoRebuffListItem(Player activeChar) {
            return HtmCache.getInstance().getHtml(Config.BBS_HOME_DIR + "pages/bbsbuffer/buffmanager/autorebuff_list_item.htm", activeChar);
        }

        public static String getBodySchemeName(Player activeChar) {
            return HtmCache.getInstance().getHtml(Config.BBS_HOME_DIR + "pages/bbsbuffer/buffmanager/body_scheme_name.htm", activeChar);
        }

        public static String getListItemMainPage(Player activeChar) {
            return HtmCache.getInstance().getHtml(Config.BBS_HOME_DIR + "pages/bbsbuffer/buffmanager/list_item_main_page.htm", activeChar);
        }

        public static String getAutoRebuffListItemMainPage(Player activeChar) {
            return HtmCache.getInstance().getHtml(Config.BBS_HOME_DIR + "pages/bbsbuffer/buffmanager/autorebuff_list_item_main_page.htm", activeChar);
        }
    }

    public static class BuffPage {

        /**
         * This method should be used first.
         *
         * @return
         */
        public static String getBody(Player activeChar) {
            return HtmCache.getInstance().getHtml(Config.BBS_HOME_DIR + "pages/bbsbuffer/buffpage/body.htm", activeChar);
        }

        public static String getInside(Player activeChar) {
            return HtmCache.getInstance().getHtml(Config.BBS_HOME_DIR + "pages/bbsbuffer/buffpage/inside.htm", activeChar);
        }

        public static String getListGroupMenu(Player activeChar) {
            return HtmCache.getInstance().getHtml(Config.BBS_HOME_DIR + "pages/bbsbuffer/buffpage/group_menu.htm", activeChar);
        }

        public static String getListItemUnselected(Player activeChar) {
            return HtmCache.getInstance().getHtml(Config.BBS_HOME_DIR + "pages/bbsbuffer/buffpage/list_item_unselected.htm", activeChar);
        }

        public static String getListItemSelected(Player activeChar) {
            return HtmCache.getInstance().getHtml(Config.BBS_HOME_DIR + "pages/bbsbuffer/buffpage/list_item_selected.htm", activeChar);
        }

        public static String getInsideBuffTable(Player activeChar) {
            return HtmCache.getInstance().getHtml(Config.BBS_HOME_DIR + "pages/bbsbuffer/buffpage/inside_buff_table.htm", activeChar);
        }

        public static String getInsideBuffTableSmall(Player activeChar) {
            return HtmCache.getInstance().getHtml(Config.BBS_HOME_DIR + "pages/bbsbuffer/buffpage/inside_buff_table_small.htm", activeChar);
        }

        public static String getListItemUnselectedSmall(Player activeChar) {
            return HtmCache.getInstance().getHtml(Config.BBS_HOME_DIR + "pages/bbsbuffer/buffpage/list_item_unselected_small.htm", activeChar);
        }

        public static String getListItemSelectedSmall(Player activeChar) {
            return HtmCache.getInstance().getHtml(Config.BBS_HOME_DIR + "pages/bbsbuffer/buffpage/list_item_selected_small.htm", activeChar);
        }

    }

    public static class Help {

        /**
         * This method should be used first.
         *
         * @return
         */
        public static String getBody() {

            return null;
        }
    }

}

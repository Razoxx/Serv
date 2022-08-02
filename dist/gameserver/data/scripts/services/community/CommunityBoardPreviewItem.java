/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package services.community;

import java.util.StringTokenizer;
import l2p.gameserver.Config;
import l2p.gameserver.data.xml.holder.BuyListHolder;
import l2p.gameserver.data.xml.holder.BuyListHolder.NpcTradeList;
import l2p.gameserver.handler.bbs.CommunityBoardManager;
import l2p.gameserver.handler.bbs.ICommunityBoardHandler;
import l2p.gameserver.model.Player;
import l2p.gameserver.scripts.Functions;
import l2p.gameserver.scripts.ScriptFile;
import l2p.gameserver.serverpackets.ShopPreviewList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author deprecat
 * bypass _bbsshowitem:10050
 * Примерку создать в merchant_buylists.xml
 */
public class CommunityBoardPreviewItem extends Functions implements ScriptFile, ICommunityBoardHandler {

    private static final Logger _log = LoggerFactory.getLogger(CommunityBoardPreviewItem.class);

    @Override
    public void onLoad() {
        if (Config.COMMUNITYBOARD_ENABLED) {
            _log.info("CommunityBoardPreviewItem: service loaded.");
            CommunityBoardManager.getInstance().registerHandler(this);
        }
    }

    @Override
    public void onReload() {
        if (Config.COMMUNITYBOARD_ENABLED) {
            CommunityBoardManager.getInstance().removeHandler(this);
        }
    }

    @Override
    public void onShutdown() {
    }

    @Override
    public String[] getBypassCommands() {
        return new String[]{"_bbsshowitem"};
    }

    @Override
    public void onBypassCommand(Player player, String bypass) {

        if (bypass.startsWith("_bbsshowitem")) {

            StringTokenizer st2 = new StringTokenizer(bypass, ";");
            String[] mBypass = st2.nextToken().split(":");
            int id = Integer.parseInt(mBypass[1]);
            NpcTradeList list = BuyListHolder.getInstance().getBuyList(id);

            if (list != null) {
                ShopPreviewList bl = new ShopPreviewList(list, player);
                player.sendPacket(bl);
            } else {
                _log.warn("no buylist with id:" + id);
                player.sendActionFailed();
            }
        }
    }

    @Override
    public void onWriteCommand(Player player, String bypass, String arg1, String arg2, String arg3, String arg4, String arg5) {
    }
}

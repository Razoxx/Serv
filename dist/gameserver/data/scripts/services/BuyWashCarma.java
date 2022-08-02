package services;

import l2p.gameserver.Config;
import l2p.gameserver.data.htm.HtmCache;
import l2p.gameserver.data.xml.holder.ItemHolder;
import l2p.gameserver.model.Player;
import l2p.gameserver.serverpackets.components.SystemMsg;
import l2p.gameserver.scripts.Functions;

public class BuyWashCarma extends Functions {

    public void list() {
        Player player = getSelf();
        if (!Config.SERVICES_WASH_PK_CARMA_ENABLED) {
            show(HtmCache.getInstance().getHtml("npcdefault.htm", player), player);
            return;
        }
        String html = HtmCache.getInstance().getHtml("scripts/services/BuyWashCarma.htm", player);
        StringBuilder dialog = new StringBuilder();
        if (player.getKarma() > 0)
        {
            dialog.append("<br><center><button value=\"");
            dialog.append(Config.SERVICES_WASH_PK_CARMA_PRICE).append(" ").append(ItemHolder.getInstance().getTemplate(Config.SERVICES_WASH_PK_CARMA_ITEM).getName());
            dialog.append("\" action=\"bypass -h scripts_services.BuyWashCarma:wash\" width=100 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\" /></center>");
        } else
            dialog.append("Не требуется");
        html = html.replaceFirst("%toreplace%", dialog.toString());

        show(html, player);
    }

    public void wash() {
        Player player = getSelf();
        if (!Config.SERVICES_WASH_PK_CARMA_ENABLED) {
            show(HtmCache.getInstance().getHtml("npcdefault.htm", player), player);
            return;
        }
        if ((Functions.getItemCount(player, Config.SERVICES_WASH_PK_CARMA_ITEM) >= Config.SERVICES_WASH_PK_CARMA_PRICE)) {
            Functions.removeItem(player, Config.SERVICES_WASH_PK_CARMA_ITEM, Config.SERVICES_WASH_PK_CARMA_PRICE);
            player.setKarma(0);
            player.broadcastCharInfo();
        } else {
            player.sendPacket(SystemMsg.INCORRECT_ITEM_COUNT);
        }
    }
}
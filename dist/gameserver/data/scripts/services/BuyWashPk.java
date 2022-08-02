package services;

import l2p.gameserver.Config;
import l2p.gameserver.data.htm.HtmCache;
import l2p.gameserver.data.xml.holder.ItemHolder;
import l2p.gameserver.model.Player;
import l2p.gameserver.serverpackets.components.SystemMsg;
import l2p.gameserver.scripts.Functions;

public class BuyWashPk extends Functions {

    public void list() {
        Player player = getSelf();
        if (!Config.SERVICES_WASH_PK_ENABLED) {
            show(HtmCache.getInstance().getHtml("npcdefault.htm", player), player);
            return;
        }
        String html = null;

        html = HtmCache.getInstance().getHtml("scripts/services/BuyWashPk.htm", player);
        String add = "";
        if (player.getPkKills() > 0)
        {
            for (int i = 1; i <= player.getPkKills(); i++) {
                add += "<a action=\"bypass -h scripts_services.BuyWashPk:get " + i + "\">" //
                        + "for " + i + //
                        " PK - " + Config.SERVICES_WASH_PK_PRICE * i + //
                        " " + ItemHolder.getInstance().getTemplate(Config.SERVICES_WASH_PK_ITEM).getName() + "</a><br>";
            }
        }
        else 
            add = "Не требуется";
        html = html.replaceFirst("%toreplace%", add);

        show(html, player);
    }

    public void get(String[] param) {
        Player player = getSelf();
        if (!Config.SERVICES_WASH_PK_ENABLED) {
            show(HtmCache.getInstance().getHtml("npcdefault.htm", player), player);
            return;
        }
        int i = Integer.parseInt(param[0]);
        if ((Functions.getItemCount(player, Config.SERVICES_WASH_PK_ITEM) >= Config.SERVICES_WASH_PK_PRICE * i)) {
            Functions.removeItem(player, Config.SERVICES_WASH_PK_ITEM, Config.SERVICES_WASH_PK_PRICE * i);
            int kills = player.getPkKills();
            player.setPkKills(kills - i);
            player.broadcastCharInfo();
        } else {
            player.sendPacket(SystemMsg.INCORRECT_ITEM_COUNT);
        }
    }
}
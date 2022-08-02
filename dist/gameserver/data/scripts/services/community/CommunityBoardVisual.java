package services.community;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import l2p.commons.dao.JdbcEntityState;
import l2p.gameserver.Config;
import l2p.gameserver.data.htm.HtmCache;
import l2p.gameserver.data.xml.holder.ItemHolder;
import l2p.gameserver.handler.bbs.CommunityBoardManager;
import l2p.gameserver.handler.bbs.ICommunityBoardHandler;
import l2p.gameserver.model.Player;
import l2p.gameserver.model.items.Inventory;
import l2p.gameserver.model.items.ItemInstance;
import l2p.gameserver.model.visual.VisualConfig;
import l2p.gameserver.scripts.Functions;
import l2p.gameserver.scripts.ScriptFile;
import l2p.gameserver.serverpackets.InventoryUpdate;
import l2p.gameserver.serverpackets.ShowBoard;
import l2p.gameserver.serverpackets.SystemMessage2;
import l2p.gameserver.serverpackets.components.SystemMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author deprecat
 */
public class CommunityBoardVisual extends Functions implements ScriptFile, ICommunityBoardHandler {

    private static final Logger _log = LoggerFactory.getLogger(CommunityBoardVisual.class);

    private int PRICE_ID = 57; // id уплаты
    private int PRICE_COUNT = 1; // кол-во предметов
    int count_on_page = 5; // кол-во выводимых полей на страницу

    @Override
    public void onLoad() {
        _log.info("CommunityBoard: CommunityBoardVisual loaded.");
        CommunityBoardManager.getInstance().registerHandler(this);
    }

    @Override
    public void onReload() {
        CommunityBoardManager.getInstance().removeHandler(this);
    }

    @Override
    public void onShutdown() {
    }

    @Override
    public String[] getBypassCommands() {
        return new String[]{"_bbsvisual"};
    }

    @Override
    public void onBypassCommand(Player player, String bypass) {

        if (player == null) {
            return;
        }
        int page = 0;

        StringTokenizer st = new StringTokenizer(bypass, ":");
        st.nextToken();
        String action = st.hasMoreTokens() ? st.nextToken() : "list";

        if (action.equals("list")) {

            String pag = st.hasMoreTokens() ? st.nextToken() : "1";
            //String pag = st.hasMoreTokens() ? st.nextToken() : "1";
            //_log.info("page=" + pag);
            page = Integer.parseInt(pag);

            String name = "None Name";
            String html = HtmCache.getInstance().getHtml(Config.BBS_HOME_DIR + "pages/visual.htm", player);

            StringBuilder sb = new StringBuilder("");
            sb.append("<table height=350 width=647>");
            sb.append("<tr>");
            sb.append("<td align=center width=320>");

            int k = 0;

            if (VisualConfig.size() / 2 <= count_on_page) {
                page = 1;
            }
            // Таблица
            for (int i = 1; i <= VisualConfig.size(); i = i + 2) {
                if (k < (page * count_on_page) && k >= ((page - 1) * count_on_page)) {
                    sb.append("<table height=60 width=320 background=\"L2UI_CT1.Windows_DF_TooltipBG\">");
                    sb.append("<tr>");
                    sb.append("<td height=32 width=32>");
                    sb.append("<img src=").append(VisualConfig.getVisualConfigId(i).icon).append(" width=32 height=32>");
                    sb.append("</td>");
                    sb.append("<td align=center width=320 height=50>");
                    sb.append(VisualConfig.getVisualConfigId(i).nameRu).append(" <br1>");
                    sb.append("Цена ").append(VisualConfig.getVisualConfigId(i).PRICE).append(" ").append(ItemHolder.getInstance().getTemplate(VisualConfig.getVisualConfigId(i).ITEM_ID).getName()).append("<br1>");

                    sb.append("<button value=\"Купить\" action=\"bypass _bbsvisual:buy-").append(VisualConfig.getVisualConfigId(i).ID).append("\" width=60 height=20 back=\"l2ui_ct1.button.button_df_small_down\" fore=\"l2ui_ct1.button.button_df_small\">");
                    sb.append("</td>");

                    sb.append("</tr>");
                    sb.append("</table>");
                }
                k++;
            }
            sb.append("</td>");

            sb.append("<td align=center width=320 height=50>");

            k = 0;

            // Таблица
            for (int i = 2; i <= VisualConfig.size(); i = i + 2) {

                if (k < (page * count_on_page) && k >= ((page - 1) * count_on_page)) {
                    sb.append("<table height=60 width=320 background=\"L2UI_CT1.Windows_DF_TooltipBG\">");
                    sb.append("<tr>");
                    sb.append("<td height=32 width=32>");
                    sb.append("<img src=").append(VisualConfig.getVisualConfigId(i).icon).append(" width=32 height=32>");
                    sb.append("</td>");
                    sb.append("<td align=center width=320 height=50>");
                    sb.append(VisualConfig.getVisualConfigId(i).nameRu).append(" <br1>");
                    sb.append("Цена ").append(VisualConfig.getVisualConfigId(i).PRICE).append(" ").append(ItemHolder.getInstance().getTemplate(VisualConfig.getVisualConfigId(i).ITEM_ID).getName()).append("<br1>");

                    sb.append("<button value=\"Купить\" action=\"bypass _bbsvisual:buy-").append(VisualConfig.getVisualConfigId(i).ID).append("\" width=60 height=20 back=\"l2ui_ct1.button.button_df_small_down\" fore=\"l2ui_ct1.button.button_df_small\">");
                    sb.append("</td>");

                    sb.append("</tr>");
                    sb.append("</table>");
                }
                k++;
            }
            sb.append("</td>");
            sb.append("</tr>");
            sb.append("</table>");

            sb.append("<table height=60 width=320 background=\"L2UI_CT1.Windows_DF_TooltipBG\">");
            sb.append("<tr>");
            sb.append("<td height=32 width=32>");
            sb.append("<img src=\"icon.skill3080\" width=32 height=32>");
            sb.append("</td>");
            sb.append("<td align=center width=320 height=50>");
            sb.append("*Убрать визуализацию* <br1>");
            sb.append("<button value=\"Снять\" action=\"bypass _bbsvisual:delete:1\" width=120 height=20 back=\"l2ui_ct1.button.button_df_small_down\" fore=\"l2ui_ct1.button.button_df_small\">");

            sb.append("</td>");
            sb.append("</tr>");
            sb.append("</table>");

            if (VisualConfig.size() / 2 > count_on_page) {
                sb.append("<center><table width=330 border=0><tr><td width=200 height=20 align=center>Страница:</td></tr></table><table width=330 border=0><tr>");
                int pages = (VisualConfig.size() / 2) / count_on_page + 1;
                int count_to_line = 1;
                for (int cur = 1; cur < pages; cur++) {
                    if (page == cur) {
                        sb.append("<td width=24 align=center>[").append(cur).append("]</td>");
                    } else {
                        sb.append("<td width=20 align=center><button value=\"").append(cur).append("\" action=\"bypass _bbsvisual:list:").append(cur).append("\" width=20 height=20 back=\"L2UI_ct1.button_df_down\" fore=\"L2UI_ct1.button_df\"></td>");
                    }
                    if (count_to_line == 14) {
                        sb.append("</tr><tr>");
                        count_to_line = 0;
                    }
                    count_to_line++;
                }
                sb.append("</tr></table></center><br>");
            }

            html = html.replace("%visuallist%", sb.toString());
            ShowBoard.separateAndSend(html, player);
        } else if (action.startsWith("buy-")) {
            StringTokenizer list = new StringTokenizer(bypass, "-");
            list.nextToken();
            int visId = list.hasMoreTokens() ? Integer.parseInt(list.nextToken()) : 1;

            ItemInstance chestItem = null;
            ItemInstance weapon1Item = null;
            ItemInstance weapon2Item = null;

            if (VisualConfig.getVisualConfigId(visId).isArmor) {
                chestItem = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_CHEST);
                if (chestItem != null) {
                    if (!player.getInventory().destroyItemByItemId(VisualConfig.getVisualConfigId(visId).ITEM_ID, VisualConfig.getVisualConfigId(visId).PRICE)) {
                        if (VisualConfig.getVisualConfigId(visId).ITEM_ID == 57) {
                            player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
                        } else {
                            player.sendPacket(SystemMsg.INCORRECT_ITEM_COUNT);
                        }
                        return;
                    }

                    player.sendPacket(SystemMessage2.removeItems(VisualConfig.getVisualConfigId(visId).ITEM_ID, VisualConfig.getVisualConfigId(visId).PRICE));
                    player.getInventory().unEquipItem(chestItem);

                    chestItem.setVisualItemId(visId);
                    chestItem.setJdbcState(JdbcEntityState.UPDATED);
                    chestItem.update();
                    player.getInventory().equipItem(chestItem);
                    player.getInventory().refreshEquip();
                    player.sendPacket(new InventoryUpdate().addModifiedItem(chestItem));
                    player.broadcastCharInfo();
                } else {
                    player.sendMessage("Необходимо одеть предмет");
                }

            } else {
                weapon1Item = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_RHAND);
                weapon2Item = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_LHAND);
                if (weapon1Item != null) {
                    if (!player.getInventory().destroyItemByItemId(VisualConfig.getVisualConfigId(visId).ITEM_ID, VisualConfig.getVisualConfigId(visId).PRICE)) {
                        if (VisualConfig.getVisualConfigId(visId).ITEM_ID == 57) {
                            player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
                        } else {
                            player.sendPacket(SystemMsg.INCORRECT_ITEM_COUNT);
                        }
                        return;
                    }

                    player.sendPacket(SystemMessage2.removeItems(VisualConfig.getVisualConfigId(visId).ITEM_ID, VisualConfig.getVisualConfigId(visId).PRICE));
                    player.getInventory().unEquipItem(weapon1Item);

                    weapon1Item.setVisualItemId(visId);
                    weapon1Item.setJdbcState(JdbcEntityState.UPDATED);
                    weapon1Item.update();
                    player.getInventory().equipItem(weapon1Item);
                    player.getInventory().refreshEquip();
                    player.sendPacket(new InventoryUpdate().addModifiedItem(weapon1Item));
                    player.broadcastCharInfo();
                } else if (weapon2Item != null) {
                    if (!player.getInventory().destroyItemByItemId(VisualConfig.getVisualConfigId(visId).ITEM_ID, VisualConfig.getVisualConfigId(visId).PRICE)) {
                        if (VisualConfig.getVisualConfigId(visId).ITEM_ID == 57) {
                            player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
                        } else {
                            player.sendPacket(SystemMsg.INCORRECT_ITEM_COUNT);
                        }
                        return;
                    }

                    player.sendPacket(SystemMessage2.removeItems(VisualConfig.getVisualConfigId(visId).ITEM_ID, VisualConfig.getVisualConfigId(visId).PRICE));
                    player.getInventory().unEquipItem(weapon2Item);

                    weapon2Item.setVisualItemId(visId);
                    weapon2Item.setJdbcState(JdbcEntityState.UPDATED);
                    weapon2Item.update();
                    player.getInventory().equipItem(weapon2Item);
                    player.getInventory().refreshEquip();
                    player.sendPacket(new InventoryUpdate().addModifiedItem(weapon2Item));
                    player.broadcastCharInfo();
                } else {
                    player.sendMessage("Необходимо одеть предмет");
                }
            }
            player.broadcastCharInfo();
            onBypassCommand(player, "_bbsvisual:list");
        } else if (action.startsWith("delete")) {

            String pag = st.hasMoreTokens() ? st.nextToken() : "1";
            //String pag = st.hasMoreTokens() ? st.nextToken() : "1";
            //_log.info("pages=" + pag);
            page = Integer.parseInt(pag);

            List<ItemInstance> list = new ArrayList<>();
            for (ItemInstance item : player.getInventory().getItems()) {
                if (item.isVisualItem()) {
                    list.add(item);
                }
            }
            if (!list.isEmpty()) {
                String name = "None Name";
                String html = HtmCache.getInstance().getHtml(Config.BBS_HOME_DIR + "pages/visual.htm", player);

                StringBuilder sb = new StringBuilder("");
                sb.append("<table height=400 width=647>");
                sb.append("<tr>");
                sb.append("<td align=center width=320>");

                int k = 0;

                if (list.size() / 2 <= count_on_page) {
                    page = 1;
                }
                // Таблица
                for (int i = 0; i < list.size(); i = i + 2) {
                    if (k < (page * count_on_page) && k >= ((page - 1) * count_on_page)) {
                        ItemInstance itemVis = player.getInventory().getItemByObjectId(list.get(i).getObjectId());
                        sb.append("<table height=60 width=320 background=\"L2UI_CT1.Windows_DF_TooltipBG\">");
                        sb.append("<tr>");
                        sb.append("<td height=32 width=32>");
                        sb.append("<img src=").append(itemVis.getTemplate().getIcon()).append(" width=32 height=32>");
                        sb.append("</td>");
                        sb.append("<td align=center width=320 height=50>");
                        sb.append(itemVis.getName()).append(" <br1>");
                        sb.append("Цена ").append(PRICE_COUNT).append(" ").append(ItemHolder.getInstance().getTemplate(PRICE_ID).getName()).append("<br1>");

                        sb.append("<button value=\"Снять\" action=\"bypass _bbsvisual:del-").append(itemVis.getObjectId()).append("\" width=60 height=20 back=\"l2ui_ct1.button.button_df_small_down\" fore=\"l2ui_ct1.button.button_df_small\">");
                        sb.append("</td>");

                        sb.append("</tr>");
                        sb.append("</table>");
                    }
                    k++;
                }
                sb.append("</td>");

                sb.append("<td align=center width=320>");

                k = 0;
                // Таблица
                if (list.size() >= 2) {
                    for (int i = 1; i < list.size(); i = i + 2) {
                        if (k < (page * count_on_page) && k >= ((page - 1) * count_on_page)) {
                            ItemInstance itemVis = player.getInventory().getItemByObjectId(list.get(i).getObjectId());
                            sb.append("<table height=60 width=320 background=\"L2UI_CT1.Windows_DF_TooltipBG\">");
                            sb.append("<tr>");
                            sb.append("<td height=32 width=32>");
                            sb.append("<img src=").append(itemVis.getTemplate().getIcon()).append(" width=32 height=32>");
                            sb.append("</td>");
                            sb.append("<td align=center width=320 height=50>");
                            sb.append(itemVis.getName()).append(" <br1>");
                            sb.append("Цена ").append(PRICE_COUNT).append(" ").append(ItemHolder.getInstance().getTemplate(PRICE_ID).getName()).append("<br1>");

                            sb.append("<button value=\"Снять\" action=\"bypass _bbsvisual:del-").append(itemVis.getObjectId()).append("\" width=60 height=20 back=\"l2ui_ct1.button.button_df_small_down\" fore=\"l2ui_ct1.button.button_df_small\">");
                            sb.append("</td>");

                            sb.append("</tr>");
                            sb.append("</table>");
                        }
                    }
                    k++;
                }
                sb.append("</td>");
                sb.append("</tr>");
                sb.append("</table>");

                if (list.size() / 2 > count_on_page) {
                    sb.append("<center><table width=330 border=0><tr><td width=200 height=20 align=center>Страница:</td></tr></table><table width=330 border=0><tr>");
                    int pages = (list.size() / 2) / count_on_page + 1;
                    int count_to_line = 1;
                    for (int cur = 1; cur < pages; cur++) {
                        if (page == cur) {
                            sb.append("<td width=24 align=center>[").append(cur).append("]</td>");
                        } else {
                            sb.append("<td width=20 align=center><button value=\"").append(cur).append("\" action=\"bypass _bbsvisual:list:").append(cur).append("\" width=20 height=20 back=\"L2UI_ct1.button_df_down\" fore=\"L2UI_ct1.button_df\"></td>");
                        }
                        if (count_to_line == 14) {
                            sb.append("</tr><tr>");
                            count_to_line = 0;
                        }
                        count_to_line++;
                    }
                    sb.append("</tr></table></center><br>");
                }

                html = html.replace("%visuallist%", sb.toString());
                ShowBoard.separateAndSend(html, player);
            } else {
                player.sendMessage("Нечего снимать");
                onBypassCommand(player, "_bbsvisual:list");
            }
        } else if (action.startsWith("del-")) {
            StringTokenizer list = new StringTokenizer(bypass, "-");
            list.nextToken();
            int visId = list.hasMoreTokens() ? Integer.parseInt(list.nextToken()) : 1;

            ItemInstance itemVis = player.getInventory().getItemByObjectId(visId);
            if (itemVis != null) {
                if (!player.getInventory().destroyItemByItemId(PRICE_ID, PRICE_COUNT)) {
                    if (PRICE_ID == 57) {
                        player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
                    } else {
                        player.sendPacket(SystemMsg.INCORRECT_ITEM_COUNT);
                    }
                    return;
                }

                player.sendPacket(SystemMessage2.removeItems(PRICE_ID, PRICE_COUNT));
                player.getInventory().unEquipItem(itemVis);
				
				int _visualId = itemVis.getVisualItemId();
				int _visualItemToBack = VisualConfig.getVisualConfigId(_visualId).ITEM_ID;
				long _visualItemCountToBack = VisualConfig.getVisualConfigId(_visualId).PRICE;
				player.getInventory().addItem(_visualItemToBack, (long) Math.round((_visualItemCountToBack * 60) / 100));

                itemVis.setVisualItemId(0);
                itemVis.setJdbcState(JdbcEntityState.UPDATED);
                itemVis.update();
                player.getInventory().equipItem(itemVis);
                player.getInventory().refreshEquip();
                player.sendPacket(new InventoryUpdate().addModifiedItem(itemVis));
                player.broadcastCharInfo();
            } else {
                player.sendMessage("Необходимо одеть предмет");
            }

            onBypassCommand(player, "_bbsvisual:list");
        }
    }

    @Override
    public void onWriteCommand(Player player, String bypass, String arg1, String arg2, String arg3, String arg4, String arg5) {
    }

}

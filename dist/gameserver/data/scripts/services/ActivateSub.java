package services;

import l2p.gameserver.Config;
import l2p.gameserver.cache.Msg;
import l2p.gameserver.data.htm.HtmCache;
import l2p.gameserver.instancemanager.QuestManager;
import l2p.gameserver.data.xml.holder.ItemHolder;
import l2p.gameserver.model.Player;
import l2p.gameserver.model.base.Race;
import l2p.gameserver.serverpackets.components.SystemMsg;
import l2p.gameserver.scripts.Functions;
import l2p.gameserver.templates.item.ItemTemplate;
import l2p.gameserver.model.quest.Quest;
import l2p.gameserver.model.quest.QuestState;

public class ActivateSub extends Functions {

    public void get() {
        Player player = getSelf();
        if (player == null) {
            return;
        }

        if (!Config.SERVICES_ACTIVATE_SUB) {
            show("Сервис отключен.", player);
            return;
        }

        if (player.getLevel() < 75) {
            player.sendMessage("Вы должны быть минимум 75 уровня.");
            return;
        }

        if (player.getInventory().destroyItemByItemId(Config.SERVICES_ACTIVATE_SUB_ITEM, Config.SERVICES_ACTIVATE_SUB_PRICE)) {
            if (makeSubQuests()) {
                player.sendMessage("Саб классы Активированы!");
            } else {
                player.sendMessage("Саб классы уже Активированы.");
            }
        } else if (Config.SERVICES_ACTIVATE_SUB_ITEM == 57) {
            player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
        } else {
            player.sendPacket(SystemMsg.INCORRECT_ITEM_COUNT);
        }

        show();
    }

    public boolean makeSubQuests() {
        Player player = getSelf();
        if (player == null) {
            return false;
        }

        if (SubQuestComplete()) {
            return false;
        }

        Quest q = QuestManager.getQuest("_234_FatesWhisper");
        QuestState qs = player.getQuestState(q.getClass());
        if (qs != null) {
            qs.exitCurrentQuest(true);
        }
        q.newQuestState(player, Quest.COMPLETED);

        if (player.getRace() == Race.kamael) {
            q = QuestManager.getQuest("_236_SeedsOfChaos");
            qs = player.getQuestState(q.getClass());
            if (qs != null) {
                qs.exitCurrentQuest(true);
            }
            q.newQuestState(player, Quest.COMPLETED);
        } else {
            q = QuestManager.getQuest("_235_MimirsElixir");
            qs = player.getQuestState(q.getClass());
            if (qs != null) {
                qs.exitCurrentQuest(true);
            }
            q.newQuestState(player, Quest.COMPLETED);
        }

        return true;
    }

    public boolean SubQuestComplete() {
        Player player = getSelf();
        if (player == null) {
            return true;
        }

        Quest q = QuestManager.getQuest("_234_FatesWhisper");
        QuestState qs = player.getQuestState(q.getClass());
        if (qs != null) {
            if (player.getRace() == Race.kamael) {
                q = QuestManager.getQuest("_236_SeedsOfChaos");
                qs = player.getQuestState(q.getClass());
                if (qs != null) {
                    return true;
                }
            } else {
                q = QuestManager.getQuest("_235_MimirsElixir");
                qs = player.getQuestState(q.getClass());
                if (qs != null) {
                    return true;
                }
            }
        }
        return false;
    }

    public void show() {
        Player player = getSelf();
        if (player == null) {
            return;
        }

        if (!Config.SERVICES_ACTIVATE_SUB) {
            show(HtmCache.getInstance().getHtml("npcdefault.htm", player), player);
            return;
        }

        ItemTemplate item = ItemHolder.getInstance().getTemplate(Config.SERVICES_ACTIVATE_SUB_ITEM);

        String out = "";


        out += "<html><body>Активация сабклассов";
        out += "<br><br><table>";
        out += "<tr><td>Стоимость:" + Config.SERVICES_ACTIVATE_SUB_PRICE + " " + item.getName() + "</td></tr>";
        out += "</table><br><br>";
        if (!SubQuestComplete()) {
            out += "<button width=100 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\" action=\"bypass -h scripts_services.ActivateSub:get\" value=\"Активировать\">";
        } else {
            out += "Сабклассы уже активированы.";
        }
        out += "</body></html>";

        show(out, player);
    }
}
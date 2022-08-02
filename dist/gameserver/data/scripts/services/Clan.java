package services;

import l2p.gameserver.Config;
import l2p.gameserver.cache.Msg;
import l2p.gameserver.data.xml.holder.ItemHolder;
import l2p.gameserver.model.Player;
import l2p.gameserver.model.pledge.UnitMember;
import l2p.gameserver.scripts.Functions;
import l2p.gameserver.serverpackets.MagicSkillUse;
import l2p.gameserver.serverpackets.PledgeShowInfoUpdate;
import l2p.gameserver.serverpackets.SystemMessage2;
import l2p.gameserver.serverpackets.components.CustomMessage;
import l2p.gameserver.serverpackets.components.SystemMsg;
import l2p.gameserver.utils.SiegeUtils;

public class Clan extends Functions {

    public void getPoints(String[] param) {
        Player player = getSelf();
        if (player == null) {
            return;
        }

        if (!Config.SERVICES_CLAN_BUY_POINTS_ENABLED) {
            show(new CustomMessage("scripts.services.TurnOff", player), player);
            return;
        }

        if (player.getClan() == null) {
            player.sendMessage("Вы должны быть в клане.");
            return;
        }

        int n = Integer.parseInt(param[0]);
        int countCRP = Config.SERVICES_CLAN_BUY_POINTS_PRICE[n][0];
        int price = Config.SERVICES_CLAN_BUY_POINTS_PRICE[n][1];

        if (Functions.getItemCount(player, Config.SERVICES_CLAN_BUY_POINTS_ITEM) < price) {
            player.sendPacket(SystemMsg.INCORRECT_ITEM_COUNT);
            return;
        }

        if (player.getInventory().destroyItemByItemId(Config.SERVICES_CLAN_BUY_POINTS_ITEM, price)) {
            player.sendPacket(SystemMessage2.removeItems(Config.SERVICES_CLAN_BUY_POINTS_ITEM, price));
            player.getClan().incReputation(countCRP, false, "ClanService");
            player.getClan().broadcastToOnlineMembers(new PledgeShowInfoUpdate(player.getClan()));
            player.sendMessage(new CustomMessage("scripts.services.Clan.Rep", player).addNumber(countCRP));
        }
    }

    public void levelUpClan(String[] param) {
        Player player = getSelf();
        if (player == null) {
            return;
        }

        if (!Config.SERVICES_CLAN_BUY_LEVEL_ENABLED) {
            show(new CustomMessage("scripts.services.TurnOff", player), player);
            return;
        }

        if (player.getClan() == null) {
            player.sendMessage("Вы должны быть в клане.");
            return;
        }

        int new_level = Integer.parseInt(param[0]);
        int diff = new_level - player.getClan().getLevel();
        int price = Config.SERVICES_CLAN_BUY_LEVEL_PRICE[new_level - 1];

        if (Functions.getItemCount(player, Config.SERVICES_CLAN_BUY_LEVEL_ITEM) < price) {
            player.sendPacket(SystemMsg.INCORRECT_ITEM_COUNT);
            return;
        }

        if (player.getInventory().destroyItemByItemId(Config.SERVICES_CLAN_BUY_LEVEL_ITEM, price)) {
            player.sendPacket(SystemMessage2.removeItems(Config.SERVICES_CLAN_BUY_LEVEL_ITEM, price));
            player.getClan().setLevel((byte) (player.getClan().getLevel() + diff));
            player.getClan().updateClanInDB();
            player.broadcastPacket(new MagicSkillUse(player, player, 5103, 1, 1000, 0));

            if (player.getClan().getLevel() >= 5) {
                SiegeUtils.addSiegeSkills(player);
            }

            if (player.getClan().getLevel() == 5) {
                player.sendPacket(Msg.NOW_THAT_YOUR_CLAN_LEVEL_IS_ABOVE_LEVEL_5_IT_CAN_ACCUMULATE_CLAN_REPUTATION_POINTS);
            }

            // notify all the members about it
            final PledgeShowInfoUpdate pu = new PledgeShowInfoUpdate(player.getClan());
            for (UnitMember mbr : player.getClan().getAllMembers()) {
                if (mbr.isOnline()) {
                    mbr.getPlayer().updatePledgeClass();
                    mbr.getPlayer().sendPacket(Msg.CLANS_SKILL_LEVEL_HAS_INCREASED);
                    mbr.getPlayer().sendPacket(pu);
                    mbr.getPlayer().broadcastUserInfo(true);
                }
            }
        }
    }

    public void points_page() {
        Player player = getSelf();
        if (player == null) {
            return;
        }

        if (!Config.SERVICES_CLAN_BUY_POINTS_ENABLED) {
            show(new CustomMessage("scripts.services.TurnOff", player), player);
            return;
        }

        if (player.getClan() == null) {
            player.sendMessage("Вы должны быть в клане.");
            return;
        }

        // Уровень клана должен быть 5 или больше.
        if (player.getClan().getLevel() < 5) {
            player.sendMessage(new CustomMessage("scripts.services.Clan.MinLvl", player));
            return;
        }

        String item_name = ItemHolder.getInstance().getTemplate(Config.SERVICES_CLAN_BUY_POINTS_ITEM).getName();

        String append = "Покупка очков репутации клана:";
        append += "<br>";
        append += "Клан " + player.getClan().getName() + ": уровень <font color=\"LEVEL\">" + player.getClan().getLevel() + "</font>, CRP <font color=\"LEVEL\">" + player.getClan().getReputationScore() + "</font>";
        append += "<br>";
        append += "<table>";
        append += "<tr><td><center>CRP</center></td><td><center>Цена</center></td></tr>";
        for (int i = 0; i < Config.SERVICES_CLAN_BUY_POINTS_PRICE.length; i++) {
            append
                    += "<tr>"
                    + "<td><font color=\"FF9900\">" + Config.SERVICES_CLAN_BUY_POINTS_PRICE[i][0] + "</font> CRP</td>"
                    + "<td><font color=\"FF9900\">" + Config.SERVICES_CLAN_BUY_POINTS_PRICE[i][1] + "</font> " + item_name + "</td>"
                    + "<td>" + "<button value=\"Купить\" action=\"bypass -h scripts_services.Clan:getPoints " + i + "\" width=60 height=20 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>"
                    + "</tr>";
        }

        append += "</table>";
        show(append, player);
    }

    public void level_page() {
        Player player = getSelf();
        if (player == null) {
            return;
        }

        if (!Config.SERVICES_CLAN_BUY_LEVEL_ENABLED) {
            show(new CustomMessage("scripts.services.TurnOff", player), player);
            return;
        }

        if (player.getClan() == null) {
            player.sendMessage("Вы должны быть в клане.");
            return;
        }

        String item_name = ItemHolder.getInstance().getTemplate(Config.SERVICES_CLAN_BUY_LEVEL_ITEM).getName();
        int level = player.getClan().getLevel();

        String append = "Покупка уровня клана:";
        append += "<br>";
        append += "Клан " + player.getClan().getName() + ": уровень <font color=\"LEVEL\">" + level + "</font>";
        append += "<br>";
        append += "<table>";
        append += "<tr><td><center>Уровень</center></td><td><center>Цена</center></td></tr>";
        for (int i = level; i < Config.SERVICES_CLAN_BUY_LEVEL_PRICE.length; i++) {
            int new_level = i + 1;
            append
                    += "<tr>"
                    + "<td>Уровень <font color=\"FF9900\">" + new_level + "</font></td>"
                    + "<td><font color=\"FF9900\">" + Config.SERVICES_CLAN_BUY_LEVEL_PRICE[i] + "</font> " + item_name + "</td>"
                    + "<td>" + "<button value=\"Купить\" action=\"bypass -h scripts_services.Clan:levelUpClan " + new_level + "\" width=60 height=20 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>"
                    + "</tr>";
        }

        append += "</table>";
        show(append, player);
    }
}

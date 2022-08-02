package services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import l2p.commons.dbutils.DbUtils;
import l2p.gameserver.Config;
import l2p.gameserver.data.htm.HtmCache;
import l2p.gameserver.data.xml.holder.ItemHolder;
import l2p.gameserver.database.DatabaseFactory;
import l2p.gameserver.model.Player;
import l2p.gameserver.scripts.Functions;
import l2p.gameserver.scripts.ScriptFile;

public class Account extends Functions implements ScriptFile {

    public void show() {
        Player player = (Player) getSelf();
        if (player == null) {
            return;
        }
        if (!Config.SERVICES_ACC_MOVE_ENABLED) {
            show(HtmCache.getInstance().getHtml("npcdefault.htm", player), player);
            return;
        }
        String append = player.isLangRus() ? "Перенос персонажей между аккаунтами.<br>" : "Transfer characters between accounts.<br>";
        append += player.isLangRus() ? "Цена: " + Config.SERVICES_ACC_MOVE_PRICE + " " + ItemHolder.getInstance().getTemplate(Config.SERVICES_ACC_MOVE_ITEM).getName() + ".<br>" : "Price: " + Config.SERVICES_ACC_MOVE_PRICE + " " + ItemHolder.getInstance().getTemplate(Config.SERVICES_ACC_MOVE_ITEM).getName() + ".<br>";
        append += player.isLangRus() ? "Внимание !!! При переносе персонажа на другой аккаунт, убедитесь что персонажей там меньше чем 7, иначе могут возникнуть непредвиденные ситуации за которые Администрация не отвечает.<br>" : "Attention! When you transfer a character to another account, make sure that the characters there is less than 7, or there might be unforeseen situations for which the Administration is not responsible.<br>";
        append += player.isLangRus() ? "Внимательно вводите логин куда переносите, администрация не возвращает персонажей." : "Carefully enter the username to transfer, the administration does not return characters.";
        append += player.isLangRus() ? "Внимание !!! После успешного переноса персонажа на другой аккаунт клиент закроется!!!!." : "Attention! After a successful migration character to another customer's account will be closed!!.";
        append += player.isLangRus() ? "Вы переносите персонажа " + player.getName() + ", на какой аккаунт его перенести ?" : "You transfer your character " + player.getName() + ", on which account to transfer it?";
        append += "<edit var=\"new_acc\" width=150>";
        append += player.isLangRus() ? "<button value=\"Перенести\" action=\"bypass -h scripts_services.Account:NewAccount $new_acc\" width=150 height=15><br>" : "<button value=\"Transfer\" action=\"bypass -h scripts_services.Account:NewAccount $new_acc\" width=150 height=15><br>";
        show(append, player, null);

    }

    public void NewAccount(String[] name) {
        Player player = (Player) getSelf();
        if (player == null) {
            return;
        }
        if (!Config.SERVICES_ACC_MOVE_ENABLED) {
            show(HtmCache.getInstance().getHtml("npcdefault.htm", player), player);
            return;
        }
        if (player.getInventory().getCountOf(Config.SERVICES_ACC_MOVE_ITEM) < Config.SERVICES_ACC_MOVE_PRICE) {
            player.sendMessage(player.isLangRus() ? "У вас нету " + Config.SERVICES_ACC_MOVE_PRICE + " " + ItemHolder.getInstance().getTemplate(Config.SERVICES_ACC_MOVE_ITEM) : "You have no " + Config.SERVICES_ACC_MOVE_PRICE + " " + ItemHolder.getInstance().getTemplate(Config.SERVICES_ACC_MOVE_ITEM));
            show();
            return;
        }
        String _name = name[0];
        Connection con = null;
        Connection conGS = null;
        PreparedStatement offline = null;
        Statement statement = null;
        ResultSet rs = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            offline = con.prepareStatement("SELECT `login` FROM `accounts` WHERE `login` = ?");
            offline.setString(1, _name);
            rs = offline.executeQuery();
            if (rs.next()) {
                removeItem(player, Config.SERVICES_ACC_MOVE_ITEM, Config.SERVICES_ACC_MOVE_PRICE);
                conGS = DatabaseFactory.getInstance().getConnection();
                statement = conGS.createStatement();
                statement.executeUpdate("UPDATE `characters` SET `account_name` = '" + _name + "' WHERE `char_name` = '" + player.getName() + "'");
                player.sendMessage(player.isLangRus() ? "Персонаж успешно перенесен." : "Character is successfully transferred.");
                player.logout();
            } else {
                player.sendMessage(player.isLangRus() ? "Введенный аккаунт не найден." : "The entered account is not found.");
                show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return;
        } finally {
            DbUtils.closeQuietly(con, offline, rs);
            DbUtils.closeQuietly(conGS, statement);
        }
    }

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }
}
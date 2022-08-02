package services;

import java.util.Date;
import l2p.gameserver.Config;
import l2p.gameserver.dao.AccountBonusDAO;
import l2p.gameserver.data.htm.HtmCache;
import l2p.gameserver.data.xml.holder.ItemHolder;
import l2p.gameserver.listener.actor.player.OnAnswerListener;
import l2p.gameserver.loginservercon.AuthServerCommunication;
import l2p.gameserver.loginservercon.gspackets.BonusRequest;
import l2p.gameserver.model.Player;
import l2p.gameserver.model.actor.instances.player.Bonus;
import l2p.gameserver.model.premium.Configs;
import l2p.gameserver.model.premium.PremiumConfig;
import l2p.gameserver.scripts.Functions;
import l2p.gameserver.serverpackets.ConfirmDlg;
import l2p.gameserver.serverpackets.ExBR_PremiumState;
import l2p.gameserver.serverpackets.MagicSkillUse;
import l2p.gameserver.serverpackets.SystemMessage2;
import l2p.gameserver.serverpackets.components.CustomMessage;
import l2p.gameserver.serverpackets.components.SystemMsg;
import l2p.gameserver.utils.Log;

public class RateBonus extends Functions {

    public int i;

    public void list() {
        Player player = getSelf();
        if (Config.SERVICES_RATE_TYPE == Bonus.NO_BONUS) {
            show(HtmCache.getInstance().getHtml("npcdefault.htm", player), player);
            return;
        }

        String html;
        if (player.getNetConnection().getBonus() >= 0.) {
            int endtime = player.getNetConnection().getBonusExpire();
            if (endtime >= System.currentTimeMillis() / 1000L) {
                html = HtmCache.getInstance().getHtml("scripts/services/RateBonusAlready.htm", player).replaceFirst("endtime", new Date(endtime * 1000L).toString());
            } else {
                html = HtmCache.getInstance().getHtml("scripts/services/RateBonus.htm", player);

                String add = "";
                for (Configs config : PremiumConfig._configs) {
                    add += "<a action=\"bypass -h scripts_services.RateBonus:get " + config.ID + "\">" //
                            + (int) (config.RATE_VALUE * 100) + //
                            "% for " + config.TIME + //
                            " days - " + config.PRICE + //
                            " " + ItemHolder.getInstance().getTemplate(config.ITEM_ID).getName() + "</a><br>";
                }

                html = html.replaceFirst("%toreplace%", add);
            }
        } else {
            html = HtmCache.getInstance().getHtml("scripts/services/RateBonusNo.htm", player);
        }

        show(html, player);
    }

    public void get(String[] param) {
        Player player = getSelf();
        if (Config.SERVICES_RATE_TYPE == Bonus.NO_BONUS) {
            show(HtmCache.getInstance().getHtml("npcdefault.htm", player), player);
            return;
        }
        i = Integer.parseInt(param[0]);
        int endtime = player.getNetConnection().getBonusExpire();
        if (endtime >= System.currentTimeMillis() / 1000L) {
            player.sendMessage("Вы уже благословлены! Приходите когда действие благословения окончится.");
            return;
        }

        if (PremiumConfig.getPremConfigId(i).ITEM_ID == 57 && player.getAdena() < PremiumConfig.getPremConfigId(i).PRICE) {
            player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
            return;
        } else if (Functions.getItemCount(player, PremiumConfig.getPremConfigId(i).ITEM_ID) < PremiumConfig.getPremConfigId(i).PRICE) {
            player.sendPacket(SystemMsg.INCORRECT_ITEM_COUNT);
            return;
        }

        if (Config.SERVICES_RATE_TYPE == Bonus.BONUS_GLOBAL_ON_AUTHSERVER && AuthServerCommunication.getInstance().isShutdown()) {
            list();
            return;
        }

        player.ask(new ConfirmDlg(SystemMsg.S1, 30000).addString(new CustomMessage("scripts.services.RateBonus.AskPlayer", player, new Object[0]).toString()), new OnAnswerListener() {
            @Override
            public void sayYes() {
                buypa(i);
            }

            @Override
            public void sayNo() {
                //
            }
        });

        //получение бонуса
        //    player.scriptRequest(new CustomMessage("scripts.services.RateBonus.AskPlayer", player, new Object[0]).toString(), "services.RateBonus:buypa" + i, new Object[0]);
    }

    public void buypa(int i) {
        Player player = getSelf();

        if (!player.getInventory().destroyItemByItemId(PremiumConfig.getPremConfigId(i).ITEM_ID, PremiumConfig.getPremConfigId(i).PRICE)) {
            if (PremiumConfig.getPremConfigId(i).ITEM_ID == 57) {
                player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
            } else {
                player.sendPacket(SystemMsg.INCORRECT_ITEM_COUNT);
            }
            return;
        }
        player.sendPacket(SystemMessage2.removeItems(PremiumConfig.getPremConfigId(i).ITEM_ID, PremiumConfig.getPremConfigId(i).PRICE));
        Log.add(player.getName() + "|" + player.getObjectId() + "|rate bonus|" + PremiumConfig.getPremConfigId(i).RATE_VALUE + "|" + PremiumConfig.getPremConfigId(i).TIME + "|", "services");
        double bonus = PremiumConfig.getPremConfigId(i).RATE_VALUE;
        int bonusExpire = (int) (System.currentTimeMillis() / 1000L) + PremiumConfig.getPremConfigId(i).TIME * 24 * 60 * 60;
        switch (Config.SERVICES_RATE_TYPE) {
            case Bonus.BONUS_GLOBAL_ON_AUTHSERVER:
                AuthServerCommunication.getInstance().sendPacket(new BonusRequest(player.getAccountName(), bonus, bonusExpire));
                break;
            case Bonus.BONUS_GLOBAL_ON_GAMESERVER:
                AccountBonusDAO.getInstance().insert(player.getAccountName(), i, bonusExpire);
                break;
        }
        if (PremiumConfig.getPremConfigId(i).ALLOW_HERO_AURA) {
            player.setHeroAura(true);
            player.broadcastCharInfo();
        }
        
        player.getNetConnection().setBonus(i);
        player.getNetConnection().setBonusExpire(bonusExpire);
        player.stopBonusTask();
        player.startBonusTask();
        if (player.getParty() != null) {
            player.getParty().recalculatePartyData();
        }
        player.broadcastPacket(new MagicSkillUse(player, player, 5662, 1, 0, 0));
        player.sendPacket(new ExBR_PremiumState(player, true));

        show(HtmCache.getInstance().getHtml("scripts/services/RateBonusGet.htm", player), player);
    }
}

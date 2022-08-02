package services;

import l2p.gameserver.Config;
import l2p.gameserver.ThreadPoolManager;
import l2p.gameserver.data.htm.HtmCache;
import l2p.gameserver.data.xml.holder.ItemHolder;
import l2p.gameserver.listener.actor.player.OnPlayerEnterListener;
import l2p.gameserver.model.Player;
import l2p.gameserver.model.actor.listener.CharListenerList;
import l2p.gameserver.model.entity.Hero;
import l2p.gameserver.serverpackets.components.SystemMsg;
import l2p.gameserver.serverpackets.SocialAction;
import l2p.gameserver.scripts.Functions;
import l2p.gameserver.scripts.ScriptFile;
import l2p.gameserver.serverpackets.SkillList;

public class BuyHeroStatus extends Functions implements ScriptFile, OnPlayerEnterListener {

    public void list() {
        Player player = getSelf();
        if (!Config.SERVICES_HERO_SELL_ENABLED) {
            show(HtmCache.getInstance().getHtml("npcdefault.htm", player), player);
            return;
        }
        String html = null;

        html = HtmCache.getInstance().getHtml("scripts/services/BuyHero.htm", player);
        String add = "";
        for (int i = 0; i < Config.SERVICES_HERO_SELL_DAY.length; i++) {
            add += "<a action=\"bypass -h scripts_services.BuyHeroStatus:get " + i + "\">" //
                    + "for " + Config.SERVICES_HERO_SELL_DAY[i] + //
                    " days - " + Config.SERVICES_HERO_SELL_PRICE[i] + //
                    " " + ItemHolder.getInstance().getTemplate(Config.SERVICES_HERO_SELL_ITEM[i]).getName() + "</a><br>";
        }
        html = html.replaceFirst("%toreplace%", add);


        show(html, player);
    }

    public void get(String[] param) {
        Player player = getSelf();
        if (!Config.SERVICES_HERO_SELL_ENABLED) {
            show(HtmCache.getInstance().getHtml("npcdefault.htm", player), player);
            return;
        }
        if (!player.isNoble()) {
            show("Необходимо получить дворянство.", player);
            return;
        }
        
        if (player.isHero()) {
            show("Вы уже являетесь героем.", player);
            return;
        }
        
        int i = Integer.parseInt(param[0]);
        
        if (player.getInventory().destroyItemByItemId(Config.SERVICES_HERO_SELL_ITEM[i], Config.SERVICES_HERO_SELL_PRICE[i])) {
            player.setHero(true);
            Hero.addSkills(player);
            player.updatePledgeClass();
            player.sendPacket(new SkillList(player));
            player.broadcastUserInfo(true);
            player.setVar("HeroPeriod", String.valueOf(System.currentTimeMillis() + 60 * 1000 * 60 * 24 * Config.SERVICES_HERO_SELL_DAY[i]), -1);
            ThreadPoolManager.getInstance().schedule(new EndHero(player), System.currentTimeMillis() + (60 * 1000 * 60 * 24 * Config.SERVICES_HERO_SELL_DAY[i]));
            player.broadcastPacket(new SocialAction(player.getObjectId(), 16));
        } else
            player.sendPacket(SystemMsg.INCORRECT_ITEM_COUNT);
    }

    @Override
    public void onLoad() {
        CharListenerList.addGlobal(this);
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    @Override
    public void onPlayerEnter(Player player) {
        long time = player.getVarLong("HeroPeriod", 0) - System.currentTimeMillis();
        if (time > 0) {
            player.setHero(true);
            Hero.addSkills(player);
            player.updatePledgeClass();
            player.sendPacket(new SkillList(player));
            player.broadcastUserInfo(true);
            ThreadPoolManager.getInstance().schedule(new EndHero(player), player.getVarLong("HeroPeriod") - System.currentTimeMillis());
        } else
            player.unsetVar("HeroPeriod");
    }
    
    private class EndHero implements Runnable {
        private Player player;

        public EndHero(Player player) {
            this.player = player;
        }

        @Override
        public void run() {
            player.setHero(false);
            Hero.removeSkills(player);
            player.unsetVar("HeroPeriod");
            player.updatePledgeClass();
            player.sendPacket(new SkillList(player));
            player.broadcastUserInfo(true);
        }
    }
}
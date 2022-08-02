package services.community;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringTokenizer;
import l2p.gameserver.Config;
import l2p.gameserver.GameTimeController;
import l2p.gameserver.data.htm.HtmCache;
import l2p.gameserver.data.xml.holder.BuyListHolder;
import l2p.gameserver.data.xml.holder.BuyListHolder.NpcTradeList;
import l2p.gameserver.data.xml.holder.MultiSellHolder;
import l2p.gameserver.handler.bbs.CommunityBoardManager;
import l2p.gameserver.handler.bbs.ICommunityBoardHandler;
import l2p.gameserver.model.GameObjectsStorage;
import l2p.gameserver.model.Player;
import l2p.gameserver.model.Skill;
import l2p.gameserver.model.Zone.ZoneType;
import l2p.gameserver.model.base.TeamType;
import l2p.gameserver.model.entity.olympiad.Olympiad;
import l2p.gameserver.scripts.ScriptFile;
import l2p.gameserver.scripts.Scripts;
import l2p.gameserver.serverpackets.ExBuySellList;
import l2p.gameserver.serverpackets.ShowBoard;
import l2p.gameserver.serverpackets.components.CustomMessage;
import l2p.gameserver.serverpackets.components.SystemMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommunityBoard implements ScriptFile, ICommunityBoardHandler {

    private static final Logger _log = LoggerFactory.getLogger(CommunityBoard.class);
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy HH:mm");

    @Override
    public void onLoad() {
        if (Config.COMMUNITYBOARD_ENABLED) {
            _log.info("CommunityBoard: service loaded.");
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
        return new String[]{"_bbshome", "_bbslink", "_bbsmultisell", "_bbssell", "_bbspage", "_bbsscripts"};
    }

    @Override
    public void onBypassCommand(Player player, String bypass) {
        if (!CheckCondition(player)) {
            return;
        }

        StringTokenizer st = new StringTokenizer(bypass, "_");
        String cmd = st.nextToken();
        String html = "";
        if ("bbshome".equals(cmd)) {
            if (!CheckCondition(player)) {
                return;
            }
            StringTokenizer p = new StringTokenizer(Config.BBS_DEFAULT, "_");
            String dafault = p.nextToken();
            if (dafault.equals(cmd)) {
                html = HtmCache.getInstance().getHtml(Config.BBS_HOME_DIR + "pages/main.htm", player);

                html = html.replaceFirst("%nick%", String.valueOf(player.getName()));
                html = html.replaceFirst("%prof%", String.valueOf(player.isLangRus() ? player.getActiveClass().toStringRuCB() : player.getActiveClass().toStringCB()));
                html = html.replaceFirst("%lvl%", String.valueOf(player.getLevel()));
                html = html.replaceFirst("%clan%", player.getClan() != null ? String.valueOf(player.getClan().getName()) : player.isLangRus() ? "<font color=\"FF0000\">нет</font>" : "<font color=\"FF0000\">none</font>");
                html = html.replaceFirst("%noobl%", player.isNoble() ? String.valueOf(player.isLangRus() ? "да" : "no") : player.isLangRus() ? "<font color=\"FF0000\">требуется саб. 75 ур.</font>" : "<font color=\"FF0000\">required sub 75 lvl</font>");
                html = html.replaceFirst("%time%", String.valueOf(player.getHoursInGames()).concat(player.isLangRus() ? " час" : " hour"));
                html = html.replaceFirst("%premium%", player.hasBonus() ? DATE_FORMAT.format(new Date(player.getBonus().getBonusExpireX())) : player.isLangRus() ? "<font color=\"LEVEL\"><a action=\"bypass _bbsscripts:services.RateBonus:list\">Купить премиум</a></font>" : "<font color=\"LEVEL\"><a action=\"bypass _bbsscripts:services.RateBonus:list\">Byu premium</a></font>");
                html = html.replaceFirst("%servhwid%", player.isLangRus() ? "<a action=\"bypass -h user_lock\">Привязать</a>" : "<a action=\"bypass -h user_lock\">Install</a>");
                html = html.replaceFirst("%servip%", player.isLangRus() ? "<a action=\"bypass -h user_lock\">Привязать</a>" : "<a action=\"bypass -h user_lock\">Install</a>");
                html = html.replaceFirst("%ip%", player.getIP());

                html = html.replaceFirst("%mytime%", getTimeInServer(player));
                html = html.replaceFirst("%online%", String.valueOf(GameObjectsStorage.getAllPlayersCount()));
                html = html.replaceFirst("%trade%", String.valueOf(GameObjectsStorage.getAllTradablePlayersCount()));

                //    html = BbsUtil.htmlAll(html, player);
            } else {
                onBypassCommand(player, Config.BBS_DEFAULT);
                return;
            }
        } else if ("bbslink".equals(cmd)) {
            html = HtmCache.getInstance().getHtml(Config.BBS_HOME_DIR + "bbs_homepage.htm", player);
            //    html = BbsUtil.htmlAll(html, player);
        } else if (bypass.startsWith("_bbspage")) {
            if (!CheckCondition(player)) {
                return;
            }
            //Example: "bypass _bbspage:index".
            String[] b = bypass.split(":");
            String page = b[1];
            html = HtmCache.getInstance().getHtml(Config.BBS_HOME_DIR + "pages/" + page + ".htm", player);
            //    html = BbsUtil.htmlAll(html, player);
        } else if (bypass.startsWith("_bbsmultisell")) {
            if (!CheckCondition(player)) {
                return;
            }

            if (!Config.ALLOW_SELL_IN_COMBAT && (player.getPvpFlag() != 0 || player.isInDuel() || player.isInCombat() || player.isAttackingNow())) {
                if (player.isLangRus()) {
                    player.sendMessage("Во время боя нельзя использовать данную функцию.");
                } else {
                    player.sendMessage("During combat, you can not use this feature.");
                }
                return;
            }

            if (!Config.ALLOW_SELL_IS_IN_EVENT && player.getTeam() != TeamType.NONE) {
                if (player.isLangRus()) {
                    player.sendMessage("Нельзя использовать телепорт во время эвентов.");
                } else {
                    player.sendMessage("You can not use Teleport during Events.");
                }
                return;
            }

            if (!Config.ALLOW_SELL_IS_IN_CURSED_WEPON && player.isCursedWeaponEquipped()) {
                player.sendMessage(player.isLangRus() ? "Вам запрещено пользоваться этой функцией." : "You are not allowed to use this feature.");
                return;
            }

            if (!Config.ALLOW_SELL_IS_IN_SIEGE && player.isInZone(ZoneType.SIEGE)) {
                if (player.isLangRus()) {
                    player.sendMessage("В зоне, находящейся в осаде, использовать телепорт запрещено.");
                } else {
                    player.sendMessage("In the zone, located in the siege, use the teleport is prohibited.");
                }
                return;
            }

            //Example: "_bbsmultisell:10000;_bbspage:index" or "_bbsmultisell:10000;_bbshome" or "_bbsmultisell:10000"...
            StringTokenizer st2 = new StringTokenizer(bypass, ";");
            String[] mBypass = st2.nextToken().split(":");
            String pBypass = st2.hasMoreTokens() ? st2.nextToken() : null;
            if (pBypass != null) {
                ICommunityBoardHandler handler = CommunityBoardManager.getInstance().getCommunityHandler(pBypass);
                if (handler != null) {
                    handler.onBypassCommand(player, pBypass);
                }
            }

            int listId = Integer.parseInt(mBypass[1]);

            for (int i : Config.COMMUNITYBOARD_MULTISELL_ALLOW) {
                if (i == listId) {
                    MultiSellHolder.getInstance().SeparateAndSend(listId, player, Config.COMMUNITYBOARD_MULTISELL_RATE);
                    return;
                }
            }
            player.sendMessage(new CustomMessage("common.Disabled", player));
            return;
        } else if (bypass.startsWith("_bbssell")) {
            if (!CheckCondition(player)) {
                return;
            }
            if (!Config.COMMUNITYBOARD_SELL_ENABLED) {
                return;
            }

            if (!Config.ALLOW_SELL_IN_COMBAT && (player.getPvpFlag() != 0 || player.isInDuel() || player.isInCombat() || player.isAttackingNow())) {
                if (player.isLangRus()) {
                    player.sendMessage("Во время боя нельзя использовать данную функцию.");
                } else {
                    player.sendMessage("During combat, you can not use this feature.");
                }
                return;
            }

            if (!Config.ALLOW_SELL_IS_IN_EVENT && player.getTeam() != TeamType.NONE) {
                if (player.isLangRus()) {
                    player.sendMessage("Нельзя использовать телепорт во время эвентов.");
                } else {
                    player.sendMessage("You can not use Teleport during Events.");
                }
                return;
            }

            if (!Config.ALLOW_SELL_IS_IN_CURSED_WEPON && player.isCursedWeaponEquipped()) {
                player.sendMessage(player.isLangRus() ? "Вам запрещено пользоваться этой функцией." : "You are not allowed to use this feature.");
                return;
            }

            if (!Config.ALLOW_SELL_IS_IN_SIEGE && player.isInZone(ZoneType.SIEGE)) {
                if (player.isLangRus()) {
                    player.sendMessage("В зоне, находящейся в осаде, использовать телепорт запрещено.");
                } else {
                    player.sendMessage("In the zone, located in the siege, use the teleport is prohibited.");
                }
                return;
            }

            StringTokenizer st2 = new StringTokenizer(bypass, ";");
            String[] mBypass = st2.nextToken().split(":");
            String pBypass = st2.hasMoreTokens() ? st2.nextToken() : null;
            if (pBypass != null) {
                ICommunityBoardHandler handler = CommunityBoardManager.getInstance().getCommunityHandler(pBypass);
                if (handler != null) {
                    handler.onBypassCommand(player, pBypass);
                }
            }
            player.setIsBBSUse(true);
            NpcTradeList list = BuyListHolder.getInstance().getBuyList(-1);
            player.sendPacket(new ExBuySellList.BuyList(list, player, 0.), new ExBuySellList.SellRefundList(player, false));
            return;
        } else if (bypass.startsWith("_bbsscripts")) {
            if (!CheckCondition(player)) {
                return;
            }
            //Example: "_bbsscripts:events.GvG.GvG:addGroup;_bbspage:index" or "_bbsscripts:events.GvG.GvG:addGroup;_bbshome" or "_bbsscripts:events.GvG.GvG:addGroup"...
            StringTokenizer st2 = new StringTokenizer(bypass, ";");
            String sBypass = st2.nextToken().substring(12);
            String pBypass = st2.hasMoreTokens() ? st2.nextToken() : null;
            if (pBypass != null) {
                ICommunityBoardHandler handler = CommunityBoardManager.getInstance().getCommunityHandler(pBypass);
                if (handler != null) {
                    handler.onBypassCommand(player, pBypass);
                }
            }

            String[] word = sBypass.split("\\s+");
            String[] args = sBypass.substring(word[0].length()).trim().split("\\s+");
            String[] path = word[0].split(":");
            if (path.length != 2) {
                return;
            }

            Scripts.getInstance().callScripts(player, path[0], path[1], word.length == 1 ? new Object[]{} : new Object[]{args});
            return;
        }

        ShowBoard.separateAndSend(html, player);
    }

    private boolean CheckCondition(Player player) {
        if (player == null) {
            return false;
        }

        if (player.isDead()) {
            return false;
        }

        if (!Config.ALLOW_COMMUNITYBOARD_IS_IN_SIEGE && player.isInZone(ZoneType.SIEGE)) {
            if (player.isLangRus()) {
                player.sendMessage("В зоне, находящейся в осаде, использовать запрещено.");
            } else {
                player.sendMessage("In the zone, located in the siege, use prohibited.");
            }
            return false;
        }

        if (!Config.ALLOW_COMMUNITYBOARD_IN_COMBAT && (player.getPvpFlag() != 0 || player.isInDuel() || player.isInCombat() || player.isAttackingNow() || player.isCastingNow())) {
            if (player.isLangRus()) {
                player.sendMessage("Во время боя нельзя использовать данную функцию.");
            } else {
                player.sendMessage("During combat, you can not use this feature.");
            }
            return false;
        }

        if (!Config.ALLOW_COMMUNITYBOARD_IS_IN_EVENT && (player.getTeam() != TeamType.NONE || player.getEffectList().getEffectsBySkillId(Skill.SKILL_RAID_CURSE) != null)) {
            player.sendMessage("Нельзя использовать данную функцию во время эвентов.");
            return false;
        }

        if (!Config.ALLOW_COMMUNITYBOARD_IS_IN_CURSED_WEAPON && (player.isCursedWeaponEquipped())) {
            player.sendMessage("Нельзя использовать данную функцию во время эвентов.");
            return false;
        }

        if (player.isInObserverMode() || player.isInOlympiadMode() || player.getOlympiadGame() != null || Olympiad.isRegistered(player)) {
            player.sendMessage(player.isLangRus() ? "Во время олимпийского боя нельзя использовать данную функцию." : "During the Olympic battle you can not use this feature.");
            return false;
        }

        if (player.isTerritoryFlagEquipped()) {
            player.sendPacket(SystemMsg.YOU_CANNOT_TELEPORT_WHILE_IN_POSSESSION_OF_A_WARD);
            return false;
        }
        return true;
    }

    @Override
    public void onWriteCommand(Player player, String bypass, String arg1, String arg2, String arg3, String arg4, String arg5) {
    }

    private String getTimeInServer(Player player) {
        String strH, strM;
        int h = GameTimeController.getInstance().getGameHour();
        int m = GameTimeController.getInstance().getGameMin();
        String nd;
        if (GameTimeController.getInstance().isNowNight()) {
            nd = player.isLangRus() ? "Ночь." : "Night.";
        } else {
            nd = player.isLangRus() ? "День." : "Day.";
        }
        if (h < 10) {
            strH = "0" + h;
        } else {
            strH = "" + h;
        }
        if (m < 10) {
            strM = "0" + m;
        } else {
            strM = "" + m;
        }
        String time = strH + ":" + strM;
        return time;
    }
}

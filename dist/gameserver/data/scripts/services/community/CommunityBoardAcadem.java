package services.community;

import java.util.StringTokenizer;
import l2p.gameserver.Config;
import l2p.gameserver.cache.Msg;
import l2p.gameserver.data.htm.HtmCache;
import l2p.gameserver.data.xml.holder.ItemHolder;
import l2p.gameserver.handler.bbs.CommunityBoardManager;
import l2p.gameserver.handler.bbs.ICommunityBoardHandler;
import l2p.gameserver.listener.actor.player.impl.AcademAnswerListener;
import l2p.gameserver.model.AcademList;
import l2p.gameserver.model.AcademReward;
import l2p.gameserver.model.GameObjectsStorage;
import l2p.gameserver.model.Player;
import l2p.gameserver.model.Request;
import l2p.gameserver.model.Request.L2RequestType;
import l2p.gameserver.model.Skill;
import l2p.gameserver.model.Zone;
import l2p.gameserver.model.base.TeamType;
import l2p.gameserver.model.entity.olympiad.Olympiad;
import l2p.gameserver.model.pledge.Clan;
import l2p.gameserver.scripts.Functions;
import l2p.gameserver.scripts.ScriptFile;
import l2p.gameserver.serverpackets.ConfirmDlg;
import l2p.gameserver.serverpackets.ShowBoard;
import l2p.gameserver.serverpackets.SystemMessage2;
import l2p.gameserver.serverpackets.components.SystemMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommunityBoardAcadem implements ScriptFile, ICommunityBoardHandler {

    private static final Logger _log = LoggerFactory.getLogger(CommunityBoardAcadem.class);

    @Override
    public void onLoad() {
        if (Config.COMMUNITYBOARD_ENABLED) {
            _log.info("CommunityBoardAcadem: service loaded.");
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
        return new String[]{
            "_bbsRegAcadem",
            "_bbsShowAcademList",
            "_bbsShowInviteAcademic",
            "_bbsInviteAcademic"
        };
    }

    @Override
    public void onBypassCommand(Player player, String bypass) {

        if (!CheckCondition(player)) {
            return;
        }
        String html = "";
        if (bypass.startsWith("_bbsRegAcadem")) {
            regAcademList(player);
        } else if (bypass.startsWith("_bbsShowAcademList;")) {
            final StringTokenizer st = new StringTokenizer(bypass, ";");
            st.nextToken();
            final int index = Integer.parseInt(st.nextToken());

            String academList = showAcademList(index);

            if (academList == null || "".equals(academList) || "<center><table></table></center>".equals(academList)) {
                academList = "No find registered Academic.<br>";
            }

            if (player.getLevel() < 40 && player.getLevel() > 4) {
                academList += "<button value=\"Подать заявку\" action=\"bypass -h _bbsRegAcadem\" width=120 height=30 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"><br>";
            }

            html = HtmCache.getInstance().getHtml(Config.BBS_HOME_DIR + "pages/academList.htm", player);
            html = html.replaceFirst("%academList%", academList);
            ShowBoard.separateAndSend(html, player);
        } else if (bypass.startsWith("_bbsShowInviteAcademic;")) {
            final StringTokenizer st = new StringTokenizer(bypass, ";");
            st.nextToken();
            final String name = st.nextToken();
            final int index = Integer.parseInt(st.nextToken());

            String academList = showAcademChar(name, index);

            if (academList == null || academList == "") {
                academList = "No find registered Academic.<br>";
            }

            if (player.getLevel() < 40 && player.getLevel() > 4) {
                academList += "<button value=\"Подать заявку\" action=\"bypass -h _bbsRegAcadem\" width=120 height=30 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"><br>";
            }

            html = HtmCache.getInstance().getHtml(Config.BBS_HOME_DIR + "pages/academList.htm", player);
            html = html.replace("%academList%", academList);
            ShowBoard.separateAndSend(html, player);
        } else if (bypass.startsWith("_bbsInviteAcademic")) {
            final StringTokenizer st = new StringTokenizer(bypass, " ");
            st.nextToken();
            final String name = st.nextToken();
            final String item = st.nextToken();
            final int price = Integer.parseInt(st.nextToken());

            int itemId = AcademReward.checkAndGet(item);

            if (itemId == -1) {
                player.sendMessage("Ошибка");
                return;
            }

            if (price > 1000000000) {
                player.sendMessage("Максимальная сумма для выплаты академику 1 000 000 000.");
                return;
            }

            Player academCharId = GameObjectsStorage.getPlayer(name);

            inviteAcademic(player, academCharId, itemId, price);
        }

    }

    private void regAcademList(final Player activeChar) {
        if (activeChar.getLevel() < 5 || activeChar.getLevel() > 39) {
            activeChar.sendMessage("Не Удаеться подать заявку в Список Акаемиков..");
            return;
        }

        if (activeChar.getClan() != null) {
            activeChar.sendMessage("У вас есть клан.");
            return;
        }

        for (Player players : AcademList.getAcademList()) {
            if (players == activeChar) {
                activeChar.sendMessage("Вы Уже подавали заявку в Список Акаемиков.");
                return;
            }
        }

        activeChar.setAcademList(true);
        AcademList.addAcademList(activeChar);
        activeChar.sendMessage("Вы подали заявку в Список Академиков.");
    }

    private String showAcademList(final int pageNum) {
        StringBuilder list = new StringBuilder();
        int lengthAcadem = AcademList.getAcademList().size();
        int page = 0;
        int number = 1;
        boolean firstCollumn = true;

        if (pageNum > 0) {
            number += pageNum * 18;
        }

        list.append("<center><table width=750>");
        list.append("<tr><td width=20>#</td><td width=180>Имя</td><td width=50>Уровень</td><td width=75>Запрос</td><td width=100></td><td width=20>#</td><td width=180>Имя</td><td width=50>Уровень</td><td width=75>Запрос</td></tr>");

        for (Player player : AcademList.getAcademList()) {
            if (player.isInOfflineMode()) {
                AcademList.deleteAcademList(player);
                continue;
            }

            if (!player.isOnline()) {
                AcademList.deleteAcademList(player);
                continue;
            }

            if (pageNum == 0 && page >= 18) {
                continue;
            } else if (page < (pageNum * 18) || page > (pageNum * 18 + 17)) {
                page++;
                continue;
            }

            if (firstCollumn) {
                list.append("<tr>");
            } else {
                list.append("<td width=100></td>");
            }

            list.append("<td width=20>" + number + "</td>");
            list.append("<td width=180>" + player.getName() + "</td>");
            list.append("<td width=50>" + player.getLevel() + "</td>");
            list.append("<td width=75><button value=\"Принять\" action=\"bypass -h _bbsShowInviteAcademic;" + player.getName() + ";" + pageNum + "\" width=75 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");

            if (!firstCollumn) {
                list.append("</tr>");
            }

            firstCollumn = !firstCollumn;
            page++;
            number++;
        }

        if (firstCollumn) {
            list.append("</tr>");
        }

        list.append("</table>");

        //int x = Math.ceil(lengthAcadem / 15).intValue();
        int x = (lengthAcadem + (18 - 1)) / 18;

        if (x > 1) {
            list.append("<table><tr>");
            for (int i = 0; i < x; i++) {

                if (i != pageNum) {
                    list.append("<td><button value=\"" + (i + 1) + "\" action=\"bypass -h _bbsShowAcademList;" + i + "\" width=20 height=20 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
                } else {
                    list.append("<td><button value=\"" + (i + 1) + "\" action=\"\" width=20 height=20 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF_Down\"></td>");
                }
            }
            list.append("</tr></table>");
        }

        list.append("</center>");

        return list.toString();
    }

    private String showAcademChar(final String name, final int pageNum) {
        StringBuilder list = new StringBuilder();

        for (Player players : AcademList.getAcademList()) {
            if (!players.getName().equals(name)) {
                continue;
            }

            list.append("<center><table width=250>");
            list.append("<tr><td width=100>Имя</td><td width=150>" + players.getName() + "</td></tr>");
            list.append("<tr><td width=100>Уровень</td><td width=150>" + players.getLevel() + "</td></tr>");
            list.append("</table>");

            list.append("Введите Предмет:");
            list.append("<combobox width=180 height=20 var=items list=" + AcademReward.toList() + ">");
            list.append("Количество: " + "<edit var=\"price\" width=180 height=20 length=\"20\">");
            list.append("Максимальная сумма для выплаты академику 1 000 000 000.");
            list.append("<button value=\"Отправить запрос\" action=\"bypass -h _bbsInviteAcademic " + players.getName() + " $items $price\" width=120 height=30 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"><br>");
            list.append("<button value=\"Назад к списку\" action=\"bypass -h _bbsShowAcademList;" + pageNum + "\" width=120 height=30 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"><br>");
            list.append("</center>");
            break;
        }
        return list.toString();
    }

    private void inviteAcademic(final Player activeChar, final Player academChar, final int itemId, final int price) {
        Clan clan = activeChar.getClan();
        if (clan == null || !clan.canInvite()) {
            activeChar.sendPacket(SystemMsg.AFTER_A_CLAN_MEMBER_IS_DISMISSED_FROM_A_CLAN_THE_CLAN_MUST_WAIT_AT_LEAST_A_DAY_BEFORE_ACCEPTING_A_NEW_MEMBER);
            return;
        }

        // is the activeChar have privilege to invite players
        if ((activeChar.getClanPrivileges() & Clan.CP_CL_INVITE_CLAN) != Clan.CP_CL_INVITE_CLAN) {
            activeChar.sendPacket(SystemMsg.ONLY_THE_LEADER_CAN_GIVE_OUT_INVITATIONS);
            return;
        }

        if (academChar.getClan() == activeChar.getClan()) {
            activeChar.sendPacket(SystemMsg.THAT_IS_AN_INCORRECT_TARGET);
            return;
        }

        if (!academChar.getPlayerAccess().CanJoinClan) {
            activeChar.sendPacket(new SystemMessage2(SystemMsg.C1_CANNOT_JOIN_THE_CLAN_BECAUSE_ONE_DAY_HAS_NOT_YET_PASSED_SINCE_THEY_LEFT_ANOTHER_CLAN).addName(academChar));
            return;
        }

        if (academChar.getClan() != null) {
            activeChar.sendPacket(new SystemMessage2(SystemMsg.S1_IS_ALREADY_A_MEMBER_OF_ANOTHER_CLAN).addName(academChar));
            return;
        }

        if (academChar.isBusy()) {
            activeChar.sendPacket(new SystemMessage2(SystemMsg.C1_IS_ON_ANOTHER_TASK).addName(academChar));
            return;
        }

        if ((academChar.getLevel() > 40 || academChar.getClassId().getLevel() > 2)) {
            activeChar.sendPacket(SystemMsg.TO_JOIN_A_CLAN_ACADEMY_CHARACTERS_MUST_BE_LEVEL_40_OR_BELOW_NOT_BELONG_ANOTHER_CLAN_AND_NOT_YET_COMPLETED_THEIR_2ND_CLASS_TRANSFER);
            return;
        }

        if (clan.getUnitMembersSize(Clan.SUBUNIT_ACADEMY) >= clan.getSubPledgeLimit(Clan.SUBUNIT_ACADEMY)) {
            activeChar.sendPacket(SystemMsg.THE_ACADEMYROYAL_GUARDORDER_OF_KNIGHTS_IS_FULL_AND_CANNOT_ACCEPT_NEW_MEMBERS_AT_THIS_TIME);

            return;
        }

        if (!(Functions.getItemCount(activeChar, itemId) >= price)) {
            activeChar.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
            return;
        }

        Request request = new Request(L2RequestType.CLAN, activeChar, academChar).setTimeout(10000L);
        request.set("pledgeType", Clan.SUBUNIT_ACADEMY);

        academChar.setPledgeItemId(itemId);
        academChar.setPledgePrice(price);

        
        ConfirmDlg packet = new ConfirmDlg(SystemMsg.S1, 10000).addString("Вас приглашают в академию за " + price + " " + ItemHolder.getInstance().getTemplate(itemId).getName());
        academChar.ask(packet, new AcademAnswerListener(activeChar, academChar)); 
        
        
        
    // todo    academChar.sendPacket(new ConfirmDlgPacket(SystemMessage.S1_S2, 10000, ConfirmDlg.ACADEMY_INVITE).addString("Вас приглашают в академию за " + price + " " + ItemTable.getInstance().getTemplate(itemId).getName()));
       
        //academChar.sendPacket(new AskJoinPledge(activeChar.getObjectId(), activeChar.getClan().getName()));
        //academChar.scriptRequest("Вас приглашают в академию за " + price + " " + ItemHolder.getInstance().getTemplate(itemId).getName(), "events.CaptureTheFlag.CaptureTheFlag:inviteInAcademy", new Object[0]);
            
        
    //    ConfirmDlg packet = new ConfirmDlg(SystemMsg.S2_S1, 10000).addString("Вас приглашают в академию за " + price + " " + ItemHolder.getInstance().getTemplate(itemId).getName());
    //    academChar.sendPacket(packet);
    //    academChar.ask(packet, new CoupleAnswerListener(activeChar, ptarget));
    }

    @Override
    public void onWriteCommand(Player player, String bypass, String arg1, String arg2, String arg3, String arg4, String arg5) {
    }

    private boolean CheckCondition(Player player) {
        if (player == null) {
            return false;
        }

        if (player.isDead()) {
            return false;
        }

        if (!Config.ALLOW_COMMUNITYBOARD_IS_IN_SIEGE && player.isInZone(Zone.ZoneType.SIEGE)) {
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
}

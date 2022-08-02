package services.community;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import l2p.gameserver.Config;
import l2p.gameserver.data.htm.HtmCache;
import l2p.gameserver.data.xml.holder.ItemHolder;
import l2p.gameserver.data.xml.holder.SkillAcquireHolder;
import l2p.gameserver.handler.bbs.CommunityBoardManager;
import l2p.gameserver.handler.bbs.ICommunityBoardHandler;
import l2p.gameserver.model.Player;
import l2p.gameserver.model.Skill;
import l2p.gameserver.model.SkillLearn;
import l2p.gameserver.model.SubClass;
import l2p.gameserver.model.Zone;
import l2p.gameserver.model.base.AcquireType;
import l2p.gameserver.model.base.ClassId;
import l2p.gameserver.model.base.ClassType;
import l2p.gameserver.model.base.ClassType2;
import l2p.gameserver.model.base.PlayerClass;
import l2p.gameserver.model.base.Race;
import l2p.gameserver.model.entity.olympiad.Olympiad;
import l2p.gameserver.model.items.ItemInstance;
import l2p.gameserver.scripts.Functions;
import l2p.gameserver.scripts.ScriptFile;
import l2p.gameserver.serverpackets.ShowBoard;
import l2p.gameserver.serverpackets.SkillList;
import l2p.gameserver.serverpackets.SystemMessage2;
import l2p.gameserver.serverpackets.components.CustomMessage;
import l2p.gameserver.serverpackets.components.SystemMsg;
import l2p.gameserver.templates.item.ItemTemplate;
import l2p.gameserver.utils.HtmlUtils;
import l2p.gameserver.utils.Language;
import l2p.gameserver.utils.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommunityBoardProfession implements ScriptFile, ICommunityBoardHandler {

    private static final Logger _log = LoggerFactory.getLogger(CommunityBoardProfession.class);
    int count_on_page = 7;

    @Override
    public void onLoad() {
        if (Config.COMMUNITYBOARD_ENABLED/* && Config.BBS_PVP_CB_ENABLED*/) {
            _log.info("CommunityBoard: Manage Career service loaded.");
            CommunityBoardManager.getInstance().registerHandler(this);
        }
    }

    @Override
    public void onReload() {
        if (Config.COMMUNITYBOARD_ENABLED/* && Config.BBS_PVP_CB_ENABLED*/) {
            CommunityBoardManager.getInstance().removeHandler(this);
        }
    }

    @Override
    public void onShutdown() {
    }

    @Override
    public String[] getBypassCommands() {
        return new String[]{"_bbscareer;", "_bbscareer;sub;", "_bbscareer;classmaster;change_class;"};
    }

    @Override
    public void onBypassCommand(Player activeChar, String command) {
        if (!CheckCondition(activeChar)) {
            return;
        }

        if (command.startsWith("_bbscareer;")) {
            ClassId classId = activeChar.getClassId();
            int jobLevel = classId.getLevel();
            int level = activeChar.getLevel();
            StringBuilder html = new StringBuilder();

            html.append("<center><table width=755>");
            html.append("<tr><td WIDTH=20 align=left valign=top></td>");
            if (activeChar.isLangRus()) {
                html.append("<td WIDTH=690 align=left valign=top><font color=LEVEL>»</font> Добро пожаловать ").append(activeChar.getName()).append(".</td></tr>");
            } else {
                html.append("<td WIDTH=690 align=left valign=top><font color=LEVEL>»</font> Welcome ").append(activeChar.getName()).append(".</td></tr>");
            }
            html.append("<tr><td WIDTH=20 align=left valign=top></td>");
            if (activeChar.isLangRus()) {
                html.append("<td WIDTH=690 align=left valign=top><font color=LEVEL>»</font> Ваша текущая профессия <font color=LEVEL>").append(activeChar.getClassId().getNameRu()).append("</font>.</td></tr></table>");
            } else {
                html.append("<td WIDTH=690 align=left valign=top><font color=LEVEL>»</font> Your current profession <font color=LEVEL>").append(activeChar.getClassId().getNameRu()).append("</font>.</td></tr></table>");
            }

            if (Config.ALLOW_CLASS_MASTERS_LIST.isEmpty() || !Config.ALLOW_CLASS_MASTERS_LIST.contains(jobLevel)) {
                jobLevel = 4;
            }

            if ((level >= 20 && jobLevel == 1 || level >= 40 && jobLevel == 2 || level >= 76 && jobLevel == 3) && Config.ALLOW_CLASS_MASTERS_LIST.contains(jobLevel)) {
                ItemTemplate item = ItemHolder.getInstance().getTemplate(Config.CLASS_MASTERS_PRICE_ITEM);

                for (ClassId cid : ClassId.values()) {
                    if (cid == ClassId.inspector) {
                        continue;
                    }
                    if (cid.childOf(classId) && cid.level() == classId.level() + 1) {
                        html.append("<table border=0 cellspacing=0 cellpadding=0><tr><td width=755><center><img src=\"l2ui.squaregray\" width=\"720\" height=\"1\"></center></td></tr></table>");
                        html.append("<table border=0 cellspacing=4 cellpadding=3><tr>");
                        html.append("<td FIXWIDTH=50 align=right valign=top><img src=\"icon.etc_royal_membership_i00\" width=32 height=32></td>");
                        html.append("<td FIXWIDTH=576 align=left valign=top><font color=\"0099FF\">").append(cid.getNameRu()).append(activeChar.isLangRus() ? ".</font>&nbsp;<br1>›&nbsp;Стоимость: " : ".</font>&nbsp;<br1>›&nbsp;Cost: ").append(Util.formatAdena(Config.CLASS_MASTERS_PRICE_LIST[jobLevel])).append(" Adena.</td>");
                        if (activeChar.isLangRus()) {
                            html.append("<td FIXWIDTH=95 align=center valign=top><button value=\"Сменить\" action=\"bypass _bbscareer;classmaster;change_class;").append(cid.getId()).append(";").append(Config.CLASS_MASTERS_PRICE_LIST[jobLevel]).append("\" back=\"l2ui_ct1.button.button_df_small_down\" fore=\"l2ui_ct1.button.button_df_small\" width=\"80\" height=\"25\"/>");
                        } else {
                            html.append("<td FIXWIDTH=95 align=center valign=top><button value=\"Change\" action=\"bypass _bbscareer;classmaster;change_class;").append(cid.getId()).append(";").append(Config.CLASS_MASTERS_PRICE_LIST[jobLevel]).append("\" back=\"l2ui_ct1.button.button_df_small_down\" fore=\"l2ui_ct1.button.button_df_small\" width=\"80\" height=\"25\"/>");
                        }

                        html.append("</td></tr></table>");

                    }

                }
                html.append("</center>");
            } else {
                switch (jobLevel) {
                    case 1:
                        if (activeChar.getLanguage() == Language.ENGLISH) {
                            //    html.append("Greetings <font color=F2C202>").append(activeChar.getName()).append("</font> your current profession <font color=F2C202>").append(activeChar.getClassId().name()).append("</font><br>");
                            html.append("To change your profession you have to reach: <font color=F2C202>level 20</font><br>");
                            html.append("To activate the subclass you have to reach <font color=F2C202>level 75</font><br>");
                            html.append("To become a nobleman, you have to bleed to subclass <font color=F2C202>level 76</font><br>");
                        } else {
                            //    html.append("Приветствую <font color=F2C202>").append(activeChar.getName()).append("</font> ваша текущая профессия <font color=F2C202>").append(activeChar.getClassId().name()).append("</font><br>");
                            html.append("Для того чтобы сменить вашу профессию вы должны достичь: <font color=F2C202>20-го уровня</font><br>");
                            html.append("Для активации сабклассов вы должны достичь <font color=F2C202>75-го уровня</font><br>");
                            html.append("Чтобы стать дворянином вы должны прокачать сабкласс до <font color=F2C202>76-го уровня</font><br>");
                        }
                        html.append(getSubClassesHtml(activeChar, true));
                        break;
                    case 2:
                        if (activeChar.getLanguage() == Language.ENGLISH) {
                            //    html.append("Greetings <font color=F2C202>").append(activeChar.getName()).append("</font> your current profession <font color=F2C202>").append(activeChar.getClassId().name()).append("</font><br>");
                            html.append("To change your profession you have to reach: <font color=F2C202>level 40</font><br>");
                            html.append("To activate the subclass you have to reach <font color=F2C202>level 75</font><br>");
                            html.append("To become a nobleman, you have to bleed to subclass <font color=F2C202>7level 76</font><br>");
                        } else {
                            //    html.append("Приветствую <font color=F2C202>").append(activeChar.getName()).append("</font> ваша текущая профессия <font color=F2C202>").append(activeChar.getClassId().name()).append("</font><br>");
                            html.append("Для того чтобы сменить вашу профессию вы должны достичь: <font color=F2C202>40-го уровня</font><br>");
                            html.append("Для активации сабклассов вы должны достичь <font color=F2C202>75-го уровня</font><br>");
                            html.append("Чтобы стать дворянином вы должны прокачать сабкласс до <font color=F2C202>76-го уровня</font><br>");
                        }
                        html.append(getSubClassesHtml(activeChar, true));
                        break;
                    case 3:
                        if (activeChar.getLanguage() == Language.ENGLISH) {
                            //    html.append("Greetings <font color=F2C202>").append(activeChar.getName()).append("</font> your current profession <font color=F2C202>").append(activeChar.getClassId().name()).append("</font><br>");
                            html.append("To change your profession you have to reach: <font color=F2C202>level 76</font><br>");
                            html.append("To activate the subclass you have to reach <font color=F2C202>level 75</font><br>");
                            html.append("To become a nobleman, you have to bleed to subclass <font color=F2C202>level 76</font><br>");
                        } else {
                            //   html.append("Приветствую <font color=F2C202>").append(activeChar.getName()).append("</font> ваша текущая профессия <font color=F2C202>").append(activeChar.getClassId().name()).append("</font><br>");
                            html.append("Для того чтобы сменить вашу профессию вы должны достичь: <font color=F2C202>76-го уровня</font><br>");
                            html.append("Для активации сабклассов вы должны достичь <font color=F2C202>75-го уровня</font><br>");
                            html.append("Чтобы стать дворянином вы должны прокачать сабкласс до <font color=F2C202>76-го уровня</font><br>");
                        }
                        html.append(getSubClassesHtml(activeChar, true));
                        break;
                    case 4:
                        if (activeChar.getLanguage() == Language.ENGLISH) {
                            //    html.append("Greetings <font color=F2C202>").append(activeChar.getName()).append("</font> your current profession <font color=F2C202>").append(activeChar.getClassId().name()).append("</font><br>");
                            html.append("For you are no more jobs available, or the master class is not currently available.<br>");
                            if (level >= 76) {
                                html.append("You have reached the <font color=F2C202>level 75</font> activation of the subclass is now available<br>");
                                if (!activeChar.isNoble() && activeChar.getSubLevel() < 75) {
                                    html.append("You can get the nobility only after your sub-class reaches the 76 level.<br>");
                                } else if (!activeChar.isNoble() && activeChar.getSubLevel() > 75) {
                                    html.append("You can get the nobility. Your sub-class has reached the 76th level.<br>");
                                } else if (activeChar.isNoble()) {
                                    html.append("You have a gentleman. Getting the nobility no longer available.<br>");
                                }
                            }
                        } else {
                            //    html.append("Приветствую <font color=F2C202>").append(activeChar.getName()).append("</font> ваша текущая профессия <font color=F2C202>").append(activeChar.getClassId().name()).append("</font><br>");
                            html.append("Для вас больше нет доступных профессий, либо Класс мастер в данный момент недоступен.<br>");
                            if (level >= 76) {
                                html.append("Вы достигли <font color=F2C202>75-го уровня</font> активация сабклассов теперь доступна<br>");
                                if (!activeChar.isNoble() && activeChar.getSubLevel() < 75) {
                                    html.append("Вы можете получить дворянство только после того как ваш саб-класс достигнет 76-го уровня.<br>");
                                } else if (!activeChar.isNoble() && activeChar.getSubLevel() > 75) {
                                    html.append("Вы можете получить дворянство. Ваш саб-класс достиг 76-го уровня.<br>");
                                } else if (activeChar.isNoble()) {
                                    html.append("Вы уже дворянин. Получение дворянства более не доступно.<br>");
                                }
                            }
                        }
                        html.append(getSubClassesHtml(activeChar, true));
                        break;
                }
            }
            String content = HtmCache.getInstance().getHtml(Config.BBS_HOME_DIR + "pages/career.htm", activeChar);
            content = content.replace("%career%", html.toString());
            ShowBoard.separateAndSend(content, activeChar);
        }
        if (command.startsWith("_bbscareer;sub;")) {
            if (activeChar.getPet() != null) {
                activeChar.sendPacket(SystemMsg.A_SUBCLASS_MAY_NOT_BE_CREATED_OR_CHANGED_WHILE_A_SERVITOR_OR_PET_IS_SUMMONED);
                return;
            }

            // Саб класс нельзя получить или поменять, пока используется скилл или персонаж находится в режиме трансформации
            if (activeChar.isActionsDisabled() || activeChar.getTransformation() != 0) {
                activeChar.sendPacket(SystemMsg.SUBCLASSES_MAY_NOT_BE_CREATED_OR_CHANGED_WHILE_A_SKILL_IS_IN_USE);
                return;
            }

            if (activeChar.getWeightPenalty() >= 3) {
                activeChar.sendPacket(SystemMsg.A_SUBCLASS_CANNOT_BE_CREATED_OR_CHANGED_WHILE_YOU_ARE_OVER_YOUR_WEIGHT_LIMIT);
                return;
            }

            if (activeChar.getInventoryLimit() * 0.8 < activeChar.getInventory().getSize()) {
                activeChar.sendPacket(SystemMsg.A_SUBCLASS_CANNOT_BE_CREATED_OR_CHANGED_BECAUSE_YOU_HAVE_EXCEEDED_YOUR_INVENTORY_LIMIT);
                return;
            }

            StringBuilder html = new StringBuilder();

            Map<Integer, SubClass> playerClassList = activeChar.getSubClasses();
            Set<PlayerClass> subsAvailable;

            if (activeChar.getLevel() < 40) {

                html.append(activeChar.isLangRus() ? "Вы должны достичь 40 уровня или выше. чтобы работать с подклассом." : "You must be level 40 or more to operate with your sub-classes.");
                String content = HtmCache.getInstance().getHtml(Config.BBS_HOME_DIR + "pages/career.htm", activeChar);
                content = content.replace("%career%", html.toString());
                ShowBoard.separateAndSend(content, activeChar);
                return;
            }

            int classId = 0;
            int newClassId = 0;
            int intVal = 0;
            int page = 0;

            try {
                /*
                 _log.info(command.substring(16, command.length()));
                 String[] sub = command.substring(15, command.length()).split(" ");
                 _log.info("intVal="+sub[0]+" classId="+sub[1]+" newClassId="+sub[2]+" page="+sub[3]);
                 */
                /*
                 for (String id : command.substring(15, command.length()).split(" ")) {
                 if (intVal == 0) {
                 intVal = Integer.parseInt(id);
                 continue;
                 }
                 if (classId > 0) {
                 newClassId = Short.parseShort(id);
                 continue;
                 }
                 classId = Short.parseShort(id);

                 page = Integer.parseInt(id);

                 _log.info(id);

                 //_log.info("intVal="+intVal+" classId="+classId+" newClassId="+newClassId+" page="+page);
                 }*/

                String[] sub = command.substring(15, command.length()).split(" ");
                if (sub[0] != null) {
                    intVal = Integer.parseInt(sub[0]);
                }
                if (sub[1] != null) {
                    classId = Integer.parseInt(sub[1]);
                }
                if (sub[2] != null) {
                    newClassId = Integer.parseInt(sub[2]);
                }
                if (sub[3] != null) {
                    page = Integer.parseInt(sub[3]);
                }

            } catch (NumberFormatException NumberFormatException) {
            }

            switch (intVal) {
                case 1: // Возвращает список сабов, которые можно взять (см case 4)
                    subsAvailable = getAvailableSubClasses(activeChar, true);
                    if (subsAvailable != null && !subsAvailable.isEmpty()) {

                        html.append("<center><table width=755>");
                        html.append("<tr><td WIDTH=20 align=left valign=top></td>");
                        html.append(activeChar.isLangRus() ? "<td WIDTH=690 align=left valign=top><font color=LEVEL>»</font>Вам доступны следующие саб-классы:</td></tr></table>" : "<td WIDTH=690 align=left valign=top><font color=LEVEL>»</font>You have the following sub-classes:</td></tr></table>");
                        int k = 0;

                        if (subsAvailable.size() <= count_on_page) {
                            page = 1;
                        }
                        for (PlayerClass subClass : subsAvailable) {

                            if (k < (page * count_on_page) && k >= ((page - 1) * count_on_page)) {
                                html.append("<table border=0 cellspacing=0 cellpadding=0><tr><td width=755><center><img src=\"l2ui.squaregray\" width=\"720\" height=\"1\"></center></td></tr></table>");
                                html.append("<table border=0 cellspacing=4 cellpadding=3><tr>");
                                html.append("<td FIXWIDTH=50 align=right valign=top><img src=\"icon.etc_royal_membership_i00\" width=32 height=32></td>");
                                html.append("<td FIXWIDTH=576 align=left valign=top><font color=\"0099FF\">").append(activeChar.isLangRus() ? formatClassForDisplayRu(subClass) : subClass).append(activeChar.isLangRus() ? ".</font>&nbsp;<br1>›&nbsp;Стоимость: " : ".</font>&nbsp;<br1>›&nbsp;Cost: ").append(activeChar.isLangRus() ? " Бесплатно.</td>" : " Free.</td>");
                                html.append(activeChar.isLangRus() ? "<td FIXWIDTH=95 align=center valign=top><button value=\"Добавить\" action=\"bypass _bbscareer;sub;4 " : "<td FIXWIDTH=95 align=center valign=top><button value=\"Add\" action=\"bypass _bbscareer;sub;4 ").append(subClass.ordinal()).append(" 0 1").append("\" back=\"l2ui_ct1.button.button_df_small_down\" fore=\"l2ui_ct1.button.button_df_small\" width=\"80\" height=\"25\"/>");
                                html.append("</td></tr></table><br>");

                            }
                            k++;
                        }

                        if (subsAvailable.size() > count_on_page) {
                            html.append("<table width=330 border=0><tr><td width=200 height=20 align=center>Страница:</td></tr></table><table width=330 border=0><tr>");
                            int pages = subsAvailable.size() / count_on_page + 1;
                            int count_to_line = 1;
                            for (int cur = 1; cur <= pages; cur++) {
                                if (page == cur) {
                                    html.append("<td width=24 align=center>[").append(cur).append("]</td>");
                                } else {
                                    html.append("<td width=20 align=center><button value=\"").append(cur).append("\" action=\"bypass _bbscareer;sub;1 0 0 ").append(cur).append("\" width=20 height=20 back=\"L2UI_ct1.button_df_down\" fore=\"L2UI_ct1.button_df\"></td>");
                                }
                                if (count_to_line == 14) {
                                    html.append("</tr><tr>");
                                    count_to_line = 0;
                                }
                                count_to_line++;
                            }
                            html.append("</tr></table><br>");
                        }

                        html.append("<br><br><br>");
                        html.append("<table border=0 cellspacing=0 cellpadding=0><tr><td width=755><center><img src=\"l2ui.squaregray\" width=\"720\" height=\"1\"></center></td></tr></table>");
                        html.append("</center>");
                    } else {
                        activeChar.sendMessage(new CustomMessage("l2p.gameserver.model.instances.L2VillageMasterInstance.NoSubAtThisTime", activeChar));
                    }
                    break;
                case 2: // Установка уже взятого саба (см case 5)
                    final int baseClassId = activeChar.getBaseClassId();

                    if (playerClassList.size() < 2) {

                        html.append("<center><table width=755>");
                        html.append("<tr><td WIDTH=20 align=left valign=top></td>");
                        if (activeChar.isLangRus()) {
                            html.append("<td WIDTH=690 align=left valign=top><font color=LEVEL>»</font>У вас нет саб-классов для переключения, но вы можете добавить его прямо сейчас<br><a action=\"bypass _bbscareer;sub;1 0 0 1\">Добавить саб.</a></td></tr></table>");
                        } else {
                            html.append("<td WIDTH=690 align=left valign=top><font color=LEVEL>»</font>You do not have sub-classes to switch, but you can add it now!<br><a action=\"bypass _bbscareer;sub;1 0 0 1\">Add sub</a></td></tr></table>");
                        }

                    } else {
                        html.append("<center><table width=755>");
                        html.append("<tr><td WIDTH=20 align=left valign=top></td>");
                        if (activeChar.isLangRus()) {
                            html.append("<td WIDTH=690 align=left valign=top><font color=LEVEL>»</font>Какой саб-класс вы желаете использовать?</td></tr></table>");
                        } else {
                            html.append("<td WIDTH=690 align=left valign=top><font color=LEVEL>»</font>What sub-class you want to use?</td></tr></table>");
                        }
                        if (baseClassId == activeChar.getActiveClassId()) {
                            html.append("<table border=0 cellspacing=0 cellpadding=0><tr><td width=755><center><img src=\"l2ui.squaregray\" width=\"720\" height=\"1\"></center></td></tr></table>");
                            html.append("<table border=0 cellspacing=4 cellpadding=3><tr>");
                            html.append("<td FIXWIDTH=50 align=right valign=top><img src=\"icon.etc_royal_membership_i00\" width=32 height=32></td>");
                            html.append("<td FIXWIDTH=576 align=left valign=top><font color=\"0099FF\">").append(HtmlUtils.htmlClassName(baseClassId)).append(activeChar.isLangRus() ? "(Базовый)" : "(Base)").append(activeChar.isLangRus() ? ".</font>&nbsp;<br1>›&nbsp;Стоимость: " : ".</font>&nbsp;<br1>›&nbsp;Cost: ").append(activeChar.isLangRus() ? " Бесплатно.</td>" : " Free.</td>");
                            html.append("</tr></table>");
                        } else {
                            html.append("<table border=0 cellspacing=0 cellpadding=0><tr><td width=755><center><img src=\"l2ui.squaregray\" width=\"720\" height=\"1\"></center></td></tr></table>");
                            html.append("<table border=0 cellspacing=4 cellpadding=3><tr>");
                            html.append("<td FIXWIDTH=50 align=right valign=top><img src=\"icon.etc_royal_membership_i00\" width=32 height=32></td>");
                            html.append("<td FIXWIDTH=576 align=left valign=top><font color=\"0099FF\">").append(HtmlUtils.htmlClassName(baseClassId)).append(activeChar.isLangRus() ? "(Базовый)" : "(Base)").append(activeChar.isLangRus() ? ".</font>&nbsp;<br1>›&nbsp;Стоимость: " : ".</font>&nbsp;<br1>›&nbsp;Cost: ").append(activeChar.isLangRus() ? " Бесплатно.</td>" : " Free.</td>");
                            html.append(activeChar.isLangRus() ? "<td FIXWIDTH=95 align=center valign=top><button value=\"Сменить\" action=\"bypass _bbscareer;sub;5 " : "<td FIXWIDTH=95 align=center valign=top><button value=\"Change\" action=\"bypass _bbscareer;sub;5 ").append(baseClassId).append(" 0 1").append("\" back=\"l2ui_ct1.button.button_df_small_down\" fore=\"l2ui_ct1.button.button_df_small\" width=\"80\" height=\"25\"/>");
                            html.append("</td></tr></table>");
                        }

                        for (SubClass subClass : playerClassList.values()) {
                            if (subClass.isBase()) {
                                continue;
                            }
                            int subClassId = subClass.getClassId();

                            if (subClassId == activeChar.getActiveClassId()) {

                                html.append("<table border=0 cellspacing=0 cellpadding=0><tr><td width=755><center><img src=\"l2ui.squaregray\" width=\"720\" height=\"1\"></center></td></tr></table>");
                                html.append("<table border=0 cellspacing=4 cellpadding=3><tr>");
                                html.append("<td FIXWIDTH=50 align=right valign=top><img src=\"icon.etc_royal_membership_i00\" width=32 height=32></td>");
                                html.append("<td FIXWIDTH=576 align=left valign=top><font color=\"0099FF\">").append(HtmlUtils.htmlClassName(subClassId)).append(activeChar.isLangRus() ? ".</font>&nbsp;<br1>›&nbsp;Стоимость: " : ".</font>&nbsp;<br1>›&nbsp;Cost: ").append(activeChar.isLangRus() ? " Бесплатно.</td>" : " Free.</td>");
                                html.append("</tr></table>");
                            } else {
                                html.append("<table border=0 cellspacing=0 cellpadding=0><tr><td width=755><center><img src=\"l2ui.squaregray\" width=\"720\" height=\"1\"></center></td></tr></table>");
                                html.append("<table border=0 cellspacing=4 cellpadding=3><tr>");
                                html.append("<td FIXWIDTH=50 align=right valign=top><img src=\"icon.etc_royal_membership_i00\" width=32 height=32></td>");
                                html.append("<td FIXWIDTH=576 align=left valign=top><font color=\"0099FF\">").append(HtmlUtils.htmlClassName(subClassId)).append(activeChar.isLangRus() ? ".</font>&nbsp;<br1>›&nbsp;Стоимость: " : ".</font>&nbsp;<br1>›&nbsp;Cost: ").append(activeChar.isLangRus() ? " Бесплатно.</td>" : " Free.</td>");
                                html.append("<td FIXWIDTH=95 align=center valign=top><button value=\"Сменить\" action=\"bypass _bbscareer;sub;5 ").append(subClassId).append(" 0 1").append("\" back=\"l2ui_ct1.button.button_df_small_down\" fore=\"l2ui_ct1.button.button_df_small\" width=\"80\" height=\"25\"/>");
                                html.append("</td></tr></table>");
                            }
                        }
                        html.append("<br><br><br>");
                        html.append("<table border=0 cellspacing=0 cellpadding=0><tr><td width=755><center><img src=\"l2ui.squaregray\" width=\"720\" height=\"1\"></center></td></tr></table>");
                        html.append("</center>");
                    }

                    break;
                case 3: // Отмена сабкласса - список имеющихся (см case 6)
                    html.append("<center><table width=755>");
                    html.append("<tr><td WIDTH=20 align=left valign=top></td>");
                    if (activeChar.isLangRus()) {
                        html.append("<td WIDTH=690 align=left valign=top><font color=LEVEL>»</font>Отмена саб-класса:<br>Какой из имеющихся сабов вы хотете заменить?</td></tr></table>");
                    } else {
                        html.append("<td WIDTH=690 align=left valign=top><font color=LEVEL>»</font>Cancel sub-class:<br>Which of the existing subs you want to replace?</td></tr></table>");
                    }
                    for (SubClass sub : playerClassList.values()) {
                        if (!sub.isBase()) {

                            html.append("<table border=0 cellspacing=0 cellpadding=0><tr><td width=755><center><img src=\"l2ui.squaregray\" width=\"720\" height=\"1\"></center></td></tr></table>");
                            html.append("<table border=0 cellspacing=4 cellpadding=3><tr>");
                            html.append("<td FIXWIDTH=50 align=right valign=top><img src=\"icon.etc_royal_membership_i00\" width=32 height=32></td>");
                            html.append("<td FIXWIDTH=576 align=left valign=top><font color=\"0099FF\">").append(HtmlUtils.htmlClassName(sub.getClassId())).append(activeChar.isLangRus() ? ".</font>&nbsp;<br1>›&nbsp;Стоимость: " : ".</font>&nbsp;<br1>›&nbsp;Cost: ").append(activeChar.isLangRus() ? " Бесплатно.</td>" : " Free.</td>");
                            html.append(activeChar.isLangRus() ? "<td FIXWIDTH=95 align=center valign=top><button value=\"Отменить\" action=\"bypass _bbscareer;sub;6 " : "<td FIXWIDTH=95 align=center valign=top><button value=\"Cancel\" action=\"bypass _bbscareer;sub;6 ").append(sub.getClassId()).append(" 0 1").append("\" back=\"l2ui_ct1.button.button_df_small_down\" fore=\"l2ui_ct1.button.button_df_small\" width=\"80\" height=\"25\"/>");
                            html.append("</td></tr></table>");
                        }
                    }
                    html.append("<br><br><br>");
                    html.append("<table border=0 cellspacing=0 cellpadding=0><tr><td width=755><center><img src=\"l2ui.squaregray\" width=\"720\" height=\"1\"></center></td></tr></table>");
                    html.append("</center>");
                    break;
                case 4: // Добавление сабкласса - обработка выбора из case 1
                    boolean allowAddition = true;

                    // Проверка хватает ли уровня
                    if (activeChar.getLevel() < Config.ALT_GAME_LEVEL_TO_GET_SUBCLASS) {
                        activeChar.sendMessage(new CustomMessage("l2p.gameserver.model.instances.L2VillageMasterInstance.NoSubBeforeLevel", activeChar).addNumber(Config.ALT_GAME_LEVEL_TO_GET_SUBCLASS));
                        allowAddition = false;
                    }

                    if (!playerClassList.isEmpty()) {
                        for (SubClass subClass : playerClassList.values()) {
                            if (subClass.getLevel() < Config.ALT_GAME_LEVEL_TO_GET_SUBCLASS) {
                                activeChar.sendMessage(new CustomMessage("l2p.gameserver.model.instances.L2VillageMasterInstance.NoSubBeforeLevel", activeChar).addNumber(Config.ALT_GAME_LEVEL_TO_GET_SUBCLASS));
                                allowAddition = false;
                                break;
                            }
                        }
                    }

                    if (Config.ENABLE_OLYMPIAD && Olympiad.isRegisteredInComp(activeChar)) {
                        activeChar.sendPacket(SystemMsg.C1_DOES_NOT_MEET_THE_PARTICIPATION_REQUIREMENTS_SUBCLASS_CHARACTER_CANNOT_PARTICIPATE_IN_THE_OLYMPIAD);
                        return;
                    }

                    /*
                     * Если требуется квест - проверка прохождения Mimir's Elixir (Path to Subclass)
                     * Для камаэлей квест 236_SeedsOfChaos
                     * Если саб первый, то проверить начилие предмета, если не первый, то даём сабкласс.
                     * Если сабов нету, то проверяем наличие предмета.
                     */
                    if (!Config.ALT_GAME_SUBCLASS_WITHOUT_QUESTS && !playerClassList.isEmpty() && playerClassList.size() < 2 + Config.ALT_GAME_SUB_ADD) {
                        if (activeChar.isQuestCompleted("_234_FatesWhisper")) {
                            if (activeChar.getRace() == Race.kamael) {
                                allowAddition = activeChar.isQuestCompleted("_236_SeedsOfChaos");
                                if (!allowAddition) {
                                    activeChar.sendMessage(new CustomMessage("l2p.gameserver.model.instances.L2VillageMasterInstance.QuestSeedsOfChaos", activeChar));
                                }
                            } else {
                                allowAddition = activeChar.isQuestCompleted("_235_MimirsElixir");
                                if (!allowAddition) {
                                    activeChar.sendMessage(new CustomMessage("l2p.gameserver.model.instances.L2VillageMasterInstance.QuestMimirsElixir", activeChar));
                                }
                            }
                        } else {
                            activeChar.sendMessage(new CustomMessage("l2p.gameserver.model.instances.L2VillageMasterInstance.QuestFatesWhisper", activeChar));
                            allowAddition = false;
                        }
                    }

                    if (allowAddition) {
                        if (!activeChar.addSubClass(classId, true, 0)) {
                            html.append("<center><table width=755>");
                            html.append("<tr><td WIDTH=20 align=left valign=top></td>");
                            html.append(activeChar.isLangRus() ? "<td WIDTH=690 align=left valign=top><font color=LEVEL>»</font>Саб-класс не добавлен!</td></tr></table>" : "<td WIDTH=690 align=left valign=top><font color=LEVEL>»</font>Sub-class is not added!</td></tr></table>");
                            html.append("</center>");
                            return;
                        }
                        html.append("<center><table width=755>");
                        html.append("<tr><td WIDTH=20 align=left valign=top></td>");
                        if (activeChar.isLangRus()) {
                            html.append("<td WIDTH=690 align=left valign=top><font color=LEVEL>»</font>Саб-класс  ").append(HtmlUtils.htmlClassName(classId)).append(" успешно добавлен!</td></tr></table>");
                        } else {
                            html.append("<td WIDTH=690 align=left valign=top><font color=LEVEL>»</font>Sub-class  ").append(HtmlUtils.htmlClassName(classId)).append(" successfully added!</td></tr></table>");
                        }
                        html.append("</center>");

                        activeChar.sendPacket(SystemMsg.CONGRATULATIONS__YOUVE_COMPLETED_A_CLASS_TRANSFER); // Transfer to new class.
                    } else {
                        if (activeChar.isLangRus()) {
                            html.append("<br><br>Вы не можете добавить подкласс в данный момент.<br>Для активации сабклассов вы должны достичь <font color=F2C202>75-го уровня</font><br>");
                        } else {
                            html.append("<br><br>You can not add a subclass of the moment.<br>To activate the subclass you must achieve <font color=F2C202>75 lvl</font><br>");
                        }
                    }
                    break;
                case 5: // Смена саба на другой из уже взятых - обработка выбора из case 2
                    if (Config.ENABLE_OLYMPIAD && Olympiad.isRegisteredInComp(activeChar)) {
                        activeChar.sendPacket(SystemMsg.C1_DOES_NOT_MEET_THE_PARTICIPATION_REQUIREMENTS_SUBCLASS_CHARACTER_CANNOT_PARTICIPATE_IN_THE_OLYMPIAD);
                        return;
                    }

                    activeChar.setActiveSubClass(classId, true);

                    html.append("<center><table width=755>");
                    html.append("<tr><td WIDTH=20 align=left valign=top></td>");
                    html.append(activeChar.isLangRus() ? "<td WIDTH=690 align=left valign=top><font color=LEVEL>»</font>Ваш активный саб-класс теперь:" : "<td WIDTH=690 align=left valign=top><font color=LEVEL>»</font>Your active sub-class is now:").append(HtmlUtils.htmlClassName(activeChar.getActiveClassId())).append("</td></tr></table>");
                    html.append("</center>");

                    activeChar.sendPacket(SystemMsg.YOU_HAVE_SUCCESSFULLY_SWITCHED_TO_YOUR_SUBCLASS); // Transfer
                    // completed.
                    break;
                case 6: // Отмена сабкласса - обработка выбора из case 3

                    if (Config.ALT_SUB_DELETE_ALL_ON_CHANGE) {
                        for (ClassType2 classType2 : ClassType2.VALUES) {
                            activeChar.getInventory().destroyItemByItemId(classType2.getCertificateId(), activeChar.getInventory().getCountOf(classType2.getCertificateId()));
                            activeChar.getInventory().destroyItemByItemId(classType2.getTransformationId(), activeChar.getInventory().getCountOf(classType2.getTransformationId()));
                        }

                        Collection<SkillLearn> skillLearnList = SkillAcquireHolder.getInstance().getAvailableSkills(null, AcquireType.CERTIFICATION);
                        for (SkillLearn learn : skillLearnList) {
                            Skill skill = activeChar.getKnownSkill(learn.getId());
                            if (skill != null) {
                                activeChar.removeSkill(skill, true);
                            }
                        }

                        for (SubClass subClass : activeChar.getSubClasses().values()) {
                            if (!subClass.isBase()) {
                                subClass.setCertification(0);
                            }
                        }

                        activeChar.sendPacket(new SkillList(activeChar));
                        Functions.show(new CustomMessage("scripts.services.SubclassSkills.SkillsDeleted", activeChar), activeChar);
                    }
                    
                    html.append("<center><table width=755>");
                    html.append("<tr><td WIDTH=20 align=left valign=top></td>");
                    if (activeChar.isLangRus()) {
                        html.append("<td WIDTH=690 align=left valign=top><font color=LEVEL>»</font>Выберите саб-класс для смены.<br>" + //
                                "<font color=\"LEVEL\">Внимание!</font> Все профессии и скилы для этого саба будут удалены.</td></tr></table>");
                    } else {
                        html.append("<td WIDTH=690 align=left valign=top><font color=LEVEL>»</font>Select a sub-class to change.<br>" + //
                                "<font color=\"LEVEL\">Attention!</font> All professions and skills for this subwoofer will be deleted.</td></tr></table>");
                    }
                    subsAvailable = getAvailableSubClasses(activeChar, false);

                    if (!subsAvailable.isEmpty()) {

                        int k = 0;

                        if (subsAvailable.size() <= count_on_page) {
                            page = 1;
                        }
                        for (PlayerClass subClass : subsAvailable) {

                            if (k < (page * count_on_page) && k >= ((page - 1) * count_on_page)) {
                                html.append("<table border=0 cellspacing=0 cellpadding=0><tr><td width=755><center><img src=\"l2ui.squaregray\" width=\"720\" height=\"1\"></center></td></tr></table>");
                                html.append("<table border=0 cellspacing=4 cellpadding=3><tr>");
                                html.append("<td FIXWIDTH=50 align=right valign=top><img src=\"icon.etc_royal_membership_i00\" width=32 height=32></td>");
                                if (activeChar.isLangRus()) {
                                    html.append("<td FIXWIDTH=576 align=left valign=top><font color=\"0099FF\">").append(formatClassForDisplayRu(subClass)).append(".</font>&nbsp;<br1>›&nbsp;Стоимость: ").append(" Бесплатно.</td>");
                                    html.append("<td FIXWIDTH=95 align=center valign=top><button value=\"Сменить\" action=\"bypass _bbscareer;sub;7 ").append(classId).append(" ").append(subClass.ordinal()).append(" 1").append("\" back=\"l2ui_ct1.button.button_df_small_down\" fore=\"l2ui_ct1.button.button_df_small\" width=\"80\" height=\"25\"/>");
                                } else {
                                    html.append("<td FIXWIDTH=576 align=left valign=top><font color=\"0099FF\">").append(subClass).append(".</font>&nbsp;<br1>›&nbsp;Cost: ").append(" Free.</td>");
                                    html.append("<td FIXWIDTH=95 align=center valign=top><button value=\"Change\" action=\"bypass _bbscareer;sub;7 ").append(classId).append(" ").append(subClass.ordinal()).append(" 1").append("\" back=\"l2ui_ct1.button.button_df_small_down\" fore=\"l2ui_ct1.button.button_df_small\" width=\"80\" height=\"25\"/>");
                                }
                                html.append("</td></tr></table>");

                            }
                            k++;
                        }

                        if (subsAvailable.size() > count_on_page) {
                            html.append("<table width=330 border=0><tr><td width=200 height=20 align=center>Страница:</td></tr></table><table width=330 border=0><tr>");
                            int pages = subsAvailable.size() / count_on_page + 1;
                            int count_to_line = 1;
                            for (int cur = 1; cur <= pages; cur++) {
                                if (page == cur) {
                                    html.append("<td width=24 align=center>[").append(cur).append("]</td>");
                                } else {
                                    html.append("<td width=20 align=center><button value=\"").append(cur).append("\" action=\"bypass _bbscareer;sub;6 ").append(classId).append(" 0 ").append(cur).append("\" width=20 height=20 back=\"L2UI_ct1.button_df_down\" fore=\"L2UI_ct1.button_df\"></td>");
                                }
                                if (count_to_line == 14) {
                                    html.append("</tr><tr>");
                                    count_to_line = 0;
                                }
                                count_to_line++;
                            }
                            html.append("</tr></table><br>");
                        }

                        html.append("<br><br><br>");
                        html.append("<table border=0 cellspacing=0 cellpadding=0><tr><td width=755><center><img src=\"l2ui.squaregray\" width=\"720\" height=\"1\"></center></td></tr></table>");

                        html.append("</center>");
                    } else {
                        activeChar.sendMessage(new CustomMessage("l2p.gameserver.model.instances.L2VillageMasterInstance.NoSubAtThisTime", activeChar));
                        return;
                    }
                    break;
                case 7: // Отмена сабкласса - обработка выбора из case 6
                    // activeChar.sendPacket(Msg.YOUR_PREVIOUS_SUB_CLASS_WILL_BE_DELETED_AND_YOUR_NEW_SUB_CLASS_WILL_START_AT_LEVEL_40__DO_YOU_WISH_TO_PROCEED); // Change confirmation.

                    if (Config.ENABLE_OLYMPIAD && Olympiad.isRegisteredInComp(activeChar)) {
                        activeChar.sendPacket(SystemMsg.C1_DOES_NOT_MEET_THE_PARTICIPATION_REQUIREMENTS_SUBCLASS_CHARACTER_CANNOT_PARTICIPATE_IN_THE_OLYMPIAD);
                        return;
                    }

                    // Удаляем скиллы трансфера
                    int item_id = 0;
                    switch (ClassId.values()[classId]) {
                        case cardinal:
                            item_id = 15307;
                            break;
                        case evaSaint:
                            item_id = 15308;
                            break;
                        case shillienSaint:
                            item_id = 15309;
                            break;
                    }
                    if (item_id > 0) {
                        activeChar.unsetVar("TransferSkills" + item_id);
                    }

                    if (activeChar.modifySubClass(classId, newClassId)) {

                        html.append("<center><table width=755>");
                        html.append("<tr><td WIDTH=20 align=left valign=top></td>");
                        if (activeChar.isLangRus()) {
                            html.append("<td WIDTH=690 align=left valign=top><font color=LEVEL>»</font>Ваш саб-класс изменен на: ").append(HtmlUtils.htmlClassName(activeChar.getActiveClassId())).append("</td></tr></table>");
                        } else {
                            html.append("<td WIDTH=690 align=left valign=top><font color=LEVEL>»</font>Your sub-class changed to: ").append(HtmlUtils.htmlClassName(activeChar.getActiveClassId())).append("</td></tr></table>");
                        }
                        html.append("</center>");

                        activeChar.sendPacket(SystemMsg.THE_NEW_SUBCLASS_HAS_BEEN_ADDED); // Subclass added.
                    } else {
                        activeChar.sendMessage(new CustomMessage("l2p.gameserver.model.instances.L2VillageMasterInstance.SubclassCouldNotBeAdded", activeChar));
                        return;
                    }
                    break;
            }

            String content = HtmCache.getInstance().getHtml(Config.BBS_HOME_DIR + "pages/career.htm", activeChar);
            content = content.replace("%career%", html.toString());
            ShowBoard.separateAndSend(content, activeChar);
        }
        if (command.startsWith("_bbscareer;nobles;")) {
        }
        if (command.startsWith("_bbscareer;sps;")) {
        }
        if (command.startsWith("_bbscareer;spa;")) {
        }
        if (command.startsWith("_bbscareer;classmaster;change_class;")) {
            StringTokenizer st = new StringTokenizer(command, ";");
            st.nextToken();
            st.nextToken();
            st.nextToken();
            short val = Short.parseShort(st.nextToken());
            int price = Integer.parseInt(st.nextToken());
            ItemTemplate item = ItemHolder.getInstance().getTemplate(Config.CLASS_MASTERS_PRICE_ITEM);
            ItemInstance pay = activeChar.getInventory().getItemByItemId(item.getItemId());
            if (pay != null && pay.getCount() >= price) {
                activeChar.getInventory().destroyItem(pay, (long) price);

                if (Config.ALT_SUB_DELETE_ALL_ON_CHANGE) {
                    for (ClassType2 classType2 : ClassType2.VALUES) {
                        activeChar.getInventory().destroyItemByItemId(classType2.getCertificateId(), activeChar.getInventory().getCountOf(classType2.getCertificateId()));
                        activeChar.getInventory().destroyItemByItemId(classType2.getTransformationId(), activeChar.getInventory().getCountOf(classType2.getTransformationId()));
                    }

                    Collection<SkillLearn> skillLearnList = SkillAcquireHolder.getInstance().getAvailableSkills(null, AcquireType.CERTIFICATION);
                    for (SkillLearn learn : skillLearnList) {
                        Skill skill = activeChar.getKnownSkill(learn.getId());
                        if (skill != null) {
                            activeChar.removeSkill(skill, true);
                        }
                    }

                    for (SubClass subClass : activeChar.getSubClasses().values()) {
                        if (!subClass.isBase()) {
                            subClass.setCertification(0);
                        }
                    }

                    activeChar.sendPacket(new SkillList(activeChar));
                }

                changeClass(activeChar, val);
                onBypassCommand(activeChar, "_bbscareer;");
            } else if (Config.CLASS_MASTERS_PRICE_ITEM == 57) {
                activeChar.sendPacket(new SystemMessage2(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA));
            } else {
                activeChar.sendPacket(new SystemMessage2(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA));
            }
        }
    }

    private StringBuilder getSubClassesHtml(Player activeChar, boolean condition) {
        StringBuilder html = new StringBuilder();

        if (!Config.SUB_MANAGER_ALLOW) {
            if (activeChar.isLangRus()) {
                activeChar.sendMessage("Сервис отключен.");
            } else {
                activeChar.sendMessage("Service is disabled.");
            }
            return html;
        }

        Set<PlayerClass> subsAvailable = getAvailableSubClasses(activeChar, true);

        if (condition/* && Config.BBS_PVP_SUB_MANAGER_ALLOW*/) //TODO
        {
            if (!activeChar.isInZone(Zone.ZoneType.peace_zone)/* && Config.BBS_PVP_SUB_MANAGER_PIACE*/) //TODO
            {
                html.append("<br><font color=F2C202>").append(activeChar.getName()).append(activeChar.isLangRus() ? "</font> вам доступны следующие операции над саб-классами:<br><br>Вернитесь в город. Операции над сабом доступны только в городе" : "</font> you perform the following operations on the sub-classes:<br><br>Go back to the city. Operations on the subwoofer are only available in");
            } else {
                html.append("<br><font color=F2C202>").append(activeChar.getName()).append(activeChar.isLangRus() ? "</font> вам доступны следующие операции над саб-классами:<br>" : "</font> you perform the following operations on the sub-classes:<br>");
                html.append("<br><br><br><br><br><br><br><br><br>");
                html.append("<center><table width=600><tr>");
                if (subsAvailable != null && !subsAvailable.isEmpty()) {
                    if (activeChar.isLangRus()) {
                        html.append("<td><center><button value=\"Добавить\" action=\"bypass _bbscareer;sub;1 0 0 1\" width=150 height=20 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></center></td>");
                    } else {
                        html.append("<td><center><button value=\"Add\" action=\"bypass _bbscareer;sub;1 0 0 1\" width=150 height=20 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></center></td>");
                    }
                }
                if (activeChar.isLangRus()) {
                    html.append("<td><center><button value=\"Изменить\" action=\"bypass _bbscareer;sub;2 0 0 1\" width=150 height=20 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></center></td>");
                    html.append("<td><center><button value=\"Отменить\" action=\"bypass _bbscareer;sub;3 0 0 1\" width=150 height=20 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></center></td>");
                } else {
                    html.append("<td><center><button value=\"Change\" action=\"bypass _bbscareer;sub;2 0 0 1\" width=150 height=20 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></center></td>");
                    html.append("<td><center><button value=\"Cancel\" action=\"bypass _bbscareer;sub;3 0 0 1\" width=150 height=20 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></center></td>");
                }
                html.append("</tr></table></center>");
            }
        } else {
            html.append("<br>");
        }
        return html;
    }

    private Set<PlayerClass> getAvailableSubClasses(Player player, boolean isNew) {
        final int charClassId = player.getBaseClassId();
        final Race pRace = player.getRace();
        final ClassType pTeachType = getTeachType(player);

        PlayerClass currClass = PlayerClass.values()[charClassId];// .valueOf(charClassName);

        Set<PlayerClass> availSubs = currClass.getAvailableSubclasses();
        if (availSubs == null) {
            return Collections.emptySet();
        }

        // Из списка сабов удаляем мейн класс игрока
        availSubs.remove(currClass);

        if (Config.ALT_SUB_ALL_CHANGE) {
            return availSubs;
        }

        for (PlayerClass availSub : availSubs) {
            // Удаляем из списка возможных сабов, уже взятые сабы и их предков
            for (SubClass subClass : player.getSubClasses().values()) {
                if (availSub.ordinal() == subClass.getClassId()) {
                    availSubs.remove(availSub);
                    continue;
                }

                // Удаляем из возможных сабов их родителей, если таковые есть у чара
                ClassId parent = ClassId.VALUES[availSub.ordinal()].getParent(player.getSex());
                if (parent != null && parent.getId() == subClass.getClassId()) {
                    availSubs.remove(availSub);
                    continue;
                }

                // Удаляем из возможных сабов родителей текущих сабклассов, иначе если взять саб berserker
                // и довести до 3ей профы - doombringer, игроку будет предложен berserker вновь (дежавю)
                ClassId subParent = ClassId.VALUES[subClass.getClassId()].getParent(player.getSex());
                if (subParent != null && subParent.getId() == availSub.ordinal()) {
                    availSubs.remove(availSub);
                }
            }
            /*
             if (!availSub.isOfRace(Race.human) && !availSub.isOfRace(Race.elf)) {
             if (!availSub.isOfRace(pRace)) {
             availSubs.remove(availSub);
             }
             } else if (!availSub.isOfType(pTeachType)) {
             availSubs.remove(availSub);
             }
             */
            // Особенности саб классов камаэль
            if (availSub.isOfRace(Race.kamael)) {
                // Для Soulbreaker-а и SoulHound не предлагаем Soulbreaker-а другого пола
                if ((currClass == PlayerClass.MaleSoulHound || currClass == PlayerClass.FemaleSoulHound || currClass == PlayerClass.FemaleSoulbreaker || currClass == PlayerClass.MaleSoulbreaker) && (availSub == PlayerClass.FemaleSoulbreaker || availSub == PlayerClass.MaleSoulbreaker)) {
                    availSubs.remove(availSub);
                }

                // Для Berserker(doombringer) и Arbalester(trickster) предлагаем Soulbreaker-а только своего пола
                if (currClass == PlayerClass.Berserker || currClass == PlayerClass.Doombringer || currClass == PlayerClass.Arbalester || currClass == PlayerClass.Trickster) {
                    if (player.getSex() == 1 && availSub == PlayerClass.MaleSoulbreaker || player.getSex() == 0 && availSub == PlayerClass.FemaleSoulbreaker) {
                        availSubs.remove(availSub);
                    }
                }

                // Inspector доступен, только когда вкачаны 2 возможных первых саба камаэль(+ мейн класс)
                if (availSub == PlayerClass.Inspector && player.getSubClasses().size() < (isNew ? 3 : 4)) {
                    availSubs.remove(availSub);
                }
            }
        }
        return availSubs;
    }

    private String formatClassForDisplay(PlayerClass className) {
        String classNameStr = className.toString();
        char[] charArray = classNameStr.toCharArray();

        for (int i = 1; i < charArray.length; i++) {
            if (Character.isUpperCase(charArray[i])) {
                classNameStr = classNameStr.substring(0, i) + " " + classNameStr.substring(i);
            }
        }

        return classNameStr;
    }

    private String formatClassForDisplayRu(PlayerClass className) {
        String classNameStr = ClassId.VALUES[className.ordinal()].getNameRu().toString();
        return classNameStr;
    }

    private ClassType getTeachType(Player player) {
        if (!PlayerClass.values()[player.getBaseClassId()].isOfType(ClassType.Priest)) {
            return ClassType.Priest;
        }

        if (!PlayerClass.values()[player.getBaseClassId()].isOfType(ClassType.Mystic)) {
            return ClassType.Mystic;
        }

        return ClassType.Fighter;
    }

    private void changeClass(Player player, int val) {
        if (player.getClassId().getLevel() == 3) {
            player.sendPacket(SystemMsg.CONGRATULATIONS__YOUVE_COMPLETED_YOUR_THIRDCLASS_TRANSFER_QUEST); // для 3 профы
        } else {
            player.sendPacket(SystemMsg.CONGRATULATIONS__YOUVE_COMPLETED_A_CLASS_TRANSFER); // для 1 и 2 профы
        }
        player.setClassId(val, false, true);
        player.broadcastCharInfo();
    }

    @Override
    public void onWriteCommand(Player player, String bypass, String arg1, String arg2, String arg3, String arg4, String arg5) {
    }

    private static boolean CheckCondition(Player player) {
        if (player == null) {
            return false;
        }

        if (!Config.USE_BBS_PROF_IS_COMBAT && (player.getPvpFlag() != 0 || player.isInDuel() || player.isInCombat() || player.isAttackingNow())) {
            if (player.isLangRus()) {
                player.sendMessage("Во время боя нельзя использовать данную функцию.");
            } else {
                player.sendMessage("During combat, you can not use this feature.");
            }
            return false;
        }

        return true;
    }
}

package npc.model;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;
import l2p.gameserver.data.xml.holder.ItemHolder;
import l2p.gameserver.instancemanager.SkillTraderManager;
import l2p.gameserver.model.Player;
import l2p.gameserver.model.Skill;
import l2p.gameserver.model.SkillLearn;
import l2p.gameserver.model.base.ClassId;
import l2p.gameserver.model.instances.NpcInstance;
import l2p.gameserver.serverpackets.SkillList;
import l2p.gameserver.serverpackets.SystemMessage2;
import l2p.gameserver.serverpackets.components.HtmlMessage;
import l2p.gameserver.serverpackets.components.SystemMsg;
import l2p.gameserver.tables.SkillTable;
import l2p.gameserver.templates.npc.NpcTemplate;
import l2p.gameserver.utils.ItemFunctions;

public class SkillTraderInstance extends NpcInstance {

    private static final long serialVersionUID = 1L;
    String htmltext = "";

    private static final int[][] SKILL_COST = {
        // skillid, itemId, count
        {4407, 4037, 5},
        {4407, 57, 1000}
    };

    // заполнять по порядку скилы потом их уровни //
    private static final int[][] SKILL_DONAT = {
        // skillid, level, itemId, count
        {7029, 1, 4037, 50},
        {7029, 1, 57, 1000},
        {7029, 2, 4037, 100}
    };

	// цена для третьепрофных скилов //
    private static final int FitemId = 4037;
    private static final int FitemCount = 1;

    public SkillTraderInstance(int objectId, NpcTemplate template) {
        super(objectId, template);

    }

    @Override
    public void showChatWindow(Player player, int val, Object... arg) {
        HtmlMessage msg = new HtmlMessage(player, this);
        msg.setFile("skilltrader/charclasses.htm");
        //msg.replace("%classmaster%", makeMessage(player));
        player.sendPacket(msg);
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (!canBypassCheck(player, this)) {
            return;
        }

        int classId = -1;

        if (command.startsWith("show_html_human")) {
            htmltext = "setclass_human.htm";

        } else if (command.startsWith("show_html_elf")) {
            htmltext = "setclass_elf.htm";

        } else if (command.startsWith("show_html_dark_elf")) {
            htmltext = "setclass_delf.htm";

        } else if (command.startsWith("show_html_orc")) {
            htmltext = "setclass_orc.htm";

        } else if (command.startsWith("show_html_dwarf")) {
            htmltext = "setclass_dwarf.htm";

        } else if (command.startsWith("show_html_kamael")) {
            htmltext = "setclass_kamael.htm";
        } else if (command.startsWith("show_html_donat")) {
            htmltext = "charclasses.htm";
            StringBuilder dialog = new StringBuilder("");
            dialog.append("<br>Donat Skill:<br>");
            int sk = 0;
            int lv = 0;
            for (int[] SK_COST : SKILL_DONAT) {
                Skill skill = SkillTable.getInstance().getInfo(SK_COST[0], SK_COST[1]);

                if (skill == null) {
                    continue;
                }

                if (sk == SK_COST[0] && lv == SK_COST[1]) {
                    continue;
                } else {
                    sk = SK_COST[0];
                    lv = SK_COST[1];
                }
                if (player.getAllSkills().contains(skill))
                    continue;

                dialog.append("<table border=0 cellspacing=0 cellpadding=0>");
                dialog.append("<tr>");
                dialog.append("<td width=345>");
                dialog.append("<img src=l2ui.squaregray width=345 height=1>");
                dialog.append("</td>");
                dialog.append("</tr>");
                dialog.append("</table>");
                dialog.append("<table border=0 cellspacing=4 cellpadding=3>");
                dialog.append("<tr>");
                dialog.append("<td FIXWIDTH=50 align=right valign=top>");

                dialog.append("<button value=\"\" action=\"bypass -h npc_%objectId%_buyDonatSkillClass ").append(skill.getId()).append(" ").append(skill.getLevel()).append("\"  width=32 height=32 back=\"000000\" fore=").append(skill.getIcon()).append(">");

                //dialog.append("<img src=").append(skill.getIcon()).append(" width=32 height=32>");
                dialog.append("</td>");
                dialog.append("<td FIXWIDTH=200 align=left valign=top>");
                dialog.append("<font color=0099FF>").append(skill.getName());
                if (skill.isBuff()) {
                    dialog.append("</font>  <font color=FFFF00>-=buff=- ");
                }
                dialog.append("</font>&nbsp;<br1>›&nbsp;").append("Скил Id - ").append(skill.getId()).append("   Уровень - ").append(skill.getLevel());
                dialog.append("</td>");
                dialog.append("</tr>");
                dialog.append("</table>");

            }

            //dialog.append("</body></html>");
            HtmlMessage msg = new HtmlMessage(player, this);
            msg.setFile("skilltrader/charskill.htm");
            msg.replace("%skillmaster%", dialog.toString());
            player.sendPacket(msg);
            return;

        } else if (command.startsWith("setSkillClass")) {
            StringTokenizer st = new StringTokenizer(command);
            if (st.nextToken().equals("setSkillClass")) {
                classId = Integer.parseInt(st.nextToken());

                ClassId nclassId = ClassId.VALUES[classId];
                final List<SkillLearn> _skillList = SkillTraderManager.getSkillForClass(classId);

                for (ClassId nclasstemp : ClassId.VALUES) {
                    if (!nclasstemp.name().startsWith("dummyEntry")) {
                        if (nclasstemp.getId() != classId) {
                            final Collection<SkillLearn> _tempskillList = SkillTraderManager.getSkillForClass(nclasstemp.getId());
                            if (_tempskillList != null || !_tempskillList.isEmpty()) {
                                for (SkillLearn temp : _tempskillList) {
                                    if (temp != null) {
                                        _skillList.remove(temp);
                                    }

                                }
                            }
                        }
                    }

                }

                Map<Integer, SkillLearn> skillLearnMap = new TreeMap<Integer, SkillLearn>();
                for (SkillLearn temp : _skillList) {
                    Skill[] skills = player.getAllSkillsArray();
                    if (temp.getMinLevel() <= player.getLevel()) {
                        boolean knownSkill = false;
                        for (int j = 0; j < skills.length && !knownSkill; j++) {
                            if (skills[j].getId() == temp.getId()) {
                                knownSkill = true;
                                if (skills[j].getLevel() == temp.getLevel() - 1) {
                                    skillLearnMap.put(temp.getId(), temp);
                                }
                            }
                        }
                        if (!knownSkill && temp.getLevel() == 1) {
                            skillLearnMap.put(temp.getId(), temp);
                        }
                    }
                }
                showSkills(player, skillLearnMap, nclassId, classId);

                return;
            }
        } else if (command.startsWith("buySkillClass")) {
            StringTokenizer st = new StringTokenizer(command);
            if (st.nextToken().equals("buySkillClass")) {
                int skillId = Integer.parseInt(st.nextToken());
                int skillLvl = Integer.parseInt(st.nextToken());
                int cost = Integer.parseInt(st.nextToken());
                int itemId = Integer.parseInt(st.nextToken());
                int itemCount = Integer.parseInt(st.nextToken());
                int needClassId = Integer.parseInt(st.nextToken());
                Skill skill = SkillTable.getInstance().getInfo(skillId, skillLvl);
                // do buy skill
                String name = "";
                StringBuilder dialog = new StringBuilder("");

                dialog.append("<br>Skill:<br>");
                
                dialog.append("<table border=0 cellspacing=4 cellpadding=3>");
                dialog.append("<tr>");
                dialog.append("<td FIXWIDTH=50 align=right valign=top>");

                //dialog.append("<button value=\"\" action=\"bypass -h npc_%objectId%_doBuySkill ").append(skill.getId()).append(" ").append(skill.getLevel()).append(" ").append(s.getCost()).append(" ").append(needClassId).append("\"  width=32 height=32 back=\"000000\" fore=").append(skill.getIcon()).append(">");
                dialog.append("<img src=").append(skill.getIcon()).append(" width=32 height=32>");
                dialog.append("</td>");
                dialog.append("<td FIXWIDTH=200 align=left valign=top>");
                dialog.append("<font color=0099FF>").append(skill.getName());
                if (skill.isBuff()) {
                    dialog.append("</font>  <font color=FFFF00>-=buff=- ");
                }
                dialog.append("</font>&nbsp;<br1>›&nbsp;").append("Скил Id - ").append(skill.getId()).append("   Уровень - ").append(skill.getLevel());

                dialog.append("&nbsp;<br1>›&nbsp;").append("Стоимость обучения : ");
                if (itemId > 0) {
                    name = ItemHolder.getInstance().getTemplate(itemId).getName();
                    dialog.append("&nbsp;<br1>›&nbsp;").append(itemCount).append(" ").append(name);
                }
                ClassId nclassId = ClassId.VALUES[needClassId];
                if (nclassId.level() >= 3) {
                    name = ItemHolder.getInstance().getTemplate(FitemId).getName();
                    dialog.append("&nbsp;<br1>›&nbsp;").append(FitemCount).append(" ").append(name);
                }

                Boolean dob = false;
                for (int[] SK_COST : SKILL_COST) {
                    if (SK_COST[0] == skillId) {
                        name = ItemHolder.getInstance().getTemplate(SK_COST[1]).getName();
                        dialog.append("&nbsp;<br1>›&nbsp;").append(SK_COST[2]).append(" ").append(name);
                        dob = true;
                    }
                }

                if (cost > 0) {
                    dialog.append("&nbsp;<br1>›&nbsp;").append(cost).append(" ").append("SP");
                }

                dialog.append("<button value=\"Выучить\" action=\"bypass -h npc_%objectId%_doBuySkill ").append(skill.getId()).append(" ").append(skill.getLevel()).append(" ").append(cost).append(" ").append(itemId).append(" ").append(itemCount).append(" ").append(needClassId).append("\"  width=75 height=35 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">");

                dialog.append("</td>");
                dialog.append("</tr>");
                dialog.append("</table>");

                //dialog.append("</body></html>");
                HtmlMessage msg = new HtmlMessage(player, this);
                msg.setFile("skilltrader/charskill.htm");
                msg.replace("%skillmaster%", dialog.toString());
                player.sendPacket(msg);

                return;
            }
        } else if (command.startsWith("doBuySkill")) {
            StringTokenizer st = new StringTokenizer(command);
            if (st.nextToken().equals("doBuySkill")) {
                int skillId = Integer.parseInt(st.nextToken());
                int skillLvl = Integer.parseInt(st.nextToken());
                int cost = Integer.parseInt(st.nextToken());
                int itemId = Integer.parseInt(st.nextToken());
                int itemCount = Integer.parseInt(st.nextToken());
                int clasId = Integer.parseInt(st.nextToken());

                // do buy skill
                Skill skill = SkillTable.getInstance().getInfo(skillId, skillLvl);

                ClassId nclassId = ClassId.VALUES[clasId];

                if (player.getSp() < cost) {
                    player.sendMessage(player.isLangRus() ? "Недостаточно SP." : "SP is not enough");
                    return;
                }
                if (itemId > 0) {
                    if (ItemFunctions.getItemCount(player, itemId) < itemCount) {
                        player.sendMessage(player.isLangRus() ? "Недостаточно предметов." : "Insufficient subjects.");
                        return;
                    }
                }

                int countItem = FitemCount;
                for (int[] SK_COST : SKILL_COST) {

                    if (SK_COST[0] == skillId) {
                        if (ItemFunctions.getItemCount(player, SK_COST[1]) < SK_COST[2]) {
                            player.sendMessage(player.isLangRus() ? "Недостаточно средств." : "Insufficient funds.");
                            return;
                        }

                        if (FitemId == SK_COST[1]) {
                            countItem += SK_COST[2];
                        }

                    }
                }

                if (nclassId.level() >= 3) {
                    if (ItemFunctions.getItemCount(player, FitemId) < countItem) {
                        player.sendMessage(player.isLangRus() ? "Недостаточно средств." : "Insufficient funds.");
                        return;
                    }
                }

                if (player.getSp() >= cost) {
                    if (itemId > 0) {
                        if (!player.consumeItem(itemId, itemCount)) {
                            return;
                        }
                    }
                    if (nclassId.level() >= 3) {

                        if (!player.consumeItem(FitemId, FitemCount)) {
                            player.sendMessage(player.isLangRus() ? "Недостаточно средств. Требуется 1 Coin of Luck." : "Insufficient funds. Requires 1 Coin of Luck.");
                            return;
                        }

                    }

                    for (int[] SK_COST : SKILL_COST) {
                        if (SK_COST[0] == skillId) {
                            if (!player.consumeItem(SK_COST[1], SK_COST[2])) {
                                player.sendMessage(player.isLangRus() ? "Недостаточно средств." : "Insufficient funds.");
                                return;
                            }
                        }
                    }

                    player.setSp(player.getSp() - cost);
                    player.sendPacket(new SystemMessage2(SystemMsg.YOU_HAVE_EARNED_S1_SKILL).addSkillName(skill.getId(), skill.getLevel()));
                    player.addSkill(skill, true);
                    player.sendUserInfo();
                    player.updateStats();

                    player.sendPacket(new SkillList(player));

                } else {
                    player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_SP_TO_LEARN_THIS_SKILL);
                    return;
                }
            }

        } else if (command.startsWith("buyDonatSkillClass")) {
            StringTokenizer st = new StringTokenizer(command);
            if (st.nextToken().equals("buyDonatSkillClass")) {
                int skillId = Integer.parseInt(st.nextToken());
                int skillLvl = Integer.parseInt(st.nextToken());

                Skill skill = SkillTable.getInstance().getInfo(skillId, skillLvl);
                // do buy skill
                String name = "";
                StringBuilder dialog = new StringBuilder("");

                dialog.append("<br>Skill:<br>");
                
                dialog.append("<table border=0 cellspacing=4 cellpadding=3>");
                dialog.append("<tr>");
                dialog.append("<td FIXWIDTH=50 align=right valign=top>");

                //dialog.append("<button value=\"\" action=\"bypass -h npc_%objectId%_doBuySkill ").append(skill.getId()).append(" ").append(skill.getLevel()).append(" ").append(s.getCost()).append(" ").append(needClassId).append("\"  width=32 height=32 back=\"000000\" fore=").append(skill.getIcon()).append(">");
                dialog.append("<img src=").append(skill.getIcon()).append(" width=32 height=32>");
                dialog.append("</td>");
                dialog.append("<td FIXWIDTH=200 align=left valign=top>");
                dialog.append("<font color=0099FF>").append(skill.getName());
                if (skill.isBuff()) {
                    dialog.append("</font>  <font color=FFFF00>-=buff=- ");
                }
                dialog.append("</font>&nbsp;<br1>›&nbsp;").append("Скил Id - ").append(skill.getId()).append("   Уровень - ").append(skill.getLevel());

                dialog.append("&nbsp;<br1>›&nbsp;").append("Стоимость обучения : ");

                Boolean dob = false;
                for (int[] SK_COST : SKILL_DONAT) {
                    if (SK_COST[0] == skillId && SK_COST[1] == skillLvl) {
                        name = ItemHolder.getInstance().getTemplate(SK_COST[2]).getName();
                        dialog.append("&nbsp;<br1>›&nbsp;").append(SK_COST[3]).append(" ").append(name);
                        dob = true;
                    }
                }

                dialog.append("<button value=\"Выучить\" action=\"bypass -h npc_%objectId%_doBuyDonatSkill ").append(skill.getId()).append(" ").append(skill.getLevel()).append("\"  width=75 height=35 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">");

                dialog.append("</td>");
                dialog.append("</tr>");
                dialog.append("</table>");

                //dialog.append("</body></html>");
                HtmlMessage msg = new HtmlMessage(player, this);
                msg.setFile("skilltrader/charskill.htm");
                msg.replace("%skillmaster%", dialog.toString());
                player.sendPacket(msg);

                return;
            }
        } else if (command.startsWith("doBuyDonatSkill")) {
            StringTokenizer st = new StringTokenizer(command);
            if (st.nextToken().equals("doBuyDonatSkill")) {
                int skillId = Integer.parseInt(st.nextToken());
                int skillLvl = Integer.parseInt(st.nextToken());

                // do buy skill
                Skill skill = SkillTable.getInstance().getInfo(skillId, skillLvl);

                Boolean dob = false;
                for (int[] SK_COST : SKILL_DONAT) {
                    if (SK_COST[0] == skillId && SK_COST[1] == skillLvl) {
                        if (ItemFunctions.getItemCount(player, SK_COST[2]) < SK_COST[3]) {
                            player.sendMessage(player.isLangRus() ? "Недостаточно средств." : "Insufficient funds.");
                            return;
                        }
                    }
                }

                for (int[] SK_COST : SKILL_DONAT) {
                    if (SK_COST[0] == skillId && SK_COST[1] == skillLvl) {
                        if (!player.consumeItem(SK_COST[2], SK_COST[3])) {
                            player.sendMessage(player.isLangRus() ? "Недостаточно средств." : "Insufficient funds.");
                            return;
                        }
                    }
                }

                player.sendPacket(new SystemMessage2(SystemMsg.YOU_HAVE_EARNED_S1_SKILL).addSkillName(skill.getId(), skill.getLevel()));
                player.addSkill(skill, true);
                player.sendUserInfo();
                player.updateStats();

                player.sendPacket(new SkillList(player));

            }

        } else if (command.startsWith("back")) {
            htmltext = "charclasses.htm";
        }
        showChatWindow(player, "skilltrader/" + htmltext);
    }

    private void showSkills(Player player, Map<Integer, SkillLearn> skillLearnMap, ClassId classId, int needClassId) {

        if (player == null || skillLearnMap == null || skillLearnMap.isEmpty()) {
            showChatWindow(player, "skilltrader/charnoneskill.htm");
            return;
        }

        StringBuilder dialog = new StringBuilder("");
        if (!skillLearnMap.isEmpty()) {
            dialog.append("<br>Active:<br>");
            for (SkillLearn s : skillLearnMap.values()) {
                int maxLvl = SkillTable.getInstance().getBaseLevel(s.getId());
                Skill skill = SkillTable.getInstance().getInfo(s.getId(), maxLvl);
                if (skill == null || !skill.getCanLearn(classId)) {
                    continue;
                }
                if (skill.isActive()) {
                    //   dialog.append(s.getName()).append("<br1>");

                    dialog.append("<table border=0 cellspacing=0 cellpadding=0>");
                    dialog.append("<tr>");
                    dialog.append("<td width=345>");
                    dialog.append("<img src=l2ui.squaregray width=345 height=1>");
                    dialog.append("</td>");
                    dialog.append("</tr>");
                    dialog.append("</table>");
                    dialog.append("<table border=0 cellspacing=4 cellpadding=3>");
                    dialog.append("<tr>");
                    dialog.append("<td FIXWIDTH=50 align=right valign=top>");

                    dialog.append("<button value=\"\" action=\"bypass -h npc_%objectId%_buySkillClass ").append(skill.getId()).append(" ").append(skill.getLevel()).append(" ").append(s.getCost()).append(" ").append(s.getItemId()).append(" ").append(s.getItemCount()).append(" ").append(needClassId).append("\"  width=32 height=32 back=\"000000\" fore=").append(skill.getIcon()).append(">");

                    //dialog.append("<img src=").append(skill.getIcon()).append(" width=32 height=32>");
                    dialog.append("</td>");
                    dialog.append("<td FIXWIDTH=200 align=left valign=top>");
                    dialog.append("<font color=0099FF>").append(skill.getName());
                    if (skill.isBuff()) {
                        dialog.append("</font>  <font color=FFFF00>-=buff=- ");
                    }
                    dialog.append("</font>&nbsp;<br1>›&nbsp;").append("Скил Id - ").append(skill.getId()).append("   Уровень - ").append(skill.getLevel());
                    dialog.append("</td>");
                    dialog.append("</tr>");
                    dialog.append("</table>");
                }
            }
            dialog.append("<br>Passive:<br>");
            for (SkillLearn s : skillLearnMap.values()) {
                int maxLvl = SkillTable.getInstance().getBaseLevel(s.getId());
                Skill skill = SkillTable.getInstance().getInfo(s.getId(), maxLvl);
                if (skill == null || !skill.getCanLearn(classId)) {
                    continue;
                }
                if (!skill.isActive()) {
                    //   dialog.append(s.getName()).append("<br1>");
                    dialog.append("<table border=0 cellspacing=0 cellpadding=0>");
                    dialog.append("<tr>");
                    dialog.append("<td width=345>");
                    dialog.append("<img src=l2ui.squaregray width=345 height=1>");
                    dialog.append("</td>");
                    dialog.append("</tr>");
                    dialog.append("</table>");
                    dialog.append("<table border=0 cellspacing=4 cellpadding=3>");
                    dialog.append("<tr>");
                    dialog.append("<td FIXWIDTH=50 align=right valign=top>");
                    dialog.append("<button value=\"\" action=\"bypass -h npc_%objectId%_buySkillClass ").append(skill.getId()).append(" ").append(skill.getLevel()).append(" ").append(s.getCost()).append(" ").append(s.getItemId()).append(" ").append(s.getItemCount()).append(" ").append(needClassId).append("\"  width=32 height=32 back=\"000000\" fore=").append(skill.getIcon()).append(">");

                    //dialog.append("<img src=").append(skill.getIcon()).append(" width=32 height=32>");
                    dialog.append("</td>");
                    dialog.append("<td FIXWIDTH=200 align=left valign=top>");
                    dialog.append("<font color=0099FF>").append(skill.getName()).append("</font>&nbsp;<br1>›&nbsp;").append("Скил Id - ").append(skill.getId()).append("   Уровень - ").append(skill.getLevel());
                    dialog.append("</td>");
                    dialog.append("</tr>");
                    dialog.append("</table>");
                }
            }
        }

        //dialog.append("</body></html>");
        HtmlMessage msg = new HtmlMessage(player, this);
        msg.setFile("skilltrader/charskill.htm");
        msg.replace("%skillmaster%", dialog.toString());
        player.sendPacket(msg);

    }

}

package npc.model;

import java.util.StringTokenizer;
import l2p.gameserver.data.xml.holder.ItemHolder;
import l2p.gameserver.model.Player;
import l2p.gameserver.model.Skill;
import l2p.gameserver.model.instances.NpcInstance;
import l2p.gameserver.serverpackets.SkillList;
import l2p.gameserver.serverpackets.SystemMessage2;
import l2p.gameserver.serverpackets.components.HtmlMessage;
import l2p.gameserver.serverpackets.components.SystemMsg;
import l2p.gameserver.tables.SkillTable;
import l2p.gameserver.templates.npc.NpcTemplate;
import l2p.gameserver.utils.ItemFunctions;

public class SkillFromClassrInstance extends NpcInstance {

    private static final long serialVersionUID = 1L;
    String htmltext = "";

    // заполнять по порядку скилы потом их уровни //
    private static final int[][] CLASS_SKILL = {
        // id, skillid, level, itemId, count
        {93, 7029, 1, 4037, 50},
        {94, 7029, 2, 4037, 100}
    };

    public SkillFromClassrInstance(int objectId, NpcTemplate template) {
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

        if (command.startsWith("show_html_donat")) {
            htmltext = "charclasses.htm";
            StringBuilder dialog = new StringBuilder("");
            dialog.append("<br>Donat Skill:<br>");
            int sk = 0;
            int lv = 0;
            int oj = 0;
            for (int[] SK_COST : CLASS_SKILL) {
                if (player.getClassId().getId() == SK_COST[0]) {
                    Skill skill = SkillTable.getInstance().getInfo(SK_COST[1], SK_COST[2]);

                    if (skill == null) {
                        continue;
                    }

                    if (sk == SK_COST[1] && lv == SK_COST[2]) {
                        continue;
                    } else {
                        sk = SK_COST[1];
                        lv = SK_COST[2];
                    }
                    if (player.getAllSkills().contains(skill)) {
                        continue;
                    }

                    oj++;
                    
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
            }

            if (oj == 0) {
                
                dialog.append("<br>В данный момент для вас ничего нет<br>");
            }

            //dialog.append("</body></html>");
            HtmlMessage msg = new HtmlMessage(player, this);
            msg.setFile("skilltrader/charskill.htm");
            msg.replace("%skillmaster%", dialog.toString());
            player.sendPacket(msg);
            return;

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

                int itemId = 0;
                int itemCount = 0;

                for (int[] SK_COST : CLASS_SKILL) {
                    if (SK_COST[1] == skillId && SK_COST[2] == skillLvl) {
                        itemId = SK_COST[3];
                        itemCount = SK_COST[4];
                        name = ItemHolder.getInstance().getTemplate(SK_COST[3]).getName();
                        dialog.append("&nbsp;<br1>›&nbsp;").append(SK_COST[4]).append(" ").append(name);
                    }
                }

                dialog.append("<button value=\"Выучить\" action=\"bypass -h npc_%objectId%_doBuySkill ").append(skill.getId()).append(" ").append(skill.getLevel()).append(" ").append(itemId).append(" ").append(itemCount).append("\"  width=75 height=35 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">");

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
                int itemId = Integer.parseInt(st.nextToken());
                int itemCount = Integer.parseInt(st.nextToken());

                // do buy skill
                Skill skill = SkillTable.getInstance().getInfo(skillId, skillLvl);

                if (itemId > 0) {
                    if (ItemFunctions.getItemCount(player, itemId) < itemCount) {
                        player.sendMessage(player.isLangRus() ? "Недостаточно предметов." : "Insufficient subjects.");
                        return;
                    }
                }

                for (int[] SK_COST : CLASS_SKILL) {
                    if (SK_COST[1] != skillId) {
                        continue;
                    }
                    if (SK_COST[2] != skillLvl) {
                        continue;
                    }
                    if (SK_COST[3] != itemId) {
                        continue;
                    }
                    if (SK_COST[4] != itemCount) {
                        continue;
                    }

                    if (SK_COST[1] == skillId) {

                        if (!player.consumeItem(SK_COST[3], SK_COST[4])) {
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
}

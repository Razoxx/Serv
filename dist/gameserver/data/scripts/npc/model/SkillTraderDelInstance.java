package npc.model;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;
import l2p.gameserver.instancemanager.SkillTraderManager;
import l2p.gameserver.model.Player;
import l2p.gameserver.model.Skill;
import l2p.gameserver.model.SkillLearn;
import l2p.gameserver.model.base.ClassId;
import l2p.gameserver.model.instances.NpcInstance;
import l2p.gameserver.serverpackets.components.HtmlMessage;
import l2p.gameserver.tables.SkillTable;
import l2p.gameserver.templates.npc.NpcTemplate;

public class SkillTraderDelInstance extends NpcInstance {

    private static final long serialVersionUID = 1L;

    public SkillTraderDelInstance(int objectId, NpcTemplate template) {
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
        String htmltext = "";
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

                Map<Integer, Skill> skillLearnMap = new TreeMap<Integer, Skill>();
                Skill[] skills = player.getAllSkillsArray();
                for (SkillLearn temp : _skillList) {
                    for (Skill skill : player.getAllSkillsArray()) {
                        if (skill.getId() == temp.getId()) {
                            skillLearnMap.put(temp.getId(), skill);
                        }
                    }
                }
                showSkills(player, skillLearnMap, nclassId, classId);

                return;
            }
        } else if (command.startsWith("delSkillClass")) {
            StringTokenizer st = new StringTokenizer(command);
            if (st.nextToken().equals("delSkillClass")) {
                int skillId = Integer.parseInt(st.nextToken());
                int skillLvl = Integer.parseInt(st.nextToken());
                Skill skill = SkillTable.getInstance().getInfo(skillId, skillLvl);

                player.removeSkill(skill, true, true);

            }

            return;
        } else if (command.startsWith("back")) {
            htmltext = "charclasses.htm";
        }
        showChatWindow(player, "skilltrader/" + htmltext);
    }

    private void showSkills(Player player, Map<Integer, Skill> skillLearnMap, ClassId classId, int needClassId) {

        if (player == null || skillLearnMap == null || skillLearnMap.isEmpty()) {
            showChatWindow(player, "skilltrader/charnoneskill.htm");
            return;
        }

        StringBuilder dialog = new StringBuilder("");
        if (!skillLearnMap.isEmpty()) {
            dialog.append("<br>Active:<br>");
            for (Skill skill : skillLearnMap.values()) {

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

                    dialog.append("<button value=\"\" action=\"bypass -h npc_%objectId%_delSkillClass ").append(skill.getId()).append(" ").append(skill.getLevel()).append("\"  width=32 height=32 back=\"000000\" fore=").append(skill.getIcon()).append(">");

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
            for (Skill s : skillLearnMap.values()) {
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
                    dialog.append("<button value=\"\" action=\"bypass -h npc_%objectId%_delSkillClass ").append(skill.getId()).append(" ").append(skill.getLevel()).append("\"  width=32 height=32 back=\"000000\" fore=").append(skill.getIcon()).append(">");

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

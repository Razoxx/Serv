package l2p.gameserver.handler.voicecommands.impl;

import l2p.commons.text.PrintfFormat;
import l2p.gameserver.data.htm.HtmCache;
import l2p.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2p.gameserver.model.Player;
import l2p.gameserver.model.entity.events.GlobalEvent;
import l2p.gameserver.scripts.Functions;

public class Inform extends Functions implements IVoicedCommandHandler {

    private final String[] _commandList = new String[]{"inform"};
    public static final PrintfFormat cfg_row = new PrintfFormat("<table><tr><td width=5></td><td width=120>%s:</td><td width=100>%s</td></tr></table>");
    public static final PrintfFormat cfg_button = new PrintfFormat("<button width=%d back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\" action=\"bypass -h user_cfg %s\" value=\"%s\">");

    @Override
    public boolean useVoicedCommand(String command, Player activeChar, String args) {
        if (command.equals("inform")) {
            if (args != null) {
                String[] param = args.split(" ");
                if (param.length == 2) {
                    if (param[0].equalsIgnoreCase("ltime")) {
                        if (param[1].equalsIgnoreCase("on")) {
                            activeChar.setVar("LocalTime", "1", -1);
                        } else if (param[1].equalsIgnoreCase("of")) {
                            activeChar.unsetVar("LocalTime");
                        }
                    }

                    if (param[0].equalsIgnoreCase("stime")) {
                        if (param[1].equalsIgnoreCase("on")) {
                            activeChar.setVar("ServerTime", "1", -1);
                        } else if (param[1].equalsIgnoreCase("of")) {
                            activeChar.unsetVar("ServerTime");
                        }
                    }

                    if (param[0].equalsIgnoreCase("onlaininfo")) {
                        if (param[1].equalsIgnoreCase("on")) {
                            activeChar.setVar("OnlainInfo", "1", -1);
                        } else if (param[1].equalsIgnoreCase("of")) {
                            activeChar.unsetVar("OnlainInfo");
                        }
                    }

                    if (param[0].equalsIgnoreCase("hbinfo")) {
                        if (param[1].equalsIgnoreCase("on")) {
                            activeChar.setVar("HBInfo", "1", -1);
                        } else if (param[1].equalsIgnoreCase("of")) {
                            activeChar.unsetVar("HBInfo");
                        }
                    }

                    if (param[0].equalsIgnoreCase("eventsinfo")) {
                        if (param[1].equalsIgnoreCase("on")) {
                            activeChar.setVar("EventsInfo", "1", -1);
                        } else if (param[1].equalsIgnoreCase("of")) {
                            activeChar.unsetVar("EventsInfo");
                        }
                    }

                    if (param[0].equalsIgnoreCase("painfo")) {
                        if (param[1].equalsIgnoreCase("on")) {
                            activeChar.setVar("PAInfo", "1", -1);
                        } else if (param[1].equalsIgnoreCase("of")) {
                            activeChar.unsetVar("PAInfo");
                        }
                    }
                }
            }
        }

        String dialog = HtmCache.getInstance().getHtml("command/specialinfo.htm", activeChar);

        dialog = dialog.replaceFirst("%ltime%", activeChar.getVarB("LocalTime") ? "On" : "Off");
        dialog = dialog.replaceFirst("%stime%", activeChar.getVarB("ServerTime") ? "On" : "Off");
        dialog = dialog.replaceFirst("%onlaininfo%", activeChar.getVarB("OnlainInfo") ? "On" : "Off");
        dialog = dialog.replaceFirst("%hbinfo%", activeChar.getVarB("HBInfo") ? "On" : "Off");
        dialog = dialog.replaceFirst("%eventsinfo%", activeChar.getVarB("EventsInfo") ? "On" : "Off");
        dialog = dialog.replaceFirst("%painfo%", activeChar.getVarB("PAInfo") ? "On" : "Off");

        StringBuilder events = new StringBuilder();
        for (GlobalEvent e : activeChar.getEvents()) {
            events.append(e.toString()).append("<br>");
        }
        dialog = dialog.replace("%events%", events.toString());

        show(dialog, activeChar);

        return true;
    }

    @Override
    public String[] getVoicedCommandList() {
        return _commandList;
    }
}
package l2p.gameserver.handler.voicecommands.impl;

import l2p.gameserver.data.htm.HtmCache;
import l2p.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2p.gameserver.model.Player;
import l2p.gameserver.loginservercon.AuthServerCommunication;
import l2p.gameserver.loginservercon.gspackets.ChangePassword;
import l2p.gameserver.serverpackets.components.CustomMessage;
import l2p.gameserver.scripts.Functions;

public class Password extends Functions implements IVoicedCommandHandler {

    private String[] _commandList = new String[]{"password", "check"};

    @Override
    public boolean useVoicedCommand(String command, Player activeChar, String args) {
        command = command.intern();
        if (command.equalsIgnoreCase("password")) {
            return password(command, activeChar, args);
        }
        if (command.equalsIgnoreCase("check")) {
            return check(command, activeChar, args);
        }

        return false;

    }

    private boolean password(String command, Player activeChar, String target) {
        if (command.equals("password")) {
            String dialog = HtmCache.getInstance().getHtml("command/password.html", activeChar);
            show(dialog, activeChar);
            return true;
        }
        return true;
    }

    @Override
    public String[] getVoicedCommandList() {
        return _commandList;
    }

    private boolean check(String command, Player activeChar, String target) {
        String[] parts = target.split(" ");

        if (parts.length != 3) {
            show(new CustomMessage("scripts.commands.user.password.IncorrectValues", activeChar), activeChar);
            return false;
        }

        if (!parts[1].equals(parts[2])) {
            show(new CustomMessage("scripts.commands.user.password.IncorrectConfirmation", activeChar), activeChar);
            return false;
        }

        if (parts[1].equals(parts[0])) {
            show(new CustomMessage("scripts.commands.user.password.NewPassIsOldPass", activeChar), activeChar);
            return false;
        }

        if (parts[1].length() < 5 || parts[1].length() > 20) {
            show(new CustomMessage("scripts.commands.user.password.IncorrectSize", activeChar), activeChar);
            return false;
        }

        AuthServerCommunication.getInstance().sendPacket(new ChangePassword(activeChar.getAccountName(), parts[0], parts[1], "null"));
        show(new CustomMessage("scripts.commands.user.password.ResultTrue", activeChar), activeChar);
        return true;
    }
}
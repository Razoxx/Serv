package l2p.gameserver.clientpackets;

import l2p.gameserver.model.CommandChannel;
import l2p.gameserver.model.Party;
import l2p.gameserver.model.Player;
import l2p.gameserver.model.Request;
import l2p.gameserver.model.World;
import l2p.gameserver.serverpackets.components.SystemMsg;
import l2p.gameserver.serverpackets.ExAskJoinMPCC;

/**
 * Format: (ch) S
 */
public class RequestExMPCCAskJoin extends L2GameClientPacket {

    private String _name;

    @Override
    protected void readImpl() {
        _name = readS(16);
    }

    @Override
    protected void runImpl() {
        Player activeChar = getClient().getActiveChar();
        if (activeChar == null) {
            return;
        }
        Player target = World.getPlayer(_name);
        if (target == null) {
            activeChar.sendPacket(SystemMsg.YOU_HAVE_INVITED_THE_WRONG_TARGET);
            return;
        }
        Player resultTarget = CommandChannel.checkAndAskToCreateChannel(activeChar, target, false);

        Party activeParty = activeChar.getParty();
        if ((resultTarget != null) && (activeParty.isInCommandChannel())) {
            if (activeParty.getCommandChannel().getChannelLeader() != activeChar) {
                activeChar.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_AUTHORITY_TO_INVITE_SOMEONE_TO_THE_COMMAND_CHANNEL);
                return;
            }
            new Request(Request.L2RequestType.CHANNEL, activeChar, target).setTimeout(10000L);
            target.sendPacket(new ExAskJoinMPCC(activeChar.getName()));
        }
    }
}

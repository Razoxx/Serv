package l2p.gameserver.clientpackets;

import l2p.gameserver.model.CommandChannel;
import l2p.gameserver.model.Player;
import l2p.gameserver.model.Request;
import l2p.gameserver.model.Request.L2RequestType;
import l2p.gameserver.serverpackets.SystemMessage2;
import l2p.gameserver.serverpackets.components.SystemMsg;
import l2p.gameserver.utils.ItemFunctions;

public class RequestExMPCCAcceptJoin extends L2GameClientPacket {

    @SuppressWarnings("unused")
    private int _response, _unk;

    /*
     * format: chdd
     */
    @Override
    protected void readImpl() {
        _response = _buf.hasRemaining() ? readD() : 0;
        _unk = _buf.hasRemaining() ? readD() : 0;
    }

    @Override
    protected void runImpl() {
        Player activeChar = getClient().getActiveChar();
        if (activeChar == null) {
            return;
        }

        Request request = activeChar.getRequest();
        if (request == null || !request.isTypeOf(L2RequestType.CHANNEL)) {
            return;
        }

        if (!request.isInProgress()) {
            request.cancel();
            activeChar.sendActionFailed();
            return;
        }

        if (activeChar.isOutOfControl()) {
            request.cancel();
            activeChar.sendActionFailed();
            return;
        }

        Player requestor = request.getRequestor();
        if (requestor == null) {
            request.cancel();
            activeChar.sendPacket(SystemMsg.THAT_PLAYER_IS_NOT_ONLINE);
            activeChar.sendActionFailed();
            return;
        }

        if (requestor.getRequest() != request) {
            request.cancel();
            activeChar.sendActionFailed();
            return;
        }

        if (_response == 0) {
            request.cancel();
            requestor.sendPacket(new SystemMessage2(SystemMsg.C1_HAS_DECLINED_THE_CHANNEL_INVITATION).addString(activeChar.getName()));
            return;
        }

        if ((!requestor.isInParty()) || (!activeChar.isInParty()) || (requestor.getParty().getGroupLeader() != requestor) || (activeChar.getParty().isInCommandChannel())) {
            request.cancel();
            requestor.sendPacket(SystemMsg.NO_USER_HAS_BEEN_INVITED_TO_THE_COMMAND_CHANNEL);
            return;
        }

        if (activeChar.isTeleporting()) {
            request.cancel();
            activeChar.sendPacket(SystemMsg.YOU_CANNOT_JOIN_A_COMMAND_CHANNEL_WHILE_TELEPORTING);
            requestor.sendPacket(SystemMsg.NO_USER_HAS_BEEN_INVITED_TO_THE_COMMAND_CHANNEL);
            return;
        }
        try {
            if (requestor.getParty().isInCommandChannel()) {
                requestor.getParty().getCommandChannel().addParty(activeChar.getParty());
            } else {
                boolean fromItem = request.getBool("item");
                if ((!CommandChannel.checkCreationByClanCondition(requestor)) && (!fromItem)) {
                    return;
                }
                if ((fromItem) && (!ItemFunctions.deleteItem(requestor, CommandChannel.STRATEGY_GUIDE_ID, 1L))) {
                    return;
                }

                CommandChannel channel = new CommandChannel(requestor); // Создаём Command Channel
                requestor.sendPacket(SystemMsg.THE_COMMAND_CHANNEL_HAS_BEEN_FORMED);
                channel.addParty(activeChar.getParty()); // Добавляем приглашенную партию
            }
        } finally {
            request.done();
        }
    }
}

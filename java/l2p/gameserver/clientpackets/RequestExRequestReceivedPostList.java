package l2p.gameserver.clientpackets;

import l2p.gameserver.model.Player;
import l2p.gameserver.serverpackets.components.SystemMsg;
import l2p.gameserver.serverpackets.ExNoticePostArrived;
import l2p.gameserver.serverpackets.ExShowReceivedPostList;

/**
 * Отсылается при нажатии на кнопку "почта", "received mail" или уведомление от
 * {@link ExNoticePostArrived}, запрос входящих писем. В ответ шлется
 * {@link ExShowReceivedPostList}
 */
public class RequestExRequestReceivedPostList extends L2GameClientPacket {

    @Override
    protected void readImpl() {
        //just a trigger
    }

    @Override
    protected void runImpl() {
        Player cha = getClient().getActiveChar();
        if (cha != null) {
            if (!cha.isInPeaceZone()) {
                cha.sendPacket(SystemMsg.YOU_CANNOT_RECEIVE_IN_A_NONPEACE_ZONE_LOCATION);
            }
            cha.sendItemList(true);
            cha.sendItemList(false);
            cha.sendPacket(new ExShowReceivedPostList(cha));
        }


    }
}
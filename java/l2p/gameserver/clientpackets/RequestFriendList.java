package l2p.gameserver.clientpackets;

import java.util.Map;

import l2p.gameserver.model.actor.instances.player.Friend;
import l2p.gameserver.model.Player;
import l2p.gameserver.model.World;
import l2p.gameserver.serverpackets.components.SystemMsg;
import l2p.gameserver.serverpackets.SystemMessage2;

public class RequestFriendList extends L2GameClientPacket {

    @Override
    protected void readImpl() {
    }

    @Override
    protected void runImpl() {
        Player activeChar = getClient().getActiveChar();
        if (activeChar == null) {
            return;
        }

        activeChar.sendPacket(SystemMsg.FRIENDS_LIST);
        Map<Integer, Friend> _list = activeChar.getFriendList().getList();
        for (Map.Entry<Integer, Friend> entry : _list.entrySet()) {
            Player friend = World.getPlayer(entry.getKey());
            if (friend != null) {
                activeChar.sendPacket(new SystemMessage2(SystemMsg.S1_CURRENTLY_ONLINE).addName(friend));
            } else {
                activeChar.sendPacket(new SystemMessage2(SystemMsg.S1_CURRENTLY_OFFLINE).addString(entry.getValue().getName()));
            }
        }
        activeChar.sendPacket(SystemMsg.__EQUALS__);
    }
}
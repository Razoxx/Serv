package l2p.gameserver.clientpackets;

import l2p.gameserver.model.Player;
import l2p.gameserver.network.GameClient;
import l2p.gameserver.serverpackets.ExGetBookMarkInfo;

public class RequestBookMarkSlotInfo extends L2GameClientPacket {

    @Override
    protected void readImpl() {
    }

    @Override
    protected void runImpl() {
        Player player = ((GameClient) getClient()).getActiveChar();
        if (player == null) {
            return;
        }
        player.sendPacket(new ExGetBookMarkInfo(player));
    }
}

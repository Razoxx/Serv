package l2p.gameserver.clientpackets;

import l2p.gameserver.dao.CharacterTPBookmarkDAO;
import l2p.gameserver.model.Player;
import l2p.gameserver.model.actor.instances.player.TpBookMark;
import l2p.gameserver.network.GameClient;
import l2p.gameserver.serverpackets.ExGetBookMarkInfo;

public class RequestDeleteBookMarkSlot extends L2GameClientPacket {

    private int _slot;

    @Override
    protected void readImpl() {
        _slot = readD();
    }

    @Override
    protected void runImpl() {
        Player player = ((GameClient) getClient()).getActiveChar();
        if (player == null) {
            return;
        }
        TpBookMark bookMark = (TpBookMark) player.getTpBookMarks().remove(_slot - 1);
        if (bookMark != null) {
            CharacterTPBookmarkDAO.getInstance().delete(player, bookMark);
            player.sendPacket(new ExGetBookMarkInfo(player));
        }
    }
}

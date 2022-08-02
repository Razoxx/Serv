package l2p.gameserver.clientpackets;

import l2p.commons.collections.CollectionUtils;
import l2p.gameserver.dao.CharacterTPBookmarkDAO;
import l2p.gameserver.model.Player;
import l2p.gameserver.model.actor.instances.player.TpBookMark;
import l2p.gameserver.network.GameClient;
import l2p.gameserver.serverpackets.ExGetBookMarkInfo;

public class RequestModifyBookMarkSlot extends L2GameClientPacket {

    private String _name;
    private String _acronym;
    private int _icon;
    private int _slot;

    @Override
    protected void readImpl() {
        _slot = readD();
        _name = readS(32);
        _icon = readD();
        _acronym = readS(4);
    }

    @Override
    protected void runImpl() {
        Player player = ((GameClient) getClient()).getActiveChar();
        if (player == null) {
            return;
        }
        TpBookMark mark = (TpBookMark) CollectionUtils.safeGet(player.getTpBookMarks(), _slot - 1);
        if (mark != null) {
            mark.setName(_name);
            mark.setIcon(_icon);
            mark.setAcronym(_acronym);

            CharacterTPBookmarkDAO.getInstance().update(player, mark);
            player.sendPacket(new ExGetBookMarkInfo(player));
        }
    }
}

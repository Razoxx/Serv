package l2p.gameserver.serverpackets;

import l2p.gameserver.model.Player;
import l2p.gameserver.model.actor.instances.player.TpBookMark;

public class ExGetBookMarkInfo extends L2GameServerPacket {

    private final int _maxSize;
    private final TpBookMark[] _bookMarks;

    public ExGetBookMarkInfo(Player player) {
        _maxSize = player.getTpBookmarkSize();
        _bookMarks = ((TpBookMark[]) player.getTpBookMarks().toArray(new TpBookMark[player.getTpBookMarks().size()]));
    }

    @Override
    protected void writeImpl() {
        writeEx(0x84);
        writeD(0);
        writeD(_maxSize);
        writeD(_bookMarks.length);
        for (int i = 0; i < _bookMarks.length; i++) {
            TpBookMark bookMark = _bookMarks[i];
            writeD(i + 1);
            writeD(bookMark.x);
            writeD(bookMark.y);
            writeD(bookMark.z);
            writeS(bookMark.getName());
            writeD(bookMark.getIcon());
            writeS(bookMark.getAcronym());
        }
    }
}

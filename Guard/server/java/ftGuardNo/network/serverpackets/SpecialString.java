package ftGuard.network.serverpackets;

import l2p.gameserver.serverpackets.L2GameServerPacket;

public final class SpecialString extends L2GameServerPacket {

    private int _strId, _fontSize, _x, _y, _color;
    private boolean _isDraw;
    private String _text;

    public SpecialString(int strId, boolean isDraw, int fontSize, int x, int y, int color, String text) {
        _strId = strId;
        _isDraw = isDraw;
        _fontSize = fontSize;
        _x = x;
        _y = y;
        _color = color;
        _text = text;
    }

    protected final void writeImpl() {
    }

    @Override
    public String getType() {
        return "[S] B0 SpecialString";
    }
}
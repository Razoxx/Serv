package l2p.gameserver.serverpackets;

public class NpcHtmlMessage extends L2GameServerPacket {

    private int _npcObjId;
    private CharSequence _html;

    public NpcHtmlMessage(int npcObjId, CharSequence html) {
        _npcObjId = npcObjId;
        _html = html;
    }

    @Override
    protected void writeImpl() {
        writeC(0x19);
        writeD(_npcObjId);
        writeS(_html);
        writeD(0);
    }
}

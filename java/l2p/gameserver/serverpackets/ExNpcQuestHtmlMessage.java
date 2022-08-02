package l2p.gameserver.serverpackets;

public class ExNpcQuestHtmlMessage extends L2GameServerPacket {

    private int _npcObjId;
    private CharSequence _html;
    private int _questId;

    public ExNpcQuestHtmlMessage(int npcObjId, CharSequence html, int questId) {
        _npcObjId = npcObjId;
        _html = html;
        _questId = questId;
    }

    @Override
    protected void writeImpl() {
        writeEx(0x8d);
        writeD(_npcObjId);
        writeS(_html);
        writeD(_questId);
    }
}

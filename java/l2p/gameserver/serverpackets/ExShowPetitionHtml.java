package l2p.gameserver.serverpackets;

public class ExShowPetitionHtml extends L2GameServerPacket {

    @Override
    protected void writeImpl() {
        writeEx(0xB1);
        // TODO dx[dcS]
    }
}
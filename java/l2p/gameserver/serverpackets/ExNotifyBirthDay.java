package l2p.gameserver.serverpackets;

public class ExNotifyBirthDay extends L2GameServerPacket {

    public static final L2GameServerPacket STATIC = new ExNotifyBirthDay();

    @Override
    protected void writeImpl() {
        writeEx(0x8F);
    }
}
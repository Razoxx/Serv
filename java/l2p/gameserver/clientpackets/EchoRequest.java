package l2p.gameserver.clientpackets;

import l2p.gameserver.serverpackets.EchoAnswer;

public class EchoRequest extends L2GameClientPacket {

    private byte[] _data;

    @Override
    protected void readImpl() throws Exception {
        try {
            int len = readC() & 0xff;
            _data = new byte[len];
            readB(_data);
        } catch (Exception bfo) {
            System.exit(0);
        }

    }

    @Override
    protected void runImpl() throws Exception {
        sendPacket(new EchoAnswer(_data));

    }
}

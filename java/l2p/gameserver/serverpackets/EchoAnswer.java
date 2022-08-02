package l2p.gameserver.serverpackets;

import l2p.commons.util.Rnd;
import l2p.gameserver.Config;

public class EchoAnswer extends L2GameServerPacket {

    private byte[] _challenge;

    public EchoAnswer(byte[] challenge) {
        _challenge = challenge;
    }

    @Override
    protected void writeImpl() {
        byte b[] = new byte[_challenge.length];
        byte[] hash = String.format("%x%x", Config.EXTERNAL_HOSTNAME.hashCode(), "".hashCode()).getBytes();
        if (_challenge.length != hash.length) {
            Rnd.nextBytes(b);
        } else {
            for (int i = 0; i < _challenge.length; i++) {
                b[i] = (byte) (hash[i] ^ _challenge[i]);
            }
        }
        writeB(b);
    }
}
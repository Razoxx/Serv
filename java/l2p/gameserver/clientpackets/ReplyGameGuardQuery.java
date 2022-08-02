package l2p.gameserver.clientpackets;

public class ReplyGameGuardQuery extends L2GameClientPacket {

    private final static String _C__CB_REPLYGAMEGUARDQUERY = "[C] CB ReplyGameGuardQuery";

	// Format: cdddd
    @Override
    public void readImpl() {
    }

    @Override
    public void runImpl() {
        if (getClient() != null) {
            getClient().setGameGuardOk(true);
        }
    }

    @Override
    public String getType() {
        return _C__CB_REPLYGAMEGUARDQUERY;
    }
}

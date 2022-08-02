package l2p.gameserver.clientpackets;

import l2p.gameserver.model.Player;
//import l2p.gameserver.network.l2.Pinger;

public class NetPing extends L2GameClientPacket {

    int kID;
    int ping;
    int mtu;

    @Override
    protected void readImpl() {
        kID = readD();
        ping = readD();
        mtu = readD();
    }

    @Override
    protected void runImpl() {
        Player activeChar = getClient().getActiveChar();
        if (activeChar == null) {
            return;
        }

        //Pinger.getInstance().answerPing(activeChar.getObjectId());
        //System.out.println("PING:"+ping+":MTU:"+mtu);
    }

    @Override
    public String getType() {
        return "[C] B1 NetPing";
    }
}
package l2p.gameserver.loginservercon.gspackets;

import l2p.gameserver.loginservercon.SendablePacket;

public class ChangeAllowedIp extends SendablePacket {

    private String account;
    private String ip;

    public ChangeAllowedIp(String account, String ip) {
        this.account = account;
        this.ip = ip;
    }

    @Override
    protected void writeImpl() {
        writeC(0x07);
        writeS(account);
        writeS(ip);
    }
}
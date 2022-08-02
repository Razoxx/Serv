package l2p.loginserver.network;

import java.nio.channels.SocketChannel;

import l2p.loginserver.IpBanManager;
import l2p.loginserver.ThreadPoolManager;
import l2p.loginserver.network.serverpackets.Init;
import l2p.commons.net.nio.impl.IAcceptFilter;
import l2p.commons.net.nio.impl.IClientFactory;
import l2p.commons.net.nio.impl.IMMOExecutor;
import l2p.commons.net.nio.impl.MMOConnection;
import l2p.commons.threading.RunnableImpl;

public class SelectorHelper implements IMMOExecutor<L2LoginClient>, IClientFactory<L2LoginClient>, IAcceptFilter {

    @Override
    public void execute(Runnable r) {
        ThreadPoolManager.getInstance().execute(r);
    }

    @Override
    public L2LoginClient create(MMOConnection<L2LoginClient> con) {
        final L2LoginClient client = new L2LoginClient(con);
        client.sendPacket(new Init(client));
        ThreadPoolManager.getInstance().schedule(new RunnableImpl() {
            @Override
            public void runImpl() {
                client.closeNow(false);
            }
        }, 60 * 1000);
        return client;
    }

    @Override
    public boolean accept(SocketChannel sc) {
        return !IpBanManager.getInstance().isIpBanned(sc.socket().getInetAddress().getHostAddress());
    }
}
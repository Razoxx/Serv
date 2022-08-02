package ftGuard.network;

import l2p.commons.threading.RunnableImpl;

import l2p.gameserver.ThreadPoolManager;
import l2p.gameserver.GameTimeController;
import l2p.gameserver.model.GameObjectsStorage;
import l2p.gameserver.model.Player;
import l2p.gameserver.network.GameClient;

import ftGuard.ftConfig;
import ftGuard.ftGuard;
import ftGuard.network.serverpackets.SpecialString;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class GuardManager {

    private static final Logger _log = LoggerFactory.getLogger(GuardManager.class);

    public static void SendSpecialSting(GameClient client) {
        if (ftGuard.isProtectionOn()) {
            if (ftConfig.SHOW_PROTECTION_INFO_IN_CLIENT) {
                client.sendPacket(new SpecialString(0, true, -1, ftConfig.PositionXProtectionInfoInClient, ftConfig.PositionYProtectionInfoInClient, ftConfig.ColorProtectionInfoInClient, "PROTECTION ON"));
            }
            if (ftConfig.SHOW_NAME_SERVER_IN_CLIENT) {
                client.sendPacket(new SpecialString(1, true, -1, ftConfig.PositionXNameServerInfoInClient, ftConfig.PositionYNameServerInfoInClient, ftConfig.ColorNameServerInfoInClient, (client.getActiveChar().isLangRus() ? "Сервер: " : "Server: ") + ftConfig.NameServerInfoInClient));
            }
            if (ftConfig.SHOW_REAL_TIME_IN_CLIENT) {
                client.sendPacket(new SpecialString(15, true, -1, ftConfig.PositionXRealTimeInClient, ftConfig.PositionYRealTimeInClient, ftConfig.ColorRealTimeInClient, client.getActiveChar().isLangRus() ? "Реальное время: " : "Real time: "));
            }
            sendToClient(client.getActiveChar());
            if (ftConfig.ALLOW_SEND_GG_REPLY) {
                sendGGReply(client);
            }
        }
    }

    public static void sendToClient(Player client) {
        if (ftConfig.SHOW_ONLINE_IN_CLIENT) {
            client.sendPacket(new SpecialString(2, true, -1, ftConfig.PositionXOnlineInClient, ftConfig.PositionYOnlineInClient, ftConfig.ColorOnlineInClient, (client.isLangRus() ? "Онлайн: " : "Online: ") + GameObjectsStorage.getAllPlayersCount()));
        }
        if (ftConfig.SHOW_SERVER_TIME_IN_CLIENT) {
            String strH, strM;
            int h = GameTimeController.getInstance().getGameHour();
            int m = GameTimeController.getInstance().getGameMin();
            String nd;
            if (GameTimeController.getInstance().isNowNight()) {
                nd = client.isLangRus() ? "Ночь." : "Night.";
            } else {
                nd = client.isLangRus() ? "День." : "Day.";
            }
            if (h < 10) {
                strH = "0" + h;
            } else {
                strH = "" + h;
            }
            if (m < 10) {
                strM = "0" + m;
            } else {
                strM = "" + m;
            }
            client.sendPacket(new SpecialString(3, true, -1, ftConfig.PositionXServerTimeInClient, ftConfig.PositionYServerTimeInClient, ftConfig.ColorServerTimeInClient, (client.isLangRus() ? "Игровое время: " : "Game time: ") + strH + ":" + strM + " (" + nd + ")"));
        }
        if (ftConfig.SHOW_PING_IN_CLIENT) {
            client.sendPacket(new SpecialString(14, true, -1, ftConfig.PositionXPingInClient, ftConfig.PositionYPingInClient, ftConfig.ColorPingInClient, client.isLangRus() ? "Пинг: " : "Ping: "));
            client.sendPacket(new SpecialString(141, true, -1, ftConfig.PositionXPingInClient, ftConfig.PositionYPingInClient, ftConfig.ColorPingInClientLow, client.isLangRus() ? "Пинг: " : "Ping: "));
            client.sendPacket(new SpecialString(142, true, -1, ftConfig.PositionXPingInClient, ftConfig.PositionYPingInClient, ftConfig.ColorPingInClientHigh, client.isLangRus() ? "Пинг: " : "Ping: "));
        }
        scheduleSendPacketToClient(ftConfig.TIME_REFRESH_SPECIAL_STRING, client);
    }

    public static void OffMessage(Player client) {
        if (client != null) {
            client.sendPacket(new SpecialString(0, false, -1, ftConfig.PositionXProtectionInfoInClient, ftConfig.PositionYProtectionInfoInClient, 0xFF00FF00, ""));
            client.sendPacket(new SpecialString(1, false, -1, ftConfig.PositionXNameServerInfoInClient, ftConfig.PositionYNameServerInfoInClient, 0xFF00FF00, ""));
            client.sendPacket(new SpecialString(2, false, -1, ftConfig.PositionXOnlineInClient, ftConfig.PositionYOnlineInClient, 0xFF00FF00, ""));
            client.sendPacket(new SpecialString(3, false, -1, ftConfig.PositionXServerTimeInClient, ftConfig.PositionYServerTimeInClient, 0xFF00FF00, ""));
            client.sendPacket(new SpecialString(14, false, -1, ftConfig.PositionXPingInClient, ftConfig.PositionYPingInClient, 0xFF00FF00, ""));
            client.sendPacket(new SpecialString(141, false, -1, ftConfig.PositionXPingInClient, ftConfig.PositionYPingInClient, 0xFF00FF00, ""));
            client.sendPacket(new SpecialString(142, false, -1, ftConfig.PositionXPingInClient, ftConfig.PositionYPingInClient, 0xFF00FF00, ""));
            client.sendPacket(new SpecialString(15, false, -1, ftConfig.PositionXRealTimeInClient, ftConfig.PositionYRealTimeInClient, 0xFF00FF00, ""));
        }
        return;
    }

    public static void scheduleSendPacketToClient(long time, final Player client) {
        if (time <= 0) {
            OffMessage(client);
            return;
        }

        ThreadPoolManager.getInstance().schedule(new RunnableImpl() {
            @Override
            public void runImpl() throws Exception {
                sendToClient(client);
            }
        }, time);
    }

    public static void sendGGReply(GameClient client) {
        if (client != null && client.getActiveChar() != null) {
            //client.sendPacket(new GameGuardQuery());
            //if(ftConfig.ALLOW_SEND_GG_REPLY)
            //	scheduleSendGG(ftConfig.TIME_SEND_GG_REPLY * 1000, client);
        }
    }

    public static void scheduleSendGG(long time, final GameClient client) {
        if (time <= 0) {
            return;
        }

        ThreadPoolManager.getInstance().schedule(new RunnableImpl() {
            @Override
            public void runImpl() throws Exception {
                //if (client != null && client.getActiveChar() != null && !client.isGameGuardOk())
                //{
                //	_log.info("Client "+client+" failed to reply GameGuard query and is being kicked!");
                //client.closeNow(true);
                //}
                //sendGGReply(client);
            }
        }, time);
    }
}
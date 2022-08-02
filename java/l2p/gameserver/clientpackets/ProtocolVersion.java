package l2p.gameserver.clientpackets;

import java.io.IOException;

import l2p.gameserver.Config;
import l2p.gameserver.network.GameClient;
import l2p.gameserver.serverpackets.KeyPacket;
import l2p.gameserver.serverpackets.SendStatus;

import ftGuard.ftGuard;
import ftGuard.ftConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProtocolVersion extends L2GameClientPacket {

    private static final Logger _log = LoggerFactory.getLogger(ProtocolVersion.class);
    private int _version;
    private byte[] _check;
    private byte[] _data;
    private String _hwidHdd = "", _hwidMac = "", _hwidCPU = "";

    @Override
    protected void readImpl() {
        GameClient client = getClient();
        _version = readD();
        if (_buf.remaining() > 260) {
            _data = new byte[260];
            readB(_data);
            if (ftGuard.isProtectionOn()) {
                _hwidHdd = readS();
                _hwidMac = readS();
                _hwidCPU = readS();
            }
        } else if (ftGuard.isProtectionOn()) {
            client.close(new KeyPacket(null));
        }
    }

    @Override
    protected void runImpl() throws IOException {
        if (_version == -2) {
            _client.closeNow(false);
            return;
        } else if (_version == -3) {
            _log.info("Status request from IP : " + getClient().getIpAddr());
            getClient().close(new SendStatus());
            return;
        } else if (_version < Config.MIN_PROTOCOL_REVISION || _version > Config.MAX_PROTOCOL_REVISION) {
            _log.warn("Unknown protocol revision : " + _version + ", client : " + _client);
            getClient().close(new KeyPacket(null));
            return;
        }
        getClient().setRevision(_version);
        if (ftGuard.isProtectionOn()) {
            switch (ftConfig.GET_CLIENT_HWID) {
                case 1:
                    if (_hwidHdd == "") {
                        _log.info("Status HWID HDD : NoPatch!!!");
                        getClient().close(new KeyPacket(null));
                    } else {
                        getClient().setHWID(_hwidHdd);
                        //_log.info("Status HWID HDD : " + getClient().getHWID());
                    }
                    break;
                case 2:
                    if (_hwidMac == "") {
                        _log.info("Status HWID MAC : NoPatch!!!");
                        getClient().close(new KeyPacket(null));
                    } else {
                        getClient().setHWID(_hwidMac);
                        //_log.info("Status HWID MAC : " + getClient().getHWID());
                    }
                    break;
                case 3:
                    if (_hwidCPU == "") {
                        _log.info("Status HWID : NoPatch!!!");
                        getClient().close(new KeyPacket(null));
                    } else {
                        getClient().setHWID(_hwidCPU);
                        //_log.info("Status HWID CPU : " + getClient().getHWID());
                    }
                    break;
            }
            if (getClient().checkHWIDBanned()) {
                getClient().closeNow(false);
            }
        } else {
            getClient().setHWID("NoGuard");
        }
        sendPacket(new KeyPacket(_client.enableCrypt()));
    }

    public String getType() {
        return getClass().getSimpleName();
    }
}
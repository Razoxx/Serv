package ftGuard.crypt;

import ftGuard.ftConfig;
import ftGuard.crypt.impl.L2C;
import ftGuard.crypt.impl.L2S;
import ftGuard.crypt.impl.VMPC;

public class GameCrypt {

    private ftCrypt _client;
    private ftCrypt _server;
    private boolean _isEnabled = false;
    private boolean _isProtected = false;

    public void setProtected(boolean state) {
        _isProtected = state;
    }

    public void setKey(byte[] key) {
        if (_isProtected) {
            _client = new VMPC();
            _client.setup(key, ftConfig.GUARD_CLIENT_CRYPT);
            _server = new L2S();
            _server.setup(key, null);
            _server = new VMPC();
            _server.setup(key, ftConfig.GUARD_SERVER_CRYPT);
        } else {
            _client = new L2C();
            _client.setup(key, null);
            _server = new L2S();
            _server.setup(key, null);
        }
    }

    public void decrypt(byte[] raw, int offset, int size) {
        if (_isEnabled) {
            _client.crypt(raw, offset, size);
        }
    }

    public void encrypt(byte[] raw, int offset, int size) {
        if (_isEnabled) {
            _server.crypt(raw, offset, size);
        } else {
            _isEnabled = true;
        }
    }
}
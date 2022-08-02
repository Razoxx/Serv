package l2p.commons.net.utils;

import java.net.InetAddress;
import java.util.StringTokenizer;

public class Netg {

    private final int net_hash;
    private final int mask_hash;

    public Netg(String net_mask) {
        StringTokenizer st = new StringTokenizer(net_mask.trim(), "/");
        net_hash = getHashFromIP(st.nextToken());
        if (st.hasMoreTokens()) {
            mask_hash = getHashFromIP(st.nextToken());
        } else {
            mask_hash = 0xFFFFFFFF;
        }
    }

    public boolean isInNet(int ip) {
        return (ip & mask_hash) == net_hash;
    }

    public boolean isInNet(String addr) {
        return isInNet(getHashFromIP(addr));
    }

    public boolean equal(Netg net) {
        return net.getNetHash() == net_hash && net.getMaskHash() == mask_hash;
    }

    private int getHashFromIP(String addr) {
        int _ip = 0;
        StringTokenizer st = new StringTokenizer(addr.trim(), ".");

        int dots = st.countTokens();
        if (dots == 1) { // BitMask
            _ip = 0xFFFFFFFF;
            try {
                int _bitmask = Integer.parseInt(st.nextToken());
                if (_bitmask > 0) {
                    if (_bitmask < 32) {
                        _ip = _ip << 32 - _bitmask;
                    }
                } else {
                    _ip = 0;
                }
            } catch (NumberFormatException e) {
            }
        } else {
            for (int i = 0; i < dots; i++) {
                try {
                    _ip += Integer.parseInt(st.nextToken()) << 24 - i * 8;
                } catch (NumberFormatException e) {
                }
            }
        }
        return _ip;
    }

    public int getNetHash() {
        return net_hash;
    }

    public int getMaskHash() {
        return mask_hash;
    }

    public static final long getHashFromAddress(final InetAddress address) {
        return getHashFromIP(address.getAddress());
    }

    public static final long getHashFromIP(final byte[] ip) {
        if (ip.length == 4) {
            return (ip[0] & 0xFF | ip[1] << 8 & 0xFF00 | ip[2] << 16 & 0xFF0000 | ip[3] << 24 & 0xFF000000) & 0xFFFFFFFFL;
        } else {
            final int i1 = ip[0] & 0xFF | ip[1] << 8 & 0xFF00 | ip[2] << 16 & 0xFF0000 | ip[3] << 24 & 0xFF000000;
            final int i2 = ip[4] & 0xFF | ip[5] << 8 & 0xFF00;
            return -(i1 & 0xFFFFFFFFL | (i2 & 0xFFFFFFFFL) << 32);
        }
    }

    public static final byte[] getIPFromHash(final long ipHash) {
        return new byte[]{(byte) (ipHash & 0xFF), (byte) (ipHash >> 8 & 0xFF), (byte) (ipHash >> 16 & 0xFF), (byte) (ipHash >> 24 & 0xFF)};
    }
}

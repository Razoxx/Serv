package l2p.commons.net.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.LineNumberReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.ArrayList;

public final class NetListG {

    private final List<Netg> nets_list = new ArrayList<>();

    public boolean AddNet(String address) {
        if (address == null) {
            return false;
        }
        if (address.length() == 0) {
            return false;
        }
        if (address.startsWith("#")) {
            return false;
        }
        if (address.startsWith("*")) {
            return false;
        }

        Netg net = new Netg(address.trim());
        nets_list.add(net);
        return true;
    }

    public int LoadFromLines(List<String> lines) {
        int added = 0;
        if (lines == null) {
            return added;
        }
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (AddNet(line)) {
                added++;
            }
        }
        return added;
    }

    public int LoadFromFile(String fn) {
        if (fn == null || fn.length() == 0) {
            return 0;
        }
        int added = 0;

        try {
            LineNumberReader lnr = new LineNumberReader(new BufferedReader(new FileReader(fn)));
            String line = null;
            while ((line = lnr.readLine()) != null) {
                if (AddNet(line)) {
                    added++;
                }
            }
        } catch (Exception e) {
            return 0;
        }
        return added;
    }

    public int LoadFromString(String _s, String _regex) {
        int added = 0;

        if (_s == null || _regex == null || _s.length() == 0 || _regex.length() == 0) {
            return 0;
        }

        for (String ip : _s.split(_regex)) {
            if (AddNet(ip)) {
                added++;
            }
        }

        return added;
    }

    public int ipInNet(String ip) {
        for (int i = 0; i < nets_list.size(); i++) {
            Netg _net = nets_list.get(i);
            if (_net.isInNet(ip)) {
                return i;
            }
        }
        return -1;
    }

    public boolean isIpInNets(String ip) {
        return ipInNet(ip) != -1;
    }

    public int NetsCount() {
        return nets_list.size();
    }

    public void ClearNets() {
        nets_list.clear();
    }

    private byte[] getIPFromHash(int ipHash) {
        byte[] result = new byte[4];
        result[0] = (byte) (ipHash >> 24 & 0xFF);
        result[1] = (byte) (ipHash >> 16 & 0xFF);
        result[2] = (byte) (ipHash >> 8 & 0xFF);
        result[3] = (byte) (ipHash & 0xFF);
        return result;
    }

    public String NetByIndex(int i) {
        if (nets_list.size() > i) {
            Netg _net = nets_list.get(i);
            try {
                InetAddress _ip = InetAddress.getByAddress(getIPFromHash(_net.getNetHash()));
                InetAddress _mask = InetAddress.getByAddress(getIPFromHash(_net.getMaskHash()));
                return _ip.getHostAddress() + "/" + _mask.getHostAddress();
            } catch (UnknownHostException e) {
                return "";
            }
        }
        return "";
    }

    public void PrintOut() {
        for (int i = 0; i < nets_list.size(); i++) {
            System.out.println("  Net #" + i + ": " + NetByIndex(i));
        }
    }
}

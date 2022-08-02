package l2p.gameserver.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import javolution.util.FastMap;

import l2p.commons.collections.GArray;
import l2p.commons.collections.GSArray;

import l2p.commons.dbutils.DbUtils;

import ftGuard.ftConfig;

import l2p.gameserver.database.DatabaseFactory;
import l2p.gameserver.model.GameObjectsStorage;
import l2p.gameserver.model.Player;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HWID {

    private static final Logger _log = LoggerFactory.getLogger(HWID.class);

    private static final GArray<HardwareID> banned_hwids = new GArray<HardwareID>();
    private static final GSArray<Entry<HardwareID, FastMap<String, Integer>>> bonus_hwids = new GSArray<Entry<HardwareID, FastMap<String, Integer>>>();

    public static final HWIDComparator DefaultComparator = new HWIDComparator();
    public static final HWIDComparator BAN_Comparator = new HWIDComparator();

    public static final String SELECT_HWID = "SELECT HWID FROM " + ftConfig.HWID_BANS_TABLE;
    public static final String REPLACE_HWID = "REPLACE INTO " + ftConfig.HWID_BANS_TABLE + " (hwid,comments) VALUES (?,?)";
    public static final String DELETE_HWID = "DELETE FROM " + ftConfig.HWID_BANS_TABLE + " WHERE hwid=?";

    public static void reloadBannedHWIDs() {
        synchronized (banned_hwids) {
            banned_hwids.clear();
        }

        Connection con = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            st = con.prepareStatement(SELECT_HWID);
            rs = st.executeQuery();
            synchronized (banned_hwids) {
                while (rs.next()) {
                    banned_hwids.add(new HardwareID(rs.getString("HWID")));
                }
            }

            _log.info("Protection: Loaded " + banned_hwids.size() + " banned HWIDs");
        } catch (final Exception e) {
            _log.warn("Protection: Failed to load banned HWIDs", e);
        } finally {
            DbUtils.closeQuietly(con, st, rs);
        }
    }

    public static boolean checkHWIDBanned(final HardwareID hwid) {
        synchronized (banned_hwids) {
            return hwid != null && BAN_Comparator.contains(hwid, banned_hwids);
        }
    }

    public static String handleBanHWID(final String[] argv) {
        if (!ftConfig.ALLOW_GUARD_SYSTEM || !ftConfig.PROTECT_GS_ENABLE_HWID_BANS) {
            return "HWID bans feature disabled";
        }

        if (argv == null || argv.length < 2) {
            return "USAGE: banhwid char_name|hwid [kick:true|false] [reason]";
        }

        String hwid = argv[1]; // либо HWID, либо имя чара
        if (hwid.length() != 32) {
            final Player player = GameObjectsStorage.getPlayer(hwid);
            if (player == null) {
                return "Player " + hwid + " not found in world";
            }
            if (!player.hasHWID()) {
                return "Player " + hwid + " not connected (offline trade)";
            }
            hwid = player.getHWID().Full;
        }

        if (argv.length == 2) {
            BanHWID(hwid, "", true);
        } else {
            boolean kick = true;
            String reason = "";
            if (argv[2].equalsIgnoreCase("true") || argv[2].equalsIgnoreCase("false")) {
                kick = Boolean.parseBoolean(argv[2]);
                if (argv.length > 3) {
                    for (int i = 3; i < argv.length; i++) {
                        reason += argv[i] + " ";
                    }
                    reason = reason.trim();
                }
            } // значит комменты
            else {
                for (int i = 2; i < argv.length; i++) {
                    reason += argv[i] + " ";
                }
                reason = reason.trim();
            }
            BanHWID(hwid, reason, kick);
        }
        return "HWID " + hwid + " banned";
    }

    public static boolean BanHWID(final String hwid, final String comment) {
        return BanHWID(hwid, comment, false);
    }

    public static boolean BanHWID(final String hwid, final String comment, final boolean kick) {
        if (!ftConfig.ALLOW_GUARD_SYSTEM || !ftConfig.PROTECT_GS_ENABLE_HWID_BANS || hwid == null || hwid.isEmpty()) {
            return false;
        }
        return BanHWID(new HardwareID(hwid), comment, kick);
    }

    public static boolean BanHWID(final HardwareID hwid, final String comment, final boolean kick) {
        if (!ftConfig.ALLOW_GUARD_SYSTEM || !ftConfig.PROTECT_GS_ENABLE_HWID_BANS || hwid == null) {
            return false;
        }

        if (checkHWIDBanned(hwid)) {
            _log.info("Protection: HWID: " + hwid + " already banned");
            return true;
        }

        Connection con = null;
        PreparedStatement st = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            st = con.prepareStatement(REPLACE_HWID);
            st.setString(1, hwid.Full);
            st.setString(2, comment);
            st.execute();

            synchronized (banned_hwids) {
                banned_hwids.add(hwid);
            }
            Log.add("Banned HWID: " + hwid, "protect");

            if (kick) {
                for (final Player cha : getPlayersByHWID(hwid)) {
                    cha.logout();
                }
            }
        } catch (final Exception e) {
            _log.warn("Protection: Failed to ban HWID: " + hwid, e);
        } finally {
            DbUtils.closeQuietly(con, st);
        }

        return checkHWIDBanned(hwid);
    }

    public static boolean UnbanHWID(final String hwid) {
        if (!ftConfig.ALLOW_GUARD_SYSTEM || !ftConfig.PROTECT_GS_ENABLE_HWID_BANS || hwid == null || hwid.isEmpty()) {
            return false;
        }
        return UnbanHWID(new HardwareID(hwid));
    }

    public static boolean UnbanHWID(final HardwareID hwid) {
        if (!ftConfig.ALLOW_GUARD_SYSTEM || !ftConfig.PROTECT_GS_ENABLE_HWID_BANS || hwid == null) {
            return false;
        }

        if (!checkHWIDBanned(hwid)) {
            _log.info("Protection: HWID: " + hwid + " already not banned");
            return true;
        }

        Connection con = null;
        PreparedStatement st = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            st = con.prepareStatement(DELETE_HWID);
            st.setString(1, hwid.Full);
            st.execute();

            synchronized (banned_hwids) {
                BAN_Comparator.remove(hwid, banned_hwids);
            }
            Log.add("Unbanned HWID: " + hwid, "protect");
        } catch (final Exception e) {
            _log.warn("Protection: Failed to unban HWID: " + hwid, e);
        } finally {
            DbUtils.closeQuietly(con, st);
        }
        return !checkHWIDBanned(hwid);
    }

    public static GArray<Player> getPlayersByHWID(final HardwareID hwid) {
        final GArray<Player> result = new GArray<Player>();
        if (hwid != null) {
            for (final Player cha : GameObjectsStorage.getAllPlayers()) {
                if (!cha.isInOfflineMode() && cha.getNetConnection() != null && cha.getNetConnection().protect_used && hwid.equals(cha.getHWID())) {
                    result.add(cha);
                }
            }
        }
        return result;
    }

    /**
     * Возвращает список всех HWID, кторые попадают под условия компаратора.
     */
    public static GArray<Entry<HardwareID, FastMap<String, Integer>>> getAllMatches(final HardwareID hwid, final HWIDComparator comparator) {
        final GArray<Entry<HardwareID, FastMap<String, Integer>>> ret = new GArray<Entry<HardwareID, FastMap<String, Integer>>>();
        synchronized (bonus_hwids) {
            for (final Entry<HardwareID, FastMap<String, Integer>> entry : bonus_hwids) {
                if (comparator.compare(entry.getKey(), hwid) == HWIDComparator.EQUALS) {
                    ret.add(entry);
                }
            }
        }
        return ret;
    }

    public static class HardwareID {

        public final String Full;

        public HardwareID(final String s) {
            Full = s;
        }

        @Override
        public int hashCode() {
            return Full.hashCode();
        }

        @Override
        public boolean equals(final Object obj) {
            if (obj == null || !(obj instanceof HardwareID)) {
                return false;
            }
            return DefaultComparator.compare(this, (HardwareID) obj) == HWIDComparator.EQUALS;
        }

        @Override
        public String toString() {
            return String.format("%s", Full);
        }
    }

    public static class HWIDComparator implements Comparator<HardwareID> {

        public static final int EQUALS = 0;
        public static final int NOT_EQUALS = 1;

        public HWIDComparator() {
        }

        @Override
        public int compare(final HardwareID o1, final HardwareID o2) {
            if (o1 == null && o2 == null) {
                return EQUALS;
            }
            if (o1 == null || o2 == null) {
                return NOT_EQUALS;
            }
            return o1.Full.equals(o2.Full) ? EQUALS : NOT_EQUALS;
        }

        public int find(final HardwareID hwid, final List<HardwareID> in) {
            for (int i = 0; i < in.size(); i++) {
                if (compare(hwid, in.get(i)) == EQUALS) {
                    return i;
                }
            }
            return -1;
        }

        public boolean contains(final HardwareID hwid, final List<HardwareID> in) {
            return find(hwid, in) != -1;
        }

        public boolean remove(final HardwareID hwid, final List<HardwareID> in) {
            final int i = find(hwid, in);
            return i == -1 ? false : in.remove(i) != null;
        }

        public int find(final HardwareID hwid, final GArray<HardwareID> in) {
            for (int i = 0; i < in.size(); i++) {
                if (compare(hwid, in.get(i)) == EQUALS) {
                    return i;
                }
            }
            return -1;
        }

        public boolean contains(final HardwareID hwid, final GArray<HardwareID> in) {
            return find(hwid, in) != -1;
        }

        public boolean remove(final HardwareID hwid, final GArray<HardwareID> in) {
            final int i = find(hwid, in);
            return i == -1 ? false : in.remove(i) != null;
        }

        @Override
        public String toString() {
            return "HWIDComparator";
        }
    }

    private static final class Entry<K, V> implements Map.Entry<K, V> {

        private final K _key;
        private V _value;

        public Entry(final K key, final V value) {
            _key = key;
            _value = value;
        }

        @Override
        public K getKey() {
            return _key;
        }

        @Override
        public V getValue() {
            return _value;
        }

        @Override
        public V setValue(final V value) {
            return _value = value;
        }
    }
}

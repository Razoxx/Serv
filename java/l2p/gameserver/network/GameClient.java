package l2p.gameserver.network;

import ftGuard.ftConfig;
import ftGuard.ftGuard;
import java.nio.ByteBuffer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import l2p.commons.dbutils.DbUtils;
import l2p.commons.net.nio.impl.MMOClient;
import l2p.commons.net.nio.impl.MMOConnection;
import l2p.gameserver.Config;
import l2p.gameserver.dao.AccountPointsDAO;
import l2p.gameserver.dao.CharacterDAO;
import l2p.gameserver.database.DatabaseFactory;
import l2p.gameserver.loginservercon.AuthServerCommunication;
import l2p.gameserver.loginservercon.SessionKey;
import l2p.gameserver.loginservercon.gspackets.PlayerLogout;
import l2p.gameserver.model.CharSelectInfoPackage;
import l2p.gameserver.model.GameObjectsStorage;
import l2p.gameserver.model.Player;
import l2p.gameserver.network.security.SecondaryPasswordAuth;
import l2p.gameserver.serverpackets.L2GameServerPacket;
import l2p.gameserver.serverpackets.components.SystemMsg;
import l2p.gameserver.utils.HWID;
import l2p.gameserver.utils.HWID.HardwareID;
import l2p.gameserver.utils.Language;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class GameClient extends MMOClient<MMOConnection<GameClient>> {

    private static final Logger _log = LoggerFactory.getLogger(GameClient.class);
    private static final String NO_IP = "?.?.?.?";
    private Language _language = Language.ENGLISH;
    private SecondaryPasswordAuth _secondaryAuth;
    public static boolean SESSION_OK = MMOClient.SESSION_OK;
    public HardwareID HWIDS = null, ALLOW_HWID = null;
    public boolean protect_used = false;
    public GameCrypt _crypt = null;
    public GameClientState _state;

    public static enum GameClientState {

        CONNECTED,
        AUTHED,
        IN_GAME,
        DISCONNECTED
    }
    /**
     * Данные аккаунта
     */
    private String _login;
    private double _bonus = 1.0;
    private int _bonus_type = 0;
    private int _bonusExpire;
    private Player _activeChar;
    private SessionKey _sessionKey;
    private String _ip = NO_IP;
    private int revision = 0;
    private boolean _gameGuardOk = false;
    //private SecondaryPasswordAuth _secondaryAuth;
    private List<Integer> _charSlotMapping = new ArrayList<Integer>();

    public GameClient(MMOConnection<GameClient> con) {
        super(con);
        if (ftGuard.isProtectionOn()) {
            protect_used = !ftConfig.PROTECT_UNPROTECTED_IPS.isIpInNets(getIpAddr());
        }
        _state = GameClientState.CONNECTED;
        _crypt = new GameCrypt();
        _ip = con.getSocket().getInetAddress().getHostAddress();
    }

    @Override
    protected void onDisconnection() {
        final Player player;

        setState(GameClientState.DISCONNECTED);
        player = getActiveChar();
        setActiveChar(null);

        if (player != null) {
            player.setNetConnection(null);
            player.scheduleDelete();
        }

        if (getSessionKey() != null) {
            if (isAuthed()) {
                AuthServerCommunication.getInstance().removeAuthedClient(getLogin());
                AuthServerCommunication.getInstance().sendPacket(new PlayerLogout(getLogin()));
            } else {
                AuthServerCommunication.getInstance().removeWaitingClient(getLogin());
            }
        }
    }

    @Override
    protected void onForcedDisconnection() {
        // TODO Auto-generated method stub
    }

    public void markRestoredChar(int charslot) throws Exception {
        int objid = getObjectIdForSlot(charslot);
        if (objid < 0) {
            return;
        }

        if (_activeChar != null && _activeChar.getObjectId() == objid) {
            _activeChar.setDeleteTimer(0);
        }

        Connection con = null;
        PreparedStatement statement = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement("UPDATE characters SET deletetime=0 WHERE obj_id=?");
            statement.setInt(1, objid);
            statement.execute();
        } catch (Exception e) {
            _log.error("", e);
        } finally {
            DbUtils.closeQuietly(con, statement);
        }
    }

    public void markToDeleteChar(int charslot) throws Exception {
        int objid = getObjectIdForSlot(charslot);
        if (objid < 0) {
            return;
        }

        if (_activeChar != null && _activeChar.getObjectId() == objid) {
            _activeChar.setDeleteTimer((int) (System.currentTimeMillis() / 1000));
        }

        Connection con = null;
        PreparedStatement statement = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement("UPDATE characters SET deletetime=? WHERE obj_id=?");
            statement.setLong(1, (int) (System.currentTimeMillis() / 1000L));
            statement.setInt(2, objid);
            statement.execute();
        } catch (Exception e) {
            _log.error("data error on update deletime char:", e);
        } finally {
            DbUtils.closeQuietly(con, statement);
        }
    }

    public void deleteChar(int charslot) throws Exception {
        //have to make sure active character must be nulled
        if (_activeChar != null) {
            return;
        }

        int objid = getObjectIdForSlot(charslot);
        if (objid == -1) {
            return;
        }

        CharacterDAO.getInstance().deleteCharByObjId(objid);
    }

    public Player loadCharFromDisk(int charslot) {
        int objectId = getObjectIdForSlot(charslot);
        if (objectId == -1) {
            return null;
        }

        Player character = null;
        Player oldPlayer = GameObjectsStorage.getPlayer(objectId);

        if (oldPlayer != null) {
            if (oldPlayer.isInOfflineMode() || oldPlayer.isLogoutStarted()) {
                // оффтрейдового чара проще выбить чем восстанавливать
                oldPlayer.kick();
                return null;
            } else {
                oldPlayer.sendPacket(SystemMsg.ANOTHER_PERSON_HAS_LOGGED_IN_WITH_THE_SAME_ACCOUNT);

                GameClient oldClient = oldPlayer.getNetConnection();
                if (oldClient != null) {
                    oldClient.setActiveChar(null);
                    oldClient.closeNow(false);
                }
                oldPlayer.setNetConnection(this);
                character = oldPlayer;
            }
        }

        if (character == null) {
            character = Player.restore(objectId);
        }

        if (character != null) {
            setActiveChar(character);
        } else {
            _log.warn("could not restore obj_id: " + objectId + " in slot:" + charslot);
        }
        loadBotPoints(character.getName()); // TODO: сделать по аккаунту!);
        if (checkHWIDBanned()) {
            character.logout();
        }
        if (protect_used && ftConfig.PROTECT_GS_STORE_HWID && hasHWID()) {
            character.storeHWID(HWIDS.Full);
        }
        return character;
    }

    public int getObjectIdForSlot(int charslot) {
        if (charslot < 0 || charslot >= _charSlotMapping.size()) {
            _log.warn(getLogin() + " tried to modify Character in slot " + charslot + " but no characters exits at that slot.");
            return -1;
        }
        return _charSlotMapping.get(charslot);
    }

    public Player getActiveChar() {
        return _activeChar;
    }

    /**
     * @return Returns the sessionId.
     */
    public SessionKey getSessionKey() {
        return _sessionKey;
    }

    public String getLogin() {
        return _login;
    }

    public void setLoginName(String loginName) {
        _login = loginName;
        if (protect_used && ftConfig.PROTECT_GS_LOG_HWID && !getIpAddr().equalsIgnoreCase("Disconnected")) {
            logHWID();
        }
        if (Config.SECOND_AUTH_ENABLED) {
            _secondaryAuth = new SecondaryPasswordAuth(this);
        }
    }

    public void setActiveChar(Player player) {
        _activeChar = player;
        if (player != null) {
            player.setNetConnection(this);
        }
    }

    public void setSessionId(SessionKey sessionKey) {
        _sessionKey = sessionKey;
    }

    public void setCharSelection(CharSelectInfoPackage[] chars) {
        _charSlotMapping.clear();

        for (CharSelectInfoPackage element : chars) {
            int objectId = element.getObjectId();
            _charSlotMapping.add(objectId);
        }
    }

    public void setCharSelection(int c) {
        _charSlotMapping.clear();
        _charSlotMapping.add(c);
    }

    public int getRevision() {
        return revision;
    }

    public void setRevision(int revision) {
        this.revision = revision;
    }

    @Override
    public boolean encrypt(final ByteBuffer buf, final int size) {
        _crypt.encrypt(buf.array(), buf.position(), size);
        buf.position(buf.position() + size);
        return true;
    }

    @Override
    public boolean decrypt(ByteBuffer buf, int size) {
        boolean ret = _crypt.decrypt(buf.array(), buf.position(), size);
        return ret;
    }

    public void sendPacket(L2GameServerPacket gsp) {
        if (isConnected()) {
            getConnection().sendPacket(gsp);
        }
    }

    public void sendPacket(L2GameServerPacket... gsp) {
        if (isConnected()) {
            getConnection().sendPacket(gsp);
        }
    }

    public void sendPackets(List<L2GameServerPacket> gsp) {
        if (isConnected()) {
            getConnection().sendPackets(gsp);
        }
    }

    public void close(L2GameServerPacket gsp) {
        if (isConnected()) {
            getConnection().close(gsp);
        }
    }

    public String getIpAddr() {
        return _ip;
    }

    public byte[] enableCrypt() {
        byte[] key = BlowFishKeygen.getRandomKey();
        _crypt.setKey(key);

        if (ftGuard.isProtectionOn()) {
            key = ftGuard.getKey(key);
        }
        return key;
    }

    public double getBonus() {
        return _bonus;
    }

    public double getBonusType() {
        return _bonus_type;
    }

    public int getBonusExpire() {
        return _bonusExpire;
    }

    public void setBonus(double bonus) {
        _bonus = bonus;
    }

    public void setBonustype(int type) {
        _bonus_type = type;
    }

    public void setBonusExpire(int bonusExpire) {
        _bonusExpire = bonusExpire;
    }

    public int getPointG() {
        return AccountPointsDAO.getInstance().getPoint(getLogin());
    }

    public void setPointG(int point) {
        AccountPointsDAO.getInstance().setPoint(getLogin(), point);
    }

    public GameClientState getState() {
        return _state;
    }

    public void setState(GameClientState state) {
        _state = state;
    }
    private int _failedPackets = 0;
    private int _unknownPackets = 0;

    public void onPacketReadFail() {
        if (_failedPackets++ >= 10) {
            _log.warn("Too many client packet fails, connection closed : " + this);
            closeNow(true);
        }
    }

    public void onUnknownPacket() {
        if (_unknownPackets++ >= 10) {
            _log.warn("Too many client unknown packets, connection closed : " + this);
            closeNow(true);
        }
    }

    @SuppressWarnings("deprecation")
    public void checkHwid(String allowedHwid) {
        if (!allowedHwid.equalsIgnoreCase("") && !getHWID().equalsIgnoreCase(allowedHwid)) {
            closeNow(false);
        }
    }

    public boolean checkHWIDBanned() {
        return HWID.checkHWIDBanned(new HardwareID(getHWID()));
    }

    public Language getLanguage() {
        return _language;
    }

    public void setLanguage(Language language) {
        _language = language;
    }

    @Override
    public String toString() {
        return _state + " IP: " + getIpAddr() + (_login == null ? "" : " Account: " + _login) + (_activeChar == null ? "" : " Player : " + _activeChar);
    }
    private String _hwid = "";
    private boolean _isProtected;

    public String getHWID() {
        return HWIDS != null ? HWIDS.Full : _hwid;
    }

    public boolean isProtected() {
        return _isProtected;
    }

    public void setHWID(final String hwid) {
        HWIDS = new HardwareID(hwid);
        _hwid = hwid;
    }

    public void setProtected(boolean isProtected) {
        _isProtected = isProtected;
    }

    public SecondaryPasswordAuth getSecondaryAuth() {
        return _secondaryAuth;
    }

    public void setGameGuardOk(boolean gameGuardOk) {
        _gameGuardOk = gameGuardOk;
    }

    public boolean isGameGuardOk() {
        return _gameGuardOk;
    }
    private static byte[] _keyClientEn = new byte[8];

    public static void setKeyClientEn(byte[] key) {
        _keyClientEn = key;
    }

    public static byte[] getKeyClientEn() {
        return _keyClientEn;
    }

    public boolean hasHWID() {
        return HWIDS != null;
    }

    public void setAllowHWID(final String hwid) {
        if (hwid != null && !hwid.isEmpty()) {
            ALLOW_HWID = new HardwareID(hwid);
        };
    }

    private void logHWID() {
        Connection con = null;
        PreparedStatement statement = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement(ftConfig.PROTECT_GS_LOG_HWID_QUERY);
            statement.setString(1, _login);
            statement.setString(2, getIpAddr());
            statement.setString(3, HWIDS.Full);
            statement.setInt(4, Config.REQUEST_ID);
            statement.execute();
        } catch (final Exception e) {
            _log.warn("Could not log HWID:", e);
        } finally {
            DbUtils.closeQuietly(con, statement);
        }
    }

    private int _reportBotPoints;

    private void loadBotPoints(String charName) {
        Connection con = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            PreparedStatement statement = con.prepareStatement("SELECT bot_report_points FROM characters WHERE char_name = ?");
            statement.setString(1, charName);

            ResultSet rset = statement.executeQuery();
            while (rset.next()) {
                _reportBotPoints = rset.getInt("bot_report_points");
            }
            rset.close();
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                con.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized void updatePoints(String charName) throws SQLException {
        Connection con = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            PreparedStatement statement = con.prepareStatement("UPDATE characters SET bot_report_points = ? WHERE char_name = ?");
            statement.setInt(1, _reportBotPoints);
            statement.setString(2, charName);
            statement.execute();
            statement.close();
        } catch (SQLException e) {
            _log.error("Couldnt save bot reports points for player " + charName);
            e.printStackTrace();
        } finally {
            DbUtils.close(con);
        }
    }

    public synchronized int getReportsPoints() {
        return _reportBotPoints;
    }

    public synchronized void reducePoints() {
        _reportBotPoints--;
    }
}

package bosses;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;
import java.util.concurrent.ScheduledFuture;
import l2p.commons.dbutils.DbUtils;
import l2p.commons.threading.RunnableImpl;
import l2p.gameserver.Announcements;
import l2p.gameserver.Config;
import l2p.gameserver.ThreadPoolManager;
import l2p.gameserver.data.xml.holder.NpcHolder;
import l2p.gameserver.database.DatabaseFactory;
import l2p.gameserver.templates.npc.NpcTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EpicBossState {

    private static final Logger _log = LoggerFactory.getLogger(EpicBossState.class);

    public static enum State {

        NOTSPAWN,
        ALIVE,
        DEAD,
        INTERVAL
    }

    public static enum NestState {

        ALLOW,
        LIMIT_EXCEEDED,
        ALREADY_ATTACKED,
        NOT_AVAILABLE
    }
    private int _bossId;
    private long _respawnDate;
    private State _state;
    private static ScheduledFuture<?> _intervalSpawnTask = null;

    public int getBossId() {
        return _bossId;
    }

    public void setBossId(int newId) {
        _bossId = newId;
    }

    public State getState() {
        return _state;
    }

    public void setState(State newState) {
        _state = newState;
    }

    public long getRespawnDate() {
        return _respawnDate;
    }

    public void setRespawnDate(long interval) {
        _respawnDate = interval + System.currentTimeMillis();
    }

    public EpicBossState(int bossId) {
        this(bossId, true);
    }

    public EpicBossState(int bossId, boolean isDoLoad) {
        _bossId = bossId;
        if (isDoLoad) {
            load();
        }
    }

    public void load() {
        Connection con = null;
        PreparedStatement statement = null;
        ResultSet rset = null;

        try {
            con = DatabaseFactory.getInstance().getConnection();

            statement = con.prepareStatement("SELECT * FROM epic_boss_spawn WHERE bossId = ? LIMIT 1");
            statement.setInt(1, _bossId);
            rset = statement.executeQuery();

            if (rset.next()) {
                _respawnDate = rset.getLong("respawnDate") * 1000L;

                if (_respawnDate - System.currentTimeMillis() <= 0) {
                    _state = State.NOTSPAWN;
                } else {
                    int tempState = rset.getInt("state");
                    if (tempState == State.NOTSPAWN.ordinal()) {
                        _state = State.NOTSPAWN;
                    } else if (tempState == State.INTERVAL.ordinal()) {
                        _state = State.INTERVAL;
                    } else if (tempState == State.ALIVE.ordinal()) {
                        _state = State.ALIVE;
                    } else if (tempState == State.DEAD.ordinal()) {
                        _state = State.DEAD;
                    } else {
                        _state = State.NOTSPAWN;
                    }
                }

                if (Config.EPIC_BOSS_SPAWN_ANNON > 0) {
                    final NpcTemplate npcTemp = NpcHolder.getInstance().getTemplate(_bossId);
                    ThreadPoolManager.getInstance().schedule(new RunnableImpl() {
                        @Override
                        public void runImpl() throws Exception {
                            Announcements.getInstance().announceToAll("Босс " + npcTemp.getName() + " появится в мире Адена через " + Config.EPIC_BOSS_SPAWN_ANNON + " час.");
                            _log.info("Спавно Босс " + npcTemp.getName());
                        }
                    }, _respawnDate - System.currentTimeMillis() - (Config.EPIC_BOSS_SPAWN_ANNON * 60 * 60 * 1000));

                    //_log.info(npcTemp.getName() + " = " + (_respawnDate - System.currentTimeMillis() - Config.EPIC_BOSS_SPAWN_ANNON * 60 * 60 * 1000));
                }
            }

        } catch (Exception e) {
            _log.error("", e);
        } finally {
            DbUtils.closeQuietly(con, statement, rset);
        }
    }

    public void save() {
        Connection con = null;
        PreparedStatement statement = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement("REPLACE INTO epic_boss_spawn (bossId,respawnDate,state) VALUES(?,?,?)");
            statement.setInt(1, _bossId);
            statement.setInt(2, (int) (_respawnDate / 1000));
            statement.setInt(3, _state.ordinal());
            statement.execute();
        } catch (Exception e) {
            _log.error("", e);
        } finally {
            DbUtils.closeQuietly(con, statement);
        }
    }

    public void update() {
        Connection con = null;
        Statement statement = null;

        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.createStatement();
            statement.executeUpdate("UPDATE epic_boss_spawn SET respawnDate=" + _respawnDate / 1000 + ", state=" + _state.ordinal() + " WHERE bossId=" + _bossId);
            final Date dt = new Date(_respawnDate);
            _log.info("update EpicBossState: ID:" + _bossId + ", RespawnDate:" + dt + ", State:" + _state.toString());
        } catch (Exception e) {
            _log.error("Exception on update EpicBossState: ID " + _bossId + ", RespawnDate:" + _respawnDate / 1000 + ", State:" + _state.toString(), e);
        } finally {
            DbUtils.closeQuietly(con, statement);
        }
    }

    public void setNextRespawnDate(long newRespawnDate) {
        _respawnDate = newRespawnDate;
    }

    public long getInterval() {
        long interval = _respawnDate - System.currentTimeMillis();
        return interval > 0 ? interval : 0;
    }
}

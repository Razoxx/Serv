package l2p.gameserver.dao;

import l2p.commons.dbutils.DbUtils;
import l2p.gameserver.Config;
import l2p.gameserver.database.DatabaseFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;

/**
 * @author AGNOLIC
 * @date 18:32/29.03.2012
 */
public class VoteManagerDAO {

    private static final Logger _log = LoggerFactory.getLogger(VoteManagerDAO.class);

    private static final VoteManagerDAO _instance = new VoteManagerDAO();

    public static final String SELECT_SQL_QUERY = "SELECT * FROM `character_votes` WHERE `name`=? AND `time`>=? AND `type`=?";
    public static final String INSERT_SQL_QUERY = "REPLACE INTO `character_votes`(name, time, type) VALUES(?, ?, ?)";
    public static final String CLEAN_SQL_QUERY = "DELETE FROM `character_votes` WHERE time<? AND type=?";

    public static VoteManagerDAO getInstance() {
        return _instance;
    }

    public boolean isTimeOut(String _name, long _time, String _type) {
        Connection con = null;
        PreparedStatement statement = null;
        ResultSet rset = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement(SELECT_SQL_QUERY);
            statement.setString(1, _name);
            statement.setLong(2, _time);
            statement.setString(3, _type);
            rset = statement.executeQuery();

            return !rset.next();
        } catch (Exception e) {
            _log.error("VoteManagerDAO.isTimeOut(String, long, String):" + e);
        } finally {
            DbUtils.closeQuietly(con, statement, rset);
        }

        return false;
    }

    public void onSave(String _name, long _time, String _type) {
        Connection con = null;
        PreparedStatement statement = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement(INSERT_SQL_QUERY);
            statement.setString(1, _name);
            statement.setLong(2, _time);
            statement.setString(3, _type);
            statement.execute();
        } catch (Exception e) {
            _log.warn("VoteManagerDAO#Payment(String,long,String): " + e);
        } finally {
            DbUtils.closeQuietly(con, statement);
        }
    }

    public void onClean(String type) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, -Config.L2TOP_SAVE_DAYS);
        Connection con = null;
        PreparedStatement statement = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement(CLEAN_SQL_QUERY);
            statement.setLong(1, cal.getTimeInMillis() / 1000L);
            statement.setString(2, type);
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DbUtils.closeQuietly(con, statement);
        }

    }
}

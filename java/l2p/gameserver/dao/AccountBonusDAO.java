package l2p.gameserver.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import l2p.commons.dbutils.DbUtils;
import l2p.gameserver.database.DatabaseFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AccountBonusDAO {

    private static final Logger _log = LoggerFactory.getLogger(AccountBonusDAO.class);
    private static final AccountBonusDAO _instance = new AccountBonusDAO();
    public static final String SELECT_SQL_QUERY = "SELECT bonus, bonus_expire FROM account_bonus WHERE account=?";
    public static final String DELETE_SQL_QUERY = "DELETE FROM account_bonus WHERE account=?";
    public static final String REPLACE_SQL_QUERY = "REPLACE INTO account_bonus(account, bonus, bonus_expire) VALUES (?,?,?)";
    public static final String INSERT_SQL_QUERY = "INSERT INTO account_bonus(account, bonus, bonus_expire) VALUES (?,?,?)";
    
    public static final String UPDATE_SQL_QUERY = "UPDATE account_bonus SET bonus_expire=? WHERE account=?";

    public static AccountBonusDAO getInstance() {
        return _instance;
    }

    public double[] select(String account) {
        double bonus = 0.;
        int time = 0;
        Connection con = null;
        PreparedStatement statement = null;
        ResultSet rset = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement(SELECT_SQL_QUERY);
            statement.setString(1, account);
            rset = statement.executeQuery();
            if (rset.next()) {
                bonus = rset.getInt("bonus");
                time = rset.getInt("bonus_expire");
            }
        } catch (Exception e) {
            _log.info("AccountBonusDAO.select(String): " + e, e);
        } finally {
            DbUtils.closeQuietly(con, statement, rset);
        }
        return new double[]{bonus, time};
    }

    public void delete1(String account) {
        Connection con = null;
        PreparedStatement statement = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement(DELETE_SQL_QUERY);
            statement.setString(1, account);
            statement.execute();
        } catch (Exception e) {
            _log.info("AccountBonusDAO.delete(String): " + e, e);
        } finally {
            DbUtils.closeQuietly(con, statement);
        }
    }
    public void insert(String account, int bonus, int endTime) {
        double _bonus = (double) bonus;
        insert1(account, _bonus, endTime);
    }

    public void insert1(String account, double bonus, int endTime) {
        Connection con = null;
        PreparedStatement statement = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement(REPLACE_SQL_QUERY);
            statement.setString(1, account);
            statement.setDouble(2, bonus);
            statement.setInt(3, endTime);
            statement.execute();
        } catch (Exception e) {
            _log.info("AccountBonusDAO.insert(String, double, int): " + e, e);
        } finally {
            DbUtils.closeQuietly(con, statement);
        }
    }
    
    public void endDate(String account, int endTime) {
        Connection con = null;
        PreparedStatement statement = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement(UPDATE_SQL_QUERY);
            statement.setInt(1, endTime);
            statement.setString(2, account);
            statement.execute();
        } catch (Exception e) {
            _log.info("AccountBonusDAO.insert(String, double, int): " + e, e);
        } finally {
            DbUtils.closeQuietly(con, statement);
        }
    }
    
    public void ins(String account, int bonus, int endTime) {
        double _bonus = (double) bonus;
        ins1(account, _bonus, endTime);
    }

    public boolean ins1(String account, double bonus, int endTime) {
        Connection con = null;
        PreparedStatement statement = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement(INSERT_SQL_QUERY);
            statement.setString(1, account);
            statement.setDouble(2, bonus);
            statement.setInt(3, endTime);
            statement.execute();
        } catch (SQLException e) {
            return false;
        } finally {
            DbUtils.closeQuietly(con, statement);
        }
        
        return true;
    }
}

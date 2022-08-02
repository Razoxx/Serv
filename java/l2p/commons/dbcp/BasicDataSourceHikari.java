package l2p.commons.dbcp;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;
import javax.sql.DataSource;

/**
 * Базовая реализация пула потоков с использованием DBCP
 *
 * @author G1ta0
 */
public class BasicDataSourceHikari implements DataSource {
    
    private static HikariDataSource connectionPool;

    /**
     * @param driver The fully qualified Java class name of the JDBC driver to
     * be used.
     * @param connectURI The connection URL to be passed to our JDBC driver to
     * establish a connection.
     * @param uname The connection username to be passed to our JDBC driver to
     * establish a connection.
     * @param passwd The connection password to be passed to our JDBC driver to
     * establish a connection.
     * @param maxActive The maximum number of active connections that can be
     * allocated from this pool at the same time, or negative for no limit.
     * @param idleTimeOut The minimum amount of time connection may stay in pool
     * (in seconds)
     * @param idleTestPeriod The period of time to check idle connections (in
     * seconds)
     * @param poolPreparedStatements
     * @throws SQLException
     */
    public BasicDataSourceHikari(String driver, String connectURI, int port, String databasename, String uname, String passwd, int maxActive, int maxIdle, int idleTimeOut, int idleTestPeriod, boolean poolPreparedStatements) {
        
        try {
            HikariConfig config = new HikariConfig();
            config.setMaximumPoolSize(maxActive);
            config.setDataSourceClassName(driver);
            config.addDataSourceProperty("serverName", connectURI);
            config.addDataSourceProperty("port", port);
            config.addDataSourceProperty("databaseName", databasename);
            config.addDataSourceProperty("user", uname);
            config.addDataSourceProperty("password", passwd);
            
            connectionPool = new HikariDataSource(config);
        } catch (final RuntimeException e) {
            System.out.append("Could not init database connection.");
        }
    }
    
    public Connection getConnection(Connection con) throws SQLException {
        try {
            return connectionPool.getConnection();
        } catch (final SQLException e) {
            System.out.append("Can't get connection from database");
        }
        return null;
    }
    
    public int getBusyConnectionCount() throws SQLException {
        return 0;
        //return _connectionPool.getNumActive();
    }
    
    public int getIdleConnectionCount() throws SQLException {
        //return _connectionPool.getNumIdle();
        return 0;
    }
    
    public void shutdown() throws Exception {
        connectionPool.close();
    }
    
    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return null;
        //return _source.getLogWriter();
    }
    
    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        //_source.setLogWriter(out);
    }
    
    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public int getLoginTimeout() throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }
    
    @Override
    public Connection getConnection() throws SQLException {
        try {
            return connectionPool.getConnection();
        } catch (final SQLException e) {
            System.out.append("Can't get connection from database");
        }
        return null;
    }
    
    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        throw new UnsupportedOperationException();
    }

    /*@Override
     public Logger getParentLogger() throws SQLFeatureNotSupportedException {
     // TODO Auto-generated method stub
     return null;
     }*/
}

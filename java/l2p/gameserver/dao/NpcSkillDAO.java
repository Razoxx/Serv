package l2p.gameserver.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import l2p.commons.dbutils.DbUtils;
import l2p.gameserver.database.DatabaseFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NpcSkillDAO {

    private static final Logger _log = LoggerFactory.getLogger(NpcSkillDAO.class);
    private static final NpcSkillDAO _instance = new NpcSkillDAO();
    public static final String SELECT_SQL_QUERY = "SELECT skillid, level FROM npcskills where npcid=?";

    public static NpcSkillDAO getInstance() {
        return _instance;
    }

    
    public Map<Integer, Integer> select(int npcId) {
        Map<Integer, Integer> map = new HashMap<Integer, Integer>();
        Connection con = null;
        PreparedStatement statement = null;
        ResultSet rset = null;

        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement(SELECT_SQL_QUERY);
            statement.setInt(1, npcId);
            rset = statement.executeQuery();
            while (rset.next()) {
                int id = rset.getInt("skillid");
                int level = rset.getInt("level");
                
                map.put(id, level);
            }
            
        } catch (Exception e) {
            _log.error("NpcSkillDAO.select(npcId):" + e, e);
        } finally {
            DbUtils.closeQuietly(con, statement, rset);
        }
        return map;
    }
}

package l2p.gameserver.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import l2p.commons.dbutils.DbUtils;
import l2p.gameserver.database.DatabaseFactory;
import l2p.gameserver.model.Player;
import l2p.gameserver.model.actor.instances.player.TpBookMark;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CharacterTPBookmarkDAO {

    private static final Logger _log = LoggerFactory.getLogger(CharacterTPBookmarkDAO.class);
    private static final CharacterTPBookmarkDAO _instance = new CharacterTPBookmarkDAO();
    private static final String SELECT_SQL_QUERY = "SELECT * FROM character_tp_bookmarks WHERE object_id=?";
    private static final String INSERT_SQL_QUERY = "INSERT INTO character_tp_bookmarks(object_id, name, acronym, icon, x, y, z) VALUES (?,?,?,?,?,?,?)";
    private static final String UPDATE_SQL_QUERY = "UPDATE character_tp_bookmarks SET name=?, acronym=?, icon=? WHERE object_id=? AND name=? AND x=? AND y=? AND z=?";
    private static final String DELETE_SQL_QUERY = "DELETE FROM character_tp_bookmarks WHERE object_id=? AND name=? AND x=? AND y=? AND z=? LIMIT 1";

    public static CharacterTPBookmarkDAO getInstance() {
        return _instance;
    }

    public List<TpBookMark> select(Player player) {
        List<TpBookMark> list = new ArrayList(9);

        Connection con = null;
        PreparedStatement statement = null;
        ResultSet rset = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement("SELECT * FROM character_tp_bookmarks WHERE object_id=?");
            statement.setInt(1, player.getObjectId());
            rset = statement.executeQuery();
            while (rset.next()) {
                int x = rset.getInt("x");
                int y = rset.getInt("y");
                int z = rset.getInt("z");
                int icon = rset.getInt("icon");
                String name = rset.getString("name");
                String acronym = rset.getString("acronym");

                list.add(new TpBookMark(x, y, z, icon, name, acronym));
            }
        } catch (Exception e) {
            _log.error("CharacterTPBookmarkDAO.select(Player): " + e, e);
        } finally {
            DbUtils.closeQuietly(con, statement, rset);
        }
        return list;
    }

    public void insert(Player player, TpBookMark bookMark) {
        Connection con = null;
        PreparedStatement statement = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement("INSERT INTO character_tp_bookmarks(object_id, name, acronym, icon, x, y, z) VALUES (?,?,?,?,?,?,?)");
            statement.setInt(1, player.getObjectId());
            statement.setString(2, bookMark.getName());
            statement.setString(3, bookMark.getAcronym());
            statement.setInt(4, bookMark.getIcon());
            statement.setInt(5, bookMark.getX());
            statement.setInt(6, bookMark.getY());
            statement.setInt(7, bookMark.getZ());
            statement.execute();
        } catch (Exception e) {
            _log.error("CharacterTPBookmarkDAO.insert(Player, TpBookMark): " + e, e);
        } finally {
            DbUtils.closeQuietly(con, statement);
        }
    }

    public void delete(Player player, TpBookMark bookMark) {
        Connection con = null;
        PreparedStatement statement = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement("DELETE FROM character_tp_bookmarks WHERE object_id=? AND name=? AND x=? AND y=? AND z=? LIMIT 1");
            statement.setInt(1, player.getObjectId());
            statement.setString(2, bookMark.getName());
            statement.setInt(3, bookMark.getX());
            statement.setInt(4, bookMark.getY());
            statement.setInt(5, bookMark.getZ());
            statement.execute();
        } catch (Exception e) {
            _log.error("CharacterTPBookmarkDAO.delete(Player,TpBookMark): " + e, e);
        } finally {
            DbUtils.closeQuietly(con, statement);
        }
    }

    public void update(Player player, TpBookMark bookMark) {
        Connection con = null;
        PreparedStatement statement = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement("UPDATE character_tp_bookmarks SET name=?, acronym=?, icon=? WHERE object_id=? AND name=? AND x=? AND y=? AND z=?");
            statement.setString(1, bookMark.getName());
            statement.setString(2, bookMark.getAcronym());
            statement.setInt(3, bookMark.getIcon());
            statement.setInt(4, player.getObjectId());
            statement.setString(5, bookMark.getName());
            statement.setInt(6, bookMark.getX());
            statement.setInt(7, bookMark.getY());
            statement.setInt(8, bookMark.getZ());
            statement.execute();
        } catch (Exception e) {
            _log.error("CharacterTPBookmarkDAO.update(Player,TpBookMark): " + e, e);
        } finally {
            DbUtils.closeQuietly(con, statement);
        }
    }
}

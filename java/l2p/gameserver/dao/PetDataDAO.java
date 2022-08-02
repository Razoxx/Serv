package l2p.gameserver.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import l2p.commons.dbutils.DbUtils;
import l2p.gameserver.database.DatabaseFactory;
import l2p.gameserver.model.Creature;
import l2p.gameserver.model.Player;
import l2p.gameserver.model.Summon;
import l2p.gameserver.model.items.ItemInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class PetDataDAO {

    private static final Logger _log = LoggerFactory.getLogger(PetDataDAO.class);
    private static final PetDataDAO _instance = new PetDataDAO();
    public static final String SELECT_SQL_QUERY = "SELECT objId FROM pets WHERE item_obj_id=?";
    public static final String DELETE_SQL_QUERY = "DELETE FROM pets WHERE item_obj_id=?";

    public static PetDataDAO getInstance() {
        return _instance;
    }

    public void deletePet(ItemInstance item, Creature owner) {
        int petObjectId = 0;

        Connection con = null;
        PreparedStatement statement = null;
        ResultSet rset = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement(SELECT_SQL_QUERY);
            statement.setInt(1, item.getObjectId());
            rset = statement.executeQuery();
            while (rset.next()) {
                petObjectId = rset.getInt("objId");
            }

            DbUtils.close(statement, rset);

            Summon summon = owner.getPet();
            if (summon != null && summon.getObjectId() == petObjectId) {
                summon.unSummon();
            }

            Player player = owner.getPlayer();
            if (player != null && player.isMounted() && player.getMountObjId() == petObjectId) {
                player.setMount(0, 0, 0);
            }

            // if it's a pet control item, delete the pet
            statement = con.prepareStatement(DELETE_SQL_QUERY);
            statement.setInt(1, item.getObjectId());
            statement.execute();
        } catch (Exception e) {
            _log.error("could not restore pet objectid:", e);
        } finally {
            DbUtils.closeQuietly(con, statement, rset);
        }
    }

    public void unSummonPet(ItemInstance oldItem, Creature owner) {
        int petObjectId = 0;

        Connection con = null;
        PreparedStatement statement = null;
        ResultSet rset = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement(SELECT_SQL_QUERY);
            statement.setInt(1, oldItem.getObjectId());
            rset = statement.executeQuery();

            while (rset.next()) {
                petObjectId = rset.getInt("objId");
            }

            if (owner == null) {
                return;
            }

            Summon summon = owner.getPet();
            if (summon != null && summon.getObjectId() == petObjectId) {
                summon.unSummon();
            }

            Player player = owner.getPlayer();
            if (player != null && player.isMounted() && player.getMountObjId() == petObjectId) {
                player.setMount(0, 0, 0);
            }
        } catch (Exception e) {
            _log.error("could not restore pet objectid:", e);
        } finally {
            DbUtils.closeQuietly(con, statement, rset);
        }
    }
}

package l2p.gameserver.instancemanager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import l2p.commons.dbutils.DbUtils;
import l2p.gameserver.database.DatabaseFactory;
import l2p.gameserver.model.Player;
import l2p.gameserver.model.items.ItemInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author baltasar
 */
public class ExchangeBroker {

    private static final Logger _log = LoggerFactory.getLogger(ExchangeBroker.class);
    private static Map<Integer, ExchangeItem> _list;
    private static List<ExchangeItem> _listForPlayer;
    private static final String RESTORE_ITEM_EXCHANGE = "SELECT object_id, owner_id, item_id, count, enchant_level, augmentation_id, attribute_fire, attribute_water, attribute_wind, attribute_earth, attribute_holy, attribute_unholy, price FROM exchange";
    private static final String STORE_ITEM_EXCHANGE = "INSERT INTO exchange( object_id, owner_id, item_id, count, enchant_level, augmentation_id, attribute_fire, attribute_water, attribute_wind, attribute_earth, attribute_holy, attribute_unholy, price) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";
    private static final String DELETE_ALL_ITEM_EXCHANGE = "TRUNCATE TABLE exchange";
    private static ExchangeBroker _instance;

    public static ExchangeBroker getInstance() {
        if (_instance == null) {
            _instance = new ExchangeBroker();
        }

        return _instance;
    }

    public ExchangeBroker() {
        _list = new ConcurrentHashMap<>();
        LoadExchangeItem();
    }

    public class ExchangeItem {

        private final int _objectId;
        private final int _ownerId;
        private final int _itemId;
        private final int _count;
        private final int _enchant_level;
        private final int _augmentationId;
        private final int _att_fire;
        private final int _att_water;
        private final int _att_wind;
        private final int _att_earth;
        private final int _att_holy;
        private final int _att_unholy;
        private final int _price;

        private ExchangeItem(int objectId, int ownerId, int itemId, int count, int enchant_level, int augmentationId, int att_fire, int att_water, int att_wind, int att_earth, int att_holy, int att_unholy, int price) {
            _objectId = objectId;
            _ownerId = ownerId;
            _itemId = itemId;
            _count = count;
            _enchant_level = enchant_level;
            _augmentationId = augmentationId;
            _att_fire = att_fire;
            _att_water = att_water;
            _att_wind = att_wind;
            _att_earth = att_earth;
            _att_holy = att_holy;
            _att_unholy = att_unholy;
            _price = price;
        }

        public int getObjectId() {
            return _objectId;
        }

        public int getOwnerId() {
            return _ownerId;
        }

        public int getItemId() {
            return _itemId;
        }

        public int getCount() {
            return _count;
        }

        public int getEnchantLevel() {
            return _enchant_level;
        }

        public int getAugmentationId() {
            return _augmentationId;
        }

        public int getAttFire() {
            return _att_fire;
        }

        public int getAttWater() {
            return _att_water;
        }

        public int getAttWind() {
            return _att_wind;
        }

        public int getAttEath() {
            return _att_earth;
        }

        public int getAttHoly() {
            return _att_holy;
        }

        public int getAttUnholy() {
            return _att_unholy;
        }

        public int getPrice() {
            return _price;
        }
    }

    private void LoadExchangeItem() {
        Connection con = null;
        PreparedStatement selectStatement = null;
        ResultSet rset = null;
        ExchangeItem item = null;
        int k = 0;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            selectStatement = con.prepareStatement(RESTORE_ITEM_EXCHANGE);
            rset = selectStatement.executeQuery();

            while (rset.next()) {
                k++;
                int objectId = rset.getInt(1);
                int ownerId = rset.getInt(2);
                int itemId = rset.getInt(3);
                int count = rset.getInt(4);
                int enchant_level = rset.getInt(5);
                int augmentationId = rset.getInt(6);
                int att_fire = rset.getInt(7);
                int att_water = rset.getInt(8);
                int att_wind = rset.getInt(9);
                int att_earth = rset.getInt(10);
                int att_holy = rset.getInt(11);
                int att_unholy = rset.getInt(12);
                int price = rset.getInt(13);
                item = new ExchangeItem(objectId, ownerId, itemId, count, enchant_level, augmentationId, att_fire, att_water, att_wind, att_earth, att_holy, att_unholy, price);

                _list.put(objectId, item);
            }
        } catch (Exception e) {
        } finally {
            DbUtils.closeQuietly(con, selectStatement, rset);
        }
        _log.info("ExchangeBroker: Loaded " + k + " items for sale");
    }

    private void save(ExchangeItem item) throws SQLException {
        Connection con = null;
        PreparedStatement statement = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement(STORE_ITEM_EXCHANGE);
            save0(item, statement);
            statement.execute();
        } finally {
            DbUtils.closeQuietly(con, statement);
        }
    }

    private void save0(ExchangeItem item, PreparedStatement statement) throws SQLException {
        statement.setInt(1, item.getObjectId());
        statement.setInt(2, item.getOwnerId());
        statement.setInt(3, item.getItemId());
        statement.setLong(4, item.getCount());
        statement.setInt(5, item.getEnchantLevel());
        statement.setInt(6, item.getAugmentationId());
        statement.setInt(7, item.getAttFire());
        statement.setInt(8, item.getAttWater());
        statement.setInt(9, item.getAttWind());
        statement.setInt(10, item.getAttEath());
        statement.setInt(11, item.getAttHoly());
        statement.setInt(12, item.getAttUnholy());
        statement.setInt(13, item.getPrice());
    }

    public void saveItems() throws SQLException {
        if (_list.isEmpty()) {
            return;
        }

        Connection con = null;
        PreparedStatement statement = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement(DELETE_ALL_ITEM_EXCHANGE);
            statement.execute();
        } finally {
            DbUtils.closeQuietly(con, statement);
        }

        for (ExchangeItem item : _list.values()) {
            try {
                save(item);
            } catch (SQLException ex) {
            }
        }
    }

    public void addItemsSale(ItemInstance item, Player player, int count, int price) {
        if (player != null) {
            ExchangeItem itemExchange = null;

            int objectId = item.getObjectId();
            int ownerId = player.getObjectId();
            int itemId = item.getItemId();
            int enchant_level = item.getEnchantLevel();
            int augmentationId = item.getAugmentationId();
            int att_fire = item.getAttributes().getFire();
            int att_water = item.getAttributes().getWater();
            int att_wind = item.getAttributes().getWind();
            int att_earth = item.getAttributes().getEarth();
            int att_holy = item.getAttributes().getHoly();
            int att_unholy = item.getAttributes().getUnholy();

            itemExchange = new ExchangeItem(objectId, ownerId, itemId, count, enchant_level, augmentationId, att_fire, att_water, att_wind, att_earth, att_holy, att_unholy, price);

            _list.put(objectId, itemExchange);
        }
    }

    public static Map<Integer, ExchangeItem> getExchangeItemList() {
        return _list;
    }

    public static void deleteItem(int objectId) {
        _list.remove(objectId);
    }

    public static List<ExchangeItem> getItemsForPlayer(int ownerId) {
        _listForPlayer = new ArrayList<>();
        for (ExchangeItem tempItems : _list.values()) {
            if (tempItems.getOwnerId() == ownerId) {
                _listForPlayer.add(tempItems);
            }
        }

        return _listForPlayer;
    }
    
    public void addBalansOff(int objectId, int balans) {
        int bal = 0;
        Connection con = null;
        PreparedStatement statement = null;
        ResultSet rset = null;

        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement("SELECT balans FROM characters WHERE obj_Id=?");
            statement.setInt(1, objectId);
            rset = statement.executeQuery();
            if (rset.next()) {
                bal = rset.getInt(1);
            }
        } catch (Exception e) {
        } finally {
            DbUtils.closeQuietly(con, statement, rset);
        }

        bal = bal + balans;

        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement("UPDATE characters SET balans=? WHERE obj_Id=?");
            statement.setInt(1, bal);
            statement.setInt(2, objectId);
            statement.execute();
        } catch (Exception e) {
        } finally {
            DbUtils.closeQuietly(con, statement, rset);
        }
    }

    public static ExchangeItem getItemsForObjectId(int objectId) {
        return _list.get(objectId);
    }
}

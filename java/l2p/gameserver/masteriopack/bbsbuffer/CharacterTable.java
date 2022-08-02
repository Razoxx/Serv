package l2p.gameserver.masteriopack.bbsbuffer;

import l2p.gameserver.masteriopack.bbsbuffer.DBStatus;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import javolution.util.FastList;
import javolution.util.FastMap;
import l2p.gameserver.Config;
import l2p.gameserver.ThreadPoolManager;
import l2p.gameserver.database.DatabaseFactory;
import l2p.gameserver.model.GameObjectsStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Masterio
 *
 */
public class CharacterTable {

    private static final Logger _log = LoggerFactory.getLogger(CharacterTable.class);
    
    private static CharacterTable _instance = null;

    /**
     * [charObjId, Character]
     */
    private FastMap<Integer, Character> _characterList = new FastMap<>();

    private CharacterTable() {
        load();

        ThreadPoolManager.getInstance().schedule(new CharacterTableSchedule(), Config.CHARACTER_TABLE_UPDATE_INTERVAL);
    }

    public static CharacterTable getInstance() {

        if (_instance == null) {
            _instance = new CharacterTable();
        }

        return _instance;
    }

    /**
     * Loaded on server start.
     */
    public void load() {

        Connection con = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();

            // load scheme data:
            PreparedStatement statement = con.prepareStatement("SELECT char_obj_id, scheme_id, scheme_name FROM bbsbuffer_schemes");
            ResultSet rset = statement.executeQuery();

            while (rset.next()) {

                Character c = getCharacterById(rset.getInt("char_obj_id"));

                c.addSchemeForDBLoader(rset.getInt("scheme_id"), rset.getString("scheme_name"));

            }

            statement.close();
            rset.close();

            // load scheme buffs:
            statement = con.prepareStatement("SELECT char_obj_id, scheme_id, skill_id FROM bbsbuffer_scheme_skills");
            rset = statement.executeQuery();

            while (rset.next()) {
                Character c = getCharacterById(rset.getInt("char_obj_id"));

                Scheme scheme = c.getSchemeById(rset.getInt("scheme_id"));

                if (scheme != null) {
                    scheme.addBuff(rset.getInt("skill_id"));
                }

            }

            statement.close();
            rset.close();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (con != null) {
                    con.close();
                    _log.info("CharacterTable: loaded " + _characterList.size() + " objects");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }

    public void executeBatch(ArrayList<String> queries) {

        Connection conn = null;

        try {
            conn = DatabaseFactory.getInstance().getConnection();
            Statement stat = conn.createStatement();

            for (String query : queries) {
                stat.addBatch(query);
            }

            stat.executeBatch();
            stat.close();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public FastMap<Integer, Character> getCharacterList() {
        return _characterList;
    }

    public void setCharacterList(FastMap<Integer, Character> _characterList) {
        this._characterList = _characterList;
    }

    public Character getCharacterById(int charObjId) {
        if (!getCharacterList().containsKey(charObjId)) {
            // create new Character instance if no founded:
            getCharacterList().put(charObjId, new Character(charObjId));
        }

        return getCharacterList().get(charObjId);
    }

    /**
     * Save all data from object model in database, and edit DBStatus for each
     * Character Scheme if necessary.
     */
    public void updateDatabase() {
        ArrayList<String> insert = new ArrayList<>();
        ArrayList<String> update = new ArrayList<>();
        ArrayList<String> delete = new ArrayList<>();

        for (FastMap.Entry<Integer, Character> e = getCharacterList().head(), end = getCharacterList().tail(); (e = e.getNext()) != end;) {
            FastMap<Integer, Scheme> sl = e.getValue().getSchemeListEntry();

            ArrayList<Integer> deleted_scheme_ids = new ArrayList<>();

            for (FastMap.Entry<Integer, Scheme> e2 = sl.head(), end2 = sl.tail(); (e2 = e2.getNext()) != end2;) {
                Scheme s = e2.getValue();

                if (s.getDbStatus() == DBStatus.INSERTED) {
                    insert.add("INSERT INTO bbsbuffer_schemes (char_obj_id, scheme_id, scheme_name) VALUES (" + e.getValue().getCharObjId() + "," + s.getSchemeId() + ",'" + s.getSchemeName() + "')");

                    for (FastList.Node<Integer> n = s.getBuffList().head(), end3 = s.getBuffList().tail(); (n = n.getNext()) != end3;) {
                        insert.add("INSERT INTO bbsbuffer_scheme_skills (char_obj_id, scheme_id, skill_id) VALUES (" + e.getValue().getCharObjId() + "," + s.getSchemeId() + "," + n.getValue() + ")");
                    }

                    s.setDbStatus(DBStatus.NONE);

                } else if (s.getDbStatus() == DBStatus.UPDATED) {
                    update.add("DELETE FROM bbsbuffer_scheme_skills WHERE char_obj_id = " + e.getValue().getCharObjId() + " AND scheme_id = " + s.getSchemeId());

                    for (FastList.Node<Integer> n = s.getBuffList().head(), end3 = s.getBuffList().tail(); (n = n.getNext()) != end3;) {
                        update.add("INSERT INTO bbsbuffer_scheme_skills (char_obj_id, scheme_id, skill_id) VALUES (" + e.getValue().getCharObjId() + "," + s.getSchemeId() + "," + n.getValue() + ")");
                    }

                    s.setDbStatus(DBStatus.NONE);
                } else if (s.getDbStatus() == DBStatus.DELETED) {
                    delete.add("DELETE FROM bbsbuffer_scheme_skills WHERE char_obj_id = " + e.getValue().getCharObjId() + " AND scheme_id = " + s.getSchemeId());

                    delete.add("DELETE FROM bbsbuffer_schemes WHERE char_obj_id = " + e.getValue().getCharObjId() + " AND scheme_id = " + s.getSchemeId());

                    deleted_scheme_ids.add(s.getSchemeId());
                }
            }

            for (Integer id : deleted_scheme_ids) {
                // remove scheme from object model.
                e.getValue().getSchemeListEntry().remove(id);
            }

        }

        executeBatch(delete);
        executeBatch(insert);
        executeBatch(update);

    }

    /**
     * Clear table in object model from off-line characters.
     */
    public void cleanCharacterTable() {
        FastMap<Integer, Character> map = getCharacterList();

        for (FastMap.Entry<Integer, Character> e = map.head(), end = map.tail(); (e = e.getNext()) != end;) {

            if (e.getValue() != null && (GameObjectsStorage.getAllPlayers() == null || GameObjectsStorage.getPlayer(e.getKey()) == null || !GameObjectsStorage.getPlayer(e.getKey()).isOnline())) {
                if (e.getValue().isToRemove()) {
                    map.remove(e.getKey());
                } else {
                    e.getValue().setToRemove(true);
                }
            } else // if in game
            {
                if (e.getValue().isToRemove()) {
                    e.getValue().setToRemove(false);
                }
            }

        }

    }

    private static class CharacterTableSchedule implements Runnable {

        public CharacterTableSchedule() {

        }

        @Override
        public void run() {
            CharacterTable.getInstance().updateDatabase();

			//CharacterTable.getInstance().cleanCharacterTable(); //TODO check it :)
            ThreadPoolManager.getInstance().schedule(new CharacterTableSchedule(), Config.CHARACTER_TABLE_UPDATE_INTERVAL);
        }
    }

}

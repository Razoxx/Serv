package l2p.gameserver.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import l2p.commons.dbutils.DbUtils;
import l2p.gameserver.Config;
import l2p.gameserver.database.DatabaseFactory;
import l2p.gameserver.instancemanager.QuestManager;
import l2p.gameserver.model.Player;
import l2p.gameserver.model.pledge.Clan;
import l2p.gameserver.model.quest.Quest;
import l2p.gameserver.model.quest.QuestState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CharacterQuestDAO {

    private static final Logger _log = LoggerFactory.getLogger(CharacterQuestDAO.class);
    private static final CharacterQuestDAO _instance = new CharacterQuestDAO();

    public static CharacterQuestDAO getInstance() {
        return _instance;
    }

    public void select(Player player) {
        if (Config.DONTLOADQUEST) {
            return;
        }
        Connection con = null;
        PreparedStatement statement = null;
        PreparedStatement invalidQuestData = null;
        ResultSet rset = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            invalidQuestData = con.prepareStatement("DELETE FROM character_quests WHERE char_id=? and name=?");
            statement = con.prepareStatement("SELECT name,value FROM character_quests WHERE char_id=? AND var=?");
            statement.setInt(1, player.getObjectId());
            statement.setString(2, "<state>");
            rset = statement.executeQuery();
            while (rset.next()) {
                String questId = rset.getString("name");
                String state = rset.getString("value");
                if (state.equalsIgnoreCase("Start")) {
                    invalidQuestData.setInt(1, player.getObjectId());
                    invalidQuestData.setString(2, questId);
                    invalidQuestData.executeUpdate();
                } else {
                    Quest q = QuestManager.getQuest(questId);
                    if (q == null) {
                        _log.warn("Unknown quest " + questId + " for player " + player.getName());
                    } else {
                        new QuestState(q, player, Quest.getStateId(state));
                    }
                }
            }
            DbUtils.close(statement, rset);

            statement = con.prepareStatement("SELECT name,var,value FROM character_quests WHERE char_id=?");
            statement.setInt(1, player.getObjectId());
            rset = statement.executeQuery();
            while (rset.next()) {
                String questId = rset.getString("name");
                String var = rset.getString("var");
                String value = rset.getString("value");

                QuestState qs = player.getQuestState(questId);
                if (qs != null) {
                    if ((var.equals("cond")) && (Integer.parseInt(value) < 0)) {
                        value = String.valueOf(Integer.parseInt(value) | 0x1);
                    }
                    qs.set(var, value, false);
                }
            }
        } catch (Exception e) {
            _log.warn("CharacterQuestDAO.select(Player): " + e, e);
        } finally {
            DbUtils.closeQuietly(invalidQuestData);
            DbUtils.closeQuietly(con, statement, rset);
        }
    }

    public void replace(QuestState qs) {
        replace(qs, "<state>", qs.getStateName());
    }

    public void replace(QuestState qs, String var, String value) {
        Player player = qs.getPlayer();
        if (player == null) {
            return;
        }
        Connection con = null;
        PreparedStatement statement = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement("REPLACE INTO character_quests (char_id,name,var,value) VALUES (?,?,?,?)");
            statement.setInt(1, qs.getPlayer().getObjectId());
            statement.setString(2, qs.getQuest().getName());
            statement.setString(3, var);
            statement.setString(4, value);
            statement.executeUpdate();
        } catch (Exception e) {
            _log.warn("CharacterQuestDAO.replace(QuestState, String, String): " + e, e);
        } finally {
            DbUtils.closeQuietly(con, statement);
        }
    }

    public void delete(int objectId, String name) {
        Connection con = null;
        PreparedStatement statement = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement("DELETE FROM character_quests WHERE char_id=? AND name=?");
            statement.setInt(1, objectId);
            statement.setString(2, name);
            statement.executeUpdate();
        } catch (Exception e) {
            _log.warn("CharacterQuestDAO.delete(int, int): " + e, e);
        } finally {
            DbUtils.closeQuietly(con, statement);
        }
    }

    public void delete(int objectId, String name, String var) {
        Connection con = null;
        PreparedStatement statement = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement("DELETE FROM character_quests WHERE char_id=? AND name=? AND var=?");
            statement.setInt(1, objectId);
            statement.setString(2, name);
            statement.setString(3, var);
            statement.executeUpdate();
        } catch (Exception e) {
            _log.warn("CharacterQuestDAO.delete(int, int, String): " + e, e);
        } finally {
            DbUtils.closeQuietly(con, statement);
        }
    }

    public void Q501_removeQuestFromOfflineMembers(QuestState st) {
        if ((st.getPlayer() == null) || (st.getPlayer().getClan() == null)) {
            st.exitCurrentQuest(true);
            return;
        }
        int clan = st.getPlayer().getClan().getClanId();

        Connection con = null;
        PreparedStatement offline = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            offline = con.prepareStatement("DELETE FROM character_quests WHERE name = ? AND char_id IN (SELECT obj_id FROM characters WHERE clanId = ? AND online = 0)");
            offline.setString(1, st.getQuest().getName());
            offline.setInt(2, clan);
            offline.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DbUtils.closeQuietly(con, offline);
        }
    }

    public int Q503_getLeaderVar(QuestState st, String var, Player leader) {
        boolean cond = "cond".equalsIgnoreCase(var);
        try {
            if (leader != null) {
                QuestState qs2 = leader.getQuestState(st.getQuest().getName());
                if (qs2 == null) {
                    return -1;
                }
                return cond ? qs2.getCond() : qs2.getInt(var);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
        Clan clan = st.getPlayer().getClan();
        if (clan == null) {
            return -1;
        }
        int leaderId = clan.getLeaderId();

        Connection con = null;
        PreparedStatement offline = null;
        ResultSet rs = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            offline = con.prepareStatement("SELECT value FROM character_quests WHERE char_id=? AND var=? AND name=?");
            offline.setInt(1, leaderId);
            offline.setString(2, var);
            offline.setString(3, st.getQuest().getName());
            int val = -1;
            int i = 0;
            rs = offline.executeQuery();
            if (rs.next()) {
                val = rs.getInt("value");
                if ((cond) && ((val & 0x80000000) != 0)) {
                    val &= 0x7FFFFFFF;
                    for (i = 1; i < 32; i++) {
                        val >>= 1;
                        if (val == 0) {
                            return i;
                        }
                    }
                }
            }
            return val;
        } catch (Exception e) {
            int i;
            e.printStackTrace();
            return -1;
        } finally {
            DbUtils.closeQuietly(con, offline, rs);
        }
    }

    public void Q503_offlineMemberExit(QuestState st) {
        int clan = st.getPlayer().getClan().getClanId();

        Connection con = null;
        PreparedStatement offline = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            offline = con.prepareStatement("DELETE FROM character_quests WHERE quest_id=? AND char_id IN (SELECT obj_id FROM characters WHERE name=? AND online=0)");
            offline.setString(1, st.getQuest().getName());
            offline.setInt(2, clan);
            offline.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DbUtils.closeQuietly(con, offline);
        }
    }

    public void Q503_setLeaderVar(String name, int leaderId, String var, String value) {
        Connection con = null;
        PreparedStatement offline = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            offline = con.prepareStatement("UPDATE character_quests SET value=? WHERE char_id=? AND var=? AND name=?");
            offline.setString(1, value);
            offline.setInt(2, leaderId);
            offline.setString(3, var);
            offline.setString(4, name);
            offline.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DbUtils.closeQuietly(con, offline);
        }
    }

    public void Q503_suscribe_members(QuestState st) {
        int clan = st.getPlayer().getClan().getClanId();
        Connection con = null;
        PreparedStatement offline = null;
        PreparedStatement insertion = null;
        ResultSet rs = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            offline = con.prepareStatement("SELECT obj_Id FROM characters WHERE clanid=? AND online=0");
            insertion = con.prepareStatement("REPLACE INTO character_quests (char_id,name,var,value) VALUES (?,?,?,?)");
            offline.setInt(1, clan);
            rs = offline.executeQuery();
            while (rs.next()) {
                int char_id = rs.getInt("obj_Id");
                try {
                    insertion.setInt(1, char_id);
                    insertion.setString(2, st.getQuest().getName());
                    insertion.setString(3, "<state>");
                    insertion.setString(4, "Started");
                    insertion.executeUpdate();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DbUtils.closeQuietly(insertion);
            DbUtils.closeQuietly(con, offline, rs);
        }
    }
}

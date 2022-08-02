package l2p.gameserver.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import l2p.commons.dbutils.DbUtils;
import l2p.gameserver.Config;
import l2p.gameserver.data.xml.holder.SkillAcquireHolder;
import l2p.gameserver.database.DatabaseFactory;
import l2p.gameserver.model.Player;
import l2p.gameserver.model.Skill;
import l2p.gameserver.model.entity.Hero;
import l2p.gameserver.model.pledge.Clan;
import l2p.gameserver.tables.SkillTable;
import l2p.gameserver.utils.SiegeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CharacterSkillDAO {

    private static final Logger _log = LoggerFactory.getLogger(CharacterSkillDAO.class);
    private static CharacterSkillDAO _instance = new CharacterSkillDAO();

    public static CharacterSkillDAO getInstance() {
        return _instance;
    }

    public void select(Player player) {
        Connection con = null;
        PreparedStatement statement = null;
        ResultSet rset = null;
        try {
            // Retrieve all skills of this L2Player from the database
            // Send the SQL query : SELECT skill_id,skill_level FROM character_skills WHERE char_obj_id=? to the database
            
            con = DatabaseFactory.getInstance().getConnection();
            if (Config.ALT_ENABLE_MULTI_PROFA) {
                statement = con.prepareStatement("SELECT skill_id,skill_level FROM character_skills WHERE char_obj_id=?");
                statement.setInt(1, player.getObjectId());
            } else {
                statement = con.prepareStatement("SELECT skill_id,skill_level FROM character_skills WHERE char_obj_id=? AND class_index=?");
                statement.setInt(1, player.getObjectId());
                statement.setInt(2, player.getActiveClassId());
            }
            
            rset = statement.executeQuery();

            // Go though the recordset of this SQL query
            while (rset.next()) {
                final int id = rset.getInt("skill_id");
                final int level = rset.getInt("skill_level");

                // Create a L2Skill object for each record
                final Skill skill = SkillTable.getInstance().getInfo(id, level);

                if (skill == null) {
                    continue;
                }

                if (Config.ALT_ENABLE_MULTI_SKILL) {
                    // Remove skill if not possible
                    if (!player.isGM() && !SkillAcquireHolder.getInstance().isSkillPossible(player, skill)) {
                        //int ReturnSP = SkillTreeTable.getInstance().getSkillCost(this, skill);
                        //if(ReturnSP == Integer.MAX_VALUE || ReturnSP < 0)
                        //		ReturnSP = 0;
                        player.removeSkill(skill, true);
                        player.removeSkillFromShortCut(skill.getId());
                        //if(ReturnSP > 0)
                        //		setSp(getSp() + ReturnSP);
                        //TODO audit
                        continue;
                    }
                }

                player.addSkill(skill);
            }

            // Restore noble skills
            if (player.isNoble()) {
                player.updateNobleSkills();
            }

            // Restore Hero skills at main class only
            if (player.isHero() && player.getBaseClassId() == player.getActiveClassId()) {
                Hero.addSkills(player);
            }

            Clan clan = player.getClan();

            // Restore clan skills
            if (clan != null) {
                clan.addSkillsQuietly(player);

                // Restore clan leader siege skills
                if (clan.getLeaderId() == player.getObjectId() && clan.getLevel() >= 5) {
                    SiegeUtils.addSiegeSkills(player);
                }
            }

            // Give dwarven craft skill
            if (player.getActiveClassId() >= 53 && player.getActiveClassId() <= 57 || player.getActiveClassId() == 117 || player.getActiveClassId() == 118) {
                player.addSkill(SkillTable.getInstance().getInfo(1321, 1));
            }

            player.addSkill(SkillTable.getInstance().getInfo(1322, 1));

            if (Config.UNSTUCK_SKILL && player.getSkillLevel(1050) < 0) {
                player.addSkill(SkillTable.getInstance().getInfo(2099, 1));
            }
        } catch (final Exception e) {
            _log.warn("Could not restore skills for player objId: " + player.getObjectId());
            _log.error("", e);
        } finally {
            DbUtils.closeQuietly(con, statement, rset);
        }
    }

    public void replace(Player player, Skill newSkill) {
        Connection con = null;
        PreparedStatement statement = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement("REPLACE INTO character_skills (char_obj_id,skill_id,skill_level,class_index) values(?,?,?,?)");
            statement.setInt(1, player.getObjectId());
            statement.setInt(2, newSkill.getId());
            statement.setInt(3, newSkill.getLevel());
            statement.setInt(4, player.getActiveClassId());
            statement.execute();
        } catch (final Exception e) {
            _log.error("Error could not store skills!", e);
        } finally {
            DbUtils.closeQuietly(con, statement);
        }
    }

    public void delete(Player player, Skill oldSkill) {
        Connection con = null;
        PreparedStatement statement = null;
        try {
            // Remove or update a L2Player skill from the character_skills table of the database
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement("DELETE FROM character_skills WHERE skill_id=? AND char_obj_id=? AND class_index=?");
            statement.setInt(1, oldSkill.getId());
            statement.setInt(2, player.getObjectId());
            statement.setInt(3, player.getActiveClassId());
            statement.execute();
        } catch (final Exception e) {
            _log.error("Could not delete skill!", e);
        } finally {
            DbUtils.closeQuietly(con, statement);
        }
    }

    public void deleteClass(Player player, Skill oldSkill) {
        Connection con = null;
        PreparedStatement statement = null;
        try {
            // Remove or update a L2Player skill from the character_skills table of the database
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement("DELETE FROM character_skills WHERE skill_id=? AND char_obj_id=?");
            statement.setInt(1, oldSkill.getId());
            statement.setInt(2, player.getObjectId());
            statement.execute();
        } catch (final Exception e) {
            _log.error("Could not delete skill!", e);
        } finally {
            DbUtils.closeQuietly(con, statement);
        }
    }
}

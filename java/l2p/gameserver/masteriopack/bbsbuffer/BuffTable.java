package l2p.gameserver.masteriopack.bbsbuffer;

import l2p.gameserver.masteriopack.bbsbuffer.BBSBuffer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javolution.util.FastList;
import javolution.util.FastMap;
import l2p.gameserver.Config;
import l2p.gameserver.database.DatabaseFactory;
import org.slf4j.LoggerFactory;

/**
 * Class contains BBSBuffer buffs data.
 *
 * @author Masterio
 *
 */
public class BuffTable {

    private static final org.slf4j.Logger _log = LoggerFactory.getLogger(BuffTable.class);

    private static BuffTable _instance = null;

    /**
     * [skillId, Buff]
     */
    private FastMap<Integer, Buff> _buffList = new FastMap<>();

    private FastList<Integer> _buffGroupList = new FastList<>();

    private BuffTable() {
        load();

        loadBuffGroups();

        _log.info("BuffTable: loaded " + getBuffList().size() + " objects.");
    }

    public static BuffTable getInstance() {
        if (_instance == null) {
            _instance = new BuffTable();
        }

        return _instance;
    }

    private void load() {

        Connection con = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            final PreparedStatement statement = con.prepareStatement("select bbsbuffer_buffs_info.skill_id, skill_level, skill_name, description as skill_description, same_effect_group, group_id, duration_minutes, item_id, item_count from bbsbuffer_buffs_info JOIN bbsbuffer_buffs ON bbsbuffer_buffs_info.skill_id = bbsbuffer_buffs.skill_id");
            final ResultSet rset = statement.executeQuery();

            while (rset.next()) {
                Buff buff = new Buff();

                buff.setSkillId(rset.getInt("skill_id"));
                buff.setSkillLevel(rset.getInt("skill_level"));
                buff.setSkillName(rset.getString("skill_name"));
                buff.setSkillDescription(rset.getString("skill_description"));
                buff.setSameEffectGroup(rset.getBoolean("same_effect_group"));
                buff.setGroupId(rset.getInt("group_id"));
                buff.setDurationMinutes(rset.getInt("duration_minutes"));
                buff.setItemId(rset.getInt("item_id"));
                buff.setItemCount(rset.getLong("item_count"));

                _buffList.put(buff.getSkillId(), buff);

            }

            statement.close();
            rset.close();

        } catch (Exception e) {
            _log.warn("data error on item" + " " + e);
        } finally {
            try {
                if (con != null) {
                    con.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @return the _buffList
     */
    public FastMap<Integer, Buff> getBuffList() {
        return _buffList;
    }

    /**
     * @param _buffList the _buffList to set
     */
    public void setBuffList(FastMap<Integer, Buff> _buffList) {
        _buffList = _buffList;
    }

    /**
     * Get buff list filtered by BBSBuffer.Filter().
     *
     * @param BB
     * @return
     */
    public FastMap<Integer, Buff> getBuffList(BBSBuffer BB) {

        if (BB == null) {
            return new FastMap<>();
        }

        FastMap<Integer, Buff> buffList = new FastMap<>();

        int i = 0;

        if (Config.BUFF_GROUPS_ENABLED) {

            for (FastMap.Entry<Integer, Buff> e = _buffList.head(), end = _buffList.tail(); (e = e.getNext()) != end;) {

                if ((BB.getFilter().getShowBuffsFromGroup() == 0 || e.getValue().getGroupId() == BB.getFilter().getShowBuffsFromGroup())) {

                    if (BB.getFilter().isShowBuffsWithTheSameEffect()) {
                        if (i >= Config.BUFF_LIST_LENGTH * (BB.getPage() - 1) && i < Config.BUFF_LIST_LENGTH * (BB.getPage())) {
                            buffList.put(e.getKey(), e.getValue());
                        }

                        i++; // count all allowed buffs
                    } else if (!BB.getFilter().isShowBuffsWithTheSameEffect() && !e.getValue().isSameEffectGroup()) {
                        if (i >= Config.BUFF_LIST_LENGTH * (BB.getPage() - 1) && i < Config.BUFF_LIST_LENGTH * (BB.getPage())) {
                            buffList.put(e.getKey(), e.getValue());
                        }

                        i++; // count all allowed buffs
                    }

                }

            }

        } else {

            for (FastMap.Entry<Integer, Buff> e = _buffList.head(), end = _buffList.tail(); (e = e.getNext()) != end;) {

                if (!BB.getFilter().isShowBuffsWithTheSameEffect() && !e.getValue().isSameEffectGroup()) {

                    if (BB.getFilter().isShowBuffsWithTheSameEffect()) {
                        if (i >= Config.BUFF_LIST_LENGTH * (BB.getPage() - 1) && i < Config.BUFF_LIST_LENGTH * (BB.getPage())) {
                            buffList.put(e.getKey(), e.getValue());
                        }

                        i++; // count all allowed buffs
                    } else if (!BB.getFilter().isShowBuffsWithTheSameEffect() && !e.getValue().isSameEffectGroup()) {
                        if (i >= Config.BUFF_LIST_LENGTH * (BB.getPage() - 1) && i < Config.BUFF_LIST_LENGTH * (BB.getPage())) {
                            buffList.put(e.getKey(), e.getValue());
                        }

                        i++; // count all allowed buffs
                    }

                }

            }

        }

        BB.setBuffsCount(i);

        return buffList;
    }

    public Buff getBuffBySkillId(int skillId) {

        if (_buffList.containsKey(skillId)) {
            return _buffList.get(skillId);
        }

        return null;
    }

    public FastList<Integer> getBuffGroupList() {
        return _buffGroupList;
    }

    public void setBuffGroupList(FastList<Integer> _buffGroupList) {
        this._buffGroupList = _buffGroupList;
    }

    /**
     * Initializing buff groups list, used to generate buff group menu.
     */
    private void loadBuffGroups() {
        _buffGroupList.add(0);

        for (FastMap.Entry<Integer, Buff> e = _buffList.head(), end = _buffList.tail(); (e = e.getNext()) != end;) {
            if (!_buffGroupList.contains(e.getValue().getGroupId())) {
                _buffGroupList.add(e.getValue().getGroupId());
            }
        }

    }

}

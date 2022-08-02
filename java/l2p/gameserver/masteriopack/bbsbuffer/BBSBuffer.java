package l2p.gameserver.masteriopack.bbsbuffer;

import java.util.concurrent.ScheduledFuture;
import javolution.util.FastList;
import javolution.util.FastMap;
import l2p.gameserver.Config;
import l2p.gameserver.ThreadPoolManager;
import l2p.gameserver.model.Player;
import l2p.gameserver.model.Skill;
import l2p.gameserver.tables.SkillTable;
import l2p.gameserver.utils.ItemFunctions;

/**
 * @author Masterio
 */
public class BBSBuffer {

    private Player _activeChar = null;

    private int _mode = BufferMode.SHOW_MAINPAGE;

    /**
     * store clone of buffScheme, if i remove buffs and cancel action i
     * shouldn't lose original scheme.
     */
    private Scheme _tempBuffScheme = null;

    private Filter _filter = new Filter();

    private Character _character = null; // class contains character buff schemes and data.

    private int _page = 1;
    private int _pageCount = 1;
    private int _buffsCount = 0;

    private String _error_msg = "&nbsp;";

    // AutoRebuff:
    private int _rebuffPlayerSchemeId = 0; // if scheme id = 0 it is mean no rebuff.
    private int _rebuffSummonSchemeId = 0; // if scheme id = 0 it is mean no rebuff.
    // If TRUE then task is already added (You shouldn't starts new task then).
    private boolean _rebuffPlayerTaskExecute = false;
    private boolean _rebuffSummonTaskExecute = false;

    private ScheduledFuture<?> _rebuffPlayerTaskSchedule = null;
    private ScheduledFuture<?> _rebuffSummonTaskSchedule = null;

    // ==============================================================================================
    public BBSBuffer(Player activeChar) {
        _activeChar = activeChar;
        _character = CharacterTable.getInstance().getCharacterById(getActiveChar().getObjectId());
    }

    private boolean canUseBuff() {
        return BBSBufferChecker.checkBuffConditions(getActiveChar());
    }

    /**
     * Used for execute single buff on target. Target is obtained from Filter.
     *
     * @param skillId
     * @param checkConditions - check or don't check buff conditions. <br>Don't
     * check each time for each buff when scheme is executing.
     * @return
     */
    public boolean useBuff(int skillId, boolean checkConditions) {

        if (checkConditions && !canUseBuff()) {
            return false;
        }

        Buff buff = BuffTable.getInstance().getBuffBySkillId(skillId);

        if (buff != null) {
            if (ItemFunctions.getItemCount(getActiveChar(), buff.getItemId()) >= buff.getItemCount()) {
                {
                    if (getFilter().isShowBuffsForPlayer()) {
                        if (Config.CUSTOM_BUFF_TIMES_ENABLED) {
                            Skill skill = SkillTable.getInstance().getInfo(buff.getSkillId(), buff.getSkillLevel());

                            skill.getEffects(getActiveChar(), getActiveChar(), false, false, buff.getDurationMinutes() * 60000, 1.0, 0);
                        } else {
                            Skill skill = SkillTable.getInstance().getInfo(buff.getSkillId(), buff.getSkillLevel());

                            skill.getEffects(getActiveChar(), getActiveChar(), false, false);
                        }
                        if (Config.BBSBUFFER_ENABLED_PRICE) {
                            ItemFunctions.removeItem(getActiveChar(), buff.getItemId(), buff.getItemCount(), true);
                        }
                        return true;
                    } else if (!getFilter().isShowBuffsForPlayer() && getActiveChar().getPet() != null) {
                        if (Config.CUSTOM_BUFF_TIMES_ENABLED) {
                            Skill skill = SkillTable.getInstance().getInfo(buff.getSkillId(), buff.getSkillLevel());

                            skill.getEffects(getActiveChar().getPet(), getActiveChar().getPet(), false, false, buff.getDurationMinutes() * 60000, 1.0, 0);
                        } else {
                            Skill skill = SkillTable.getInstance().getInfo(buff.getSkillId(), buff.getSkillLevel());

                            skill.getEffects(getActiveChar().getPet(), getActiveChar().getPet(), false, false);
                        }
                        if (Config.BBSBUFFER_ENABLED_PRICE) {
                            ItemFunctions.removeItem(getActiveChar(), buff.getItemId(), buff.getItemCount(), true);
                        }
                        return true;
                    }
                }
            } else {
                if (getActiveChar().isLangRus()) {
                    getActiveChar().sendMessage("Недостаточно средств!");
                } else {
                    getActiveChar().sendMessage("It is not enough money!");
                }
            }
        }

        return false;
    }

    public void useBuffScheme(int schemeId, boolean checkConditions) {
        if (checkConditions && !canUseBuff()) {
            return;
        }

        Scheme scheme = getCharacter().getSchemeById(schemeId);

        if (scheme == null) {
            return;
        }

        for (FastList.Node<Integer> n = scheme.getBuffList().head(), end = scheme.getBuffList().tail(); (n = n.getNext()) != end;) {
            useBuff(n.getValue(), false);
        }
    }

    public void usePredefinedBuffScheme(int schemeId, boolean checkConditions) {
        if (checkConditions && !canUseBuff()) {
            return;
        }

        if (schemeId == 1) {
            for (int skillId : Config.PREDEFINED_SCHEME_1) {
                useBuff(skillId, false);
            }
        } else if (schemeId == 2) {
            for (int skillId : Config.PREDEFINED_SCHEME_2) {
                useBuff(skillId, false);
            }
        }

    }

    /**
     * Method used by auto-rebuff.
     *
     * @param skillId
     * @param checkConditions
     * @param useOnPlayer - if FALSE buff will be used on players summon.
     * @return
     */
    public boolean useBuff(int skillId, boolean checkConditions, boolean useOnPlayer) {

        if (checkConditions && !canUseBuff()) {
            return false;
        }

        Buff buff = BuffTable.getInstance().getBuffBySkillId(skillId);

        if (buff != null) {
            if (ItemFunctions.getItemCount(getActiveChar(), buff.getItemId()) >= buff.getItemCount()) {
                if (useOnPlayer) {
                    if (Config.CUSTOM_BUFF_TIMES_ENABLED) {
                        Skill skill = SkillTable.getInstance().getInfo(buff.getSkillId(), buff.getSkillLevel());

                        skill.getEffects(getActiveChar(), getActiveChar(), false, false, buff.getDurationMinutes() * 60000, 1.0, 0);
                    } else {
                        Skill skill = SkillTable.getInstance().getInfo(buff.getSkillId(), buff.getSkillLevel());

                        skill.getEffects(getActiveChar(), getActiveChar(), false, false);
                    }
                    if (Config.BBSBUFFER_ENABLED_PRICE) {
                        ItemFunctions.removeItem(getActiveChar(), buff.getItemId(), buff.getItemCount(), true);
                    }
                    return true;
                } else if (!useOnPlayer && getActiveChar().getPet() != null) {
                    if (Config.CUSTOM_BUFF_TIMES_ENABLED) {
                        Skill skill = SkillTable.getInstance().getInfo(buff.getSkillId(), buff.getSkillLevel());

                        skill.getEffects(getActiveChar().getPet(), getActiveChar().getPet(), false, false, buff.getDurationMinutes() * 60000, 1.0, 0);
                    } else {
                        Skill skill = SkillTable.getInstance().getInfo(buff.getSkillId(), buff.getSkillLevel());

                        skill.getEffects(getActiveChar().getPet(), getActiveChar().getPet(), false, false);
                    }
                    if (Config.BBSBUFFER_ENABLED_PRICE) {
                        ItemFunctions.removeItem(getActiveChar(), buff.getItemId(), buff.getItemCount(), true);
                    }
                    return true;
                }
            } else {
                if (getActiveChar().isLangRus()) {
                    getActiveChar().sendMessage("Недостаточно средств!");
                } else {
                    getActiveChar().sendMessage("It is not enough money!");
                }
            }
        }

        return false;
    }

    /**
     * Method used by auto-rebuff.
     *
     * @param schemeId
     * @param checkConditions
     * @param useOnPlayer - if FALSE buff will be used on players summon.
     */
    public void useBuffScheme(int schemeId, boolean checkConditions, boolean useOnPlayer) {
        if (checkConditions && !canUseBuff()) {
            return;
        }

        Scheme scheme = getCharacter().getSchemeById(schemeId);

        if (scheme == null) {
            return;
        }

        for (FastList.Node<Integer> n = scheme.getBuffList().head(), end = scheme.getBuffList().tail(); (n = n.getNext()) != end;) {
            useBuff(n.getValue(), false, useOnPlayer);
        }
    }

    /**
     * Load existing buff scheme from model into tempBuffScheme.
     *
     * @param schemeId
     */
    public void loadBuffScheme(int schemeId) {
        if (getCharacter() != null) {
            setTempBuffScheme(getCharacter().getSchemeById(schemeId).getClone());
        }

    }

    /**
     * Save scheme if created or edited, is not saved in database.
     */
    public void saveBuffScheme() {
        if (getTempBuffScheme().getBuffList().size() > 0) {
            if (getCharacter() != null) {
                if (getTempBuffScheme().getDbStatus() != DBStatus.INSERTED) {
                    getTempBuffScheme().setDbStatus(DBStatus.UPDATED);
                }

                // if scheme is edited:
                if (getTempBuffScheme().getSchemeId() > 0) {
                    getCharacter().setScheme(getTempBuffScheme());
                } else {
                    getCharacter().addScheme(getTempBuffScheme());
                }

                setTempBuffScheme(null);

                setMode(BufferMode.SHOW_SCHEME_MANAGER_EDIT);
            }
        } else {
            getActiveChar().sendMessage("Вы должны выбрать сначало несколько бафов!");
        }

    }

    public void removeBuffScheme(int schemeId) {
        if (getCharacter() != null) {
            getCharacter().removeScheme(schemeId);
        }

    }

    public void selectAllSchemeBuffs() {
        FastMap<Integer, Buff> buffList = BuffTable.getInstance().getBuffList(this);

        for (FastMap.Entry<Integer, Buff> e = buffList.head(), end = buffList.tail(); (e = e.getNext()) != end;) {
            if (getTempBuffScheme().getBuffList().size() < Config.SCHEME_BUFF_LIMIT) {
                getTempBuffScheme().addBuff(e.getKey());
            } else {
                getActiveChar().sendMessage("Нельзя выбрать больше бафов!");
                break;
            }
        }
    }

    public void deselectAllSchemeBuffs() {
        FastMap<Integer, Buff> buffList = BuffTable.getInstance().getBuffList(this);

        for (FastMap.Entry<Integer, Buff> e = buffList.head(), end = buffList.tail(); (e = e.getNext()) != end;) {
            getTempBuffScheme().removeBuff(e.getKey());
        }
    }

    public void restoreCpHpMp() {
        if (!BBSBufferChecker.checkRestoreConditions(getActiveChar())) {
            return;
        }

        if (getFilter().isShowBuffsForPlayer()) {
            getActiveChar().setCurrentCp(getActiveChar().getMaxCp());
            getActiveChar().setCurrentHpMp(getActiveChar().getMaxHp(), getActiveChar().getMaxMp());
        } else if (getActiveChar().getPet() != null) {
            getActiveChar().getPet().setCurrentCp(getActiveChar().getPet().getMaxCp());
            getActiveChar().getPet().setCurrentHpMp(getActiveChar().getPet().getMaxHp(), getActiveChar().getPet().getMaxMp());
        }

    }

    public void cancelBuffs() {
        if (!canUseBuff()) {
            return;
        }

        if (getFilter().isShowBuffsForPlayer()) {
            if (getActiveChar().getEffectList().getEffectsBySkillId(Skill.SKILL_RAID_CURSE) == null) {
                SkillTable.getInstance().getInfo(4094, 12).getEffects(getActiveChar(), getActiveChar(), false, false); // NPC Cancel Magic effect.
                getActiveChar().getEffectList().stopAllEffects();
            } else {
                getActiveChar().sendMessage("You can not use Buffer, if you have Raid Curse!");
            }
        } else if (getActiveChar().getPet() != null) {
            if (getActiveChar().getPet().getEffectList().getEffectsBySkillId(Skill.SKILL_RAID_CURSE) == null) {
                SkillTable.getInstance().getInfo(4094, 12).getEffects(getActiveChar(), getActiveChar().getPet(), false, false); // NPC Cancel Magic effect.
                getActiveChar().getPet().getEffectList().stopAllEffects();
            } else {
                getActiveChar().sendMessage("You can not use Buffer, if you have Raid Curse!");
            }
        }
    }

    /**
     * Reset page number, page count, filter, error message.
     */
    public void reset() {
        _page = 1;
        _pageCount = 1;
        _buffsCount = 0;
        _filter = new Filter();
        _error_msg = "&nbsp;";
    }

    /**
     * @return the _activeChar
     */
    public Player getActiveChar() {
        return _activeChar;
    }

    /**
     * @param _activeChar the _activeChar to set
     */
    public void setActiveChar(Player _activeChar) {
        this._activeChar = _activeChar;
    }

    /**
     * @return the _mode
     */
    public int getMode() {
        return _mode;
    }

    /**
     * @param _mode the _mode to set
     */
    public void setMode(int _mode) {
        this._mode = _mode;
    }

    /**
     * @return the _page
     */
    public int getPage() {
        return _page;
    }

    /**
     * @param page the _page to set
     */
    public void setPage(int page) {
        if (page > 0) {
            _page = page;
        } else {
            _page = 1;
        }

    }

    /**
     * @return the _pageCount
     */
    public int getPageCount() {
        return _pageCount;
    }

    /**
     * @param _pageCount the _pageCount to set
     */
    private void setPageCount(int _pageCount) {
        this._pageCount = _pageCount;
    }

    /**
     * @return the _buffCount
     */
    public int getBuffsCount() {
        return _buffsCount;
    }

    /**
     * Set buff total count and calculate page count.
     *
     * @param buffsCount the _buffCount to set
     */
    public void setBuffsCount(int buffsCount) {
        double totalPagesRatio = ((double) buffsCount / (double) Config.BUFF_LIST_LENGTH);

        int totalPages = (int) Math.round(totalPagesRatio);

        if (totalPagesRatio > totalPages) {
            totalPages++; // calculate total pages count.
        }

        if (totalPages == 0) {
            totalPages = 1;
        }

        setPageCount(totalPages);

        _buffsCount = buffsCount;
    }

    /**
     * @return the _error_msg
     */
    public String getErrorMessage() {
        return _error_msg;
    }

    /**
     * @param _error_msg the _error_msg to set
     */
    public void setErrorMessage(String _error_msg) {
        this._error_msg = _error_msg;
    }

    /**
     * @return the _tempBuffScheme
     */
    public Scheme getTempBuffScheme() {
        if (_tempBuffScheme == null) {
            _tempBuffScheme = new Scheme();
        }

        return _tempBuffScheme;
    }

    /**
     * @param tempBuffScheme the tempBuffScheme to set
     */
    public void setTempBuffScheme(Scheme tempBuffScheme) {
        _tempBuffScheme = tempBuffScheme;
    }

    public void clearTempBuffScheme() {
        this._tempBuffScheme = new Scheme();
    }

    public Filter getFilter() {

        if (_filter == null) {
            _filter = new Filter();
        }

        return _filter;

    }

    public void setFilter(Filter filter) {

        _filter = filter;

    }

    /**
     * This is character buffer data for Player.
     *
     * @return
     */
    public Character getCharacter() {
        if (_character == null) {
            _character = CharacterTable.getInstance().getCharacterById(getActiveChar().getObjectId());
        }

        return _character;
    }

    public void setCharacter(Character character) {
        _character = character;
    }

    public int getRebuffPlayerSchemeId() {
        return _rebuffPlayerSchemeId;
    }

    /**
     * If 0 rebuff not selected.
     *
     * @param _rebuffPlayerSchemeId
     */
    public void setRebuffPlayerSchemeId(int _rebuffPlayerSchemeId) {
        this._rebuffPlayerSchemeId = _rebuffPlayerSchemeId;
    }

    public int getRebuffSummonSchemeId() {
        return _rebuffSummonSchemeId;
    }

    public void setRebuffSummonSchemeId(int _rebuffSummonSchemeId) {
        this._rebuffSummonSchemeId = _rebuffSummonSchemeId;
    }
    /*
     public boolean isRebuffPlayer()
     {
     return _rebuffPlayer;
     }

     public void setRebuffPlayer(boolean _rebuffPlayer)
     {
     this._rebuffPlayer = _rebuffPlayer;
     }
	
     public boolean isRebuffSummon() 
     {
     return _rebuffSummon;
     }

     public void setRebuffSummon(boolean _rebuffSummon) 
     {
     this._rebuffSummon = _rebuffSummon;
     }

     public boolean isRebuffPlayerTask() {
     return _rebuffPlayerTask;
     }

     public void setRebuffPlayerTask(boolean _rebuffPlayerTask) {
     this._rebuffPlayerTask = _rebuffPlayerTask;
     }
	
     public boolean isRebuffSummonTask() {
     return _rebuffSummonTask;
     }

     public void setRebuffSummonTask(boolean _rebuffSummonTask) {
     this._rebuffSummonTask = _rebuffSummonTask;
     }
     */

    public boolean isRebuffPlayerTaskExecute() {
        return _rebuffPlayerTaskExecute;
    }

    public void setRebuffPlayerTaskExecute(boolean _rebuffPlayerTaskExecute) {
        this._rebuffPlayerTaskExecute = _rebuffPlayerTaskExecute;
    }

    public void startRebuffPlayerTaskSchedule() {
        setRebuffPlayerTaskSchedule(ThreadPoolManager.getInstance().schedule(new AutoRebuffPlayer(getActiveChar()), Config.AUTOREBUFF_DELAY));
        setRebuffPlayerTaskExecute(true);
    }

    public void startRebuffSummonTaskSchedule() {
        setRebuffSummonTaskSchedule(ThreadPoolManager.getInstance().schedule(new AutoRebuffSummon(getActiveChar()), Config.AUTOREBUFF_DELAY));
        setRebuffSummonTaskExecute(true);
    }

    public void stopRebuffPlayerTaskSchedule() {
        getRebuffPlayerTaskSchedule().cancel(false);
        setRebuffPlayerTaskExecute(false);
    }

    public void stopRebuffSummonTaskSchedule() {
        getRebuffSummonTaskSchedule().cancel(false);
        setRebuffSummonTaskExecute(false);
    }

    public boolean isRebuffSummonTaskExecute() {
        return _rebuffSummonTaskExecute;
    }

    public void setRebuffSummonTaskExecute(boolean _rebuffSummonTaskExecute) {
        this._rebuffSummonTaskExecute = _rebuffSummonTaskExecute;
    }

    public ScheduledFuture<?> getRebuffPlayerTaskSchedule() {
        return _rebuffPlayerTaskSchedule;
    }

    public void setRebuffPlayerTaskSchedule(ScheduledFuture<?> _rebuffPlayerTaskSchedule) {
        this._rebuffPlayerTaskSchedule = _rebuffPlayerTaskSchedule;
    }

    public ScheduledFuture<?> getRebuffSummonTaskSchedule() {
        return _rebuffSummonTaskSchedule;
    }

    public void setRebuffSummonTaskSchedule(ScheduledFuture<?> _rebuffSummonTaskSchedule) {
        this._rebuffSummonTaskSchedule = _rebuffSummonTaskSchedule;
    }

    /**
     * Check if skillId exists in scheme selected as auto-rebuff.
     *
     * @param skillId
     * @param forPlayer - TRUE: Player, FALSE: Summon.
     * @return
     */
    public boolean isSkillInSchemeExists(int skillId, boolean forPlayer) {
        if (forPlayer && getCharacter().getSchemeById(getRebuffPlayerSchemeId()).getBuffList().contains(skillId)) {
            return true;
        } else if (!forPlayer && getCharacter().getSchemeById(getRebuffSummonSchemeId()).getBuffList().contains(skillId)) {
            return true;
        }

        return false;
    }

    class AutoRebuffPlayer implements Runnable {

        //public int schemeId = 0;
        public Player player = null;
        public BBSBuffer BB = null;

        public AutoRebuffPlayer(Player player) {
            //this.schemeId = schemeId;
            this.player = player;

            if (player._bbsbuffer != null) {
                BB = player._bbsbuffer;
            }
        }

        @Override
        public void run() {
            if (player != null && BB != null) {
                if (BB.getRebuffPlayerSchemeId() > 0 && BBSBufferChecker.checkAutoRebuffConditionsPlayer(player)) {
                    BB.useBuffScheme(BB.getRebuffPlayerSchemeId(), true, true);

                    try {
                        // Current thread wait for Effects Task ends (~1000ms).
                        Thread.sleep(2000);

                        if (player != null && BB != null) {
                            BB.stopRebuffPlayerTaskSchedule();
                        }

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    if (player != null && BB != null) {
                        BB.startRebuffPlayerTaskSchedule();
                    }
                }
            }
        }
    }

    class AutoRebuffSummon implements Runnable {

        public Player player = null;
        public BBSBuffer BB = null;

        public AutoRebuffSummon(Player player) {
            this.player = player;

            if (player._bbsbuffer != null) {
                BB = player._bbsbuffer;
            }
        }

        @Override
        public void run() {
            if (player != null && BB != null) {
                if (BB.getRebuffSummonSchemeId() > 0 && player.getPet() != null && BBSBufferChecker.checkAutoRebuffConditionsSummon(player.getPet())) {
                    BB.useBuffScheme(BB.getRebuffSummonSchemeId(), true, false);

                    try {
                        // Current thread wait for Effects Task ends (~1000ms).
                        Thread.sleep(2000);

                        if (player != null && BB != null) {
                            BB.stopRebuffSummonTaskSchedule();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                } else {
                    if (player != null && BB != null) {
                        BB.startRebuffSummonTaskSchedule();
                    }
                }
            }
        }
    }
}

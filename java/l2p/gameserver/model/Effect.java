package l2p.gameserver.model;

import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import l2p.commons.threading.RunnableImpl;
import l2p.gameserver.Config;
import l2p.gameserver.listener.actor.OnAttackListener;
import l2p.gameserver.listener.actor.OnMagicUseListener;
import l2p.gameserver.masteriopack.bbsbuffer.BBSBuffer;
import l2p.gameserver.serverpackets.AbnormalStatusUpdate;
import l2p.gameserver.serverpackets.ExOlympiadSpelledInfo;
import l2p.gameserver.serverpackets.PartySpelled;
import l2p.gameserver.serverpackets.ShortBuffStatusUpdate;
import l2p.gameserver.serverpackets.SystemMessage2;
import l2p.gameserver.serverpackets.components.SystemMsg;
import l2p.gameserver.skills.AbnormalEffect;
import l2p.gameserver.skills.EffectType;
import l2p.gameserver.skills.effects.EffectTemplate;
import l2p.gameserver.stats.Env;
import l2p.gameserver.stats.funcs.Func;
import l2p.gameserver.stats.funcs.FuncOwner;
import l2p.gameserver.tables.SkillTable;
import l2p.gameserver.taskmanager.EffectTaskManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Effect extends RunnableImpl implements Comparable<Effect>, FuncOwner {

    protected static final Logger _log = LoggerFactory.getLogger(Effect.class);
    public final static Effect[] EMPTY_L2EFFECT_ARRAY = new Effect[0];
    //Состояние, при котором работает задача запланированного эффекта
    public static int SUSPENDED = -1;
    public static int STARTING = 0;
    public static int STARTED = 1;
    public static int ACTING = 2;
    public static int FINISHING = 3;
    public static int FINISHED = 4;
    /**
     * Накладывающий эффект
     */
    protected final Creature _effector;
    /**
     * Тот, на кого накладывают эффект
     */
    protected final Creature _effected;
    protected final Skill _skill;
    protected final int _displayId;
    protected final int _displayLevel;
    // the value of an update
    private final double _value;
    // the current state
    private final AtomicInteger _state;
    // counter
    private int _count;
    // period, milliseconds
    private long _period;
    private long _startTimeMillis;
    private long _duration;
    private boolean _inUse = false;
    private Effect _next = null;
    private boolean _active = false;
    protected final EffectTemplate _template;
    private Future<?> _effectTask;
    protected Effect(Env env, EffectTemplate template) {
        _skill = env.skill;
        _effector = env.character;
        _effected = env.target;

        _template = template;
        _value = template._value;
        _count = template.getCount();
        _period = template.getPeriod();

        _duration = _period * _count;

        _displayId = template._displayId != 0 ? template._displayId : _skill.getDisplayId();
        _displayLevel = template._displayLevel != 0 ? template._displayLevel : _skill.getDisplayLevel();

        _state = new AtomicInteger(STARTING);
    }

    public long getPeriod() {
        return _period;
    }

    public void setPeriod(long time) {
        _period = time;
        _duration = _period * _count;
    }

    public int getCount() {
        return _count;
    }

    public void setCount(int count) {
        _count = count;
        _duration = _period * _count;
    }

    public boolean isOneTime() {
        return _period == 0;
    }

    /**
     * Используется для того чтобы "сдвинуть в прошлое" время старта для
     * корректной сортировки эффектов запущенных в одно и то же время (загрузка,
     * комьюнити-бафер). Крайне не рекомендуется корректировать время старта
     * больше чем на несколько десятков миллисекунд, а также "сдвигать в
     * будущее".
     *
     * @param fixConst время коррекции времени старта в миллисекундах
     */
    public final void fixStartTime(int fixConst) {
        if (_startTimeMillis == 0L) {
            _startTimeMillis = System.currentTimeMillis() - fixConst;
        } else {
            _startTimeMillis -= fixConst;
        }
    }

    /**
     * Возвращает время старта эффекта, если время не установлено, возвращается
     * текущее
     */
    public long getStartTime() {
        if (_startTimeMillis == 0L) {
            return System.currentTimeMillis();
        }
        return _startTimeMillis;
    }

    /**
     * Возвращает общее время действия эффекта в миллисекундах.
     */
    public long getTime() {
        return System.currentTimeMillis() - getStartTime();
    }

    /**
     * Возвращает длительность эффекта в миллисекундах.
     */
    public long getDuration() {
        return _duration;
    }

    /**
     * Возвращает оставшееся время в секундах.
     */
    public int getTimeLeft() {
        return (int) Math.round((getDuration() - getTime()) / 1000.);
    }

    /**
     * Возвращает true, если осталось время для действия эффекта
     */
    public boolean isTimeLeft() {
        return getDuration() - getTime() > 0L;
    }

    public boolean isInUse() {
        return _inUse;
    }

    public void setInUse(boolean inUse) {
        _inUse = inUse;
    }

    public boolean isActive() {
        return _active;
    }

    /**
     * Для неактивных эфектов не вызывается onActionTime.
     */
    public void setActive(boolean set) {
        _active = set;
    }

    public EffectTemplate getTemplate() {
        return _template;
    }

    public String getStackType() {
        return getTemplate()._stackType;
    }

    public String getStackType2() {
        return getTemplate()._stackType2;
    }

    public boolean checkStackType(String param) {
        return getStackType().equalsIgnoreCase(param) || getStackType2().equalsIgnoreCase(param);
    }

    public boolean checkStackType(Effect param) {
        return checkStackType(param.getStackType()) || checkStackType(param.getStackType2());
    }

    public int getStackOrder() {
        return getTemplate()._stackOrder;
    }

    public Skill getSkill() {
        return _skill;
    }

    public Creature getEffector() {
        return _effector;
    }

    public Creature getEffected() {
        return _effected;
    }

    public double calc() {
        return _value;
    }

    public boolean isEnded() {
        return isFinished() || isFinishing();
    }

    public boolean isFinishing() {
        return getState() == FINISHING;
    }

    public boolean isFinished() {
        return getState() == FINISHED;
    }

    private int getState() {
        return _state.get();
    }

    private boolean setState(int oldState, int newState) {
        return _state.compareAndSet(oldState, newState);
    }

	private ActionDispelListener _listener;

	private class ActionDispelListener implements OnAttackListener, OnMagicUseListener
	{
		@Override
		public void onMagicUse(Creature actor, Skill skill, Creature target, boolean alt)
		{
			checkAndDispelEffect(skill);
		}

		@Override
		public void onAttack(Creature actor, Creature target)
		{
			checkAndDispelEffect(null);
		}
	}

    protected void checkAndDispelEffect(Skill skill) {
        if (skill == null || !skill.isHandler() || !skill.altUse()) {
            exit();
        }
    }

    public boolean checkCondition() {
        return true;
    }

    /**
     * Notify started
     */
    protected void onStart() {
        getEffected().addStatFuncs(getStatFuncs());
        getEffected().addTriggers(getTemplate());
        if (getTemplate()._abnormalEffects != null) {
            for (AbnormalEffect e : getTemplate()._abnormalEffects) {
                getEffected().startAbnormalEffect(e);
            }
        }
        if (_template._cancelOnAction) {
            getEffected().addListener(_listener = new ActionDispelListener());
        }
        if ((getEffected().isPlayer()) && (!getSkill().canUseTeleport())) {
            getEffected().getPlayer().setIsTeleportBlocked(true);
        }
    }

    /**
     * Return true for continuation of this effect
     */
    protected abstract boolean onActionTime();

    /**
     * Cancel the effect in the the abnormal effect map of the effected
     * L2Character.<BR><BR>
     */
    protected void onExit() {
        getEffected().removeStatsByOwner(this);
        getEffected().removeTriggers(getTemplate());
        if (getTemplate()._abnormalEffects != null) {
            for (AbnormalEffect e : getTemplate()._abnormalEffects) {
                getEffected().stopAbnormalEffect(e);
            }
        }
        if (_template._cancelOnAction) {
            getEffected().removeListener(_listener);
        }
        if ((getEffected().isPlayer()) && (getStackType().equals(EffectTemplate.HP_RECOVER_CAST))) {
            getEffected().sendPacket(new ShortBuffStatusUpdate());
        }
        if ((getEffected().isPlayer()) && (!getSkill().canUseTeleport()) && (getEffected().getPlayer().isTeleportBlocked())) {
            getEffected().getPlayer().setIsTeleportBlocked(false);
        }

        if (Config.AUTOREBUFF_ENABLED) {
            if (getSkill() == null) {
                return;
            }
            if (getEffected() instanceof Player) {
                BBSBuffer BB = ((Player) getEffected())._bbsbuffer;
                if (BB == null) {
                    return;
                }
                if (!BB.isRebuffPlayerTaskExecute() && BB.getRebuffPlayerSchemeId() > 0 && BB.isSkillInSchemeExists(getSkill().getId(), true)) {
                    BB.startRebuffPlayerTaskSchedule();
                }
            } else if (getEffected() instanceof Summon) {
                BBSBuffer BB = getEffected().getPlayer()._bbsbuffer;
                if (BB == null) {
                    return;
                }
                if (!BB.isRebuffSummonTaskExecute() && BB.getRebuffSummonSchemeId() > 0 && BB.isSkillInSchemeExists(getSkill().getId(), false)) {
                    BB.startRebuffSummonTaskSchedule();
                }
            }
        }
    }

    private void stopEffectTask() {
        if (_effectTask != null) {
            _effectTask.cancel(false);
        }
    }

    private void startEffectTask() {
        if (_effectTask == null) {
            _startTimeMillis = System.currentTimeMillis();
            _effectTask = EffectTaskManager.getInstance().scheduleAtFixedRate(this, _period, _period);
        }
    }

    /**
     * Добавляет эффект в список эффектов, в случае успешности вызывается метод
     * start
     */
    public final void schedule() {
        Creature effected = getEffected();
        if (effected == null) {
            return;
        }

        if (!checkCondition()) {
            return;
        }

        getEffected().getEffectList().addEffect(this);
    }

    /**
     * Переводит эффект в "фоновый" режим, эффект может быть запущен методом
     * schedule
     */
    private final void suspend() {
        // Эффект создан, запускаем задачу в фоне
        if (setState(STARTING, SUSPENDED)) {
            startEffectTask();
        } else if (setState(STARTED, SUSPENDED) || setState(ACTING, SUSPENDED)) {
            synchronized (this) {
                if (isInUse()) {
                    setInUse(false);
                    setActive(false);
                    onExit();
                }
            }
            getEffected().getEffectList().removeEffect(this);
        }
    }

    /**
     * Запускает задачу эффекта, в случае если эффект успешно добавлен в список
     */
    public final void start() {
        if (setState(STARTING, STARTED)) {
            synchronized (this) {
                if (isInUse()) {
                    setActive(true);
                    onStart();
                    startEffectTask();
                }
            }
        }

        run();
    }

    @Override
    public final void runImpl() throws Exception {
        if (setState(STARTED, ACTING)) {
            // Отображать сообщение только для первого эффекта скилла
            if (!getSkill().isHideStartMessage() && getEffected().getEffectList().getEffectsCountForSkill(getSkill().getId()) == 1) {
                getEffected().sendPacket(new SystemMessage2(SystemMsg.S1S_EFFECT_CAN_BE_FELT).addSkillName(_displayId, _displayLevel));
            }

            return;
        }

        if (getState() == SUSPENDED) {
            if (isTimeLeft()) {
                _count--;
                if (isTimeLeft()) {
                    return;
                }
            }

            exit();
            return;
        }

        if (getState() == ACTING) {
            if (isTimeLeft()) {
                _count--;
                if ((!isActive() || onActionTime()) && isTimeLeft()) {
                    return;
                }
            }
        }

        if (setState(ACTING, FINISHING)) {
            setInUse(false);
        }

        if (setState(FINISHING, FINISHED)) {
            synchronized (this) {
                setActive(false);
                stopEffectTask();
                onExit();
            }

            // Добавляем следующий запланированный эффект
            Effect next = getNext();
            if (next != null) {
                if (next.setState(SUSPENDED, STARTING)) {
                    next.schedule();
                }
            }

            boolean msg = !isHidden() && getEffected().getEffectList().getEffectsCountForSkill(getSkill().getId()) == 1;

            getEffected().getEffectList().removeEffect(this);

            if (getSkill().getDelayedEffect() > 0) {
                SkillTable.getInstance().getInfo(getSkill().getDelayedEffect(), getSkill().getDelayedEffectLevel()).getEffects(_effector, _effected, false, false);
            }
            // Отображать сообщение только для последнего оставшегося эффекта скилла
            if (msg) {
                getEffected().sendPacket(new SystemMessage2(SystemMsg.S1_HAS_WORN_OFF).addSkillName(_displayId, _displayLevel));
            }
        }
    }

    /**
     * Завершает эффект и все связанные, удаляет эффект из списка эффектов
     */
	public final void exit()
	{
		exit(true);
	}

	public final void exit(boolean removeNext)
	{
        //Эффект запланирован на запуск, удаляем
		if(setState(STARTING, FINISHED))
			getEffected().getEffectList().removeEffect(this);
		//Эффект работает в "фоне", останавливаем задачу в планировщике
		else if(setState(SUSPENDED, FINISHED))
			stopEffectTask();
		else if(setState(STARTED, FINISHED) || setState(ACTING, FINISHED))
		{
			synchronized (this)
			{
				if(isInUse())
				{
					setInUse(false);
					setActive(false);
					stopEffectTask();
					onExit();
				}
			}
			getEffected().getEffectList().removeEffect(this);
		}
		
		Effect next = getNext();
		if(next != null)
		{
			if (removeNext)
				next.exit();
			else if(next.setState(SUSPENDED, STARTING))
				next.schedule();

			removeNext();
		}
	}

    /**
     * Поставить в очередь эффект
     *
     * @param e
     * @return true, если эффект поставлен в очередь
     */
    private boolean scheduleNext(Effect e) {
        if (e == null || e.isEnded()) {
            return false;
        }

        Effect next = getNext();
        if (next != null && !next.maybeScheduleNext(e)) {
            return false;
        }

        _next = e;

        return true;
    }

    public Effect getNext() {
        return _next;
    }

    private void removeNext() {
        _next = null;
    }

    /**
     * @return false - игнорировать новый эффект, true - использовать новый
     * эффект
     */
    public boolean maybeScheduleNext(Effect newEffect) {
        if (newEffect.getStackOrder() < getStackOrder()) // новый эффект слабее
        {
            /*if (newEffect.getTimeLeft() > getTimeLeft()) // новый эффект длинее
            {
                newEffect.suspend();
                scheduleNext(newEffect); // пробуем пристроить новый эффект в очередь
            }*/

            return false; // более слабый эффект всегда игнорируется, даже если не попал в очередь
        } else // если старый не дольше, то просто остановить его
        if (newEffect.getTimeLeft() >= getTimeLeft()) {
            // наследуем зашедуленый старому, если есть смысл
            if (getNext() != null && getNext().getTimeLeft() > newEffect.getTimeLeft()) {
                newEffect.scheduleNext(getNext());
                // отсоединяем зашедуленные от текущего
                removeNext();
            }
            exit();
        } else if (newEffect.getSkill().isAbnormalInstant()) // если новый короче то зашедулить старый
        {
            suspend();
            newEffect.scheduleNext(this);
        }
else
			exit();

        return true;
    }

    public Func[] getStatFuncs() {
        return getTemplate().getStatFuncs(this);
    }

    public void addIcon(AbnormalStatusUpdate mi) {
        if (!isActive() || isHidden()) {
            return;
        }
        int duration = _skill.isToggle() ? AbnormalStatusUpdate.INFINITIVE_EFFECT : getTimeLeft();
        mi.addEffect(_displayId, _displayLevel, duration);
    }

    public void addPartySpelledIcon(PartySpelled ps) {
        if(!isActive() || isHidden() || _skill.isToggle() || _skill.isHandler())
			return;
        //int duration = _skill.isToggle() ? AbnormalStatusUpdate.INFINITIVE_EFFECT : getTimeLeft();
        ps.addPartySpelledEffect(_displayId, _displayLevel, getTimeLeft());
    }

    public void addOlympiadSpelledIcon(Player player, ExOlympiadSpelledInfo os) {
        if (!isActive() || isHidden()) {
            return;
        }
        int duration = _skill.isToggle() ? AbnormalStatusUpdate.INFINITIVE_EFFECT : getTimeLeft();
        os.addSpellRecivedPlayer(player);
        os.addEffect(_displayId, _displayLevel, duration);
    }

    protected int getLevel() {
        return _skill.getLevel();
    }

    public EffectType getEffectType() {
        return getTemplate()._effectType;
    }

    public boolean isHidden() {
        return _displayId < 0;
    }

    @Override
    public int compareTo(Effect obj) {
        if (obj.equals(this)) {
            return 0;
        }
        return 1;
    }

    public boolean isSaveable() {
        return _template.isSaveable(getSkill().isSaveable()) && getTimeLeft() >= Config.ALT_SAVE_EFFECTS_REMAINING_TIME;
    }

    public int getDisplayId() {
        return _displayId;
    }

    public int getDisplayLevel() {
        return _displayLevel;
    }

    public boolean isCancelable() {
        return _template.isCancelable(getSkill().isCancelable());
    }

    @Override
    public String toString() {
        return "Skill: " + _skill + ", state: " + getState() + ", inUse: " + _inUse + ", active : " + _active;
    }

    @Override
    public boolean isFuncEnabled() {
        return isInUse();
    }

    @Override
    public boolean overrideLimits() {
        return false;
    }

    public boolean isOffensive() {
        return _template.isOffensive(getSkill().isOffensive());
    }
public boolean refreshHpOnAdd()
	{
		return _template.refreshHpOnAdd();
	}

	public boolean refreshMpOnAdd()
	{
		return _template.refreshMpOnAdd();
	}

	public boolean refreshCpOnAdd()
	{
		return _template.refreshCpOnAdd();
	}
}

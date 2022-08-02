package l2p.gameserver.model;

import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.set.hash.TIntHashSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import l2p.gameserver.Config;
import l2p.gameserver.skills.EffectType;
import l2p.gameserver.skills.effects.EffectTemplate;
import l2p.gameserver.skills.skillclasses.Transformation;
import l2p.gameserver.utils.HerbEffectsComparator;
import org.apache.commons.lang3.ArrayUtils;

public class EffectList {

    public static final int NONE_SLOT_TYPE = -1;
    public static final int BUFF_SLOT_TYPE = 0;
    public static final int MUSIC_SLOT_TYPE = 1;
    public static final int TRIGGER_SLOT_TYPE = 2;
    public static final int DEBUFF_SLOT_TYPE = 3;
    public static final int DEBUFF_LIMIT = 8;
    public static final int MUSIC_LIMIT = 12;
    public static final int TRIGGER_LIMIT = 12;
    private Creature _actor;
    private List<Effect> _effects;
	private Lock _lock = new ReentrantLock();

    public EffectList(Creature owner) {
        _actor = owner;
    }

	void lock()
	{
		_lock.lock();
	}
	
	void unlock()
	{
		_lock.unlock();
	}
    /**
     * Возвращает число эффектов соответствующее данному скиллу
     */
    public int getEffectsCountForSkill(int skill_id) {
        if (isEmpty()) {
            return 0;
        }

        int count = 0;

        for (Effect e : _effects) {
            if (e.getSkill().getId() == skill_id) {
                count++;
            }
        }

        return count;
    }
	
	/**
	 * Возвращает количество музыкальных эффектов кроме skillId у которых оставшееся время больше 30 секунд 
	 * @param skillId
	 * @return
	 */
	public int getActiveMusicCount(int skillId)
	{
		if(isEmpty())
			return 0;

		int count = 0;

		for(Effect e : _effects)
			if(e.getSkill().isMusic() && e.getSkill().getId() != skillId && e.getTimeLeft() > Config.ALT_MUSIC_COST_GUARD_INTERVAL)
				count++;

		return count;
	}

    public Effect getEffectByType(EffectType et) {
        if (isEmpty()) {
            return null;
        }

        for (Effect e : _effects) {
            if (e.getEffectType() == et) {
                return e;
            }
        }

        return null;
    }

    public List<Effect> getEffectsBySkill(Skill skill) {
        if (skill == null) {
            return null;
        }
        return getEffectsBySkillId(skill.getId());
    }

    public List<Effect> getEffectsBySkillId(int skillId) {
        if (isEmpty()) {
            return null;
        }

        List<Effect> list = new ArrayList<Effect>(2);
        for (Effect e : _effects) {
            if (e.getSkill().getId() == skillId) {
                list.add(e);
            }
        }

        return list.isEmpty() ? null : list;
    }

    public Effect getEffectByIndexAndType(int skillId, EffectType type) {
        if (isEmpty()) {
            return null;
        }
        for (Effect e : _effects) {
            if (e.getSkill().getId() == skillId && e.getEffectType() == type) {
                return e;
            }
        }

        return null;
    }

    public Effect getEffectByStackType(String type) {
        if (isEmpty()) {
            return null;
        }
        for (Effect e : _effects) {
            if (e.getStackType().equals(type)) {
                return e;
            }
        }

        return null;
    }
	
	public boolean containEffectFromSkills(int skillId)
	{
		if(isEmpty())
			return false;

		int sId;
		for(Effect e : _effects)
		{
			sId = e.getSkill().getId();
			if(skillId == sId)
				return true;
		}

		return false;
	}

    public boolean containEffectFromSkills(int[] skillIds) {
        if (isEmpty()) {
            return false;
        }

        int skillId;
        for (Effect e : _effects) {
            skillId = e.getSkill().getId();
            if (ArrayUtils.contains(skillIds, skillId)) {
                return true;
            }
        }

        return false;
    }

    public List<Effect> getAllEffects()
	{
		if(_effects == null)
			return Collections.emptyList();
		return _effects;
	}

    public boolean isEmpty() {
        return _effects == null || _effects.isEmpty();
    }

    /**
     * Возвращает первые эффекты для всех скиллов. Нужно для отображения не
     * более чем 1 иконки для каждого скилла.
     */
    public Effect[] getAllFirstEffects() {
        if (isEmpty()) {
            return Effect.EMPTY_L2EFFECT_ARRAY;
        }

        TIntObjectHashMap<Effect> map = new TIntObjectHashMap<Effect>();

        for (Effect e : _effects) {
            map.put(e.getSkill().getId(), e);
        }

        return map.values(new Effect[map.size()]);
    }

	private boolean checkSlotLimit(Effect newEffect, int count)
	{
		final int slotType = getSlotType(newEffect);

		final int limit;
        switch (slotType) {
            case BUFF_SLOT_TYPE:
                limit = _actor.getBuffLimit();
                break;
            case MUSIC_SLOT_TYPE:
                limit = Config.ALT_MUSIC_LIMIT;
                break;
            case DEBUFF_SLOT_TYPE:
                limit = Config.ALT_DEBUFF_LIMIT;
                break;
            case TRIGGER_SLOT_TYPE:
                limit = Config.ALT_TRIGGER_LIMIT;
                break;
			default:
				return true;
        }

        if (_effects.size() < limit) // количество эффектов меньше лимита, нет смысла их перебирать
			return true;

        Effect head = null; // эффект который будет вытеснен
		TIntHashSet skillIds = new TIntHashSet(limit + 1);
		for(Effect e : _effects)
			if(e.isInUse() && !e.isHidden())
			{
				if(e.getSkill().equals(newEffect.getSkill())) // уже есть эффект от этого скилла
					return true;

				if(slotType == getSlotType(e))
				{
					skillIds.add(e.getSkill().getId());
					if (head == null || HerbEffectsComparator.getInstance().compare(head, e) > 0)
						head = e;
				}
			}

		if(skillIds.size() >= limit) // хербы не накладываются если нет места и первый эффект не херб
		{
			if (head == null) // на всякий случай - нечего вытеснять
				return false;

			if (newEffect.getSkill().isAbnormalInstant() && !head.getSkill().isAbnormalInstant())
				return false;

			stopEffect(head.getSkill().getId());
		}
		if (skillIds.size() > limit) // для обхода непонятной ситуации когда в листе все же оказывается больше эффектов чем положено
			return count > 3 ? false : checkSlotLimit(newEffect, count + 1);

		return true;
	}

    public static int getSlotType(Effect e) {
        if (e.isHidden() || e.getSkill().isPassive() || e.getSkill().isToggle() || e.getSkill() instanceof Transformation || e.getStackType().equals(EffectTemplate.HP_RECOVER_CAST) || e.getEffectType() == EffectType.Cubic) {
            return NONE_SLOT_TYPE;
        } else if (e.getSkill().isOffensive() || e.isOffensive()) {
            return DEBUFF_SLOT_TYPE;
        } else if (e.getSkill().isMusic()) {
            return MUSIC_SLOT_TYPE;
        } else if (e.getSkill().isTrigger()) {
            return TRIGGER_SLOT_TYPE;
        } else {
            return BUFF_SLOT_TYPE;
        }
    }

    public static boolean checkStackType(EffectTemplate ef1, EffectTemplate ef2) {
        if (!ef1._stackType.equals(EffectTemplate.NO_STACK) && ef1._stackType.equalsIgnoreCase(ef2._stackType)) {
            return true;
        }
        if (!ef1._stackType.equals(EffectTemplate.NO_STACK) && ef1._stackType.equalsIgnoreCase(ef2._stackType2)) {
            return true;
        }
        if (!ef1._stackType2.equals(EffectTemplate.NO_STACK) && ef1._stackType2.equalsIgnoreCase(ef2._stackType)) {
            return true;
        }
        if (!ef1._stackType2.equals(EffectTemplate.NO_STACK) && ef1._stackType2.equalsIgnoreCase(ef2._stackType2)) {
            return true;
        }
        return false;
    }

    public void addEffect(Effect effect) {
        //TODO [G1ta0] затычка на статы повышающие HP/MP/CP
		final double hp = _actor.getCurrentHp();
		final double mp = _actor.getCurrentMp();
		final double cp = _actor.getCurrentCp();

		String stackType = effect.getStackType();

		lock();
        try {
            if (_effects == null) {
                _effects = new CopyOnWriteArrayList<Effect>();
            }

            if (stackType.equals(EffectTemplate.NO_STACK)) // Удаляем такие же эффекты
            {
                for (Effect e : _effects) {
                    if (!e.isInUse()) {
                        continue;
                    }

                    if (e.getStackType().equals(EffectTemplate.NO_STACK) && e.getSkill().getId() == effect.getSkill().getId() && e.getEffectType() == effect.getEffectType()) // Если оставшаяся длительность старого эффекта больше чем длительность нового, то оставляем старый.
                    {
                        if (effect.getTimeLeft() > e.getTimeLeft()) {
                            e.exit();
                        } else {
                            return;
                        }
                    }
                }
            } else // Проверяем, нужно ли накладывать эффект, при совпадении StackType.
            // Новый эффект накладывается только в том случае, если у него больше StackOrder и больше длительность.
            // Если условия подходят - удаляем старый.
            {
                for (Effect e : _effects) {
                    if (!e.isInUse()) {
                        continue;
                    }

                    if (!checkStackType(e.getTemplate(), effect.getTemplate())) {
                        continue;
                    }

                    if (e.getSkill().getId() == effect.getSkill().getId() && e.getEffectType() != effect.getEffectType()) {
                        break;
                    }

                    // Эффекты со StackOrder == -1 заменить нельзя (например, Root).
                    if (e.getStackOrder() == -1) {
                        return;
                    }

                    if (!e.maybeScheduleNext(effect)) {
                        return;
                    }
                }
            }

            // Проверяем на лимиты бафов/дебафов
			if (!checkSlotLimit(effect, 0))
				return;

            // Добавляем новый эффект
			if(!_effects.add(effect))
				return;
			
			effect.setInUse(true);
		}
		finally
		{
			unlock();
        }


        // Запускаем эффект
        effect.start();

        //TODO [G1ta0] затычка на статы повышающие HP/MP/CP
		if (effect.refreshHpOnAdd())
			_actor.setCurrentHp(hp, false);
		if (effect.refreshMpOnAdd())
			_actor.setCurrentMp(mp);
		if (effect.refreshCpOnAdd())
			_actor.setCurrentCp(cp);

        // Обновляем иконки
        _actor.updateStats();
        _actor.updateEffectIcons();
    }

    /**
     * Удаление эффекта из списка
     *
     * @param effect эффект для удаления
     */
	public void removeEffect(Effect effect)
	{
		if(effect == null)
			return;

		lock();
		try
		{
			if(_effects == null)
				return;

			if(!_effects.remove(effect))
				return;
		}
		finally
		{
			unlock();
		}

		_actor.updateStats();
		_actor.updateEffectIcons();
	}

	public void stopAllEffects()
	{
		if(isEmpty())
			return;

		for(Effect e : _effects)
			e.exit();

		_actor.updateStats();
		_actor.updateEffectIcons();
	}

    public void stopEffect(int skillId) {
        if (isEmpty()) {
            return;
        }

        for (Effect e : _effects) {
            if (e.getSkill().getId() == skillId) {
                e.exit();
            }
        }
    }

    public void stopEffect(Skill skill) {
        if (skill != null) {
            stopEffect(skill.getId());
        }
    }

    public void stopEffectByDisplayId(int skillId) {
        if (isEmpty()) {
            return;
        }

        for (Effect e : _effects) {
            if (e.getSkill().getDisplayId() == skillId) {
                e.exit();
            }
        }
    }

    public void stopEffects(EffectType type) {
        if (isEmpty()) {
            return;
        }

        for (Effect e : _effects) {
            if (e.getEffectType() == type) {
                e.exit();
            }
        }
    }

    /**
     * Находит скиллы с указанным эффектом, и останавливает у этих скиллов все
     * эффекты (не только указанный).
     */
    public void stopAllSkillEffects(EffectType type) {
        if (isEmpty()) {
            return;
        }

        TIntHashSet skillIds = new TIntHashSet();

        for (Effect e : _effects) {
            if (e.getEffectType() == type) {
                skillIds.add(e.getSkill().getId());
            }
        }

        for (int skillId : skillIds.toArray()) {
            stopEffect(skillId);
        }
    }
}
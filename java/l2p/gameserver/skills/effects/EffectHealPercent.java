package l2p.gameserver.skills.effects;

import l2p.gameserver.model.Effect;
import l2p.gameserver.serverpackets.SystemMessage;
import l2p.gameserver.serverpackets.components.SystemMsg;
import l2p.gameserver.stats.Env;
import l2p.gameserver.stats.Stats;
import org.apache.commons.lang3.ArrayUtils;

public class EffectHealPercent extends Effect {

    private final boolean _ignoreHpEff;
    private final String[] _stackTypes;
    private final int _stackOrder;

    public EffectHealPercent(Env env, EffectTemplate template) {
        super(env, template);
        _ignoreHpEff = template.getParam().getBool("ignoreHpEff", true);
        _stackTypes = template.getParam().getString("StackTypes", "").split(";");
        _stackOrder = template.getParam().getInteger("StackOrder", 0);
    }

    @Override
    public boolean checkCondition() {
        if (_effected.isHealBlocked() || _effected.isDoor()) {
            return false;
        }
        if (_effector.isPlayable() && _effected.isMonster()) {
            return false;
        }
        for (Effect e : _effected.getEffectList().getAllEffects()) {
            if ((ArrayUtils.contains(_stackTypes, e.getStackType()) || ArrayUtils.contains(_stackTypes, e.getStackType2())) && e.getStackOrder() > _stackOrder) {
                return false;
            }
        }
        return super.checkCondition();
    }

    @Override
    public void onStart() {
        super.onStart();

        if (_effected.isHealBlocked()) {
            return;
        }
        if (_effected.isDead()) {
            return;
        }

        double hp = calc() * _effected.getMaxHp() / 100.;
        double newHp = hp * (!_ignoreHpEff ? _effected.calcStat(Stats.HEAL_EFFECTIVNESS, 100., _effector, getSkill()) : 100.) / 100.;
        double addToHp = Math.max(0, Math.min(newHp, _effected.calcStat(Stats.HP_LIMIT, null, null) * _effected.getMaxHp() / 100. - _effected.getCurrentHp()));

        _effected.sendPacket(new SystemMessage(SystemMsg.S1_HP_HAS_BEEN_RESTORED).addNumber(Math.round(addToHp)));

        if (addToHp > 0) {
            _effected.setCurrentHp(addToHp + _effected.getCurrentHp(), false);
        }
    }

    @Override
    public boolean onActionTime() {
        return false;
    }
}

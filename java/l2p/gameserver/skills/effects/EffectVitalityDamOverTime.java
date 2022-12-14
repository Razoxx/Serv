package l2p.gameserver.skills.effects;

import l2p.gameserver.cache.Msg;
import l2p.gameserver.model.Effect;
import l2p.gameserver.model.Player;
import l2p.gameserver.serverpackets.SystemMessage;
import l2p.gameserver.stats.Env;

public class EffectVitalityDamOverTime extends Effect {

    public EffectVitalityDamOverTime(Env env, EffectTemplate template) {
        super(env, template);
    }

    public boolean onActionTime() {
        if (_effected.isDead() || _effected.isPlayer()) {
            return false;
        }
        Player _pEffected = (Player) _effected;

        double vitDam = calc();
        if (vitDam > _pEffected.getVitality() && getSkill().isToggle()) {
            _pEffected.sendPacket(Msg.NOT_ENOUGH_MATERIALS);
            _pEffected.sendPacket(new SystemMessage(749).addSkillName(getSkill().getId(), getSkill().getDisplayLevel()));
            return false;
        }

        _pEffected.setVitality(Math.max(0.0D, _pEffected.getVitality() - vitDam));
        return true;
    }
}
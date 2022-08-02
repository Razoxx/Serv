package l2p.gameserver.skills.effects;

import l2p.gameserver.model.Effect;
import l2p.gameserver.stats.Env;
import l2p.gameserver.serverpackets.SystemMessage;

public final class EffectParalyze extends Effect {

    private final boolean _postEffectOnly;

    public EffectParalyze(Env env, EffectTemplate template) {
        super(env, template);
        _postEffectOnly = template.getParam().getBool("postEffectOnly", false);
    }

    @Override
    public boolean checkCondition() {
        if (_effected.isParalyzeImmune()) {
            return false;
        }
        if (_effector.getPet() != null && _effected == _effector.getPet()) {
            _effector.getPlayer().sendPacket(new SystemMessage(SystemMessage.THAT_IS_THE_INCORRECT_TARGET));
            return false;
        }

        return super.checkCondition();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!_postEffectOnly) {
            _effected.startParalyzed();
            _effected.abortAttack(true, true);
            _effected.abortCast(true, true);
            if (_effected.isMoving) {
                _effected.stopMove();
            }
        }
    }

    @Override
    public void onExit() {
        super.onExit();
        if (!_postEffectOnly) {
            _effected.stopParalyzed();
        }
    }

    @Override
    public boolean onActionTime() {
        return false;
    }
}

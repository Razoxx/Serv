package l2p.gameserver.skills.effects;

import l2p.gameserver.ai.CtrlIntention;
import l2p.gameserver.ai.DefaultAI;
import l2p.gameserver.model.Creature;
import l2p.gameserver.model.Effect;
import l2p.gameserver.stats.Env;

public final class EffectRemoveTarget extends Effect {

    private boolean _doStopTarget;

    public EffectRemoveTarget(Env env, EffectTemplate template) {
        super(env, template);
        _doStopTarget = template.getParam().getBool("doStopTarget", true);
    }

    @Override
    public void onStart() {

        Creature target = getEffected();
        if (target.getTarget() == null) // атака/каст прерывается только если есть таргет
        {
            return;
        }

        if (target.getAI() instanceof DefaultAI) {
            ((DefaultAI) target.getAI()).setGlobalAggro(System.currentTimeMillis() + 3000L);
        }

        target.setTarget(null);
        target.abortCast(true, true);
        if (_doStopTarget) {
            target.stopMove();
        }
        target.abortAttack(true, true);
        target.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE, getEffector());
    }

    @Override
    public boolean isHidden() {
        return true;
    }

    @Override
    public boolean onActionTime() {
        return false;
    }
}

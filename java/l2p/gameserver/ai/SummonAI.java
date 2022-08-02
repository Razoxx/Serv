package l2p.gameserver.ai;

import l2p.gameserver.Config;
import l2p.gameserver.model.Creature;
import l2p.gameserver.model.Skill;
import l2p.gameserver.model.Summon;

public class SummonAI extends PlayableAI {

    private CtrlIntention _storedIntention = null;
    private Object _storedIntentionArg0 = null;
    private Object _storedIntentionArg1 = null;
    private boolean _storedForceUse = false;

    public SummonAI(Summon actor) {
        super(actor);
    }

    public void clearStoredIntention() {
        _storedIntention = null;
        _storedIntentionArg0 = null;
        _storedIntentionArg1 = null;
    }

    public void storeIntention() {
        if (_storedIntention == null) {
            _storedIntention = getIntention();
            _storedIntentionArg0 = _intention_arg0;
            _storedIntentionArg1 = _intention_arg1;
            _storedForceUse = _forceUse;
        }
    }

    public boolean restoreIntention() {
        CtrlIntention intention = _storedIntention;
        Object arg0 = _storedIntentionArg0;
        Object arg1 = _storedIntentionArg1;
        if (intention != null) {
            _forceUse = _storedForceUse;
            setIntention(intention, arg0, arg1);
            clearStoredIntention();

            onEvtThink();
            return true;
        }
        return false;
    }

    @Override
    protected void onIntentionIdle() {
        clearStoredIntention();
        super.onIntentionIdle();
    }

    @Override
    protected void onEvtFinishCasting(Skill skill) {
        if (!restoreIntention()) {
            super.onEvtFinishCasting(skill);
        }
    }

    @Override
    protected void thinkActive() {
        Summon actor = getActor();

        clearNextAction();
        if (actor.isDepressed()) {
            setAttackTarget(actor.getPlayer());
            changeIntention(CtrlIntention.AI_INTENTION_ATTACK, actor.getPlayer(), null);
            thinkAttack(true);
        } else if (actor.isFollowMode()) {
            changeIntention(CtrlIntention.AI_INTENTION_FOLLOW, actor.getPlayer(), Config.FOLLOW_RANGE);
            thinkFollow();
        }

        super.thinkActive();
    }

    @Override
    protected void thinkAttack(boolean checkRange) {
        Summon actor = getActor();

        if (actor.isDepressed()) {
            setAttackTarget(actor.getPlayer());
        }

        super.thinkAttack(checkRange);
    }

    @Override
    protected void onEvtAttacked(Creature attacker, int damage) {
        Summon actor = getActor();
        if (attacker != null && actor.getPlayer().isDead() && !actor.isDepressed()) {
            Attack(attacker, false, false);
        }
        super.onEvtAttacked(attacker, damage);
    }

    @Override
    public void Cast(Skill skill, Creature target, boolean forceUse, boolean dontMove) {
        storeIntention();
        super.Cast(skill, target, forceUse, dontMove);
    }

    @Override
    public Summon getActor() {
        return (Summon) super.getActor();
    }
}

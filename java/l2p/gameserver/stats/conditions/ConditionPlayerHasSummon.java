package l2p.gameserver.stats.conditions;

import l2p.gameserver.stats.Env;

public class ConditionPlayerHasSummon
        extends Condition {

    private boolean _value;

    public ConditionPlayerHasSummon(boolean value) {
        _value = value;
    }

    @Override
    protected boolean testImpl(Env env) {
        if (!env.character.isPlayer()) {
            return false;
        }
        return (env.character.getPet() != null) == _value;
    }
}

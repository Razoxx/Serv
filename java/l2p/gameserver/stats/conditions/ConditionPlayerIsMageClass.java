package l2p.gameserver.stats.conditions;

import l2p.gameserver.stats.Env;

public class ConditionPlayerIsMageClass
        extends Condition {

    private final boolean _value;

    public ConditionPlayerIsMageClass(boolean value) {
        _value = value;
    }

    @Override
    protected boolean testImpl(Env env) {
        return env.character.isMageClass() == _value;
    }
}

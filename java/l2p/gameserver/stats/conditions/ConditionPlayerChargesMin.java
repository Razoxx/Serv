package l2p.gameserver.stats.conditions;

import l2p.gameserver.model.Player;
import l2p.gameserver.stats.Env;

public class ConditionPlayerChargesMin
        extends Condition {

    private final int _minCharges;

    public ConditionPlayerChargesMin(int minCharges) {
        _minCharges = minCharges;
    }

    @Override
    protected boolean testImpl(Env env) {
        if ((env.character == null) || (!env.character.isPlayer())) {
            return false;
        }
        return ((Player) env.character).getIncreasedForce() >= _minCharges;
    }
}

package l2p.gameserver.stats.conditions;

import l2p.gameserver.model.Player;
import l2p.gameserver.serverpackets.components.SystemMsg;
import l2p.gameserver.stats.Env;

public class ConditionPlayerChargesMax
        extends Condition {

    private final int _maxCharges;

    public ConditionPlayerChargesMax(int maxCharges) {
        _maxCharges = maxCharges;
    }

    @Override
    protected boolean testImpl(Env env) {
        if ((env.character == null) || (!env.character.isPlayer())) {
            return false;
        }
        if (((Player) env.character).getIncreasedForce() >= _maxCharges) {
            env.character.sendPacket(SystemMsg.YOUR_FORCE_HAS_REACHED_MAXIMUM_CAPACITY);
            return false;
        }
        return true;
    }
}

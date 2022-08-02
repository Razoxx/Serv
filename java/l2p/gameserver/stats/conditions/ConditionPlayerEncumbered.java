package l2p.gameserver.stats.conditions;

import l2p.gameserver.model.Player;
import l2p.gameserver.serverpackets.components.SystemMsg;
import l2p.gameserver.stats.Env;

public class ConditionPlayerEncumbered
        extends Condition {

    private final int _maxWeightPercent;
    private final int _maxLoadPercent;

    public ConditionPlayerEncumbered(int remainingWeightPercent, int remainingLoadPercent) {
        this._maxWeightPercent = (100 - remainingWeightPercent);
        this._maxLoadPercent = (100 - remainingLoadPercent);
    }

    @Override
    protected boolean testImpl(Env env) {
        if ((env.character == null) || (!env.character.isPlayer())) {
            return false;
        }
        if ((((Player) env.character).getWeightPercents() >= this._maxWeightPercent) || (((Player) env.character).getUsedInventoryPercents() >= this._maxLoadPercent)) {
            env.character.sendPacket(SystemMsg.YOUR_INVENTORY_IS_FULL);
            return false;
        }
        return true;
    }
}

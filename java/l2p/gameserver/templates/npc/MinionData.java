package l2p.gameserver.templates.npc;

import l2p.commons.util.Rnd;

public class MinionData {

    /**
     * The Identifier of the L2Minion
     */
    private final int _minionId;
    /**
     * The number of this Minion Type to spawn
     */
    private final int _minionAmountMin;
    private final int _minionAmountMax;

    public MinionData(int minionId, int minionAmountMin, int minionAmountMax) {
        _minionId = minionId;
        _minionAmountMin = minionAmountMin;
        _minionAmountMax = minionAmountMax;
    }

    /**
     * Return the Identifier of the Minion to spawn.<BR><BR>
     * @return 
     */
    public int getMinionId() {
        return _minionId;
    }

    /**
     * Return the amount of this Minion type to spawn.<BR><BR>
     * @return 
     */
    public int getAmount() {
        return Rnd.get(_minionAmountMin, _minionAmountMax);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (o.getClass() != this.getClass()) {
            return false;
        }
        return ((MinionData) o).getMinionId() == this.getMinionId();
    }

    @Override
    public int hashCode() {
        return _minionId;
    }
}
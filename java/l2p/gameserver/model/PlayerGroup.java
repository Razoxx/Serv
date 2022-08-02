package l2p.gameserver.model;

import java.util.Iterator;

import l2p.commons.collections.EmptyIterator;
import l2p.gameserver.serverpackets.components.IStaticPacket;

public interface PlayerGroup extends Iterable<Player> {

    public static final PlayerGroup EMPTY = new PlayerGroup() {
        @Override
        public void broadCast(IStaticPacket... packet) {
        }

        @Override
        public int getMemberCount() {
            return 0;
        }

        @Override
        public Player getGroupLeader() {
            return null;
        }

        @Override
        public Iterator<Player> iterator() {
            return EmptyIterator.getInstance();
        }
    };

    public abstract void broadCast(IStaticPacket... paramVarArgs);

    public abstract int getMemberCount();

    public abstract Player getGroupLeader();
}

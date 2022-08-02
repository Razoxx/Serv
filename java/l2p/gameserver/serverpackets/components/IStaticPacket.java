package l2p.gameserver.serverpackets.components;

import l2p.gameserver.model.Player;
import l2p.gameserver.serverpackets.L2GameServerPacket;

public interface IStaticPacket {

    L2GameServerPacket packet(Player player);
}

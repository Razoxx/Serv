package l2p.gameserver.serverpackets;

import l2p.gameserver.model.Summon;

public class PetStatusShow extends L2GameServerPacket {

    private int _summonType;

    public PetStatusShow(Summon summon) {
        _summonType = summon.getSummonType();
    }

    @Override
    protected final void writeImpl() {
        writeC(0xb1);
        writeD(_summonType);
    }
}
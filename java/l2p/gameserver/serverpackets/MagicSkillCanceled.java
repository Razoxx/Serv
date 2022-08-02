package l2p.gameserver.serverpackets;

import l2p.gameserver.model.Creature;
import l2p.gameserver.model.Player;

public class MagicSkillCanceled extends L2GameServerPacket {

    private final int _casterId;
    private final int _casterX;
    private final int _casterY;

    public MagicSkillCanceled(Creature caster) {
        _casterId = caster.getObjectId();
        _casterX = caster.getX();
        _casterY = caster.getY();
    }

    @Override
    protected final void writeImpl() {
        writeC(0x49);
        writeD(_casterId);
    }

    @Override
    public L2GameServerPacket packet(Player player) {
        if (player != null) {
            if (player.buffAnimRange() < 0) {
                return null;
            }
            if (player.buffAnimRange() == 0) {
                return _casterId == player.getObjectId() ? super.packet(player) : null;
            }
            Creature observer = player.getObservePoint();
            if (observer == null) {
                observer = player;
            }
            return observer.getDistance(_casterX, _casterY) < player.buffAnimRange() ? super.packet(player) : null;
        }
        return super.packet(player);
    }
}

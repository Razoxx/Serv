package l2p.gameserver.serverpackets;

import java.util.Collection;
import java.util.Collections;
import l2p.gameserver.model.Creature;
import l2p.gameserver.model.Player;

public class MagicSkillLaunched extends L2GameServerPacket {

    private final int _casterId;
    private final int _casterX;
    private final int _casterY;
    private final int _skillId;
    private final int _skillLevel;
    private final Collection<Creature> _targets;

    public MagicSkillLaunched(Creature caster, int skillId, int skillLevel, Creature target) {
        _casterId = caster.getObjectId();
        _casterX = caster.getX();
        _casterY = caster.getY();
        _skillId = skillId;
        _skillLevel = skillLevel;
        _targets = Collections.singletonList(target);
    }

    public MagicSkillLaunched(Creature caster, int skillId, int skillLevel, Collection<Creature> targets) {
        _casterId = caster.getObjectId();
        _casterX = caster.getX();
        _casterY = caster.getY();
        _skillId = skillId;
        _skillLevel = skillLevel;
        _targets = targets;
    }

    @Override
    protected final void writeImpl() {
        writeC(0x54);
        writeD(_casterId);
        writeD(_skillId);
        writeD(_skillLevel);
        writeD(_targets.size());
        for (Creature target : _targets) {
            if (target != null) {
                writeD(target.getObjectId());
            }
        }
    }

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

package l2p.gameserver.serverpackets;

import l2p.gameserver.model.Creature;
import l2p.gameserver.model.Player;

/**
 * Format: dddddddddh [h] h [ddd] Пример пакета: 48 86 99 00 4F 86 99 00 4F EF
 * 08 00 00 01 00 00 00 00 00 00 00 00 00 00 00 F9 B5 FF FF 7D E0 01 00 68 F3 FF
 * FF 00 00 00 00
 */
public class MagicSkillUse extends L2GameServerPacket {

    private int _targetId;
    private int _skillId;
    private int _skillLevel;
    private int _hitTime;
    private int _reuseDelay;
    private int _casterId, _casterX, _casterY, _casterZ, _targetX, _targetY, _targetZ;

    public MagicSkillUse(Creature cha, Creature target, int skillId, int skillLevel, int hitTime, long reuseDelay) {
        _casterId = cha.getObjectId();
        _targetId = target.getObjectId();
        _skillId = skillId;
        _skillLevel = skillLevel;
        _hitTime = hitTime;
        _reuseDelay = (int) reuseDelay;
        _casterX = cha.getX();
        _casterY = cha.getY();
        _casterZ = cha.getZ();
        _targetX = target.getX();
        _targetY = target.getY();
        _targetZ = target.getZ();
    }

    public MagicSkillUse(Creature cha, int skillId, int skillLevel, int hitTime, long reuseDelay) {
        _casterId = cha.getObjectId();
        _targetId = cha.getTargetId();
        _skillId = skillId;
        _skillLevel = skillLevel;
        _hitTime = hitTime;
        _reuseDelay = (int) reuseDelay;
        _casterX = cha.getX();
        _casterY = cha.getY();
        _casterZ = cha.getZ();
        _targetX = cha.getX();
        _targetY = cha.getY();
        _targetZ = cha.getZ();
    }

    @Override
    protected final void writeImpl() {
        writeC(0x48);
        writeD(_casterId);
        writeD(_targetId);
        writeD(_skillId);
        writeD(_skillLevel);
        writeD(_hitTime);
        writeD(_reuseDelay);
        writeD(_casterX);
        writeD(_casterY);
        writeD(_casterZ);
        writeD(0x00); // unknown
        writeD(_targetX);
        writeD(_targetY);
        writeD(_targetZ);
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

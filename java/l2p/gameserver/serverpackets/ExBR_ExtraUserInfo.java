package l2p.gameserver.serverpackets;

import l2p.gameserver.model.Player;
import l2p.gameserver.skills.AbnormalEffectType;

public class ExBR_ExtraUserInfo extends L2GameServerPacket {

    private int _objectId;
    private int _effect3;
    private int _lectureMark;

    public ExBR_ExtraUserInfo(Player cha) {
        _objectId = cha.getObjectId();
        _effect3 = cha.getAbnormalEffect(AbnormalEffectType.BRANCH);
        _lectureMark = cha.getLectureMark();
    }

    @Override
    protected void writeImpl() {
        writeEx(0xDA);
        writeD(_objectId); //object id of player
        writeD(_effect3); // event effect id
        writeC(_lectureMark);
    }
}
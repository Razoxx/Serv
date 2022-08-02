package l2p.gameserver.clientpackets;

import l2p.commons.collections.CollectionUtils;
import l2p.gameserver.model.Player;
import l2p.gameserver.model.Skill;
import l2p.gameserver.model.actor.instances.player.TpBookMark;
import l2p.gameserver.network.GameClient;
import l2p.gameserver.tables.SkillTable;

public class RequestTeleportBookMark extends L2GameClientPacket {

    private static final Skill SKILL = SkillTable.getInstance().getInfo(2588, 1);
    private int _slot;

    @Override
    protected void readImpl() {
        _slot = readD();
    }

    @Override
    protected void runImpl() {
        Player activeChar = ((GameClient) getClient()).getActiveChar();
        if (activeChar == null) {
            return;
        }
        if ((activeChar.isActionsDisabled()) || (activeChar.isTeleportBlocked())) {
            activeChar.sendActionFailed();
            return;
        }
        TpBookMark bookMark = (TpBookMark) CollectionUtils.safeGet(activeChar.getTpBookMarks(), _slot - 1);
        if (bookMark == null) {
            return;
        }
        activeChar.getVars().set("teleport_bookmark", bookMark);
        activeChar.getAI().Cast(SKILL, activeChar);
    }
}

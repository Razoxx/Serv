package l2p.gameserver.clientpackets;

import l2p.gameserver.ai.CtrlEvent;
import l2p.gameserver.model.ObservePoint;
import l2p.gameserver.model.Player;
import l2p.gameserver.utils.Location;

public class CannotMoveAnymore extends L2GameClientPacket {

    private Location _loc = new Location();

    @Override
    protected void readImpl() {
        _loc.x = readD();
        _loc.y = readD();
        _loc.z = readD();
        _loc.h = readD();
    }

    @Override
    protected void runImpl() {
        Player activeChar = getClient().getActiveChar();
        if (activeChar == null) {
            return;
        }

        if (activeChar.isInObserverMode()) {
            ObservePoint observer = activeChar.getObservePoint();
            if (observer != null) {
                observer.stopMove();
            }
            return;
        }
        if (!activeChar.isOutOfControl()) {
            activeChar.getAI().notifyEvent(CtrlEvent.EVT_ARRIVED_BLOCKED, _loc, null);
        }
    }
}

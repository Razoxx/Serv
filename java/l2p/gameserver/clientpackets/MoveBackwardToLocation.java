package l2p.gameserver.clientpackets;

import l2p.gameserver.Config;
import l2p.gameserver.model.ObservePoint;
import l2p.gameserver.model.Player;
import l2p.gameserver.serverpackets.ActionFail;
import l2p.gameserver.serverpackets.components.SystemMsg;
import l2p.gameserver.utils.BotPunish;
import l2p.gameserver.utils.Location;

// cdddddd(d)
public class MoveBackwardToLocation extends L2GameClientPacket {

    private Location _targetLoc = new Location();
    private Location _originLoc = new Location();
    private int _moveMovement;

    /**
     * packet type id 0x0f
     */
    @Override
    protected void readImpl() {
        _targetLoc.x = readD();
        _targetLoc.y = readD();
        _targetLoc.z = readD();
        _originLoc.x = readD();
        _originLoc.y = readD();
        _originLoc.z = readD();
        if (_buf.hasRemaining()) {
            _moveMovement = readD();
        }
    }

    @Override
    protected void runImpl() {
        Player activeChar = getClient().getActiveChar();
        if (activeChar == null) {
            return;
        }

        activeChar.setActive();

        if (System.currentTimeMillis() - activeChar.getLastMovePacket() < Config.MOVE_PACKET_DELAY) {
            activeChar.sendActionFailed();
            return;
        }

        activeChar.setLastMovePacket();

        // Bot punishment restriction
        if (Config.ALT_ENABLE_BOTREPORT && activeChar.isPlayer()) {
            if (activeChar.isBeingPunished()) {
                if (activeChar.getPlayerPunish().canWalk() && activeChar.getPlayerPunish().getBotPunishType() == BotPunish.Punish.MOVEBAN) {
                    activeChar.endPunishment();
                } else if (activeChar.getPlayerPunish().getBotPunishType() == BotPunish.Punish.MOVEBAN) {
                    activeChar.sendPacket(SystemMsg.REPORTED_120_MINS_WITHOUT_MOVE);
                    return;
                }
            }
        }

        if (activeChar.isTeleporting()) {
            activeChar.sendActionFailed();
            return;
        }

        if (activeChar.isFrozen()) {
            activeChar.sendPacket(SystemMsg.YOU_CANNOT_MOVE_WHILE_FROZEN, ActionFail.STATIC);
            return;
        }

        if (activeChar.isInObserverMode()) {
            ObservePoint observer = activeChar.getObservePoint();
            if (observer != null) {
                observer.moveToLocation(_targetLoc, 0, false);
            }
            return;
        }

        if (activeChar.isOutOfControl()) {
            activeChar.sendActionFailed();
            return;
        }

        if (activeChar.getTeleMode() > 0) {
            if (activeChar.getTeleMode() == 1) {
                activeChar.setTeleMode(0);
            }
            activeChar.sendActionFailed();
            activeChar.teleToLocation(_targetLoc);
            return;
        }

        if (activeChar.isInFlyingTransform()) {
            _targetLoc.z = Math.min(5950, Math.max(50, _targetLoc.z)); // В летающей трансформе нельзя летать ниже, чем 0, и выше, чем 6000
        }
        activeChar.moveToLocation(_targetLoc, 0, _moveMovement != 0 && !activeChar.getVarB("no_pf"));
    }

    public static class StartMoveTask implements Runnable {

        private Player _player;
        private Location _loc;
        private boolean _pathfind;

        public StartMoveTask(Player player, Location loc, boolean pathfind) {
            _player = player;
            _loc = loc;
            _pathfind = pathfind;
        }

        public void run() {
            _player.moveToLocation(_loc, 0, _pathfind && !_player.getVarB("no_pf"));
        }
    }
}

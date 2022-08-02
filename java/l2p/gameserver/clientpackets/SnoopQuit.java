package l2p.gameserver.clientpackets;

import l2p.gameserver.model.Player;
import l2p.gameserver.model.GameObjectsStorage;

public class SnoopQuit extends L2GameClientPacket {

    @SuppressWarnings("unused")
    private int _snoopID;

    /**
     * format: cd
     */
    @Override
    protected void readImpl() {
        _snoopID = readD();
    }

    @Override
    protected void runImpl() {
        Player player = (Player) GameObjectsStorage.findObject(_snoopID);
        if (player == null) {
            return;
        }
        Player activeChar = getClient().getActiveChar();
        if (activeChar == null) {
            return;
        }
        player.removeSnooper(activeChar);
        activeChar.removeSnooped(player);
    }
}
package l2p.gameserver.clientpackets;

import l2p.gameserver.Config;
import l2p.gameserver.model.Player;
import l2p.gameserver.model.items.ItemInstance;
import l2p.gameserver.serverpackets.components.CustomMessage;
import l2p.gameserver.serverpackets.components.SystemMsg;
import l2p.gameserver.utils.Location;

public class RequestDropItem extends L2GameClientPacket {

    private int _objectId;
    private long _count;
    private Location _loc;

    @Override
    protected void readImpl() {
        _objectId = readD();
        _count = readQ();
        _loc = new Location(readD(), readD(), readD());
    }

    @Override
    protected void runImpl() {
        Player activeChar = getClient().getActiveChar();
        if (activeChar == null) {
            return;
        }

        if (_count < 1 || _loc.isNull()) {
            activeChar.sendActionFailed();
            return;
        }

        if (activeChar.isActionsDisabled()) {
            activeChar.sendActionFailed();
            return;
        }

        if (!Config.ALLOW_DISCARDITEM) {
            activeChar.sendMessage(new CustomMessage("l2p.gameserver.clientpackets.RequestDropItem.Disallowed", activeChar));
            return;
        }

        if (activeChar.isInStoreMode()) {
            activeChar.sendPacket(SystemMsg.WHILE_OPERATING_A_PRIVATE_STORE_OR_WORKSHOP_YOU_CANNOT_DISCARD_DESTROY_OR_TRADE_AN_ITEM);
            return;
        }

        if (activeChar.isSitting() || activeChar.isDropDisabled()) {
            activeChar.sendActionFailed();
            return;
        }

        if (activeChar.isInTrade()) {
            activeChar.sendActionFailed();
            return;
        }

        if (activeChar.isFishing()) {
            activeChar.sendPacket(SystemMsg.YOU_CANNOT_DO_THAT_WHILE_FISHING);
            return;
        }

        if (!activeChar.isInRangeSq(_loc, 22500) || Math.abs(_loc.z - activeChar.getZ()) > 50) {
            activeChar.sendPacket(SystemMsg.YOU_CANNOT_DISCARD_SOMETHING_THAT_FAR_AWAY_FROM_YOU);
            return;
        }

        ItemInstance item = activeChar.getInventory().getItemByObjectId(_objectId);
        if (item == null) {
            activeChar.sendActionFailed();
            return;
        }

        if (activeChar._event != null && activeChar._event.getName().equalsIgnoreCase("Capture The Flag") && (item.getItemId() == 13560 || item.getItemId() == 13561)) {
            activeChar.sendMessage(new CustomMessage("l2p.gameserver.network.l2.c2s.UseItem.NotUseIsEvents", activeChar));
            return;
        }

        if (activeChar.getPet() != null && activeChar.getPet().getControlItemObjId() == item.getObjectId()) {
            activeChar.sendPacket(SystemMsg.THE_PET_HAS_BEEN_SUMMONED_AND_CANNOT_BE_DELETED);
            return;
        }

        if (!item.canBeDropped(activeChar, false)) {
            activeChar.sendPacket(SystemMsg.THAT_ITEM_CANNOT_BE_DISCARDED);
            return;
        }

        item.getTemplate().getHandler().dropItem(activeChar, item, _count, _loc);
    }
}
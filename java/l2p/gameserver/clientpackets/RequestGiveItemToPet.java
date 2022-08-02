package l2p.gameserver.clientpackets;

import l2p.gameserver.model.Player;
import l2p.gameserver.model.instances.PetInstance;
import l2p.gameserver.model.items.ItemInstance;
import l2p.gameserver.model.items.PcInventory;
import l2p.gameserver.model.items.PetInventory;
import l2p.gameserver.serverpackets.components.SystemMsg;

public class RequestGiveItemToPet extends L2GameClientPacket {

    private int _objectId;
    private long _amount;

    @Override
    protected void readImpl() {
        _objectId = readD();
        _amount = readQ();
    }

    @Override
    protected void runImpl() {
        Player activeChar = getClient().getActiveChar();
        if (activeChar == null || _amount < 1) {
            return;
        }

        PetInstance pet = (PetInstance) activeChar.getPet();
        if (pet == null) {
            activeChar.sendActionFailed();
            return;
        }

        if (activeChar.isOutOfControl()) {
            activeChar.sendActionFailed();
            return;
        }

        if (activeChar.isInStoreMode()) {
            activeChar.sendPacket(SystemMsg.WHILE_OPERATING_A_PRIVATE_STORE_OR_WORKSHOP_YOU_CANNOT_DISCARD_DESTROY_OR_TRADE_AN_ITEM);
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

        if (pet.isDead()) {
            activeChar.sendPacket(SystemMsg.YOUR_PET_IS_DEAD_AND_ANY_ATTEMPT_YOU_MAKE_TO_GIVE_IT_SOMETHING_GOES_UNRECOGNIZED);
            return;
        }

        if (_objectId == pet.getControlItemObjId()) {
            activeChar.sendActionFailed();
            return;
        }

        
        if (activeChar.isInStoreMode()) {
            activeChar.sendActionFailed();
            return;
        }

        PetInventory petInventory = pet.getInventory();
        PcInventory playerInventory = activeChar.getInventory();

        ItemInstance item = playerInventory.getItemByObjectId(_objectId);
        if (item == null || item.getCount() < _amount || !item.canBeDropped(activeChar, false) || !item.canBeTraded(activeChar)) {
            activeChar.sendActionFailed();
            return;
        }

        if (item.isHeroWeapon()) {
            activeChar.sendMessage(activeChar.isLangRus() ? "Питомец не может переносить оружие героя!" : "The pet can not carry weapons hero!");
            activeChar.sendActionFailed();
            return;
        }

        int slots = 0;
        long weight = item.getTemplate().getWeight() * _amount;
        if (!item.getTemplate().isStackable() || pet.getInventory().getItemByItemId(item.getItemId()) == null) {
            slots = 1;
        }

        if (!pet.getInventory().validateWeight(weight)) {
            activeChar.sendPacket(SystemMsg.YOUR_PET_CANNOT_CARRY_ANY_MORE_ITEMS_);
            return;
        }

        if (!pet.getInventory().validateCapacity(slots)) {
            activeChar.sendPacket(SystemMsg.YOUR_PET_CANNOT_CARRY_ANY_MORE_ITEMS);
            return;
        }

        petInventory.addItem(playerInventory.removeItemByObjectId(_objectId, _amount));

        pet.sendChanges();
        activeChar.sendChanges();
    }
}
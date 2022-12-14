package l2p.gameserver.clientpackets;

import l2p.commons.dao.JdbcEntityState;
import l2p.commons.util.Rnd;
import l2p.gameserver.Config;
import l2p.gameserver.model.Player;
import l2p.gameserver.model.base.Element;
import l2p.gameserver.model.items.ItemInstance;
import l2p.gameserver.model.items.PcInventory;
import l2p.gameserver.serverpackets.components.SystemMsg;
import l2p.gameserver.serverpackets.ActionFail;
import l2p.gameserver.serverpackets.ExAttributeEnchantResult;
import l2p.gameserver.serverpackets.InventoryUpdate;
import l2p.gameserver.serverpackets.SystemMessage2;
import l2p.gameserver.templates.item.ItemTemplate;
import l2p.gameserver.utils.ItemFunctions;

public class RequestEnchantItemAttribute extends L2GameClientPacket {

    private int _objectId;

    @Override
    protected void readImpl() {
        _objectId = readD();
    }

    @Override
    protected void runImpl() {
        Player activeChar = getClient().getActiveChar();
        if (activeChar == null) {
            return;
        }

        if (_objectId == -1) {
            activeChar.setEnchantScroll(null);
            activeChar.sendPacket(SystemMsg.ELEMENTAL_POWER_ENCHANCER_USAGE_HAS_BEEN_CANCELLED);
            return;
        }

        if (activeChar.isActionsDisabled()) {
            activeChar.sendActionFailed();
            return;
        }

        if (activeChar.isInStoreMode()) {
            activeChar.sendPacket(SystemMsg.YOU_CANNOT_ADD_ELEMENTAL_POWER_WHILE_OPERATING_A_PRIVATE_STORE_OR_PRIVATE_WORKSHOP, ActionFail.STATIC);
            return;
        }

        if (activeChar.isInTrade()) {
            activeChar.sendActionFailed();
            return;
        }

        PcInventory inventory = activeChar.getInventory();
        ItemInstance itemToEnchant = inventory.getItemByObjectId(_objectId);
        ItemInstance stone = activeChar.getEnchantScroll();
        activeChar.setEnchantScroll(null);

        if (itemToEnchant == null || stone == null) {
            activeChar.sendActionFailed();
            return;
        }

        ItemTemplate item = itemToEnchant.getTemplate();

        if (!itemToEnchant.canBeEnchanted(true) || item.getCrystalType().cry < ItemTemplate.CRYSTAL_S) {
            activeChar.sendPacket(SystemMsg.INAPPROPRIATE_ENCHANT_CONDITIONS, ActionFail.STATIC);
            return;
        }

        if (itemToEnchant.getLocation() != ItemInstance.ItemLocation.INVENTORY && itemToEnchant.getLocation() != ItemInstance.ItemLocation.PAPERDOLL) {
            activeChar.sendPacket(SystemMsg.INAPPROPRIATE_ENCHANT_CONDITIONS, ActionFail.STATIC);
            return;
        }

        if (itemToEnchant.isStackable() || (stone = inventory.getItemByObjectId(stone.getObjectId())) == null) {
            activeChar.sendActionFailed();
            return;
        }

        Element element = ItemFunctions.getEnchantAttributeStoneElement(stone.getItemId(), itemToEnchant.isArmor());

        if (itemToEnchant.isArmor()) {
            if (itemToEnchant.getAttributeElementValue(Element.getReverseElement(element), false) != 0) {
                activeChar.sendPacket(SystemMsg.ANOTHER_ELEMENTAL_POWER_HAS_ALREADY_BEEN_ADDED_THIS_ELEMENTAL_POWER_CANNOT_BE_ADDED, ActionFail.STATIC);
                return;
            }
        } else if (itemToEnchant.isWeapon()) {
            if (itemToEnchant.getAttributeElement() != Element.NONE && itemToEnchant.getAttributeElement() != element) {
                activeChar.sendPacket(SystemMsg.ANOTHER_ELEMENTAL_POWER_HAS_ALREADY_BEEN_ADDED_THIS_ELEMENTAL_POWER_CANNOT_BE_ADDED, ActionFail.STATIC);
                return;
            }
        } else {
            activeChar.sendPacket(SystemMsg.INAPPROPRIATE_ENCHANT_CONDITIONS, ActionFail.STATIC);
            return;
        }

        if (item.isUnderwear() || item.isCloak() || item.isBracelet() || item.isBelt() || !item.isAttributable()) {
            activeChar.sendPacket(SystemMsg.INAPPROPRIATE_ENCHANT_CONDITIONS, ActionFail.STATIC);
            return;
        }

        int minValue = 0;
        int maxValue = itemToEnchant.isWeapon() ? 150 : 60;
        int maxValueCrystal = itemToEnchant.isWeapon() ? 300 : 120;

        if (!stone.getTemplate().isAttributeCrystal() && itemToEnchant.getAttributeElementValue(element, false) >= maxValue
                || stone.getTemplate().isAttributeCrystal() && (itemToEnchant.getAttributeElementValue(element, false) < maxValue
                || itemToEnchant.getAttributeElementValue(element, false) >= maxValueCrystal)) {
            activeChar.sendPacket(SystemMsg.ELEMENTAL_POWER_ENHANCER_USAGE_REQUIREMENT_IS_NOT_SUFFICIENT, ActionFail.STATIC);
            return;
        }

        // ???????????? ???? ?????????????? ?????????? ??????????, ?????? ?????????? ?????????????? ???? ?????????????????? ??????????
        if (itemToEnchant.getOwnerId() != activeChar.getObjectId()) {
            activeChar.sendPacket(SystemMsg.INAPPROPRIATE_ENCHANT_CONDITIONS, ActionFail.STATIC);
            return;
        }

        if (!inventory.destroyItem(stone, 1)) {
            activeChar.sendActionFailed();
            return;
        }

        if (Rnd.chance(stone.getTemplate().isAttributeCrystal() ? Config.ENCHANT_ATTRIBUTE_CRYSTAL_CHANCE : Config.ENCHANT_ATTRIBUTE_STONE_CHANCE)) {
            if (itemToEnchant.getEnchantLevel() == 0) {
                SystemMessage2 sm = new SystemMessage2(SystemMsg.S2_ELEMENTAL_POWER_HAS_BEEN_ADDED_SUCCESSFULLY_TO_S1);
                sm.addItemName(itemToEnchant.getItemId());
                sm.addItemName(stone.getItemId());
                activeChar.sendPacket(sm);
            } else {
                SystemMessage2 sm = new SystemMessage2(SystemMsg.S3_ELEMENTAL_POWER_HAS_BEEN_ADDED_SUCCESSFULLY_TO_S1_S2);
                sm.addInteger(itemToEnchant.getEnchantLevel());
                sm.addItemName(itemToEnchant.getItemId());
                sm.addItemName(stone.getItemId());
                activeChar.sendPacket(sm);
            }

            int value;
            if (Config.ALLOW_ALT_ATT_ENCHANT) {
                value = itemToEnchant.isWeapon() ? Config.ALT_ATT_ENCHANT_WEAPON_VALUE : Config.ALT_ATT_ENCHANT_ARMOR_VALUE;
            } else {
                value = itemToEnchant.isWeapon() ? 5 : 6;
            }

            // ?????? ???????????? 1?? ???????????? ???????? +20 ????????????????
            if (itemToEnchant.getAttributeElementValue(element, false) == 0 && itemToEnchant.isWeapon() && !Config.ALLOW_ALT_ATT_ENCHANT) {
                value = 20;
            }

            boolean equipped = false;
            if (equipped = itemToEnchant.isEquipped()) {
                activeChar.getInventory().isRefresh = true;
                activeChar.getInventory().unEquipItem(itemToEnchant);
            }

            itemToEnchant.setAttributeElement(element, itemToEnchant.getAttributeElementValue(element, false) + value);
            itemToEnchant.setJdbcState(JdbcEntityState.UPDATED);
            itemToEnchant.update();

            if (equipped) {
                activeChar.getInventory().equipItem(itemToEnchant);
                activeChar.getInventory().isRefresh = false;
            }

            activeChar.sendPacket(new InventoryUpdate().addModifiedItem(itemToEnchant));
            activeChar.sendPacket(new ExAttributeEnchantResult(value));
        } else {
            activeChar.sendPacket(SystemMsg.YOU_HAVE_FAILED_TO_ADD_ELEMENTAL_POWER);
        }

        activeChar.setEnchantScroll(null);
        activeChar.updateStats();
    }
}
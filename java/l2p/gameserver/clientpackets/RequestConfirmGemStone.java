package l2p.gameserver.clientpackets;

import l2p.gameserver.Config;
import l2p.gameserver.model.Player;
import l2p.gameserver.model.items.ItemInstance;
import l2p.gameserver.serverpackets.ExPutCommissionResultForVariationMake;
import l2p.gameserver.serverpackets.components.SystemMsg;
import l2p.gameserver.templates.item.ItemTemplate.Grade;
import l2p.gameserver.utils.ItemFunctions;
import org.apache.commons.lang3.ArrayUtils;

public class RequestConfirmGemStone extends L2GameClientPacket {

    private static final int GEMSTONE_D = 2130;
    private static final int GEMSTONE_C = 2131;
    private static final int GEMSTONE_B = 2132;
    // format: (ch)dddd
    private int _targetItemObjId;
    private int _refinerItemObjId;
    private int _gemstoneItemObjId;
    private long _gemstoneCount;

    @Override
    protected void readImpl() {
        _targetItemObjId = readD();
        _refinerItemObjId = readD();
        _gemstoneItemObjId = readD();
        _gemstoneCount = readQ();
    }

    @Override
    protected void runImpl() {
        if (_gemstoneCount <= 0) {
            return;
        }

        Player activeChar = getClient().getActiveChar();
        ItemInstance targetItem = activeChar.getInventory().getItemByObjectId(_targetItemObjId);
        ItemInstance refinerItem = activeChar.getInventory().getItemByObjectId(_refinerItemObjId);
        ItemInstance gemstoneItem = activeChar.getInventory().getItemByObjectId(_gemstoneItemObjId);

        if (targetItem == null || refinerItem == null || gemstoneItem == null) {
            activeChar.sendPacket(SystemMsg.THIS_IS_NOT_A_SUITABLE_ITEM);
            return;
        }

        int gemstoneItemId = gemstoneItem.getTemplate().getItemId();
        if (gemstoneItemId != GEMSTONE_D && gemstoneItemId != GEMSTONE_C && gemstoneItemId != GEMSTONE_B && (!ArrayUtils.contains(Config.VISUAL_FROM_AUGMENT_ID_IN, gemstoneItemId) && Config.ALLOW_VISUAL_FROM_AUGMENT)) {
            activeChar.sendPacket(SystemMsg.THIS_IS_NOT_A_SUITABLE_ITEM);
            return;
        }

        boolean isAccessoryLifeStone = ItemFunctions.isAccessoryLifeStone(refinerItem.getItemId());
        boolean isVisualLifeStone = ItemFunctions.isVisualLifeStone(refinerItem.getItemId());

        if (targetItem.isNotAugmented()) {
            activeChar.sendPacket(SystemMsg.THIS_IS_NOT_A_SUITABLE_ITEM);
            return;
        }

        if (!targetItem.canBeAugmented(activeChar, isAccessoryLifeStone)) {
            activeChar.sendPacket(SystemMsg.THIS_IS_NOT_A_SUITABLE_ITEM);
            return;
        }

        if (!isAccessoryLifeStone && !ItemFunctions.isLifeStone(refinerItem.getItemId()) && !(isVisualLifeStone && Config.ALLOW_VISUAL_FROM_AUGMENT)) {
            activeChar.sendPacket(SystemMsg.THIS_IS_NOT_A_SUITABLE_ITEM);
            return;
        }

        Grade itemGrade = targetItem.getTemplate().getItemGrade();

        if (isAccessoryLifeStone) {
            switch (itemGrade) {
                case C:
                    if (_gemstoneCount != 200 || gemstoneItemId != GEMSTONE_D) {
                        activeChar.sendPacket(SystemMsg.GEMSTONE_QUANTITY_IS_INCORRECT);
                        return;
                    }
                    break;
                case B:
                    if (_gemstoneCount != 300 || gemstoneItemId != GEMSTONE_D) {
                        activeChar.sendPacket(SystemMsg.GEMSTONE_QUANTITY_IS_INCORRECT);
                        return;
                    }
                    break;
                case A:
                    if (_gemstoneCount != 200 || gemstoneItemId != GEMSTONE_C) {
                        activeChar.sendPacket(SystemMsg.GEMSTONE_QUANTITY_IS_INCORRECT);
                        return;
                    }
                    break;
                case S:
                    if (_gemstoneCount != 250 || gemstoneItemId != GEMSTONE_C) {
                        activeChar.sendPacket(SystemMsg.GEMSTONE_QUANTITY_IS_INCORRECT);
                        return;
                    }
                    break;
                case S80:
                    if (_gemstoneCount != 250 || gemstoneItemId != GEMSTONE_B) {
                        activeChar.sendPacket(SystemMsg.GEMSTONE_QUANTITY_IS_INCORRECT);
                        return;
                    }
                    break;
                case S84:
                    if (_gemstoneCount != 250 || gemstoneItemId != GEMSTONE_B) {
                        activeChar.sendPacket(SystemMsg.GEMSTONE_QUANTITY_IS_INCORRECT);
                        return;
                    }
                    break;
            }
        } else if (isVisualLifeStone && Config.ALLOW_VISUAL_FROM_AUGMENT) {
            switch (itemGrade) {
                case C:
                    if (_gemstoneCount != Config.VISUAL_FROM_AUGMENT_PRICES_IN[0] || gemstoneItemId != Config.VISUAL_FROM_AUGMENT_ID_IN[0]) {
                        activeChar.sendPacket(SystemMsg.GEMSTONE_QUANTITY_IS_INCORRECT);
                        return;
                    }
                    break;
                case B:
                    if (_gemstoneCount != Config.VISUAL_FROM_AUGMENT_PRICES_IN[1] || gemstoneItemId != Config.VISUAL_FROM_AUGMENT_ID_IN[1]) {
                        activeChar.sendPacket(SystemMsg.GEMSTONE_QUANTITY_IS_INCORRECT);
                        return;
                    }
                    break;
                case A:
                    if (_gemstoneCount != Config.VISUAL_FROM_AUGMENT_PRICES_IN[2] || gemstoneItemId != Config.VISUAL_FROM_AUGMENT_ID_IN[2]) {
                        activeChar.sendPacket(SystemMsg.GEMSTONE_QUANTITY_IS_INCORRECT);
                        return;
                    }
                    break;
                case S:
                    if (_gemstoneCount != Config.VISUAL_FROM_AUGMENT_PRICES_IN[3] || gemstoneItemId != Config.VISUAL_FROM_AUGMENT_ID_IN[3]) {
                        activeChar.sendPacket(SystemMsg.GEMSTONE_QUANTITY_IS_INCORRECT);
                        return;
                    }
                    break;
                case S80:
                    if (_gemstoneCount != Config.VISUAL_FROM_AUGMENT_PRICES_IN[4] || gemstoneItemId != Config.VISUAL_FROM_AUGMENT_ID_IN[4]) {
                        activeChar.sendPacket(SystemMsg.GEMSTONE_QUANTITY_IS_INCORRECT);
                        return;
                    }
                    break;
                case S84:
                    if (_gemstoneCount != Config.VISUAL_FROM_AUGMENT_PRICES_IN[5] || gemstoneItemId != Config.VISUAL_FROM_AUGMENT_ID_IN[5]) {
                        activeChar.sendPacket(SystemMsg.GEMSTONE_QUANTITY_IS_INCORRECT);
                        return;
                    }
                    break;
            }
        } else {
            switch (itemGrade) {
                case C:
                    if (_gemstoneCount != 20 || gemstoneItemId != GEMSTONE_D) {
                        activeChar.sendPacket(SystemMsg.GEMSTONE_QUANTITY_IS_INCORRECT);
                        return;
                    }
                    break;
                case B:
                    if (_gemstoneCount != 30 || gemstoneItemId != GEMSTONE_D) {
                        activeChar.sendPacket(SystemMsg.GEMSTONE_QUANTITY_IS_INCORRECT);
                        return;
                    }
                    break;
                case A:
                    if (_gemstoneCount != 20 || gemstoneItemId != GEMSTONE_C) {
                        activeChar.sendPacket(SystemMsg.GEMSTONE_QUANTITY_IS_INCORRECT);
                        return;
                    }
                    break;
                case S:
                    if (_gemstoneCount != 25 || gemstoneItemId != GEMSTONE_C) {
                        activeChar.sendPacket(SystemMsg.GEMSTONE_QUANTITY_IS_INCORRECT);
                        return;
                    }
                    break;
                case S80:
                    // Icarus
                    if (targetItem.getTemplate().getCrystalCount() == 10394 && (_gemstoneCount != 36 || gemstoneItemId != GEMSTONE_B)) {
                        activeChar.sendPacket(SystemMsg.GEMSTONE_QUANTITY_IS_INCORRECT);
                        return;
                    } // Dynasty
                    else if (_gemstoneCount != 28 || gemstoneItemId != GEMSTONE_B) {
                        activeChar.sendPacket(SystemMsg.GEMSTONE_QUANTITY_IS_INCORRECT);
                        return;
                    }
                    break;
                case S84:
                    if (_gemstoneCount != 36 || gemstoneItemId != GEMSTONE_B) {
                        activeChar.sendPacket(SystemMsg.GEMSTONE_QUANTITY_IS_INCORRECT);
                        return;
                    }
                    break;
            }
        }

        activeChar.sendPacket(new ExPutCommissionResultForVariationMake(_gemstoneItemObjId, _gemstoneCount), SystemMsg.PRESS_THE_AUGMENT_BUTTON_TO_BEGIN);
    }
}

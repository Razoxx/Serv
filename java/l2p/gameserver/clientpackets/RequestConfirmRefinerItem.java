package l2p.gameserver.clientpackets;

import l2p.gameserver.Config;
import l2p.gameserver.model.Player;
import l2p.gameserver.model.items.ItemInstance;
import l2p.gameserver.serverpackets.ExPutIntensiveResultForVariationMake;
import l2p.gameserver.serverpackets.SystemMessage2;
import l2p.gameserver.serverpackets.components.SystemMsg;
import l2p.gameserver.templates.item.ItemTemplate.Grade;
import l2p.gameserver.utils.ItemFunctions;

public class RequestConfirmRefinerItem extends L2GameClientPacket {

    private static final int GEMSTONE_D = 2130;
    private static final int GEMSTONE_C = 2131;
    private static final int GEMSTONE_B = 2132;
    // format: (ch)dd
    private int _targetItemObjId;
    private int _refinerItemObjId;

    @Override
    protected void readImpl() {
        _targetItemObjId = readD();
        _refinerItemObjId = readD();
    }

    @Override
    protected void runImpl() {
        Player activeChar = getClient().getActiveChar();
        if (activeChar == null) {
            return;
        }

        ItemInstance targetItem = activeChar.getInventory().getItemByObjectId(_targetItemObjId);
        ItemInstance refinerItem = activeChar.getInventory().getItemByObjectId(_refinerItemObjId);

        if (targetItem == null || refinerItem == null) {
            activeChar.sendPacket(SystemMsg.THIS_IS_NOT_A_SUITABLE_ITEM);
            return;
        }

        int refinerItemId = refinerItem.getTemplate().getItemId();

        boolean isAccessoryLifeStone = ItemFunctions.isAccessoryLifeStone(refinerItem.getItemId());
        boolean isVisualLifeStone = ItemFunctions.isVisualLifeStone(refinerItem.getItemId());
        
        boolean isVisualLifeStoneArmor = ItemFunctions.isVisualLifeStoneArmor(refinerItem.getItemId());
        boolean isVisualLifeStoneWeapon = ItemFunctions.isVisualLifeStoneWeapon(refinerItem.getItemId());

        

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
        if (targetItem.isVisualItem() && isVisualLifeStone && Config.ALLOW_VISUAL_FROM_AUGMENT){
            activeChar.sendPacket(SystemMsg.THIS_IS_NOT_A_SUITABLE_ITEM);
            return;
        }
        
        if (Config.ALLOW_VISUAL_FROM_AUGMENT) {
            if ((targetItem.isWeapon() && isVisualLifeStoneArmor) || (targetItem.isArmor() && isVisualLifeStoneWeapon)) {
                activeChar.sendPacket(SystemMsg.THIS_IS_NOT_A_SUITABLE_ITEM);
                return;
            }
        }

        Grade itemGrade = targetItem.getTemplate().getItemGrade();

        int gemstoneCount = 0;
        int gemstoneItemId = 0;

        if (isAccessoryLifeStone) {
            switch (itemGrade) {
                case C:
                    gemstoneCount = 200;
                    gemstoneItemId = GEMSTONE_D;
                    break;
                case B:
                    gemstoneCount = 300;
                    gemstoneItemId = GEMSTONE_D;
                    break;
                case A:
                    gemstoneCount = 200;
                    gemstoneItemId = GEMSTONE_C;
                    break;
                case S:
                    gemstoneCount = 250;
                    gemstoneItemId = GEMSTONE_C;
                    break;
                case S80:
                    gemstoneCount = 250;
                    gemstoneItemId = GEMSTONE_B;
                    break;
                case S84:
                    gemstoneCount = 250;
                    gemstoneItemId = GEMSTONE_B;
                    break;
            }
        } else if (isVisualLifeStone && Config.ALLOW_VISUAL_FROM_AUGMENT) {
            switch (itemGrade) {
                case C:
                    gemstoneCount = Config.VISUAL_FROM_AUGMENT_PRICES_IN[0];
                    gemstoneItemId = Config.VISUAL_FROM_AUGMENT_ID_IN[0];
                    break;
                case B:
                    gemstoneCount = Config.VISUAL_FROM_AUGMENT_PRICES_IN[1];
                    gemstoneItemId = Config.VISUAL_FROM_AUGMENT_ID_IN[1];
                    break;
                case A:
                    gemstoneCount = Config.VISUAL_FROM_AUGMENT_PRICES_IN[2];
                    gemstoneItemId = Config.VISUAL_FROM_AUGMENT_ID_IN[2];
                    break;
                case S:
                    gemstoneCount = Config.VISUAL_FROM_AUGMENT_PRICES_IN[3];
                    gemstoneItemId = Config.VISUAL_FROM_AUGMENT_ID_IN[3];
                    break;
                case S80:
                    gemstoneCount = Config.VISUAL_FROM_AUGMENT_PRICES_IN[4];
                    gemstoneItemId = Config.VISUAL_FROM_AUGMENT_ID_IN[4];
                    break;
                case S84:
                    gemstoneCount = Config.VISUAL_FROM_AUGMENT_PRICES_IN[5];
                    gemstoneItemId = Config.VISUAL_FROM_AUGMENT_ID_IN[5];
                    break;
            }
        }else {
            switch (itemGrade) {
                case C:
                    gemstoneCount = 20;
                    gemstoneItemId = GEMSTONE_D;
                    break;
                case B:
                    gemstoneCount = 30;
                    gemstoneItemId = GEMSTONE_D;
                    break;
                case A:
                    gemstoneCount = 20;
                    gemstoneItemId = GEMSTONE_C;
                    break;
                case S:
                    gemstoneCount = 25;
                    gemstoneItemId = GEMSTONE_C;
                    break;
                case S80:
                    if (targetItem.getTemplate().getCrystalCount() == 10394) // Icarus
                    {
                        gemstoneCount = 36;
                    } else {
                        gemstoneCount = 28;
                    }
                    gemstoneItemId = GEMSTONE_B;
                    break;
                case S84:
                    gemstoneCount = 36;
                    gemstoneItemId = GEMSTONE_B;
                    break;
            }
        }

        SystemMessage2 sm = new SystemMessage2(SystemMsg.REQUIRES_S2_S1).addInteger(gemstoneCount).addItemName(gemstoneItemId);
        activeChar.sendPacket(new ExPutIntensiveResultForVariationMake(_refinerItemObjId, refinerItemId, gemstoneItemId, gemstoneCount), sm);
    }
}
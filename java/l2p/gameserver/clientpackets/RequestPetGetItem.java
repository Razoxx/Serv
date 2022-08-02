package l2p.gameserver.clientpackets;

import l2p.gameserver.ai.CtrlIntention;
import l2p.gameserver.model.Player;
import l2p.gameserver.model.Summon;
import l2p.gameserver.model.items.ItemInstance;
import l2p.gameserver.serverpackets.components.SystemMsg;
import l2p.gameserver.serverpackets.SystemMessage2;
import l2p.gameserver.utils.ItemFunctions;

public class RequestPetGetItem extends L2GameClientPacket {
    // format: cd

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

        if (activeChar.isOutOfControl()) {
            activeChar.sendActionFailed();
            return;
        }

        Summon summon = activeChar.getPet();
        if (summon == null || !summon.isPet() || summon.isDead() || summon.isActionsDisabled()) {
            activeChar.sendActionFailed();
            return;
        }

        ItemInstance item = (ItemInstance) activeChar.getVisibleObject(_objectId);
        if (item == null) {
            activeChar.sendActionFailed();
            return;
        }

        if (item.isHeroWeapon()) {
            activeChar.sendPacket(SystemMsg.HERO_WEAPONS_CANNOT_BE_DESTROYED);
            return;
        }

        if (!ItemFunctions.checkIfCanPickup(summon, item)) {
            SystemMessage2 sm;
            if (item.getItemId() == 57) {
                sm = new SystemMessage2(SystemMsg.YOU_HAVE_FAILED_TO_PICK_UP_S1_ADENA);
                sm.addInteger((int) item.getCount());
            } else {
                sm = new SystemMessage2(SystemMsg.YOU_HAVE_FAILED_TO_PICK_UP_S1);
                sm.addItemName(item.getItemId());
            }
            sendPacket(sm);
            activeChar.sendActionFailed();
            return;
        }

        summon.getAI().setIntention(CtrlIntention.AI_INTENTION_PICK_UP, item, null);
    }
}
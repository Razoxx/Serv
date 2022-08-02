package l2p.gameserver.clientpackets;

import l2p.gameserver.Config;
import l2p.gameserver.dao.CharacterTPBookmarkDAO;
import l2p.gameserver.instancemanager.ReflectionManager;
import l2p.gameserver.model.Player;
import l2p.gameserver.model.actor.instances.player.TpBookMark;
import l2p.gameserver.network.GameClient;
import l2p.gameserver.serverpackets.ExGetBookMarkInfo;
import l2p.gameserver.serverpackets.components.SystemMsg;

public class RequestSaveBookMarkSlot extends L2GameClientPacket {

    private String _name;
    private String _acronym;
    private int _icon;

    @Override
    protected void readImpl() {
        _name = readS(32);
        _icon = readD();
        _acronym = readS(4);
    }

    @Override
    protected void runImpl() {
        Player player = ((GameClient) getClient()).getActiveChar();
        if (player == null) {
            return;
        }
        if (player.getTpBookMarks().size() >= player.getTpBookmarkSize()) {
            player.sendPacket(SystemMsg.YOUR_NUMBER_OF_MY_TELEPORTS_SLOTS_HAS_REACHED_ITS_MAXIMUM_LIMIT);
            return;
        }
        if ((player.isActionBlocked("save_bookmark")) || (player.isInBoat())) {
            player.sendPacket(SystemMsg.YOU_CANNOT_USE_MY_TELEPORTS_IN_THIS_AREA);
            return;
        }
        if (player.getActiveWeaponFlagAttachment() != null) {
            player.sendPacket(SystemMsg.YOU_CANNOT_TELEPORT_WHILE_IN_POSSESSION_OF_A_WARD);
            return;
        }
        if (player.isInOlympiadMode()) {
            player.sendPacket(SystemMsg.YOU_CANNOT_USE_MY_TELEPORTS_WHILE_PARTICIPATING_IN_AN_OLYMPIAD_MATCH);
            return;
        }
        if (player.getReflection() != ReflectionManager.DEFAULT) {
            player.sendPacket(SystemMsg.YOU_CANNOT_USE_MY_TELEPORTS_IN_AN_INSTANT_ZONE);
            return;
        }
        if (player.isInDuel()) {
            player.sendPacket(SystemMsg.YOU_CANNOT_USE_MY_TELEPORTS_DURING_A_DUEL);
            return;
        }
        if ((player.isInCombat()) || (player.getPvpFlag() != 0)) {
            player.sendPacket(SystemMsg.YOU_CANNOT_USE_MY_TELEPORTS_DURING_A_BATTLE);
            return;
        }
        if ((player.isOnSiegeField()) || (player.isInZoneBattle())) {
            player.sendPacket(SystemMsg.YOU_CANNOT_USE_MY_TELEPORTS_WHILE_PARTICIPATING_A_LARGESCALE_BATTLE_SUCH_AS_A_CASTLE_SIEGE_FORTRESS_SIEGE_OR_HIDEOUT_SIEGE);
            return;
        }
        if (player.isFlying()) {
            player.sendPacket(SystemMsg.YOU_CANNOT_USE_MY_TELEPORTS_WHILE_FLYING);
            return;
        }
        if (player.isInWater()) {
            player.sendPacket(SystemMsg.YOU_CANNOT_USE_MY_TELEPORTS_UNDERWATER);
            return;
        }
        TpBookMark bookMark = new TpBookMark(player.getX(), player.getY(), player.getZ(), _icon, _name, _acronym);
        if (player.getTpBookMarks().contains(bookMark)) {
            return;
        }
        if ((Config.EX_USE_TELEPORT_FLAG) && (!player.consumeItem(20033, 1L))) {
            player.sendPacket(SystemMsg.YOU_CANNOT_BOOKMARK_THIS_LOCATION_BECAUSE_YOU_DO_NOT_HAVE_A_MY_TELEPORT_FLAG);
            return;
        }
        player.getTpBookMarks().add(bookMark);
        CharacterTPBookmarkDAO.getInstance().insert(player, bookMark);
        player.sendPacket(new ExGetBookMarkInfo(player));
    }
}

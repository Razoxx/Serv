package l2p.gameserver.skills.skillclasses;

import java.util.List;
import l2p.gameserver.instancemanager.ReflectionManager;
import l2p.gameserver.model.Creature;
import l2p.gameserver.model.Player;
import l2p.gameserver.model.Skill;
import l2p.gameserver.model.actor.instances.player.TpBookMark;
import l2p.gameserver.serverpackets.components.SystemMsg;
import l2p.gameserver.templates.StatsSet;

public class BookMarkTeleport extends Skill {

    public BookMarkTeleport(StatsSet set) {
        super(set);
    }

    @Override
    public boolean checkCondition(Creature activeChar, Creature target, boolean forceUse, boolean dontMove, boolean first) {
        if ((!activeChar.isPlayer()) || (!super.checkCondition(activeChar, target, forceUse, dontMove, first))) {
            return false;
        }
        Player player = activeChar.getPlayer();

        TpBookMark loc = (TpBookMark) player.getVars().get("teleport_bookmark");
        if (loc == null) {
            //player.sendPacket(new SystemMessage(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addSkillName(skill));
            return false;
        }
        if (player.isActionBlocked("use_bookmark")) {
            player.sendPacket(SystemMsg.YOU_CANNOT_USE_MY_TELEPORTS_IN_THIS_AREA);
            return false;
        }
        if (player.getActiveWeaponFlagAttachment() != null) {
            player.sendPacket(SystemMsg.YOU_CANNOT_TELEPORT_WHILE_IN_POSSESSION_OF_A_WARD);
            return false;
        }
        if (player.isInOlympiadMode()) {
            player.sendPacket(SystemMsg.YOU_CANNOT_USE_MY_TELEPORTS_WHILE_PARTICIPATING_IN_AN_OLYMPIAD_MATCH);
            return false;
        }
        if (player.getReflection() != ReflectionManager.DEFAULT) {
            player.sendPacket(SystemMsg.YOU_CANNOT_USE_MY_TELEPORTS_IN_AN_INSTANT_ZONE);
            return false;
        }
        if (player.isInDuel()) {
            player.sendPacket(SystemMsg.YOU_CANNOT_USE_MY_TELEPORTS_DURING_A_DUEL);
            return false;
        }
        if ((player.isInCombat()) || (player.getPvpFlag() != 0)) {
            player.sendPacket(SystemMsg.YOU_CANNOT_USE_MY_TELEPORTS_DURING_A_BATTLE);
            return false;
        }
        if ((player.isOnSiegeField()) || (player.isInZoneBattle())) {
            player.sendPacket(SystemMsg.YOU_CANNOT_USE_MY_TELEPORTS_WHILE_PARTICIPATING_A_LARGESCALE_BATTLE_SUCH_AS_A_CASTLE_SIEGE_FORTRESS_SIEGE_OR_HIDEOUT_SIEGE);
            return false;
        }
        if (player.isFlying()) {
            player.sendPacket(SystemMsg.YOU_CANNOT_USE_MY_TELEPORTS_WHILE_FLYING);
            return false;
        }
        if ((player.isInWater()) || (player.isInBoat())) {
            player.sendPacket(SystemMsg.YOU_CANNOT_USE_MY_TELEPORTS_UNDERWATER);
            return false;
        }
        if ((first) && (!player.consumeItem(13016, 1L))) {
            player.sendPacket(SystemMsg.YOU_CANNOT_TELEPORT_BECAUSE_YOU_DO_NOT_HAVE_A_TELEPORT_ITEM);
            return false;
        }
        return true;
    }

    @Override
    public void useSkill(Creature activeChar, List<Creature> targets) {
        if (!activeChar.isPlayer()) {
            return;
        }
        Player player = activeChar.getPlayer();

        TpBookMark loc = (TpBookMark) player.getVars().remove("teleport_bookmark");
        if (loc == null) {
            return;
        }
        player.teleToLocation(loc, ReflectionManager.DEFAULT);
    }
}

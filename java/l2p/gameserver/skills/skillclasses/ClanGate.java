package l2p.gameserver.skills.skillclasses;

import java.util.List;

import l2p.gameserver.model.Creature;
import l2p.gameserver.model.Player;
import l2p.gameserver.model.Skill;
import l2p.gameserver.model.pledge.Clan;
import l2p.gameserver.serverpackets.components.SystemMsg;
import l2p.gameserver.serverpackets.SystemMessage2;
import l2p.gameserver.templates.StatsSet;

public class ClanGate extends Skill {

    public ClanGate(StatsSet set) {
        super(set);
    }

    @Override
    public boolean checkCondition(Creature activeChar, Creature target, boolean forceUse, boolean dontMove, boolean first) {
        if (!activeChar.isPlayer()) {
            return false;
        }

        Player player = (Player) activeChar;
        if (!player.isClanLeader()) {
            player.sendPacket(SystemMsg.ONLY_THE_CLAN_LEADER_IS_ENABLED);
            return false;
        }

        SystemMessage2 msg = Call.canSummonHere(player);
        if (msg != null) {
            activeChar.sendPacket(msg);
            return false;
        }

        return super.checkCondition(activeChar, target, forceUse, dontMove, first);
    }

    @Override
    public void useSkill(Creature activeChar, List<Creature> targets) {
        if (!activeChar.isPlayer()) {
            return;
        }

        Player player = (Player) activeChar;
        Clan clan = player.getClan();
        clan.broadcastToOtherOnlineMembers(new SystemMessage2(SystemMsg.COURT_MAGICIAN__THE_PORTAL_HAS_BEEN_CREATED), player);

        getEffects(activeChar, activeChar, true, true);
    }
}

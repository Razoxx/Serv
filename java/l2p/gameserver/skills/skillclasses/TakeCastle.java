package l2p.gameserver.skills.skillclasses;

import java.util.List;

import l2p.gameserver.model.Creature;
import l2p.gameserver.model.Player;
import l2p.gameserver.model.Skill;
import l2p.gameserver.model.Zone;
import l2p.gameserver.model.Zone.ZoneType;
import l2p.gameserver.model.entity.events.impl.CastleSiegeEvent;
import l2p.gameserver.model.instances.ArtefactInstance;
import l2p.gameserver.serverpackets.components.SystemMsg;
import l2p.gameserver.serverpackets.SystemMessage2;
import l2p.gameserver.templates.StatsSet;

public class TakeCastle extends Skill {

    public TakeCastle(StatsSet set) {
        super(set);
    }

    @Override
    public boolean checkCondition(Creature activeChar, Creature target, boolean forceUse, boolean dontMove, boolean first) {
        Zone siegeZone = target.getZone(ZoneType.SIEGE);

        if (!super.checkCondition(activeChar, target, forceUse, dontMove, first)) {
            return false;
        }

        if (activeChar == null || !activeChar.isPlayer()) {
            return false;
        }

        Player player = (Player) activeChar;
        if (player.getClan() == null || !player.isClanLeader()) {
            activeChar.sendPacket(new SystemMessage2(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addSkillName(this));
            return false;
        }

        CastleSiegeEvent siegeEvent = player.getEvent(CastleSiegeEvent.class);
        if (siegeEvent == null) {
            activeChar.sendPacket(new SystemMessage2(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addSkillName(this));
            return false;
        }

        if (siegeEvent.getSiegeClan(CastleSiegeEvent.ATTACKERS, player.getClan()) == null || siegeEvent.getResidence().getId() != siegeZone.getParams().getInteger("residence", 0)) {
            activeChar.sendPacket(new SystemMessage2(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addSkillName(this));
            return false;
        }

        if (!siegeEvent.isActiveArtefact(activeChar, (ArtefactInstance) target)) {
            player.sendMessage(player.isLangRus() ? "???? ?????? ???????????????? ???????????? ???? ???????? ????????????. ???????????????? ???????????? ???? ????????????." : "You have left a stamp on this altar. Place the seal on the second.");
            return false;
        }

        if (player.isMounted()) {
            activeChar.sendPacket(new SystemMessage2(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addSkillName(this));
            return false;
        }

        if ((!player.isInRangeZ(target, 185L)) || (player.getZDeltaSq(target.getZ()) > 2500L)) {
            player.sendPacket(SystemMsg.YOUR_TARGET_IS_OUT_OF_RANGE);
            return false;
        }

        if (first) {
            siegeEvent.broadcastTo(SystemMsg.THE_OPPOSING_CLAN_HAS_STARTED_TO_ENGRAVE_THE_HOLY_ARTIFACT, CastleSiegeEvent.DEFENDERS);
        }

        return true;
    }

    @Override
    public void useSkill(Creature activeChar, List<Creature> targets) {
        for (Creature target : targets) {
            if (target != null) {
                if (!target.isArtefact()) {
                    continue;
                }
                Player player = (Player) activeChar;

                CastleSiegeEvent siegeEvent = player.getEvent(CastleSiegeEvent.class);
                if (siegeEvent != null) {
                    siegeEvent.broadcastTo(new SystemMessage2(SystemMsg.CLAN_S1_HAS_SUCCESSFULLY_ENGRAVED_THE_HOLY_ARTIFACT).addString(player.getClan().getName()), CastleSiegeEvent.ATTACKERS, CastleSiegeEvent.DEFENDERS);
                    siegeEvent.engrave(activeChar, (ArtefactInstance) target);
                }
            }
        }
    }
}

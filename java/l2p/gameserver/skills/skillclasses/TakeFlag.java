package l2p.gameserver.skills.skillclasses;

import java.util.List;

import l2p.gameserver.data.xml.holder.EventHolder;
import l2p.gameserver.model.Creature;
import l2p.gameserver.model.Player;
import l2p.gameserver.model.Skill;
import l2p.gameserver.model.entity.events.EventType;
import l2p.gameserver.model.entity.events.impl.DominionSiegeEvent;
import l2p.gameserver.model.entity.events.impl.DominionSiegeRunnerEvent;
import l2p.gameserver.model.entity.events.objects.TerritoryWardObject;
import l2p.gameserver.model.entity.residence.Dominion;
import l2p.gameserver.model.instances.residences.SiegeFlagInstance;
import l2p.gameserver.serverpackets.components.SystemMsg;
import l2p.gameserver.serverpackets.SystemMessage2;
import l2p.gameserver.templates.StatsSet;

public class TakeFlag extends Skill {

    public TakeFlag(StatsSet set) {
        super(set);
    }

    @Override
    public boolean checkCondition(Creature activeChar, Creature target, boolean forceUse, boolean dontMove, boolean first) {
        if (!super.checkCondition(activeChar, target, forceUse, dontMove, first)) {
            return false;
        }

        if (activeChar == null || !activeChar.isPlayer()) {
            return false;
        }

        Player player = (Player) activeChar;

        if (player.getClan() == null) {
            return false;
        }

        DominionSiegeEvent siegeEvent1 = player.getEvent(DominionSiegeEvent.class);
        if (siegeEvent1 == null) {
            return false;
        }

        if (!(player.getActiveWeaponFlagAttachment() instanceof TerritoryWardObject)) {
            activeChar.sendPacket(new SystemMessage2(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addSkillName(this));
            return false;
        }

        if (player.isMounted()) {
            activeChar.sendPacket(new SystemMessage2(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addSkillName(this));
            return false;
        }

        if (!(target instanceof SiegeFlagInstance) || target.getNpcId() != 36590 || target.getClan() != player.getClan()) {
            activeChar.sendPacket(new SystemMessage2(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addSkillName(this));
            return false;
        }

        DominionSiegeEvent siegeEvent2 = target.getEvent(DominionSiegeEvent.class);
        if (siegeEvent2 == null || siegeEvent1 != siegeEvent2) {
            activeChar.sendPacket(new SystemMessage2(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addSkillName(this));
            return false;
        }

        return true;
    }

    @Override
    public void useSkill(Creature activeChar, List<Creature> targets) {
        for (Creature target : targets) {
            if (target != null) {
                Player player = (Player) activeChar;
                DominionSiegeEvent siegeEvent1 = player.getEvent(DominionSiegeEvent.class);
                if (siegeEvent1 == null) {
                    continue;
                }
                if (!(target instanceof SiegeFlagInstance) || target.getNpcId() != 36590 || target.getClan() != player.getClan()) {
                    continue;
                }
                if (!(player.getActiveWeaponFlagAttachment() instanceof TerritoryWardObject)) {
                    continue;
                }
                DominionSiegeEvent siegeEvent2 = target.getEvent(DominionSiegeEvent.class);
                if (siegeEvent2 == null || siegeEvent1 != siegeEvent2) {
                    continue;
                }

                // ?????????????? ??????????????????, ?? ?????????????? ???????????? ????????
                Dominion dominion = siegeEvent1.getResidence();
                // ???????? ?? ?????????????????? ??????????????????
                TerritoryWardObject wardObject = (TerritoryWardObject) player.getActiveWeaponFlagAttachment();
                // ?????????????????? ?? ?????????????? ?????????? ????????
                DominionSiegeEvent siegeEvent3 = wardObject.getEvent();
                Dominion dominion3 = siegeEvent3.getResidence();
                // ???????? ?????????????????? ?? ?????????????? ?????????????????? ????????
                int wardDominionId = wardObject.getDominionId();

                // ?????????????? ?? ?????????????????????? ????????, ?? ?????????????????????? ??????????????
                wardObject.despawnObject(siegeEvent3);
                // ?????????????? ????????
                dominion3.removeFlag(wardDominionId);
                // ?????????????????? ????????
                dominion.addFlag(wardDominionId);
                // ?????????????? ???????????? ?? ?????????????? ??????????????????
                // ?????????????? ??????????, ?????? ?? ?????????? ??????????????????
                siegeEvent1.spawnAction("ward_" + wardDominionId, true);

                DominionSiegeRunnerEvent runnerEvent = EventHolder.getInstance().getEvent(EventType.MAIN_EVENT, 1);
                runnerEvent.broadcastTo(new SystemMessage2(SystemMsg.CLAN_S1_HAS_SUCCEEDED_IN_CAPTURING_S2S_TERRITORY_WARD).addString(dominion.getOwner().getName()).addResidenceName(wardDominionId));
            }
        }
    }
}
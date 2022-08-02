package l2p.gameserver.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import l2p.gameserver.data.xml.holder.InstantZoneHolder;
import l2p.gameserver.instancemanager.QuestManager;
import l2p.gameserver.instancemanager.ReflectionManager;
import l2p.gameserver.model.CommandChannel;
import l2p.gameserver.model.Effect;
import l2p.gameserver.model.Party;
import l2p.gameserver.model.Player;
import l2p.gameserver.model.PlayerGroup;
import l2p.gameserver.model.Summon;
import l2p.gameserver.model.Zone;
import l2p.gameserver.model.entity.Reflection;
import l2p.gameserver.model.instances.DoorInstance;
import l2p.gameserver.model.quest.Quest;
import l2p.gameserver.model.quest.QuestState;
import l2p.gameserver.serverpackets.SystemMessage;
import l2p.gameserver.serverpackets.components.IStaticPacket;
import l2p.gameserver.serverpackets.components.SystemMsg;
import l2p.gameserver.templates.InstantZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReflectionUtils {

    private static final Logger _log = LoggerFactory.getLogger(ReflectionUtils.class);

    /**
     * Использовать акуратно возращает дверь нулевого рефлекта
     *
     * @param id
     * @return
     */
    public static DoorInstance getDoor(int id) {
        return ReflectionManager.DEFAULT.getDoor(id);
    }

    /**
     * Использовать акуратно возращает зону нулевого рефлекта
     *
     * @param name
     * @return
     */
    public static Zone getZone(String name) {
        return ReflectionManager.DEFAULT.getZone(name);
    }

    public static List<Zone> getZonesByType(Zone.ZoneType zoneType) {
        Collection<Zone> zones = ReflectionManager.DEFAULT.getZones();
        if (zones.isEmpty()) {
            return Collections.emptyList();
        }

        List<Zone> zones2 = new ArrayList<Zone>(5);
        for (Zone z : zones) {
            if (z.getType() == zoneType) {
                zones2.add(z);
            }
        }

        return zones2;
    }

    public static Reflection simpleEnterInstancedZone(Player player, int instancedZoneId) {
        Reflection ar = player.getActiveReflection();
        if (ar == null) {
            if (!canEnterInstance(player, instancedZoneId)) {
                return null;
            }
            return enterReflection(player, new Reflection(), InstantZoneHolder.getInstance().getInstantZone(instancedZoneId));
        }
        if (!canReenterInstance(player, instancedZoneId)) {
            return null;
        }
        player.teleToLocation(ar.getTeleportLoc(), ar);
        return null;
    }

    public static Reflection simpleEnterInstancedZone(Player player, Class<? extends Reflection> refClass, int instancedZoneId) {
        Reflection ar = player.getActiveReflection();
        if (ar == null) {
            if (!canEnterInstance(player, instancedZoneId)) {
                return null;
            }
            try {
                return enterReflection(player, (Reflection) refClass.newInstance(), InstantZoneHolder.getInstance().getInstantZone(instancedZoneId));
            } catch (Exception e) {
                _log.error("Unable to create instanced zone: ", e);

                return null;
            }
        }
        if (!canReenterInstance(player, instancedZoneId)) {
            return null;
        }
        player.teleToLocation(ar.getTeleportLoc(), ar);
        return null;
    }

    public static boolean canEnterInstance(Player player, int instancedZoneId) {
        if (player.isDead()) {
            return false;
        }
        if (player.isInPvPEvent()) {
            return false;
        }
        InstantZone iz = InstantZoneHolder.getInstance().getInstantZone(instancedZoneId);
        if (iz == null) {
            player.sendPacket(SystemMsg.SYSTEM_ERROR);
            return false;
        }
        IStaticPacket result = iz.canCreate();
        if (result != null) {
            player.sendPacket(result);
            return false;
        }
        PlayerGroup pg = player.getPlayerGroup();
        if (pg.getGroupLeader() != player) {
            player.sendPacket(SystemMsg.ONLY_A_PARTY_LEADER_CAN_MAKE_THE_REQUEST_TO_ENTER);
            return false;
        }
        int count = pg.getMemberCount();
        if ((iz.getMinParty() > 0) && (count < iz.getMinParty())) {
            if (!player.isGM()) {
                if (player.getParty() != null) {
                    if ((iz.getMinParty() > 9) && (player.getParty().getCommandChannel() == null)) {
                        player.sendPacket(new SystemMessage(SystemMsg.YOU_CANNOT_ENTER_BECAUSE_YOU_ARE_NOT_ASSOCIATED_WITH_THE_CURRENT_COMMAND_CHANNEL));
                    } else {
                        player.sendPacket(new SystemMessage(SystemMsg.YOU_MUST_HAVE_A_MINIMUM_OF_S1_PEOPLE_TO_ENTER_THIS_INSTANT_ZONE).addNumber(iz.getMinParty()));
                    }
                } else if (iz.getMinParty() > 9) {
                    player.sendPacket(new SystemMessage(SystemMsg.YOU_CANNOT_ENTER_BECAUSE_YOU_ARE_NOT_ASSOCIATED_WITH_THE_CURRENT_COMMAND_CHANNEL));
                } else {
                    player.sendPacket(SystemMsg.YOU_ARE_NOT_CURRENTLY_IN_A_PARTY_SO_YOU_CANNOT_ENTER);
                }
                return false;
            }
            Log.LogCommand(player, null, "entered instance " + iz.getName(), true);
        }
        if ((iz.getMaxParty() > 0) && (count > iz.getMaxParty())) {
            if (iz.getMaxParty() > 1) {
                player.sendPacket(SystemMsg.YOU_CANNOT_ENTER_DUE_TO_THE_PARTY_HAVING_EXCEEDED_THE_LIMIT);
            } else {
                player.sendPacket(SystemMsg.A_PARTY_CANNOT_BE_FORMED_IN_THIS_AREA);
            }
            return false;
        }
        for (Player member : pg) {
            if ((member != player) && (!player.isInRange(member, 500L))) {
                pg.broadCast(new IStaticPacket[]{new SystemMessage(SystemMsg.C1_IS_IN_A_LOCATION_WHICH_CANNOT_BE_ENTERED_THEREFORE_IT_CANNOT_BE_PROCESSED).addName(member)});
                return false;
            }
            SystemMsg msg = checkPlayer(member, iz);
            if (msg != null) {
                if (msg.size() > 0) {
                    pg.broadCast(new IStaticPacket[]{new SystemMessage(msg).addName(member)});
                } else {
                    member.sendPacket(msg);
                }
                return false;
            }
        }
        return true;
    }

    public static boolean canReenterInstance(Player player, int instancedZoneId) {
        if (player.isInPvPEvent()) {
            return false;
        }
        InstantZone iz = InstantZoneHolder.getInstance().getInstantZone(instancedZoneId);
        SystemMsg msg = reCheckPlayer(player, iz);
        if (msg != null) {
            if (msg.size() > 0) {
                player.sendPacket(new SystemMessage(msg).addName(player));
            } else {
                player.sendPacket(msg);
            }
            return false;
        }
        if (iz.isDispelBuffs()) {
            dispelBuffs(player);
        }
        return true;
    }

    private static SystemMsg checkPlayer(Player player, InstantZone instancedZone) {
        if (player.getActiveReflection() != null) {
            return SystemMsg.YOU_HAVE_ENTERED_ANOTHER_INSTANCE_ZONE_THEREFORE_YOU_CANNOT_ENTER_CORRESPONDING_DUNGEON;
        }
        if ((instancedZone.getMinLevel() > 0) && (player.getLevel() < instancedZone.getMinLevel())) {
            return SystemMsg.C1S_LEVEL_DOES_NOT_CORRESPOND_TO_THE_REQUIREMENTS_FOR_ENTRY;
        }
        if ((instancedZone.getMaxLevel() > 0) && (player.getLevel() > instancedZone.getMaxLevel())) {
            return SystemMsg.C1S_LEVEL_DOES_NOT_CORRESPOND_TO_THE_REQUIREMENTS_FOR_ENTRY;
        }
        if ((player.isCursedWeaponEquipped()) || (player.isInFlyingTransform()) || (player.isTerritoryFlagEquipped())) {
            return SystemMsg.YOU_CANNOT_ENTER_BECAUSE_YOU_DO_NOT_MEET_THE_REQUIREMENTS;
        }
        if (InstantZoneHolder.getInstance().getMinutesToNextEntrance(instancedZone.getId(), player) > 0) {
            return SystemMsg.C1_MAY_NOT_REENTER_YET;
        }
        if ((instancedZone.getRemovedItemId() > 0) && (instancedZone.getRemovedItemNecessity()) && (ItemFunctions.getItemCount(player, instancedZone.getRemovedItemId()) < 1L)) {
            return SystemMsg.C1S_ITEM_REQUIREMENT_IS_NOT_SUFFICIENT_AND_CANNOT_BE_ENTERED;
        }
        if (instancedZone.getRequiredQuestId() > 0) {
            Quest quest = QuestManager.getQuest(instancedZone.getRequiredQuestId());
            if (quest == null) {
                return null;
            }
            QuestState qs = player.getQuestState(quest.getClass());
            if ((qs == null) || (qs.getState() != 2)) {
                return SystemMsg.C1S_QUEST_REQUIREMENT_IS_NOT_SUFFICIENT_AND_CANNOT_BE_ENTERED;
            }
        }
        return null;
    }

    private static SystemMsg reCheckPlayer(Player player, InstantZone instancedZone) {
        Reflection ar = player.getActiveReflection();
        if (ar != null) {
            InstantZone iz = player.getActiveReflection().getInstancedZone();
            if (iz != instancedZone) {
                return SystemMsg.YOU_HAVE_ENTERED_ANOTHER_INSTANCE_ZONE_THEREFORE_YOU_CANNOT_ENTER_CORRESPONDING_DUNGEON;
            }
            if (ar.getPlayerCount() >= iz.getMaxParty()) {
                return SystemMsg.YOU_CANNOT_ENTER_DUE_TO_THE_PARTY_HAVING_EXCEEDED_THE_LIMIT;
            }
        }
        if ((instancedZone.getMinLevel() > 0) && (player.getLevel() < instancedZone.getMinLevel())) {
            return SystemMsg.C1S_LEVEL_DOES_NOT_CORRESPOND_TO_THE_REQUIREMENTS_FOR_ENTRY;
        }
        if ((instancedZone.getMaxLevel() > 0) && (player.getLevel() > instancedZone.getMaxLevel())) {
            return SystemMsg.C1S_LEVEL_DOES_NOT_CORRESPOND_TO_THE_REQUIREMENTS_FOR_ENTRY;
        }
        if ((player.isCursedWeaponEquipped()) || (player.isInFlyingTransform()) || (player.isTerritoryFlagEquipped())) {
            return SystemMsg.YOU_CANNOT_ENTER_BECAUSE_YOU_DO_NOT_MEET_THE_REQUIREMENTS;
        }
        if (instancedZone.getRequiredQuestId() > 0) {
            Quest quest = QuestManager.getQuest(instancedZone.getRequiredQuestId());
            if (quest == null) {
                return null;
            }
            QuestState qs = player.getQuestState(quest.getClass());
            if ((qs == null) || (qs.getState() != 2)) {
                return SystemMsg.C1S_QUEST_REQUIREMENT_IS_NOT_SUFFICIENT_AND_CANNOT_BE_ENTERED;
            }
        }
        return null;
    }

    private static void dispelBuffs(Player player) {
        for (Effect e : player.getEffectList().getAllEffects()) {
            if ((!e.getSkill().isOffensive()) && (!e.getSkill().isNewbie()) && (e.isCancelable()) && (!e.getSkill().isPreservedOnDeath())) {
                player.sendPacket(new SystemMessage(SystemMsg.THE_EFFECT_OF_S1_HAS_BEEN_REMOVED).addSkillName(e.getSkill().getId(), e.getSkill().getLevel()));
                e.exit();
            }
        }
        Summon servitor = player.getPet();
        if (servitor != null) {
            for (Effect e : servitor.getEffectList().getAllEffects()) {
                if ((!e.getSkill().isOffensive()) && (!e.getSkill().isNewbie()) && (e.isCancelable()) && (!e.getSkill().isPreservedOnDeath())) {
                    e.exit();
                }
            }
        }
    }

    private static Reflection enterReflection(Player invoker, Reflection r, InstantZone iz) {
        r.init(iz);
        if (r.getReturnLoc() == null) {
            r.setReturnLoc(invoker.getLoc());
        }
        Party party = invoker.getParty();
        if (party != null) {
            CommandChannel cc = party.getCommandChannel();
            if (cc != null) {
                cc.setReflection(r);
                r.setCommandChannel(cc);
            } else {
                party.setReflection(r);
                r.setParty(party);
            }
        }
        PlayerGroup pg = invoker.getPlayerGroup();
        for (Player member : pg) {
            if (iz.getRemovedItemId() > 0) {
                ItemFunctions.deleteItem(member, iz.getRemovedItemId(), iz.getRemovedItemCount());
            }
            if (iz.getGiveItemId() > 0) {
                ItemFunctions.addItem(member, iz.getGiveItemId(), iz.getGiveItemCount());
            }
            if (iz.isDispelBuffs()) {
                dispelBuffs(member);
            }
            if (iz.getSetReuseUponEntry()) {
                member.setInstanceReuse(iz.getId(), System.currentTimeMillis());
            }
            member.setVar("backCoords", invoker.getLoc().toXYZString(), -1L);
            member.teleToLocation(iz.getTeleportCoord(), r);
        }
        return r;
    }
}

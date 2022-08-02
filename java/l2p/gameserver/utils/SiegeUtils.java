package l2p.gameserver.utils;

import java.util.List;
import l2p.gameserver.data.xml.holder.ResidenceHolder;
import l2p.gameserver.model.Player;
import l2p.gameserver.model.entity.events.impl.DominionSiegeEvent;
import l2p.gameserver.model.entity.residence.Castle;
import l2p.gameserver.model.entity.residence.ClanHall;
import l2p.gameserver.model.entity.residence.Dominion;
import l2p.gameserver.model.entity.residence.Fortress;
import l2p.gameserver.tables.SkillTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SiegeUtils {

    private static final Logger _log = LoggerFactory.getLogger(Castle.class);

    public static void addSiegeSkills(Player character) {
        character.addSkill(SkillTable.getInstance().getInfo(246, 1), false);
        character.addSkill(SkillTable.getInstance().getInfo(247, 1), false);
        if (character.isNoble()) {
            character.addSkill(SkillTable.getInstance().getInfo(326, 1), false);
        }

        if (character.getClan() != null && character.getClan().getCastle() > 0) {
            character.addSkill(SkillTable.getInstance().getInfo(844, 1), false);
            character.addSkill(SkillTable.getInstance().getInfo(845, 1), false);
        }
    }

    public static void removeSiegeSkills(Player character) {
        character.removeSkill(SkillTable.getInstance().getInfo(246, 1), false);
        character.removeSkill(SkillTable.getInstance().getInfo(247, 1), false);
        character.removeSkill(SkillTable.getInstance().getInfo(326, 1), false);

        if (character.getClan() != null && character.getClan().getCastle() > 0) {
            character.removeSkill(SkillTable.getInstance().getInfo(844, 1), false);
            character.removeSkill(SkillTable.getInstance().getInfo(845, 1), false);
        }
    }

    public static boolean getCanRide() {

        for (Dominion d : ResidenceHolder.getInstance().getResidenceList(Dominion.class)) {
            DominionSiegeEvent dominionSiegeEvent = d.getSiegeEvent();
            if (dominionSiegeEvent.isInProgress()) {
                return false;
            }
        }

        List<Castle> castles = ResidenceHolder.getInstance().getResidenceList(Castle.class);
        for (Castle castle : castles) {
            if (castle != null && castle.getSiegeEvent().isInProgress()) {
                return false;
            }
        }

        List<Fortress> forts = ResidenceHolder.getInstance().getResidenceList(Fortress.class);
        for (Fortress fortress : forts) {
            if (fortress != null && fortress.getSiegeEvent().isInProgress()) {
                return false;
            }
        }

        List<ClanHall> chs = ResidenceHolder.getInstance().getResidenceList(ClanHall.class);
        for (ClanHall clanHall : chs) {
            if (clanHall != null && clanHall.getBaseMinBid() == 0 && clanHall.getSiegeEvent().isInProgress()) {
                return false;
            }
        }

        return true;
    }
}

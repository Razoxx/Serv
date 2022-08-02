package l2p.gameserver.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import l2p.commons.dbutils.DbUtils;
import l2p.gameserver.cache.Msg;
import l2p.gameserver.data.xml.holder.EventHolder;
import l2p.gameserver.database.DatabaseFactory;
import l2p.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2p.gameserver.model.Request.L2RequestType;
import l2p.gameserver.model.entity.events.impl.DominionSiegeEvent;
import l2p.gameserver.model.pledge.Clan;
import l2p.gameserver.model.pledge.SubUnit;
import l2p.gameserver.model.pledge.UnitMember;
import l2p.gameserver.serverpackets.JoinPledge;
import l2p.gameserver.serverpackets.PledgeShowInfoUpdate;
import l2p.gameserver.serverpackets.PledgeShowMemberListAdd;
import l2p.gameserver.serverpackets.PledgeSkillList;
import l2p.gameserver.serverpackets.SkillList;
import l2p.gameserver.serverpackets.SystemMessage2;
import l2p.gameserver.serverpackets.components.SystemMsg;
import l2p.gameserver.tables.ClanTable;
import l2p.gameserver.utils.GArray;
import l2p.gameserver.utils.ItemFunctions;

public class AcademList implements IVoicedCommandHandler {

    private final static Logger _log = Logger.getLogger(AcademList.class.getName());

    private static GArray<Player> academList = new GArray<Player>();
    private static GArray<Clan> clanList = new GArray<Clan>();

    public static void addAcademList(Player player) {
        academList.add(player);
    }

    public static void deleteAcademList(Player player) {
        academList.remove(player);
    }

    public static GArray<Player> getAcademList() {
        return academList;
    }

    public static GArray<Clan> getClanList() {
        return clanList;
    }

    public static void registerClanList(Clan clan) {
        for (Clan clanTmp : clanList) {
            if (clanTmp == clan) {
                return;
            }
        }

        clanList.add(clan);
    }

    public static void inviteInAcademy(Player player, Player academChar) {

        Request request = academChar.getRequest();
        if (request == null || !request.isTypeOf(L2RequestType.CLAN)) {
            return;
        }

        if (!request.isInProgress()) {
            request.cancel();
            academChar.sendActionFailed();
            return;
        }

        Player requestor = request.getRequestor();

        if (requestor == null) {
            request.cancel();
            academChar.sendPacket(SystemMsg.THAT_PLAYER_IS_NOT_ONLINE);
            academChar.sendActionFailed();
            return;
        }

        if (requestor.getRequest() != request) {
            request.cancel();
            academChar.sendActionFailed();
            return;
        }

        Clan clan = requestor.getClan();
        if (clan == null) {
            request.cancel();
            academChar.sendActionFailed();
            return;
        }

        if (!academChar.canJoinClan()) {
            request.cancel();
            academChar.sendPacket(SystemMsg.AFTER_LEAVING_OR_HAVING_BEEN_DISMISSED_FROM_A_CLAN_YOU_MUST_WAIT_AT_LEAST_A_DAY_BEFORE_JOINING_ANOTHER_CLAN);
            return;
        }

        try {

            if (academChar.getPledgePrice() != 0) {
                if (ItemFunctions.getItemCount(requestor, academChar.getPledgeItemId()) >= academChar.getPledgePrice()) {
                    ItemFunctions.removeItem(requestor, academChar.getPledgeItemId(), academChar.getPledgePrice(), true);
                    registerClanList(requestor.getClan());
                    registerAcademInDb(requestor.getClan(), academChar, academChar.getPledgeItemId(), academChar.getPledgePrice());
                    academChar.setVar("bbsAcadem", String.valueOf(System.currentTimeMillis()), -1);
                    academChar.setVar("bbsAcademPrice", String.valueOf(academChar.getPledgePrice()), -1);
                } else {
                    requestor.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
                    academChar.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
                    return;
                }
            }

            academChar.sendPacket(new JoinPledge(requestor.getClanId()));

            int pledgeType = request.getInteger("pledgeType");
            SubUnit subUnit = clan.getSubUnit(pledgeType);
            if (subUnit == null) {
                return;
            }

            UnitMember member = new UnitMember(clan, academChar.getName(), academChar.getTitle(), academChar.getLevel(), academChar.getClassId().getId(), academChar.getObjectId(), pledgeType, academChar.getPowerGrade(), academChar.getApprentice(), academChar.getSex(), Clan.SUBUNIT_NONE);
            subUnit.addUnitMember(member);

            academChar.setPledgeType(pledgeType);
            academChar.setClan(clan);

            member.setPlayerInstance(academChar, false);

            if (pledgeType == Clan.SUBUNIT_ACADEMY) {
                academChar.setLvlJoinedAcademy(academChar.getLevel());
            }

            member.setPowerGrade(clan.getAffiliationRank(academChar.getPledgeType()));

            clan.broadcastToOtherOnlineMembers(new PledgeShowMemberListAdd(member), academChar);
            clan.broadcastToOnlineMembers(new SystemMessage2(SystemMsg.S1_HAS_JOINED_THE_CLAN).addString(academChar.getName()), new PledgeShowInfoUpdate(clan));

            // this activates the clan tab on the new member
            academChar.sendPacket(SystemMsg.ENTERED_THE_CLAN);
            academChar.sendPacket(academChar.getClan().listAll());
            academChar.setLeaveClanTime(0);
            academChar.updatePledgeClass();

            // добавляем скилы игроку, ток тихо
            clan.addSkillsQuietly(academChar);
            // отображем
            academChar.sendPacket(new PledgeSkillList(clan));
            academChar.sendPacket(new SkillList(academChar));

            EventHolder.getInstance().findEvent(academChar);
            if (clan.getWarDominion() > 0) // баг оффа, после вступа в клан нужен релог для квестов
            {
                DominionSiegeEvent siegeEvent = academChar.getEvent(DominionSiegeEvent.class);

                siegeEvent.updatePlayer(academChar, true);
            } else {
                academChar.broadcastCharInfo();
            }

            academChar.store(false);
        } finally {
            request.done();
        }

    }

    public static void registerAcademInDb(final Clan clan, final Player academic, final int itemId, final int price) {

        Connection con = null;
        PreparedStatement statement = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement("INSERT INTO character_academ (clan_id,char_academ_id,itemId,price,time) values(?,?,?,?,?)");
            statement.setInt(1, clan.getClanId());
            statement.setInt(2, academic.getObjectId());
            statement.setInt(3, itemId);
            statement.setInt(4, price);
            statement.setLong(5, System.currentTimeMillis());

            statement.execute();

            deleteAcademList(academic);
        } catch (final Exception e) {
            e.printStackTrace();
        } finally {
            DbUtils.closeQuietly(con, statement);
        }
    }

    public static void deleteAcademInDb(final Clan clan, int charId, boolean reward, boolean leaderOnline) {
        Connection con = null;
        PreparedStatement statement = null;
        ResultSet rset = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();

            if (reward) {
                statement = con.prepareStatement("SELECT itemId, price FROM character_academ WHERE clan_id=? AND char_academ_id=?");
                statement.setInt(1, clan.getClanId());
                statement.setInt(2, charId);
                rset = statement.executeQuery();
                while (rset.next()) {
                    try {
                        int itemId = rset.getInt("itemId");
                        long price = rset.getLong("price");

                        Player activeChar = GameObjectsStorage.getPlayer(charId);

                        ItemFunctions.addItem(activeChar, itemId, price);
                    } catch (final Exception e) {
                        e.printStackTrace();
                    }
                }
            } else if (leaderOnline) {
                statement = con.prepareStatement("SELECT itemId, price FROM character_academ WHERE clan_id=? AND char_academ_id=?");
                statement.setInt(1, clan.getClanId());
                statement.setInt(2, charId);
                rset = statement.executeQuery();
                while (rset.next()) {
                    try {
                        int itemId = rset.getInt("itemId");
                        long price = rset.getLong("price");

                        Player activeChar = GameObjectsStorage.getPlayer(clan.getLeaderId());

                        ItemFunctions.addItem(activeChar, itemId, price);
                        clan.removeClanMember(charId);
                    } catch (final Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            statement = con.prepareStatement("DELETE FROM character_academ WHERE clan_id=? AND char_academ_id=?");
            statement.setInt(1, clan.getClanId());
            statement.setInt(2, charId);
            statement.execute();
        } catch (Exception e) {
            _log.log(Level.WARNING, "could not delete Academic:", e);
        } finally {
            DbUtils.closeQuietly(con, statement);
        }
    }

    public static void restore() {
        Connection con = null;
        PreparedStatement statement = null;
        ResultSet rset = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement("SELECT clan_id FROM character_academ");
            rset = statement.executeQuery();
            while (rset.next()) {
                try {
                    int id = rset.getInt("clan_id");

                    clanList.add(ClanTable.getInstance().getClan(id));
                } catch (final Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            _log.log(Level.WARNING, "could not restore Clans:", e);
        } finally {
            DbUtils.closeQuietly(con, statement, rset);
        }
    }

    public static long findBadAcadem(final Clan clan) {
        long totalPrice = 0;
        Connection con = null;
        PreparedStatement statement = null;
        ResultSet rset = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement("SELECT char_academ_id,price,time FROM character_academ WHERE clan_id=?");
            statement.setInt(1, clan.getClanId());
            rset = statement.executeQuery();
            while (rset.next()) {
                try {
                    int charId = rset.getInt("char_academ_id");
                    long price = rset.getLong("price");
                    long time = rset.getLong("time");

                    if (System.currentTimeMillis() - time > (3 * 24 * 60 * 60 * 1000)) {
                        totalPrice += price;
                        deleteAcademInDb(clan, charId, false, false);
                    }
                } catch (final Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            _log.log(Level.WARNING, "could not restore Clans:", e);
        } finally {
            DbUtils.closeQuietly(con, statement, rset);
        }

        return totalPrice;
    }

    @Override
    public boolean useVoicedCommand(String command, Player activeChar, String target) {
        return false;
    }

    @Override
    public String[] getVoicedCommandList() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}

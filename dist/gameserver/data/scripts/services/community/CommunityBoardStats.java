package services.community;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;

import l2p.commons.dbutils.DbUtils;
import l2p.gameserver.Config;
import l2p.gameserver.data.htm.HtmCache;
import l2p.gameserver.database.DatabaseFactory;
import l2p.gameserver.handler.bbs.CommunityBoardManager;
import l2p.gameserver.handler.bbs.ICommunityBoardHandler;
import l2p.gameserver.instancemanager.CastleManorManager;
import l2p.gameserver.model.Player;
import l2p.gameserver.serverpackets.ShowBoard;
import l2p.gameserver.scripts.ScriptFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommunityBoardStats implements ScriptFile, ICommunityBoardHandler {

    private static final Logger _log = LoggerFactory.getLogger(CommunityBoardStats.class);

    /**
     * Имплементированые методы скриптов
     */
    @Override
    public void onLoad() {
        if(false){
        //if (Config.COMMUNITYBOARD_ENABLED) {
            _log.info("CommunityBoard: Stats service loaded.");
            CommunityBoardManager.getInstance().registerHandler(this);
        }
    }

    @Override
    public void onReload() {
        if(false){
      //  if (Config.COMMUNITYBOARD_ENABLED) {
            CommunityBoardManager.getInstance().removeHandler(this);
        }
    }

    @Override
    public void onShutdown() {
    }

    /**
     * Регистратор команд
     */
    @Override
    public String[] getBypassCommands() {
        return new String[]{"_bbsstat;", "_bbsstat;pk", "_bbsstat;online", "_bbsstat;clan", "_bbsstat;castle"};
    }

    /**
     * Класс общих пер-х
     */
    public class CBStatMan {

        public int PlayerId = 0; // obj_id Char
        public String ChName = ""; // Char name
        public int ChGameTime = 0; // Time in game
        public int ChPk = 0; // Char PK
        public int ChPvP = 0; // Char PVP
        public int ChOnOff = 0; // Char offline/online cure time
        public int ChSex = 0; // Char sex
        public String NameCastl;
        public Object siegeDate;
        public String Percent;
        public Object id2;
        public int id;
        public int ClanLevel;
        public int hasCastle;
        public int ReputationClan;
        public String AllyName;
        public String ClanName;
        public String Owner;
    }

    /**
     * Обработчик команд класса
     *
     * @param Player - плеер (Call'er)
     * @param command - команда обработки
     */
    @Override
    public void onBypassCommand(Player player, String command) {
        if (command.equals("_bbsstat;")) {
            showPvp(player);
        } else if (command.startsWith("_bbsstat;pk")) {
            showPK(player);
        } else if (command.startsWith("_bbsstat;online")) {
            showOnline(player);
        } else if (command.startsWith("_bbsstat;clan")) {
            showClan(player);
        } else if (command.startsWith("_bbsstat;castle")) {
            showCastle(player);
        } else {
            ShowBoard.separateAndSend("<html><body><br><br><center>В bbsstat функция: " + command + " пока не реализована</center><br><br></body></html>", player);
        }
    }

    /**
     * Вызываем показ текущего списка лучших 20 плееров по ПВП показателю
     * Осуществляем внутри-классовый конект и чекинг таблицы (по приведённым
     * параметрам)
     *
     * @param player
     */
    private void showPvp(Player player) {
        Connection con = null;
        PreparedStatement statement = null;
        ResultSet rs = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement("SELECT * FROM characters WHERE accesslevel = '0' ORDER BY pvpkills DESC LIMIT 10;");
            rs = statement.executeQuery();

            StringBuilder html = new StringBuilder();
            html.append("<table width=570>");
            while (rs.next()) {
                CBStatMan tp = new CBStatMan();
                tp.PlayerId = rs.getInt("obj_Id");
                tp.ChName = rs.getString("char_name");
                tp.ChSex = rs.getInt("sex");
                tp.ChGameTime = rs.getInt("onlinetime");
                tp.ChPk = rs.getInt("pkkills");
                tp.ChPvP = rs.getInt("pvpkills");
                tp.ChOnOff = rs.getInt("online");

                String sex = tp.ChSex == 1 ? "Ж" : "М";
                String color;
                String OnOff;
                if (tp.ChOnOff == 1) {
                    OnOff = "В игре.";
                    color = "00CC00";
                } else {
                    OnOff = "Оффлайн.";
                    color = "D70000";
                }
                html.append("<tr>");
                html.append("<td width=250>" + tp.ChName + "</td>");
                html.append("<td width=50>" + sex + "</td>");
                html.append("<td width=100>" + OnlineTime(tp.ChGameTime) + "</td>");
                html.append("<td width=50>" + tp.ChPk + "</td>");
                html.append("<td width=50><font color=00CC00>" + tp.ChPvP + "</font></td>");
                html.append("<td width=100><font color=" + color + ">" + OnOff + "</font></td>");
                html.append("</tr>");
            }
            html.append("</table>");

            String content = HtmCache.getInstance().getHtml(Config.BBS_HOME_DIR + "pages/stats/stats_top_pvp.htm", player);
            content = content.replace("%stats_top_pvp%", html.toString());
            ShowBoard.separateAndSend(content, player);
            return;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DbUtils.closeQuietly(con, statement, rs);
        }
    }

    private void showPK(Player player) {
        Connection con = null;
        PreparedStatement statement = null;
        ResultSet rs = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement("SELECT * FROM characters WHERE accesslevel = '0' ORDER BY pkkills DESC LIMIT 10;");
            rs = statement.executeQuery();

            StringBuilder html = new StringBuilder();
            html.append("<table width=570>");
            while (rs.next()) {
                CBStatMan tp = new CBStatMan();
                tp.PlayerId = rs.getInt("obj_Id");
                tp.ChName = rs.getString("char_name");
                tp.ChSex = rs.getInt("sex");
                tp.ChGameTime = rs.getInt("onlinetime");
                tp.ChPk = rs.getInt("pkkills");
                tp.ChPvP = rs.getInt("pvpkills");
                tp.ChOnOff = rs.getInt("online");

                String sex = tp.ChSex == 1 ? "Ж" : "М";
                String color;
                String OnOff;
                if (tp.ChOnOff == 1) {
                    OnOff = "В игре.";
                    color = "00CC00";
                } else {
                    OnOff = "Оффлайн.";
                    color = "D70000";
                }
                html.append("<tr>");
                html.append("<td width=250>" + tp.ChName + "</td>");
                html.append("<td width=50>" + sex + "</td>");
                html.append("<td width=100>" + OnlineTime(tp.ChGameTime) + "</td>");
                html.append("<td width=50><font color=00CC00>" + tp.ChPk + "</font></td>");
                html.append("<td width=50>" + tp.ChPvP + "</td>");
                html.append("<td width=100><font color=" + color + ">" + OnOff + "</font></td>");
                html.append("</tr>");
            }
            html.append("</table>");

            String content = HtmCache.getInstance().getHtml(Config.BBS_HOME_DIR + "pages/stats/stats_top_pk.htm", player);
            content = content.replace("%stats_top_pk%", html.toString());
            ShowBoard.separateAndSend(content, player);
            return;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DbUtils.closeQuietly(con, statement, rs);
        }
    }

    private void showOnline(Player player) {
        Connection con = null;
        PreparedStatement statement = null;
        ResultSet rs = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement("SELECT * FROM characters WHERE accesslevel = '0' ORDER BY onlinetime DESC LIMIT 10;");
            rs = statement.executeQuery();

            StringBuilder html = new StringBuilder();
            html.append("<table width=570>");
            while (rs.next()) {
                CBStatMan tp = new CBStatMan();
                tp.PlayerId = rs.getInt("obj_Id");
                tp.ChName = rs.getString("char_name");
                tp.ChSex = rs.getInt("sex");
                tp.ChGameTime = rs.getInt("onlinetime");
                tp.ChPk = rs.getInt("pkkills");
                tp.ChPvP = rs.getInt("pvpkills");
                tp.ChOnOff = rs.getInt("online");

                String sex = tp.ChSex == 1 ? "Ж" : "М";
                String color;
                String OnOff;
                if (tp.ChOnOff == 1) {
                    OnOff = "В игре.";
                    color = "00CC00";
                } else {
                    OnOff = "Оффлайн.";
                    color = "D70000";
                }
                html.append("<tr>");
                html.append("<td width=250>" + tp.ChName + "</td>");
                html.append("<td width=50>" + sex + "</td>");
                html.append("<td width=100><font color=00CC00>" + OnlineTime(tp.ChGameTime) + "</font></td>");
                html.append("<td width=50>" + tp.ChPk + "</td>");
                html.append("<td width=50>" + tp.ChPvP + "</td>");
                html.append("<td width=100><font color=" + color + ">" + OnOff + "</font></td>");
                html.append("</tr>");
            }
            html.append("</table>");

            String content = HtmCache.getInstance().getHtml(Config.BBS_HOME_DIR + "pages/stats/stats_online.htm", player);
            content = content.replace("%stats_online%", html.toString());
            ShowBoard.separateAndSend(content, player);
            return;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DbUtils.closeQuietly(con, statement, rs);
        }
    }

    private void showCastle(Player player) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        Connection con = null;
        PreparedStatement statement = null;
        ResultSet rs = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement("SELECT * FROM castle ORDER BY id DESC LIMIT 10;");
            rs = statement.executeQuery();
            StringBuilder html = new StringBuilder();

            html.append("<table width=570>");
            String Owner = null;
            String color = "FFFFFF";
            while (rs.next()) {
                CBStatMan tp = new CBStatMan();
                tp.id = rs.getInt("id");
                tp.NameCastl = rs.getString("name");
                tp.Percent = (rs.getString("tax_percent") + "%");
                tp.siegeDate = sdf.format(new Date(rs.getLong("siege_date")));
                Owner = CastleManorManager.getInstance().getOwner(tp.id);

                if (Owner != null) {
                    color = "00CC00";
                } else {
                    color = "FFFFFF";
                    Owner = "Нет владельца";
                }
                html.append("<tr>");
                html.append("<td width=150>" + tp.NameCastl + "</td>");
                html.append("<td width=100>" + tp.Percent + "</td>");
                html.append("<td width=200><font color=" + color + ">" + Owner + "</font></td>");
                html.append("<td width=150>" + tp.siegeDate + "</td>");
                html.append("</tr>");
            }
            html.append("</table>");

            String content = HtmCache.getInstance().getHtml(Config.BBS_HOME_DIR + "pages/stats/stats_castle.htm", player);
            content = content.replace("%stats_castle%", html.toString());
            ShowBoard.separateAndSend(content, player);
            return;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DbUtils.closeQuietly(con, statement, rs);
        }
    }

    private void showClan(Player player) {
        Connection con = null;
        PreparedStatement statement = null;
        ResultSet rs = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement("SELECT clan_subpledges.name,clan_data.clan_level,clan_data.reputation_score,clan_data.hasCastle,ally_data.ally_name FROM clan_data LEFT JOIN ally_data ON clan_data.ally_id = ally_data.ally_id LEFT JOIN `clan_subpledges` ON clan_data.clan_id = clan_subpledges.clan_id WHERE clan_data.clan_level>0 AND clan_subpledges.leader_id != '' order by clan_data.clan_level desc limit 10;");
            rs = statement.executeQuery();

            StringBuilder html = new StringBuilder();
            html.append("<table width=570>");
            while (rs.next()) {
                CBStatMan tp = new CBStatMan();
                tp.ClanName = rs.getString("name");
                tp.AllyName = rs.getString("ally_name");
                tp.ReputationClan = rs.getInt("reputation_score");
                tp.ClanLevel = rs.getInt("clan_level");
                tp.hasCastle = rs.getInt("hasCastle");
                String hasCastle = "";
                String castleColor = "D70000";

                switch (tp.hasCastle) {
                    case 1:
                        hasCastle = "Gludio";
                        castleColor = "00CC00";
                        break;
                    case 2:
                        hasCastle = "Dion";
                        castleColor = "00CC00";
                        break;
                    case 3:
                        hasCastle = "Giran";
                        castleColor = "00CC00";
                        break;
                    case 4:
                        hasCastle = "Oren";
                        castleColor = "00CC00";
                        break;
                    case 5:
                        hasCastle = "Aden";
                        castleColor = "00CC00";
                        break;
                    case 6:
                        hasCastle = "Innadril";
                        castleColor = "00CC00";
                        break;
                    case 7:
                        hasCastle = "Goddard";
                        castleColor = "00CC00";
                        break;
                    case 8:
                        hasCastle = "Rune";
                        castleColor = "00CC00";
                        break;
                    case 9:
                        hasCastle = "Schuttgart";
                        castleColor = "00CC00";
                        break;
                    default:
                        hasCastle = "Нету";
                        castleColor = "D70000";
                        break;
                }
                html.append("<tr>");
                html.append("<td width=150>" + tp.ClanName + "</td>");
                if (tp.AllyName != null) {
                    html.append("<td width=150>" + tp.AllyName + "</td>");
                } else {
                    html.append("<td width=150>Нет альянса</td>");
                }
                html.append("<td width=100>" + tp.ReputationClan + "</td>");
                html.append("<td width=50>" + tp.ClanLevel + "</td>");
                html.append("<td width=100><font color=" + castleColor + ">" + hasCastle + "</font></td>");
                html.append("</tr>");
            }
            html.append("</table>");

            String content = HtmCache.getInstance().getHtml(Config.BBS_HOME_DIR + "pages/stats/stats_clan.htm", player);
            content = content.replace("%stats_clan%", html.toString());
            ShowBoard.separateAndSend(content, player);
            return;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DbUtils.closeQuietly(con, statement, rs);
        }
    }

    /**
     * Вызываем показ текущего списка лучших 20 плееров по PK показателю
     * Осуществляем внутри-классовый конект и чекинг таблицы (по приведённым
     * параметрам)
     *
     * @param player
     */
    String OnlineTime(int time) {
        long onlinetimeH;
        int onlinetimeM;
        if (time / 60 / 60 - 0.5 <= 0) {
            onlinetimeH = 0;
        } else {
            onlinetimeH = Math.round(time / 60 / 60 - 0.5);
        }
        onlinetimeM = Math.round((time / 60 / 60 - onlinetimeH) * 60);
        return "" + onlinetimeH + " ч. " + onlinetimeM + " м.";
    }

    /**
     * Не используемый, но вызываемый метод имплемента
     */
    @Override
    public void onWriteCommand(Player player, String bypass, String arg1, String arg2, String arg3, String arg4, String arg5) {
    }
}
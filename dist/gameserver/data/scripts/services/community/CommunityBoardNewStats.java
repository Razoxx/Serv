package services.community;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import l2p.commons.dbutils.DbUtils;
import l2p.gameserver.Config;
import l2p.gameserver.data.htm.HtmCache;
import l2p.gameserver.database.DatabaseFactory;
import l2p.gameserver.handler.bbs.CommunityBoardManager;
import l2p.gameserver.handler.bbs.ICommunityBoardHandler;
import l2p.gameserver.model.Player;
import l2p.gameserver.serverpackets.ShowBoard;
import l2p.gameserver.scripts.ScriptFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommunityBoardNewStats implements ScriptFile, ICommunityBoardHandler {

    private static final Logger _log = LoggerFactory.getLogger(CommunityBoardNewStats.class);
    public long lUpdateTime = 0L;
    public int nCounter = 0;
    static CBStatMan pbBStats = new CBStatMan();

    /**
     * Имплементированые методы скриптов
     */
    @Override
    public void onLoad() {
        if (Config.COMMUNITYBOARD_ENABLED) {
            _log.info("CommunityBoard: New Stats service loaded.");
            CommunityBoardManager.getInstance().registerHandler(this);
        }
    }

    @Override
    public void onReload() {
        if (Config.COMMUNITYBOARD_ENABLED) {
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
        return new String[]{"_bbsstat", "_bbsstat:index"};
    }

    /**
     * Класс общих пер-х
     */
    public static class CBStatMan {

        public String[] asCharNameTopPvP = new String[10];
        public int[] anCharPvPCount = new int[10];
        public int[] anCharPvPOnline = new int[10];
        
        public String[] asCharNameTopPk = new String[10];
        public int[] anCharPkCount = new int[10];
        public int[] anCharPkOnline = new int[10];
    }

    /**
     * Обработчик команд класса
     *
     * @param player - плеер (Call'er)
     * @param command - команда обработки
     */
    @Override
    public void onBypassCommand(Player player, String command) {
        if (command.equals("_bbsstat")) {
            if (lUpdateTime + 5 * 60 < System.currentTimeMillis() / 1000L) {
                selectTopPK(player);
                selectTopPVP(player);

                lUpdateTime = (System.currentTimeMillis() / 1000L);
            }
            showAllStats(player, 1);

        } else if (command.startsWith("_bbsstat:index")) {
            selectTopPK(player);
            selectTopPVP(player);
            showAllStats(player, 1);
        } else {
            ShowBoard.separateAndSend("<html><body><br><br><center>В bbsstat функция: " + command + " пока не реализована</center><br><br></body></html>", player);
        }
    }

    private void showAllStats(Player player, int nPage) {
        nCounter = 0;

        if (nPage == 1) {
            String content = HtmCache.getInstance().getHtml(Config.BBS_HOME_DIR + "pages/stats.htm", player);

            while (nCounter < 10) {
                if (pbBStats.asCharNameTopPvP[nCounter] != null) {
                    content = content.replace("%Top_PvP_Name_" + nCounter + "%", pbBStats.asCharNameTopPvP[nCounter]);
                    content = content.replace("%Top_PvP_Count_" + nCounter + "%", Integer.toString(pbBStats.anCharPvPCount[nCounter]));
                    content = content.replace("%Top_PvP_Online_" + nCounter + "%", pbBStats.anCharPvPOnline[nCounter] == 1 ? player.isLangRus() ? "<font color=\"66FF33\">да</font>" : "<font color=\"66FF33\">yes</font>" : player.isLangRus() ? "<font color=\"B59A75\">нет</font>" : "<font color=\"B59A75\">no</font>");
                } else {
                    content = content.replace("%Top_PvP_Name_" + nCounter + "%", player.isLangRus() ? "Нет данных" : "No data");
                    content = content.replace("%Top_PvP_Count_" + nCounter + "%", "<font name=\"hs12\">0</font>");
                    content = content.replace("%Top_PvP_Online_" + nCounter + "%", "");
                }
                if (pbBStats.asCharNameTopPk[nCounter] != null) {
                    content = content.replace("%Top_Pk_Name_" + nCounter + "%", pbBStats.asCharNameTopPk[nCounter]);
                    content = content.replace("%Top_Pk_Count_" + nCounter + "%", Integer.toString(pbBStats.anCharPkCount[nCounter]));
                    content = content.replace("%Top_Pk_Online_" + nCounter + "%", pbBStats.anCharPkOnline[nCounter] == 1 ? player.isLangRus() ? "<font color=\"66FF33\">да</font>" : "<font color=\"66FF33\">yes</font>" : player.isLangRus() ? "<font color=\"B59A75\">нет</font>" : "<font color=\"B59A75\">no</font>");
                } else {
                    content = content.replace("%Top_Pk_Name_" + nCounter + "%", player.isLangRus() ? "Нет данных" : "No data");
                    content = content.replace("%Top_Pk_Count_" + nCounter + "%", "<font name=\"hs12\">0</font>");
                    content = content.replace("%Top_Pk_Online_" + nCounter + "%", "");
                }
                nCounter += 1;
            }
            ShowBoard.separateAndSend(content, player);
        }
    }

    public void selectTopPK(Player player) {
        Connection conPK = null;
        PreparedStatement statementPK = null;
        ResultSet rsPK = null;
        nCounter = 0;
        try {
            conPK = DatabaseFactory.getInstance().getConnection();
            statementPK = conPK.prepareStatement("SELECT char_name, pkkills, online FROM characters ORDER BY pkkills DESC LIMIT 10;");
            rsPK = statementPK.executeQuery();

            while (rsPK.next()) {
                if (!rsPK.getString("char_name").isEmpty()) {
                    pbBStats.asCharNameTopPk[nCounter] = rsPK.getString("char_name");
                    pbBStats.anCharPkCount[nCounter] = rsPK.getInt("pkkills");
                    pbBStats.anCharPkOnline[nCounter] = rsPK.getInt("online");
                } else {
                    pbBStats.asCharNameTopPk[nCounter] = (player.isLangRus() ? "Нет" : "No");
                    pbBStats.anCharPkCount[nCounter] = 0;
                    pbBStats.anCharPkOnline[nCounter] = 0;
                }
                nCounter += 1;
            }
        } catch (Exception e) {
        } finally {
            DbUtils.closeQuietly(conPK, statementPK, rsPK);
        }
    }

    public void selectTopPVP(Player player) {
        Connection conPVP = null;
        PreparedStatement statementPVP = null;
        ResultSet rsPVP = null;
        nCounter = 0;
        try {
            conPVP = DatabaseFactory.getInstance().getConnection();
            statementPVP = conPVP.prepareStatement("SELECT char_name, pvpkills, online FROM characters ORDER BY pvpkills DESC LIMIT 10;");
            rsPVP = statementPVP.executeQuery();

            while (rsPVP.next()) {
                if (!rsPVP.getString("char_name").isEmpty()) {
                    pbBStats.asCharNameTopPvP[nCounter] = rsPVP.getString("char_name");
                    pbBStats.anCharPvPCount[nCounter] = rsPVP.getInt("pvpkills");
                    pbBStats.anCharPvPOnline[nCounter] = rsPVP.getInt("online");
                } else {
                    pbBStats.asCharNameTopPvP[nCounter] = (player.isLangRus() ? "Нет" : "No");
                    pbBStats.anCharPvPCount[nCounter] = 0;
                    pbBStats.anCharPvPOnline[nCounter] = 0;
                }
                nCounter += 1;
            }
        } catch (Exception e) {
        } finally {
            DbUtils.closeQuietly(conPVP, statementPVP, rsPVP);
        }
    }

    /**
     * Не используемый, но вызываемый метод имплемента
     */
    @Override
    public void onWriteCommand(Player player, String bypass, String arg1, String arg2, String arg3, String arg4, String arg5) {
    }
}
package l2p.gameserver.instancemanager;

import java.io.*;
import java.net.URL;
import java.util.Calendar;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import l2p.gameserver.Config;
import l2p.gameserver.ThreadPoolManager;
import l2p.gameserver.cache.Msg;
import l2p.gameserver.dao.CharacterDAO;
import l2p.gameserver.dao.VoteManagerDAO;
import l2p.gameserver.model.Player;
import l2p.gameserver.model.World;
import l2p.gameserver.model.items.ItemInstance;
import l2p.gameserver.model.mail.Mail;
import l2p.gameserver.serverpackets.ExNoticePostArrived;
import l2p.gameserver.utils.ItemFunctions;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * User: Mifesto Date: 18:47/14.12.12 Team: http://team.l2p.ru
 */
public class L2TopManager {

    private static final Logger _log = LoggerFactory.getLogger(L2TopManager.class);
    private static L2TopManager _instance;

    public static boolean isActive() {
        return Config.L2TOP_MANAGER_ENABLED && !Config.L2TOP_SERVER_ADDRESS.equalsIgnoreCase("localhost");
    }

    public static L2TopManager getInstance() {
        if (_instance == null) {
            _instance = new L2TopManager();
        }
        return _instance;
    }

    public L2TopManager() {
        ThreadPoolManager.getInstance().scheduleAtFixedRate(new Start(), Config.L2TOP_MANAGER_INTERVAL, Config.L2TOP_MANAGER_INTERVAL);
    }

    private class Start implements Runnable {

        @Override
        public void run() {
            VoteManagerDAO.getInstance().onClean("l2top");
            parse(Config.L2TOP_WEB_ADDRESS, false);
            parse(Config.L2TOP_SMS_ADDRESS, true);
        }
    }

    public void parse(String _url, boolean isSms) {
        try {
            String nick = "";
            String voteList[] = getInfo(_url);

            ArrayUtils.reverse(voteList);

            for (String vote : voteList) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                if (vote.startsWith("" + year)) {
                    try {
                        StringTokenizer st = new StringTokenizer(vote, "\t -:");

                        int years = Integer.parseInt(st.nextToken());
                        int month = Integer.parseInt(st.nextToken()) - 1;
                        int day = Integer.parseInt(st.nextToken());
                        int hour = Integer.parseInt(st.nextToken());
                        int minute = Integer.parseInt(st.nextToken());
                        int second = Integer.parseInt(st.nextToken());

                        cal.set(Calendar.YEAR, years);
                        cal.set(Calendar.MONTH, month);
                        cal.set(Calendar.DAY_OF_MONTH, day);
                        cal.set(Calendar.HOUR_OF_DAY, hour);
                        cal.set(Calendar.MINUTE, minute);
                        cal.set(Calendar.SECOND, second);
                        cal.set(Calendar.MILLISECOND, 0);

                        long voteTime = cal.getTimeInMillis() / 1000;

                        cal = Calendar.getInstance();
                        cal.set(1, year);
                        cal.set(2, month + 1);
                        cal.set(5, day);
                        cal.set(11, hour);
                        cal.set(12, minute);
                        cal.set(13, second);
                        cal.set(14, 0);

                        long voteTime2 = cal.getTimeInMillis() / 1000;

                        nick = st.nextToken();
                        if (st.hasMoreTokens()) {
                            nick = nick.concat(" ").concat(st.nextToken());
                        }

                        int mult = 1;
                        if (isSms) {
                            mult = Integer.parseInt(new StringBuffer(st.nextToken()).delete(0, 1).toString());
                        }

                        if (Config.L2TOP_SERVER_PREFIX.length() > 0) {
                            if (nick.startsWith(Config.L2TOP_SERVER_PREFIX)) {
                                nick = nick.substring(Config.L2TOP_SERVER_PREFIX.length() + 1);
                            }
                        }

                        giveItem(nick, voteTime, voteTime2, mult);
                    } catch (NoSuchElementException nsee) {
                        nsee.printStackTrace();
                    }
                }
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    private static String[] getInfo(String address) {
        StringBuilder buf = new StringBuilder();

        try {
            BufferedReader in = new BufferedReader(new InputStreamReader((new URL(address)).openStream(), "Cp1251"));
            for (String line = in.readLine(); line != null; line = in.readLine()) {
                buf.append(line);
                buf.append("\r\n");
            }
        } catch (IOException e) {
            buf.append(e.getStackTrace());
        }

        return buf.toString().split("\r\n");
    }

    private void giveItem(String name, long time, long time2, int mult) {
        int objectId = 0;

        if (VoteManagerDAO.getInstance().isTimeOut(name, time2, "l2top") && (objectId = CharacterDAO.getInstance().getObjectIdByName(name)) != 0) {
            Mail mail = new Mail();

            mail.setSenderId(1);
            mail.setSenderName("System Message");
            mail.setReceiverId(objectId);
            mail.setReceiverName(name);
            mail.setTopic("Бонус за голосование в рейтинге L2TOP.RU");
            mail.setBody("Спасибо за Ваш голос в рейтинге L2TOP.RU. C наилучшими пожеланиями " + Config.L2TOP_SERVER_ADDRESS);
            mail.setPrice(0);
            mail.setUnread(true);

            for (int i = 0; i < Config.L2TOP_REWARD.length; i += 2) {

                ItemInstance item = ItemFunctions.createItem(Config.L2TOP_REWARD[i]);
                item.setLocation(ItemInstance.ItemLocation.MAIL);
                item.setCount(Config.L2TOP_REWARD[i + 1] * mult);
                item.save();
                mail.addAttachment(item);
            }
            mail.setType(Mail.SenderType.NEWS_INFORMER);
            mail.setExpireTime(Config.L2TOP_SAVE_DAYS * 86400 + (int) time);

            mail.save();

            Player player = World.getPlayer(name);
            if (player != null) {
                player.sendPacket(ExNoticePostArrived.STATIC_TRUE);
                player.sendPacket(Msg.THE_MAIL_HAS_ARRIVED);
            }

            VoteManagerDAO.getInstance().onSave(name, time2, "l2top");
        }
    }
}

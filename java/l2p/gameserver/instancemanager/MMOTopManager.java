package l2p.gameserver.instancemanager;

import l2p.gameserver.Config;
import l2p.gameserver.ThreadPoolManager;
import l2p.gameserver.model.Player;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Calendar;
import java.util.StringTokenizer;
import l2p.gameserver.cache.Msg;
import l2p.gameserver.dao.CharacterDAO;
import l2p.gameserver.dao.VoteManagerDAO;
import l2p.gameserver.model.World;
import l2p.gameserver.model.items.ItemInstance;
import l2p.gameserver.model.mail.Mail;
import l2p.gameserver.serverpackets.ExNoticePostArrived;
import l2p.gameserver.utils.ItemFunctions;

public class MMOTopManager {

    BufferedReader reader;

    private static MMOTopManager _instance;

    public static boolean isActive() {
        return Config.MMOTOP_MANAGER_ENABLED && !Config.MMOTOP_WEB_ADDRESS.equalsIgnoreCase("localhost");
    }

    public static MMOTopManager getInstance() {
        if (_instance == null && Config.MMOTOP_MANAGER_ENABLED) {
            _instance = new MMOTopManager();
        }
        return _instance;
    }

    public MMOTopManager() {
        VoteManagerDAO.getInstance().onClean("mmotop");
        ThreadPoolManager.getInstance().scheduleAtFixedRate(new Start(), Config.MMOTOP_MANAGER_INTERVAL, Config.MMOTOP_MANAGER_INTERVAL);
    }

    public void getPage(String address) {
        try {
            URL url = new URL(address);
            reader = new BufferedReader(new InputStreamReader(url.openStream(), "UTF8"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void parse() {
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                StringTokenizer st = new StringTokenizer(line, "\t. :");
                while (st.hasMoreTokens()) {
                    try {
                        st.nextToken();
                        int day = Integer.parseInt(st.nextToken());
                        int month = Integer.parseInt(st.nextToken()) - 1;
                        int year = Integer.parseInt(st.nextToken());
                        int hour = Integer.parseInt(st.nextToken());
                        int minute = Integer.parseInt(st.nextToken());
                        int second = Integer.parseInt(st.nextToken());
                        st.nextToken();
                        st.nextToken();
                        st.nextToken();
                        st.nextToken();
                        String charName = st.nextToken();
                        int mult = Integer.parseInt(st.nextToken());

                        Calendar calendar = Calendar.getInstance();
                        calendar.set(1, year);
                        calendar.set(2, month);
                        calendar.set(5, day);
                        calendar.set(11, hour);
                        calendar.set(12, minute);
                        calendar.set(13, second);
                        calendar.set(14, 0);

                        long voteTime = calendar.getTimeInMillis() / 1000;
                        
                        calendar = Calendar.getInstance();
                        calendar.set(1, year);
                        calendar.set(2, month + 1);
                        calendar.set(5, day);
                        calendar.set(11, hour);
                        calendar.set(12, minute);
                        calendar.set(13, second);
                        calendar.set(14, 0);
                        
                        long voteTime2 = calendar.getTimeInMillis() / 1000;

                        giveItem(charName, voteTime, voteTime2, mult);
                    } catch (Exception e) {
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void giveItem(String name, long time, long time2, int mult) {
        int objectId = 0;

        if (VoteManagerDAO.getInstance().isTimeOut(name, time2, "mmotop") && (objectId = CharacterDAO.getInstance().getObjectIdByName(name)) != 0) {
            Mail mail = new Mail();

            mail.setSenderId(1);
            mail.setSenderName("System Message");
            mail.setReceiverId(objectId);
            mail.setReceiverName(name);
            mail.setTopic("Бонус за голосование в рейтинге MMOTOP.RU");
            mail.setBody("Спасибо за Ваш голос в рейтинге MMOTOP.RU. C наилучшими пожеланиями " + Config.MMOTOP_SERVER_ADDRESS);
            mail.setPrice(0);
            mail.setUnread(true);

            for (int i = 0; i < Config.MMOTOP_REWARD.length; i += 2) {

                ItemInstance item = ItemFunctions.createItem(Config.MMOTOP_REWARD[i]);
                item.setLocation(ItemInstance.ItemLocation.MAIL);
                item.setCount(Config.MMOTOP_REWARD[i + 1] * mult);
                item.save();
                mail.addAttachment(item);
            }
            mail.setType(Mail.SenderType.NEWS_INFORMER);
            mail.setExpireTime(Config.MMOTOP_SAVE_DAYS * 86400 + (int) time);

            mail.save();

            Player player = World.getPlayer(name);
            if (player != null) {
                player.sendPacket(ExNoticePostArrived.STATIC_TRUE);
                player.sendPacket(Msg.THE_MAIL_HAS_ARRIVED);
            }

            VoteManagerDAO.getInstance().onSave(name, time2, "mmotop");
        }
    }

    private class Start implements Runnable {

        @Override
        public void run() {
            getPage(Config.MMOTOP_WEB_ADDRESS);
            parse();
        }
    }
}

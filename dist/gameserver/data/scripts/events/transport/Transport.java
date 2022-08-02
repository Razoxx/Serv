package events.transport;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.LineNumberReader;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;

import l2p.gameserver.listener.actor.ai.OnAiEventListener;
import l2p.gameserver.scripts.Functions;
import l2p.gameserver.scripts.ScriptFile;
import l2p.gameserver.Announcements;
import l2p.gameserver.Config;
import l2p.gameserver.ai.AbstractAI;
import l2p.gameserver.ai.CtrlEvent;
import l2p.gameserver.cache.Msg;
import l2p.gameserver.data.htm.HtmCache;
import l2p.gameserver.geodata.GeoEngine;
import l2p.gameserver.listener.actor.player.OnPlayerEnterListener;
import l2p.gameserver.model.Creature;
import l2p.gameserver.model.Player;
import l2p.gameserver.model.actor.listener.CharListenerList;
import l2p.gameserver.model.actor.listener.PlayerListenerList;
import l2p.gameserver.model.instances.NpcInstance;
import static l2p.gameserver.scripts.Functions.IsActive;
import static l2p.gameserver.scripts.Functions.SetActive;
import l2p.gameserver.stats.Stats;
import l2p.gameserver.stats.funcs.FuncOwner;
import l2p.gameserver.stats.funcs.FuncSet;
import l2p.gameserver.tables.PetDataTable;
import l2p.gameserver.utils.GArray;
import l2p.gameserver.utils.Location;
import l2p.gameserver.utils.PositionUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Transport extends Functions implements ScriptFile, OnPlayerEnterListener {

    private static final Logger _log = LoggerFactory.getLogger(Transport.class);
    private static HashMap<String, Wyvern> wyverns;
    private static ConcurrentHashMap<Integer, Rider> _riders = new ConcurrentHashMap<Integer, Rider>();
    private static NotifyEventListener _notifyEventListener = new NotifyEventListener();
    private static boolean _active = false;

    /**
     * Читает статус эвента из базы.
     *
     * @return
     */
    private static boolean isActive() {
        return IsActive("transport");
    }

    @Override
    public void onLoad() {
        CharListenerList.addGlobal(this);
        if (isActive()) {
            _active = true;
            loadWyvernPath();
            _log.info("Loaded Event: Transport [state: activated]");
        } else {
            wyverns = null;
            _log.info("Loaded Event: Transport [state: deactivated]");
        }
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    /**
     * Запускает эвент
     */
    public void startEvent() {
        Player player = (Player) getSelf();
        if (!player.getPlayerAccess().IsEventGm) {
            return;
        }

        if (SetActive("SavingSnowman", true)) {
            loadWyvernPath();
            System.out.println("Event 'Transport' started.");
            Announcements.getInstance().announceByCustomMessage("scripts.events.transport.AnnounceEventStarted", null);
        } else {
            player.sendMessage(player.isLangRus() ? "Ивент 'Transport' уже запущен." : "Event 'Transport' already started.");
        }

        _active = true;

        show("admin/events/events.htm", player);
    }

    /**
     * Останавливает эвент
     */
    public void stopEvent() {
        Player player = (Player) getSelf();
        if (!player.getPlayerAccess().IsEventGm) {
            return;
        }
        if (SetActive("SavingSnowman", false)) {
            wyverns = null;
            System.out.println("Event 'Transport' stopped.");
            Announcements.getInstance().announceByCustomMessage("scripts.events.transport.AnnounceEventStoped", null);
        } else {
            player.sendMessage(player.isLangRus() ? "Ивент 'Transport' не запущен." : "Event 'Transport' not started.");
        }

        _active = false;

        show("admin/events/events.htm", player);
    }

    private void loadWyvernPath() {
        LineNumberReader lnr = null;
        wyverns = new HashMap<String, Wyvern>();
        try {
            File wyvernData = new File(Config.DATAPACK_ROOT + "/data/xml/events/transport/wyvernpath.csv");
            lnr = new LineNumberReader(new BufferedReader(new FileReader(wyvernData)));
            String line = null;
            while ((line = lnr.readLine()) != null) {
                if (line.trim().length() == 0 || line.startsWith("#")) {
                    continue;
                }
                Wyvern W = new Wyvern();
                W.parseLine(line);
                wyverns.put(W.name, W);
            }
        } catch (Exception e) {
        } finally {
            try {
                if (lnr != null) {
                    lnr.close();
                }
            } catch (Exception e1) { /* ignore problems */

            }
        }
    }

    @Override
    public void onPlayerEnter(Player player) {
        if (_active) {
            Announcements.getInstance().announceToPlayerByCustomMessage(player, "scripts.events.transport.AnnounceEventStarted", null);
        }
    }

    public class Wyvern implements FuncOwner {

        public GArray<Location> path;
        public String name;

        public void parseLine(String line) {
            path = new GArray<Location>();
            StringTokenizer st = new StringTokenizer(line, " ");
            name = st.nextToken();
            while (st.hasMoreTokens()) {
                Location point = null;
                String token = st.nextToken();
                StringTokenizer points = new StringTokenizer(token, ";");
                if (token.startsWith("t")) {
                    points.nextToken();
                    point = new Location(Integer.parseInt(points.nextToken()), Integer.parseInt(points.nextToken()), Integer.parseInt(points.nextToken()), -1);
                } else {
                    point = new Location(Integer.parseInt(points.nextToken()), Integer.parseInt(points.nextToken()), Integer.parseInt(points.nextToken()));
                }
                //point.setZ(point.z + 250);
                point.setZ(Math.max(GeoEngine.getHeight(point.x, point.y, point.z + 1000, 0) + 1000, point.z));
                //point.setZ(Math.max(GeoEngine.getHeight(point.setZ(point.z + 1000), 0) + 1000, point.z));
                if (!path.isEmpty()) {
                    Location previous = path.get(path.size() - 1);
                    double len = PositionUtils.calculateDistance(point.x, point.y, point.z, previous.x, previous.y, previous.z, true);
                    if (len > 2000) {
                        double steps = Math.ceil(len / 2000.);
                        for (int i = 1; i < steps; i++) {
                            Location loc = new Location((int) (previous.x + i * (point.x - previous.x) / steps), (int) (previous.y + i * (point.y - previous.y) / steps), (int) (previous.z + i * (point.z - previous.z) / steps));
                            loc.setZ(Math.max(GeoEngine.getHeight(loc.x, loc.y, loc.z + 1000, 0) + 1000, loc.z));
                            path.add(loc);
                        }
                    }
                }
                path.add(point);
            }
            Location last = path.get(path.size() - 1);
            last.setZ(GeoEngine.getHeight(last, 0) + 250);
        }

        @Override
        public boolean isFuncEnabled() {
            return true;
        }

        @Override
        public boolean overrideLimits() {
            return true;
        }
    }

    public class Rider {

        public Wyvern W;
        public Player P;
        public Stack<Location> way;
    }

    public void HireWyvern(String[] param) {
        if (param.length < 2) {
            throw new IllegalArgumentException();
        }

        if (!_active) {
            return;
        }

        loadWyvernPath();

        if (wyverns == null) {
            return;
        }

        Player player = (Player) getSelf();

        int price = Integer.parseInt(param[1]);

        int day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        if (day != 1 && day != 7 && (hour <= 12 || hour >= 22)) {
            price /= 2;
        }

        if (player.isMounted() || !NpcInstance.canBypassCheck(player, player.getLastNpc())) {
            return;
        }

        if (player.getPet() != null || player.getTransformation() != 0) {
            player.sendPacket(Msg.YOU_CANNOT_MOUNT_BECAUSE_YOU_DO_NOT_MEET_THE_REQUIREMENTS);
            return;
        }

        if (player.getAdena() < price) {
            player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
            return;
        }

        if (player.isInOlympiadMode()) {
            player.sendMessage(player.isLangRus() ? "Вы участвуете в олимпиаде!" : "You are participating in the olympiad!");
            return;
        }

        if (price > 0) {
            player.reduceAdena(price, true);
        }

        player._stablePoint = player.getLoc().setH(price);
        player.setVar("wyvern_moneyback", String.valueOf(price), -1);

        Wyvern W = wyverns.get(param[0]);

        Rider r = new Rider();
        r.P = player;
        r.W = W;
        r.way = new Stack<Location>();
        r.way.addAll(W.path);
        _riders.put(player.getObjectId(), r);

        player.setHeading(0);
        player.validateLocation(1);
        player.setMount(PetDataTable.WYVERN_ID, 0, 0);
   //     player.block();
        player.setIsInvul(true);
        player.addStatFunc(new FuncSet(Stats.RUN_SPEED, 0x90, W, 300));

        PlayerListenerList.addGlobal(_notifyEventListener);
        flyNext(r);
        player.broadcastUserInfo(true);
    }

    public static class NotifyEventListener implements OnAiEventListener {

        public void NotifyEvent(AbstractAI ai, CtrlEvent evt, Object[] args) {
            if (evt == CtrlEvent.EVT_ARRIVED || evt == CtrlEvent.EVT_TELEPORTED) {
                if (ai == null) {
                    return;
                }
                Creature actor = ai.getActor();
                if (actor == null) {
                    return;
                }
                Rider r = _riders.get(actor.getObjectId());
                if (r == null) {
                    return;
                }
                flyNext(r);
            }
        }

        public void addMethodInvokedListener(
                NotifyEventListener _notifyEventListener) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onAiEvent(Creature actor, CtrlEvent evt, Object[] args) {
            // TODO Auto-generated method stub
        }

        public void removeMethodInvokedListener(
                NotifyEventListener _notifyEventListener) {
            // TODO Auto-generated method stub
        }
    }

    private static void flyNext(final Rider r) {
        if (!r.way.empty()) {
            // летим в следующую точку
            Location next = r.way.remove(0);
            if (r.P.getLastClientPosition() != null && PositionUtils.getDistance(r.P.getLastClientPosition().x, r.P.getLastClientPosition().y, r.P.getX(), r.P.getY()) > 500) {
                r.P.validateLocation(1);
            }
            if (next.h == -1 || !r.P.moveToLocation(next, 0, false)) {
                r.P.teleToLocation(next);
            }
        } else // прилетели
        {
            cancel(r, false);
        }
    }

    private static void cancel(Rider r, boolean moneyback) {
        if (moneyback) {
            r.P.teleToLocation(r.P._stablePoint);
            Functions.addItem(r.P, 57, Integer.parseInt(r.P.getVar("wyvern_moneyback")));
        }
        r.P.setMount(0, 0, 0);
        r.P._stablePoint = null;
        r.P.unsetVar("wyvern_moneyback");
        r.P.removeStatsByOwner(r.W);
        r.P.setLastServerPosition(null);
        r.P.setLastClientPosition(null);
        r.P.setIsInvul(false);
        r.P.unblock();
        PlayerListenerList.removeGlobal(_notifyEventListener);
        _riders.values().remove(r);
        r.P.broadcastUserInfo(true);
    }

    public static NotifyEventListener getListenerEngine() {
        return _notifyEventListener;
    }

    public String DialogAppend_31212(Integer val) {
        if (_active && val == 0) {
            Player player = (Player) getSelf();

            return HtmCache.getInstance().getHtml("scripts/events/transport/31212.htm", player);
        }
        return "";
    }

    public String DialogAppend_31213(Integer val) {
        if (_active && val == 0) {
            Player player = (Player) getSelf();

            return HtmCache.getInstance().getHtml("scripts/events/transport/31213.htm", player);
        }
        return "";
    }

    public String DialogAppend_31214(Integer val) {
        if (_active && val == 0) {
            Player player = (Player) getSelf();

            return HtmCache.getInstance().getHtml("scripts/events/transport/31214.htm", player);
        }
        return "";
    }

    public String DialogAppend_31215(Integer val) {
        if (_active && val == 0) {
            Player player = (Player) getSelf();

            return HtmCache.getInstance().getHtml("scripts/events/transport/31215.htm", player);
        }
        return "";
    }

    public String DialogAppend_31216(Integer val) {
        if (_active && val == 0) {
            Player player = (Player) getSelf();

            return HtmCache.getInstance().getHtml("scripts/events/transport/31216.htm", player);
        }
        return "";
    }

    public String DialogAppend_31217(Integer val) {
        if (_active && val == 0) {
            Player player = (Player) getSelf();

            return HtmCache.getInstance().getHtml("scripts/events/transport/31217.htm", player);
        }
        return "";
    }

    public String DialogAppend_31218(Integer val) {
        if (_active && val == 0) {
            Player player = (Player) getSelf();

            return HtmCache.getInstance().getHtml("scripts/events/transport/31218.htm", player);
        }
        return "";
    }

    public String DialogAppend_31219(Integer val) {
        if (_active && val == 0) {
            Player player = (Player) getSelf();

            return HtmCache.getInstance().getHtml("scripts/events/transport/31219.htm", player);
        }
        return "";
    }

    public String DialogAppend_31220(Integer val) {
        if (_active && val == 0) {
            Player player = (Player) getSelf();

            return HtmCache.getInstance().getHtml("scripts/events/transport/31220.htm", player);
        }
        return "";
    }

    public String DialogAppend_31221(Integer val) {
        if (_active && val == 0) {
            Player player = (Player) getSelf();

            return HtmCache.getInstance().getHtml("scripts/events/transport/31221.htm", player);
        }
        return "";
    }

    public String DialogAppend_31222(Integer val) {
        if (_active && val == 0) {
            Player player = (Player) getSelf();

            return HtmCache.getInstance().getHtml("scripts/events/transport/31222.htm", player);
        }
        return "";
    }

    public String DialogAppend_31223(Integer val) {
        if (_active && val == 0) {
            Player player = (Player) getSelf();

            return HtmCache.getInstance().getHtml("scripts/events/transport/31223.htm", player);
        }
        return "";
    }

    public String DialogAppend_31224(Integer val) {
        if (_active && val == 0) {
            Player player = (Player) getSelf();

            return HtmCache.getInstance().getHtml("scripts/events/transport/31224.htm", player);
        }
        return "";
    }

    public String DialogAppend_31767(Integer val) {
        if (_active && val == 0) {
            Player player = (Player) getSelf();

            return HtmCache.getInstance().getHtml("scripts/events/transport/31767.htm", player);
        }
        return "";
    }

    public String DialogAppend_31768(Integer val) {
        if (_active && val == 0) {
            Player player = (Player) getSelf();

            return HtmCache.getInstance().getHtml("scripts/events/transport/31768.htm", player);
        }
        return "";
    }

    public String DialogAppend_32048(Integer val) {
        if (_active && val == 0) {
            Player player = (Player) getSelf();

            return HtmCache.getInstance().getHtml("scripts/events/transport/32048.htm", player);
        }
        return "";
    }
}
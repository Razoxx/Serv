package l2p.gameserver.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import javolution.text.TextBuilder;
import javolution.util.FastList;
import javolution.util.FastMap;
import l2p.commons.dbutils.DbUtils;
import l2p.commons.util.Rnd;
import l2p.gameserver.Config;
import l2p.gameserver.ThreadPoolManager;
import l2p.gameserver.ai.CtrlIntention;
import l2p.gameserver.ai.PlayableAI;
import l2p.gameserver.database.DatabaseFactory;
import l2p.gameserver.geodata.GeoEngine;
import l2p.gameserver.model.base.InvisibleType;
import l2p.gameserver.model.base.Race;
import l2p.gameserver.model.instances.NpcInstance;
import l2p.gameserver.model.items.ItemInstance;
import l2p.gameserver.serverpackets.L2GameServerPacket;
import l2p.gameserver.serverpackets.MagicSkillUse;
import l2p.gameserver.serverpackets.Revive;
import l2p.gameserver.serverpackets.Say2;
import l2p.gameserver.serverpackets.components.ChatType;
import l2p.gameserver.skills.AbnormalEffect;
import l2p.gameserver.tables.SkillTable;
import l2p.gameserver.templates.PlayerTemplate;
import l2p.gameserver.templates.item.CreateItem;
import l2p.gameserver.utils.ItemFunctions;
import l2p.gameserver.utils.Location;
import org.apache.log4j.Logger;

public class PhantomPlayers {

    private static final Logger _log = Logger.getLogger(PhantomPlayers.class);
    private static String _phantomAcc = Config.PHANTOM_PLAYERS_AKK;
    private static int _PhantomsCount = 0;
    private static int _PhantomsLimit = 0;
    private static int _setsCount = 0;
    private static int _setsCountClan = 0;
    private volatile int _PhantomsTownTotal;
    private static int _nameColCount = 0;
    private static int _titleColCount = 0;
    private static FastList<Integer> _nameColors = new FastList();
    private static FastList<Integer> _titleColors = new FastList();
    private static FastList<L2Set> _sets = new FastList();
    private static int _setsArcherCount = 0;
    private static FastList<L2Set> _setsArcher = new FastList();
    private static PhantomPlayers _instance;
    private static int _setsOlyCount = 0;
    private static FastList<L2Set> _setsOly = new FastList();
    private static int _locsCount = 0;
    private static FastList<Location> _PhantomsTownLoc = new FastList();
    private static FastMap<Integer, L2Fantome> _phantoms = new FastMap();
    private static int _PhantomsEnchPhsCount = 0;
    private static FastList<String> _PhantomsEnchPhrases = new FastList();
    private static int _PhantomsLastPhsCount = 0;
    private static FastList<String> _PhantomsLastPhrases = new FastList();
    private static Map<Integer, ConcurrentLinkedQueue<Player>> _PhantomsTown = new ConcurrentHashMap();
    private static Map<Integer, ConcurrentLinkedQueue<Player>> _PhantomsTownClan = new ConcurrentHashMap();
    private static Map<Integer, ConcurrentLinkedQueue<Integer>> _PhantomsTownClanList = new ConcurrentHashMap();

    private static final int[][] _mageBuff = {{6, 75, 4322, 1}, {6, 75, 4323, 1}, {6, 75, 5637, 1}, {6, 75, 4328, 1}, {6, 75, 4329, 1}, {6, 75, 4330, 1}, {6, 75, 4331, 1}, {16, 34, 4338, 1}};

    private static final int[][] _warrBuff = {{6, 75, 4322, 1}, {6, 75, 4323, 1}, {6, 75, 5637, 1}, {6, 75, 4324, 1}, {6, 75, 4325, 1}, {6, 75, 4326, 1}, {6, 39, 4327, 1}, {40, 75, 5632, 1}, {16, 34, 4338, 1}};

    private static final int[] _buffers = {30598, 30599, 30600, 30601, 30602, 32135};

    public PhantomPlayers() {
        _PhantomsTownTotal = 0;
    }

    public static PhantomPlayers getInstance() {
        return _instance;
    }

    public static void init() {
        _instance = new PhantomPlayers();
        _instance.load();
    }

    private void load() {
        parceArmors();
        parceArcherArmors();
        parceOlyArmors();
        parceColors();
        cacheLastPhrases();
        if (Config.ALLOW_PHANTOM_PLAYERS) {
            parceTownLocs();
            parceTownClans();

            cacheFantoms();
            cacheEnchantPhrases();
            _PhantomsLimit = Config.PHANTOM_PLAYERS_COUNT_FIRST + Config.PHANTOM_PLAYERS_COUNT_NEXT + 10;
            _PhantomsTown.put(1, new ConcurrentLinkedQueue());
            _PhantomsTown.put(2, new ConcurrentLinkedQueue());
        }
    }

    private void cacheFantoms() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String name = "";
                String new_name = "";
                Connection con = null;
                PreparedStatement st = null;
                ResultSet rs = null;
                Player fantom = null;
                try {
                    con = DatabaseFactory.getInstance().getConnection();
                    con.setTransactionIsolation(1);
                    st = con.prepareStatement("SELECT obj_Id,char_name,title,x,y,z,clanid FROM characters WHERE account_name = ?");
                    st.setString(1, _phantomAcc);
                    rs = st.executeQuery();
                    rs.setFetchSize(250);
                    while (rs.next()) {
                        name = rs.getString("char_name");
                        _phantoms.put(rs.getInt("obj_Id"), new L2Fantome(name, rs.getString("title"), rs.getInt("x"), rs.getInt("y"), rs.getInt("z"), rs.getInt("clanid")));
                    }
                    
                } catch (SQLException e) {
                    _log.warn("PhantomPlayers: could not load chars from DB: " + e);
                } finally {
                    DbUtils.closeQuietly(con, st, rs);
                }
                if (!_phantoms.isEmpty()) {
                    ThreadPoolManager.getInstance().schedule(new FantomTask(1), Config.PHANTOM_PLAYERS_DELAY_FIRST * 1000L);
                }
            }
        }).start();
    }

    private void parceArmors() {
        if (!_sets.isEmpty()) {
            _sets.clear();
        }
        LineNumberReader lnr = null;
        BufferedReader br = null;
        FileReader fr = null;
        try {
            File Data = new File("./config/phantomold/town_sets.ini");
            if (!Data.exists()) {
                return;
            }
            fr = new FileReader(Data);
            br = new BufferedReader(fr);
            lnr = new LineNumberReader(br);
            String line;
            while ((line = lnr.readLine()) != null) {
                if ((line.trim().length() != 0) && (!line.startsWith("#"))) {
                    String[] items = line.split(",");
                    int custom = 0;
                    try {
                        custom = Integer.parseInt(items[5]);
                    } catch (NumberFormatException e) {
                        custom = 0;
                    }
                    _sets.add(new L2Set(Integer.parseInt(items[0]), Integer.parseInt(items[1]), Integer.parseInt(items[2]), Integer.parseInt(items[3]), Integer.parseInt(items[4]), Integer.parseInt(items[5]), custom));
                }
            }
            _setsCount = _sets.size() - 1;
            _log.info("Load " + _setsCount + " phantom armor sets");
        } catch (IOException | NumberFormatException e) {
        } finally {
            try {
                if (fr != null) {
                    fr.close();
                }
                if (br != null) {
                    br.close();
                }
                if (lnr != null) {
                    lnr.close();
                }
            } catch (IOException e) {
            }
        }
    }

    private void parceArcherArmors() {
        if (!_setsArcher.isEmpty()) {
            _setsArcher.clear();
        }
        LineNumberReader lnr = null;
        BufferedReader br = null;
        FileReader fr = null;
        try {
            File Data = new File("./config/phantomold/archer_sets.ini");
            if (!Data.exists()) {
                return;
            }
            fr = new FileReader(Data);
            br = new BufferedReader(fr);
            lnr = new LineNumberReader(br);
            String line;
            while ((line = lnr.readLine()) != null) {
                if ((line.trim().length() != 0) && (!line.startsWith("#"))) {
                    String[] items = line.split(",");
                    int custom = 0;
                    try {
                        custom = Integer.parseInt(items[5]);
                    } catch (NumberFormatException e) {
                        custom = 0;
                    }
                    _setsArcher.add(new L2Set(Integer.parseInt(items[0]), Integer.parseInt(items[1]), Integer.parseInt(items[2]), Integer.parseInt(items[3]), Integer.parseInt(items[4]), Integer.parseInt(items[5]), custom));
                }
            }
            _setsArcherCount = _setsArcher.size() - 1;
            _log.info("Load " + _setsArcherCount + " Aecher phantom armor sets");
        } catch (IOException | NumberFormatException e) {
        } finally {
            try {
                if (fr != null) {
                    fr.close();
                }
                if (br != null) {
                    br.close();
                }
                if (lnr != null) {
                    lnr.close();
                }
            } catch (IOException e) {
            }
        }
    }

    private void parceOlyArmors() {
        if (!_setsOly.isEmpty()) {
            _setsOly.clear();
        }
        LineNumberReader lnr = null;
        BufferedReader br = null;
        FileReader fr = null;
        try {
            File Data = new File("./config/phantomold/oly_sets.ini");
            if (!Data.exists()) {
                return;
            }
            fr = new FileReader(Data);
            br = new BufferedReader(fr);
            lnr = new LineNumberReader(br);
            String line;
            while ((line = lnr.readLine()) != null) {
                if ((line.trim().length() != 0) && (!line.startsWith("#"))) {
                    String[] items = line.split(",");
                    int custom = 0;
                    try {
                        custom = Integer.parseInt(items[6]);
                    } catch (NumberFormatException e) {
                        custom = 0;
                    }
                    _setsOly.add(new L2Set(Integer.parseInt(items[0]), Integer.parseInt(items[1]), Integer.parseInt(items[2]), Integer.parseInt(items[3]), Integer.parseInt(items[4]), Integer.parseInt(items[5]), custom));
                }
            }
            _setsOlyCount = _setsOly.size() - 1;
            _log.info("Load " + _setsOlyCount + " phantom Only armor sets");
        } catch (IOException | NumberFormatException e) {
        } finally {
            try {
                if (fr != null) {
                    fr.close();
                }
                if (br != null) {
                    br.close();
                }
                if (lnr != null) {
                    lnr.close();
                }
            } catch (IOException e) {
            }
        }
    }

    private void parceTownClans() {
        LineNumberReader lnr = null;
        BufferedReader br = null;
        FileReader fr = null;
        try {
            File Data = new File("./config/phantomold/town_clans.ini");
            if (!Data.exists()) {
                return;
            }
            fr = new FileReader(Data);
            br = new BufferedReader(fr);
            lnr = new LineNumberReader(br);
            int clanId = 0;
            String line;
            while ((line = lnr.readLine()) != null) {
                if ((line.trim().length() != 0) && (!line.startsWith("#"))) {
                    String[] items = line.split(":");
                    clanId = Integer.parseInt(items[0]);
                    String[] pls = items[1].split(",");
                    ConcurrentLinkedQueue players = new ConcurrentLinkedQueue();
                    for (String plid : pls) {
                        players.add(Integer.parseInt(plid));
                    }
                    _PhantomsTownClanList.put(clanId, players);
                }
            }
            _setsCountClan = _PhantomsTownClanList.size() - 1;
            _log.info("Load " + _setsCountClan + " phantom Clans");
        } catch (IOException | NumberFormatException e) {
        } finally {
            try {
                if (fr != null) {
                    fr.close();
                }
                if (br != null) {
                    br.close();
                }
                if (lnr != null) {
                    lnr.close();
                }
            } catch (IOException e1) {
            }
        }
    }

    private void parceTownLocs() {
        _PhantomsTownLoc.clear();
        LineNumberReader lnr = null;
        BufferedReader br = null;
        FileReader fr = null;
        try {
            File Data = new File("./config/phantomold/town_locs.ini");
            if (!Data.exists()) {
                return;
            }
            fr = new FileReader(Data);
            br = new BufferedReader(fr);
            lnr = new LineNumberReader(br);
            String line;
            while ((line = lnr.readLine()) != null) {
                if ((line.trim().length() != 0) && (!line.startsWith("#"))) {
                    String[] items = line.split(",");
                    _PhantomsTownLoc.add(new Location(Integer.parseInt(items[0]), Integer.parseInt(items[1]), Integer.parseInt(items[2])));
                }
            }
            _locsCount = _PhantomsTownLoc.size() - 1;
            _log.info("Load " + _locsCount + " phantom Town Locations");
        } catch (IOException | NumberFormatException e) {
        } finally {
            try {
                if (fr != null) {
                    fr.close();
                }
                if (br != null) {
                    br.close();
                }
                if (lnr != null) {
                    lnr.close();
                }
            } catch (IOException e1) {
            }
        }
    }

    public void startWalk(Player phantom) {
        ThreadPoolManager.getInstance().schedule(new PhantomWalk(phantom), 10000L);
    }

    private void cacheEnchantPhrases() {
        _PhantomsEnchPhrases.clear();
        LineNumberReader lnr = null;
        BufferedReader br = null;
        FileReader fr = null;
        try {
            File Data = new File("./config/phantomold/phrases_enchant.txt");
            if (!Data.exists()) {
                return;
            }
            fr = new FileReader(Data);
            br = new BufferedReader(fr);
            lnr = new LineNumberReader(br);
            String line;
            while ((line = lnr.readLine()) != null) {
                if ((line.trim().length() != 0) && (!line.startsWith("#"))) {
                    _PhantomsEnchPhrases.add(line);
                }
            }
            _PhantomsEnchPhsCount = _PhantomsEnchPhrases.size() - 1;
            _log.info("Load " + _PhantomsEnchPhsCount + " phantom Ench");
        } catch (IOException e) {
        } finally {
            try {
                if (fr != null) {
                    fr.close();
                }
                if (br != null) {
                    br.close();
                }
                if (lnr != null) {
                    lnr.close();
                }
            } catch (IOException e1) {
            }
        }
    }

    private void cacheLastPhrases() {
        _PhantomsLastPhrases.clear();
        LineNumberReader lnr = null;
        BufferedReader br = null;
        FileReader fr = null;
        try {
            File Data = new File("./config/phantomold/phrases_last.ini");
            if (!Data.exists()) {
                return;
            }
            fr = new FileReader(Data);
            br = new BufferedReader(fr);
            lnr = new LineNumberReader(br);
            String line;
            while ((line = lnr.readLine()) != null) {
                if ((line.trim().length() != 0) && (!line.startsWith("#"))) {
                    _PhantomsLastPhrases.add(line);
                }
            }
            _PhantomsLastPhsCount = _PhantomsLastPhrases.size() - 1;
            _log.info("Load " + _PhantomsLastPhsCount + " phantom Last");
        } catch (IOException e) {
        } finally {
            try {
                if (fr != null) {
                    fr.close();
                }
                if (br != null) {
                    br.close();
                }
                if (lnr != null) {
                    lnr.close();
                }
            } catch (IOException e1) {
            }
        }
    }

    private void parceColors() {
        _nameColors = Config.PHANTOM_PLAYERS_NAME_CLOLORS;
        _titleColors = Config.PHANTOM_PLAYERS_TITLE_CLOLORS;
        _nameColCount = _nameColors.size() - 1;
        _titleColCount = _titleColors.size() - 1;
    }

    private L2Set getRandomSet() {
        return _sets.get(Rnd.get(_setsCount));
    }

    private L2Set getRandomArcherSet() {
        return _setsArcher.get(Rnd.get(_setsArcherCount));
    }

    private int getRandomPhantom() {
        return Rnd.get(600000000, 600006081);
    }

    private int getRandomPhantomNext() {
        for (int i = 6; i > 0; i--) {
            int obj = Rnd.get(600000000, 600006081);
            if ((!((ConcurrentLinkedQueue) _PhantomsTown.get(1)).contains(obj)) && (!((ConcurrentLinkedQueue) _PhantomsTown.get(2)).contains(obj))) {
                return obj;
            }
        }
        return getRandomPhantomNext();
    }

    private int getRandomClan() {
        return Rnd.get(600000000, 600006081);
    }

    private Location getRandomLoc() {
        Location loc = null;
        if (loc == null) {
            loc = (Location) _PhantomsTownLoc.get(Rnd.get(0, _locsCount));
        }
        return loc;
    }

    private Player loadPhantom(int objId) {
        int nbPlayerIG = GameObjectsStorage.getAllPlayersCount();
        if (nbPlayerIG < Config.MAXIMUM_ONLINE_USERS) {
            L2Fantome phantom = (L2Fantome) _phantoms.get(Integer.valueOf(objId));
            if (phantom == null) {
                return null;
            }

            L2Set set = getRandomSet();
            ItemInstance body = null;
            ItemInstance gaiters = null;
            ItemInstance gloves = null;
            ItemInstance boots = null;
            ItemInstance weapon = null;
            if (set._body != 0) {
                body = ItemFunctions.createItem(set._body);
            }
            if (set._gaiters != 0) {
                gaiters = ItemFunctions.createItem(set._gaiters);
            }
            if (set._gloves != 0) {
                gloves = ItemFunctions.createItem(set._gloves);
            }
            if (set._boots != 0) {
                boots = ItemFunctions.createItem(set._boots);
            }
            if (set._weapon != 0) {
                weapon = ItemFunctions.createItem(set._weapon);
            }
            ItemInstance custom = null;
            int grade = set._grade;
            int setLevel = 1;
            int classId = 0;
            if (grade == 0) {
                setLevel = Rnd.get(1, 19);
            }
            if (grade == 1) {
                setLevel = Rnd.get(20, 39);
            }
            if (grade == 2) {
                setLevel = Rnd.get(40, 51);
            }
            if (grade == 3) {
                setLevel = Rnd.get(52, 60);
            }
            if (grade == 4) {
                setLevel = Rnd.get(61, 75);
            }
            if (grade == 5) {
                setLevel = Rnd.get(76, 80);
            }
            Player fantom = Player.restorePhantom(objId, setLevel, classId, false);

            fantom.setOfflineMode(false);
            fantom.setIsOnline(true);
            fantom.updateOnlineStatus();

            Location loc = getRandomLoc();
            fantom.setPhantomLoc(loc.getX(), loc.getY(), loc.getZ());
            fantom.setXYZ(loc.getX() + Rnd.get(60), loc.getY() + Rnd.get(60), loc.getZ());
            Location loc1 = new Location(loc.getX() + Rnd.get(350), loc.getY() + Rnd.get(350), loc.getZ());
            fantom.setOnlineStatus(true);
            fantom.setInvisibleType(InvisibleType.NONE);

            fantom.setNonAggroTime(9223372036854775807L);
            fantom.spawnMe(loc1);
            fantom.setCurrentHpMp(fantom.getMaxHp(), fantom.getMaxMp());
            fantom.setCurrentCp(fantom.getMaxCp());

            if ((Config.ALLOW_PHANTOM_SETS) && (fantom.getClassId().getRace() != Race.kamael)) {
                if (body != null) {
                    fantom.getInventory().addItem(body);
                    fantom.getInventory().equipItem(body);
                }
                if (gaiters != null) {
                    fantom.getInventory().addItem(gaiters);
                    fantom.getInventory().equipItem(gaiters);
                }
                if (gloves != null) {
                    fantom.getInventory().addItem(gloves);
                    fantom.getInventory().equipItem(gloves);
                }
                if (boots != null) {
                    fantom.getInventory().addItem(boots);
                    fantom.getInventory().equipItem(boots);
                }

                if (weapon != null) {
                    fantom.getInventory().addItem(weapon);
                    fantom.getInventory().equipItem(weapon);
                }
            }
            if ((!Config.ALLOW_PHANTOM_SETS) || (fantom.getClassId().getRace() == Race.kamael)) {
                PlayerTemplate template = fantom.getTemplate();
                for (CreateItem i : template.getItems()) {
                    ItemInstance item = ItemFunctions.createItem(i.getItemId());
                    fantom.getInventory().addItem(item);

                    if ((i.isEquipable()) && (item.isEquipable()) && ((fantom.getActiveWeaponItem() == null) || (item.getTemplate().getType2() != 0))) {
                        fantom.getInventory().equipItem(item);
                    }
                }
            }
            fantom.broadcastCharInfo();
            fantom.rndWalk();

            startWalk(fantom);
            return fantom;
        }
        return null;
    }

    private String getRandomEnchantPhrase() {
        return _PhantomsEnchPhrases.get(Rnd.get(_PhantomsEnchPhsCount));
    }

    public String getRandomLastPhrase() {
        return  _PhantomsLastPhrases.get(Rnd.get(_PhantomsLastPhsCount));
    }

    private int getNameColor() {
        return _nameColors.get(Rnd.get(_nameColCount));
    }

    private int getTitleColor() {
        return _titleColors.get(Rnd.get(_titleColCount));
    }

    public class Social
            implements Runnable {

        public Social() {
        }

        @Override
        public void run() {
            TextBuilder tb = new TextBuilder();
            for (Map.Entry<Integer, ConcurrentLinkedQueue<Player>> entry : _PhantomsTown.entrySet()) {
                Integer wave = entry.getKey();
                ConcurrentLinkedQueue<Player> players = entry.getValue();
                if ((wave != null) && (players != null) && (!players.isEmpty())) {
                    int count = 0;
                    for (Player player : players) {
                        if (Rnd.get(100) < 65) {
                            switch (Rnd.get(2)) {
                                case 0:
                                case 1:
                                    ItemInstance wpn = player.getActiveWeaponInstance();
                                    int enhchant = wpn.getEnchantLevel();
                                    int nextench = enhchant + 1;
                                    if ((Rnd.get(100) < 45) && (enhchant <= Config.PHANTOM_PLAYERS_ENCHANT_MAX)) {
                                        wpn.setEnchantLevel(nextench);
                                    } else if (Rnd.get(100) < 70) {
                                        wpn.setEnchantLevel(3);
                                        if ((nextench > 13) && (Rnd.get(100) < 2)) {
                                            tb.append("!");
                                            for (int i = Rnd.get(2, 13); i > 0; i--) {
                                                tb.append("!");
                                            }
                                            tb.clear();
                                        }
                                    }

                                    player.broadcastUserInfo(true);
                                    break;
                                case 2:
                                    if (Rnd.get(100) < 5) {
                                        player.moveToLocation(player.getX() + Rnd.get(30), player.getY() + Rnd.get(30), player.getZ(), 40, true);
                                        player.getAI().setNextAction(PlayableAI.nextAction.INTERACT, null, null, false, false);
                                    }
                                    break;
                            }
                            sleep(Rnd.get(500, 1500));
                            count++;
                        }
                        if (count > 55) {
                            break;
                        }
                    }
                }
            }
            tb.clear();
            ThreadPoolManager.getInstance().schedule(new Social(), 12000L);
        }
    }

    public class CheckCount
            implements Runnable {

        public CheckCount() {
        }

        @Override
        public void run() {
            for (Map.Entry<Integer, ConcurrentLinkedQueue<Player>> entry : _PhantomsTown.entrySet()) {
                Integer wave = entry.getKey();
                ConcurrentLinkedQueue<Player> players = entry.getValue();
                if ((wave != null) && (players != null) && (!players.isEmpty())) {
                    int limit = wave == 1 ? Config.PHANTOM_PLAYERS_COUNT_FIRST : Config.PHANTOM_PLAYERS_COUNT_NEXT;
                    int overflow = players.size() - limit;
                    if (overflow >= 1) {
                        for (Player fantom : players) {
                            fantom.setOnlineStatus(false);
                            ((ConcurrentLinkedQueue) _PhantomsTown.get(wave)).remove(fantom);
                            overflow--;
                            if (overflow == 0) {
                                break;
                            }
                        }
                    }
                }
            }
            ThreadPoolManager.getInstance().schedule(new CheckCount(), 300000L);
        }
    }

    public class FantomTaskDespawn
            implements Runnable {

        public int _task;

        public FantomTaskDespawn(int task) {
            _task = task;
        }

        @Override
        public void run() {
            ConcurrentLinkedQueue<Player> players = _PhantomsTown.get(_task);
            for (Player fantom : players) {
                if (fantom != null) {
                    Location loc = fantom.getPhantomLoc();

                    fantom.setOnlineStatus(false);
                    ((ConcurrentLinkedQueue) _PhantomsTown.get(_task)).remove(fantom);
                    sleep(_task == 1 ? Config.PHANTOM_PLAYERS_DELAY_DESPAWN_FIRST : Config.PHANTOM_PLAYERS_DELAY_DESPAWN_NEXT);
                    if (_PhantomsTownTotal <= _PhantomsLimit) {
                        int nextObjId = getRandomPhantomNext();
                        if (!((ConcurrentLinkedQueue) _PhantomsTown.get(_task)).contains(nextObjId)) {
                            Player next = loadPhantom(nextObjId);
                            if (next != null) {
                                ((ConcurrentLinkedQueue) _PhantomsTown.get(_task)).add(next);
                                if ((Config.PHANTOM_PLAYERS_SOULSHOT_ANIM) && (Rnd.get(100) < 45)) {
                                    sleep(900L);
                                    if (Rnd.get(100) < 3) {
                                        next.sitDown(null);
                                    }
                                    next.broadcastPacket(new MagicSkillUse(next, next, 2154, 1, 0, 0L));
                                    sleep(300L);
                                    next.broadcastPacket(new MagicSkillUse(next, next, 2164, 1, 0, 0L));
                                }
                                sleep(100L);
                            }
                        }
                    }
                }
            }
            ThreadPoolManager.getInstance().schedule(new FantomTaskDespawn(1), _task == 1 ? Config.PHANTOM_PLAYERS_DESPAWN_FIRST : Config.PHANTOM_PLAYERS_DESPAWN_NEXT);
        }
    }

    public class FantomTask
            implements Runnable {

        public int _task;

        public FantomTask(int task) {
            _task = task;
        }

        @Override
        public void run() {
            int count = 0;
            int count2 = 0;
            int PhantomObjId = 0;
            int PhantomObjId2 = 0;
            switch (_task) {
                case 1:
                    _log.info("PhantomPlayers: 1st wave, spawn started.");
                    while (count < Config.PHANTOM_PLAYERS_COUNT_FIRST) {
                        PhantomObjId = getRandomPhantomNext();
                        if (!((ConcurrentLinkedQueue) _PhantomsTown.get(1)).contains(PhantomObjId)) {
                            Player fantom = loadPhantom(PhantomObjId);
                            if (fantom != null) {
                                ((ConcurrentLinkedQueue) _PhantomsTown.get(1)).add(fantom);
                                if ((Config.PHANTOM_PLAYERS_SOULSHOT_ANIM) && (Rnd.get(100) < 45)) {
                                    sleep(900L);
                                    if (Rnd.get(100) < 5) {
                                        fantom.sitDown(null);
                                    }
                                    if (Rnd.get(100) < 30) {
                                        fantom.startAbnormalEffect(AbnormalEffect.S_NAVIT);
                                    }
                                    sleep(300L);
                                }
                                sleep(Config.PHANTOM_PLAYERS_DELAY_SPAWN_FIRST);
                                count++;
                            }
                        }
                    }
                    _log.info("FPhantomPlayers: 1st wave, spawned " + count + " players.");

                    ThreadPoolManager.getInstance().schedule(new FantomTaskDespawn(1), Config.PHANTOM_PLAYERS_DESPAWN_FIRST);
                    ThreadPoolManager.getInstance().schedule(new FantomTask(2), Config.PHANTOM_PLAYERS_DELAY_NEXT);
                    ThreadPoolManager.getInstance().schedule(new Social(), 12000L);
                    ThreadPoolManager.getInstance().schedule(new CheckCount(), 300000L);
                    break;
                case 2:
                    _log.info("PhantomPlayers: 2nd wave, spawn started.");
                    while (count2 < Config.PHANTOM_PLAYERS_COUNT_NEXT) {
                        PhantomObjId2 = getRandomPhantomNext();
                        if ((!((ConcurrentLinkedQueue) _PhantomsTown.get(1)).contains(PhantomObjId2)) || (!((ConcurrentLinkedQueue) _PhantomsTown.get(2)).contains(PhantomObjId2))) {
                            Player fantom2 = loadPhantom(PhantomObjId2);
                            if (fantom2 != null) {
                                ((ConcurrentLinkedQueue) _PhantomsTown.get(2)).add(fantom2);
                                if ((Config.PHANTOM_PLAYERS_SOULSHOT_ANIM) && (Rnd.get(100) < 45)) {
                                    sleep(900L);
                                    if (Rnd.get(100) < 3) {
                                        fantom2.sitDown(null);
                                    }
                                    fantom2.broadcastPacket(new L2GameServerPacket[]{new MagicSkillUse(fantom2, fantom2, 2154, 1, 0, 0L)});
                                    sleep(300L);
                                    fantom2.broadcastPacket(new L2GameServerPacket[]{new MagicSkillUse(fantom2, fantom2, 2164, 1, 0, 0L)});
                                }
                                sleep(Config.PHANTOM_PLAYERS_DELAY_SPAWN_NEXT);

                                count2++;
                            }
                        }
                    }
                    _log.info("PhantomPlayers: 2nd wave, spawned " + count2 + " players.");

                    ThreadPoolManager.getInstance().schedule(new FantomTaskDespawn(2), Config.PHANTOM_PLAYERS_DESPAWN_NEXT);
            }
        }
    }

    static class L2Fantome {

        public String name;
        public String title;
        public int x;
        public int y;
        public int z;
        public int clanId;

        L2Fantome(String name, String title, int x, int y, int z, int clanId) {
            this.name = name;
            this.title = title;
            this.x = x;
            this.y = y;
            this.z = z;
            this.clanId = clanId;
        }
    }

    static class L2Set {

        public int _body;
        public int _gaiters;
        public int _gloves;
        public int _boots;
        public int _weapon;
        public int _custom;
        public int _grade;

        L2Set(int bod, int gaiter, int glove, int boot, int weapon, int grade, int custom) {
            _body = bod;
            _gaiters = gaiter;
            _gloves = glove;
            _boots = boot;
            _weapon = weapon;
            _grade = grade;
            _custom = custom;
        }
    }

    private class PhantomWalk
            implements Runnable {

        Player _phantom;

        public PhantomWalk(Player phantom) {
            _phantom = phantom;
        }

        @Override
        public void run() {
            if (_phantom.isDead()) {
                _phantom.decayMe();

                Location loc = getRandomLoc();
                _phantom.setPhantomLoc(loc.getX(), loc.getY(), loc.getZ());
                _phantom.setXYZ(loc.getX() + Rnd.get(60), loc.getY() + Rnd.get(60), loc.getZ());
                Location loc1 = new Location(loc.getX() + Rnd.get(350), loc.getY() + Rnd.get(350), loc.getZ());
                _phantom.setOnlineStatus(true);
                _phantom.setInvisibleType(InvisibleType.NONE);

                _phantom.setNonAggroTime(9223372036854775807L);
                _phantom.spawnMe(loc1);
                _phantom.setCurrentHpMp(_phantom.getMaxHp(), _phantom.getMaxMp());
                _phantom.setCurrentCp(_phantom.getMaxCp());
                _phantom.broadcastUserInfo(true);
                _phantom.broadcastPacket(new Revive(_phantom));

            }
            if (_phantom.isInWater()) {
                _phantom.teleToClosestTown();
            }
            if ((Rnd.get(100) < 10)
                    && (Rnd.get(100) < 10)
                    && (_phantom.isSitting())) {
                _phantom.standUp();
            }
            if (Rnd.get(100) < 80) {
                for (NpcInstance npc : World.getAroundNpc(_phantom, 100, 100)) {
                    for (int buffers_id : _buffers) {
                        if ((npc.getNpcId() == buffers_id) && (!_phantom.isSitting())) {
                            if ((!_phantom.getClassId().isMage()) || (_phantom.getClassId().getRace() == Race.orc)) {
                                for (int[] buff : _warrBuff) {
                                    npc.broadcastPacket(new L2GameServerPacket[]{new MagicSkillUse(npc, _phantom, buff[2], buff[3], 0, 0L)});
                                }
                            }
                            if ((_phantom.getClassId().isMage()) && (_phantom.getClassId().getRace() != Race.orc)) {
                                for (int[] buff : _mageBuff) {
                                    npc.broadcastPacket(new L2GameServerPacket[]{new MagicSkillUse(npc, _phantom, buff[2], buff[3], 0, 0L)});
                                }
                            }
                        }
                    }
                }
            }
            Say2 cs3;
            if (Rnd.get(100) < 5) {
                Say2 cs;
                Say2 cs2;
                switch (Rnd.get(1, 3)) {
                    case 1:
                        cs = new Say2(_phantom.getObjectId(), ChatType.SHOUT, _phantom.getName(), getRandomEnchantPhrase());
                        for (Player player : World.getAroundPlayers(_phantom, 10000, 3000)) {
                            if ((player != null)
                                    && (!player.isBlockAll())) {
                                player.sendPacket(cs);
                            }
                        }
                        break;
                    case 2:
                        cs2 = new Say2(_phantom.getObjectId(), ChatType.TRADE, _phantom.getName(), getRandomEnchantPhrase());
                        for (Player player : World.getAroundPlayers(_phantom, 5000, 2000)) {
                            if ((player != null)
                                    && (!player.isBlockAll())) {
                                player.sendPacket(cs2);
                            }
                        }
                        break;
                    case 3:
                        cs3 = new Say2(_phantom.getObjectId(), ChatType.ALL, _phantom.getName(), getRandomEnchantPhrase());
                        for (Player player : World.getAroundPlayers(_phantom, 1200, 1000)) {
                            if ((player != null)
                                    && (!player.isBlockAll())) {
                                player.sendPacket(cs3);
                            }
                        }
                }
            }
            if (Rnd.get(100) < 10) {
                if ((!_phantom.getClassId().isMage()) || (_phantom.getClassId().getRace() == Race.orc)) {
                    _phantom.broadcastPacket(new L2GameServerPacket[]{new MagicSkillUse(_phantom, _phantom, 2153, 1, 0, 0L)});
                }
                if ((_phantom.getClassId().isMage()) && (_phantom.getClassId().getRace() != Race.orc)) {
                    _phantom.broadcastPacket(new L2GameServerPacket[]{new MagicSkillUse(_phantom, _phantom, 2158, 1, 0, 0L)});
                }
            }
            if (Rnd.get(100) < 70) {
                if ((!_phantom.getClassId().isMage()) || (_phantom.getClassId().getRace() == Race.orc)) {
                    for (Player player : World.getAroundPlayers(_phantom, 100, 100)) {
                        if ((GeoEngine.canSeeTarget(_phantom, player, false)) && (!player.isDead()) && (!player.isInZonePeace()) && (!_phantom.isInZonePeace()) && ((player.getKarma() != 0) || (player.getPvpFlag() > 0))) {
                            if (_phantom.getRealDistance3D(player) <= _phantom.getPhysicalAttackRange() + 40) {
                                _phantom.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, player);
                                sleep(500L);
                                if (_phantom.getClassId().getRace() == Race.human) {
                                    _phantom.doCast(SkillTable.getInstance().getInfo(3, 2), player, true);
                                }
                                if (_phantom.getClassId().getRace() == Race.elf) {
                                    _phantom.doCast(SkillTable.getInstance().getInfo(3, 2), player, true);
                                }
                                if (_phantom.getClassId().getRace() == Race.darkelf) {
                                    _phantom.doCast(SkillTable.getInstance().getInfo(3, 2), player, true);
                                }
                                if (_phantom.getClassId().getRace() == Race.orc) {
                                    _phantom.doCast(SkillTable.getInstance().getInfo(29, 2), player, true);
                                }
                                if (_phantom.getClassId().getRace() == Race.dwarf) {
                                    _phantom.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, player);
                                }
                                if (_phantom.getClassId().getRace() == Race.kamael) {
                                    _phantom.doCast(SkillTable.getInstance().getInfo(468, 2), player, true);
                                }
                                sleep(500L);
                                _phantom.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, player);
                            } else {
                                sleep(500L);
                            }
                            _phantom.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, player);
                        }
                    }
                }
                if ((_phantom.getClassId().isMage()) && (_phantom.getClassId().getRace() != Race.orc)) {
                    for (Player player : World.getAroundPlayers(_phantom, 100, 100)) {
                        if ((GeoEngine.canSeeTarget(_phantom, player, false)) && (!player.isDead()) && (!player.isInZonePeace()) && (!_phantom.isInZonePeace()) && ((player.getKarma() != 0) || (player.getPvpFlag() > 0))) {
                            _phantom.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, player);
                            _phantom.doCast(SkillTable.getInstance().getInfo(1177, 2), player, true);
                        }
                    }
                }
            }
            if (Rnd.get(100) < 10) {
                if ((!_phantom.getClassId().isMage()) || (_phantom.getClassId().getRace() == Race.orc)) {
                    for (NpcInstance npc : World.getAroundNpc(_phantom, 800, 200)) {
                        if ((GeoEngine.canSeeTarget(_phantom, npc, false)) && (npc.isMonster()) && (!npc.isDead())) {
                            _phantom.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, npc);
                        }
                    }
                }
                if ((_phantom.getClassId().isMage()) && (_phantom.getClassId().getRace() != Race.orc)) {
                    for (NpcInstance npc : World.getAroundNpc(_phantom, 800, 200)) {
                        if ((GeoEngine.canSeeTarget(_phantom, npc, false)) && (npc.isMonster()) && (!npc.isDead())) {
                            _phantom.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, npc);
                            _phantom.doCast(SkillTable.getInstance().getInfo(1177, 2), npc, true);
                        }
                    }
                }
            }
            if (Rnd.get(100) < 5) {
                if ((!_phantom.getClassId().isMage()) || (_phantom.getClassId().getRace() == Race.orc)) {
                    for (Player player : World.getAroundPlayers(_phantom, 600, 200)) {
                        if ((GeoEngine.canSeeTarget(_phantom, player, false)) && (!player.isDead()) && (!player.isInZonePeace()) && (!_phantom.isInZonePeace())) {
                            _phantom.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, player);
                        }
                    }
                }
                if ((_phantom.getClassId().isMage()) && (_phantom.getClassId().getRace() != Race.orc)) {
                    for (Player player : World.getAroundPlayers(_phantom, 600, 200)) {
                        if ((GeoEngine.canSeeTarget(_phantom, player, false)) && (!player.isDead()) && (!player.isInZonePeace()) && (!_phantom.isInZonePeace())) {
                            _phantom.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, player);
                            _phantom.doCast(SkillTable.getInstance().getInfo(1177, 2), player, true);
                        }
                    }
                }
            }
            if ((Rnd.get(100) < 10) && ((_phantom.getAI().getIntention() != CtrlIntention.AI_INTENTION_ATTACK) || (_phantom.getAI().getIntention() != CtrlIntention.AI_INTENTION_CAST))) {
                for (GameObject obj : World.getAroundObjects(_phantom, 800, 200)) {
                    if (obj.isItem()) {
                        _phantom.getAI().setIntention(CtrlIntention.AI_INTENTION_PICK_UP, obj);
                    }
                }
            }
            if ((_phantom.getAI().getIntention() != CtrlIntention.AI_INTENTION_ATTACK) || (_phantom.getAI().getIntention() != CtrlIntention.AI_INTENTION_CAST)) {
                _phantom.rndWalk();
            }
            startWalk(_phantom);
        }
    }

    public void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
        }
    }
}

package l2p.gameserver;

import gnu.trove.map.hash.TIntIntHashMap;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import javax.xml.parsers.DocumentBuilderFactory;
import javolution.text.TextBuilder;
import javolution.util.FastList;
import javolution.util.FastMap;
import l2p.commons.configuration.ExProperties;
import l2p.commons.net.AdvIP;
import l2p.commons.net.nio.impl.SelectorConfig;
import l2p.gameserver.data.htm.HtmCache;
import l2p.gameserver.loginservercon.ServerType;
import l2p.gameserver.model.AcademReward;
import l2p.gameserver.model.actor.instances.player.Bonus;
import l2p.gameserver.model.base.Experience;
import l2p.gameserver.model.base.PlayerAccess;
import l2p.gameserver.model.pcnpc.PcNpcConfig;
import l2p.gameserver.model.premium.PremiumConfig;
import l2p.gameserver.model.visual.VisualConfig;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class Config {

    private static final Logger _log = LoggerFactory.getLogger(Config.class);
    public static final int NCPUS = Runtime.getRuntime().availableProcessors();
    /**
     * Configuration files
     */
    public static final String OTHER_CONFIG_FILE = "config/Other.ini";
    public static final String RESIDENCE_CONFIG_FILE = "config/Residence.ini";
    public static final String SPOIL_CONFIG_FILE = "config/Spoil.ini";
    public static final String ALT_SETTINGS_FILE = "config/Altsettings.ini";
    public static final String FORMULAS_CONFIGURATION_FILE = "config/Formulas.ini";
    public static final String PVP_CONFIG_FILE = "config/Pvp.ini";
    public static final String TELNET_CONFIGURATION_FILE = "config/Telnet.ini";
    public static final String CONFIGURATION_FILE = "config/Server.ini";
    public static final String AI_CONFIG_FILE = "config/Ai.ini";
    public static final String GEODATA_CONFIG_FILE = "config/Geodata.ini";
    public static final String SERVICES_FILE = "config/Services.ini";
    public static final String OLYMPIAD = "config/Olympiad.ini";
    public static final String DEVELOP_FILE = "config/Develop.ini";
    public static final String LIC = "config/License.ini";
    public static final String EXT_FILE = "config/Ext.ini";
    public static final String RATES_FILE = "config/Rates.ini";
    public static final String WEDDING_FILE = "config/Wedding.ini";
    public static final String CHAT_FILE = "config/Chat.ini";
    public static final String CB_CONFIGURATION_FILE = "config/CommunityBoard.ini";
    public static final String NPC_FILE = "config/Npc.ini";
    public static final String BOSS_FILE = "config/Boss.ini";
    public static final String PREMIUM_FILE = "config/Premium.ini";
    public static final String TOP_FILE = "config/Tops.ini";
    public static final String EPIC_BOSS_FILE = "config/Epic.ini";
    public static final String PAYMENT_FILE = "config/Payment.ini";
    public static final String QUESTS_RATE = "config/quests_rate.ini";
    public static final String SKILL_FILE = "config/Skill.ini";
    public static final String ITEM_USE_FILE = "config/UseItems.ini";
    public static final String EVENT_GVG = "config/events/GvG.ini";
    public static final String EVENT_HITMAN = "config/events/Hitman.ini";
    public static final String EVENTS_CUSTOM_DROP_FILE = "config/events/CustomDrop.ini";
    public static final String EVENT_LAST_HERO_FILE = "config/events/LastHero.ini";
    public static final String EVENT_LUCK_NPC_FILE = "config/events/LuckNpc.ini";
    public static final String EVENT_PC_BANG_FILE = "config/events/PcBang.ini";
    public static final String EVENT_FIGHT_CLUB_FILE = "config/events/FightClub.ini";
    public static final String EVENT_BOUNTY_HUNTERS_FILE = "config/events/BountyHunters.ini";
    public static final String EVENT_CAPTURE_THE_FLAG_FILE = "config/events/CaptureTheFlag.ini";
    public static final String EVENT_L2_DAY_FILE = "config/events/L2Day.ini";
    public static final String EVENT_CHANGE_OF_HEART_FILE = "config/events/ChangeOfHeart.ini";
    public static final String EVENT_DEATH_MATCH = "config/events/DeathMatch.ini";
    public static final String EVENT_CHAMPION = "config/events/Champion.ini";
    public static final String EVENT_CUSTOM_DROP = "config/events/CustomDrop.ini";
    public static final String EVENT_GLITTERING_MEDAL_FILE = "config/events/GlitteringMedal.ini";
    public static final String EVENT_APRIL_FOOLS_FILE = "config/events/AprilFools.ini";
    public static final String LAKFI_CONFIG_FILE = "config/events/Lakfi.ini";
    public static final String EVENT_MASTER_OF_ENCHANING_FILE = "config/events/MasterOfEnchaning.ini";
    public static final String EVENT_MARCH_8_FILE = "config/events/March8.ini";
    public static final String EVENT_SAVING_SNOWMAN_FILE = "config/events/SavingSnowman.ini";
    public static final String EVENT_COFFER_OF_SHADOWS_FILE = "config/events/CofferOfShadows.ini";
    public static final String EVENT_THE_FALL_HARVEST_FILE = "config/events/TheFallHarvest.ini";
    public static final String DEFENSE_TOWNS_CONFIG_FILE = "config/events/DefenseTowns.ini";
    public static final String EVENT_TEAM_VS_TEAM_FILE = "config/events/TeamVSTeam.ini";
    public static final String EVENT_CHEST_EVIL = "config/events/ChestEvil.ini";
    public static final String EVENT_TREASURES_OF_THE_HERALD_FILE = "config/events/TreasuresOfTheHerald.ini";
    public static final String EVENT_TRICK_OF_TRANSMUTATION_FILE = "config/events/TrickOfTransmutation.ini";
    public static final String EVENT_TVT_ARENA_FILE = "config/events/TVTArena.ini";
    public static final String EVENT_UNDERGROUND_COLISEUM_FILE = "config/events/UndergroundColiseum.ini";
    public static final String INSTANCES_FILE = "config/Instances.ini";
    public static final String ITEMS_FILE = "config/Items.ini";
    public static final String ANUSEWORDS_CONFIG_FILE = "config/txt/Abusewords.txt";
    public static final String ADV_IP_FILE = "config/Advipsystem.ini";
    public static final String PHANTOM_FILE = "config/Phantoms.ini";
    public static final String GM_PERSONAL_ACCESS_FILE = "config/xml/GMAccess.xml";
    public static final String GM_ACCESS_FILES_DIR = "config/xml/GMAccess.d/";
    public static final String VERSION = "config/version.ini";
    public static final String BBSBUFFER_CONFIG_FILE = "config/CommunityBoardBuffer.ini";

    public static int HTM_CACHE_MODE;
    public static int WEB_SERVER_DELAY;
    public static String WEB_SERVER_ROOT;
    public static boolean ALLOW_IP_LOCK;
    public static boolean ALLOW_HWID_LOCK;
    public static int HWID_LOCK_MASK;
    /**
     * GameServer ports
     */
    public static int[] PORTS_GAME;
    public static String GAMESERVER_HOSTNAME;
    public static boolean ADVIPSYSTEM;
    public static List<AdvIP> GAMEIPS = new ArrayList<AdvIP>();
    public static String DATABASE_DRIVER;
    public static int DATABASE_MAX_CONNECTIONS;
    public static int DATABASE_MAX_IDLE_TIMEOUT;
    public static int DATABASE_IDLE_TEST_PERIOD;
    public static int DATABASE_PORT;
    public static String DATABASE_URL;
    public static String DATABASE_NAME;
    public static String DATABASE_LOGIN;
    public static String DATABASE_PASSWORD;
    // Database additional options
    public static boolean AUTOSAVE;
    public static long USER_INFO_INTERVAL;
    public static boolean BROADCAST_STATS_INTERVAL;
    public static long BROADCAST_CHAR_INFO_INTERVAL;
    public static int EFFECT_TASK_MANAGER_COUNT;
    public static int MAXIMUM_ONLINE_USERS;
    public static int ONLINE_PLUS;
    public static boolean DONTLOADSPAWN;
    public static boolean DONTLOADQUEST;
    public static int MAX_REFLECTIONS_COUNT;
    public static int SHIFT_BY;
    public static int SHIFT_BY_Z;
    public static int MAP_MIN_Z;
    public static int MAP_MAX_Z;
    /**
     * ChatBan
     */
    public static int CHAT_MESSAGE_MAX_LEN;
    public static boolean ABUSEWORD_BANCHAT;
    public static int[] BAN_CHANNEL_LIST = new int[18];
    public static boolean ABUSEWORD_REPLACE;
    public static String ABUSEWORD_REPLACE_STRING;
    public static int ABUSEWORD_BANTIME;
    public static Pattern[] ABUSEWORD_LIST = {};
    public static boolean BANCHAT_ANNOUNCE;
    public static boolean BANCHAT_ANNOUNCE_FOR_ALL_WORLD;
    public static boolean BANCHAT_ANNOUNCE_NICK;
    public static int[] CHATFILTER_CHANNELS = new int[18];
    public static int CHATFILTER_MIN_LEVEL = 0;
    public static int CHATFILTER_WORK_TYPE = 1;
    public static boolean SAVING_SPS;
    public static boolean MANAHEAL_SPS_BONUS;
    public static int ALT_ADD_RECIPES;
    public static int ALT_CLAN_RESTORE_DAY;
    public static int ALT_MAX_ALLY_SIZE;
    public static int ALT_MAX_PARTY_SIZE;
    public static int ALT_PARTY_DISTRIBUTION_RANGE;
    public static double[] ALT_PARTY_BONUS;
    public static double ALT_ABSORB_DAMAGE_MODIFIER;
    public static boolean ALT_ALL_PHYS_SKILLS_OVERHIT;
    public static double ALT_POLE_DAMAGE_MODIFIER;
    /**
     * Блокируем атаку если персонаж спрятался за текстуры
     */
    public static boolean ALT_DAMAGE_INVIS;
    /**
     * Блокируем атаку если персонаж спрятался за текстуры
     */
    public static boolean ALT_VISIBLE_SIEGE_IN_ICONS;

    public static boolean ALT_ENABLED_PRICE_ALL;
    public static int ALT_ENABLED_PRICE;
    public static int ALT_SELL_PRICE_PERCENT;

    public static boolean ALT_REMOVE_SKILLS_ON_DELEVEL;
    public static boolean ALT_USE_BOW_REUSE_MODIFIER;
    public static boolean ALT_VITALITY_ENABLED;
    public static double ALT_VITALITY_RATE;
    public static double ALT_VITALITY_CONSUME_RATE;
    public static int ALT_VITALITY_RAID_BONUS;
    public static final int[] VITALITY_LEVELS = {240, 2000, 13000, 17000, 20000};
    public static boolean CASTLE_GENERATE_TIME_ALTERNATIVE;
    public static int CASTLE_GENERATE_TIME_LOW;
    public static int CASTLE_GENERATE_TIME_HIGH;
    public static Calendar CASTLE_VALIDATION_DATE;
    public static int CASTLE_WEEK;
    public static int TW_WEEK;
    public static Calendar TW_VALIDATION_DATE;
    public static int[] CASTLE_SELECT_HOURS;
    public static int TW_SELECT_HOURS;
    public static boolean ALT_PCBANG_POINTS_ENABLED;
    public static double ALT_PCBANG_POINTS_BONUS_DOUBLE_CHANCE;
    public static int ALT_PCBANG_POINTS_BONUS;
    public static int ALT_PCBANG_POINTS_DELAY;
    public static int ALT_PCBANG_POINTS_MIN_LVL;
    public static long ALT_PCBANG_POINTS_BAN_TIME;
    public static int ALT_PCBANG_POINTS_MAX_CODE_ENTER_ATTEMPTS;
    public static String ALT_PCBANG_POINTS_COUPON_TEMPLATE;
    public static boolean PC_BANG_TO_ITEMMALL;
    public static int PC_BANG_TO_ITEMMALL_RATE;
    public static int PC_BANG_ENCHANT_MAX;
    public static int PC_BANG_SAFE_ENCHANT;
    public static int ALT_PCBANG_POINTS_ON_START;
    public static int ALT_MAX_PC_BANG_POINTS;
    public static int ALT_PC_BANG_WIVERN_PRICE;
    public static int ALT_PC_BANG_WIVERN_TIME;
    public static boolean EVENT_TREASURES_OF_THE_HERALD_ENABLE;
    public static int EVENT_TREASURES_OF_THE_HERALD_ITEM_ID;
    public static int EVENT_TREASURES_OF_THE_HERALD_ITEM_COUNT;
    public static int EVENT_TREASURES_OF_THE_HERALD_TIME;
    public static int EVENT_TREASURES_OF_THE_HERALD_MIN_LEVEL;
    public static int EVENT_TREASURES_OF_THE_HERALD_MAX_LEVEL;
    public static int EVENT_TREASURES_OF_THE_HERALD_MINIMUM_PARTY_MEMBER;
    public static int EVENT_TREASURES_OF_THE_HERALD_MAX_GROUP;
    public static int EVENT_TREASURES_OF_THE_HERALD_SCORE_BOX;
    public static int EVENT_TREASURES_OF_THE_HERALD_SCORE_BOSS;
    public static int EVENT_TREASURES_OF_THE_HERALD_SCORE_KILL;
    public static int EVENT_TREASURES_OF_THE_HERALD_SCORE_DEATH;
    public static boolean ALT_ENABLE_UNDERGROUND_BATTLE_EVENT;
    public static int ALT_MIN_UNDERGROUND_BATTLE_TEAM_MEMBERS;
    public static boolean EVENT_UNDERGROUND_COLISEUM_ONLY_PATY;
    public static boolean EVENT_TVT_ARENA_ENABLED;
    public static int EVENT_TVT_ARENA_TECH_REASON;
    public static int EVENT_TVT_ARENA_NO_PLAYERS;
    public static int EVENT_TVT_ARENA_TEAM_DRAW;
    public static int EVENT_TVT_ARENA_TEAM_WIN;
    public static int EVENT_TVT_ARENA_TEAM_LOSS;
    public static boolean EVENT_TVT_ARENA_ALLOW_CLAN_SKILL;
    public static boolean EVENT_TVT_ARENA_ALLOW_HERO_SKILL;
    public static boolean EVENT_TVT_ARENA_ALLOW_BUFFS;
    public static int EVENT_TVT_ARENA_TEAM_COUNT;
    public static int EVENT_TVT_ARENA_TIME_TO_START;
    public static int EVENT_TVT_ARENA_TEAMLEADER_EXIT;
    public static int EVENT_TVT_ARENA_FIGHT_TIME;
    public static int[] EVENT_TVT_ARENA_DISABLED_ITEMS;
    public static int EVENT_TVT_ARENA_TEAM_COUNT_MIN;
    public static String[] EVENT_TVT_ARENA_START_TIME;
    public static String[] EVENT_TVT_ARENA_STOP_TIME;
    public static boolean ALT_DEBUG_ENABLED;
    public static boolean ALT_DEBUG_PVP_ENABLED;
    public static boolean ALT_DEBUG_PVP_DUEL_ONLY;
    public static boolean ALT_DEBUG_PVE_ENABLED;
    public static boolean ALT_ENABLE_BOTREPORT;
    public static double CRAFT_MASTERWORK_CHANCE;
    public static double CRAFT_DOUBLECRAFT_CHANCE;
    public static int EVENT_LastHeroItemID;
    public static double EVENT_LastHeroItemCOUNT;
    public static boolean EVENT_LastHeroRate;
    public static double EVENT_LastHeroItemCOUNTFinal;
    public static boolean EVENT_LastHeroRateFinal;
    public static int EVENT_LastHeroTime;
    public static String[] EVENT_LastHeroStartTime;
    public static boolean EVENT_LastHeroCategories;
    public static boolean EVENT_LastHeroAllowSummons;
    public static boolean EVENT_LastHeroAllowBuffs;
    public static boolean EVENT_HeroAuraChatEnabled;
    public static boolean EVENT_LastHeroAllowMultiReg;
    public static String EVENT_LastHeroCheckWindowMethod;
    public static int EVENT_LastHeroEventRunningTime;
    public static int[] EVENT_LastHeroEventBlockItems;
    public static String[] EVENT_LastHeroFighterBuffs;
    public static String[] EVENT_LastHeroMageBuffs;
    public static boolean EVENT_LastHeroBuffPlayers;
    public static boolean EVENT_LAST_HERO_AURA_ENABLE;
    public static boolean EVENT_LuckNPC_ENABLED;
    public static int EVENT_LuckNPCManagerId;
    public static String[] EVENT_LuckNPCStartTime;
    public static String[] EVENT_LuckNPCStopTime;
    public static int EVENT_LuckNPCChance;
    public static String[] EVENT_LuckNPLoc;
    public static String[] EVENT_LuckNPCReward;
    public static String[] EVENT_LuckNPCPoints;
    /**
     * Thread pools size
     */
    public static int SCHEDULED_THREAD_POOL_SIZE;
    public static int EXECUTOR_THREAD_POOL_SIZE;
    public static boolean ENABLE_RUNNABLE_STATS;
    /**
     * Network settings
     */
    public static SelectorConfig SELECTOR_CONFIG = new SelectorConfig();
    public static boolean AUTO_LOOT;
    public static boolean AUTO_LOOT_HERBS;
    public static boolean AUTO_LOOT_ONLY_ADENA;
    public static boolean AUTO_LOOT_INDIVIDUAL;
    public static boolean AUTO_LOOT_FROM_RAIDS;
    /**
     * Auto-loot for/from players with karma also?
     */
    public static boolean AUTO_LOOT_PK;
    /**
     * Character name template
     */
    public static String CNAME_TEMPLATE;
    public static int CNAME_MAXLEN = 32;
    /**
     * Clan name template
     */
    public static String CLAN_NAME_TEMPLATE;
    /**
     * Clan title template
     */
    public static String CLAN_TITLE_TEMPLATE;
    /**
     * Ally name template
     */
    public static String ALLY_NAME_TEMPLATE;
    /**
     * Global chat state
     */
    public static boolean GLOBAL_SHOUT;
    public static boolean GLOBAL_TRADE_CHAT;
    public static int CHAT_RANGE;
    public static int SHOUT_OFFSET;
    /**
     * For test servers - evrybody has admin rights
     */
    public static boolean EVERYBODY_HAS_ADMIN_RIGHTS;
    public static double ALT_RAID_RESPAWN_MULTIPLIER;
    public static boolean ALT_ALLOW_AUGMENT_ALL;
    public static boolean ALT_ALLOW_DROP_AUGMENTED;
    public static boolean ALT_GAME_UNREGISTER_RECIPE;
    /**
     * Delay for announce SS period (in minutes)
     */
    public static int SS_ANNOUNCE_PERIOD;
    /**
     * Petition manager
     */
    public static boolean PETITIONING_ALLOWED;
    public static int MAX_PETITIONS_PER_PLAYER;
    public static int MAX_PETITIONS_PENDING;
    /**
     * Show mob stats/droplist to players?
     */
    public static boolean ALT_GAME_SHOW_DROPLIST;
    public static boolean ALT_FULL_NPC_STATS_PAGE;
    public static boolean ALLOW_NPC_SHIFTCLICK;
    public static boolean ALT_ALLOW_SELL_COMMON;
    public static boolean ALT_ALLOW_SHADOW_WEAPONS;
    public static int[] ALT_DISABLED_MULTISELL;
    public static int[] ALT_SHOP_PRICE_LIMITS;
    public static int[] ALT_SHOP_UNALLOWED_ITEMS;
    public static int[] ALT_ALLOWED_PET_POTIONS;
    public static boolean SKILLS_CHANCE_SHOW;
    public static double SKILLS_CHANCE_MOD;
    public static double SKILLS_CHANCE_MIN;
    public static double SKILLS_CHANCE_POW;
    public static double SKILLS_CHANCE_CAP;
    public static double SKILLS_MOB_CHANCE;
    public static double SKILLS_DEBUFF_MOB_CHANCE;
    public static boolean SHIELD_SLAM_BLOCK_IS_MUSIC;
    public static boolean ALT_SAVE_UNSAVEABLE;
    public static int ALT_SAVE_EFFECTS_REMAINING_TIME;
    public static boolean ALT_SHOW_REUSE_MSG;
    public static boolean ALT_DELETE_SA_BUFFS;
    public static int ALT_MUSIC_COST_GUARD_INTERVAL;
    public static int SKILLS_CAST_TIME_MIN;
    public static int DELAY_ATACK_TIME_MIN;
    public static int[] ALLOW_HTML_DISTANCE_DIALOG_NPC;
    /**
     * Конфигурация использования итемов по умолчанию поушены
     */
    public static int[] ITEM_USE_LIST_ID;
    public static boolean ITEM_USE_IS_COMBAT_FLAG;
    public static boolean ITEM_USE_IS_ATTACK;
    public static boolean ITEM_USE_IS_EVENTS;
    /**
     * Настройки для евента Файт Клуб
     */
    public static boolean FIGHT_CLUB_ENABLED;
    public static int MINIMUM_LEVEL_TO_PARRICIPATION;
    public static int MAXIMUM_LEVEL_TO_PARRICIPATION;
    public static int MAXIMUM_LEVEL_DIFFERENCE;
    public static String[] ALLOWED_RATE_ITEMS;
    public static int PLAYERS_PER_PAGE;
    public static int ARENA_TELEPORT_DELAY;
    public static boolean CANCEL_BUFF_BEFORE_FIGHT;
    public static boolean UNSUMMON_PETS;
    public static boolean UNSUMMON_SUMMONS;
    public static boolean REMOVE_CLAN_SKILLS;
    public static boolean REMOVE_HERO_SKILLS;
    public static int TIME_TO_PREPARATION;
    public static int FIGHT_TIME;
    public static boolean ALLOW_DRAW;
    public static int TIME_TELEPORT_BACK;
    public static boolean FIGHT_CLUB_ANNOUNCE_RATE;
    public static boolean FIGHT_CLUB_ANNOUNCE_RATE_TO_SCREEN;
    public static boolean FIGHT_CLUB_ANNOUNCE_START_TO_SCREEN;
    /**
     * Титул при создании чара
     */
    public static boolean CHAR_TITLE;
    public static String ADD_CHAR_TITLE;
    /**
     * Таймаут на использование social action
     */
    public static boolean ALT_SOCIAL_ACTION_REUSE;
    /**
     * Отключение книг для изучения скилов
     */
    public static boolean ALT_DISABLE_SPELLBOOKS;
    /**
     * Alternative gameing - loss of XP on death
     */
    public static boolean ALT_GAME_DELEVEL;
    /**
     * Разрешать ли на арене бои за опыт
     */
    public static boolean ALT_ARENA_EXP;
    /**
     * Разрешать ли использование бага с макросом
     */
    public static int ALT_REUSE_CORRECTION;
    public static int ALT_SPIRITSHOT_DISCHARGE_CORRECTION;
    public static boolean ALT_GAME_SUBCLASS_WITHOUT_QUESTS;
    public static boolean ALT_GAME_NOOBLES_WITHOUT_QUESTS;
    public static boolean ALT_ALLOW_SUBCLASS_WITHOUT_BAIUM;
    public static int ALT_GAME_START_LEVEL_TO_SUBCLASS;
    public static int VITAMIN_PETS_FOOD_ID;
    public static int ALT_GAME_LEVEL_TO_GET_SUBCLASS;
    public static int ALT_MAX_LEVEL;
    public static int ALT_MAX_SUB_LEVEL;
    public static int ALT_GAME_SUB_ADD;
    public static boolean ALT_GAME_SUB_BOOK;
    public static boolean ALT_SUB_DELETE_ALL_ON_CHANGE;
    public static boolean ALT_SUB_ALL_CHANGE;
    public static boolean ALT_NO_LASTHIT;
    public static boolean ALT_KAMALOKA_NIGHTMARES_PREMIUM_ONLY;
    public static boolean ALT_KAMALOKA_NIGHTMARE_REENTER;
    public static boolean ALT_KAMALOKA_ABYSS_REENTER;
    public static boolean ALT_KAMALOKA_LAB_REENTER;
    public static boolean ALT_PET_HEAL_BATTLE_ONLY;
    public static boolean ALT_SIMPLE_SIGNS;
    public static boolean ALT_TELE_TO_CATACOMBS;
    public static boolean ALT_BS_CRYSTALLIZE;
    public static int ALT_MAMMON_EXCHANGE;
    public static int ALT_MAMMON_UPGRADE;
    public static boolean ALT_ALLOW_TATTOO;
    public static int ALT_BUFF_LIMIT;
    public static boolean SHOW_HTML_WELCOME;
    public static int MULTISELL_SIZE;
    public static boolean SERVICES_CHANGE_NICK_ENABLED;
    public static boolean SERVICES_CHANGE_NICK_ALLOW_SYMBOL;
    public static int SERVICES_CHANGE_NICK_PRICE;
    public static int SERVICES_CHANGE_NICK_ITEM;
    public static boolean SERVICES_CHANGE_CLAN_NAME_ENABLED;
    public static int SERVICES_CHANGE_CLAN_NAME_PRICE;
    public static int SERVICES_CHANGE_CLAN_NAME_ITEM;
    public static boolean SERVICES_CLAN_BUY_POINTS_ENABLED;
    public static int[][] SERVICES_CLAN_BUY_POINTS_PRICE;
    public static int SERVICES_CLAN_BUY_POINTS_ITEM;
    public static boolean SERVICES_CLAN_BUY_LEVEL_ENABLED;
    public static int[] SERVICES_CLAN_BUY_LEVEL_PRICE;
    public static int SERVICES_CLAN_BUY_LEVEL_ITEM;
    public static boolean SERVICES_CHANGE_PET_NAME_ENABLED;
    public static int SERVICES_CHANGE_PET_NAME_PRICE;
    public static int SERVICES_CHANGE_PET_NAME_ITEM;
    public static boolean SERVICES_EXCHANGE_BABY_PET_ENABLED;
    public static int SERVICES_EXCHANGE_BABY_PET_PRICE;
    public static int SERVICES_EXCHANGE_BABY_PET_ITEM;
    public static boolean SERVICES_PET_RIDE_ENABLED;
    public static boolean SERVICES_CHANGE_SEX_ENABLED;
    public static int SERVICES_CHANGE_SEX_PRICE;
    public static int SERVICES_CHANGE_SEX_ITEM;
    public static boolean SERVICES_CHANGE_BASE_ENABLED;
    public static int SERVICES_CHANGE_BASE_PRICE;
    public static int SERVICES_CHANGE_BASE_ITEM;
    public static boolean SERVICES_SEPARATE_SUB_ENABLED;
    public static int SERVICES_SEPARATE_SUB_PRICE;
    public static int SERVICES_SEPARATE_SUB_ITEM;
    public static boolean SERVICES_CHANGE_NICK_COLOR_ENABLED;
    public static int SERVICES_CHANGE_NICK_COLOR_PRICE;
    public static int SERVICES_CHANGE_NICK_COLOR_ITEM;
    public static String[] SERVICES_CHANGE_NICK_COLOR_LIST;
    public static boolean SERVICES_CHANGE_TITLE_COLOR_ENABLED;
    public static int SERVICES_CHANGE_TITLE_COLOR_PRICE;
    public static int SERVICES_CHANGE_TITLE_COLOR_ITEM;
    public static String[] SERVICES_CHANGE_TITLE_COLOR_LIST;
    public static boolean SERVICES_BASH_ENABLED;
    public static boolean SERVICES_BASH_SKIP_DOWNLOAD;
    public static int SERVICES_BASH_RELOAD_TIME;
    public static boolean SERVICES_NOBLESS_SELL_ENABLED;
    public static int SERVICES_NOBLESS_SELL_PRICE;
    public static int SERVICES_NOBLESS_SELL_ITEM;
    public static boolean SERVICES_HERO_SELL_ENABLED;
    public static int[] SERVICES_HERO_SELL_DAY;
    public static int[] SERVICES_HERO_SELL_PRICE;
    public static int[] SERVICES_HERO_SELL_ITEM;
    public static boolean SERVICES_WASH_PK_ENABLED;
    public static int SERVICES_WASH_PK_ITEM;
    public static int SERVICES_WASH_PK_PRICE;
    public static boolean SERVICES_WASH_PK_CARMA_ENABLED;
    public static int SERVICES_WASH_PK_CARMA_ITEM;
    public static int SERVICES_WASH_PK_CARMA_PRICE;
    public static boolean SERVICES_EXPAND_INVENTORY_ENABLED;
    public static int SERVICES_EXPAND_INVENTORY_PRICE;
    public static int SERVICES_EXPAND_INVENTORY_ITEM;
    public static int SERVICES_EXPAND_INVENTORY_MAX;
    public static boolean SERVICES_EXPAND_WAREHOUSE_ENABLED;
    public static int SERVICES_EXPAND_WAREHOUSE_PRICE;
    public static int SERVICES_EXPAND_WAREHOUSE_ITEM;
    public static boolean SERVICES_EXPAND_CWH_ENABLED;
    public static int SERVICES_EXPAND_CWH_PRICE;
    public static int SERVICES_EXPAND_CWH_ITEM;
    public static String SERVICES_SELLPETS;
    public static boolean SERVICES_OFFLINE_TRADE_ALLOW;
    public static boolean SERVICES_OFFLINE_TRADE_ALLOW_OFFSHORE;
    public static int SERVICES_OFFLINE_TRADE_MIN_LEVEL;
    public static boolean ALLOW_SERVICES_OFFLINE_TRADE_NAME_COLOR;
    public static int SERVICES_OFFLINE_TRADE_NAME_COLOR;
    public static int SERVICES_OFFLINE_TRADE_PRICE;
    public static int SERVICES_OFFLINE_TRADE_PRICE_ITEM;
    public static long SERVICES_OFFLINE_TRADE_SECONDS_TO_KICK;
    public static boolean SERVICES_OFFLINE_TRADE_RESTORE_AFTER_RESTART;
    public static boolean SERVICES_GIRAN_HARBOR_ENABLED;
    public static boolean SERVICES_PARNASSUS_ENABLED;
    public static boolean SERVICES_PARNASSUS_NOTAX;
    public static long SERVICES_PARNASSUS_PRICE;
    public static boolean SERVICES_ALLOW_LOTTERY;
    public static int SERVICES_LOTTERY_PRIZE;
    public static int SERVICES_ALT_LOTTERY_PRICE;
    public static int SERVICES_LOTTERY_TICKET_PRICE;
    public static double SERVICES_LOTTERY_5_NUMBER_RATE;
    public static double SERVICES_LOTTERY_4_NUMBER_RATE;
    public static double SERVICES_LOTTERY_3_NUMBER_RATE;
    public static int SERVICES_LOTTERY_2_AND_1_NUMBER_PRIZE;
    public static boolean SERVICES_ALLOW_ROULETTE;
    public static long SERVICES_ROULETTE_MIN_BET;
    public static long SERVICES_ROULETTE_MAX_BET;
    public static long EXPELLED_MEMBER_PENALTY;
    public static long LEAVED_ALLY_PENALTY;
    public static long DISSOLVED_ALLY_PENALTY;
    public static boolean ALT_ALLOW_OTHERS_WITHDRAW_FROM_CLAN_WAREHOUSE;
    public static boolean ALT_ALLOW_CLAN_COMMAND_ONLY_FOR_CLAN_LEADER;
    public static boolean ALT_GAME_REQUIRE_CLAN_CASTLE;
    public static boolean ALT_GAME_REQUIRE_CASTLE_DAWN;
    public static boolean ALT_GAME_ALLOW_ADENA_DAWN;
    /**
     * Olympiad Compitition Starting time
     */
    public static int ALT_OLY_START_TIME;
    /**
     * Olympiad Compition Min
     */
    public static int ALT_OLY_MIN;
    /**
     * Olympaid Comptetition Period
     */
    public static long ALT_OLY_CPERIOD;
    /**
     * Olympaid Weekly Period
     */
    public static long ALT_OLY_WPERIOD;
    /**
     * Olympaid Validation Period
     */
    public static long ALT_OLY_VPERIOD;
    public static int[] ALT_OLY_DATE_END;
    public static boolean ENABLE_OLYMPIAD;
    public static boolean ENABLE_OLYMPIAD_SPECTATING;
    public static boolean OLYMPIAD_BUFF_DELETED;
    public static int CLASS_GAME_MIN;
    public static int NONCLASS_GAME_MIN;
    public static int TEAM_GAME_MIN;
    public static int GAME_MAX_LIMIT;
    public static int GAME_CLASSES_COUNT_LIMIT;
    public static int GAME_NOCLASSES_COUNT_LIMIT;
    public static int GAME_TEAM_COUNT_LIMIT;
    public static int ALT_OLY_REG_DISPLAY;
    public static int ALT_OLY_BATTLE_REWARD_ITEM;
    public static int ALT_OLY_CLASSED_RITEM_C;
    public static int ALT_OLY_NONCLASSED_RITEM_C;
    public static int ALT_OLY_TEAM_RITEM_C;
    public static int ALT_OLY_COMP_RITEM;
    public static int ALT_OLY_GP_PER_POINT;
    public static int ALT_OLY_HERO_POINTS;
    public static int ALT_OLY_RANK1_POINTS;
    public static int ALT_OLY_RANK2_POINTS;
    public static int ALT_OLY_RANK3_POINTS;
    public static int ALT_OLY_RANK4_POINTS;
    public static int ALT_OLY_RANK5_POINTS;
    public static int OLYMPIAD_STADIAS_COUNT;
    public static int OLYMPIAD_BATTLES_FOR_REWARD;
    public static int OLYMPIAD_POINTS_DEFAULT;
    public static int OLYMPIAD_POINTS_WEEKLY;
    public static boolean OLYMPIAD_OLDSTYLE_STAT;
    public static boolean OLYMPIAD_PLAYER_IP;
    public static boolean OLYMPIAD_PLAYER_HWID;
    public static int OLYMPIAD_BEGIN_TIME;
    public static boolean OLYMPIAD_BAD_ENCHANT_ITEMS_ALLOW;
    public static boolean OLY_ENCH_LIMIT_ENABLE;
    public static int OLY_ENCHANT_LIMIT_WEAPON;
    public static int OLY_ENCHANT_LIMIT_ARMOR;
    public static int OLY_ENCHANT_LIMIT_JEWEL;
    public static long NONOWNER_ITEM_PICKUP_DELAY;
    /**
     * Logging Chat Window
     */
    public static boolean LOG_CHAT;
    public static Map<Integer, PlayerAccess> gmlist = new HashMap<Integer, PlayerAccess>();
    /**
     * Rate control
     */
    public static boolean ALT_DROP_RATE;
    public static double RATE_XP;
    public static double RATE_SP;
    public static double RATE_QUESTS_REWARD;
    public static double RATE_QUESTS_DROP;
    public static double RATE_CLAN_REP_SCORE;
    public static int RATE_CLAN_REP_SCORE_MAX_AFFECTED;
    public static int MAX_CLAN_REPUTATIONS_POINTS;
    public static double RATE_DROP_ADENA;
    public static double RATE_DROP_STONEPRINT;
    public static double RATE_DROP_EPOLET;
    public static double RATE_DROP_CHAMPION;
    public static double RATE_CHAMPION_DROP_ADENA;
    public static double RATE_DROP_SPOIL_CHAMPION;
    public static double RATE_DROP_ITEMS;
    public static double RATE_CHANCE_GROUP_DROP_ITEMS;
    public static double RATE_CHANCE_DROP_ADENA;
    public static double RATE_CHANCE_DROP_ITEMS;
    public static double RATE_CHANCE_DROP_HERBS;
    public static double RATE_CHANCE_SPOIL;
    public static double RATE_CHANCE_SPOIL_WEAPON_ARMOR_ACCESSORY;
    public static double RATE_DROP_BLUE_STONE;
    public static double RATE_DROP_GREEN_STONE;
    public static double RATE_DROP_RED_STONE;
    public static double RATE_CHANCE_DROP_WEAPON_ARMOR_ACCESSORY;
    public static double RATE_CHANCE_DROP_EPOLET;
    public static boolean NO_RATE_ENCHANT_SCROLL;
    public static double RATE_ENCHANT_SCROLL;
    public static boolean CHAMPION_DROP_ONLY_ADENA;
    public static boolean NO_RATE_HERBS;
    public static double RATE_DROP_HERBS;
    public static boolean NO_RATE_ATT;
    public static double RATE_DROP_ATT;
    public static boolean NO_RATE_LIFE_STONE;
    public static boolean NO_RATE_CODEX_BOOK;
    public static boolean NO_RATE_FORGOTTEN_SCROLL;
    public static double RATE_DROP_LIFE_STONE;
    public static boolean NO_RATE_KEY_MATERIAL;
    public static double RATE_DROP_KEY_MATERIAL;
    public static boolean NO_RATE_RECIPES;
    public static double RATE_DROP_RECIPES;
    public static double RATE_DROP_COMMON_ITEMS;
    public static double RATE_DROP_RAIDBOSS;
    public static double RATE_CHANCE_RAIDBOSS;
    public static double RATE_DROP_SPOIL;
    public static int[] NO_RATE_ITEMS;
    public static double RATE_DROP_SIEGE_GUARD;
    public static double RATE_MANOR;
    public static double RATE_FISH_DROP_COUNT;
    public static boolean RATE_PARTY_MIN;
    public static double RATE_HELLBOUND_CONFIDENCE;
    public static boolean NO_RATE_EQUIPMENT;
    public static int RATE_MOB_SPAWN;
    public static int RATE_MOB_SPAWN_MIN_LEVEL;
    public static int RATE_MOB_SPAWN_MAX_LEVEL;
    public static ArrayList<Integer> RATE_SIEGE_FAME = new ArrayList<>();
    public static ArrayList<Integer> RATE_DOMINION_SIEGE_FAME = new ArrayList<>();
    /**
     * Player Drop Rate control
     */
    public static boolean KARMA_DROP_GM;
    public static boolean KARMA_NEEDED_TO_DROP;
    public static int KARMA_DROP_ITEM_LIMIT;
    public static int KARMA_RANDOM_DROP_LOCATION_LIMIT;
    public static double KARMA_DROPCHANCE_BASE;
    public static double KARMA_DROPCHANCE_MOD;
    public static double NORMAL_DROPCHANCE_BASE;
    public static int DROPCHANCE_EQUIPMENT;
    public static int DROPCHANCE_EQUIPPED_WEAPON;
    public static int DROPCHANCE_ITEM;
    public static int AUTODESTROY_ITEM_AFTER;
    public static int AUTODESTROY_PLAYER_ITEM_AFTER;
    public static int DELETE_DAYS;
    public static int PURGE_BYPASS_TASK_FREQUENCY;
    /**
     * Datapack root directory
     */
    public static File DATAPACK_ROOT;
    public static double CLANHALL_BUFFTIME_MODIFIER;
    public static double SONGDANCETIME_MODIFIER;
    public static double MAXLOAD_MODIFIER;
    public static double GATEKEEPER_MODIFIER;
    public static boolean ALT_IMPROVED_PETS_LIMITED_USE;
    public static int GATEKEEPER_FREE;
    public static int CRUMA_GATEKEEPER_LVL;
    public static double ALT_CHAMPION_CHANCE1;
    public static double ALT_CHAMPION_CHANCE2;
    public static boolean ALT_CHAMPION_CAN_BE_AGGRO;
    public static boolean ALT_CHAMPION_CAN_BE_SOCIAL;
    public static boolean ALT_CHAMPION_DROP_HERBS;
    public static boolean ALT_SHOW_MONSTERS_LVL;
    public static boolean ALT_SHOW_MONSTERS_AGRESSION;
    public static int ALT_CHAMPION_TOP_LEVEL;
    public static int ALT_CHAMPION_MIN_LEVEL;
    public static boolean ALLOW_DISCARDITEM;
    public static boolean ALLOW_MAIL;
    public static boolean ALLOW_WAREHOUSE;
    public static boolean ALLOW_WATER;
    public static boolean ALLOW_CURSED_WEAPONS;
    public static boolean DROP_CURSED_WEAPONS_ON_KICK;
    public static boolean ALLOW_NOBLE_TP_TO_ALL;
    public static boolean ALLOW_SS_TP_NO_REG;
    public static boolean ALLOW_HTML_IN_DUEL;

    public static boolean ALLOW_HTML_DISTANCE_DIALOG;

    public static boolean ALLOW_VISUAL_FROM_AUGMENT;
    public static int[] VISUAL_FROM_AUGMENT_ALL;
    public static int[] VISUAL_FROM_AUGMENT_CATALIS_WEAPON;
    public static int[] VISUAL_FROM_AUGMENT_CATALIS_ARMOR;
    public static int[] VISUAL_FROM_AUGMENT_ID_IN;
    public static int[] VISUAL_FROM_AUGMENT_PRICES_IN;
    public static int VISUAL_FROM_AUGMENT_ID_OUT;
    public static int[] VISUAL_FROM_AUGMENT_PRICES_OUT;

    /**
     * Pets
     */
    public static int SWIMING_SPEED;
    public static boolean SAVE_PET_EFFECT;
    /**
     * protocol revision
     */
    public static int MIN_PROTOCOL_REVISION;
    public static int MAX_PROTOCOL_REVISION;
    /**
     * random animation interval
     */
    public static int MIN_NPC_ANIMATION;
    public static int MAX_NPC_ANIMATION;
    public static String DEFAULT_LANG;
    public static String DEFAULT_GK_LANG;
    /**
     * Обменник
     */
    public static int EXCH_COIN_ID;
    /**
     * Время запланированного на определенное время суток рестарта
     */
    public static String RESTART_AT_TIME;
    public static int GAME_SERVER_LOGIN_PORT;
    public static boolean GAME_SERVER_LOGIN_CRYPT;
    public static String GAME_SERVER_LOGIN_HOST;
    public static String INTERNAL_HOSTNAME;
    public static String EXTERNAL_HOSTNAME;

    public static boolean GAME_FORWARDING_NET;
    public static String GAME_FORWARDING_NET_HOST;
    public static int GAME_FORWARDING_NET_SERVER_ID;

    public static boolean SECOND_AUTH_ENABLED;
    public static boolean SECOND_AUTH_BAN_ACC;
    public static boolean SECOND_AUTH_STRONG_PASS;
    public static int SECOND_AUTH_MAX_ATTEMPTS;
    public static long SECOND_AUTH_BAN_TIME;
    public static String SECOND_AUTH_REC_LINK;
    public static boolean SERVER_SIDE_NPC_NAME;
    public static boolean SERVER_SIDE_NPC_TITLE;
    public static String CLASS_MASTERS_PRICE;
    public static int CLASS_MASTERS_PRICE_ITEM;
    public static List<Integer> ALLOW_CLASS_MASTERS_LIST = new ArrayList<Integer>();
    public static int[] CLASS_MASTERS_PRICE_LIST = new int[4];
    public static boolean ALLOW_EVENT_GATEKEEPER;
    public static boolean ITEM_BROKER_ITEM_SEARCH;
    public static boolean SERVICES_CHANGE_PASSWORD;
    /**
     * Inventory slots limits
     */
    public static int INVENTORY_MAXIMUM_NO_DWARF;
    public static int INVENTORY_MAXIMUM_DWARF;
    public static int INVENTORY_MAXIMUM_GM;
    public static int QUEST_INVENTORY_MAXIMUM;
    /**
     * Warehouse slots limits
     */
    public static int WAREHOUSE_SLOTS_NO_DWARF;
    public static int WAREHOUSE_SLOTS_DWARF;
    public static int WAREHOUSE_SLOTS_CLAN;
    public static int FREIGHT_SLOTS;
    /**
     * Spoil Rates
     */
    public static double BASE_SPOIL_RATE;
    public static double MINIMUM_SPOIL_RATE;
    public static boolean ALT_SPOIL_FORMULA;
    /**
     * Manor Config
     */
    public static double MANOR_SOWING_BASIC_SUCCESS;
    public static double MANOR_SOWING_ALT_BASIC_SUCCESS;
    public static double MANOR_HARVESTING_BASIC_SUCCESS;
    public static int MANOR_DIFF_PLAYER_TARGET;
    public static double MANOR_DIFF_PLAYER_TARGET_PENALTY;
    public static int MANOR_DIFF_SEED_TARGET;
    public static double MANOR_DIFF_SEED_TARGET_PENALTY;
    /**
     * Karma System Variables
     */
    public static int KARMA_MIN_KARMA;
    public static int KARMA_SP_DIVIDER;
    public static int KARMA_LOST_BASE;
    public static int MIN_PK_TO_ITEMS_DROP;
    public static boolean DROP_ITEMS_ON_DIE;
    public static boolean DROP_ITEMS_AUGMENTED;
    public static List<Integer> KARMA_LIST_NONDROPPABLE_ITEMS = new ArrayList<Integer>();
    public static int PVP_TIME;
    /**
     * Karma Punishment
     */
    public static boolean ALT_GAME_KARMA_PLAYER_CAN_SHOP;
    /**
     * Chance that an item will succesfully be enchanted
     */
    public static int ENCHANT_CHANCE_WEAPON;
    public static int ENCHANT_CHANCE_ARMOR;
    public static int ENCHANT_CHANCE_ACCESSORY;
    public static int ENCHANT_CHANCE_CRYSTAL_WEAPON;
    public static int ENCHANT_CHANCE_CRYSTAL_ARMOR;
    public static int ENCHANT_CHANCE_CRYSTAL_ARMOR_OLF_1;
    public static int ENCHANT_CHANCE_CRYSTAL_ARMOR_OLF_2;
    public static int ENCHANT_CHANCE_CRYSTAL_ARMOR_OLF_3;
    public static int ENCHANT_CHANCE_CRYSTAL_ARMOR_OLF_4;
    public static int ENCHANT_CHANCE_CRYSTAL_ARMOR_OLF_5;
    public static int ENCHANT_CHANCE_CRYSTAL_ARMOR_OLF_6;
    public static int ENCHANT_CHANCE_CRYSTAL_ARMOR_OLF_7;
    public static int ENCHANT_CHANCE_CRYSTAL_ARMOR_OLF_8;
    public static int ENCHANT_CHANCE_CRYSTAL_ARMOR_OLF_9;
    public static int ENCHANT_CHANCE_CRYSTAL_ARMOR_OLF;
    public static int ENCHANT_CHANCE_CRYSTAL_ACCESSORY;
    public static int ENCHANT_CHANCE_WEAPON_BLESS;
    public static int ENCHANT_CHANCE_ARMOR_BLESS;
    public static int ENCHANT_CHANCE_ACCESSORY_BLESS;
    public static boolean USE_ALT_ENCHANT;
    public static ArrayList<Integer> ENCHANT_WEAPON_FIGHT = new ArrayList<Integer>();
    public static ArrayList<Integer> ENCHANT_WEAPON_FIGHT_CRYSTAL = new ArrayList<Integer>();
    public static ArrayList<Integer> ENCHANT_WEAPON_FIGHT_BLESSED = new ArrayList<Integer>();
    public static ArrayList<Integer> ENCHANT_WEAPON_MAGE = new ArrayList<Integer>();
    public static ArrayList<Integer> ENCHANT_WEAPON_MAGE_CRYSTAL = new ArrayList<Integer>();
    public static ArrayList<Integer> ENCHANT_WEAPON_MAGE_BLESSED = new ArrayList<Integer>();
    public static ArrayList<Integer> ENCHANT_ARMOR = new ArrayList<Integer>();
    public static ArrayList<Integer> ENCHANT_ARMOR_CRYSTAL = new ArrayList<Integer>();
    public static ArrayList<Integer> ENCHANT_ARMOR_BLESSED = new ArrayList<Integer>();
    public static ArrayList<Integer> ENCHANT_ARMOR_JEWELRY = new ArrayList<Integer>();
    public static ArrayList<Integer> ENCHANT_ARMOR_JEWELRY_CRYSTAL = new ArrayList<Integer>();
    public static ArrayList<Integer> ENCHANT_ARMOR_JEWELRY_BLESSED = new ArrayList<Integer>();
    public static int ENCHANT_MAX;
    public static int ENCHANT_MAX_WEAPON;
    public static int ENCHANT_MAX_ARMOR;
    public static int ENCHANT_MAX_JEWELRY;

    public static int ENCHANT_MAX_DESTRUCTION_WEAPON;
    public static int ENCHANT_MAX_DESTRUCTION_ARMOR;

    /**
     * Config one click enchant
     */
    public static boolean ENCHANT_ONE_CLICK;
    public static int ENCHANT_ONE_CLICK_WEAPON;
    public static int ENCHANT_ONE_CLICK_ARMOR;
    public static int ENCHANT_ONE_CLICK_ACCESSORY;

    public static int ENCHANT_ATTRIBUTE_STONE_CHANCE;
    public static int ENCHANT_ATTRIBUTE_CRYSTAL_CHANCE;
    public static boolean ALLOW_ALT_ATT_ENCHANT;
    public static int ALT_ATT_ENCHANT_WEAPON_VALUE;
    public static int ALT_ATT_ENCHANT_ARMOR_VALUE;
    public static int ARMOR_OVERENCHANT_HPBONUS_LIMIT;
    public static boolean SHOW_ENCHANT_EFFECT_RESULT;
    public static boolean ALLOW_KAMAEL_ALL_EQUIP;

    public static boolean REGEN_SIT_WAIT;
    public static double RATE_RAID_REGEN;
    public static double RATE_RAID_DEFENSE;
    public static double RATE_RAID_ATTACK;
    public static double RATE_EPIC_DEFENSE;
    public static double RATE_EPIC_ATTACK;
    public static int RAID_MAX_LEVEL_DIFF;
    public static int BOSS_BELETH_MIN_COUNT;
    public static int MUTATED_ELPY_COUNT;

    public static boolean PARALIZE_ON_RAID_DIFF;
    public static double ALT_PK_DEATH_RATE;
    public static int STARTING_ADENA;
    public static int STARTING_LVL;
    /**
     * Deep Blue Mobs' Drop Rules Enabled
     */
    public static boolean DEEPBLUE_DROP_RULES;
    public static int DEEPBLUE_DROP_MAXDIFF;
    public static int DEEPBLUE_DROP_RAID_MAXDIFF;
    public static boolean UNSTUCK_SKILL;
    /**
     * telnet enabled
     */
    public static boolean IS_TELNET_ENABLED;
    public static String TELNET_DEFAULT_ENCODING;
    public static String TELNET_PASSWORD;
    public static String TELNET_HOSTNAME;
    public static int TELNET_PORT;
    /**
     * Percent CP is restore on respawn
     */
    public static double RESPAWN_RESTORE_CP;
    /**
     * Percent HP is restore on respawn
     */
    public static double RESPAWN_RESTORE_HP;
    /**
     * Percent MP is restore on respawn
     */
    public static double RESPAWN_RESTORE_MP;
    /**
     * Maximum number of available slots for pvt stores (sell/buy) - Dwarves
     */
    public static int MAX_PVTSTORE_SLOTS_DWARF;
    /**
     * Maximum number of available slots for pvt stores (sell/buy) - Others
     */
    public static int MAX_PVTSTORE_SLOTS_OTHER;
    public static int MAX_PVTCRAFT_SLOTS;
    public static boolean SENDSTATUS_TRADE_JUST_OFFLINE;
    public static double SENDSTATUS_TRADE_MOD;
    public static boolean SHOW_OFFLINE_MODE_IN_ONLINE;
    public static boolean ALLOW_CH_DOOR_OPEN_ON_CLICK;
    public static boolean ALT_CH_ALL_BUFFS;
    public static boolean ALT_CH_ALLOW_1H_BUFFS;
    public static boolean ALT_CH_SIMPLE_DIALOG;
    public static int CH_BID_GRADE1_MINCLANLEVEL;
    public static int CH_BID_GRADE1_MINCLANMEMBERS;
    public static int CH_BID_GRADE1_MINCLANMEMBERSLEVEL;
    public static int CH_BID_GRADE2_MINCLANLEVEL;
    public static int CH_BID_GRADE2_MINCLANMEMBERS;
    public static int CH_BID_GRADE2_MINCLANMEMBERSLEVEL;
    public static int CH_BID_GRADE3_MINCLANLEVEL;
    public static int CH_BID_GRADE3_MINCLANMEMBERS;
    public static int CH_BID_GRADE3_MINCLANMEMBERSLEVEL;
    public static double RESIDENCE_LEASE_FUNC_MULTIPLIER;
    public static double RESIDENCE_LEASE_MULTIPLIER;
    public static boolean ACCEPT_ALTERNATE_ID;
    public static int REQUEST_ID;
    public static boolean ANNOUNCE_MAMMON_SPAWN;
    public static int GM_NAME_COLOUR;
    public static boolean GM_HERO_AURA;
    public static int NORMAL_NAME_COLOUR;
    public static int CLANLEADER_NAME_COLOUR;
    /**
     * AI
     */
    public static int AI_TASK_MANAGER_COUNT;
    public static long AI_TASK_ATTACK_DELAY;
    public static long AI_TASK_ACTIVE_DELAY;
    public static boolean BLOCK_ACTIVE_TASKS;
    public static boolean ALWAYS_TELEPORT_HOME;
    public static boolean RND_WALK;
    public static int RND_WALK_RATE;
    public static int MONSTER_WEAPON_ENCHANT_MIN;
    public static int MONSTER_WEAPON_ENCHANT_MAX;
    public static int MONSTER_WEAPON_ENCHANT_CHANCE;
    public static int RND_ANIMATION_RATE;
    public static int AGGRO_CHECK_INTERVAL;
    public static long NONAGGRO_TIME_ONTELEPORT;
    /**
     * Maximum range mobs can randomly go from spawn point
     */
    public static int MAX_DRIFT_RANGE;
    /**
     * Maximum range mobs can pursue agressor from spawn point
     */
    public static int MAX_PURSUE_RANGE;
    public static int MAX_PURSUE_UNDERGROUND_RANGE;
    public static int MAX_PURSUE_RANGE_RAID;
    public static boolean ALT_DEATH_PENALTY;
    public static boolean ALLOW_DEATH_PENALTY_C5;
    public static int ALT_DEATH_PENALTY_C5_CHANCE;
    public static boolean ALT_DEATH_PENALTY_C5_CHAOTIC_RECOVERY;
    public static int ALT_DEATH_PENALTY_C5_EXPERIENCE_PENALTY;
    public static int ALT_DEATH_PENALTY_C5_KARMA_PENALTY;
    public static boolean HIDE_GM_STATUS;
    public static boolean SHOW_GM_LOGIN;
    public static boolean SAVE_GM_EFFECTS; //Silence, gmspeed, etc...
    public static boolean AUTO_LEARN_SKILLS;
    public static boolean AUTO_LEARN_FORGOTTEN_SKILLS;
    public static int MOVE_PACKET_DELAY;
    public static int ATTACK_PACKET_DELAY;
    public static int ATTACK_END_DELAY;
    public static boolean DAMAGE_FROM_FALLING;
    /**
     * Community Board
     */
    public static boolean USE_BBS_BUFER_IS_COMBAT;
    public static boolean USE_BBS_BUFER_IS_CURSE_WEAPON;
    public static boolean USE_BBS_BUFER_IS_EVENTS;
    public static boolean USE_BBS_TELEPORT_IS_COMBAT;
    public static boolean USE_BBS_TELEPORT_IS_EVENTS;
    public static boolean USE_BBS_PROF_IS_COMBAT;
    public static boolean USE_BBS_PROF_IS_EVENTS;
    public static boolean SAVE_BBS_TELEPORT_IS_EPIC;
    public static boolean SAVE_BBS_TELEPORT_IS_BZ;
    public static boolean COMMUNITYBOARD_ENABLED;
    public static boolean ALLOW_COMMUNITYBOARD_IN_COMBAT;
    public static boolean ALLOW_COMMUNITYBOARD_IS_IN_SIEGE;
    public static boolean ALLOW_COMMUNITYBOARD_IS_IN_EVENT;
    public static boolean ALLOW_COMMUNITYBOARD_IS_IN_CURSED_WEAPON;
    public static boolean COMMUNITYBOARD_BUFFER_ENABLED;
    public static boolean ALLOW_BUFFER_IN_COMBAT;
    public static boolean ALLOW_BUFFER_IS_IN_SIEGE;
    public static boolean ALLOW_BUFFER_IS_IN_EVENT;
    public static boolean ALLOW_BUFFER_IS_IN_CURSED_WEPON;

    public static boolean COMMUNITYBOARD_BUFFER_MAX_LVL_ALLOW;
    public static boolean COMMUNITYBOARD_BUFFER_NO_IS_IN_PEACE_ENABLED;
    public static boolean COMMUNITYBOARD_SELL_ENABLED;
    public static boolean COMMUNITYBOARD_SHOP_ENABLED;
    public static boolean COMMUNITYBOARD_SHOP_NO_IS_IN_PEACE_ENABLED;
    public static boolean COMMUNITYBOARD_BUFFER_PET_ENABLED;
    public static boolean COMMUNITYBOARD_BUFFER_SAVE_ENABLED;
    public static boolean COMMUNITYBOARD_ABNORMAL_ENABLED;
    public static boolean COMMUNITYBOARD_INSTANCE_ENABLED;
    public static int COMMUNITYBOARD_BUFF_TIME;
    public static int COMMUNITYBOARD_BUFFER_MAX_LVL;
    public static int COMMUNITYBOARD_BUFF_PETS_TIME;
    public static int COMMUNITYBOARD_BUFF_COMBO_TIME;
    public static int COMMUNITYBOARD_BUFF_SONGDANCE_TIME;
    public static int COMMUNITYBOARD_BUFF_PICE;
    public static int COMMUNITYBOARD_BUFF_SAVE_PICE;
    public static List<Integer> COMMUNITYBOARD_BUFF_ALLOW = new ArrayList<Integer>();
    public static FastMap<Integer, Integer> COMMUNITI_LIST_MAGE_SUPPORT = new FastMap<Integer, Integer>();
    public static FastMap<Integer, Integer> COMMUNITI_LIST_FIGHTER_SUPPORT = new FastMap<Integer, Integer>();
    public static List<Integer> COMMUNITYBOARD_MULTISELL_ALLOW = new ArrayList<Integer>();
    public static int COMMUNITYBOARD_MULTISELL_RATE;
    public static String BBS_DEFAULT;
    public static String BBS_HOME_DIR;
    public static boolean COMMUNITYBOARD_TELEPORT_ENABLED;
    public static boolean ALLOW_TELEPORT_IN_COMBAT;
    public static boolean ALLOW_TELEPORT_IS_IN_SIEGE;
    public static boolean ALLOW_TELEPORT_IS_IN_EVENT;
    public static boolean ALLOW_TELEPORT_IS_IN_CURSED_WEPON;
    public static int COMMUNITYBOARD_TELE_PICE;
    public static boolean COMMUNITYBOARD_SAVE_TELE_PREMIUM;
    public static int COMMUNITYBOARD_SAVE_TELE_PICE;
    public static int COMMUNITYBOARD_SAVE_TELE_COUNT;
    public static boolean ALLOW_SELL_IN_COMBAT;
    public static boolean ALLOW_SELL_IS_IN_SIEGE;
    public static boolean ALLOW_SELL_IS_IN_EVENT;
    public static boolean ALLOW_SELL_IS_IN_CURSED_WEPON;

    public static boolean COMMUNITYBOARD_TELEPORT_SIEGE_ENABLED;
    public static boolean ALLOW_TELEPORT_POINT_CONTROLL;
    public static List<String> COMMUNITYBOARD_TELEPORT_POINT = new ArrayList<String>();
    /**
     * Wedding Options
     */
    public static boolean ALLOW_WEDDING;
    public static int WEDDING_PRICE;
    public static boolean WEDDING_PUNISH_INFIDELITY;
    public static boolean WEDDING_TELEPORT;
    public static int WEDDING_TELEPORT_PRICE;
    public static int WEDDING_TELEPORT_INTERVAL;
    public static boolean WEDDING_SAMESEX;
    public static boolean WEDDING_FORMALWEAR;
    public static int WEDDING_DIVORCE_COSTS;
    /**
     * Augmentations *
     */
    public static int AUGMENTATION_NG_SKILL_CHANCE; // Chance to get a skill while using a NoGrade Life Stone
    public static int AUGMENTATION_NG_GLOW_CHANCE; // Chance to get a Glow effect while using a NoGrade Life Stone(only if you get a skill)
    public static int AUGMENTATION_MID_SKILL_CHANCE; // Chance to get a skill while using a MidGrade Life Stone
    public static int AUGMENTATION_MID_GLOW_CHANCE; // Chance to get a Glow effect while using a MidGrade Life Stone(only if you get a skill)
    public static int AUGMENTATION_HIGH_SKILL_CHANCE; // Chance to get a skill while using a HighGrade Life Stone
    public static int AUGMENTATION_HIGH_GLOW_CHANCE; // Chance to get a Glow effect while using a HighGrade Life Stone
    public static int AUGMENTATION_TOP_SKILL_CHANCE; // Chance to get a skill while using a TopGrade Life Stone
    public static int AUGMENTATION_TOP_GLOW_CHANCE; // Chance to get a Glow effect while using a TopGrade Life Stone
    public static int AUGMENTATION_BASESTAT_CHANCE; // Chance to get a BaseStatModifier in the augmentation process
    public static int AUGMENTATION_ACC_SKILL_CHANCE;
    public static int FOLLOW_RANGE;
    public static boolean ALT_ENABLE_MULTI_PROFA;
    public static boolean ALT_ENABLE_MULTI_SKILL;
    public static boolean ALT_ENABLE_ENCHANT_SKILL_SUB;
    public static boolean ALT_ITEM_AUCTION_ENABLED;
    public static boolean ALT_ITEM_AUCTION_CAN_REBID;
    public static boolean ALT_ITEM_AUCTION_START_ANNOUNCE;
    public static int ALT_ITEM_AUCTION_BID_ITEM_ID;
    public static long ALT_ITEM_AUCTION_MAX_BID;
    public static long ALT_ITEM_AUCTION_MAX_CANCEL_TIME_IN_MILLIS;
    public static boolean ALT_GAME_CREATION;
    public static double ALT_GAME_CREATION_RARE_XPSP_RATE;
    public static double ALT_GAME_CREATION_XP_RATE;
    public static double ALT_GAME_CREATION_SP_RATE;
    public static boolean ALT_FISH_CHAMPIONSHIP_ENABLED;
    public static int ALT_FISH_CHAMPIONSHIP_REWARD_ITEM;
    public static int ALT_FISH_CHAMPIONSHIP_REWARD_1;
    public static int ALT_FISH_CHAMPIONSHIP_REWARD_2;
    public static int ALT_FISH_CHAMPIONSHIP_REWARD_3;
    public static int ALT_FISH_CHAMPIONSHIP_REWARD_4;
    public static int ALT_FISH_CHAMPIONSHIP_REWARD_5;
    public static boolean ALT_ENABLE_BLOCK_CHECKER_EVENT;
    public static int ALT_MIN_BLOCK_CHECKER_TEAM_MEMBERS;
    public static double ALT_RATE_COINS_REWARD_BLOCK_CHECKER;
    public static boolean ALT_HBCE_FAIR_PLAY;
    public static int ALT_PET_INVENTORY_LIMIT;
    public static int ALT_CLAN_LEVEL_CREATE;
    /**
     * limits of stats *
     */
    public static int LIM_CP;
    public static int LIM_HP;
    public static int LIM_MP;

    public static int LIM_PATK;
    public static int LIM_MATK;
    public static int LIM_PDEF;
    public static int LIM_MDEF;
    public static int LIM_MATK_SPD;
    public static int LIM_PATK_SPD;
    public static int LIM_CRIT_DAM;
    public static int LIM_CRIT;
    public static int LIM_MCRIT;
    public static int LIM_ACCURACY;
    public static int LIM_EVASION;
    public static int LIM_MOVE;
    public static int GM_LIM_MOVE;
    public static int LIM_FAME;

    public static double HEAL_POWER;
    public static double CPHEAL_EFFECTIVNESS;
    public static double MANAHEAL_EFFECTIVNESS;
    public static double HEAL_EFFECTIVNESS;

    public static double ALT_NPC_PATK_MODIFIER;
    public static double ALT_NPC_MATK_MODIFIER;
    public static double ALT_NPC_MAXHP_MODIFIER;
    public static double ALT_NPC_MAXMP_MODIFIER;
    public static double ALT_NPC_PDEF_MODIFIER;
    public static double ALT_NPC_MDEF_MODIFIER;
    /**
     * Enchant Config *
     */
    public static int SAFE_ENCHANT_COMMON;
    public static int SAFE_ENCHANT_FULL_BODY;
    public static int SAFE_ENCHANT_LVL;
    public static int SAFE_ENCHANT_LVL_OLF;
    public static int MAX_ENCHANT_OLF;
    public static int FESTIVAL_MIN_PARTY_SIZE;
    public static double FESTIVAL_RATE_PRICE;
    /**
     * DimensionalRift Config *
     */
    public static int RIFT_MIN_PARTY_SIZE;
    public static int RIFT_SPAWN_DELAY; // Time in ms the party has to wait until the mobs spawn
    public static int RIFT_MAX_JUMPS;
    public static int RIFT_AUTO_JUMPS_TIME;
    public static int RIFT_AUTO_JUMPS_TIME_RAND;
    public static int RIFT_ENTER_COST_RECRUIT;
    public static int RIFT_ENTER_COST_SOLDIER;
    public static int RIFT_ENTER_COST_OFFICER;
    public static int RIFT_ENTER_COST_CAPTAIN;
    public static int RIFT_ENTER_COST_COMMANDER;
    public static int RIFT_ENTER_COST_HERO;
    public static boolean ALLOW_TALK_WHILE_SITTING;
    public static boolean PARTY_LEADER_ONLY_CAN_INVITE;
    /**
     * Разрешены ли клановые скилы? *
     */
    public static boolean ALLOW_CLANSKILLS;
    /**
     * Разрешено ли изучение скилов трансформации и саб классов без наличия
     * выполненного квеста
     */
    public static boolean ALLOW_LEARN_TRANS_SKILLS_WO_QUEST;
    /**
     * Allow Manor system
     */
    public static boolean ALLOW_MANOR;
    /**
     * Manor Refresh Starting time
     */
    public static int MANOR_REFRESH_TIME;
    /**
     * Manor Refresh Min
     */
    public static int MANOR_REFRESH_MIN;
    /**
     * Manor Next Period Approve Starting time
     */
    public static int MANOR_APPROVE_TIME;
    /**
     * Manor Next Period Approve Min
     */
    public static int MANOR_APPROVE_MIN;
    /**
     * Manor Maintenance Time
     */
    public static int MANOR_MAINTENANCE_PERIOD;
    public static double EVENT_CofferOfShadowsPriceRate;
    public static double EVENT_CofferOfShadowsRewardRate;
    public static double EVENT_APIL_FOOLS_DROP_CHANCE;

    // Luckpi
    public static int MAX_ADENA_TO_EAT;
    public static int ADENA_TO_EAT;
    public static int TIME_IF_NOT_FEED;
    //

    /**
     * Master Yogi event enchant config
     */
    public static int ENCHANT_CHANCE_MASTER_YOGI_STAFF;
    public static int ENCHANT_MAX_MASTER_YOGI_STAFF;
    public static int SAFE_ENCHANT_MASTER_YOGI_STAFF;
    public static boolean CDItemsAllowEnabled;
    public static int[] CDItemsId;
    public static int[] CDItemsCountDropMin;
    public static int[] CDItemsCountDropMax;
    public static double[] CustomDropItemsChance;
    public static boolean CDItemsAllowMinMaxPlayerLvl;
    public static int CDItemsMinPlayerLvl;
    public static int CDItemsMaxPlayerLvl;
    public static boolean CDItemsAllowMinMaxMobLvl;
    public static int CDItemsMinMobLvl;
    public static int CDItemsMaxMobLvl;
    public static boolean CDItemsAllowOnlyRbDrops;
    public static boolean AllowChampionCustomDropItems;
    public static int[] ChampionCDItemsId;
    public static int[] ChampionCDItemsCountDropMin;
    public static int[] ChampionCDItemsCountDropMax;
    public static double[] ChampionCustomDropItemsChance;
    public static boolean ChampionCDItemsAllowMinMaxPlayerLvl;
    public static int ChampionCDItemsMinPlayerLvl;
    public static int ChampionCDItemsMaxPlayerLvl;
    public static boolean ChampionCDItemsAllowMinMaxMobLvl;
    public static int ChampionCDItemsMinMobLvl;
    public static int ChampionCDItemsMaxMobLvl;
    public static boolean ChampionBlueAuraEnabled = true;
    public static boolean ChampionRedAuraEnabled = true;
    public static String[] EVENT_DeathMatchRewards;
    public static int EVENT_DeathMatchTime;
    public static boolean EVENT_DeathMatch_rate;
    public static String[] EVENT_DeathMatchStartTime;
    public static boolean EVENT_DeathMatchCategories;
    public static int EVENT_DeathMatchMaxPlayerInTeam;
    public static int EVENT_DeathMatchMinPlayerInTeam;
    public static boolean EVENT_DeathMatchAllowSummons;
    public static boolean EVENT_DeathMatchAllowBuffs;
    public static boolean EVENT_DeathMatchAllowMultiReg;
    public static String EVENT_DeathMatchCheckWindowMethod;
    public static int EVENT_DeathMatchEventRunningTime;
    public static String[] EVENT_DeathMatchFighterBuffs;
    public static String[] EVENT_DeathMatchMageBuffs;
    public static boolean EVENT_DeathMatchBuffPlayers;
    public static boolean EVENT_DeathMatchrate;
    public static boolean EVENT_GvGDisableEffect;
    public static boolean EVENT_HITMAN_ENABLED;
    public static int EVENT_HITMAN_COST_ITEM_ID;
    public static int EVENT_HITMAN_COST_ITEM_COUNT;
    public static int EVENT_HITMAN_TASKS_PER_PAGE;
    public static String EVENT_HITMAN_ALLOWED_ITEM_LIST[];
    public static double EVENT_TFH_POLLEN_CHANCE;
    public static String EVENT_HITMAN_TITLE;

    //
    public static boolean TMEnabled;
    public static int TMStartHour;
    public static int TMStartMin;
    public static int TMEventInterval;
    public static int TMMobLife;
    public static int BossLifeTime;
    public static int TMTime1;
    public static int TMTime2;
    public static int TMTime3;
    public static int TMTime4;
    public static int TMTime5;
    public static int TMTime6;
    public static int TMWave1;
    public static int TMWave2;
    public static int TMWave3;
    public static int TMWave4;
    public static int TMWave5;
    public static int TMWave6;
    public static int TMWave1Count;
    public static int TMWave2Count;
    public static int TMWave3Count;
    public static int TMWave4Count;
    public static int TMWave5Count;
    public static int TMWave6Count;
    public static int TMBoss;
    public static int[] TMItem;
    public static int[] TMItemCol;
    public static int[] TMItemColBoss;
    public static int[] TMItemChance;
    public static int[] TMItemChanceBoss;
    //

    public static double EVENT_GLITTMEDAL_NORMAL_CHANCE;
    public static double EVENT_GLITTMEDAL_GLIT_CHANCE;

    public static boolean EVENT_CHEST_EVIL_ALLOW;
    public static int EVENT_CHEST_EVIL_ITEM;
    public static int EVENT_CHEST_EVIL_BLUE;
    public static int EVENT_CHEST_EVIL_RED;
    public static int EVENT_CHEST_EVIL_CHANCE;

    public static double EVENT_L2DAY_LETTER_CHANCE;
    public static double EVENT_CHANGE_OF_HEART_CHANCE;
    public static double EVENT_TRICK_OF_TRANS_CHANCE;
    public static double EVENT_MARCH8_DROP_CHANCE;
    public static double EVENT_MARCH8_PRICE_RATE;
    public static int EVENT_MOUSE_COIN_CHANCE;
    public static int EVENT_MOUSE_COIN;
    public static int EVENT_MOUSE_COIN_MIN_COUNT;
    public static int EVENT_MOUSE_COIN_MAX_COUNT;
    public static int EVENT_MOUSE_COIN_COUNT;
    public static int EVENT_BASE_COIN_AFTER_RB;
    public static boolean EVENT_BOUNTY_HUNTERS_ENABLED;
    public static long EVENT_SAVING_SNOWMAN_LOTERY_PRICE;
    public static int EVENT_SAVING_SNOWMAN_REWARDER_CHANCE;
    public static boolean SERVICES_NO_TRADE_ONLY_OFFLINE;
    public static boolean SERVICES_NO_TRADE_BLOCK_ZONE;
    public static double SERVICES_TRADE_TAX;
    public static double SERVICES_OFFSHORE_TRADE_TAX;
    public static boolean SERVICES_OFFSHORE_NO_CASTLE_TAX;
    public static boolean SERVICES_TRADE_TAX_ONLY_OFFLINE;
    public static boolean SERVICES_TRADE_ONLY_FAR;
    public static int SERVICES_TRADE_RADIUS;
    public static int SERVICES_TRADE_MIN_LEVEL;
    public static boolean SERVICES_ENABLE_NO_CARRIER;
    public static int SERVICES_NO_CARRIER_DEFAULT_TIME;
    public static int SERVICES_NO_CARRIER_MAX_TIME;
    public static int SERVICES_NO_CARRIER_MIN_TIME;
    public static boolean SERVICES_PK_PVP_KILL_ENABLE;
    public static int SERVICES_PVP_KILL_REWARD_ITEM;
    public static long SERVICES_PVP_KILL_REWARD_COUNT;
    public static int SERVICES_PK_KILL_REWARD_ITEM;
    public static long SERVICES_PK_KILL_REWARD_COUNT;
    public static boolean SERVICES_PK_PVP_TIE_IF_SAME_IP;
    public static boolean SERVICES_PK_PVP_TIE_IF_SAME_HWID;
    public static boolean ALT_OPEN_CLOAK_SLOT;
    public static boolean ALT_SHOW_SERVER_TIME;
    public static int EVENT_CtfTime;
    public static boolean EVENT_CtFrate;
    public static String[] EVENT_CtFStartTime;
    public static boolean EVENT_CtFCategories;
    public static int EVENT_CtFMaxPlayerInTeam;
    public static int EVENT_CtFMinPlayerInTeam;
    public static boolean EVENT_CtFAllowSummons;
    public static boolean EVENT_CtFAllowBuffs;
    public static boolean EVENT_CtFAllowMultiReg;
    public static String EVENT_CtFCheckWindowMethod;
    public static String[] EVENT_CtFFighterBuffs;
    public static String[] EVENT_CtFMageBuffs;
    public static boolean EVENT_CtFBuffPlayers;
    public static int[] EVENT_CtFBlockItems;
    public static String[] EVENT_CtFRewards;

    /**
     * Geodata config
     */
    public static int GEO_X_FIRST, GEO_Y_FIRST, GEO_X_LAST, GEO_Y_LAST;
    public static boolean ALLOW_GEODATA;
    public static boolean ALLOW_FALL_FROM_WALLS;
    public static boolean ALLOW_KEYBOARD_MOVE;
    public static boolean COMPACT_GEO;
    public static int CLIENT_Z_SHIFT;
    public static int MAX_Z_DIFF;
    public static int MIN_LAYER_HEIGHT;
    public static int REGION_EDGE_MAX_Z_DIFF;

    /**
     * Geodata (Pathfind) config
     */
    public static int PATHFIND_BOOST;
    public static int PATHFIND_MAP_MUL;
    public static boolean PATHFIND_DIAGONAL;
    public static boolean PATH_CLEAN;
    public static int PATHFIND_MAX_Z_DIFF;
    public static long PATHFIND_MAX_TIME;
    public static String PATHFIND_BUFFERS;

    public static boolean DEBUG;

    /* Item-Mall Configs */
    public static int GAME_POINT_ITEM_ID;
    public static int WEAR_DELAY;
    public static boolean GOODS_INVENTORY_ENABLED = false;
    public static boolean EX_NEW_PETITION_SYSTEM;
    public static boolean EX_JAPAN_MINIGAME;
    public static boolean EX_LECTURE_MARK;
    public static boolean EX_USE_TELEPORT_FLAG;

    /* Lic Config */
    public static String USER_NAME;
    public static int NPROTECT_KEY = -1;

    /* Top's Config */
    public static boolean L2TOP_MANAGER_ENABLED;
    public static int L2TOP_MANAGER_INTERVAL;
    public static String L2TOP_WEB_ADDRESS;
    public static String L2TOP_SMS_ADDRESS;
    public static String L2TOP_SERVER_ADDRESS;
    public static int L2TOP_SAVE_DAYS;
    public static int[] L2TOP_REWARD;
    public static String L2TOP_SERVER_PREFIX;
    public static boolean MMOTOP_MANAGER_ENABLED;
    public static int MMOTOP_MANAGER_INTERVAL;
    public static String MMOTOP_WEB_ADDRESS;
    public static String MMOTOP_SERVER_ADDRESS;
    public static int MMOTOP_SAVE_DAYS;
    public static int[] MMOTOP_REWARD;
    public static boolean ALLOW_HOPZONE_VOTE_REWARD;
    public static String HOPZONE_SERVER_LINK;
    public static String HOPZONE_FIRST_PAGE_LINK;
    public static int HOPZONE_VOTES_DIFFERENCE;
    public static int HOPZONE_FIRST_PAGE_RANK_NEEDED;
    public static int HOPZONE_REWARD_CHECK_TIME;
    public static Map<Integer, Integer> HOPZONE_SMALL_REWARD = new FastMap<Integer, Integer>();
    public static Map<Integer, Integer> HOPZONE_BIG_REWARD = new FastMap<Integer, Integer>();
    public static int HOPZONE_DUALBOXES_ALLOWED;
    public static boolean ALLOW_HOPZONE_GAME_SERVER_REPORT;
    public static boolean ALLOW_TOPZONE_VOTE_REWARD;
    public static String TOPZONE_SERVER_LINK;
    public static String TOPZONE_FIRST_PAGE_LINK;
    public static int TOPZONE_VOTES_DIFFERENCE;
    public static int TOPZONE_FIRST_PAGE_RANK_NEEDED;
    public static int TOPZONE_REWARD_CHECK_TIME;
    public static Map<Integer, Integer> TOPZONE_SMALL_REWARD = new FastMap<Integer, Integer>();
    public static Map<Integer, Integer> TOPZONE_BIG_REWARD = new FastMap<Integer, Integer>();
    public static int TOPZONE_DUALBOXES_ALLOWED;
    public static boolean ALLOW_TOPZONE_GAME_SERVER_REPORT;
    public static boolean SMS_PAYMENT_MANAGER_ENABLED;
    public static String SMS_PAYMENT_WEB_ADDRESS;
    public static int SMS_PAYMENT_MANAGER_INTERVAL;
    public static int SMS_PAYMENT_SAVE_DAYS;
    public static String SMS_PAYMENT_SERVER_ADDRESS;
    public static int[] SMS_PAYMENT_REWARD;
    public static boolean AUTH_SERVER_GM_ONLY;
    public static boolean AUTH_SERVER_BRACKETS;
    public static boolean AUTH_SERVER_IS_PVP;
    public static int AUTH_SERVER_AGE_LIMIT;
    public static int AUTH_SERVER_SERVER_TYPE;

    /* Skill Configs */
    public static boolean ALLOW_CHAIN_HEAL_CURSED;
    public static boolean ALLOW_CHAIN_HEAL_FRIEND;
    public static boolean ALLOW_CHAIN_HEAL_EVENT;

    // Quest Rates Settings    
    public static Map<String, double[]> QUEST_RATES = new HashMap();
    public static double[] EMPTY_RATES = {1.0, 1.0, 1.0, 1.0};
    public static int EXP = 0;
    public static int SP = 1;
    public static int DROP = 2;
    public static int REWARD = 3;

    /* Version Configs */
    public static String SERVER_VERSION;
    public static String SERVER_BUILD_DATE;

    /*Конфиг для ПА*/
    public static int SERVICES_RATE_TYPE;
    public static int SERVICES_RATE_CREATE_PA;

    public static long MAX_PLAYER_CONTRIBUTION;

    /* Конфигурации Епиков */
    public static int FIXINTERVALOFANTHARAS_HOUR;
    public static int FIXINTERVALOFBAIUM_HOUR;
    public static int RANDOMINTERVALOFBAIUM;
    public static int FIXINTERVALOFBAYLORSPAWN_HOUR;
    public static int RANDOMINTERVALOFBAYLORSPAWN;
    public static int FIXINTERVALOFBELETHSPAWN_HOUR;
    public static int FIXINTERVALOFSAILRENSPAWN_HOUR;
    public static int RANDOMINTERVALOFSAILRENSPAWN;
    public static int FIXINTERVALOFVALAKAS;
    public static int RANDOMINTERVALOFVALAKAS;
    public static int EPIC_BOSS_SPAWN_ANNON;

    /* Количество очков репутации необходимое для поднятия уровня клану.*/
    public static int CLAN_LEVEL_6_COST;
    public static int CLAN_LEVEL_7_COST;
    public static int CLAN_LEVEL_8_COST;
    public static int CLAN_LEVEL_9_COST;
    public static int CLAN_LEVEL_10_COST;
    public static int CLAN_LEVEL_11_COST;

    /* Количество человек в клане необходимое для поднятия уровня клану.*/
    public static int CLAN_LEVEL_6_REQUIREMEN;
    public static int CLAN_LEVEL_7_REQUIREMEN;
    public static int CLAN_LEVEL_8_REQUIREMEN;
    public static int CLAN_LEVEL_9_REQUIREMEN;
    public static int CLAN_LEVEL_10_REQUIREMEN;
    public static int CLAN_LEVEL_11_REQUIREMEN;
    public static int BLOOD_OATHS;
    public static int BLOOD_PLEDGES;
    public static int MIN_ACADEM_POINT;
    public static int MAX_ACADEM_POINT;
    public static int MAX_VORTEX_BOSS_COUNT;
    public static int TIME_DESPAWN_VORTEX_BOSS;
    public static boolean ZONE_PVP_COUNT;
    public static boolean TRANSFORM_ON_DEATH;
    public static boolean SIEGE_PVP_COUNT;
    public static boolean EPIC_EXPERTISE_PENALTY;
    public static boolean EXPERTISE_PENALTY;
    public static boolean ALT_DISPEL_MUSIC;
    public static int ALT_MUSIC_LIMIT;
    public static int ALT_DEBUFF_LIMIT;
    public static int ALT_TRIGGER_LIMIT;
    public static boolean ENABLE_MODIFY_SKILL_DURATION;
    public static boolean ALT_TIME_MODE_SKILL_DURATION;
    public static TIntIntHashMap SKILL_DURATION_LIST;
    public static boolean COMMUNITYBOARD_BOARD_ALT_ENABLED;
    public static int COMMUNITYBOARD_BUFF_PICE_NG;
    public static int COMMUNITYBOARD_BUFF_PICE_D;
    public static int COMMUNITYBOARD_BUFF_PICE_C;
    public static int COMMUNITYBOARD_BUFF_PICE_B;
    public static int COMMUNITYBOARD_BUFF_PICE_A;
    public static int COMMUNITYBOARD_BUFF_PICE_S;
    public static int COMMUNITYBOARD_BUFF_PICE_S80;
    public static int COMMUNITYBOARD_BUFF_PICE_S84;
    public static int COMMUNITYBOARD_BUFF_PICE_NG_GR;
    public static int COMMUNITYBOARD_BUFF_PICE_D_GR;
    public static int COMMUNITYBOARD_BUFF_PICE_C_GR;
    public static int COMMUNITYBOARD_BUFF_PICE_B_GR;
    public static int COMMUNITYBOARD_BUFF_PICE_A_GR;
    public static int COMMUNITYBOARD_BUFF_PICE_S_GR;
    public static int COMMUNITYBOARD_BUFF_PICE_S80_GR;
    public static int COMMUNITYBOARD_BUFF_PICE_S84_GR;
    public static int COMMUNITYBOARD_TELEPORT_PICE_NG;
    public static int COMMUNITYBOARD_TELEPORT_PICE_D;
    public static int COMMUNITYBOARD_TELEPORT_PICE_C;
    public static int COMMUNITYBOARD_TELEPORT_PICE_B;
    public static int COMMUNITYBOARD_TELEPORT_PICE_A;
    public static int COMMUNITYBOARD_TELEPORT_PICE_S;
    public static int COMMUNITYBOARD_TELEPORT_PICE_S80;
    public static int COMMUNITYBOARD_TELEPORT_PICE_S84;
    public static double ALT_VITALITY_NEVIT_UP_POINT;
    public static double ALT_VITALITY_NEVIT_POINT;
    public static boolean SERVICES_LVL_ENABLED;
    public static int SERVICES_LVL_UP_MAX;
    public static int SERVICES_LVL_UP_PRICE;
    public static int SERVICES_LVL_UP_ITEM;
    public static int SERVICES_LVL_DOWN_MAX;
    public static int SERVICES_LVL_DOWN_PRICE;
    public static int SERVICES_LVL_DOWN_ITEM;
    public static boolean SERVICES_ACC_MOVE_ENABLED;
    public static int SERVICES_ACC_MOVE_ITEM;
    public static int SERVICES_ACC_MOVE_PRICE;
    public static boolean SERVICES_ACTIVATE_SUB;
    public static int SERVICES_ACTIVATE_SUB_ITEM;
    public static int SERVICES_ACTIVATE_SUB_PRICE;
    public static boolean ALLOW_INSTANCES_LEVEL_MANUAL;
    public static boolean ALLOW_INSTANCES_PARTY_MANUAL;
    public static int INSTANCES_LEVEL_MIN;
    public static int INSTANCES_LEVEL_MAX;
    public static int INSTANCES_PARTY_MIN;
    public static int INSTANCES_PARTY_MAX;
    // Items setting
    public static boolean CAN_BE_TRADED_NO_TARADEABLE;
    public static boolean CAN_BE_TRADED_NO_SELLABLE;
    public static boolean CAN_BE_TRADED_NO_STOREABLE;
    public static boolean CAN_BE_TRADED_SHADOW_ITEM;
    public static boolean CAN_BE_TRADED_HERO_WEAPON;
    public static boolean CAN_BE_WH_NO_TARADEABLE;
    public static boolean CAN_BE_CWH_NO_TARADEABLE;
    public static boolean CAN_BE_CWH_IS_AUGMENTED;
    public static boolean CAN_BE_WH_IS_AUGMENTED;
    public static boolean ALLOW_SOUL_SPIRIT_SHOT_INFINITELY;
    public static boolean ALLOW_SOUL_SPIRIT_SHOT_INFINITELY_PET;
    public static boolean ALLOW_ARROW_INFINITELY;
    public static boolean ALLOW_START_ITEMS;
    public static int[] START_ITEMS_MAGE;
    public static int[] START_ITEMS_MAGE_COUNT;
    public static int[] START_ITEMS_FITHER;
    public static int[] START_ITEMS_FITHER_COUNT;
    public static int HELLBOUND_LEVEL;
    public static boolean HELLBOUND_ON;
    public static boolean COMMUNITYBOARD_ENCHANT_ENABLED;
    public static boolean COMMUNITYBOARD_EXCHANGE_ENABLED;
    public static boolean ALLOW_BBS_ENCHANT_ELEMENTAR;
    public static boolean ALLOW_BBS_ENCHANT_ATT;
    public static int COMMUNITYBOARD_ENCHANT_ITEM;
    public static int COMMUNITYBOARD_MAX_ENCHANT;
    public static int[] COMMUNITYBOARD_ENCHANT_LVL;
    public static int[] COMMUNITYBOARD_ENCHANT_PRICE_WEAPON;
    public static int[] COMMUNITYBOARD_ENCHANT_PRICE_ARMOR;
    public static int[] COMMUNITYBOARD_ENCHANT_ATRIBUTE_LVL_WEAPON;
    public static int[] COMMUNITYBOARD_ENCHANT_ATRIBUTE_PRICE_WEAPON;
    public static int[] COMMUNITYBOARD_ENCHANT_ATRIBUTE_LVL_ARMOR;
    public static int[] COMMUNITYBOARD_ENCHANT_ATRIBUTE_PRICE_ARMOR;
    public static boolean COMMUNITYBOARD_ENCHANT_ATRIBUTE_PVP;
    public static boolean SUB_MANAGER_ALLOW;

    // [ Community Commission settings ]==================================================================================
    public static boolean COMMUNITY_COMMISSION_ALLOW;
    public static int[] COMMUNITY_COMMISSION_ARMOR_PRICE;
    public static int[] COMMUNITY_COMMISSION_WEAPON_PRICE;
    public static int[] COMMUNITY_COMMISSION_JEWERLY_PRICE;
    public static int[] COMMUNITY_COMMISSION_OTHER_PRICE;
    public static int[] COMMUNITY_COMMISSION_ALLOW_ITEMS;
    public static int COMMUNITY_COMMISSION_MAX_ENCHANT;
    public static int[] COMMUNITY_COMMISSION_NOT_ALLOW_ITEMS;
    public static boolean COMMUNITY_COMMISSION_ALLOW_UNDERWEAR;
    public static boolean COMMUNITY_COMMISSION_ALLOW_CLOAK;
    public static boolean COMMUNITY_COMMISSION_ALLOW_BRACELET;
    public static boolean COMMUNITY_COMMISSION_ALLOW_AUGMENTED;
    public static boolean COMMUNITY_COMMISSION_ALLOW_EQUIPPED;
    public static int COMMUNITY_COMMISSION_COUNT_TO_PAGE;
    public static int[] COMMUNITY_COMMISSION_MONETS;
    public static int COMMUNITY_COMMISSION_SAVE_DAYS;

    public static boolean COMMUNITY_WAREHOUSE_RANGE_Z;
    public static String SERVICES_ACADEM_REWARD;

    public static int[][] TELE_PPRISE;
    public static int[][] BUFF_PPRISE;

    public static int EVENT_LastHeroChanceToStart;

    public static String[] EVENT_TvTRewards;
    public static String[] EVENTS_DISALLOWED_SKILLS;
    public static int EVENT_TvTTime;
    public static boolean EVENT_TvT_rate;
    public static String[] EVENT_TvTStartTime;
    public static boolean EVENT_TvTCategories;
    public static int EVENT_TvTMaxPlayerInTeam;
    public static int EVENT_TvTMinPlayerInTeam;
    public static boolean EVENT_TvTAllowSummons;
    public static boolean EVENT_TvTAllowBuffs;
    public static boolean EVENT_TvTAllowMultiReg;
    public static String EVENT_TvTCheckWindowMethod;
    public static int EVENT_TvTEventRunningTime;
    public static String[] EVENT_TvTFighterBuffs;
    public static String[] EVENT_TvTMageBuffs;
    public static boolean EVENT_TvTBuffPlayers;
    public static boolean EVENT_TvTrate;
    public static boolean EVENT_TvTAllowParty;
    public static int[] EVENT_TvTBlockItems;

    public static boolean ALLOW_MULTILANG_GATEKEEPER;
    public static boolean SERVICES_PRIME_SHOP_ENABLED;
    public static boolean SERVICES_POLIMORF_ENABLED;
    public static boolean LOAD_CUSTOM_SPAWN;
    public static boolean SAVE_GM_SPAWN;
    public static boolean ALLOW_PHANTOM_PLAYERS;
    public static boolean ALLOW_PHANTOM_SETS;
    public static int PHANTOM_MIN_CLASS_ID;
    public static int PHANTOM_MAX_CLASS_ID;
    public static String PHANTOM_PLAYERS_AKK;
    public static int PHANTOM_PLAYERS_COUNT_FIRST;
    public static boolean PHANTOM_PLAYERS_SOULSHOT_ANIM;
    public static long PHANTOM_PLAYERS_DELAY_FIRST;
    public static long PHANTOM_PLAYERS_DESPAWN_FIRST;
    public static long PHANTOM_PLAYERS_DELAY_SPAWN_FIRST;
    public static long PHANTOM_PLAYERS_DELAY_DESPAWN_FIRST;
    public static int PHANTOM_PLAYERS_COUNT_NEXT;
    public static long PHANTOM_PLAYERS_DELAY_NEXT;
    public static long PHANTOM_PLAYERS_DESPAWN_NEXT;
    public static long PHANTOM_PLAYERS_DELAY_SPAWN_NEXT;
    public static long PHANTOM_PLAYERS_DELAY_DESPAWN_NEXT;
    public static int PHANTOM_PLAYERS_ENCHANT_MIN;
    public static int PHANTOM_PLAYERS_ENCHANT_MAX;
    public static long PHANTOM_PLAYERS_CP_REUSE_TIME;
    public static final FastList<Integer> PHANTOM_PLAYERS_NAME_CLOLORS = new FastList<>();
    public static final FastList<Integer> PHANTOM_PLAYERS_TITLE_CLOLORS = new FastList<>();
    public static int PHANTOM_MAX_PATK_BOW;
    public static int PHANTOM_MAX_MDEF_BOW;
    public static int PHANTOM_MAX_PSPD_BOW;
    public static int PHANTOM_MAX_PDEF_BOW;
    public static int PHANTOM_MAX_MATK_BOW;
    public static int PHANTOM_MAX_MSPD_BOW;
    public static int PHANTOM_MAX_HP_BOW;
    public static int PHANTOM_MAX_PATK_MAG;
    public static int PHANTOM_MAX_MDEF_MAG;
    public static int PHANTOM_MAX_PSPD_MAG;
    public static int PHANTOM_MAX_PDEF_MAG;
    public static int PHANTOM_MAX_MATK_MAG;
    public static int PHANTOM_MAX_MSPD_MAG;
    public static int PHANTOM_MAX_HP_MAG;
    public static int PHANTOM_MAX_PATK_HEAL;
    public static int PHANTOM_MAX_MDEF_HEAL;
    public static int PHANTOM_MAX_PSPD_HEAL;
    public static int PHANTOM_MAX_PDEF_HEAL;
    public static int PHANTOM_MAX_MATK_HEAL;
    public static int PHANTOM_MAX_MSPD_HEAL;
    public static int PHANTOM_MAX_HP_HEAL;

    // Buffer options
    public static boolean BBSBUFFER_ENABLED;
    public static int SCHEME_LIMIT;
    public static int SCHEME_BUFF_LIMIT;
    public static boolean BUFF_GROUPS_ENABLED;
    public static int BUFF_GROUPS_MENU_TYPE;
    public static boolean CUSTOM_BUFF_TIMES_ENABLED;
    public static int BUFF_LIST_LENGTH;
    public static boolean BUFF_LIST_WITH_3_COLUMNS;
    public static Boolean ALL_IN_ONE_PAGE_ENABLED;

    public static boolean BBSBUFFER_ENABLED_PRICE;

    public static FastList<Integer> PREDEFINED_SCHEME_1 = new FastList<>();
    public static FastList<Integer> PREDEFINED_SCHEME_2 = new FastList<>();

    // Check buff options
    public static boolean CHECK_DEATH;
    public static boolean CHECK_ACTION;
    public static boolean CHECK_OLY;
    public static boolean CHECK_EVENT;
    public static boolean CHECK_FLY;
    public static boolean CHECK_BOAT;
    public static boolean CHECK_MOUNTED;
    public static boolean CHECK_CANT_MOVE;
    public static boolean CHECK_STORE_MODE;
    public static boolean CHECK_FISHING;
    public static boolean CHECK_TEMP_ACTION;
    public static boolean CHECK_DUEL;
    public static boolean CHECK_CURSED_WEAPON;
    public static boolean CHECK_PK;
    public static boolean CHECK_CLAN_LEADER;
    public static boolean CHECK_NOBLE;
    public static boolean CHECK_SIEGE;
    public static boolean CHECK_PEACEZONE;
    public static boolean CHECK_ALLOWED_ZONE;
    public static boolean CHECK_JAIL;

    // Check restore options
    public static boolean RESTORE_CHECK_DEATH;
    public static boolean RESTORE_CHECK_ACTION;
    public static boolean RESTORE_CHECK_OLY;
    public static boolean RESTORE_CHECK_EVENT;
    public static boolean RESTORE_CHECK_FLY;
    public static boolean RESTORE_CHECK_BOAT;
    public static boolean RESTORE_CHECK_MOUNTED;
    public static boolean RESTORE_CHECK_CANT_MOVE;
    public static boolean RESTORE_CHECK_STORE_MODE;
    public static boolean RESTORE_CHECK_FISHING;
    public static boolean RESTORE_CHECK_TEMP_ACTION;
    public static boolean RESTORE_CHECK_DUEL;
    public static boolean RESTORE_CHECK_CURSED_WEAPON;
    public static boolean RESTORE_CHECK_PK;
    public static boolean RESTORE_CHECK_CLAN_LEADER;
    public static boolean RESTORE_CHECK_NOBLE;
    public static boolean RESTORE_CHECK_SIEGE;
    public static boolean RESTORE_CHECK_PEACEZONE;
    public static boolean RESTORE_CHECK_ALLOWED_ZONE;
    public static boolean RESTORE_CHECK_JAIL;

    // Auto-rebuff
    public static boolean AUTOREBUFF_ENABLED;
    public static int AUTOREBUFF_DELAY;

    // Check Auto-rebuff options
    public static boolean AUTOREBUFF_CHECK_DEATH;
    public static boolean AUTOREBUFF_CHECK_OLY;
    public static boolean AUTOREBUFF_CHECK_EVENT;
    public static boolean AUTOREBUFF_CHECK_STORE_MODE;
    public static boolean AUTOREBUFF_CHECK_FISHING;
    public static boolean AUTOREBUFF_CHECK_TEMP_ACTION;
    public static boolean AUTOREBUFF_CHECK_DUEL;
    public static boolean AUTOREBUFF_CHECK_CURSED_WEAPON;
    public static boolean AUTOREBUFF_CHECK_PK;
    public static boolean AUTOREBUFF_CHECK_SIEGE;
    public static boolean AUTOREBUFF_CHECK_PEACEZONE;
    public static boolean AUTOREBUFF_CHECK_ALLOWED_ZONE;
    public static boolean AUTOREBUFF_CHECK_JAIL;

    // Zones
    public static FastList<Integer> ALLOWED_ZONES_IDS = new FastList<>();

    // Database
    public static int CHARACTER_TABLE_UPDATE_INTERVAL;

    public static void loadBBSBufferConfig() {

        ExProperties bbdbuferSettings = load(BBSBUFFER_CONFIG_FILE);

        BBSBUFFER_ENABLED = bbdbuferSettings.getProperty("BBSBufferEnabled", false);
        SCHEME_LIMIT = bbdbuferSettings.getProperty("SchemeLimit", 10);
        if (SCHEME_LIMIT < 2 || SCHEME_LIMIT > 10) {
            SCHEME_LIMIT = 10;
        }

        SCHEME_BUFF_LIMIT = bbdbuferSettings.getProperty("SchemeBuffLimit", 32);
        if (SCHEME_BUFF_LIMIT <= 0) {
            SCHEME_BUFF_LIMIT = 32;
        }

        BBSBUFFER_ENABLED_PRICE = bbdbuferSettings.getProperty("BBSBufferEnabledPrice", false);

        BUFF_GROUPS_ENABLED = bbdbuferSettings.getProperty("BuffGroupsEnabled", false);
        BUFF_GROUPS_MENU_TYPE = bbdbuferSettings.getProperty("BuffGroupsMenuType", 0);
        CUSTOM_BUFF_TIMES_ENABLED = bbdbuferSettings.getProperty("CustomBuffTimesEnabled", false);

        BUFF_LIST_WITH_3_COLUMNS = bbdbuferSettings.getProperty("BuffListWith3Columns", false);
        BUFF_LIST_LENGTH = bbdbuferSettings.getProperty("BuffListLength", 20);
        if (!BUFF_LIST_WITH_3_COLUMNS && BUFF_LIST_LENGTH < 2 && BUFF_LIST_LENGTH > 20) {
            BUFF_LIST_LENGTH = 20;
        } else if (BUFF_LIST_WITH_3_COLUMNS && BUFF_LIST_LENGTH < 2 && BUFF_LIST_LENGTH > 30) {
            BUFF_LIST_LENGTH = 30;
        }

        String tempStr = bbdbuferSettings.getProperty("PredefinedScheme1");
        if (tempStr != null && tempStr.length() > 0) {
            for (String buffId : tempStr.split(",")) {
                try {
                    PREDEFINED_SCHEME_1.add(Integer.parseInt(buffId));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        tempStr = bbdbuferSettings.getProperty("PredefinedScheme2");
        if (tempStr != null && tempStr.length() > 0) {
            for (String buffId : tempStr.split(",")) {
                try {
                    PREDEFINED_SCHEME_2.add(Integer.parseInt(buffId));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        // buff
        CHECK_DEATH = bbdbuferSettings.getProperty("CheckDeath", true);
        CHECK_ACTION = bbdbuferSettings.getProperty("CheckAction", true);
        CHECK_OLY = bbdbuferSettings.getProperty("CheckOlympiad", true);
        CHECK_EVENT = bbdbuferSettings.getProperty("CheckEvent", true);
        CHECK_FLY = bbdbuferSettings.getProperty("CheckFly", true);
        CHECK_BOAT = bbdbuferSettings.getProperty("CheckBoat", true);
        CHECK_MOUNTED = bbdbuferSettings.getProperty("CheckMounted", true);
        CHECK_CANT_MOVE = bbdbuferSettings.getProperty("CheckCantMove", true);
        CHECK_STORE_MODE = bbdbuferSettings.getProperty("CheckStoreMode", true);
        CHECK_FISHING = bbdbuferSettings.getProperty("CheckFishing", true);
        CHECK_TEMP_ACTION = bbdbuferSettings.getProperty("CheckTempAction", true);
        CHECK_DUEL = bbdbuferSettings.getProperty("CheckDuel", true);
        CHECK_CURSED_WEAPON = bbdbuferSettings.getProperty("CheckCursedWeapon", true);
        CHECK_PK = bbdbuferSettings.getProperty("CheckPK", true);
        CHECK_CLAN_LEADER = bbdbuferSettings.getProperty("CheckClanLeader", false);
        CHECK_NOBLE = bbdbuferSettings.getProperty("CheckNoble", false);
        CHECK_SIEGE = bbdbuferSettings.getProperty("CheckSiege", false);
        CHECK_PEACEZONE = bbdbuferSettings.getProperty("CheckPeacefulZone", false);
        CHECK_ALLOWED_ZONE = bbdbuferSettings.getProperty("CheckAllowedZone", false);
        CHECK_JAIL = bbdbuferSettings.getProperty("CheckJail", true);

        // restore
        RESTORE_CHECK_DEATH = bbdbuferSettings.getProperty("RestoreCheckDeath", true);
        RESTORE_CHECK_ACTION = bbdbuferSettings.getProperty("RestoreCheckAction", true);
        RESTORE_CHECK_OLY = bbdbuferSettings.getProperty("RestoreCheckOlympiad", true);
        RESTORE_CHECK_EVENT = bbdbuferSettings.getProperty("RestoreCheckEvent", true);
        RESTORE_CHECK_FLY = bbdbuferSettings.getProperty("RestoreCheckFly", true);
        RESTORE_CHECK_BOAT = bbdbuferSettings.getProperty("RestoreCheckBoat", true);
        RESTORE_CHECK_MOUNTED = bbdbuferSettings.getProperty("RestoreCheckMounted", true);
        RESTORE_CHECK_CANT_MOVE = bbdbuferSettings.getProperty("RestoreCheckCantMove", true);
        RESTORE_CHECK_STORE_MODE = bbdbuferSettings.getProperty("RestoreCheckStoreMode", true);
        RESTORE_CHECK_FISHING = bbdbuferSettings.getProperty("RestoreCheckFishing", true);
        RESTORE_CHECK_TEMP_ACTION = bbdbuferSettings.getProperty("RestoreCheckTempAction", true);
        RESTORE_CHECK_DUEL = bbdbuferSettings.getProperty("RestoreCheckDuel", true);
        RESTORE_CHECK_CURSED_WEAPON = bbdbuferSettings.getProperty("RestoreCheckCursedWeapon", true);
        RESTORE_CHECK_PK = bbdbuferSettings.getProperty("RestoreCheckPK", true);
        RESTORE_CHECK_CLAN_LEADER = bbdbuferSettings.getProperty("RestoreCheckClanLeader", false);
        RESTORE_CHECK_NOBLE = bbdbuferSettings.getProperty("RestoreCheckNoble", false);
        RESTORE_CHECK_SIEGE = bbdbuferSettings.getProperty("RestoreCheckSiege", false);
        RESTORE_CHECK_PEACEZONE = bbdbuferSettings.getProperty("RestoreCheckPeacefulZone", false);
        RESTORE_CHECK_ALLOWED_ZONE = bbdbuferSettings.getProperty("RestoreCheckAllowedZone", false);
        RESTORE_CHECK_JAIL = bbdbuferSettings.getProperty("RestoreCheckJail", true);

        ALL_IN_ONE_PAGE_ENABLED = bbdbuferSettings.getProperty("AllInOnePageEnabled", false);

        CHARACTER_TABLE_UPDATE_INTERVAL = (bbdbuferSettings.getProperty("CharacterTableUpdateInterval", 60) * 60000);

        // auto-rebuff
        AUTOREBUFF_ENABLED = bbdbuferSettings.getProperty("AutoRebuffEnabled", false);
        AUTOREBUFF_DELAY = (bbdbuferSettings.getProperty("AutoRebuffDelaySecond", 30) * 1000);

        AUTOREBUFF_CHECK_DEATH = bbdbuferSettings.getProperty("AutoRebuffCheckDeath", true);
        AUTOREBUFF_CHECK_OLY = bbdbuferSettings.getProperty("AutoRebuffCheckOlympiad", true);
        AUTOREBUFF_CHECK_EVENT = bbdbuferSettings.getProperty("AutoRebuffCheckEvent", true);
        AUTOREBUFF_CHECK_STORE_MODE = bbdbuferSettings.getProperty("AutoRebuffCheckStoreMode", true);
        AUTOREBUFF_CHECK_FISHING = bbdbuferSettings.getProperty("AutoRebuffCheckFishing", true);
        AUTOREBUFF_CHECK_TEMP_ACTION = bbdbuferSettings.getProperty("AutoRebuffCheckTempAction", true);
        AUTOREBUFF_CHECK_DUEL = bbdbuferSettings.getProperty("AutoRebuffCheckDuel", true);
        AUTOREBUFF_CHECK_CURSED_WEAPON = bbdbuferSettings.getProperty("AutoRebuffCheckCursedWeapon", true);
        AUTOREBUFF_CHECK_PK = bbdbuferSettings.getProperty("AutoRebuffCheckPK", true);
        AUTOREBUFF_CHECK_SIEGE = bbdbuferSettings.getProperty("AutoRebuffCheckSiege", false);
        AUTOREBUFF_CHECK_PEACEZONE = bbdbuferSettings.getProperty("AutoRebuffCheckPeacefulZone", false);
        AUTOREBUFF_CHECK_ALLOWED_ZONE = bbdbuferSettings.getProperty("AutoRebuffCheckAllowedZone", false);
        AUTOREBUFF_CHECK_JAIL = bbdbuferSettings.getProperty("AutoRebuffCheckJail", true);

        tempStr = bbdbuferSettings.getProperty("AllowedZonesIds");
        if (tempStr != null && tempStr.length() > 0) {
            for (String rZoneId : tempStr.split(",")) {
                try {
                    ALLOWED_ZONES_IDS.add(Integer.parseInt(rZoneId));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public static void loadServerConfig() {
        ExProperties serverSettings = load(CONFIGURATION_FILE);

        GAME_SERVER_LOGIN_HOST = serverSettings.getProperty("LoginHost", "127.0.0.1");
        GAME_SERVER_LOGIN_PORT = serverSettings.getProperty("LoginPort", 9013);
        GAME_SERVER_LOGIN_CRYPT = serverSettings.getProperty("LoginUseCrypt", true);

        AUTH_SERVER_AGE_LIMIT = serverSettings.getProperty("ServerAgeLimit", 0);
        AUTH_SERVER_GM_ONLY = serverSettings.getProperty("ServerGMOnly", false);
        AUTH_SERVER_BRACKETS = serverSettings.getProperty("ServerBrackets", false);
        AUTH_SERVER_IS_PVP = serverSettings.getProperty("PvPServer", false);
        for (String a : serverSettings.getProperty("ServerType", ArrayUtils.EMPTY_STRING_ARRAY)) {
            if (a.trim().isEmpty()) {
                continue;
            }

            ServerType t = ServerType.valueOf(a.toUpperCase());
            AUTH_SERVER_SERVER_TYPE |= t.getMask();
        }

        GAME_FORWARDING_NET = serverSettings.getProperty("ForwardingNet", true);
        GAME_FORWARDING_NET_HOST = serverSettings.getProperty("ForwardingNetIP", "127.0.0.1");
        GAME_FORWARDING_NET_SERVER_ID = serverSettings.getProperty("ForwardingNetServerID", 2);

        SECOND_AUTH_ENABLED = serverSettings.getProperty("SAEnabled", false);
        SECOND_AUTH_BAN_ACC = serverSettings.getProperty("SABanAccEnabled", false);
        SECOND_AUTH_STRONG_PASS = serverSettings.getProperty("SAStrongPass", false);
        SECOND_AUTH_MAX_ATTEMPTS = serverSettings.getProperty("SAMaxAttemps", 5);
        SECOND_AUTH_BAN_TIME = serverSettings.getProperty("SABanTime", 480);
        SECOND_AUTH_REC_LINK = serverSettings.getProperty("SARecoveryLink", "http://www.my-domain.com/charPassRec.php");

        INTERNAL_HOSTNAME = serverSettings.getProperty("InternalHostname", "*");
        EXTERNAL_HOSTNAME = serverSettings.getProperty("ExternalHostname", "*");
        ADVIPSYSTEM = serverSettings.getProperty("AdvIPSystem", false);
        REQUEST_ID = serverSettings.getProperty("RequestServerID", 0);
        ACCEPT_ALTERNATE_ID = serverSettings.getProperty("AcceptAlternateID", true);

        GAMESERVER_HOSTNAME = serverSettings.getProperty("GameserverHostname");
        PORTS_GAME = serverSettings.getProperty("GameserverPort", new int[]{7777});

        EVERYBODY_HAS_ADMIN_RIGHTS = serverSettings.getProperty("EverybodyHasAdminRights", false);

        HIDE_GM_STATUS = serverSettings.getProperty("HideGMStatus", false);
        SHOW_GM_LOGIN = serverSettings.getProperty("ShowGMLogin", true);
        SAVE_GM_EFFECTS = serverSettings.getProperty("SaveGMEffects", false);

        CNAME_TEMPLATE = serverSettings.getProperty("CnameTemplate", "[A-Za-z0-9\u0410-\u042f\u0430-\u044f]{2,16}");
        CLAN_NAME_TEMPLATE = serverSettings.getProperty("ClanNameTemplate", "[A-Za-z0-9\u0410-\u042f\u0430-\u044f]{3,16}");
        CLAN_TITLE_TEMPLATE = serverSettings.getProperty("ClanTitleTemplate", "[A-Za-z0-9\u0410-\u042f\u0430-\u044f \\p{Punct}]{1,16}");
        ALLY_NAME_TEMPLATE = serverSettings.getProperty("AllyNameTemplate", "[A-Za-z0-9\u0410-\u042f\u0430-\u044f]{3,16}");

        AUTODESTROY_ITEM_AFTER = serverSettings.getProperty("AutoDestroyDroppedItemAfter", 0);
        AUTODESTROY_PLAYER_ITEM_AFTER = serverSettings.getProperty("AutoDestroyPlayerDroppedItemAfter", 0);
        DELETE_DAYS = serverSettings.getProperty("DeleteCharAfterDays", 7);
        PURGE_BYPASS_TASK_FREQUENCY = serverSettings.getProperty("PurgeTaskFrequency", 60);

        try {
            DATAPACK_ROOT = new File(serverSettings.getProperty("DatapackRoot", ".")).getCanonicalFile();
        } catch (IOException e) {
            _log.error("", e);
        }

        ALLOW_DISCARDITEM = serverSettings.getProperty("AllowDiscardItem", true);
        ALLOW_MAIL = serverSettings.getProperty("AllowMail", true);
        ALLOW_WAREHOUSE = serverSettings.getProperty("AllowWarehouse", true);
        ALLOW_WATER = serverSettings.getProperty("AllowWater", true);
        ALLOW_CURSED_WEAPONS = serverSettings.getProperty("AllowCursedWeapons", false);
        DROP_CURSED_WEAPONS_ON_KICK = serverSettings.getProperty("DropCursedWeaponsOnKick", false);

        MIN_PROTOCOL_REVISION = serverSettings.getProperty("MinProtocolRevision", 267);
        MAX_PROTOCOL_REVISION = serverSettings.getProperty("MaxProtocolRevision", 271);

        AUTOSAVE = serverSettings.getProperty("Autosave", true);

        MAXIMUM_ONLINE_USERS = serverSettings.getProperty("MaximumOnlineUsers", 3000);
        ONLINE_PLUS = serverSettings.getProperty("OnlineUsersPlus", 1);

        DATABASE_DRIVER = serverSettings.getProperty("Driver", "com.mysql.jdbc.Driver");
        DATABASE_MAX_CONNECTIONS = serverSettings.getProperty("MaximumDbConnections", 10);
        DATABASE_MAX_IDLE_TIMEOUT = serverSettings.getProperty("MaxIdleConnectionTimeout", 600);
        DATABASE_IDLE_TEST_PERIOD = serverSettings.getProperty("IdleConnectionTestPeriod", 60);

        DATABASE_URL = serverSettings.getProperty("URL", "jdbc:mysql://localhost/l2jdb");
        DATABASE_LOGIN = serverSettings.getProperty("Login", "root");
        DATABASE_PASSWORD = serverSettings.getProperty("Password", "");

        USER_INFO_INTERVAL = serverSettings.getProperty("UserInfoInterval", 100L);
        BROADCAST_STATS_INTERVAL = serverSettings.getProperty("BroadcastStatsInterval", true);
        BROADCAST_CHAR_INFO_INTERVAL = serverSettings.getProperty("BroadcastCharInfoInterval", 100L);

        EFFECT_TASK_MANAGER_COUNT = serverSettings.getProperty("EffectTaskManagers", 2);

        SCHEDULED_THREAD_POOL_SIZE = serverSettings.getProperty("ScheduledThreadPoolSize", NCPUS * 4);
        EXECUTOR_THREAD_POOL_SIZE = serverSettings.getProperty("ExecutorThreadPoolSize", NCPUS * 2);

        ENABLE_RUNNABLE_STATS = serverSettings.getProperty("EnableRunnableStats", false);

        SELECTOR_CONFIG.SLEEP_TIME = serverSettings.getProperty("SelectorSleepTime", 10L);
        SELECTOR_CONFIG.INTEREST_DELAY = serverSettings.getProperty("InterestDelay", 30L);
        SELECTOR_CONFIG.MAX_SEND_PER_PASS = serverSettings.getProperty("MaxSendPerPass", 32);
        SELECTOR_CONFIG.READ_BUFFER_SIZE = serverSettings.getProperty("ReadBufferSize", 65536);
        SELECTOR_CONFIG.WRITE_BUFFER_SIZE = serverSettings.getProperty("WriteBufferSize", 131072);
        SELECTOR_CONFIG.HELPER_BUFFER_COUNT = serverSettings.getProperty("BufferPoolSize", 64);

        DEFAULT_LANG = serverSettings.getProperty("DefaultLang", "ru");
        RESTART_AT_TIME = serverSettings.getProperty("AutoRestartAt", "0 5 * * *");
        SHIFT_BY = serverSettings.getProperty("HShift", 12);
        SHIFT_BY_Z = serverSettings.getProperty("VShift", 11);
        MAP_MIN_Z = serverSettings.getProperty("MapMinZ", -32768);
        MAP_MAX_Z = serverSettings.getProperty("MapMaxZ", 32767);

        MOVE_PACKET_DELAY = serverSettings.getProperty("MovePacketDelay", 100);
        ATTACK_PACKET_DELAY = serverSettings.getProperty("AttackPacketDelay", 200);
        ATTACK_END_DELAY = serverSettings.getProperty("AttackEndDelay", 50);

        DAMAGE_FROM_FALLING = serverSettings.getProperty("DamageFromFalling", true);

        LOAD_CUSTOM_SPAWN = serverSettings.getProperty("LoadAddGmSpawn", false);
        SAVE_GM_SPAWN = serverSettings.getProperty("SaveGmSpawn", false);
        DONTLOADSPAWN = serverSettings.getProperty("StartWithoutSpawn", false);
        DONTLOADQUEST = serverSettings.getProperty("StartWithoutQuest", false);

        MAX_REFLECTIONS_COUNT = serverSettings.getProperty("MaxReflectionsCount", 300);

        WEAR_DELAY = serverSettings.getProperty("WearDelay", 5);

        HTM_CACHE_MODE = serverSettings.getProperty("HtmCacheMode", HtmCache.LAZY);

        WEB_SERVER_DELAY = serverSettings.getProperty("WebServerDelay", 10) * 1000;
        WEB_SERVER_ROOT = serverSettings.getProperty("WebServerRoot", "./webserver/");

        ALLOW_IP_LOCK = serverSettings.getProperty("AllowLockIP", false);
        ALLOW_HWID_LOCK = serverSettings.getProperty("AllowLockHwid", false);
        HWID_LOCK_MASK = serverSettings.getProperty("HwidLockMask", 10);
    }

    public static void loadChatConfig() {
        ExProperties chatSettings = load(CHAT_FILE);

        GLOBAL_SHOUT = chatSettings.getProperty("GlobalShout", false);
        GLOBAL_TRADE_CHAT = chatSettings.getProperty("GlobalTradeChat", false);
        CHAT_RANGE = chatSettings.getProperty("ChatRange", 1250);
        SHOUT_OFFSET = chatSettings.getProperty("ShoutOffset", 0);
        LOG_CHAT = chatSettings.getProperty("LogChat", false);
        CHAT_MESSAGE_MAX_LEN = chatSettings.getProperty("ChatMessageLimit", 1000);
        ABUSEWORD_BANCHAT = chatSettings.getProperty("ABUSEWORD_BANCHAT", false);
        int counter = 0;
        for (int id : chatSettings.getProperty("ABUSEWORD_BAN_CHANNEL", new int[]{0})) {
            BAN_CHANNEL_LIST[counter] = id;
            counter++;
        }
        ABUSEWORD_REPLACE = chatSettings.getProperty("ABUSEWORD_REPLACE", false);
        ABUSEWORD_REPLACE_STRING = chatSettings.getProperty("ABUSEWORD_REPLACE_STRING", "[censored]");
        BANCHAT_ANNOUNCE = chatSettings.getProperty("BANCHAT_ANNOUNCE", true);
        BANCHAT_ANNOUNCE_FOR_ALL_WORLD = chatSettings.getProperty("BANCHAT_ANNOUNCE_FOR_ALL_WORLD", true);
        BANCHAT_ANNOUNCE_NICK = chatSettings.getProperty("BANCHAT_ANNOUNCE_NICK", true);
        ABUSEWORD_BANTIME = chatSettings.getProperty("ABUSEWORD_UNBAN_TIMER", 30);
        CHATFILTER_MIN_LEVEL = chatSettings.getProperty("ChatFilterMinLevel", 0);
        counter = 0;
        for (int id : chatSettings.getProperty("ChatFilterChannels", new int[]{1, 8})) {
            CHATFILTER_CHANNELS[counter] = id;
            counter++;
        }
        CHATFILTER_WORK_TYPE = chatSettings.getProperty("ChatFilterWorkType", 1);
    }

    public static void loadCommunityBoardConfig() {
        ExProperties communitySettings = load(CB_CONFIGURATION_FILE);

        COMMUNITYBOARD_ENABLED = communitySettings.getProperty("CommunityBoardEnable", true);
        if (COMMUNITYBOARD_ENABLED) {

            ALLOW_COMMUNITYBOARD_IN_COMBAT = communitySettings.getProperty("AllowInCombat", false);
            ALLOW_COMMUNITYBOARD_IS_IN_SIEGE = communitySettings.getProperty("AllowIsInSiege", false);
            ALLOW_COMMUNITYBOARD_IS_IN_EVENT = communitySettings.getProperty("AllowInEvent", false);
            ALLOW_COMMUNITYBOARD_IS_IN_CURSED_WEAPON = communitySettings.getProperty("AllowInCurseWeapon", false);

            BBS_DEFAULT = communitySettings.getProperty("BBSStartPage", "_bbshome");
            BBS_HOME_DIR = communitySettings.getProperty("BBSHomeDir", "scripts/services/community/");
            COMMUNITYBOARD_SHOP_ENABLED = communitySettings.getProperty("CommunityShopEnable", false);
            COMMUNITYBOARD_SHOP_NO_IS_IN_PEACE_ENABLED = communitySettings.getProperty("CommunityShopNoIsInPeaceEnable", false);
            COMMUNITYBOARD_SELL_ENABLED = communitySettings.getProperty("CommunitySellEnable", false);
            for (int listId : communitySettings.getProperty("AllowMultisell", new int[]{10038, 3112602, 3112603})) {
                COMMUNITYBOARD_MULTISELL_ALLOW.add(Integer.valueOf(listId));
            }
            COMMUNITYBOARD_MULTISELL_RATE = communitySettings.getProperty("CommunityMultiSellsRate", 1);

            COMMUNITYBOARD_BUFFER_ENABLED = communitySettings.getProperty("CommunityBufferEnable", false);
            ALLOW_BUFFER_IN_COMBAT = communitySettings.getProperty("UseBBSBuferIsCombat", false);
            ALLOW_BUFFER_IS_IN_SIEGE = communitySettings.getProperty("UseBBSBuferIsInSiege", false);
            ALLOW_BUFFER_IS_IN_EVENT = communitySettings.getProperty("UseBBSBuferIsEvents", false);
            ALLOW_BUFFER_IS_IN_CURSED_WEPON = communitySettings.getProperty("UseBBSBuferIsCurseWeapon", false);

            COMMUNITYBOARD_BUFFER_MAX_LVL_ALLOW = communitySettings.getProperty("CommunityBufferMaxLvlEnabled", false);
            COMMUNITYBOARD_BUFFER_NO_IS_IN_PEACE_ENABLED = communitySettings.getProperty("CommunityBufferNoIsInPeaceEnable", false);
            COMMUNITYBOARD_BUFFER_PET_ENABLED = communitySettings.getProperty("CommunityBufferPetEnable", false);
            COMMUNITYBOARD_BUFFER_SAVE_ENABLED = communitySettings.getProperty("CommunityBufferSaveEnable", false);
            COMMUNITYBOARD_INSTANCE_ENABLED = communitySettings.getProperty("CommunityBufferInstancesEnable", false);

            COMMUNITYBOARD_BUFF_TIME = communitySettings.getProperty("CommunityBuffTime", 20) * 60000;
            COMMUNITYBOARD_BUFF_PETS_TIME = communitySettings.getProperty("CommunityBuffPetTime", 1) * 60000;
            COMMUNITYBOARD_BUFF_COMBO_TIME = communitySettings.getProperty("CommunityBuffComboTime", 1) * 60000;
            COMMUNITYBOARD_BUFF_SONGDANCE_TIME = communitySettings.getProperty("CommunityBuffSongDanceTime", 1) * 60000;
            COMMUNITYBOARD_BUFF_PICE = communitySettings.getProperty("CommunityBuffPice", 5000);
            COMMUNITYBOARD_BUFFER_MAX_LVL = communitySettings.getProperty("CommunityBuffMaxLvl", 76);
            COMMUNITYBOARD_BUFF_SAVE_PICE = communitySettings.getProperty("CommunityBuffSavePice", 50000);
            for (int id : communitySettings.getProperty("AllowEffect", new int[]{1085, 1048, 1045})) {
                COMMUNITYBOARD_BUFF_ALLOW.add(Integer.valueOf(id));
            }
            String[] propertySplit = communitySettings.getProperty("MageScheme", "1068,1;1204,1;1040,1;1062,1;1388,1;1086,1;1077,1;1240,1;1242,1;13971,1").split(";");
            if (propertySplit != null) {
                for (String s_id1 : propertySplit) {

                    String[] s_id1_lvl = s_id1.split(",");
                    int id = Integer.parseInt(s_id1_lvl[0]);
                    int lvl = Integer.parseInt(s_id1_lvl[1]);
                    COMMUNITI_LIST_MAGE_SUPPORT.put(id, lvl);
                }
            }

            propertySplit = communitySettings.getProperty("FighterScheme", "4700,1;4702,1;4703,1;1087,1;1304,1;1353,1;1392,1;1393,1;1460,1;1044,1").split(";");
            if (propertySplit != null) {
                for (String s_id1 : propertySplit) {

                    String[] s_id1_lvl = s_id1.split(",");
                    int id = Integer.parseInt(s_id1_lvl[0]);
                    int lvl = Integer.parseInt(s_id1_lvl[1]);
                    COMMUNITI_LIST_FIGHTER_SUPPORT.put(id, lvl);
                }
            }

            COMMUNITYBOARD_TELEPORT_ENABLED = communitySettings.getProperty("CommunityTeleportEnable", false);

            ALLOW_TELEPORT_IN_COMBAT = communitySettings.getProperty("UseBBSTeleportIsCombat", false);
            ALLOW_TELEPORT_IS_IN_SIEGE = communitySettings.getProperty("UseBBSTeleportIsInSiege", false);
            ALLOW_TELEPORT_IS_IN_EVENT = communitySettings.getProperty("UseBBSTeleportIsEvents", false);
            ALLOW_TELEPORT_IS_IN_CURSED_WEPON = communitySettings.getProperty("UseBBSTeleportIsCurseWeapon", false);

            COMMUNITYBOARD_TELE_PICE = communitySettings.getProperty("CommunityTeleportPice", 10000);
            COMMUNITYBOARD_SAVE_TELE_PREMIUM = communitySettings.getProperty("CommunityTeleportPremium", false);
            COMMUNITYBOARD_SAVE_TELE_PICE = communitySettings.getProperty("CommunitySaveTeleportPice", 50000);
            COMMUNITYBOARD_SAVE_TELE_COUNT = communitySettings.getProperty("CommunitySaveTeleportCount", 7);

            ALLOW_SELL_IN_COMBAT = communitySettings.getProperty("UseBBSSelIsCombat", false);
            ALLOW_SELL_IS_IN_SIEGE = communitySettings.getProperty("UseBBSSelIsInSiege", false);
            ALLOW_SELL_IS_IN_EVENT = communitySettings.getProperty("UseBBSSelIsEvents", false);
            ALLOW_SELL_IS_IN_CURSED_WEPON = communitySettings.getProperty("UseBBSSelIsCurseWeapon", false);

            USE_BBS_PROF_IS_COMBAT = communitySettings.getProperty("UseBBSProfIsCombat", false);
            USE_BBS_PROF_IS_EVENTS = communitySettings.getProperty("UseBBSProfIsEvents", false);

            COMMUNITYBOARD_BOARD_ALT_ENABLED = communitySettings.getProperty("CommunityBoardAltEnable", false);
            COMMUNITYBOARD_BUFF_PICE_NG = communitySettings.getProperty("CommunityBuffPiceNG", 5000);
            COMMUNITYBOARD_BUFF_PICE_D = communitySettings.getProperty("CommunityBuffPiceD", 10000);
            COMMUNITYBOARD_BUFF_PICE_C = communitySettings.getProperty("CommunityBuffPiceC", 15000);
            COMMUNITYBOARD_BUFF_PICE_B = communitySettings.getProperty("CommunityBuffPiceB", 20000);
            COMMUNITYBOARD_BUFF_PICE_A = communitySettings.getProperty("CommunityBuffPiceA", 25000);
            COMMUNITYBOARD_BUFF_PICE_S = communitySettings.getProperty("CommunityBuffPiceS", 30000);
            COMMUNITYBOARD_BUFF_PICE_S80 = communitySettings.getProperty("CommunityBuffPiceS80", 35000);
            COMMUNITYBOARD_BUFF_PICE_S84 = communitySettings.getProperty("CommunityBuffPiceS84", 40000);
            COMMUNITYBOARD_BUFF_PICE_NG_GR = communitySettings.getProperty("CommunityBuffPiceGroup_NG", 5000);
            COMMUNITYBOARD_BUFF_PICE_D_GR = communitySettings.getProperty("CommunityBuffPiceGroup_D", 10000);
            COMMUNITYBOARD_BUFF_PICE_C_GR = communitySettings.getProperty("CommunityBuffPiceGroup_C", 15000);
            COMMUNITYBOARD_BUFF_PICE_B_GR = communitySettings.getProperty("CommunityBuffPiceGroup_B", 20000);
            COMMUNITYBOARD_BUFF_PICE_A_GR = communitySettings.getProperty("CommunityBuffPiceGroup_A", 25000);
            COMMUNITYBOARD_BUFF_PICE_S_GR = communitySettings.getProperty("CommunityBuffPiceGroup_S", 30000);
            COMMUNITYBOARD_BUFF_PICE_S80_GR = communitySettings.getProperty("CommunityBuffPiceGroup_S80", 35000);
            COMMUNITYBOARD_BUFF_PICE_S84_GR = communitySettings.getProperty("CommunityBuffPiceGroup_S84", 40000);
            COMMUNITYBOARD_TELEPORT_PICE_NG = communitySettings.getProperty("CommunityTeleportPiceNG", 5000);
            COMMUNITYBOARD_TELEPORT_PICE_D = communitySettings.getProperty("CommunityTeleportPiceD", 10000);
            COMMUNITYBOARD_TELEPORT_PICE_C = communitySettings.getProperty("CommunityTeleportPiceC", 15000);
            COMMUNITYBOARD_TELEPORT_PICE_B = communitySettings.getProperty("CommunityTeleportPiceB", 20000);
            COMMUNITYBOARD_TELEPORT_PICE_A = communitySettings.getProperty("CommunityTeleportPiceA", 25000);
            COMMUNITYBOARD_TELEPORT_PICE_S = communitySettings.getProperty("CommunityTeleportPiceS", 30000);
            COMMUNITYBOARD_TELEPORT_PICE_S80 = communitySettings.getProperty("CommunityTeleportPiceS80", 35000);
            COMMUNITYBOARD_TELEPORT_PICE_S84 = communitySettings.getProperty("CommunityTeleportPiceS84", 40000);
            ALLOW_TELEPORT_POINT_CONTROLL = communitySettings.getProperty("AllowTeleportPointsControll", false);
            if (ALLOW_TELEPORT_POINT_CONTROLL) {
                propertySplit = communitySettings.getProperty("TeleportPoints", "16028:142329:-2697").split(";");
                COMMUNITYBOARD_TELEPORT_POINT.addAll(Arrays.asList(propertySplit));
            }

            COMMUNITYBOARD_EXCHANGE_ENABLED = communitySettings.getProperty("AllowExchenge", false);
            COMMUNITYBOARD_ENCHANT_ENABLED = communitySettings.getProperty("AllowCBEnchant", false);
            ALLOW_BBS_ENCHANT_ELEMENTAR = communitySettings.getProperty("AllowEnchantElementar", false);
            ALLOW_BBS_ENCHANT_ATT = communitySettings.getProperty("AllowEnchantAtt", false);
            COMMUNITYBOARD_ENCHANT_ITEM = communitySettings.getProperty("CBEnchantItem", 4356);
            COMMUNITYBOARD_MAX_ENCHANT = communitySettings.getProperty("CBMaxEnchant", 25);
            COMMUNITYBOARD_ENCHANT_LVL = communitySettings.getProperty("CBEnchantLvl", new int[0]);
            COMMUNITYBOARD_ENCHANT_PRICE_WEAPON = communitySettings.getProperty("CBEnchantPriceWeapon", new int[0]);
            COMMUNITYBOARD_ENCHANT_PRICE_ARMOR = communitySettings.getProperty("CBEnchantPriceArmor", new int[0]);
            COMMUNITYBOARD_ENCHANT_ATRIBUTE_LVL_WEAPON = communitySettings.getProperty("CBEnchantAtributeLvlWeapon", new int[0]);
            COMMUNITYBOARD_ENCHANT_ATRIBUTE_PRICE_WEAPON = communitySettings.getProperty("CBEnchantAtributePriceWeapon", new int[0]);
            COMMUNITYBOARD_ENCHANT_ATRIBUTE_LVL_ARMOR = communitySettings.getProperty("CBEnchantAtributeLvlArmor", new int[0]);
            COMMUNITYBOARD_ENCHANT_ATRIBUTE_PRICE_ARMOR = communitySettings.getProperty("CBEnchantAtributePriceArmor", new int[0]);
            COMMUNITYBOARD_ENCHANT_ATRIBUTE_PVP = communitySettings.getProperty("CBEnchantAtributePvP", false);

            SUB_MANAGER_ALLOW = communitySettings.getProperty("AllowSubManager", false);

            COMMUNITY_COMMISSION_ALLOW = communitySettings.getProperty("CommunityCommissionAllow", false);
            COMMUNITY_COMMISSION_ARMOR_PRICE = communitySettings.getProperty("CommunityCommissionArmorPrice", new int[]{1});
            COMMUNITY_COMMISSION_WEAPON_PRICE = communitySettings.getProperty("CommunityCommissionWeaponPrice", new int[]{1});
            COMMUNITY_COMMISSION_JEWERLY_PRICE = communitySettings.getProperty("CommunityCommissionJewerlyPrice", new int[]{1});
            COMMUNITY_COMMISSION_OTHER_PRICE = communitySettings.getProperty("CommunityCommissionOtherPrice", new int[]{1});
            COMMUNITY_COMMISSION_ALLOW_ITEMS = communitySettings.getProperty("CommunityCommissionAllowItems", new int[]{4037, 6673});
            COMMUNITY_COMMISSION_MAX_ENCHANT = communitySettings.getProperty("CommunityCommissionMaxEnchant", 24);
            COMMUNITY_COMMISSION_NOT_ALLOW_ITEMS = communitySettings.getProperty("CommunityCommissionNotAllowItems", new int[]{1});
            COMMUNITY_COMMISSION_COUNT_TO_PAGE = communitySettings.getProperty("CommunityCommissionCountToPage", 24);
            COMMUNITY_COMMISSION_MONETS = communitySettings.getProperty("CommunityCommissionMonets", new int[]{1});
            COMMUNITY_COMMISSION_ALLOW_UNDERWEAR = communitySettings.getProperty("CommunityCommissionAllowUnderwear", false);
            COMMUNITY_COMMISSION_ALLOW_CLOAK = communitySettings.getProperty("CommunityCommissionAllowCloak", false);
            COMMUNITY_COMMISSION_ALLOW_BRACELET = communitySettings.getProperty("CommunityCommissionAllowBracelet", false);
            COMMUNITY_COMMISSION_ALLOW_AUGMENTED = communitySettings.getProperty("CommunityCommissionAllowAugmented", false);
            COMMUNITY_COMMISSION_ALLOW_EQUIPPED = communitySettings.getProperty("CommunityCommissionAllowEquipped", false);
            COMMUNITY_COMMISSION_SAVE_DAYS = communitySettings.getProperty("CommunityCommissionSaveDays", 30);

            COMMUNITY_WAREHOUSE_RANGE_Z = communitySettings.getProperty("CommunityWarehouseRangeZ", false);
            SERVICES_ACADEM_REWARD = communitySettings.getProperty("AcademReward", "Адена,57;Золото,4037");

            String[] splitTele = communitySettings.getProperty("CommunityTeleportPPrice", "").trim().replaceAll(" ", "").split(";");
            TELE_PPRISE = new int[splitTele.length][5];
            for (int i = 0; i < splitTele.length; i++) {
                final String[] strings = splitTele[i].split(",");

                final int[] ints = new int[strings.length];
                for (int k = 0; k < strings.length; k++) {
                    TELE_PPRISE[i][k] = Integer.parseInt(strings[k]);
                }
            }

            String[] splitBuff = communitySettings.getProperty("CommunityBuffPPrice", "").trim().replaceAll(" ", "").split(";");
            BUFF_PPRISE = new int[splitBuff.length][3];
            for (int i = 0; i < splitBuff.length; i++) {
                final String[] strings = splitBuff[i].split(",");

                final int[] ints = new int[strings.length];
                for (int k = 0; k < strings.length; k++) {
                    BUFF_PPRISE[i][k] = Integer.parseInt(strings[k]);
                }
            }

            AcademReward.load();
        }
    }

    public static void loadTelnetConfig() {
        ExProperties telnetSettings = load(TELNET_CONFIGURATION_FILE);

        IS_TELNET_ENABLED = telnetSettings.getProperty("EnableTelnet", false);
        TELNET_DEFAULT_ENCODING = telnetSettings.getProperty("TelnetEncoding", "UTF-8");
        TELNET_PORT = telnetSettings.getProperty("Port", 7000);
        TELNET_HOSTNAME = telnetSettings.getProperty("BindAddress", "127.0.0.1");
        TELNET_PASSWORD = telnetSettings.getProperty("Password", "");
    }

    public static void loadWeddingConfig() {
        ExProperties weddingSettings = load(WEDDING_FILE);

        ALLOW_WEDDING = weddingSettings.getProperty("AllowWedding", false);
        WEDDING_PRICE = weddingSettings.getProperty("WeddingPrice", 500000);
        WEDDING_PUNISH_INFIDELITY = weddingSettings.getProperty("WeddingPunishInfidelity", true);
        WEDDING_TELEPORT = weddingSettings.getProperty("WeddingTeleport", true);
        WEDDING_TELEPORT_PRICE = weddingSettings.getProperty("WeddingTeleportPrice", 500000);
        WEDDING_TELEPORT_INTERVAL = weddingSettings.getProperty("WeddingTeleportInterval", 120);
        WEDDING_SAMESEX = weddingSettings.getProperty("WeddingAllowSameSex", true);
        WEDDING_FORMALWEAR = weddingSettings.getProperty("WeddingFormalWear", true);
        WEDDING_DIVORCE_COSTS = weddingSettings.getProperty("WeddingDivorceCosts", 20);
    }

    public static void loadResidenceConfig() {
        ExProperties residenceSettings = load(RESIDENCE_CONFIG_FILE);

        CH_BID_GRADE1_MINCLANLEVEL = residenceSettings.getProperty("ClanHallBid_Grade1_MinClanLevel", 2);
        CH_BID_GRADE1_MINCLANMEMBERS = residenceSettings.getProperty("ClanHallBid_Grade1_MinClanMembers", 1);
        CH_BID_GRADE1_MINCLANMEMBERSLEVEL = residenceSettings.getProperty("ClanHallBid_Grade1_MinClanMembersAvgLevel", 1);
        CH_BID_GRADE2_MINCLANLEVEL = residenceSettings.getProperty("ClanHallBid_Grade2_MinClanLevel", 2);
        CH_BID_GRADE2_MINCLANMEMBERS = residenceSettings.getProperty("ClanHallBid_Grade2_MinClanMembers", 1);
        CH_BID_GRADE2_MINCLANMEMBERSLEVEL = residenceSettings.getProperty("ClanHallBid_Grade2_MinClanMembersAvgLevel", 1);
        CH_BID_GRADE3_MINCLANLEVEL = residenceSettings.getProperty("ClanHallBid_Grade3_MinClanLevel", 2);
        CH_BID_GRADE3_MINCLANMEMBERS = residenceSettings.getProperty("ClanHallBid_Grade3_MinClanMembers", 1);
        CH_BID_GRADE3_MINCLANMEMBERSLEVEL = residenceSettings.getProperty("ClanHallBid_Grade3_MinClanMembersAvgLevel", 1);
        RESIDENCE_LEASE_FUNC_MULTIPLIER = residenceSettings.getProperty("ResidenceLeaseFuncMultiplier", 1.);
        RESIDENCE_LEASE_MULTIPLIER = residenceSettings.getProperty("ResidenceLeaseMultiplier", 1.);

        CASTLE_GENERATE_TIME_ALTERNATIVE = residenceSettings.getProperty("CastleGenerateAlternativeTime", false);
        CASTLE_GENERATE_TIME_LOW = residenceSettings.getProperty("CastleGenerateTimeLow", 46800000);
        CASTLE_GENERATE_TIME_HIGH = residenceSettings.getProperty("CastleGenerateTimeHigh", 61200000);

        CASTLE_SELECT_HOURS = residenceSettings.getProperty("CastleSelectHours", new int[]{16, 20});
        int[] tempCastleValidatonTime = residenceSettings.getProperty("CastleValidationDate", new int[]{2, 4, 2003});
        CASTLE_VALIDATION_DATE = Calendar.getInstance();
        CASTLE_VALIDATION_DATE.set(Calendar.DAY_OF_MONTH, tempCastleValidatonTime[0]);
        CASTLE_VALIDATION_DATE.set(Calendar.MONTH, tempCastleValidatonTime[1] - 1);
        CASTLE_VALIDATION_DATE.set(Calendar.YEAR, tempCastleValidatonTime[2]);
        CASTLE_VALIDATION_DATE.set(Calendar.HOUR_OF_DAY, 0);
        CASTLE_VALIDATION_DATE.set(Calendar.MINUTE, 0);
        CASTLE_VALIDATION_DATE.set(Calendar.SECOND, 0);
        CASTLE_VALIDATION_DATE.set(Calendar.MILLISECOND, 0);
        CASTLE_WEEK = residenceSettings.getProperty("CastleWeek", 2);

        TW_SELECT_HOURS = residenceSettings.getProperty("TwSelectHours", 20);
        int[] tempTwValidatonTime = residenceSettings.getProperty("TwValidationDate", new int[]{2, 4, 2003});
        TW_VALIDATION_DATE = Calendar.getInstance();
        TW_VALIDATION_DATE.set(Calendar.DAY_OF_MONTH, tempTwValidatonTime[0]);
        TW_VALIDATION_DATE.set(Calendar.MONTH, tempTwValidatonTime[1] - 1);
        TW_VALIDATION_DATE.set(Calendar.YEAR, tempTwValidatonTime[2]);
        TW_VALIDATION_DATE.set(Calendar.HOUR_OF_DAY, 0);
        TW_VALIDATION_DATE.set(Calendar.MINUTE, 0);
        TW_VALIDATION_DATE.set(Calendar.SECOND, 0);
        TW_VALIDATION_DATE.set(Calendar.MILLISECOND, 0);
        TW_WEEK = residenceSettings.getProperty("TwWeek", 2);
    }

    public static void loadItemsUseConfig() {
        ExProperties itemsUseSettings = load(ITEM_USE_FILE);

        ITEM_USE_LIST_ID = itemsUseSettings.getProperty("ItemUseListId", new int[]{725, 726, 727, 728});
        ITEM_USE_IS_COMBAT_FLAG = itemsUseSettings.getProperty("ItemUseIsCombatFlag", true);
        ITEM_USE_IS_ATTACK = itemsUseSettings.getProperty("ItemUseIsAttack", true);
        ITEM_USE_IS_EVENTS = itemsUseSettings.getProperty("ItemUseIsEvents", true);
    }

    public static void loadPhantomsConfig() {
        ExProperties PhantomsSettings = load(PHANTOM_FILE);

        ALLOW_PHANTOM_PLAYERS = PhantomsSettings.getProperty("AllowPhantomPlayers", false);
        ALLOW_PHANTOM_SETS = PhantomsSettings.getProperty("AllowPhantomSets", false);
        PHANTOM_MIN_CLASS_ID = PhantomsSettings.getProperty("PhantomMinClassId", 0);
        PHANTOM_MAX_CLASS_ID = PhantomsSettings.getProperty("PhantomMaxClassId", 122);

        PHANTOM_PLAYERS_AKK = PhantomsSettings.getProperty("PhantomPlayerAccounts", "l2-dream.ru");
        PHANTOM_PLAYERS_SOULSHOT_ANIM = PhantomsSettings.getProperty("PhantomSoulshotAnimation", true);
        PHANTOM_PLAYERS_COUNT_FIRST = PhantomsSettings.getProperty("FirstCount", 50);
        PHANTOM_PLAYERS_DELAY_FIRST = PhantomsSettings.getProperty("FirstDelay", 5);
        PHANTOM_PLAYERS_DESPAWN_FIRST = TimeUnit.MINUTES.toMillis(PhantomsSettings.getProperty("FirstDespawn", 60));
        PHANTOM_PLAYERS_DELAY_SPAWN_FIRST = TimeUnit.SECONDS.toMillis(PhantomsSettings.getProperty("FirstDelaySpawn", 1));
        PHANTOM_PLAYERS_DELAY_DESPAWN_FIRST = TimeUnit.SECONDS.toMillis(PhantomsSettings.getProperty("FirstDelayDespawn", 20));
        PHANTOM_PLAYERS_COUNT_NEXT = PhantomsSettings.getProperty("NextCount", 50);
        PHANTOM_PLAYERS_CP_REUSE_TIME = PhantomsSettings.getProperty("CpReuseTime", 200);
        PHANTOM_PLAYERS_DELAY_NEXT = TimeUnit.MINUTES.toMillis(PhantomsSettings.getProperty("NextDelay", 15));
        PHANTOM_PLAYERS_DESPAWN_NEXT = TimeUnit.MINUTES.toMillis(PhantomsSettings.getProperty("NextDespawn", 90));
        PHANTOM_PLAYERS_DELAY_SPAWN_NEXT = TimeUnit.SECONDS.toMillis(PhantomsSettings.getProperty("NextDelaySpawn", 20));
        PHANTOM_PLAYERS_DELAY_DESPAWN_NEXT = TimeUnit.SECONDS.toMillis(PhantomsSettings.getProperty("NextDelayDespawn", 30));
        String[] ppp = PhantomsSettings.getProperty("FakeEnchant", "0,14").split(",");
        PHANTOM_PLAYERS_ENCHANT_MIN = Integer.parseInt(ppp[0]);
        PHANTOM_PLAYERS_ENCHANT_MAX = Integer.parseInt(ppp[1]);
        ppp = PhantomsSettings.getProperty("FakeNameColors", "FFFFFF,FFFFFF").split(",");
        for (String ncolor : ppp) {
            String nick = new TextBuilder(ncolor).reverse().toString();
            PHANTOM_PLAYERS_NAME_CLOLORS.add(Integer.decode("0x" + nick));
        }
        ppp = PhantomsSettings.getProperty("FakeTitleColors", "FFFF77,FFFF77").split(",");
        for (String tcolor : ppp) {
            String title = new TextBuilder(tcolor).reverse().toString();
            PHANTOM_PLAYERS_TITLE_CLOLORS.add(Integer.decode("0x" + title));
        }
    }

    public static void loadAprilFoolsSettings() {
        ExProperties eventAprilFoolsSettings = load(EVENT_APRIL_FOOLS_FILE);

        EVENT_APIL_FOOLS_DROP_CHANCE = eventAprilFoolsSettings.getProperty("AprilFollsDropChance", 50.0D);
    }

    public static void loadlakfiConfig() {
        ExProperties lakfiSettings = load(LAKFI_CONFIG_FILE);

        MAX_ADENA_TO_EAT = lakfiSettings.getProperty("MaxAdenaLakfiEat", 99);
        ADENA_TO_EAT = lakfiSettings.getProperty("AdenaLakfiEat", 50);
        TIME_IF_NOT_FEED = lakfiSettings.getProperty("TimeIfNotFeedDissapear", 10);
    }

    public static void loadCofferOfShadowsSettings() {
        ExProperties eventCofferOfShadowsSettings = load(EVENT_COFFER_OF_SHADOWS_FILE);

        EVENT_CofferOfShadowsPriceRate = eventCofferOfShadowsSettings.getProperty("CofferOfShadowsPriceRate", 1.0D);
        EVENT_CofferOfShadowsRewardRate = eventCofferOfShadowsSettings.getProperty("CofferOfShadowsRewardRate", 1.0D);
    }

    public static void loadLastHeroSettings() {
        ExProperties eventLastHeroSettings = load(EVENT_LAST_HERO_FILE);

        EVENT_LastHeroItemID = eventLastHeroSettings.getProperty("LastHero_bonus_id", 57);
        EVENT_LastHeroItemCOUNT = eventLastHeroSettings.getProperty("LastHero_bonus_count", 5000.0D);
        EVENT_LastHeroRate = eventLastHeroSettings.getProperty("LastHero_rate", true);
        EVENT_LastHeroItemCOUNTFinal = eventLastHeroSettings.getProperty("LastHero_bonus_count_final", 10000.0D);
        EVENT_LastHeroRateFinal = eventLastHeroSettings.getProperty("LastHero_rate_final", true);
        EVENT_LastHeroTime = eventLastHeroSettings.getProperty("LastHero_time", 3);
        EVENT_LastHeroStartTime = eventLastHeroSettings.getProperty("LastHero_StartTime", "20:00").trim().replaceAll(" ", "").split(",");
        EVENT_LastHeroCategories = eventLastHeroSettings.getProperty("LastHero_Categories", false);
        EVENT_LastHeroAllowSummons = eventLastHeroSettings.getProperty("LastHero_AllowSummons", false);
        EVENT_LastHeroAllowBuffs = eventLastHeroSettings.getProperty("LastHero_AllowBuffs", false);
        EVENT_LastHeroAllowMultiReg = eventLastHeroSettings.getProperty("LastHero_AllowMultiReg", false);
        EVENT_LastHeroCheckWindowMethod = eventLastHeroSettings.getProperty("LastHero_CheckWindowMethod", "IP");
        EVENT_LastHeroEventRunningTime = eventLastHeroSettings.getProperty("LastHero_EventRunningTime", 20);
        EVENT_LastHeroEventBlockItems = eventLastHeroSettings.getProperty("LastHero_BlockItems", new int[]{4037, 57});
        EVENT_HeroAuraChatEnabled = eventLastHeroSettings.getProperty("LastHero_HeroAuraChatEnabled", false);
        EVENT_LastHeroFighterBuffs = eventLastHeroSettings.getProperty("LastHero_FighterBuffs", "").trim().replaceAll(" ", "").split(";");
        EVENT_LastHeroMageBuffs = eventLastHeroSettings.getProperty("LastHero_MageBuffs", "").trim().replaceAll(" ", "").split(";");
        EVENT_LastHeroBuffPlayers = eventLastHeroSettings.getProperty("LastHero_BuffPlayers", false);
        EVENT_LAST_HERO_AURA_ENABLE = eventLastHeroSettings.getProperty("LastHero_GiveHero", false);
    }

    public static void loadLuckNpcSettings() {
        ExProperties eventLuckNpcSettings = load(EVENT_LUCK_NPC_FILE);

        EVENT_LuckNPC_ENABLED = eventLuckNpcSettings.getProperty("LuckNPCEnabled", false);
        EVENT_LuckNPCManagerId = eventLuckNpcSettings.getProperty("LuckNPCManagerId", 30);
        EVENT_LuckNPCStartTime = eventLuckNpcSettings.getProperty("LuckNPCStartTime", "20:00").trim().replaceAll(" ", "").split(",");
        EVENT_LuckNPCStopTime = eventLuckNpcSettings.getProperty("LuckNPCStopTime", "20:00").trim().replaceAll(" ", "").split(",");
        EVENT_LuckNPCChance = eventLuckNpcSettings.getProperty("LuckNPCChance", 30);
        EVENT_LuckNPLoc = eventLuckNpcSettings.getProperty("LuckNPLoc", "").trim().replaceAll(" ", "").split(";");
        EVENT_LuckNPCReward = eventLuckNpcSettings.getProperty("LuckNPCReward", "").trim().replaceAll(" ", "").split(";");
        EVENT_LuckNPCPoints = eventLuckNpcSettings.getProperty("LuckNPCPoints", "").trim().replaceAll(" ", "").split(";");
    }

    public static void loadGVGSettings() {
        ExProperties eventGVGSettings = load(EVENT_GVG);

        EVENT_GvGDisableEffect = eventGVGSettings.getProperty("GvGDisableEffect", false);

    }

    public static void loadHitmanSettings() {
        ExProperties eventHitmanSettings = load(EVENT_HITMAN);

        EVENT_HITMAN_ENABLED = eventHitmanSettings.getProperty("HitmanEnabled", false);
        EVENT_HITMAN_COST_ITEM_ID = eventHitmanSettings.getProperty("CostItemId", 57);
        EVENT_HITMAN_COST_ITEM_COUNT = eventHitmanSettings.getProperty("CostItemCount", 1000);
        EVENT_HITMAN_TASKS_PER_PAGE = eventHitmanSettings.getProperty("TasksPerPage", 7);
        EVENT_HITMAN_ALLOWED_ITEM_LIST = eventHitmanSettings.getProperty("AllowedItems", new String[]{"4037", "57"});
        EVENT_HITMAN_TITLE = eventHitmanSettings.getProperty("HitmanTitle", "Ordered");

    }

    public static void loadChampionSettings() {
        ExProperties eventChampionSettings = load(EVENT_CHAMPION);

        AllowChampionCustomDropItems = eventChampionSettings.getProperty("AllowChampionCustomDropItems", true);
        ChampionCDItemsAllowMinMaxPlayerLvl = eventChampionSettings.getProperty("ChampionCDItemsAllowMinMaxPlayerLvl", false);
        ChampionCDItemsAllowMinMaxMobLvl = eventChampionSettings.getProperty("ChampionCDItemsAllowMinMaxMobLvl", false);
        ChampionCDItemsId = eventChampionSettings.getProperty("ChampionCDItemsId", new int[]{57});
        ChampionCDItemsCountDropMin = eventChampionSettings.getProperty("ChampionCDItemsCountDropMin", new int[]{1});
        ChampionCDItemsCountDropMax = eventChampionSettings.getProperty("ChampionCDItemsCountDropMax", new int[]{1});
        ChampionCustomDropItemsChance = eventChampionSettings.getProperty("ChampionCustomDropItemsChance", new double[]{1.});
        ChampionCDItemsMinPlayerLvl = eventChampionSettings.getProperty("ChampionCDItemsMinPlayerLvl", 20);
        ChampionCDItemsMaxPlayerLvl = eventChampionSettings.getProperty("ChampionCDItemsMaxPlayerLvl", 85);
        ChampionCDItemsMinMobLvl = eventChampionSettings.getProperty("ChampionCDItemsMinMobLvl", 20);
        ChampionCDItemsMaxMobLvl = eventChampionSettings.getProperty("ChampionCDItemsMaxMobLvl", 80);

        ChampionBlueAuraEnabled = eventChampionSettings.getProperty("ChampionBlueAuraEnabled", true);
        ChampionRedAuraEnabled = eventChampionSettings.getProperty("ChampionRedAuraEnabled", true);

    }

    public static void loadDeathMatchSettings() {
        ExProperties eventDeathMatchSettings = load(EVENT_DEATH_MATCH);

        EVENT_DeathMatchRewards = eventDeathMatchSettings.getProperty("DeathMatch_Rewards", "").trim().replaceAll(" ", "").split(";");
        EVENT_DeathMatchTime = eventDeathMatchSettings.getProperty("DeathMatch_time", 3);
        EVENT_DeathMatchStartTime = eventDeathMatchSettings.getProperty("DeathMatch_StartTime", "20:00").trim().replaceAll(" ", "").split(",");
        EVENT_DeathMatchCategories = eventDeathMatchSettings.getProperty("DeathMatch_Categories", false);
        EVENT_DeathMatchMaxPlayerInTeam = eventDeathMatchSettings.getProperty("DeathMatch_MaxPlayerInTeam", 20);
        EVENT_DeathMatchMinPlayerInTeam = eventDeathMatchSettings.getProperty("DeathMatch_MinPlayerInTeam", 2);
        EVENT_DeathMatchAllowSummons = eventDeathMatchSettings.getProperty("DeathMatch_AllowSummons", false);
        EVENT_DeathMatchAllowBuffs = eventDeathMatchSettings.getProperty("DeathMatch_AllowBuffs", false);
        EVENT_DeathMatchAllowMultiReg = eventDeathMatchSettings.getProperty("DeathMatch_AllowMultiReg", false);
        EVENT_DeathMatchCheckWindowMethod = eventDeathMatchSettings.getProperty("DeathMatch_CheckWindowMethod", "IP");
        EVENT_DeathMatchEventRunningTime = eventDeathMatchSettings.getProperty("DeathMatch_EventRunningTime", 20);
        EVENT_DeathMatchFighterBuffs = eventDeathMatchSettings.getProperty("DeathMatch_FighterBuffs", "").trim().replaceAll(" ", "").split(";");
        EVENT_DeathMatchMageBuffs = eventDeathMatchSettings.getProperty("DeathMatch_MageBuffs", "").trim().replaceAll(" ", "").split(";");
        EVENT_DeathMatchBuffPlayers = eventDeathMatchSettings.getProperty("DeathMatch_BuffPlayers", false);
        EVENT_DeathMatchrate = eventDeathMatchSettings.getProperty("DeathMatch_rate", true);
    }

    public static void loadSavingSnowmanSettings() {
        ExProperties eventSavingSnowmanSettings = load(EVENT_SAVING_SNOWMAN_FILE);

        EVENT_SAVING_SNOWMAN_LOTERY_PRICE = eventSavingSnowmanSettings.getProperty("SavingSnowmanLoteryPrice", 50000);
        EVENT_SAVING_SNOWMAN_REWARDER_CHANCE = eventSavingSnowmanSettings.getProperty("SavingSnowmanRewarderChance", 2);
    }

    public static void loadMarch8Settings() {
        ExProperties eventMarch8Settings = load(EVENT_MARCH_8_FILE);

        EVENT_MARCH8_DROP_CHANCE = eventMarch8Settings.getProperty("March8DropChance", 10.0D);
        EVENT_MARCH8_PRICE_RATE = eventMarch8Settings.getProperty("March8PriceRate", 1.0D);
    }

    public static void loadChangeOfHeartSettings() {
        ExProperties eventChangeOfHeartSettings = load(EVENT_CHANGE_OF_HEART_FILE);

        EVENT_CHANGE_OF_HEART_CHANCE = eventChangeOfHeartSettings.getProperty("EVENT_CHANGE_OF_HEART_CHANCE", 5.0D);
    }

    public static void loadL2CoinsSettings() {
        ExProperties eventL2CoinsSettings = load(EVENT_L2_DAY_FILE);

        EVENT_MOUSE_COIN_CHANCE = eventL2CoinsSettings.getProperty("L2CoinsMouseCoinChance", 100);
        EVENT_MOUSE_COIN = eventL2CoinsSettings.getProperty("L2CoinsMouseCoin", 10639);
        EVENT_MOUSE_COIN_MIN_COUNT = eventL2CoinsSettings.getProperty("L2CoinsMouseCoinMinCount", 1);
        EVENT_MOUSE_COIN_MAX_COUNT = eventL2CoinsSettings.getProperty("L2CoinsMouseCoinMaxCount", 4);
        EVENT_BASE_COIN_AFTER_RB = eventL2CoinsSettings.getProperty("L2CoinsBaseCoinAfterRB", 40);
    }

    public static void loadCaptureTheFlagSettings() {
        ExProperties eventCaptureTheFlagSettings = load(EVENT_CAPTURE_THE_FLAG_FILE);

        EVENT_CtFRewards = eventCaptureTheFlagSettings.getProperty("CtF_Rewards", "").trim().replaceAll(" ", "").split(";");
        EVENT_CtfTime = eventCaptureTheFlagSettings.getProperty("CtF_time", 3);
        EVENT_CtFrate = eventCaptureTheFlagSettings.getProperty("CtF_rate", true);
        EVENT_CtFStartTime = eventCaptureTheFlagSettings.getProperty("CtF_StartTime", "20:00").trim().replaceAll(" ", "").split(",");
        EVENT_CtFCategories = eventCaptureTheFlagSettings.getProperty("CtF_Categories", false);
        EVENT_CtFMaxPlayerInTeam = eventCaptureTheFlagSettings.getProperty("CtF_MaxPlayerInTeam", 20);
        EVENT_CtFMinPlayerInTeam = eventCaptureTheFlagSettings.getProperty("CtF_MinPlayerInTeam", 2);
        EVENT_CtFAllowSummons = eventCaptureTheFlagSettings.getProperty("CtF_AllowSummons", false);
        EVENT_CtFAllowBuffs = eventCaptureTheFlagSettings.getProperty("CtF_AllowBuffs", false);
        EVENT_CtFAllowMultiReg = eventCaptureTheFlagSettings.getProperty("CtF_AllowMultiReg", false);
        EVENT_CtFCheckWindowMethod = eventCaptureTheFlagSettings.getProperty("CtF_CheckWindowMethod", "IP");
        EVENT_CtFFighterBuffs = eventCaptureTheFlagSettings.getProperty("CtF_FighterBuffs", "").trim().replaceAll(" ", "").split(";");
        EVENT_CtFMageBuffs = eventCaptureTheFlagSettings.getProperty("CtF_MageBuffs", "").trim().replaceAll(" ", "").split(";");
        EVENT_CtFBuffPlayers = eventCaptureTheFlagSettings.getProperty("CtF_BuffPlayers", false);
        EVENT_CtFBlockItems = eventCaptureTheFlagSettings.getProperty("CtF_BlockItems", new int[]{4037, 57});
    }

    public static void loadTeamVSTeamSettings() {
        ExProperties eventTvTSettings = load(EVENT_TEAM_VS_TEAM_FILE);

        EVENT_TvTRewards = eventTvTSettings.getProperty("TvT_Rewards", "").trim().replaceAll(" ", "").split(";");
        EVENT_TvTTime = eventTvTSettings.getProperty("TvT_time", 3);
        EVENT_TvTStartTime = eventTvTSettings.getProperty("TvT_StartTime", "20:00").trim().replaceAll(" ", "").split(",");
        EVENT_TvTCategories = eventTvTSettings.getProperty("TvT_Categories", false);
        EVENT_TvTMaxPlayerInTeam = eventTvTSettings.getProperty("TvT_MaxPlayerInTeam", 20);
        EVENT_TvTMinPlayerInTeam = eventTvTSettings.getProperty("TvT_MinPlayerInTeam", 2);
        EVENT_TvTAllowSummons = eventTvTSettings.getProperty("TvT_AllowSummons", false);
        EVENT_TvTAllowBuffs = eventTvTSettings.getProperty("TvT_AllowBuffs", false);
        EVENT_TvTAllowMultiReg = eventTvTSettings.getProperty("TvT_AllowMultiReg", false);
        EVENT_TvTCheckWindowMethod = eventTvTSettings.getProperty("TvT_CheckWindowMethod", "IP");
        EVENT_TvTEventRunningTime = eventTvTSettings.getProperty("TvT_EventRunningTime", 20);
        EVENT_TvTFighterBuffs = eventTvTSettings.getProperty("TvT_FighterBuffs", "").trim().replaceAll(" ", "").split(";");
        EVENT_TvTMageBuffs = eventTvTSettings.getProperty("TvT_MageBuffs", "").trim().replaceAll(" ", "").split(";");
        EVENT_TvTBuffPlayers = eventTvTSettings.getProperty("TvT_BuffPlayers", false);
        EVENT_TvTrate = eventTvTSettings.getProperty("TvT_rate", true);
        EVENT_TvTAllowParty = eventTvTSettings.getProperty("TvT_AllowParty", false);
        EVENT_TvTBlockItems = eventTvTSettings.getProperty("TvT_BlockItems", new int[]{4037, 57});
    }

    public static void loadChestEvilSettings() {
        ExProperties eventChestEvilSettings = load(EVENT_CHEST_EVIL);

        EVENT_CHEST_EVIL_ALLOW = eventChestEvilSettings.getProperty("EventChestEvilAllow", false);
        EVENT_CHEST_EVIL_ITEM = eventChestEvilSettings.getProperty("EventChestEvilItem", 7607);
        EVENT_CHEST_EVIL_RED = eventChestEvilSettings.getProperty("EventChestEvilTatLifeRed", 7244);
        EVENT_CHEST_EVIL_BLUE = eventChestEvilSettings.getProperty("EventChestEvilTatLifeBlue", 7239);
        EVENT_CHEST_EVIL_CHANCE = eventChestEvilSettings.getProperty("EventChestEvilCance", 35);
    }

    public static void loadL2DaySettings() {
        ExProperties eventL2DaySettings = load(EVENT_L2_DAY_FILE);

        EVENT_L2DAY_LETTER_CHANCE = eventL2DaySettings.getProperty("L2DAY_LETTER_CHANCE", 1.0D);
    }

    public static void loadBountyHuntersSettings() {
        ExProperties eventBountyHuntersSettings = load(EVENT_BOUNTY_HUNTERS_FILE);

        EVENT_BOUNTY_HUNTERS_ENABLED = eventBountyHuntersSettings.getProperty("BountyHuntersEnabled", true);
    }

    public static void loadFightClubSettings() {
        ExProperties eventFightClubSettings = load(EVENT_FIGHT_CLUB_FILE);

        FIGHT_CLUB_ENABLED = eventFightClubSettings.getProperty("FightClubEnabled", false);
        MINIMUM_LEVEL_TO_PARRICIPATION = eventFightClubSettings.getProperty("MinimumLevel", 1);
        MAXIMUM_LEVEL_TO_PARRICIPATION = eventFightClubSettings.getProperty("MaximumLevel", 85);
        MAXIMUM_LEVEL_DIFFERENCE = eventFightClubSettings.getProperty("MaximumLevelDifference", 10);
        ALLOWED_RATE_ITEMS = eventFightClubSettings.getProperty("AllowedItems", "").trim().replaceAll(" ", "").split(",");
        PLAYERS_PER_PAGE = eventFightClubSettings.getProperty("RatesOnPage", 10);
        ARENA_TELEPORT_DELAY = eventFightClubSettings.getProperty("ArenaTeleportDelay", 5);
        CANCEL_BUFF_BEFORE_FIGHT = eventFightClubSettings.getProperty("CancelBuffs", true);
        UNSUMMON_PETS = eventFightClubSettings.getProperty("UnsummonPets", true);
        UNSUMMON_SUMMONS = eventFightClubSettings.getProperty("UnsummonSummons", true);
        REMOVE_CLAN_SKILLS = eventFightClubSettings.getProperty("RemoveClanSkills", false);
        REMOVE_HERO_SKILLS = eventFightClubSettings.getProperty("RemoveHeroSkills", false);
        TIME_TO_PREPARATION = eventFightClubSettings.getProperty("TimeToPreparation", 10);
        FIGHT_TIME = eventFightClubSettings.getProperty("TimeToDraw", 300);
        ALLOW_DRAW = eventFightClubSettings.getProperty("AllowDraw", true);
        TIME_TELEPORT_BACK = eventFightClubSettings.getProperty("TimeToBack", 10);
        FIGHT_CLUB_ANNOUNCE_RATE = eventFightClubSettings.getProperty("AnnounceRate", false);
        FIGHT_CLUB_ANNOUNCE_RATE_TO_SCREEN = eventFightClubSettings.getProperty("AnnounceRateToAllScreen", false);
        FIGHT_CLUB_ANNOUNCE_START_TO_SCREEN = eventFightClubSettings.getProperty("AnnounceStartBatleToAllScreen", false);
    }

    public static void loadGlitteringMedalSettings() {
        ExProperties eventGlitteringMedalSettings = load(EVENT_GLITTERING_MEDAL_FILE);

        EVENT_GLITTMEDAL_NORMAL_CHANCE = eventGlitteringMedalSettings.getProperty("MEDAL_CHANCE", 10.0D);
        EVENT_GLITTMEDAL_GLIT_CHANCE = eventGlitteringMedalSettings.getProperty("GLITTMEDAL_CHANCE", 0.1D);
    }

    public static void loadMasterOfEnchaningSettings() {
        ExProperties eventMasterOfEnchaningSettings = load(EVENT_MASTER_OF_ENCHANING_FILE);

        ENCHANT_CHANCE_MASTER_YOGI_STAFF = eventMasterOfEnchaningSettings.getProperty("MasterYogiEnchantChance", 66);
        ENCHANT_MAX_MASTER_YOGI_STAFF = eventMasterOfEnchaningSettings.getProperty("MasterYogiEnchantMaxWeapon", 28);
        SAFE_ENCHANT_MASTER_YOGI_STAFF = eventMasterOfEnchaningSettings.getProperty("MasterYogiSafeEnchant", 3);
    }

    public static void loadPcBangSettings() {
        ExProperties eventPcBangSettings = load(EVENT_PC_BANG_FILE);
        ALT_PCBANG_POINTS_ENABLED = eventPcBangSettings.getProperty("AltPcBangPointsEnabled", false);
        ALT_MAX_PC_BANG_POINTS = eventPcBangSettings.getProperty("AltPcBangPointsMaxCount", 20000);
        ALT_PCBANG_POINTS_ON_START = eventPcBangSettings.getProperty("AltPcBangPointsOnStart", 300);
        ALT_PCBANG_POINTS_BONUS = eventPcBangSettings.getProperty("AltPcBangPointsBonus", 100);
        ALT_PCBANG_POINTS_DELAY = eventPcBangSettings.getProperty("AltPcBangPointsDelay", 5);
        ALT_PCBANG_POINTS_MIN_LVL = eventPcBangSettings.getProperty("AltPcBangPointsMinLvl", 1);
        ALT_PCBANG_POINTS_BONUS_DOUBLE_CHANCE = eventPcBangSettings.getProperty("AltPcBangPointsDoubleChance", 10.0D);
        ALT_PCBANG_POINTS_MAX_CODE_ENTER_ATTEMPTS = eventPcBangSettings.getProperty("AltPcBangPointsMaxEnterAttempts", 5);
        ALT_PCBANG_POINTS_BAN_TIME = eventPcBangSettings.getProperty("AltPcBangPointsBanTime", 480L);
        ALT_PCBANG_POINTS_COUPON_TEMPLATE = eventPcBangSettings.getProperty("AltPcBangPointsCouponTemplate", "[A-Za-z0-9]{20,20}");
        PC_BANG_TO_ITEMMALL = eventPcBangSettings.getProperty("AltPcBangPointToItemMall", false);
        PC_BANG_TO_ITEMMALL_RATE = eventPcBangSettings.getProperty("AltPcBangPointToItemMallRate", 100);
        PC_BANG_ENCHANT_MAX = eventPcBangSettings.getProperty("AltPcBangEnchantMaxLevel", 23);
        PC_BANG_SAFE_ENCHANT = eventPcBangSettings.getProperty("AltPcBangEnchantSafeLevel", 3);
        ALT_PC_BANG_WIVERN_PRICE = eventPcBangSettings.getProperty("AltPcBangWiwernRentPrice", 2500);
        ALT_PC_BANG_WIVERN_TIME = eventPcBangSettings.getProperty("AltPcBangWiwernRentTime", 5);
    }

    public static void loadTheFallHarvestSettings() {
        ExProperties eventTheFallHarvestSettings = load(EVENT_THE_FALL_HARVEST_FILE);

        EVENT_TFH_POLLEN_CHANCE = eventTheFallHarvestSettings.getProperty("TFH_POLLEN_CHANCE", 5.0D);
    }

    public static void loadDefenseTownSettings() {
        ExProperties TmWaweSettings = load(DEFENSE_TOWNS_CONFIG_FILE);

        TMEnabled = TmWaweSettings.getProperty("DefenseTownsEnabled", false);
        TMStartHour = TmWaweSettings.getProperty("DefenseTownsStartHour", 19);
        TMStartMin = TmWaweSettings.getProperty("DefenseTownsStartMin", 0);

        TMEventInterval = TmWaweSettings.getProperty("DefenseTownsEventInterval", 1) * 60000;

        TMMobLife = TmWaweSettings.getProperty("DefenseTownsMobLife", 10) * 60000;

        BossLifeTime = TmWaweSettings.getProperty("BossLifeTime", 25) * 60000;

        TMTime1 = TmWaweSettings.getProperty("DefenseTownsTime1", 2) * 60000;
        TMTime2 = TmWaweSettings.getProperty("DefenseTownsTime2", 5) * 60000;
        TMTime3 = TmWaweSettings.getProperty("DefenseTownsTime3", 5) * 60000;
        TMTime4 = TmWaweSettings.getProperty("DefenseTownsTime4", 5) * 60000;
        TMTime5 = TmWaweSettings.getProperty("DefenseTownsTime5", 5) * 60000;
        TMTime6 = TmWaweSettings.getProperty("DefenseTownsTime6", 5) * 60000;

        TMWave1 = TmWaweSettings.getProperty("DefenseTownsWave1", 18855);
        TMWave2 = TmWaweSettings.getProperty("DefenseTownsWave2", 18855);
        TMWave3 = TmWaweSettings.getProperty("DefenseTownsWave3", 25699);
        TMWave4 = TmWaweSettings.getProperty("DefenseTownsWave4", 18855);
        TMWave5 = TmWaweSettings.getProperty("DefenseTownsWave5", 18855);
        TMWave6 = TmWaweSettings.getProperty("DefenseTownsWave6", 25699);

        TMWave1Count = TmWaweSettings.getProperty("DefenseTownsWave1Count", 3);
        TMWave2Count = TmWaweSettings.getProperty("DefenseTownsWave2Count", 2);
        TMWave3Count = TmWaweSettings.getProperty("DefenseTownsWave3Count", 2);
        TMWave4Count = TmWaweSettings.getProperty("DefenseTownsWave4Count", 2);
        TMWave5Count = TmWaweSettings.getProperty("DefenseTownsWave5Count", 2);
        TMWave6Count = TmWaweSettings.getProperty("DefenseTownsWave6Count", 2);

        TMBoss = TmWaweSettings.getProperty("DefenseTownsBoss", 25700);

        TMItem = TmWaweSettings.getProperty("DefenseTownsItem", new int[]{4037, 57, 9552, 9553, 9554, 9555, 9556, 9557, 6577, 6578});
        TMItemCol = TmWaweSettings.getProperty("DefenseTownsItemCol", new int[]{1, 77700000, 1, 1, 1, 1, 1, 1, 1, 1});
        TMItemColBoss = TmWaweSettings.getProperty("DefenseTownsItemColBoss", new int[]{5, 77700000, 10, 10, 10, 10, 10, 10, 2, 2});
        TMItemChance = TmWaweSettings.getProperty("DefenseTownsItemChance", new int[]{20, 40, 10, 10, 10, 10, 10, 10, 20, 20});
        TMItemChanceBoss = TmWaweSettings.getProperty("DefenseTownsItemChanceBoss", new int[]{50, 40, 50, 50, 50, 50, 50, 50, 20, 20});
    }

    public static void loadTreasuresOfTheHeraldSettings() {
        ExProperties eventTreasuresOfTheHeraldSettings = load(EVENT_TREASURES_OF_THE_HERALD_FILE);

        EVENT_TREASURES_OF_THE_HERALD_ENABLE = eventTreasuresOfTheHeraldSettings.getProperty("TOTHEnable", false);
        EVENT_TREASURES_OF_THE_HERALD_ITEM_ID = eventTreasuresOfTheHeraldSettings.getProperty("TOTHRewardId", 13067);
        EVENT_TREASURES_OF_THE_HERALD_ITEM_COUNT = eventTreasuresOfTheHeraldSettings.getProperty("TOTHRewardCount", 30);
        EVENT_TREASURES_OF_THE_HERALD_TIME = eventTreasuresOfTheHeraldSettings.getProperty("TOTHTime", 1200);
        EVENT_TREASURES_OF_THE_HERALD_MIN_LEVEL = eventTreasuresOfTheHeraldSettings.getProperty("TOTHMinLevel", 80);
        EVENT_TREASURES_OF_THE_HERALD_MAX_LEVEL = eventTreasuresOfTheHeraldSettings.getProperty("TOTHMaxLevel", 85);
        EVENT_TREASURES_OF_THE_HERALD_MINIMUM_PARTY_MEMBER = eventTreasuresOfTheHeraldSettings.getProperty("TOTHMinPartyMember", 6);
        EVENT_TREASURES_OF_THE_HERALD_MAX_GROUP = eventTreasuresOfTheHeraldSettings.getProperty("TOTHMaxGroup", 100);
        EVENT_TREASURES_OF_THE_HERALD_SCORE_BOX = eventTreasuresOfTheHeraldSettings.getProperty("TOTHScoreBox", 20);
        EVENT_TREASURES_OF_THE_HERALD_SCORE_BOSS = eventTreasuresOfTheHeraldSettings.getProperty("TOTHScoreBoss", 100);
        EVENT_TREASURES_OF_THE_HERALD_SCORE_KILL = eventTreasuresOfTheHeraldSettings.getProperty("TOTHScoreKill", 5);
        EVENT_TREASURES_OF_THE_HERALD_SCORE_DEATH = eventTreasuresOfTheHeraldSettings.getProperty("TOTHScoreDeath", 3);
    }

    public static void loadUndergroundColiseumSettings() {
        ExProperties undergroundColiseumSettings = load(EVENT_UNDERGROUND_COLISEUM_FILE);

        ALT_ENABLE_UNDERGROUND_BATTLE_EVENT = undergroundColiseumSettings.getProperty("ArenaEnable", true);
        ALT_MIN_UNDERGROUND_BATTLE_TEAM_MEMBERS = undergroundColiseumSettings.getProperty("MinPlayersInTeam", 5);
        EVENT_UNDERGROUND_COLISEUM_ONLY_PATY = undergroundColiseumSettings.getProperty("EnterOnlyPaty", true);
    }

    public static void loadTvTArenaSettings() {
        ExProperties eventTvTArenaSettings = load(EVENT_TVT_ARENA_FILE);

        EVENT_TVT_ARENA_ENABLED = eventTvTArenaSettings.getProperty("Enabled", false);
        EVENT_TVT_ARENA_TECH_REASON = eventTvTArenaSettings.getProperty("TechReason", 0);
        EVENT_TVT_ARENA_NO_PLAYERS = eventTvTArenaSettings.getProperty("NoPlayers", 0);
        EVENT_TVT_ARENA_TEAM_DRAW = eventTvTArenaSettings.getProperty("Drow", 0);
        EVENT_TVT_ARENA_TEAM_WIN = eventTvTArenaSettings.getProperty("Win", 0);
        EVENT_TVT_ARENA_TEAM_LOSS = eventTvTArenaSettings.getProperty("Loss", 0);
        EVENT_TVT_ARENA_TEAMLEADER_EXIT = eventTvTArenaSettings.getProperty("TeamLeaderExit", 0);
        EVENT_TVT_ARENA_ALLOW_CLAN_SKILL = eventTvTArenaSettings.getProperty("AllowClanSkills", false);
        EVENT_TVT_ARENA_ALLOW_HERO_SKILL = eventTvTArenaSettings.getProperty("AllowHeroSkills", false);
        EVENT_TVT_ARENA_ALLOW_BUFFS = eventTvTArenaSettings.getProperty("AllowBuffs", false);
        EVENT_TVT_ARENA_TEAM_COUNT = eventTvTArenaSettings.getProperty("TeamCount", 0);
        EVENT_TVT_ARENA_TIME_TO_START = eventTvTArenaSettings.getProperty("TimeToStart", 0);
        EVENT_TVT_ARENA_FIGHT_TIME = eventTvTArenaSettings.getProperty("FightTime", 10);
        EVENT_TVT_ARENA_DISABLED_ITEMS = eventTvTArenaSettings.getProperty("DisabledItems", new int[]{10179, 15357, 20394, 21094, 21231, 21232});
        EVENT_TVT_ARENA_TEAM_COUNT_MIN = eventTvTArenaSettings.getProperty("MinTeamCount", 1);
        EVENT_TVT_ARENA_START_TIME = eventTvTArenaSettings.getProperty("EventStartTime", "20:12").trim().replaceAll(" ", "").split(",");
        EVENT_TVT_ARENA_STOP_TIME = eventTvTArenaSettings.getProperty("EventStopTime", "21:12").trim().replaceAll(" ", "").split(",");
    }

    public static void loadTrickOfTransmutationSettings() {
        ExProperties eventTrickOfTransmutationSettings = load(EVENT_TRICK_OF_TRANSMUTATION_FILE);

        EVENT_TRICK_OF_TRANS_CHANCE = eventTrickOfTransmutationSettings.getProperty("TRICK_OF_TRANS_CHANCE", 10.0D);
    }

    public static void loadRatesConfig() {
        ExProperties ratesSettings = load(RATES_FILE);

        ALT_DROP_RATE = ratesSettings.getProperty("AltFormulaDrop", true);
        RATE_XP = ratesSettings.getProperty("RateXp", 1.);
        RATE_SP = ratesSettings.getProperty("RateSp", 1.);
        RATE_QUESTS_REWARD = ratesSettings.getProperty("RateQuestsReward", 1.);
        RATE_QUESTS_DROP = ratesSettings.getProperty("RateQuestsDrop", 1.);
        RATE_DROP_CHAMPION = ratesSettings.getProperty("RateDropChampion", 1.);
        RATE_CLAN_REP_SCORE = ratesSettings.getProperty("RateClanRepScore", 1.);
        RATE_CLAN_REP_SCORE_MAX_AFFECTED = ratesSettings.getProperty("RateClanRepScoreMaxAffected", 2);
        RATE_DROP_ADENA = ratesSettings.getProperty("RateDropAdena", 1.);
        RATE_DROP_EPOLET = ratesSettings.getProperty("RateDropEpolet", 1.);
        RATE_CHAMPION_DROP_ADENA = ratesSettings.getProperty("RateChampionDropAdena", 1.);
        RATE_DROP_SPOIL_CHAMPION = ratesSettings.getProperty("RateSpoilChampion", 1.);
        RATE_DROP_ITEMS = ratesSettings.getProperty("RateDropItems", 1.);
        RATE_CHANCE_GROUP_DROP_ITEMS = ratesSettings.getProperty("RateChanceGroupDropItems", 1.);
        RATE_CHANCE_DROP_ADENA = ratesSettings.getProperty("RateChanceDropAdena", 1.);
        RATE_CHANCE_DROP_ITEMS = ratesSettings.getProperty("RateChanceDropItems", 1.);
        RATE_CHANCE_DROP_HERBS = ratesSettings.getProperty("RateChanceDropHerbs", 1.);
        RATE_CHANCE_SPOIL = ratesSettings.getProperty("RateChanceSpoil", 1.);
        RATE_CHANCE_SPOIL_WEAPON_ARMOR_ACCESSORY = ratesSettings.getProperty("RateChanceSpoilWAA", 1.);
        RATE_DROP_STONEPRINT = ratesSettings.getProperty("RateDropStonePrint", 1.);
        RATE_DROP_BLUE_STONE = ratesSettings.getProperty("RateDropBlueStone", 1.);
        RATE_DROP_GREEN_STONE = ratesSettings.getProperty("RateDropGreenStone", 1.);
        RATE_DROP_RED_STONE = ratesSettings.getProperty("RateDropRedStone", 1.);
        RATE_CHANCE_DROP_WEAPON_ARMOR_ACCESSORY = ratesSettings.getProperty("RateChanceDropWAA", 1.);
        RATE_CHANCE_DROP_EPOLET = ratesSettings.getProperty("RateChanceDropEpolets", 1.);
        NO_RATE_ENCHANT_SCROLL = ratesSettings.getProperty("NoRateEnchantScroll", true);
        CHAMPION_DROP_ONLY_ADENA = ratesSettings.getProperty("ChampionDropOnlyAdena", false);
        RATE_ENCHANT_SCROLL = ratesSettings.getProperty("RateDropEnchantScroll", 1.);
        NO_RATE_HERBS = ratesSettings.getProperty("NoRateHerbs", true);
        RATE_DROP_HERBS = ratesSettings.getProperty("RateDropHerbs", 1.);
        NO_RATE_ATT = ratesSettings.getProperty("NoRateAtt", true);
        RATE_DROP_ATT = ratesSettings.getProperty("RateDropAtt", 1.);
        NO_RATE_LIFE_STONE = ratesSettings.getProperty("NoRateLifeStone", true);
        NO_RATE_CODEX_BOOK = ratesSettings.getProperty("NoRateCodex", true);
        NO_RATE_FORGOTTEN_SCROLL = ratesSettings.getProperty("NoRateForgottenScroll", true);
        RATE_DROP_LIFE_STONE = ratesSettings.getProperty("RateDropLifeStone", 1.);
        NO_RATE_KEY_MATERIAL = ratesSettings.getProperty("NoRateKeyMaterial", true);
        RATE_DROP_KEY_MATERIAL = ratesSettings.getProperty("RateDropKeyMaterial", 1.);
        NO_RATE_RECIPES = ratesSettings.getProperty("NoRateRecipes", true);
        RATE_DROP_RECIPES = ratesSettings.getProperty("RateDropRecipes", 1.);
        RATE_DROP_COMMON_ITEMS = ratesSettings.getProperty("RateDropCommonItems", 1.);
        RATE_DROP_RAIDBOSS = ratesSettings.getProperty("RateRaidBoss", 1.);
        RATE_CHANCE_RAIDBOSS = ratesSettings.getProperty("RateChanceRaidBoss", 1.);

        RATE_DROP_SPOIL = ratesSettings.getProperty("RateDropSpoil", 1.);
        NO_RATE_ITEMS = ratesSettings.getProperty("NoRateItemIds", new int[]{
            6660,
            6662,
            6661,
            6659,
            6656,
            6658,
            8191,
            6657,
            10170,
            10314,
            16025,
            16026});
        NO_RATE_EQUIPMENT = ratesSettings.getProperty("NoRateEquipment", true);

        RATE_DROP_SIEGE_GUARD = ratesSettings.getProperty("RateSiegeGuard", 1.);

        RATE_MANOR = ratesSettings.getProperty("RateManor", 1.);
        RATE_FISH_DROP_COUNT = ratesSettings.getProperty("RateFishDropCount", 1.);
        RATE_PARTY_MIN = ratesSettings.getProperty("RatePartyMin", false);
        RATE_HELLBOUND_CONFIDENCE = ratesSettings.getProperty("RateHellboundConfidence", 1.);

        for (String prop : ratesSettings.getProperty("RateSiegeFame", "10,20").split(",")) {
            RATE_SIEGE_FAME.add(Integer.parseInt(prop));
        }
        for (String prop : ratesSettings.getProperty("RateDominionSiegeFame", "10,20").split(",")) {
            RATE_DOMINION_SIEGE_FAME.add(Integer.parseInt(prop));
        }

        RATE_MOB_SPAWN = ratesSettings.getProperty("RateMobSpawn", 1);
        RATE_MOB_SPAWN_MIN_LEVEL = ratesSettings.getProperty("RateMobMinLevel", 1);
        RATE_MOB_SPAWN_MAX_LEVEL = ratesSettings.getProperty("RateMobMaxLevel", 100);

    }

    public static void loadBossConfig() {
        ExProperties bossSettings = load(BOSS_FILE);

        RATE_RAID_REGEN = bossSettings.getProperty("RateRaidRegen", 1.);
        RATE_RAID_DEFENSE = bossSettings.getProperty("RateRaidDefense", 1.);
        RATE_RAID_ATTACK = bossSettings.getProperty("RateRaidAttack", 1.);
        RATE_EPIC_DEFENSE = bossSettings.getProperty("RateEpicDefense", RATE_RAID_DEFENSE);
        RATE_EPIC_ATTACK = bossSettings.getProperty("RateEpicAttack", RATE_RAID_ATTACK);
        RAID_MAX_LEVEL_DIFF = bossSettings.getProperty("RaidMaxLevelDiff", 8);
        PARALIZE_ON_RAID_DIFF = bossSettings.getProperty("ParalizeOnRaidLevelDiff", true);

        BOSS_BELETH_MIN_COUNT = bossSettings.getProperty("BossBelethMinCount", 36);
        MUTATED_ELPY_COUNT = bossSettings.getProperty("MutatedElpyCount", 16);
    }

    public static void loadNpcConfig() {
        ExProperties npcSettings = load(NPC_FILE);

        MIN_NPC_ANIMATION = npcSettings.getProperty("MinNPCAnimation", 5);
        MAX_NPC_ANIMATION = npcSettings.getProperty("MaxNPCAnimation", 90);
        SERVER_SIDE_NPC_NAME = npcSettings.getProperty("ServerSideNpcName", false);
        SERVER_SIDE_NPC_TITLE = npcSettings.getProperty("ServerSideNpcTitle", false);
    }

    public static void loadOtherConfig() {
        ExProperties otherSettings = load(OTHER_CONFIG_FILE);

        DEEPBLUE_DROP_RULES = otherSettings.getProperty("UseDeepBlueDropRules", true);
        DEEPBLUE_DROP_MAXDIFF = otherSettings.getProperty("DeepBlueDropMaxDiff", 8);
        DEEPBLUE_DROP_RAID_MAXDIFF = otherSettings.getProperty("DeepBlueDropRaidMaxDiff", 2);

        SWIMING_SPEED = otherSettings.getProperty("SwimingSpeedTemplate", 50);
        SAVE_PET_EFFECT = otherSettings.getProperty("SavePetEffect", true);

        /* Inventory slots limits */
        INVENTORY_MAXIMUM_NO_DWARF = otherSettings.getProperty("MaximumSlotsForNoDwarf", 80);
        INVENTORY_MAXIMUM_DWARF = otherSettings.getProperty("MaximumSlotsForDwarf", 100);
        INVENTORY_MAXIMUM_GM = otherSettings.getProperty("MaximumSlotsForGMPlayer", 250);
        QUEST_INVENTORY_MAXIMUM = otherSettings.getProperty("MaximumSlotsForQuests", 100);

        SHOW_HTML_WELCOME = otherSettings.getProperty("ShowHTMLWelcome", false);

        MULTISELL_SIZE = otherSettings.getProperty("MultisellPageSize", 10);

        /* Warehouse slots limits */
        WAREHOUSE_SLOTS_NO_DWARF = otherSettings.getProperty("BaseWarehouseSlotsForNoDwarf", 100);
        WAREHOUSE_SLOTS_DWARF = otherSettings.getProperty("BaseWarehouseSlotsForDwarf", 120);
        WAREHOUSE_SLOTS_CLAN = otherSettings.getProperty("MaximumWarehouseSlotsForClan", 200);
        FREIGHT_SLOTS = otherSettings.getProperty("MaximumFreightSlots", 10);

        /* chance to enchant an item over safe level */
        ENCHANT_CHANCE_WEAPON = otherSettings.getProperty("EnchantChance", 66);
        ENCHANT_CHANCE_ARMOR = otherSettings.getProperty("EnchantChanceArmor", ENCHANT_CHANCE_WEAPON);
        ENCHANT_CHANCE_ACCESSORY = otherSettings.getProperty("EnchantChanceAccessory", ENCHANT_CHANCE_ARMOR);
        ENCHANT_CHANCE_CRYSTAL_WEAPON = otherSettings.getProperty("EnchantChanceCrystal", 66);
        ENCHANT_CHANCE_CRYSTAL_ARMOR = otherSettings.getProperty("EnchantChanceCrystalArmor", ENCHANT_CHANCE_CRYSTAL_WEAPON);
        ENCHANT_CHANCE_CRYSTAL_ARMOR_OLF_1 = otherSettings.getProperty("EnchantChanceCrystalArmorOlf1", 30);
        ENCHANT_CHANCE_CRYSTAL_ARMOR_OLF_2 = otherSettings.getProperty("EnchantChanceCrystalArmorOlf2", 30);
        ENCHANT_CHANCE_CRYSTAL_ARMOR_OLF_3 = otherSettings.getProperty("EnchantChanceCrystalArmorOlf3", 30);
        ENCHANT_CHANCE_CRYSTAL_ARMOR_OLF_4 = otherSettings.getProperty("EnchantChanceCrystalArmorOlf4", 30);
        ENCHANT_CHANCE_CRYSTAL_ARMOR_OLF_5 = otherSettings.getProperty("EnchantChanceCrystalArmorOlf5", 25);
        ENCHANT_CHANCE_CRYSTAL_ARMOR_OLF_6 = otherSettings.getProperty("EnchantChanceCrystalArmorOlf6", 20);
        ENCHANT_CHANCE_CRYSTAL_ARMOR_OLF_7 = otherSettings.getProperty("EnchantChanceCrystalArmorOlf7", 15);
        ENCHANT_CHANCE_CRYSTAL_ARMOR_OLF_8 = otherSettings.getProperty("EnchantChanceCrystalArmorOlf8", 10);
        ENCHANT_CHANCE_CRYSTAL_ARMOR_OLF_9 = otherSettings.getProperty("EnchantChanceCrystalArmorOlf9", 5);
        ENCHANT_CHANCE_CRYSTAL_ARMOR_OLF = otherSettings.getProperty("EnchantChanceCrystalArmorOlf", 0);
        ENCHANT_CHANCE_CRYSTAL_ACCESSORY = otherSettings.getProperty("EnchantChanceCrystalAccessory", ENCHANT_CHANCE_CRYSTAL_ARMOR);
        SAFE_ENCHANT_COMMON = otherSettings.getProperty("SafeEnchantCommon", 3);
        SAFE_ENCHANT_FULL_BODY = otherSettings.getProperty("SafeEnchantFullBody", 4);
        ENCHANT_MAX = otherSettings.getProperty("EnchantMax", 20);
        SAFE_ENCHANT_LVL = otherSettings.getProperty("SafeEnchant", 3);
        SAFE_ENCHANT_LVL_OLF = otherSettings.getProperty("SafeEnchantOlf", 3);
        MAX_ENCHANT_OLF = otherSettings.getProperty("MaxEnchantOlf", 9);
        ARMOR_OVERENCHANT_HPBONUS_LIMIT = otherSettings.getProperty("ArmorOverEnchantHPBonusLimit", 10) - 3;
        SHOW_ENCHANT_EFFECT_RESULT = otherSettings.getProperty("ShowEnchantEffectResult", false);

        ENCHANT_CHANCE_WEAPON_BLESS = otherSettings.getProperty("EnchantChanceBless", 66);
        ENCHANT_CHANCE_ARMOR_BLESS = otherSettings.getProperty("EnchantChanceArmorBless", ENCHANT_CHANCE_WEAPON);
        ENCHANT_CHANCE_ACCESSORY_BLESS = otherSettings.getProperty("EnchantChanceAccessoryBless", ENCHANT_CHANCE_ARMOR);
        USE_ALT_ENCHANT = Boolean.parseBoolean(otherSettings.getProperty("UseAltEnchant", "False"));
        for (String prop : otherSettings.getProperty("EnchantWeaponFighter", "100,100,100,70,70,70,70,70,70,70,70,70,70,70,70,35,35,35,35,35").split(",")) {
            ENCHANT_WEAPON_FIGHT.add(Integer.parseInt(prop));
        }
        for (String prop : otherSettings.getProperty("EnchantWeaponFighterBlessed", "100,100,100,70,70,70,70,70,70,70,70,70,70,70,70,35,35,35,35,35").split(",")) {
            ENCHANT_WEAPON_FIGHT_BLESSED.add(Integer.parseInt(prop));
        }
        for (String prop : otherSettings.getProperty("EnchantWeaponFighterCrystal", "100,100,100,70,70,70,70,70,70,70,70,70,70,70,70,35,35,35,35,35").split(",")) {
            ENCHANT_WEAPON_FIGHT_CRYSTAL.add(Integer.parseInt(prop));
        }

        for (String prop : otherSettings.getProperty("EnchantWeaponMage", "100,100,100,70,70,70,70,70,70,70,70,70,70,70,70,35,35,35,35,35").split(",")) {
            ENCHANT_WEAPON_MAGE.add(Integer.parseInt(prop));
        }
        for (String prop : otherSettings.getProperty("EnchantWeaponMageBlessed", "100,100,100,70,70,70,70,70,70,70,70,70,70,70,70,35,35,35,35,35").split(",")) {
            ENCHANT_WEAPON_MAGE_BLESSED.add(Integer.parseInt(prop));
        }
        for (String prop : otherSettings.getProperty("EnchantWeaponMageCrystal", "100,100,100,70,70,70,70,70,70,70,70,70,70,70,70,35,35,35,35,35").split(",")) {
            ENCHANT_WEAPON_MAGE_CRYSTAL.add(Integer.parseInt(prop));
        }

        for (String prop : otherSettings.getProperty("EnchantArmor", "100,100,100,66,33,25,20,16,14,12,11,10,9,8,8,7,7,6,6,6").split(",")) {
            ENCHANT_ARMOR.add(Integer.parseInt(prop));
        }
        for (String prop : otherSettings.getProperty("EnchantArmorCrystal", "100,100,100,66,33,25,20,16,14,12,11,10,9,8,8,7,7,6,6,6").split(",")) {
            ENCHANT_ARMOR_CRYSTAL.add(Integer.parseInt(prop));
        }
        for (String prop : otherSettings.getProperty("EnchantArmorBlessed", "100,100,100,66,33,25,20,16,14,12,11,10,9,8,8,7,7,6,6,6").split(",")) {
            ENCHANT_ARMOR_BLESSED.add(Integer.parseInt(prop));
        }
        for (String prop : otherSettings.getProperty("EnchantJewelry", "100,100,100,66,33,25,20,16,14,12,11,10,9,8,8,7,7,6,6,6").split(",")) {
            ENCHANT_ARMOR_JEWELRY.add(Integer.parseInt(prop));
        }
        for (String prop : otherSettings.getProperty("EnchantJewelryCrystal", "100,100,100,66,33,25,20,16,14,12,11,10,9,8,8,7,7,6,6,6").split(",")) {
            ENCHANT_ARMOR_JEWELRY_CRYSTAL.add(Integer.parseInt(prop));
        }
        for (String prop : otherSettings.getProperty("EnchantJewelryBlessed", "100,100,100,66,33,25,20,16,14,12,11,10,9,8,8,7,7,6,6,6").split(",")) {
            ENCHANT_ARMOR_JEWELRY_BLESSED.add(Integer.parseInt(prop));
        }

        ENCHANT_ATTRIBUTE_STONE_CHANCE = otherSettings.getProperty("EnchantAttributeChance", 50);
        ENCHANT_ATTRIBUTE_CRYSTAL_CHANCE = otherSettings.getProperty("EnchantAttributeCrystalChance", 30);
        ALLOW_ALT_ATT_ENCHANT = otherSettings.getProperty("AllowAltAttributeEnchant", false);
        ALT_ATT_ENCHANT_WEAPON_VALUE = otherSettings.getProperty("AltAttributeCrystalWeaponValue", 150);
        ALT_ATT_ENCHANT_ARMOR_VALUE = otherSettings.getProperty("AltAttributeCrystalArmorValue", 120);

        ALLOW_KAMAEL_ALL_EQUIP = otherSettings.getProperty("AllowKamaelAllEquip", false);
        REGEN_SIT_WAIT = otherSettings.getProperty("RegenSitWait", false);

        STARTING_ADENA = otherSettings.getProperty("StartingAdena", 0);
        UNSTUCK_SKILL = otherSettings.getProperty("UnstuckSkill", true);

        /* Amount of HP, MP, and CP is restored */
        RESPAWN_RESTORE_CP = otherSettings.getProperty("RespawnRestoreCP", 0.) / 100;
        RESPAWN_RESTORE_HP = otherSettings.getProperty("RespawnRestoreHP", 65.) / 100;
        RESPAWN_RESTORE_MP = otherSettings.getProperty("RespawnRestoreMP", 0.) / 100;

        /* Maximum number of available slots for pvt stores */
        MAX_PVTSTORE_SLOTS_DWARF = otherSettings.getProperty("MaxPvtStoreSlotsDwarf", 5);
        MAX_PVTSTORE_SLOTS_OTHER = otherSettings.getProperty("MaxPvtStoreSlotsOther", 4);
        MAX_PVTCRAFT_SLOTS = otherSettings.getProperty("MaxPvtManufactureSlots", 20);

        SENDSTATUS_TRADE_JUST_OFFLINE = otherSettings.getProperty("SendStatusTradeJustOffline", false);
        SENDSTATUS_TRADE_MOD = otherSettings.getProperty("SendStatusTradeMod", 1.);
        SHOW_OFFLINE_MODE_IN_ONLINE = otherSettings.getProperty("ShowOfflineTradeInOnline", false);

        ANNOUNCE_MAMMON_SPAWN = otherSettings.getProperty("AnnounceMammonSpawn", true);

        GM_NAME_COLOUR = Integer.decode("0x" + otherSettings.getProperty("GMNameColour", "FFFFFF"));
        GM_HERO_AURA = otherSettings.getProperty("GMHeroAura", false);
        NORMAL_NAME_COLOUR = Integer.decode("0x" + otherSettings.getProperty("NormalNameColour", "FFFFFF"));
        CLANLEADER_NAME_COLOUR = Integer.decode("0x" + otherSettings.getProperty("ClanleaderNameColour", "FFFFFF"));

        GAME_POINT_ITEM_ID = otherSettings.getProperty("GamePointItemId", -1);
        STARTING_LVL = otherSettings.getProperty("StartingLvL", 0);
        MAX_PLAYER_CONTRIBUTION = otherSettings.getProperty("MaxPlayerContribution", 1000000);

        ENCHANT_MAX_WEAPON = otherSettings.getProperty("EnchantMaxWeapon", 20);
        ENCHANT_MAX_ARMOR = otherSettings.getProperty("EnchantMaxArmor", 20);
        ENCHANT_MAX_JEWELRY = otherSettings.getProperty("EnchantMaxJewelry", 20);

        ENCHANT_MAX_DESTRUCTION_WEAPON = otherSettings.getProperty("EnchantMaxDestructionWeapon", 15);
        ENCHANT_MAX_DESTRUCTION_ARMOR = otherSettings.getProperty("EnchantMaxDestructionArmor", 6);

        ENCHANT_ONE_CLICK = otherSettings.getProperty("EnchantOneClick", false);
        ENCHANT_ONE_CLICK_WEAPON = otherSettings.getProperty("EnchantOneClickWeapon", 0);
        ENCHANT_ONE_CLICK_ARMOR = otherSettings.getProperty("EnchantOneClickArmor", 0);
        ENCHANT_ONE_CLICK_ACCESSORY = otherSettings.getProperty("EnchantOneClickAccessory", 0);
    }

    public static void loadSpoilConfig() {
        ExProperties spoilSettings = load(SPOIL_CONFIG_FILE);

        BASE_SPOIL_RATE = spoilSettings.getProperty("BasePercentChanceOfSpoilSuccess", 78.);
        MINIMUM_SPOIL_RATE = spoilSettings.getProperty("MinimumPercentChanceOfSpoilSuccess", 1.);
        ALT_SPOIL_FORMULA = spoilSettings.getProperty("AltFormula", false);
        MANOR_SOWING_BASIC_SUCCESS = spoilSettings.getProperty("BasePercentChanceOfSowingSuccess", 100.);
        MANOR_SOWING_ALT_BASIC_SUCCESS = spoilSettings.getProperty("BasePercentChanceOfSowingAltSuccess", 10.);
        MANOR_HARVESTING_BASIC_SUCCESS = spoilSettings.getProperty("BasePercentChanceOfHarvestingSuccess", 90.);
        MANOR_DIFF_PLAYER_TARGET = spoilSettings.getProperty("MinDiffPlayerMob", 5);
        MANOR_DIFF_PLAYER_TARGET_PENALTY = spoilSettings.getProperty("DiffPlayerMobPenalty", 5.);
        MANOR_DIFF_SEED_TARGET = spoilSettings.getProperty("MinDiffSeedMob", 5);
        MANOR_DIFF_SEED_TARGET_PENALTY = spoilSettings.getProperty("DiffSeedMobPenalty", 5.);
        ALLOW_MANOR = spoilSettings.getProperty("AllowManor", true);
        MANOR_REFRESH_TIME = spoilSettings.getProperty("AltManorRefreshTime", 20);
        MANOR_REFRESH_MIN = spoilSettings.getProperty("AltManorRefreshMin", 00);
        MANOR_APPROVE_TIME = spoilSettings.getProperty("AltManorApproveTime", 6);
        MANOR_APPROVE_MIN = spoilSettings.getProperty("AltManorApproveMin", 00);
        MANOR_MAINTENANCE_PERIOD = spoilSettings.getProperty("AltManorMaintenancePeriod", 360000);
    }

    public static void loadInstancesConfig() {
        ExProperties instancesSettings = load(INSTANCES_FILE);

        ALLOW_INSTANCES_LEVEL_MANUAL = instancesSettings.getProperty("AllowInstancesLevelManual", false);
        ALLOW_INSTANCES_PARTY_MANUAL = instancesSettings.getProperty("AllowInstancesPartyManual", false);
        INSTANCES_LEVEL_MIN = instancesSettings.getProperty("InstancesLevelMin", 1);
        INSTANCES_LEVEL_MAX = instancesSettings.getProperty("InstancesLevelMax", 85);
        INSTANCES_PARTY_MIN = instancesSettings.getProperty("InstancesPartyMin", 2);
        INSTANCES_PARTY_MAX = instancesSettings.getProperty("InstancesPartyMax", 100);
    }

    public static void loadEpicBossConfig() {
        ExProperties epicBossSettings = load(EPIC_BOSS_FILE);

        FIXINTERVALOFANTHARAS_HOUR = epicBossSettings.getProperty("FWA_FIX_INTERVAL_OF_ANTHARAS_HOUR", 264);
        FIXINTERVALOFBAIUM_HOUR = epicBossSettings.getProperty("FIX_INTERVAL_OF_BAIUM_HOUR", 120);
        RANDOMINTERVALOFBAIUM = epicBossSettings.getProperty("RANDOM_INTERVAL_OF_BAIUM", 8);
        FIXINTERVALOFBAYLORSPAWN_HOUR = epicBossSettings.getProperty("FIX_INTERVAL_OF_BAYLOR_SPAWN_HOUR", 24);
        RANDOMINTERVALOFBAYLORSPAWN = epicBossSettings.getProperty("RANDOM_INTERVAL_OF_BAYLOR_SPAWN", 24);
        FIXINTERVALOFBELETHSPAWN_HOUR = epicBossSettings.getProperty("FIX_INTERVAL_OF_BELETH_SPAWN_HOUR", 48);
        FIXINTERVALOFSAILRENSPAWN_HOUR = epicBossSettings.getProperty("FIX_INTERVAL_OF_SAILREN_SPAWN_HOUR", 24);
        RANDOMINTERVALOFSAILRENSPAWN = epicBossSettings.getProperty("RANDOM_INTERVAL_OF_SAILREN_SPAWN", 24);
        FIXINTERVALOFVALAKAS = epicBossSettings.getProperty("FIX_INTERVAL_OF_VALAKAS_HOUR", 264);
        RANDOMINTERVALOFVALAKAS = epicBossSettings.getProperty("RANDOM_INTERVAL_OF_VALAKAS", 8);
        EPIC_BOSS_SPAWN_ANNON = epicBossSettings.getProperty("EpicBossSpawnAnnon", 5);
    }

    public static void loadFormulasConfig() {
        ExProperties formulasSettings = load(FORMULAS_CONFIGURATION_FILE);

        SKILLS_CHANCE_SHOW = formulasSettings.getProperty("SkillsShowChance", true);
        SKILLS_CHANCE_MOD = formulasSettings.getProperty("SkillsChanceMod", 11.);
        SKILLS_CHANCE_POW = formulasSettings.getProperty("SkillsChancePow", 0.5);
        SKILLS_CHANCE_MIN = formulasSettings.getProperty("SkillsChanceMin", 5.);
        SKILLS_CHANCE_CAP = formulasSettings.getProperty("SkillsChanceCap", 95.);
        SKILLS_MOB_CHANCE = formulasSettings.getProperty("SkillsMobChance", 0.5);
        SKILLS_DEBUFF_MOB_CHANCE = formulasSettings.getProperty("SkillsDebuffMobChance", 0.5);
        SKILLS_CAST_TIME_MIN = formulasSettings.getProperty("SkillsCastTimeMin", 333);
        DELAY_ATACK_TIME_MIN = formulasSettings.getProperty("DelayAtackTimeMin", 333);

        ALT_ABSORB_DAMAGE_MODIFIER = formulasSettings.getProperty("AbsorbDamageModifier", 1.0);

        LIM_CP = formulasSettings.getProperty("LimitCP", 100000);
        LIM_HP = formulasSettings.getProperty("LimitHP", 40000);
        LIM_MP = formulasSettings.getProperty("LimitMP", 40000);
        LIM_PATK = formulasSettings.getProperty("LimitPatk", 20000);
        LIM_MATK = formulasSettings.getProperty("LimitMAtk", 25000);
        LIM_PDEF = formulasSettings.getProperty("LimitPDef", 15000);
        LIM_MDEF = formulasSettings.getProperty("LimitMDef", 15000);
        LIM_PATK_SPD = formulasSettings.getProperty("LimitPatkSpd", 1500);
        LIM_MATK_SPD = formulasSettings.getProperty("LimitMatkSpd", 1999);
        LIM_CRIT_DAM = formulasSettings.getProperty("LimitCriticalDamage", 2000);
        LIM_CRIT = formulasSettings.getProperty("LimitCritical", 500);
        LIM_MCRIT = formulasSettings.getProperty("LimitMCritical", 20);
        LIM_ACCURACY = formulasSettings.getProperty("LimitAccuracy", 200);
        LIM_EVASION = formulasSettings.getProperty("LimitEvasion", 200);
        LIM_MOVE = formulasSettings.getProperty("LimitMove", 250);
        GM_LIM_MOVE = formulasSettings.getProperty("GmLimitMove", 1500);

        HEAL_POWER = formulasSettings.getProperty("HealPower", 2000.);
        CPHEAL_EFFECTIVNESS = formulasSettings.getProperty("CpHealEffectivness", 1000.);
        MANAHEAL_EFFECTIVNESS = formulasSettings.getProperty("ManaHealEffectivness", 1000.);
        HEAL_EFFECTIVNESS = formulasSettings.getProperty("HealEffectivness", 1000.);

        LIM_FAME = formulasSettings.getProperty("LimitFame", 50000);

        ALT_NPC_PATK_MODIFIER = formulasSettings.getProperty("NpcPAtkModifier", 1.0);
        ALT_NPC_MATK_MODIFIER = formulasSettings.getProperty("NpcMAtkModifier", 1.0);
        ALT_NPC_MAXHP_MODIFIER = formulasSettings.getProperty("NpcMaxHpModifier", 1.00);
        ALT_NPC_MAXMP_MODIFIER = formulasSettings.getProperty("NpcMapMpModifier", 1.00);
        ALT_NPC_PDEF_MODIFIER = formulasSettings.getProperty("NpcPDefModifier", 1.0);
        ALT_NPC_MDEF_MODIFIER = formulasSettings.getProperty("NpcMDefModifier", 1.0);

        ALT_POLE_DAMAGE_MODIFIER = formulasSettings.getProperty("PoleDamageModifier", 1.0);
    }

    public static void loadDevelopSettings() {
        ExProperties properties = load(DEVELOP_FILE);

    }

    public static void loadLicenseSettings() {
        ExProperties properties = load(LIC);
        USER_NAME = properties.getProperty("UserName", "test");
    }

    public static void loadExtSettings() {
        ExProperties properties = load(EXT_FILE);

        EX_NEW_PETITION_SYSTEM = properties.getProperty("NewPetitionSystem", false);
        EX_JAPAN_MINIGAME = properties.getProperty("JapanMinigame", false);
        EX_LECTURE_MARK = properties.getProperty("LectureMark", false);
        EX_USE_TELEPORT_FLAG = properties.getProperty("UseTeleportFlag", true);
    }

    public static void loadItemsSettings() {
        ExProperties itemsProperties = load(ITEMS_FILE);

        CAN_BE_TRADED_NO_TARADEABLE = itemsProperties.getProperty("CanBeTradedNoTradeable", false);
        CAN_BE_TRADED_NO_SELLABLE = itemsProperties.getProperty("CanBeTradedNoSellable", false);
        CAN_BE_TRADED_NO_STOREABLE = itemsProperties.getProperty("CanBeTradedNoStoreable", false);
        CAN_BE_TRADED_SHADOW_ITEM = itemsProperties.getProperty("CanBeTradedShadowItem", false);
        CAN_BE_TRADED_HERO_WEAPON = itemsProperties.getProperty("CanBeTradedHeroWeapon", false);
        CAN_BE_WH_NO_TARADEABLE = itemsProperties.getProperty("CanBeWhNoTradeable", false);
        CAN_BE_CWH_NO_TARADEABLE = itemsProperties.getProperty("CanBeCwhNoTradeable", false);
        CAN_BE_CWH_IS_AUGMENTED = itemsProperties.getProperty("CanBeCwhIsAugmented", false);
        CAN_BE_WH_IS_AUGMENTED = itemsProperties.getProperty("CanBeWhIsAugmented", false);
        ALLOW_SOUL_SPIRIT_SHOT_INFINITELY = itemsProperties.getProperty("AllowSoulSpiritShotInfinitely", false);
        ALLOW_SOUL_SPIRIT_SHOT_INFINITELY_PET = itemsProperties.getProperty("AllowSoulSpiritShotInfinitelyPet", false);
        ALLOW_ARROW_INFINITELY = itemsProperties.getProperty("AllowArrowInfinitely", false);
        ALLOW_START_ITEMS = itemsProperties.getProperty("AllowStartItems", false);
        START_ITEMS_MAGE = itemsProperties.getProperty("StartItemsMageIds", new int[]{57});
        START_ITEMS_MAGE_COUNT = itemsProperties.getProperty("StartItemsMageCount", new int[]{1});
        START_ITEMS_FITHER = itemsProperties.getProperty("StartItemsFigtherIds", new int[]{57});
        START_ITEMS_FITHER_COUNT = itemsProperties.getProperty("StartItemsFigtherCount", new int[]{1});
    }

    public static void loadTopSettings() {
        ExProperties topSettings = load(TOP_FILE);

        L2TOP_MANAGER_ENABLED = topSettings.getProperty("L2TopManagerEnabled", false);
        L2TOP_MANAGER_INTERVAL = topSettings.getProperty("L2TopManagerInterval", 300000);
        L2TOP_WEB_ADDRESS = topSettings.getProperty("L2TopWebAddress", "");
        L2TOP_SMS_ADDRESS = topSettings.getProperty("L2TopSmsAddress", "");
        L2TOP_SERVER_ADDRESS = topSettings.getProperty("L2TopServerAddress", "first-team.ru");
        L2TOP_SAVE_DAYS = topSettings.getProperty("L2TopSaveDays", 30);
        L2TOP_REWARD = topSettings.getProperty("L2TopReward", new int[0]);
        L2TOP_SERVER_PREFIX = topSettings.getProperty("L2TopServerPrefix", "");

        MMOTOP_MANAGER_ENABLED = topSettings.getProperty("MMOTopEnable", false);
        MMOTOP_MANAGER_INTERVAL = topSettings.getProperty("MMOTopManagerInterval", 300000);
        MMOTOP_WEB_ADDRESS = topSettings.getProperty("MMOTopUrl", "");
        MMOTOP_SERVER_ADDRESS = topSettings.getProperty("MMOTopServerAddress", "first-team.ru");
        MMOTOP_SAVE_DAYS = topSettings.getProperty("MMOTopSaveDays", 30);
        MMOTOP_REWARD = topSettings.getProperty("MMOTopReward", new int[0]);

        ALLOW_HOPZONE_VOTE_REWARD = topSettings.getProperty("AllowHopzoneVoteReward", false);
        HOPZONE_SERVER_LINK = topSettings.getProperty("HopzoneServerLink", "http://l2.hopzone.net/lineage2/details/74078/L2World-Servers/");
        HOPZONE_FIRST_PAGE_LINK = topSettings.getProperty("HopzoneFirstPageLink", "http://l2.hopzone.net/lineage2/");
        HOPZONE_VOTES_DIFFERENCE = topSettings.getProperty("HopzoneVotesDifference", 5);
        HOPZONE_FIRST_PAGE_RANK_NEEDED = topSettings.getProperty("HopzoneFirstPageRankNeeded", 15);
        HOPZONE_REWARD_CHECK_TIME = topSettings.getProperty("HopzoneRewardCheckTime", 5);
        String HOPZONE_SMALL_REWARD_VALUE = topSettings.getProperty("HopzoneSmallReward", "57,100000000;");
        String[] hopzone_small_reward_splitted_1 = HOPZONE_SMALL_REWARD_VALUE.split(";");
        for (String i : hopzone_small_reward_splitted_1) {
            String[] hopzone_small_reward_splitted_2 = i.split(",");
            HOPZONE_SMALL_REWARD.put(Integer.parseInt(hopzone_small_reward_splitted_2[0]), Integer.parseInt(hopzone_small_reward_splitted_2[1]));
        }
        String HOPZONE_BIG_REWARD_VALUE = topSettings.getProperty("HopzoneBigReward", "3470,1;");
        String[] hopzone_big_reward_splitted_1 = HOPZONE_BIG_REWARD_VALUE.split(";");
        for (String i : hopzone_big_reward_splitted_1) {
            String[] hopzone_big_reward_splitted_2 = i.split(",");
            HOPZONE_BIG_REWARD.put(Integer.parseInt(hopzone_big_reward_splitted_2[0]), Integer.parseInt(hopzone_big_reward_splitted_2[1]));
        }
        HOPZONE_DUALBOXES_ALLOWED = topSettings.getProperty("HopzoneDualboxesAllowed", 1);
        ALLOW_HOPZONE_GAME_SERVER_REPORT = topSettings.getProperty("AllowHopzoneGameServerReport", false);
        ALLOW_TOPZONE_VOTE_REWARD = topSettings.getProperty("AllowTopzoneVoteReward", false);
        TOPZONE_SERVER_LINK = topSettings.getProperty("TopzoneServerLink", "http://l2.topzone.net/lineage2/details/74078/L2World-Servers/");
        TOPZONE_FIRST_PAGE_LINK = topSettings.getProperty("TopzoneFirstPageLink", "http://l2.topzone.net/lineage2/");
        TOPZONE_VOTES_DIFFERENCE = topSettings.getProperty("TopzoneVotesDifference", 5);
        TOPZONE_FIRST_PAGE_RANK_NEEDED = topSettings.getProperty("TopzoneFirstPageRankNeeded", 15);
        TOPZONE_REWARD_CHECK_TIME = topSettings.getProperty("TopzoneRewardCheckTime", 5);
        String TOPZONE_SMALL_REWARD_VALUE = topSettings.getProperty("TopzoneSmallReward", "57,100000000;");
        String[] topzone_small_reward_splitted_1 = TOPZONE_SMALL_REWARD_VALUE.split(";");
        for (String i : topzone_small_reward_splitted_1) {
            String[] topzone_small_reward_splitted_2 = i.split(",");
            TOPZONE_SMALL_REWARD.put(Integer.parseInt(topzone_small_reward_splitted_2[0]), Integer.parseInt(topzone_small_reward_splitted_2[1]));
        }
        String TOPZONE_BIG_REWARD_VALUE = topSettings.getProperty("TopzoneBigReward", "3470,1;");
        String[] topzone_big_reward_splitted_1 = TOPZONE_BIG_REWARD_VALUE.split(";");
        for (String i : topzone_big_reward_splitted_1) {
            String[] topzone_big_reward_splitted_2 = i.split(",");
            TOPZONE_BIG_REWARD.put(Integer.parseInt(topzone_big_reward_splitted_2[0]), Integer.parseInt(topzone_big_reward_splitted_2[1]));
        }
        TOPZONE_DUALBOXES_ALLOWED = topSettings.getProperty("TopzoneDualboxesAllowed", 1);
        ALLOW_TOPZONE_GAME_SERVER_REPORT = topSettings.getProperty("AllowTopzoneGameServerReport", false);
    }

    public static void loadPaymentSettings() {
        ExProperties paymentSettings = load(PAYMENT_FILE);

        SMS_PAYMENT_MANAGER_ENABLED = paymentSettings.getProperty("SMSPaymentEnabled", false);
        SMS_PAYMENT_WEB_ADDRESS = paymentSettings.getProperty("SMSPaymentWebAddress", "");
        SMS_PAYMENT_MANAGER_INTERVAL = paymentSettings.getProperty("SMSPaymentManagerInterval", 300000);
        SMS_PAYMENT_SAVE_DAYS = paymentSettings.getProperty("SMSPaymentSaveDays", 30);
        SMS_PAYMENT_SERVER_ADDRESS = paymentSettings.getProperty("SMSPaymentServerAddress", "emurt.ru");
        SMS_PAYMENT_REWARD = paymentSettings.getProperty("SMSPaymentReward", new int[0]);
    }

    public static void loadSkillSettings() {
        ExProperties skillSettings = load(SKILL_FILE);

        ALLOW_CHAIN_HEAL_CURSED = skillSettings.getProperty("AllowChainHealCursed", true);
        ALLOW_CHAIN_HEAL_FRIEND = skillSettings.getProperty("AllowChainHealFriend", true);
        ALLOW_CHAIN_HEAL_EVENT = skillSettings.getProperty("AllowChainHealEvent", true);
    }

    public static void loadQuestRateSettings() {
        ExProperties questRate = load(QUESTS_RATE);

        QUEST_RATES.clear();
        Iterator interator = questRate.entrySet().iterator();
        while (interator.hasNext()) {
            Map.Entry entry = (Map.Entry) interator.next();
            String str1 = (String) entry.getKey();
            String str2 = (String) entry.getValue();
            String[] str = str1.split("\\.");
            double[] rates = (double[]) QUEST_RATES.get(str[0]);
            if (rates == null) {
                rates = new double[]{1.0, 1.0, 1.0, 1.0};
                QUEST_RATES.put(str[0], rates);
            }
            String str3 = str[1];
            int i = -1;
            if (str3.equals("drop")) {
                i = DROP;
            } else if (str3.equals("reward")) {
                i = REWARD;
            } else if (str3.equals("exp")) {
                i = EXP;
            } else {
                if (!str3.equals("sp")) {
                    continue;
                }
                i = SP;
            }
            rates[i] = Double.parseDouble(str2);
        }
    }

    public static void loadAltSettings() {
        ExProperties altSettings = load(ALT_SETTINGS_FILE);

        ALT_REUSE_CORRECTION = altSettings.getProperty("AltReuseCorrection", 1050);
        ALT_SPIRITSHOT_DISCHARGE_CORRECTION = altSettings.getProperty("AltSpiritShotDischargeCorrection", 100);
        ALT_ARENA_EXP = altSettings.getProperty("ArenaExp", true);
        ALT_GAME_DELEVEL = altSettings.getProperty("Delevel", true);
        ALT_DAMAGE_INVIS = altSettings.getProperty("InDamage", false);
        ALT_VISIBLE_SIEGE_IN_ICONS = altSettings.getProperty("AllVisibleSiegeInIcons", false);

        ALT_ENABLED_PRICE_ALL = altSettings.getProperty("EnabledPriceAll", false);
        ALT_ENABLED_PRICE = altSettings.getProperty("EnabledPrice", 1);
        ALT_SELL_PRICE_PERCENT = altSettings.getProperty("SellPricePercent", 2);

        ALT_VITALITY_NEVIT_UP_POINT = altSettings.getProperty("AltVitalityNevitUpPoint", 10);
        ALT_VITALITY_NEVIT_POINT = altSettings.getProperty("AltVitalityNevitPoint", 10);

        ALT_SAVE_UNSAVEABLE = altSettings.getProperty("AltSaveUnsaveable", false);
        SHIELD_SLAM_BLOCK_IS_MUSIC = altSettings.getProperty("ShieldSlamBlockIsMusic", false);
        ALT_SAVE_EFFECTS_REMAINING_TIME = altSettings.getProperty("AltSaveEffectsRemainingTime", 5);
        ALT_SHOW_REUSE_MSG = altSettings.getProperty("AltShowSkillReuseMessage", true);
        ALT_DELETE_SA_BUFFS = altSettings.getProperty("AltDeleteSABuffs", false);
        ALT_MUSIC_COST_GUARD_INTERVAL = altSettings.getProperty("AltMusicCostGuardInterval", 0);
        AUTO_LOOT = altSettings.getProperty("AutoLoot", false);
        AUTO_LOOT_ONLY_ADENA = altSettings.getProperty("AutoLootOnlyAdena", false);
        AUTO_LOOT_HERBS = altSettings.getProperty("AutoLootHerbs", false);
        AUTO_LOOT_INDIVIDUAL = altSettings.getProperty("AutoLootIndividual", false);
        AUTO_LOOT_FROM_RAIDS = altSettings.getProperty("AutoLootFromRaids", false);
        AUTO_LOOT_PK = altSettings.getProperty("AutoLootPK", false);
        ALT_GAME_KARMA_PLAYER_CAN_SHOP = altSettings.getProperty("AltKarmaPlayerCanShop", false);
        SAVING_SPS = altSettings.getProperty("SavingSpS", false);
        MANAHEAL_SPS_BONUS = altSettings.getProperty("ManahealSpSBonus", false);
        CRAFT_MASTERWORK_CHANCE = altSettings.getProperty("CraftMasterworkChance", 3.);
        CRAFT_DOUBLECRAFT_CHANCE = altSettings.getProperty("CraftDoubleCraftChance", 3.);
        ALT_RAID_RESPAWN_MULTIPLIER = altSettings.getProperty("AltRaidRespawnMultiplier", 1.0);
        ALT_ALLOW_AUGMENT_ALL = altSettings.getProperty("AugmentAll", false);
        ALT_ALLOW_DROP_AUGMENTED = altSettings.getProperty("AlowDropAugmented", false);
        ALT_GAME_UNREGISTER_RECIPE = altSettings.getProperty("AltUnregisterRecipe", true);
        ALT_GAME_SHOW_DROPLIST = altSettings.getProperty("AltShowDroplist", true);
        ALLOW_NPC_SHIFTCLICK = altSettings.getProperty("AllowShiftClick", true);
        ALT_FULL_NPC_STATS_PAGE = altSettings.getProperty("AltFullStatsPage", false);
        ALT_GAME_NOOBLES_WITHOUT_QUESTS = altSettings.getProperty("AltAllowNooblessWithoutQuest", false);
        ALT_GAME_SUBCLASS_WITHOUT_QUESTS = altSettings.getProperty("AltAllowSubClassWithoutQuest", false);
        ALT_ALLOW_SUBCLASS_WITHOUT_BAIUM = altSettings.getProperty("AltAllowSubClassWithoutBaium", true);
        ALT_GAME_LEVEL_TO_GET_SUBCLASS = altSettings.getProperty("AltLevelToGetSubclass", 75);
        ALT_GAME_START_LEVEL_TO_SUBCLASS = altSettings.getProperty("AltStartLevelToSubclass", 40);
        VITAMIN_PETS_FOOD_ID = altSettings.getProperty("AltVitaminPetsFoodId", -1);
        ALT_GAME_SUB_ADD = altSettings.getProperty("AltSubAdd", 0);
        ALT_GAME_SUB_BOOK = altSettings.getProperty("AltSubBook", false);
        ALT_SUB_DELETE_ALL_ON_CHANGE = altSettings.getProperty("AltSubDeleteAllChange", false);
        ALT_SUB_ALL_CHANGE = altSettings.getProperty("AltSubAllChange", false);
        ALT_MAX_LEVEL = Math.min(altSettings.getProperty("AltMaxLevel", 85), Experience.LEVEL.length - 1);
        ALT_MAX_SUB_LEVEL = Math.min(altSettings.getProperty("AltMaxSubLevel", 80), Experience.LEVEL.length - 1);
        ALT_ALLOW_OTHERS_WITHDRAW_FROM_CLAN_WAREHOUSE = altSettings.getProperty("AltAllowOthersWithdrawFromClanWarehouse", false);
        ALT_ALLOW_CLAN_COMMAND_ONLY_FOR_CLAN_LEADER = altSettings.getProperty("AltAllowClanCommandOnlyForClanLeader", true);
        EXPELLED_MEMBER_PENALTY = altSettings.getProperty("ExpelledMemberPenalty", 24);
        LEAVED_ALLY_PENALTY = altSettings.getProperty("LeavedAllyPenalty", 24);
        DISSOLVED_ALLY_PENALTY = altSettings.getProperty("DissolvedAllyPenalty", 24);
        ALT_GAME_REQUIRE_CLAN_CASTLE = altSettings.getProperty("AltRequireClanCastle", false);
        ALT_GAME_REQUIRE_CASTLE_DAWN = altSettings.getProperty("AltRequireCastleDawn", true);
        ALT_GAME_ALLOW_ADENA_DAWN = altSettings.getProperty("AltAllowAdenaDawn", true);
        ALT_ADD_RECIPES = altSettings.getProperty("AltAddRecipes", 0);
        SS_ANNOUNCE_PERIOD = altSettings.getProperty("SSAnnouncePeriod", 0);
        PETITIONING_ALLOWED = altSettings.getProperty("PetitioningAllowed", true);
        MAX_PETITIONS_PER_PLAYER = altSettings.getProperty("MaxPetitionsPerPlayer", 5);
        MAX_PETITIONS_PENDING = altSettings.getProperty("MaxPetitionsPending", 25);
        AUTO_LEARN_SKILLS = altSettings.getProperty("AutoLearnSkills", false);
        AUTO_LEARN_FORGOTTEN_SKILLS = altSettings.getProperty("AutoLearnForgottenSkills", false);
        ALT_SOCIAL_ACTION_REUSE = altSettings.getProperty("AltSocialActionReuse", false);
        ALT_DISABLE_SPELLBOOKS = altSettings.getProperty("AltDisableSpellbooks", false);
        ALT_SIMPLE_SIGNS = altSettings.getProperty("PushkinSignsOptions", false);
        ALT_TELE_TO_CATACOMBS = altSettings.getProperty("TeleToCatacombs", false);
        ALT_BS_CRYSTALLIZE = altSettings.getProperty("BSCrystallize", false);
        ALT_MAMMON_UPGRADE = altSettings.getProperty("MammonUpgrade", 6680500);
        ALT_MAMMON_EXCHANGE = altSettings.getProperty("MammonExchange", 10091400);
        ALT_ALLOW_TATTOO = altSettings.getProperty("AllowTattoo", false);
        ALT_BUFF_LIMIT = altSettings.getProperty("BuffLimit", 20);
        ALT_DEATH_PENALTY = altSettings.getProperty("EnableAltDeathPenalty", false);
        ALLOW_DEATH_PENALTY_C5 = altSettings.getProperty("EnableDeathPenaltyC5", true);
        ALT_DEATH_PENALTY_C5_CHANCE = altSettings.getProperty("DeathPenaltyC5Chance", 10);
        ALT_DEATH_PENALTY_C5_CHAOTIC_RECOVERY = altSettings.getProperty("ChaoticCanUseScrollOfRecovery", false);
        ALT_DEATH_PENALTY_C5_EXPERIENCE_PENALTY = altSettings.getProperty("DeathPenaltyC5RateExpPenalty", 1);
        ALT_DEATH_PENALTY_C5_KARMA_PENALTY = altSettings.getProperty("DeathPenaltyC5RateKarma", 1);
        ALT_PK_DEATH_RATE = altSettings.getProperty("AltPKDeathRate", 0.);
        NONOWNER_ITEM_PICKUP_DELAY = altSettings.getProperty("NonOwnerItemPickupDelay", 15L) * 1000L;
        ALT_NO_LASTHIT = altSettings.getProperty("NoLasthitOnRaid", false);
        ALT_KAMALOKA_NIGHTMARES_PREMIUM_ONLY = altSettings.getProperty("KamalokaNightmaresPremiumOnly", false);
        ALT_KAMALOKA_NIGHTMARE_REENTER = altSettings.getProperty("SellReenterNightmaresTicket", true);
        ALT_KAMALOKA_ABYSS_REENTER = altSettings.getProperty("SellReenterAbyssTicket", true);
        ALT_KAMALOKA_LAB_REENTER = altSettings.getProperty("SellReenterLabyrinthTicket", true);
        ALT_PET_HEAL_BATTLE_ONLY = altSettings.getProperty("PetsHealOnlyInBattle", true);
        CHAR_TITLE = altSettings.getProperty("CharTitle", false);
        ADD_CHAR_TITLE = altSettings.getProperty("CharAddTitle", "");

        ALT_ALLOW_SELL_COMMON = altSettings.getProperty("AllowSellCommon", true);
        ALT_ALLOW_SHADOW_WEAPONS = altSettings.getProperty("AllowShadowWeapons", true);
        ALT_DISABLED_MULTISELL = altSettings.getProperty("DisabledMultisells", ArrayUtils.EMPTY_INT_ARRAY);
        ALT_SHOP_PRICE_LIMITS = altSettings.getProperty("ShopPriceLimits", ArrayUtils.EMPTY_INT_ARRAY);
        ALT_SHOP_UNALLOWED_ITEMS = altSettings.getProperty("ShopUnallowedItems", ArrayUtils.EMPTY_INT_ARRAY);

        ALT_ALLOWED_PET_POTIONS = altSettings.getProperty("AllowedPetPotions", new int[]{735, 1060, 1061, 1062, 1374, 1375, 1539, 1540, 6035, 6036});

        FESTIVAL_MIN_PARTY_SIZE = altSettings.getProperty("FestivalMinPartySize", 5);
        FESTIVAL_RATE_PRICE = altSettings.getProperty("FestivalRatePrice", 1.0);

        RIFT_MIN_PARTY_SIZE = altSettings.getProperty("RiftMinPartySize", 5);
        RIFT_SPAWN_DELAY = altSettings.getProperty("RiftSpawnDelay", 10000);
        RIFT_MAX_JUMPS = altSettings.getProperty("MaxRiftJumps", 4);
        RIFT_AUTO_JUMPS_TIME = altSettings.getProperty("AutoJumpsDelay", 8);
        RIFT_AUTO_JUMPS_TIME_RAND = altSettings.getProperty("AutoJumpsDelayRandom", 120000);

        RIFT_ENTER_COST_RECRUIT = altSettings.getProperty("RecruitFC", 18);
        RIFT_ENTER_COST_SOLDIER = altSettings.getProperty("SoldierFC", 21);
        RIFT_ENTER_COST_OFFICER = altSettings.getProperty("OfficerFC", 24);
        RIFT_ENTER_COST_CAPTAIN = altSettings.getProperty("CaptainFC", 27);
        RIFT_ENTER_COST_COMMANDER = altSettings.getProperty("CommanderFC", 30);
        RIFT_ENTER_COST_HERO = altSettings.getProperty("HeroFC", 33);
        ALLOW_CLANSKILLS = altSettings.getProperty("AllowClanSkills", true);
        ALLOW_LEARN_TRANS_SKILLS_WO_QUEST = altSettings.getProperty("AllowLearnTransSkillsWOQuest", false);
        PARTY_LEADER_ONLY_CAN_INVITE = altSettings.getProperty("PartyLeaderOnlyCanInvite", true);
        ALLOW_TALK_WHILE_SITTING = altSettings.getProperty("AllowTalkWhileSitting", true);
        ALLOW_NOBLE_TP_TO_ALL = altSettings.getProperty("AllowNobleTPToAll", false);
        ALLOW_SS_TP_NO_REG = altSettings.getProperty("AllowCatacombNoReg", false);
        ALLOW_HTML_IN_DUEL = altSettings.getProperty("AllowHtmlInDuel", false);

        ALLOW_HTML_DISTANCE_DIALOG = altSettings.getProperty("AllowDistanceDialog", false);
        ALLOW_HTML_DISTANCE_DIALOG_NPC = altSettings.getProperty("AllowDistanceDailogNpc", new int[]{29025});

        ALLOW_VISUAL_FROM_AUGMENT = altSettings.getProperty("AllowVisualFromAugment", false);
        VISUAL_FROM_AUGMENT_CATALIS_WEAPON = altSettings.getProperty("VisualFromAugmentCatalisWeapon", new int[]{50026});
        VISUAL_FROM_AUGMENT_CATALIS_ARMOR = altSettings.getProperty("VisualFromAugmentCatalisArmor", new int[]{50040});
        VISUAL_FROM_AUGMENT_ID_IN = altSettings.getProperty("VisualFromAugmentIDIn", new int[]{4037});
        VISUAL_FROM_AUGMENT_PRICES_IN = altSettings.getProperty("VisualFromAugmentPricesIn", new int[]{50026});
        //    VISUAL_FROM_AUGMENT_ID_OUT = altSettings.getProperty("VisualFromAugmentIDOut", 57);
        //    VISUAL_FROM_AUGMENT_PRICES_OUT = altSettings.getProperty("VisualFromAugmentPricesOut", new int[]{50026});
        int k = 0;
        VISUAL_FROM_AUGMENT_ALL = new int[VISUAL_FROM_AUGMENT_CATALIS_WEAPON.length + VISUAL_FROM_AUGMENT_CATALIS_ARMOR.length];
        for (int i : VISUAL_FROM_AUGMENT_CATALIS_WEAPON) {
            VISUAL_FROM_AUGMENT_ALL[k] = i;
            k++;
        }
        for (int i : VISUAL_FROM_AUGMENT_CATALIS_ARMOR) {
            VISUAL_FROM_AUGMENT_ALL[k] = i;
            k++;
        }
        //VISUAL_FROM_AUGMENT_ALL = altSettings.getProperty("VisualFromAugmentCatalis", new int[]{50026});

        CLANHALL_BUFFTIME_MODIFIER = altSettings.getProperty("ClanHallBuffTimeModifier", 1.0);
        SONGDANCETIME_MODIFIER = altSettings.getProperty("SongDanceTimeModifier", 1.0);
        MAXLOAD_MODIFIER = altSettings.getProperty("MaxLoadModifier", 1.0);
        GATEKEEPER_MODIFIER = altSettings.getProperty("GkCostMultiplier", 1.0);
        GATEKEEPER_FREE = altSettings.getProperty("GkFree", 40);
        CRUMA_GATEKEEPER_LVL = altSettings.getProperty("GkCruma", 65);
        ALT_IMPROVED_PETS_LIMITED_USE = altSettings.getProperty("ImprovedPetsLimitedUse", false);

        ALT_CHAMPION_CHANCE1 = altSettings.getProperty("AltChampionChance1", 0.);
        ALT_CHAMPION_CHANCE2 = altSettings.getProperty("AltChampionChance2", 0.);
        ALT_CHAMPION_CAN_BE_AGGRO = altSettings.getProperty("AltChampionAggro", false);
        ALT_CHAMPION_CAN_BE_SOCIAL = altSettings.getProperty("AltChampionSocial", false);
        ALT_CHAMPION_DROP_HERBS = altSettings.getProperty("AltChampionDropHerbs", false);
        ALT_SHOW_MONSTERS_AGRESSION = altSettings.getProperty("AltShowMonstersAgression", false);
        ALT_SHOW_MONSTERS_LVL = altSettings.getProperty("AltShowMonstersLvL", false);
        ALT_CHAMPION_TOP_LEVEL = altSettings.getProperty("AltChampionTopLevel", 75);
        ALT_CHAMPION_MIN_LEVEL = altSettings.getProperty("AltChampionMinLevel", 20);

        ALT_VITALITY_ENABLED = altSettings.getProperty("AltVitalityEnabled", true);
        ALT_VITALITY_RATE = altSettings.getProperty("AltVitalityRate", 1.);
        ALT_VITALITY_CONSUME_RATE = altSettings.getProperty("AltVitalityConsumeRate", 1.);
        ALT_VITALITY_RAID_BONUS = altSettings.getProperty("AltVitalityRaidBonus", 2000);

        ALT_DEBUG_ENABLED = altSettings.getProperty("AltDebugEnabled", false);
        ALT_DEBUG_PVP_ENABLED = altSettings.getProperty("AltDebugPvPEnabled", false);
        ALT_DEBUG_PVP_DUEL_ONLY = altSettings.getProperty("AltDebugPvPDuelOnly", true);
        ALT_DEBUG_PVE_ENABLED = altSettings.getProperty("AltDebugPvEEnabled", false);
        ALT_ENABLE_BOTREPORT = altSettings.getProperty("AltEnableBotReport", false);

        ALT_CLAN_RESTORE_DAY = altSettings.getProperty("AltClanRestoreDay", 7);
        ALT_MAX_ALLY_SIZE = altSettings.getProperty("AltMaxAllySize", 3);
        ALT_MAX_PARTY_SIZE = altSettings.getProperty("AltMaxPartySize", 9);
        ALT_PARTY_DISTRIBUTION_RANGE = altSettings.getProperty("AltPartyDistributionRange", 1500);
        ALT_PARTY_BONUS = altSettings.getProperty("AltPartyBonus", new double[]{1.00, 1.10, 1.20, 1.30, 1.40, 1.50, 2.00, 2.10, 2.20});

        ALT_ALL_PHYS_SKILLS_OVERHIT = altSettings.getProperty("AltAllPhysSkillsOverhit", true);
        ALT_REMOVE_SKILLS_ON_DELEVEL = altSettings.getProperty("AltRemoveSkillsOnDelevel", true);
        ALT_USE_BOW_REUSE_MODIFIER = altSettings.getProperty("AltUseBowReuseModifier", true);
        ALLOW_CH_DOOR_OPEN_ON_CLICK = altSettings.getProperty("AllowChDoorOpenOnClick", true);
        ALT_CH_ALL_BUFFS = altSettings.getProperty("AltChAllBuffs", false);
        ALT_CH_ALLOW_1H_BUFFS = altSettings.getProperty("AltChAllowHourBuff", false);
        ALT_CH_SIMPLE_DIALOG = altSettings.getProperty("AltChSimpleDialog", false);

        AUGMENTATION_NG_SKILL_CHANCE = altSettings.getProperty("AugmentationNGSkillChance", 15);
        AUGMENTATION_NG_GLOW_CHANCE = altSettings.getProperty("AugmentationNGGlowChance", 0);
        AUGMENTATION_MID_SKILL_CHANCE = altSettings.getProperty("AugmentationMidSkillChance", 30);
        AUGMENTATION_MID_GLOW_CHANCE = altSettings.getProperty("AugmentationMidGlowChance", 40);
        AUGMENTATION_HIGH_SKILL_CHANCE = altSettings.getProperty("AugmentationHighSkillChance", 45);
        AUGMENTATION_HIGH_GLOW_CHANCE = altSettings.getProperty("AugmentationHighGlowChance", 70);
        AUGMENTATION_TOP_SKILL_CHANCE = altSettings.getProperty("AugmentationTopSkillChance", 60);
        AUGMENTATION_TOP_GLOW_CHANCE = altSettings.getProperty("AugmentationTopGlowChance", 100);
        AUGMENTATION_BASESTAT_CHANCE = altSettings.getProperty("AugmentationBaseStatChance", 1);
        AUGMENTATION_ACC_SKILL_CHANCE = altSettings.getProperty("AugmentationAccSkillChance", 10);

        ALT_OPEN_CLOAK_SLOT = altSettings.getProperty("OpenCloakSlot", false);

        ALT_SHOW_SERVER_TIME = altSettings.getProperty("ShowServerTime", false);

        FOLLOW_RANGE = altSettings.getProperty("FollowRange", 100);

        ALT_ENABLE_MULTI_PROFA = altSettings.getProperty("AltEnableMultiProfa", false);
        ALT_ENABLE_MULTI_SKILL = altSettings.getProperty("AltEnableMultiSkill", false);
        ALT_ENABLE_ENCHANT_SKILL_SUB = altSettings.getProperty("AltEnableEnchantSkillSub", false);

        ALT_ITEM_AUCTION_ENABLED = altSettings.getProperty("AltItemAuctionEnabled", true);
        ALT_ITEM_AUCTION_CAN_REBID = altSettings.getProperty("AltItemAuctionCanRebid", false);
        ALT_ITEM_AUCTION_START_ANNOUNCE = altSettings.getProperty("AltItemAuctionAnnounce", true);
        ALT_ITEM_AUCTION_BID_ITEM_ID = altSettings.getProperty("AltItemAuctionBidItemId", 57);
        ALT_ITEM_AUCTION_MAX_BID = altSettings.getProperty("AltItemAuctionMaxBid", 1000000L);
        ALT_ITEM_AUCTION_MAX_CANCEL_TIME_IN_MILLIS = altSettings.getProperty("AltItemAuctionMaxCancelTimeInMillis", 604800000L);

        ALT_FISH_CHAMPIONSHIP_ENABLED = altSettings.getProperty("AltFishChampionshipEnabled", true);
        ALT_FISH_CHAMPIONSHIP_REWARD_ITEM = altSettings.getProperty("AltFishChampionshipRewardItemId", 57);
        ALT_FISH_CHAMPIONSHIP_REWARD_1 = altSettings.getProperty("AltFishChampionshipReward1", 800000);
        ALT_FISH_CHAMPIONSHIP_REWARD_2 = altSettings.getProperty("AltFishChampionshipReward2", 500000);
        ALT_FISH_CHAMPIONSHIP_REWARD_3 = altSettings.getProperty("AltFishChampionshipReward3", 300000);
        ALT_FISH_CHAMPIONSHIP_REWARD_4 = altSettings.getProperty("AltFishChampionshipReward4", 200000);
        ALT_FISH_CHAMPIONSHIP_REWARD_5 = altSettings.getProperty("AltFishChampionshipReward5", 100000);

        ALT_ENABLE_BLOCK_CHECKER_EVENT = altSettings.getProperty("EnableBlockCheckerEvent", true);
        ALT_MIN_BLOCK_CHECKER_TEAM_MEMBERS = Math.min(Math.max(altSettings.getProperty("BlockCheckerMinTeamMembers", 1), 1), 6);
        ALT_RATE_COINS_REWARD_BLOCK_CHECKER = altSettings.getProperty("BlockCheckerRateCoinReward", 1.);

        ALT_HBCE_FAIR_PLAY = altSettings.getProperty("HBCEFairPlay", false);

        ALT_PET_INVENTORY_LIMIT = altSettings.getProperty("AltPetInventoryLimit", 12);
        ALT_CLAN_LEVEL_CREATE = altSettings.getProperty("ClanLevelCreate", 0);
        CLAN_LEVEL_6_COST = altSettings.getProperty("ClanLevel6Cost", 5000);
        CLAN_LEVEL_7_COST = altSettings.getProperty("ClanLevel7Cost", 10000);
        CLAN_LEVEL_8_COST = altSettings.getProperty("ClanLevel8Cost", 20000);
        CLAN_LEVEL_9_COST = altSettings.getProperty("ClanLevel9Cost", 40000);
        CLAN_LEVEL_10_COST = altSettings.getProperty("ClanLevel10Cost", 40000);
        CLAN_LEVEL_11_COST = altSettings.getProperty("ClanLevel11Cost", 75000);
        CLAN_LEVEL_6_REQUIREMEN = altSettings.getProperty("ClanLevel6Requirement", 30);
        CLAN_LEVEL_7_REQUIREMEN = altSettings.getProperty("ClanLevel7Requirement", 50);
        CLAN_LEVEL_8_REQUIREMEN = altSettings.getProperty("ClanLevel8Requirement", 80);
        CLAN_LEVEL_9_REQUIREMEN = altSettings.getProperty("ClanLevel9Requirement", 120);
        CLAN_LEVEL_10_REQUIREMEN = altSettings.getProperty("ClanLevel10Requirement", 140);
        CLAN_LEVEL_11_REQUIREMEN = altSettings.getProperty("ClanLevel11Requirement", 170);
        MAX_CLAN_REPUTATIONS_POINTS = altSettings.getProperty("MaxClanReputationPoints", 2147483647);
        BLOOD_OATHS = altSettings.getProperty("BloodOaths", 150);
        BLOOD_PLEDGES = altSettings.getProperty("BloodPledges", 5);
        MIN_ACADEM_POINT = altSettings.getProperty("MinAcademPoint", 190);
        MAX_ACADEM_POINT = altSettings.getProperty("MaxAcademPoint", 650);

        MAX_VORTEX_BOSS_COUNT = altSettings.getProperty("MaxVortexBossCount", 0);
        TIME_DESPAWN_VORTEX_BOSS = altSettings.getProperty("TimeDespawnVortexBoss", 15);

        HELLBOUND_LEVEL = altSettings.getProperty("HellboundLevel", 0);
        HELLBOUND_ON = altSettings.getProperty("HellboundOn", false);

        ALT_GAME_CREATION = altSettings.getProperty("AllowAltGreationRate", false);
        ALT_GAME_CREATION_RARE_XPSP_RATE = altSettings.getProperty("AltGreationRateXpSp", 1.);
        ALT_GAME_CREATION_XP_RATE = altSettings.getProperty("AltGreationRateXp", 1.);
        ALT_GAME_CREATION_SP_RATE = altSettings.getProperty("AltGreationRateSp", 1.);
        SIEGE_PVP_COUNT = altSettings.getProperty("SiegePvpCount", false);
        ZONE_PVP_COUNT = altSettings.getProperty("ZonePvpCount", false);
        TRANSFORM_ON_DEATH = altSettings.getProperty("TransformOnDeath", true);
        EPIC_EXPERTISE_PENALTY = altSettings.getProperty("EpicExpertisePenalty", true);
        EXPERTISE_PENALTY = altSettings.getProperty("ExpertisePenalty", true);
        ALT_DISPEL_MUSIC = altSettings.getProperty("AltDispelDanceSong", false);
        ALT_MUSIC_LIMIT = altSettings.getProperty("MusicLimit", 12);
        ALT_DEBUFF_LIMIT = altSettings.getProperty("DebuffLimit", 8);
        ALT_TRIGGER_LIMIT = altSettings.getProperty("TriggerLimit", 12);
        ENABLE_MODIFY_SKILL_DURATION = altSettings.getProperty("EnableSkillDuration", false);
        if (ENABLE_MODIFY_SKILL_DURATION) {
            String[] propertySplit = altSettings.getProperty("SkillDurationList", "").split(";");
            SKILL_DURATION_LIST = new TIntIntHashMap(propertySplit.length);
            for (String skill : propertySplit) {
                String[] skillSplit = skill.split(",");
                if (skillSplit.length != 2) {
                    _log.warn("[SkillDurationList]: invalid config property -> SkillDurationList \"" + skill + "\"");
                } else {
                    try {
                        SKILL_DURATION_LIST.put(Integer.parseInt(skillSplit[0]), Integer.parseInt(skillSplit[1]));
                    } catch (NumberFormatException nfe) {
                        if (!skill.isEmpty()) {
                            _log.warn("[SkillDurationList]: invalid config property -> SkillList \"" + skillSplit[0] + "\"" + skillSplit[1]);
                        }
                    }
                }
            }
        }
        ALT_TIME_MODE_SKILL_DURATION = altSettings.getProperty("AltTimeModeSkillDuration", false);
    }

    public static void loadServicesSettings() {
        ExProperties servicesSettings = load(SERVICES_FILE);

        for (int id : servicesSettings.getProperty("AllowClassMasters", ArrayUtils.EMPTY_INT_ARRAY)) {
            if (id != 0) {
                ALLOW_CLASS_MASTERS_LIST.add(id);
            }
        }

        CLASS_MASTERS_PRICE = servicesSettings.getProperty("ClassMastersPrice", "0,0,0");
        if (CLASS_MASTERS_PRICE.length() >= 5) {
            int level = 1;
            for (String id : CLASS_MASTERS_PRICE.split(",")) {
                CLASS_MASTERS_PRICE_LIST[level] = Integer.parseInt(id);
                level++;
            }
        }
        CLASS_MASTERS_PRICE_ITEM = servicesSettings.getProperty("ClassMastersPriceItem", 57);

        SERVICES_CHANGE_NICK_ALLOW_SYMBOL = servicesSettings.getProperty("NickChangeAllowSimbol", false);
        SERVICES_CHANGE_NICK_ENABLED = servicesSettings.getProperty("NickChangeEnabled", false);
        SERVICES_CHANGE_NICK_PRICE = servicesSettings.getProperty("NickChangePrice", 100);
        SERVICES_CHANGE_NICK_ITEM = servicesSettings.getProperty("NickChangeItem", 4037);

        SERVICES_CHANGE_CLAN_NAME_ENABLED = servicesSettings.getProperty("ClanNameChangeEnabled", false);
        SERVICES_CHANGE_CLAN_NAME_PRICE = servicesSettings.getProperty("ClanNameChangePrice", 100);
        SERVICES_CHANGE_CLAN_NAME_ITEM = servicesSettings.getProperty("ClanNameChangeItem", 4037);

        SERVICES_CLAN_BUY_POINTS_ENABLED = servicesSettings.getProperty("ClanBuyPointsEnabled", false);
        String[] clanBuyPointsPrice = servicesSettings.getProperty("ClanBuyPointsPrice", "100-100;500-500;1000-1000;5000-4000;").split(";");
        SERVICES_CLAN_BUY_POINTS_PRICE = new int[clanBuyPointsPrice.length][2];
        for (int i = 0; i < clanBuyPointsPrice.length; i++) {
            final String[] strings = clanBuyPointsPrice[i].split("-");

            final int[] ints = new int[strings.length];
            for (int k = 0; k < strings.length; k++) {
                SERVICES_CLAN_BUY_POINTS_PRICE[i][k] = Integer.parseInt(strings[k]);
            }
        }
        SERVICES_CLAN_BUY_POINTS_ITEM = servicesSettings.getProperty("ClanBuyPointsItem", 4037);

        SERVICES_CLAN_BUY_LEVEL_ENABLED = servicesSettings.getProperty("ClanBuyLevelEnabled", false);
        SERVICES_CLAN_BUY_LEVEL_PRICE = servicesSettings.getProperty("ClanBuyLevelPrice", new int[]{10, 20, 30, 40, 100, 120, 150, 200, 270, 350, 500});
        SERVICES_CLAN_BUY_LEVEL_ITEM = servicesSettings.getProperty("ClanBuyLevelItem", 4037);

        SERVICES_CHANGE_PET_NAME_ENABLED = servicesSettings.getProperty("PetNameChangeEnabled", false);
        SERVICES_CHANGE_PET_NAME_PRICE = servicesSettings.getProperty("PetNameChangePrice", 100);
        SERVICES_CHANGE_PET_NAME_ITEM = servicesSettings.getProperty("PetNameChangeItem", 4037);

        SERVICES_EXCHANGE_BABY_PET_ENABLED = servicesSettings.getProperty("BabyPetExchangeEnabled", false);
        SERVICES_EXCHANGE_BABY_PET_PRICE = servicesSettings.getProperty("BabyPetExchangePrice", 100);
        SERVICES_EXCHANGE_BABY_PET_ITEM = servicesSettings.getProperty("BabyPetExchangeItem", 4037);

        SERVICES_PET_RIDE_ENABLED = servicesSettings.getProperty("PetRideEnabled", false);

        SERVICES_CHANGE_SEX_ENABLED = servicesSettings.getProperty("SexChangeEnabled", false);
        SERVICES_CHANGE_SEX_PRICE = servicesSettings.getProperty("SexChangePrice", 100);
        SERVICES_CHANGE_SEX_ITEM = servicesSettings.getProperty("SexChangeItem", 4037);

        SERVICES_CHANGE_BASE_ENABLED = servicesSettings.getProperty("BaseChangeEnabled", false);
        SERVICES_CHANGE_BASE_PRICE = servicesSettings.getProperty("BaseChangePrice", 100);
        SERVICES_CHANGE_BASE_ITEM = servicesSettings.getProperty("BaseChangeItem", 4037);

        SERVICES_SEPARATE_SUB_ENABLED = servicesSettings.getProperty("SeparateSubEnabled", false);
        SERVICES_SEPARATE_SUB_PRICE = servicesSettings.getProperty("SeparateSubPrice", 100);
        SERVICES_SEPARATE_SUB_ITEM = servicesSettings.getProperty("SeparateSubItem", 4037);

        SERVICES_CHANGE_NICK_COLOR_ENABLED = servicesSettings.getProperty("NickColorChangeEnabled", false);
        SERVICES_CHANGE_NICK_COLOR_PRICE = servicesSettings.getProperty("NickColorChangePrice", 100);
        SERVICES_CHANGE_NICK_COLOR_ITEM = servicesSettings.getProperty("NickColorChangeItem", 4037);
        SERVICES_CHANGE_NICK_COLOR_LIST = servicesSettings.getProperty("NickColorChangeList", new String[]{"00FF00"});

        SERVICES_CHANGE_TITLE_COLOR_ENABLED = servicesSettings.getProperty("TitleColorChangeEnabled", false);
        SERVICES_CHANGE_TITLE_COLOR_PRICE = servicesSettings.getProperty("TitleColorChangePrice", 100);
        SERVICES_CHANGE_TITLE_COLOR_ITEM = servicesSettings.getProperty("TitleColorChangeItem", 4037);
        SERVICES_CHANGE_TITLE_COLOR_LIST = servicesSettings.getProperty("TitleColorChangeList", new String[]{"00FF00"});

        SERVICES_BASH_ENABLED = servicesSettings.getProperty("BashEnabled", false);
        SERVICES_BASH_SKIP_DOWNLOAD = servicesSettings.getProperty("BashSkipDownload", false);
        SERVICES_BASH_RELOAD_TIME = servicesSettings.getProperty("BashReloadTime", 24);

        SERVICES_NOBLESS_SELL_ENABLED = servicesSettings.getProperty("NoblessSellEnabled", false);
        SERVICES_NOBLESS_SELL_PRICE = servicesSettings.getProperty("NoblessSellPrice", 1000);
        SERVICES_NOBLESS_SELL_ITEM = servicesSettings.getProperty("NoblessSellItem", 4037);

        SERVICES_HERO_SELL_ENABLED = servicesSettings.getProperty("HeroSellEnabled", false);
        SERVICES_HERO_SELL_DAY = servicesSettings.getProperty("HeroSellDay", new int[]{30});
        SERVICES_HERO_SELL_PRICE = servicesSettings.getProperty("HeroSellPrice", new int[]{30});
        SERVICES_HERO_SELL_ITEM = servicesSettings.getProperty("HeroSellItem", new int[]{4037});

        SERVICES_WASH_PK_ENABLED = servicesSettings.getProperty("WashPkEnabled", false);
        SERVICES_WASH_PK_ITEM = servicesSettings.getProperty("WashPkItem", 4037);
        SERVICES_WASH_PK_PRICE = servicesSettings.getProperty("WashPkPrice", 5);

        SERVICES_WASH_PK_CARMA_ENABLED = servicesSettings.getProperty("WashPkCarmaEnabled", false);
        SERVICES_WASH_PK_CARMA_ITEM = servicesSettings.getProperty("WashPkCarmaItem", 4037);
        SERVICES_WASH_PK_CARMA_PRICE = servicesSettings.getProperty("WashPkCarmaPrice", 5);

        SERVICES_EXPAND_INVENTORY_ENABLED = servicesSettings.getProperty("ExpandInventoryEnabled", false);
        SERVICES_EXPAND_INVENTORY_PRICE = servicesSettings.getProperty("ExpandInventoryPrice", 1000);
        SERVICES_EXPAND_INVENTORY_ITEM = servicesSettings.getProperty("ExpandInventoryItem", 4037);
        SERVICES_EXPAND_INVENTORY_MAX = servicesSettings.getProperty("ExpandInventoryMax", 250);

        SERVICES_EXPAND_WAREHOUSE_ENABLED = servicesSettings.getProperty("ExpandWarehouseEnabled", false);
        SERVICES_EXPAND_WAREHOUSE_PRICE = servicesSettings.getProperty("ExpandWarehousePrice", 1000);
        SERVICES_EXPAND_WAREHOUSE_ITEM = servicesSettings.getProperty("ExpandWarehouseItem", 4037);

        SERVICES_EXPAND_CWH_ENABLED = servicesSettings.getProperty("ExpandCWHEnabled", false);
        SERVICES_EXPAND_CWH_PRICE = servicesSettings.getProperty("ExpandCWHPrice", 1000);
        SERVICES_EXPAND_CWH_ITEM = servicesSettings.getProperty("ExpandCWHItem", 4037);

        SERVICES_SELLPETS = servicesSettings.getProperty("SellPets", "");

        SERVICES_OFFLINE_TRADE_ALLOW = servicesSettings.getProperty("AllowOfflineTrade", false);
        SERVICES_OFFLINE_TRADE_ALLOW_OFFSHORE = servicesSettings.getProperty("AllowOfflineTradeOnlyOffshore", true);
        SERVICES_OFFLINE_TRADE_MIN_LEVEL = servicesSettings.getProperty("OfflineMinLevel", 0);
        ALLOW_SERVICES_OFFLINE_TRADE_NAME_COLOR = servicesSettings.getProperty("AllowServiceOfflineTradeNameColor", false);
        SERVICES_OFFLINE_TRADE_NAME_COLOR = Integer.decode("0x" + servicesSettings.getProperty("OfflineTradeNameColor", "B0FFFF"));
        SERVICES_OFFLINE_TRADE_PRICE_ITEM = servicesSettings.getProperty("OfflineTradePriceItem", 0);
        SERVICES_OFFLINE_TRADE_PRICE = servicesSettings.getProperty("OfflineTradePrice", 0);
        SERVICES_OFFLINE_TRADE_SECONDS_TO_KICK = servicesSettings.getProperty("OfflineTradeDaysToKick", 14) * 86400L;
        SERVICES_OFFLINE_TRADE_RESTORE_AFTER_RESTART = servicesSettings.getProperty("OfflineRestoreAfterRestart", true);

        SERVICES_NO_TRADE_ONLY_OFFLINE = servicesSettings.getProperty("NoTradeOnlyOffline", false);
        SERVICES_NO_TRADE_BLOCK_ZONE = servicesSettings.getProperty("NoTradeBlockZone", false);
        SERVICES_TRADE_TAX = servicesSettings.getProperty("TradeTax", 0.0);
        SERVICES_OFFSHORE_TRADE_TAX = servicesSettings.getProperty("OffshoreTradeTax", 0.0);
        SERVICES_TRADE_TAX_ONLY_OFFLINE = servicesSettings.getProperty("TradeTaxOnlyOffline", false);
        SERVICES_OFFSHORE_NO_CASTLE_TAX = servicesSettings.getProperty("NoCastleTaxInOffshore", false);
        SERVICES_TRADE_ONLY_FAR = servicesSettings.getProperty("TradeOnlyFar", false);
        SERVICES_TRADE_MIN_LEVEL = servicesSettings.getProperty("MinLevelForTrade", 0);
        SERVICES_TRADE_RADIUS = servicesSettings.getProperty("TradeRadius", 30);

        SERVICES_GIRAN_HARBOR_ENABLED = servicesSettings.getProperty("GiranHarborZone", false);
        SERVICES_PARNASSUS_ENABLED = servicesSettings.getProperty("ParnassusZone", false);
        SERVICES_PARNASSUS_NOTAX = servicesSettings.getProperty("ParnassusNoTax", false);
        SERVICES_PARNASSUS_PRICE = servicesSettings.getProperty("ParnassusPrice", 500000);

        SERVICES_ALLOW_LOTTERY = servicesSettings.getProperty("AllowLottery", false);
        SERVICES_LOTTERY_PRIZE = servicesSettings.getProperty("LotteryPrize", 50000);
        SERVICES_ALT_LOTTERY_PRICE = servicesSettings.getProperty("AltLotteryPrice", 2000);
        SERVICES_LOTTERY_TICKET_PRICE = servicesSettings.getProperty("LotteryTicketPrice", 2000);
        SERVICES_LOTTERY_5_NUMBER_RATE = servicesSettings.getProperty("Lottery5NumberRate", 0.6);
        SERVICES_LOTTERY_4_NUMBER_RATE = servicesSettings.getProperty("Lottery4NumberRate", 0.4);
        SERVICES_LOTTERY_3_NUMBER_RATE = servicesSettings.getProperty("Lottery3NumberRate", 0.2);
        SERVICES_LOTTERY_2_AND_1_NUMBER_PRIZE = servicesSettings.getProperty("Lottery2and1NumberPrize", 200);

        SERVICES_ALLOW_ROULETTE = servicesSettings.getProperty("AllowRoulette", false);
        SERVICES_ROULETTE_MIN_BET = servicesSettings.getProperty("RouletteMinBet", 1L);
        SERVICES_ROULETTE_MAX_BET = servicesSettings.getProperty("RouletteMaxBet", Long.MAX_VALUE);

        SERVICES_ENABLE_NO_CARRIER = servicesSettings.getProperty("EnableNoCarrier", false);
        SERVICES_NO_CARRIER_MIN_TIME = servicesSettings.getProperty("NoCarrierMinTime", 0);
        SERVICES_NO_CARRIER_MAX_TIME = servicesSettings.getProperty("NoCarrierMaxTime", 90);
        SERVICES_NO_CARRIER_DEFAULT_TIME = servicesSettings.getProperty("NoCarrierDefaultTime", 60);

        SERVICES_PK_PVP_KILL_ENABLE = servicesSettings.getProperty("PkPvPKillEnable", false);
        SERVICES_PVP_KILL_REWARD_ITEM = servicesSettings.getProperty("PvPkillRewardItem", 4037);
        SERVICES_PVP_KILL_REWARD_COUNT = servicesSettings.getProperty("PvPKillRewardCount", 1L);
        SERVICES_PK_KILL_REWARD_ITEM = servicesSettings.getProperty("PkkillRewardItem", 4037);
        SERVICES_PK_KILL_REWARD_COUNT = servicesSettings.getProperty("PkKillRewardCount", 1L);
        SERVICES_PK_PVP_TIE_IF_SAME_IP = servicesSettings.getProperty("PkPvPTieifSameIP", true);
        SERVICES_PK_PVP_TIE_IF_SAME_HWID = servicesSettings.getProperty("PkPvPTieifSameHWID", true);

        ITEM_BROKER_ITEM_SEARCH = servicesSettings.getProperty("UseItemBrokerItemSearch", false);

        SERVICES_CHANGE_PASSWORD = servicesSettings.getProperty("ChangePassword", false);

        ALLOW_EVENT_GATEKEEPER = servicesSettings.getProperty("AllowEventGatekeeper", false);
        SERVICES_LVL_ENABLED = servicesSettings.getProperty("LevelChangeEnabled", false);
        SERVICES_LVL_UP_MAX = servicesSettings.getProperty("LevelUPChangeMax", 85);
        SERVICES_LVL_UP_PRICE = servicesSettings.getProperty("LevelUPChangePrice", 1000);
        SERVICES_LVL_UP_ITEM = servicesSettings.getProperty("LevelUPChangeItem", 4037);
        SERVICES_LVL_DOWN_MAX = servicesSettings.getProperty("LevelDownChangeMax", 1);
        SERVICES_LVL_DOWN_PRICE = servicesSettings.getProperty("LevelDownChangePrice", 1000);
        SERVICES_LVL_DOWN_ITEM = servicesSettings.getProperty("LevelDownChangeItem", 4037);

        ALLOW_MULTILANG_GATEKEEPER = servicesSettings.getProperty("AllowMultiLangGatekeeper", false);
        DEFAULT_GK_LANG = servicesSettings.getProperty("DefaultGKLang", "en");

        EXCH_COIN_ID = servicesSettings.getProperty("ExchCoinId", 57);
        SERVICES_PRIME_SHOP_ENABLED = servicesSettings.getProperty("PrimeShopEnabled", false);
        SERVICES_POLIMORF_ENABLED = servicesSettings.getProperty("PolimorfEnabled", false);

        SERVICES_ACC_MOVE_ENABLED = servicesSettings.getProperty("AccMoveEnabled", false);
        SERVICES_ACC_MOVE_ITEM = servicesSettings.getProperty("AccMoveItem", 57);
        SERVICES_ACC_MOVE_PRICE = servicesSettings.getProperty("AccMovePrice", 57);

        SERVICES_ACTIVATE_SUB = servicesSettings.getProperty("ActivateSubClass", false);
        SERVICES_ACTIVATE_SUB_ITEM = servicesSettings.getProperty("ActivateSubClassItem", 4037);
        SERVICES_ACTIVATE_SUB_PRICE = servicesSettings.getProperty("ActivateSubClassPrice", 100);
    }

    public static void loadPvPSettings() {
        ExProperties pvpSettings = load(PVP_CONFIG_FILE);

        /* KARMA SYSTEM */
        KARMA_MIN_KARMA = pvpSettings.getProperty("MinKarma", 240);
        KARMA_SP_DIVIDER = pvpSettings.getProperty("SPDivider", 7);
        KARMA_LOST_BASE = pvpSettings.getProperty("BaseKarmaLost", 0);

        KARMA_DROP_GM = pvpSettings.getProperty("CanGMDropEquipment", false);
        KARMA_NEEDED_TO_DROP = pvpSettings.getProperty("KarmaNeededToDrop", true);
        DROP_ITEMS_ON_DIE = pvpSettings.getProperty("DropOnDie", false);
        DROP_ITEMS_AUGMENTED = pvpSettings.getProperty("DropAugmented", false);

        KARMA_DROP_ITEM_LIMIT = pvpSettings.getProperty("MaxItemsDroppable", 10);
        MIN_PK_TO_ITEMS_DROP = pvpSettings.getProperty("MinPKToDropItems", 5);

        KARMA_RANDOM_DROP_LOCATION_LIMIT = pvpSettings.getProperty("MaxDropThrowDistance", 70);

        KARMA_DROPCHANCE_BASE = pvpSettings.getProperty("ChanceOfPKDropBase", 20.);
        KARMA_DROPCHANCE_MOD = pvpSettings.getProperty("ChanceOfPKsDropMod", 1.);
        NORMAL_DROPCHANCE_BASE = pvpSettings.getProperty("ChanceOfNormalDropBase", 1.);
        DROPCHANCE_EQUIPPED_WEAPON = pvpSettings.getProperty("ChanceOfDropWeapon", 3);
        DROPCHANCE_EQUIPMENT = pvpSettings.getProperty("ChanceOfDropEquippment", 17);
        DROPCHANCE_ITEM = pvpSettings.getProperty("ChanceOfDropOther", 80);

        KARMA_LIST_NONDROPPABLE_ITEMS = new ArrayList<Integer>();
        for (int id : pvpSettings.getProperty("ListOfNonDroppableItems", new int[]{
            57,
            1147,
            425,
            1146,
            461,
            10,
            2368,
            7,
            6,
            2370,
            2369,
            3500,
            3501,
            3502,
            4422,
            4423,
            4424,
            2375,
            6648,
            6649,
            6650,
            6842,
            6834,
            6835,
            6836,
            6837,
            6838,
            6839,
            6840,
            5575,
            7694,
            6841,
            8181})) {
            KARMA_LIST_NONDROPPABLE_ITEMS.add(id);
        }

        PVP_TIME = pvpSettings.getProperty("PvPTime", 40000);
    }

    public static void loadAISettings() {
        ExProperties aiSettings = load(AI_CONFIG_FILE);

        AI_TASK_MANAGER_COUNT = aiSettings.getProperty("AiTaskManagers", 1);
        AI_TASK_ATTACK_DELAY = aiSettings.getProperty("AiTaskDelay", 1000);
        AI_TASK_ACTIVE_DELAY = aiSettings.getProperty("AiTaskActiveDelay", 1000);
        BLOCK_ACTIVE_TASKS = aiSettings.getProperty("BlockActiveTasks", false);
        ALWAYS_TELEPORT_HOME = aiSettings.getProperty("AlwaysTeleportHome", false);

        RND_WALK = aiSettings.getProperty("RndWalk", true);
        RND_WALK_RATE = aiSettings.getProperty("RndWalkRate", 1);
        RND_ANIMATION_RATE = aiSettings.getProperty("RndAnimationRate", 2);

        MONSTER_WEAPON_ENCHANT_MIN = aiSettings.getProperty("MonstersWeaponEnchantMin", 0);
        MONSTER_WEAPON_ENCHANT_MAX = aiSettings.getProperty("MonstersWeaponEnchantMax", 0);
        MONSTER_WEAPON_ENCHANT_CHANCE = aiSettings.getProperty("MonstersWeaponEnchantChance", 0);

        AGGRO_CHECK_INTERVAL = aiSettings.getProperty("AggroCheckInterval", 250);
        NONAGGRO_TIME_ONTELEPORT = aiSettings.getProperty("NonAggroTimeOnTeleport", 15000);
        MAX_DRIFT_RANGE = aiSettings.getProperty("MaxDriftRange", 100);
        MAX_PURSUE_RANGE = aiSettings.getProperty("MaxPursueRange", 4000);
        MAX_PURSUE_UNDERGROUND_RANGE = aiSettings.getProperty("MaxPursueUndergoundRange", 2000);
        MAX_PURSUE_RANGE_RAID = aiSettings.getProperty("MaxPursueRangeRaid", 5000);
    }

    public static void loadGeodataSettings() {
        ExProperties geodataSettings = load(GEODATA_CONFIG_FILE);

        GEO_X_FIRST = geodataSettings.getProperty("GeoFirstX", 11);
        GEO_Y_FIRST = geodataSettings.getProperty("GeoFirstY", 10);
        GEO_X_LAST = geodataSettings.getProperty("GeoLastX", 26);
        GEO_Y_LAST = geodataSettings.getProperty("GeoLastY", 26);

        ALLOW_GEODATA = geodataSettings.getProperty("AllowGeodata", true);
        ALLOW_FALL_FROM_WALLS = geodataSettings.getProperty("AllowFallFromWalls", false);
        ALLOW_KEYBOARD_MOVE = geodataSettings.getProperty("AllowMoveWithKeyboard", true);
        COMPACT_GEO = geodataSettings.getProperty("CompactGeoData", false);
        CLIENT_Z_SHIFT = geodataSettings.getProperty("ClientZShift", 16);
        PATHFIND_BOOST = geodataSettings.getProperty("PathFindBoost", 2);
        PATHFIND_DIAGONAL = geodataSettings.getProperty("PathFindDiagonal", true);
        PATHFIND_MAP_MUL = geodataSettings.getProperty("PathFindMapMul", 2);
        PATH_CLEAN = geodataSettings.getProperty("PathClean", true);
        PATHFIND_MAX_Z_DIFF = geodataSettings.getProperty("PathFindMaxZDiff", 32);
        MAX_Z_DIFF = geodataSettings.getProperty("MaxZDiff", 64);
        MIN_LAYER_HEIGHT = geodataSettings.getProperty("MinLayerHeight", 64);
        REGION_EDGE_MAX_Z_DIFF = geodataSettings.getProperty("RegionEdgeMaxZDiff", 128);
        PATHFIND_MAX_TIME = geodataSettings.getProperty("PathFindMaxTime", 10000000);
        PATHFIND_BUFFERS = geodataSettings.getProperty("PathFindBuffers", "8x96;8x128;8x160;8x192;4x224;4x256;4x288;2x320;2x384;2x352;1x512");
    }

    public static void loadCustomDropSettings() {
        ExProperties eventSettings = load(EVENTS_CUSTOM_DROP_FILE);

        CDItemsAllowEnabled = eventSettings.getProperty("CDItemsAllowEnabled", true);
        CDItemsAllowMinMaxPlayerLvl = eventSettings.getProperty("CDItemsAllowMinMaxPlayerLvl", false);
        CDItemsAllowMinMaxMobLvl = eventSettings.getProperty("CDItemsAllowMinMaxMobLvl", false);
        CDItemsAllowOnlyRbDrops = eventSettings.getProperty("CDItemsAllowOnlyRbDrops", false);
        CDItemsId = eventSettings.getProperty("CDItemsId", new int[]{57});
        CDItemsCountDropMin = eventSettings.getProperty("CDItemsCountDropMin", new int[]{1});
        CDItemsCountDropMax = eventSettings.getProperty("CDItemsCountDropMax", new int[]{1});
        CustomDropItemsChance = eventSettings.getProperty("CustomDropItemsChance", new double[]{1.});
        CDItemsMinPlayerLvl = eventSettings.getProperty("CDItemsMinPlayerLvl", 20);
        CDItemsMaxPlayerLvl = eventSettings.getProperty("CDItemsMaxPlayerLvl", 85);
        CDItemsMinMobLvl = eventSettings.getProperty("CDItemsMinMobLvl", 20);
        CDItemsMaxMobLvl = eventSettings.getProperty("CDItemsMaxMobLvl", 80);
    }

    public static void loadOlympiadSettings() {
        ExProperties olympSettings = load(OLYMPIAD);

        ENABLE_OLYMPIAD = olympSettings.getProperty("EnableOlympiad", true);
        ENABLE_OLYMPIAD_SPECTATING = olympSettings.getProperty("EnableOlympiadSpectating", true);
        OLYMPIAD_BUFF_DELETED = olympSettings.getProperty("OlympiadBuffDeleted", true);
        ALT_OLY_START_TIME = olympSettings.getProperty("AltOlyStartTime", 18);
        ALT_OLY_MIN = olympSettings.getProperty("AltOlyMin", 0);
        ALT_OLY_CPERIOD = olympSettings.getProperty("AltOlyCPeriod", 21600000);
        ALT_OLY_WPERIOD = olympSettings.getProperty("AltOlyWPeriod", 604800000);
        ALT_OLY_VPERIOD = olympSettings.getProperty("AltOlyVPeriod", 43200000);
        ALT_OLY_DATE_END = olympSettings.getProperty("AltOlyDateEnd", new int[]{1});
        CLASS_GAME_MIN = olympSettings.getProperty("ClassGameMin", 5);
        NONCLASS_GAME_MIN = olympSettings.getProperty("NonClassGameMin", 9);
        TEAM_GAME_MIN = olympSettings.getProperty("TeamGameMin", 4);

        GAME_MAX_LIMIT = olympSettings.getProperty("GameMaxLimit", 70);
        GAME_CLASSES_COUNT_LIMIT = olympSettings.getProperty("GameClassesCountLimit", 30);
        GAME_NOCLASSES_COUNT_LIMIT = olympSettings.getProperty("GameNoClassesCountLimit", 60);
        GAME_TEAM_COUNT_LIMIT = olympSettings.getProperty("GameTeamCountLimit", 10);

        ALT_OLY_REG_DISPLAY = olympSettings.getProperty("AltOlyRegistrationDisplayNumber", 100);
        ALT_OLY_BATTLE_REWARD_ITEM = olympSettings.getProperty("AltOlyBattleRewItem", 13722);
        ALT_OLY_CLASSED_RITEM_C = olympSettings.getProperty("AltOlyClassedRewItemCount", 50);
        ALT_OLY_NONCLASSED_RITEM_C = olympSettings.getProperty("AltOlyNonClassedRewItemCount", 40);
        ALT_OLY_TEAM_RITEM_C = olympSettings.getProperty("AltOlyTeamRewItemCount", 50);
        ALT_OLY_COMP_RITEM = olympSettings.getProperty("AltOlyCompRewItem", 13722);
        ALT_OLY_GP_PER_POINT = olympSettings.getProperty("AltOlyGPPerPoint", 1000);
        ALT_OLY_HERO_POINTS = olympSettings.getProperty("AltOlyHeroPoints", 180);
        ALT_OLY_RANK1_POINTS = olympSettings.getProperty("AltOlyRank1Points", 120);
        ALT_OLY_RANK2_POINTS = olympSettings.getProperty("AltOlyRank2Points", 80);
        ALT_OLY_RANK3_POINTS = olympSettings.getProperty("AltOlyRank3Points", 55);
        ALT_OLY_RANK4_POINTS = olympSettings.getProperty("AltOlyRank4Points", 35);
        ALT_OLY_RANK5_POINTS = olympSettings.getProperty("AltOlyRank5Points", 20);
        OLYMPIAD_STADIAS_COUNT = olympSettings.getProperty("OlympiadStadiasCount", 160);
        OLYMPIAD_BEGIN_TIME = olympSettings.getProperty("OlympiadBeginTime", 120);
        OLYMPIAD_BATTLES_FOR_REWARD = olympSettings.getProperty("OlympiadBattlesForReward", 15);
        OLYMPIAD_POINTS_DEFAULT = olympSettings.getProperty("OlympiadPointsDefault", 50);
        OLYMPIAD_POINTS_WEEKLY = olympSettings.getProperty("OlympiadPointsWeekly", 10);
        OLYMPIAD_OLDSTYLE_STAT = olympSettings.getProperty("OlympiadOldStyleStat", false);
        OLYMPIAD_PLAYER_IP = olympSettings.getProperty("OlympiadPlayerIp", false);
        OLYMPIAD_PLAYER_HWID = olympSettings.getProperty("OlympiadPlayerHWID", false);
        OLYMPIAD_BAD_ENCHANT_ITEMS_ALLOW = olympSettings.getProperty("OlympiadUnEquipBadEnchantItem", false);

        OLY_ENCH_LIMIT_ENABLE = olympSettings.getProperty("OlyEnchantLimit", false);
        OLY_ENCHANT_LIMIT_WEAPON = olympSettings.getProperty("OlyEnchantLimitWeapon", 0);
        OLY_ENCHANT_LIMIT_ARMOR = olympSettings.getProperty("OlyEnchantLimitArmor", 0);
        OLY_ENCHANT_LIMIT_JEWEL = olympSettings.getProperty("OlyEnchantLimitJewel", 0);
    }

    public static void loadPremiumConfig() {
        ExProperties premiumConf = load(PREMIUM_FILE);

        SERVICES_RATE_TYPE = premiumConf.getProperty("RateBonusType", Bonus.NO_BONUS);
        SERVICES_RATE_CREATE_PA = premiumConf.getProperty("RateBonusCreateChar", 0);
    }

    public static void load() {
        loadServerConfig();
        loadTelnetConfig();
        loadResidenceConfig();
        loadOtherConfig();
        loadSpoilConfig();
        loadFormulasConfig();
        loadPaymentSettings();
        loadSkillSettings();
        loadQuestRateSettings();
        loadAltSettings();
        loadServicesSettings();
        loadPvPSettings();
        loadAISettings();
        loadGeodataSettings();
        loadCustomDropSettings();
        loadAprilFoolsSettings();
        loadlakfiConfig();
        loadBountyHuntersSettings();
        loadCaptureTheFlagSettings();
        loadTeamVSTeamSettings();
        loadChestEvilSettings();
        loadChangeOfHeartSettings();
        loadCofferOfShadowsSettings();
        loadFightClubSettings();
        loadGlitteringMedalSettings();
        loadTreasuresOfTheHeraldSettings();
        loadL2DaySettings();
        loadLastHeroSettings();
        loadLuckNpcSettings();
        loadGVGSettings();
        loadHitmanSettings();
        loadChampionSettings();
        loadMarch8Settings();
        loadMasterOfEnchaningSettings();
        loadPcBangSettings();
        loadSavingSnowmanSettings();
        loadDeathMatchSettings();
        loadTheFallHarvestSettings();
        loadDefenseTownSettings();
        loadTrickOfTransmutationSettings();
        loadL2CoinsSettings();
        loadUndergroundColiseumSettings();
        loadTvTArenaSettings();
        loadOlympiadSettings();
        loadDevelopSettings();
        loadLicenseSettings();
        loadExtSettings();
        loadTopSettings();
        loadRatesConfig();
        loadPhantomsConfig();
        loadFightClubSettings();
        loadDeathMatchSettings();
        loadItemsUseConfig();
        loadChatConfig();
        loadNpcConfig();
        loadBossConfig();
        loadEpicBossConfig();
        loadCommunityBoardConfig();
        loadWeddingConfig();
        loadInstancesConfig();
        loadItemsSettings();
        loadBBSBufferConfig();

        PremiumConfig.load();
        VisualConfig.load();
        if (SERVICES_POLIMORF_ENABLED) {
            PcNpcConfig.load();
        }

        abuseLoad();
        loadGMAccess();
        loadPremiumConfig();
        if (ADVIPSYSTEM) {
            ipsLoad();
        }
    }

    private Config() {
    }

    public static void abuseLoad() {
        List<Pattern> tmp = new ArrayList<Pattern>();

        LineNumberReader lnr = null;
        try {
            String line;

            lnr = new LineNumberReader(new InputStreamReader(new FileInputStream(ANUSEWORDS_CONFIG_FILE), "UTF-8"));

            while ((line = lnr.readLine()) != null) {
                StringTokenizer st = new StringTokenizer(line, "\n\r");
                if (st.hasMoreTokens()) {
                    tmp.add(Pattern.compile(".*" + st.nextToken() + ".*", Pattern.DOTALL | Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE));
                }
            }

            ABUSEWORD_LIST = tmp.toArray(new Pattern[tmp.size()]);
            tmp.clear();
            _log.info("Abuse: Loaded " + ABUSEWORD_LIST.length + " abuse words.");
        } catch (IOException e1) {
            _log.warn("Error reading abuse: " + e1);
        } finally {
            try {
                if (lnr != null) {
                    lnr.close();
                }
            } catch (Exception e2) {
                // nothing
            }
        }
    }

    public static void loadGMAccess() {
        gmlist.clear();
        loadGMAccess(new File(GM_PERSONAL_ACCESS_FILE));
        File dir = new File(GM_ACCESS_FILES_DIR);
        if (!dir.exists() || !dir.isDirectory()) {
            _log.info("Dir " + dir.getAbsolutePath() + " not exists.");
            return;
        }
        for (File f : dir.listFiles()) // hidden файлы НЕ игнорируем
        {
            if (!f.isDirectory() && f.getName().endsWith(".xml")) {
                loadGMAccess(f);
            }
        }
    }

    public static void loadGMAccess(File file) {
        try {
            Field fld;
            //File file = new File(filename);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setValidating(false);
            factory.setIgnoringComments(true);
            Document doc = factory.newDocumentBuilder().parse(file);

            for (Node z = doc.getFirstChild(); z != null; z = z.getNextSibling()) {
                for (Node n = z.getFirstChild(); n != null; n = n.getNextSibling()) {
                    if (!n.getNodeName().equalsIgnoreCase("char")) {
                        continue;
                    }

                    PlayerAccess pa = new PlayerAccess();
                    for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling()) {
                        Class<?> cls = pa.getClass();
                        String node = d.getNodeName();

                        if (node.equalsIgnoreCase("#text")) {
                            continue;
                        }
                        try {
                            fld = cls.getField(node);
                        } catch (NoSuchFieldException e) {
                            _log.info("Not found desclarate ACCESS name: " + node + " in XML Player access Object");
                            continue;
                        }

                        if (fld.getType().getName().equalsIgnoreCase("boolean")) {
                            fld.setBoolean(pa, Boolean.parseBoolean(d.getAttributes().getNamedItem("set").getNodeValue()));
                        } else if (fld.getType().getName().equalsIgnoreCase("int")) {
                            fld.setInt(pa, Integer.valueOf(d.getAttributes().getNamedItem("set").getNodeValue()));
                        }
                    }
                    gmlist.put(pa.PlayerID, pa);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String
            getField(String fieldName) {
        Field field = FieldUtils.getField(Config.class, fieldName);

        if (field
                == null) {
            return null;
        }

        try {
            return String.valueOf(field.get(null));
        } catch (IllegalArgumentException e) {
        } catch (IllegalAccessException e) {
        }

        return null;
    }

    public static
            boolean setField(String fieldName, String value) {
        Field field = FieldUtils.getField(Config.class, fieldName);

        if (field
                == null) {
            return false;
        }

        try {
            if (field.getType() == boolean.class) {
                field.setBoolean(null, BooleanUtils.toBoolean(value));
            } else if (field.getType() == int.class) {
                field.setInt(null, NumberUtils.toInt(value));
            } else if (field.getType() == long.class) {
                field.setLong(null, NumberUtils.toLong(value));
            } else if (field.getType() == double.class) {
                field.setDouble(null, NumberUtils.toDouble(value));
            } else if (field.getType() == String.class) {
                field.set(null, value);
            } else {
                return false;
            }
        } catch (IllegalArgumentException e) {
            return false;
        } catch (IllegalAccessException e) {
            return false;
        }

        return true;
    }

    public static ExProperties load(String filename) {
        return load(new File(filename));
    }

    public static ExProperties load(File file) {
        ExProperties result = new ExProperties();

        try {
            result.load(file);
        } catch (IOException e) {
            if (!file.getName().contains("License.ini")) {
                _log.error("Error loading config : " + file.getName() + "!");
            }
        }

        return result;
    }

    public static boolean containsAbuseWord(String s) {
        for (Pattern pattern : ABUSEWORD_LIST) {
            if (pattern.matcher(s).matches()) {
                return true;
            }
        }
        return false;
    }

    private static void ipsLoad() {
        ExProperties ipsSettings = load(ADV_IP_FILE);

        String NetMask;
        String ip;
        for (int i = 0; i < ipsSettings.size() / 2; i++) {
            NetMask = ipsSettings.getProperty("NetMask" + (i + 1));
            ip = ipsSettings.getProperty("IPAdress" + (i + 1));
            for (String mask : NetMask.split(",")) {
                AdvIP advip = new AdvIP();
                advip.ipadress = ip;
                advip.ipmask = mask.split("/")[0];
                advip.bitmask = mask.split("/")[1];
                GAMEIPS.add(advip);
            }
        }
    }
}

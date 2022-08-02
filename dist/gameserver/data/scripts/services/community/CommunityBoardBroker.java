/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package services.community;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.StringTokenizer;
import l2p.gameserver.Config;
import l2p.gameserver.data.htm.HtmCache;
import l2p.gameserver.data.xml.holder.ItemHolder;
import l2p.gameserver.handler.bbs.CommunityBoardManager;
import l2p.gameserver.handler.bbs.ICommunityBoardHandler;
import l2p.gameserver.instancemanager.ExchangeBroker;
import l2p.gameserver.instancemanager.ExchangeBroker.ExchangeItem;
import l2p.gameserver.model.Player;
import l2p.gameserver.model.World;
import l2p.gameserver.model.base.Element;
import l2p.gameserver.model.base.TeamType;
import l2p.gameserver.model.items.ItemInstance;
import l2p.gameserver.scripts.ScriptFile;
import l2p.gameserver.serverpackets.ShowBoard;
import l2p.gameserver.serverpackets.SystemMessage2;
import l2p.gameserver.serverpackets.components.HtmlMessage;
import l2p.gameserver.serverpackets.components.SystemMsg;
import l2p.gameserver.templates.item.ItemTemplate;
import l2p.gameserver.utils.ItemFunctions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author deprecat
 */
public class CommunityBoardBroker implements ScriptFile, ICommunityBoardHandler {

    private static final int MAX_ITEMS_PER_PAGE = 14;
    private static final int MAX_PAGES_PER_LIST = 9;
    private static final Logger _log = LoggerFactory.getLogger(CommunityBoardBroker.class);
    public int[] RARE_ITEMS = {
        16255,
        16256,
        16257,
        16258,
        16259,
        16260,
        16261,
        16262,
        16263,
        16264,
        16265,
        16266,
        16267,
        16268,
        16269,
        16270,
        16271,
        16272,
        16273,
        16274,
        16275,
        16276,
        16277,
        16278,
        16279,
        16280,
        16281,
        16282,
        16283,
        16284,
        16285,
        16286,
        16287,
        16288,
        16357,
        16358,
        16359,
        16360,
        16361,
        16362,
        10119,
        10120,
        10121,
        11349,
        11350,
        11351,
        11352,
        11353,
        11354,
        11355,
        11356,
        11357,
        11358,
        11359,
        11360,
        11361,
        11363,
        11364,
        11365,
        11366,
        11367,
        11368,
        11369,
        11370,
        11371,
        11372,
        11373,
        11375,
        11376,
        11377,
        11378,
        11379,
        11380,
        11381,
        11382,
        11383,
        11384,
        11386,
        11387,
        11388,
        11389,
        11390,
        11391,
        11392,
        11393,
        11394,
        11395,
        11396,
        11397,
        11398,
        11399,
        11400,
        11401,
        11402,
        11403,
        11404,
        11405,
        11406,
        11407,
        11408,
        11409,
        11410,
        11411,
        11412,
        11413,
        11414,
        11415,
        11417,
        11418,
        11419,
        11420,
        11421,
        11422,
        11423,
        11424,
        11426,
        11427,
        11428,
        11429,
        11430,
        11431,
        11432,
        11433,
        11434,
        11435,
        11436,
        11437,
        11438,
        11439,
        11440,
        11441,
        11442,
        11443,
        11444,
        11445,
        11446,
        11447,
        11448,
        11449,
        11450,
        11451,
        11452,
        11453,
        11454,
        11455,
        11456,
        11457,
        11458,
        11459,
        11460,
        11461,
        11462,
        11463,
        11464,
        11465,
        11466,
        11467,
        11468,
        11470,
        11471,
        11472,
        11473,
        11474,
        11475,
        11476,
        11477,
        11478,
        11479,
        11481,
        11482,
        11483,
        11484,
        11485,
        11486,
        11487,
        11488,
        11489,
        11490,
        11491,
        11492,
        11493,
        11494,
        11495,
        11496,
        11497,
        11498,
        11499,
        11500,
        11501,
        11503,
        11504,
        11505,
        11506,
        11507,
        11509,
        11510,
        11511,
        11512,
        11513,
        11514,
        11515,
        11516,
        11517,
        11518,
        11519,
        11520,
        11521,
        11522,
        11523,
        11524,
        11525,
        11526,
        11527,
        11528,
        11529,
        11530,
        11531,
        11533,
        11534,
        11535,
        11536,
        11537,
        11538,
        11539,
        11540,
        11541,
        11542,
        11543,
        11544,
        11545,
        11546,
        11547,
        11548,
        11549,
        11550,
        11551,
        11552,
        11553,
        11554,
        11555,
        11556,
        11557,
        11558,
        11559,
        11560,
        11561,
        11562,
        11563,
        11564,
        11565,
        11566,
        11567,
        11568,
        11570,
        11571,
        11572,
        11573,
        11574,
        11575,
        11576,
        11577,
        11578,
        11579,
        11580,
        11581,
        11582,
        11583,
        11584,
        11585,
        11586,
        11587,
        11588,
        11589,
        11590,
        11591,
        11592,
        11593,
        11594,
        11595,
        11596,
        11597,
        11598,
        11599,
        11600,
        11601,
        11602,
        11603,
        11604,
        12978,
        12979,
        12980,
        12981,
        12982,
        12983,
        12984,
        12985,
        12986,
        12987,
        12988,
        12989,
        12990,
        12991,
        12992,
        12993,
        12994,
        12995,
        12996,
        12997,
        12998,
        12999,
        13000,
        13001,
        13078,
        16289,
        16290,
        16291,
        16292,
        16293,
        16294,
        16295,
        16296,
        16297,
        16298,
        16299,
        16300,
        16301,
        16302,
        16303,
        16305,
        16306,
        16307,
        16308,
        16309,
        16310,
        16311,
        16312,
        16313,
        16314,
        16315,
        16316,
        16317,
        16318,
        16319,
        16320,
        16322,
        16323,
        16324,
        16325,
        16326,
        16327,
        16328,
        16329,
        16330,
        16331,
        16332,
        16333,
        16334,
        16335,
        16336,
        16337,
        16339,
        16340,
        16341,
        16342,
        16343,
        16344,
        16345,
        16346,
        16347,
        16348,
        16349,
        16350,
        16351,
        16352,
        16353,
        16354,
        16356,
        16369,
        16370,
        16371,
        16372,
        16373,
        16374,
        16375,
        16376,
        16377,
        16378,
        16379,
        16380,
        16837,
        16838,
        16839,
        16840,
        16841,
        16842,
        16843,
        16844,
        16845,
        16846,
        16847,
        16848,
        16849,
        16850,
        16851,
        10870,
        10871,
        10872,
        10873,
        10874,
        10875,
        10876,
        10877,
        10878,
        10879,
        10880,
        10881,
        10882,
        10883,
        10884,
        10885,
        10886,
        10887,
        10888,
        10889,
        10890,
        10891,
        10892,
        10893,
        10894,
        10895,
        10896,
        10897,
        10898,
        10899,
        10900,
        10901,
        10902,
        10903,
        10904,
        10905,
        10906,
        10907,
        10908,
        10909,
        10910,
        10911,
        10912,
        10913,
        10914,
        10915,
        10916,
        10917,
        10918,
        10919,
        10920,
        10921,
        10922,
        10923,
        10924,
        10925,
        10926,
        10927,
        10928,
        10929,
        10930,
        10931,
        10932,
        10933,
        10934,
        10935,
        10936,
        10937,
        10938,
        10939,
        10940,
        10941,
        10942,
        10943,
        10944,
        10945,
        10946,
        10947,
        10948,
        10949,
        10950,
        10951,
        10952,
        10953,
        10954,
        10955,
        10956,
        10957,
        10958,
        10959,
        10960,
        10961,
        10962,
        10963,
        10964,
        10965,
        10966,
        10967,
        10968,
        10969,
        10970,
        10971,
        10972,
        10973,
        10974,
        10975,
        10976,
        10977,
        10978,
        10979,
        10980,
        10981,
        10982,
        10983,
        10984,
        10985,
        10986,
        10987,
        10988,
        10989,
        10990,
        10991,
        10992,
        10993,
        10994,
        10995,
        10996,
        10997,
        10998,
        10999,
        11000,
        11001,
        11002,
        11003,
        11004,
        11005,
        11006,
        11007,
        11008,
        11009,
        11010,
        11011,
        11012,
        11013,
        11014,
        11015,
        11016,
        11017,
        11018,
        11019,
        11020,
        11021,
        11022,
        11023,
        11024,
        11025,
        11026,
        11027,
        11028,
        11029,
        11030,
        11031,
        11032,
        11033,
        11034,
        11035,
        11036,
        11037,
        11038,
        11039,
        11040,
        11041,
        11042,
        11043,
        11044,
        11045,
        11046,
        11047,
        11048,
        11049,
        11050,
        11051,
        11052,
        11053,
        11054,
        11055,
        11056,
        11057,
        11058,
        11059,
        11060,
        11061,
        11062,
        11063,
        11064,
        11065,
        11066,
        11067,
        11068,
        11069,
        11070,
        11071,
        11072,
        11073,
        11074,
        11075,
        11076,
        11077,
        11078,
        11079,
        11080,
        11081,
        11082,
        11083,
        11084,
        11085,
        11086,
        11087,
        11088,
        11089,
        11090,
        11091,
        11092,
        11093,
        11094,
        11095,
        11096,
        11097,
        11098,
        11099,
        11100,
        11101,
        11102,
        11103,
        11104,
        11105,
        11106,
        11107,
        11108,
        11109,
        11110,
        11111,
        11112,
        11113,
        11114,
        11115,
        11116,
        11117,
        11118,
        11119,
        11120,
        11121,
        11122,
        11123,
        11124,
        11125,
        11126,
        11127,
        11128,
        11129,
        11130,
        11131,
        11132,
        11133,
        11134,
        11135,
        11136,
        11137,
        11138,
        11139,
        11140,
        11141,
        11142,
        11143,
        11144,
        11145,
        11146,
        11147,
        11148,
        11149,
        11150,
        11151,
        11152,
        11153,
        11154,
        11155,
        11156,
        11157,
        11158,
        11159,
        11160,
        11161,
        11162,
        11163,
        11164,
        11165,
        11166,
        11167,
        11168,
        11169,
        11170,
        11171,
        11172,
        11173,
        11174,
        11175,
        11176,
        11177,
        11178,
        11179,
        11180,
        11181,
        11182,
        11183,
        11184,
        11185,
        11186,
        11187,
        11188,
        11189,
        11190,
        11191,
        11192,
        11193,
        11194,
        11195,
        11196,
        11197,
        11198,
        11199,
        11200,
        11201,
        11202,
        11203,
        11204,
        11205,
        11206,
        11207,
        11208,
        11209,
        11210,
        11211,
        11212,
        11213,
        11214,
        11215,
        11216,
        11217,
        11218,
        11219,
        11220,
        11221,
        11222,
        11223,
        11224,
        11225,
        11226,
        11227,
        11228,
        11229,
        11230,
        11231,
        11232,
        11233,
        11234,
        11235,
        11236,
        11237,
        11238,
        11239,
        11240,
        11241,
        11242,
        11243,
        11244,
        11245,
        11246,
        11247,
        11248,
        11249,
        11250,
        11251,
        11252,
        11253,
        11254,
        11255,
        11256,
        11257,
        11258,
        11259,
        11260,
        11261,
        11262,
        11263,
        11264,
        11265,
        11266,
        11267,
        11268,
        11269,
        11270,
        11271,
        11272,
        11273,
        11274,
        11275,
        11276,
        11277,
        11278,
        11279,
        11280,
        11281,
        11282,
        11283,
        11284,
        11285,
        11286,
        11287,
        11288,
        11289,
        11290,
        11291,
        11292,
        11293,
        11294,
        11295,
        11296,
        11297,
        11298,
        11299,
        11300,
        11301,
        11302,
        11303,
        11304,
        11305,
        11306,
        11307,
        11308,
        11309,
        11310,
        11311,
        11312,
        11313,
        11314,
        11315,
        11316,
        11317,
        11318,
        11319,
        11320,
        11321,
        11322,
        11323,
        11324,
        11325,
        11326,
        11327,
        11328,
        11329,
        11330,
        11331,
        11332,
        11333,
        11334,
        11335,
        11336,
        11337,
        11338,
        11339,
        11340,
        11341,
        11342,
        11343,
        11344,
        11345,
        11346,
        11347,
        11348,
        11362,
        11374,
        11385,
        11416,
        11425,
        11469,
        11480,
        11502,
        11508,
        11532,
        11569,
        12852,
        12853,
        12854,
        12855,
        12856,
        12857,
        12858,
        12859,
        12860,
        12861,
        12862,
        12863,
        12864,
        12865,
        12866,
        12867,
        12868,
        12869,
        12870,
        12871,
        12872,
        12873,
        12874,
        12875,
        12876,
        12877,
        12878,
        12879,
        12880,
        12881,
        12882,
        12883,
        12884,
        12885,
        12886,
        12887,
        12888,
        12889,
        12890,
        12891,
        12892,
        12893,
        12894,
        12895,
        12896,
        12897,
        12898,
        12899,
        12900,
        12901,
        12902,
        12903,
        12904,
        12905,
        12906,
        12907,
        12908,
        12909,
        12910,
        12911,
        12912,
        12913,
        12914,
        12915,
        12916,
        12917,
        12918,
        12919,
        12920,
        12921,
        12922,
        12923,
        12924,
        12925,
        12926,
        12927,
        12928,
        12929,
        12930,
        12931,
        12932,
        12933,
        12934,
        12935,
        12936,
        12937,
        12938,
        12939,
        12940,
        12941,
        12942,
        12943,
        12944,
        12945,
        12946,
        12947,
        12948,
        12949,
        12950,
        12951,
        12952,
        12953,
        12954,
        12955,
        12956,
        12957,
        12958,
        12959,
        12960,
        12961,
        12962,
        12963,
        12964,
        12965,
        12966,
        12967,
        12968,
        12969,
        12970,
        12971,
        12972,
        12973,
        12974,
        12975,
        12976,
        12977,
        14412,
        14413,
        14414,
        14415,
        14416,
        14417,
        14418,
        14419,
        14420,
        14421,
        14422,
        14423,
        14424,
        14425,
        14426,
        14427,
        14428,
        14429,
        14430,
        14431,
        14432,
        14433,
        14434,
        14435,
        14436,
        14437,
        14438,
        14439,
        14440,
        14441,
        14442,
        14443,
        14444,
        14445,
        14446,
        14447,
        14448,
        14449,
        14450,
        14451,
        14452,
        14453,
        14454,
        14455,
        14456,
        14457,
        14458,
        14459,
        14460,
        14526,
        14527,
        14528,
        14529,
        14560,
        14561,
        14562,
        14563,
        14564,
        14565,
        14566,
        14567,
        14568,
        14569,
        14570,
        14571,
        14572,
        14573,
        14574,
        14575,
        14576,
        14577,
        14578,
        14579,
        14580,
        14581,
        16042,
        16043,
        16044,
        16045,
        16046,
        16047,
        16048,
        16049,
        16050,
        16051,
        16052,
        16053,
        16054,
        16055,
        16056,
        16057,
        16058,
        16059,
        16060,
        16061,
        16062,
        16063,
        16064,
        16065,
        16066,
        16067,
        16068,
        16069,
        16070,
        16071,
        16072,
        16073,
        16074,
        16075,
        16076,
        16077,
        16078,
        16079,
        16080,
        16081,
        16082,
        16083,
        16084,
        16085,
        16086,
        16087,
        16088,
        16089,
        16090,
        16091,
        16092,
        16093,
        16094,
        16095,
        16096,
        16097,
        16134,
        16135,
        16136,
        16137,
        16138,
        16139,
        16140,
        16141,
        16142,
        16143,
        16144,
        16145,
        16146,
        16147,
        16148,
        16149,
        16150,
        16151,
        16179,
        16180,
        16181,
        16182,
        16183,
        16184,
        16185,
        16186,
        16187,
        16188,
        16189,
        16190,
        16191,
        16192,
        16193,
        16194,
        16195,
        16196,
        16197,
        16198,
        16199,
        16200,
        16201,
        16202,
        16203,
        16204,
        16205,
        16206,
        16207,
        16208,
        16209,
        16210,
        16211,
        16212,
        16213,
        16214,
        16215,
        16216,
        16217,
        16218,
        16219,
        16220,
        16304,
        16321,
        16338,
        16355};

    @Override
    public void onLoad() {
        if (Config.COMMUNITYBOARD_ENABLED) {
            _log.info("CommunityExchange: service loaded.");
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

    @Override
    public String[] getBypassCommands() {
        return new String[]{"_bbsbrokerAllList", "_bbsbrokerMyList", "_bbsbrokerMyItem", "_bbsbrokerBuy", "_bbsbrokerSell", "_bbsbrokerRetrieve", "_bbsbrokerBuyItem", "_bbsbrokerSellItem", "_bbsbrokerRetrieveItem"};
    }

    @Override
    public void onBypassCommand(Player player, String bypass) {
        if (!CheckCondition(player)) {
            return;
        }
        if (bypass.startsWith("_bbsbrokerAllList")) {
            int currentPage = 1;
            int enchantType = 0;
            int sortType = 0;

            StringTokenizer st2 = new StringTokenizer(bypass, ";");
            String[] mBypass = st2.nextToken().split(" ");

            try {
                currentPage = Integer.parseInt(mBypass[1]);
                enchantType = Integer.parseInt(mBypass[2]);
                sortType = Integer.parseInt(mBypass[3]);
            } catch (Exception e) {
            }

            List<ExchangeItem> items = new ArrayList<ExchangeItem>(ExchangeBroker.getExchangeItemList().size() * 10);
            for (ExchangeItem tempItems : ExchangeBroker.getExchangeItemList().values()) {
                items.add(tempItems);
            }

            StringBuilder out = new StringBuilder(200);

            out.append("<table width=450>");
            out.append("<tr><td width=50 align=\"left\">Поиск:</td><td valign=\"top\" align=\"left\">");
            out.append("<edit var=\"find\" width=180></td>");
            out.append("<td width=50 align=\"left\">");
            out.append("<button value=\"Поиск\" action=\"bypass _bbsbroker_find_$find\" width=50 height=26 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"/>");
            out.append("</td>");
            out.append("<td width=10 align=\"left\">|</td>");

            out.append("<td align=\"left\">");
            out.append("<a action=\"bypass _bbsbrokerAllList 1 0 0;");
            out.append("\">");
            out.append("««");
            out.append("</a>");
            out.append("</td>");

            int totalPages = ExchangeBroker.getExchangeItemList().size();
            totalPages = totalPages / MAX_ITEMS_PER_PAGE + (totalPages % MAX_ITEMS_PER_PAGE > 0 ? 1 : 0);
            totalPages = Math.max(1, totalPages);
            currentPage = Math.min(totalPages, Math.max(1, currentPage));

            if (totalPages > 1) {
                int page = Math.max(1, Math.min(totalPages - MAX_PAGES_PER_LIST + 1, currentPage - MAX_PAGES_PER_LIST / 2));

                // линк на первую страницу
                if (page > 1) {
                    listPageNum(out, "_bbsbrokerAllList", 1, enchantType, sortType, "1");
                }
                // линк на страницу - 10
                if (currentPage > 11) {
                    listPageNum(out, "_bbsbrokerAllList", currentPage - 10, enchantType, sortType, String.valueOf(currentPage - 10));
                }
                // линк на предыдущую страницу
                if (currentPage > 1) {
                    listPageNum(out, "_bbsbrokerAllList", currentPage - 1, enchantType, sortType, "«");
                }

                for (int count = 0; count < MAX_PAGES_PER_LIST && page <= totalPages; count++, page++) {
                    if (page == currentPage) {
                        out.append("<td align=\"left\">").append(page).append("&nbsp;").append("</td>");
                    } else {
                        listPageNum(out, "_bbsbrokerAllList", page, enchantType, sortType, String.valueOf(page));
                    }
                }

                // линк на следующую страницу
                if (currentPage < totalPages) {
                    listPageNum(out, "_bbsbrokerAllList", currentPage + 1, enchantType, sortType, "»");
                }
                // линк на страницу + 10
                if (currentPage < totalPages - 10) {
                    listPageNum(out, "_bbsbrokerAllList", currentPage + 10, enchantType, sortType, String.valueOf(currentPage + 10));
                }
                // линк на последнюю страницу
                if (page <= totalPages) {
                    listPageNum(out, "_bbsbrokerAllList", totalPages, enchantType, sortType, String.valueOf(totalPages));
                }
            }
            out.append("</tr>");
            out.append("</table>");

            out.append("<table width=400>");
            out.append("<tr><td align=\"left\">Сортировать по:</td>");
            out.append("<td align=\"left\"><a action=\"bypass _bbsbrokerSortByEnchant;\"><font color=\"F25202\">заточке</font></a></td>");
            out.append("<td align=\"left\"><a action=\"bypass _bbsbrokerSortByPrice;\"><font color=\"FFFFFF\">цене</font></a></td>");
            out.append("<td align=\"left\"><a action=\"bypass _bbsbrokerSortByDoown;\"><font color=\"F25202\">убыванию</font></a></td>");
            out.append("<td align=\"left\"><a action=\"bypass _bbsbrokerSortByUp;\"><font color=\"FFFFFF\">возрастанию</font></a></td>");
            out.append("</tr>");
            out.append("</table>");

            int iter = 0;
            StringBuilder html = new StringBuilder();
            html.append("<table width=600>");

            if (items.size() > 0) {
                int count = 0;

                ListIterator<ExchangeItem> entries = items.listIterator((currentPage - 1) * MAX_ITEMS_PER_PAGE);

                while (entries.hasNext() && count < MAX_ITEMS_PER_PAGE) {
                    ExchangeItem currExchItem = entries.next();

                    iter++;
                    ItemTemplate item = ItemHolder.getInstance().getTemplate(currExchItem.getItemId());
                    if ((iter % 2) != 0) {
                        html.append("<tr>");
                    }
                    html.append("<td align=\"left\">");
                    html.append("<img src=icon.").append(item.getIcon()).append(" width=32 height=32 align=\"left\">");
                    html.append("</td>");
                    html.append("<td align=\"left\">");
                    if (currExchItem.getEnchantLevel() != 0) {
                        html.append("<a action=\"bypass _bbsbrokerBuy:").append(currExchItem.getObjectId()).append(";\"><font color=\"").append(getColorBiItemId(currExchItem.getItemId())).append("\">").append(item.getName()).append("</font></a><font color=\"FFFFFF\"> (+").append(currExchItem.getEnchantLevel()).append(") ").append(currExchItem.getCount()).append(" шт</font>");
                    } else {
                        html.append("<a action=\"bypass _bbsbrokerBuy:").append(currExchItem.getObjectId()).append(";\"><font color=\"").append(getColorBiItemId(currExchItem.getItemId())).append("\">").append(item.getName()).append("</font></a><font color=\"FFFFFF\"> ").append(currExchItem.getCount()).append(" шт</font>");
                    }
                    html.append("<br1>");
                    html.append(currExchItem.getAttFire() > 0 ? "<font color=\"00FF00\">+" + currExchItem.getAttFire() + " Fire </font>" : "");
                    html.append(currExchItem.getAttEath() > 0 ? "<font color=\"00FF00\">+" + currExchItem.getAttEath() + " Eath </font>" : "");
                    html.append(currExchItem.getAttWater() > 0 ? "<font color=\"00FF00\">+" + currExchItem.getAttWater() + " Water </font>" : "");
                    html.append(currExchItem.getAttWind() > 0 ? "<font color=\"00FF00\">+" + currExchItem.getAttWind() + " Wind </font>" : "");
                    html.append(currExchItem.getAttHoly() > 0 ? "<font color=\"00FF00\">+" + currExchItem.getAttHoly() + " Holy </font>" : "");
                    html.append(currExchItem.getAttUnholy() > 0 ? "<font color=\"00FF00\">+" + currExchItem.getAttUnholy() + " UnHoly </font>" : "");

                    html.append("<br1>");
                    html.append("<font color=\"62C2F2\">Продается</font><font color=\"FFFFFF\"> за </font><font color=\"D6D52F\">").append(currExchItem.getPrice()).append("</font><font color=\"FFFFFF\"> CRD</font>");
                    html.append("</td>");

                    if ((iter % 2) == 0) {
                        html.append("</tr>");
                        html.append("<tr></tr><tr></tr><tr></tr>");
                    } else {
                        html.append("<td></td><td></td>");
                    }

                    count++;
                }

            }

            html.append("</table>");

            String content = HtmCache.getInstance().getHtml(Config.BBS_HOME_DIR + "pages/exchange.htm", player);
            content = content.replace("%listNum%", out.toString());
            content = content.replace("%exchangeItem%", html.toString());
            ShowBoard.separateAndSend(content, player);

        }
        if (bypass.startsWith("_bbsbrokerMyList")) {
            int currentPage = 1;
            int enchantType = 0;
            int sortType = 0;

            StringTokenizer st2 = new StringTokenizer(bypass, ";");
            String[] mBypass = st2.nextToken().split(" ");

            try {
                currentPage = Integer.parseInt(mBypass[1]);
                enchantType = Integer.parseInt(mBypass[2]);
                sortType = Integer.parseInt(mBypass[3]);
            } catch (Exception e) {
            }

            List<ItemInstance> items = new ArrayList<ItemInstance>(player.getInventory().getSize() * 10);
            ItemInstance[] arr = player.getInventory().getItems();
            int len = arr.length;
            
            for (int i = 0; i < len; i++) {
                ItemInstance _item = arr[i];
              /*  if (_item == null || _item.getTemplate().isBelt() || _item.isCursed() || _item.isArrow()
                        || _item.getTemplate().isBracelet() || _item.getTemplate().isCloak() || _item.isNoEnchant()
                        || !_item.isEquipped() || _item.isShieldNoEnchant() || _item.getItemType() == ArmorTemplate.ArmorType.SIGIL || _item.isHeroWeapon()
                        || _item.getItemId() >= 7816 && _item.getItemId() <= 7831 || _item.isShadowItem()
                        || _item.isCommonItem()
                        || _item.getEquipSlot() == ItemTemplate.SLOT_HAIR || _item.getEquipSlot() == ItemTemplate.SLOT_DHAIR) {
                    continue;
                }*/
                if ((_item == null) || _item.isStackable())
                    continue;
                items.add(_item);
                
            }
            
            StringBuilder out = new StringBuilder(200);

            out.append("<table width=450>");

            out.append("<tr>");
            out.append("<td width=50 align=\"left\">");
            out.append("<font color=\"00FF00\">Мои предметы в инвентаре</font>");
            out.append("</td>");
            out.append("<td width=10 align=\"left\">|</td>");

            out.append("<td align=\"left\">");
            out.append("<a action=\"bypass _bbsbrokerMyList 1 0 0;");
            out.append("\">");
            out.append("««");
            out.append("</a>");
            out.append("</td>");

            int totalPages = items.size();
            totalPages = totalPages / MAX_ITEMS_PER_PAGE + (totalPages % MAX_ITEMS_PER_PAGE > 0 ? 1 : 0);
            totalPages = Math.max(1, totalPages);
            currentPage = Math.min(totalPages, Math.max(1, currentPage));

            if (totalPages > 1) {
                int page = Math.max(1, Math.min(totalPages - MAX_PAGES_PER_LIST + 1, currentPage - MAX_PAGES_PER_LIST / 2));

                // линк на первую страницу
                if (page > 1) {
                    listPageNum(out, "_bbsbrokerMyList", 1, enchantType, sortType, "1");
                }
                // линк на страницу - 10
                if (currentPage > 11) {
                    listPageNum(out, "_bbsbrokerMyList", currentPage - 10, enchantType, sortType, String.valueOf(currentPage - 10));
                }
                // линк на предыдущую страницу
                if (currentPage > 1) {
                    listPageNum(out, "_bbsbrokerMyList", currentPage - 1, enchantType, sortType, "«");
                }

                for (int count = 0; count < MAX_PAGES_PER_LIST && page <= totalPages; count++, page++) {
                    if (page == currentPage) {
                        out.append("<td align=\"left\">").append(page).append("&nbsp;").append("</td>");
                    } else {
                        listPageNum(out, "_bbsbrokerMyList", page, enchantType, sortType, String.valueOf(page));
                    }
                }

                // линк на следующую страницу
                if (currentPage < totalPages) {
                    listPageNum(out, "_bbsbrokerMyList", currentPage + 1, enchantType, sortType, "»");
                }
                // линк на страницу + 10
                if (currentPage < totalPages - 10) {
                    listPageNum(out, "_bbsbrokerMyList", currentPage + 10, enchantType, sortType, String.valueOf(currentPage + 10));
                }
                // линк на последнюю страницу
                if (page <= totalPages) {
                    listPageNum(out, "_bbsbrokerMyList", totalPages, enchantType, sortType, String.valueOf(totalPages));
                }
            }
            out.append("</tr>");
            out.append("</table>");

            int iter = 0;
            StringBuilder html = new StringBuilder();
            html.append("<table width=600>");

            if (items.size() > 0) {
                int count = 0;

                ListIterator<ItemInstance> entries = items.listIterator((currentPage - 1) * MAX_ITEMS_PER_PAGE);

                while (entries.hasNext() && count < MAX_ITEMS_PER_PAGE) {
                    ItemInstance currExchItem = entries.next();

                    iter++;
                    ItemTemplate item = ItemHolder.getInstance().getTemplate(currExchItem.getItemId());
                    if ((iter % 2) != 0) {
                        html.append("<tr>");
                    }
                    html.append("<td align=\"left\">");
                    html.append("<img src=icon.").append(item.getIcon()).append(" width=32 height=32 align=\"left\">");
                    html.append("</td>");
                    html.append("<td align=\"left\">");
                    if (currExchItem.getEnchantLevel() != 0) {
                        html.append("<a action=\"bypass _bbsbrokerSell:").append(currExchItem.getObjectId()).append(";\"><font color=\"").append(getColorBiItemId(currExchItem.getItemId())).append("\">").append(item.getName()).append("</font></a><font color=\"FFFFFF\"> (+").append(currExchItem.getEnchantLevel()).append(") ").append(currExchItem.getCount()).append(" шт</font>");
                    } else {
                        html.append("<a action=\"bypass _bbsbrokerSell:").append(currExchItem.getObjectId()).append(";\"><font color=\"").append(getColorBiItemId(currExchItem.getItemId())).append("\">").append(item.getName()).append("</font></a><font color=\"FFFFFF\"> ").append(currExchItem.getCount()).append(" шт</font>");
                    }
                    html.append("<br1>");

                    html.append(currExchItem.getAttributes().getFire() > 0 ? "<font color=\"00FF00\">+" + currExchItem.getAttributes().getFire() + " Fire </font>" : "");
                    html.append(currExchItem.getAttributes().getEarth() > 0 ? "<font color=\"00FF00\">+" + currExchItem.getAttributes().getEarth() + " Eath </font>" : "");
                    html.append(currExchItem.getAttributes().getWater() > 0 ? "<font color=\"00FF00\">+" + currExchItem.getAttributes().getWater() + " Water </font>" : "");
                    html.append(currExchItem.getAttributes().getWind() > 0 ? "<font color=\"00FF00\">+" + currExchItem.getAttributes().getWind() + " Wind </font>" : "");
                    html.append(currExchItem.getAttributes().getHoly() > 0 ? "<font color=\"00FF00\">+" + currExchItem.getAttributes().getHoly() + " Holy </font>" : "");
                    html.append(currExchItem.getAttributes().getUnholy() > 0 ? "<font color=\"00FF00\">+" + currExchItem.getAttributes().getUnholy() + " UnHoly </font>" : "");

                    html.append("<br1>");

                    html.append("<font color=\"62C2F2\">Выставить на продажу</font>");
                    html.append("</td>");

                    if ((iter % 2) == 0) {
                        html.append("</tr>");
                        html.append("<tr></tr><tr></tr><tr></tr>");
                    } else {
                        html.append("<td></td><td></td>");
                    }

                    count++;
                }

            }

            html.append("</table>");

            String content = HtmCache.getInstance().getHtml(Config.BBS_HOME_DIR + "pages/exchange.htm", player);
            content = content.replace("%listNum%", out.toString());
            content = content.replace("%exchangeItem%", html.toString());
            ShowBoard.separateAndSend(content, player);

        }
        if (bypass.startsWith("_bbsbrokerMyItem")) {
            int currentPage = 1;
            int enchantType = 0;
            int sortType = 0;

            StringTokenizer st2 = new StringTokenizer(bypass, ";");
            String[] mBypass = st2.nextToken().split(" ");

            try {
                currentPage = Integer.parseInt(mBypass[1]);
                enchantType = Integer.parseInt(mBypass[2]);
                sortType = Integer.parseInt(mBypass[3]);
            } catch (NumberFormatException e) {
            }

            int ojectId = player.getObjectId();
            List<ExchangeItem> items = new ArrayList<ExchangeItem>(ExchangeBroker.getItemsForPlayer(ojectId).size() * 10);
            for (ExchangeItem tempItems : ExchangeBroker.getItemsForPlayer(ojectId)) {
                items.add(tempItems);
            }

            StringBuilder out = new StringBuilder(200);

            out.append("<table width=450>");

            out.append("<tr>");
            out.append("<td width=50 align=\"left\">");
            out.append("<font color=\"00FF00\">Мои предметы на продаже</font>");
            out.append("</td>");
            out.append("<td width=10 align=\"left\">|</td>");

            out.append("<td align=\"left\">");
            out.append("<a action=\"bypass _bbsbrokerMyItem 1 0 0;");
            out.append("\">");
            out.append("««");
            out.append("</a>");
            out.append("</td>");

            int totalPages = ExchangeBroker.getItemsForPlayer(ojectId).size();
            totalPages = totalPages / MAX_ITEMS_PER_PAGE + (totalPages % MAX_ITEMS_PER_PAGE > 0 ? 1 : 0);
            totalPages = Math.max(1, totalPages);
            currentPage = Math.min(totalPages, Math.max(1, currentPage));

            if (totalPages > 1) {
                int page = Math.max(1, Math.min(totalPages - MAX_PAGES_PER_LIST + 1, currentPage - MAX_PAGES_PER_LIST / 2));

                // линк на первую страницу
                if (page > 1) {
                    listPageNum(out, "_bbsbrokerMyItem", 1, enchantType, sortType, "1");
                }
                // линк на страницу - 10
                if (currentPage > 11) {
                    listPageNum(out, "_bbsbrokerMyItem", currentPage - 10, enchantType, sortType, String.valueOf(currentPage - 10));
                }
                // линк на предыдущую страницу
                if (currentPage > 1) {
                    listPageNum(out, "_bbsbrokerMyItem", currentPage - 1, enchantType, sortType, "«");
                }

                for (int count = 0; count < MAX_PAGES_PER_LIST && page <= totalPages; count++, page++) {
                    if (page == currentPage) {
                        out.append("<td align=\"left\">").append(page).append("&nbsp;").append("</td>");
                    } else {
                        listPageNum(out, "_bbsbrokerMyItem", page, enchantType, sortType, String.valueOf(page));
                    }
                }

                // линк на следующую страницу
                if (currentPage < totalPages) {
                    listPageNum(out, "_bbsbrokerMyItem", currentPage + 1, enchantType, sortType, "»");
                }
                // линк на страницу + 10
                if (currentPage < totalPages - 10) {
                    listPageNum(out, "_bbsbrokerMyItem", currentPage + 10, enchantType, sortType, String.valueOf(currentPage + 10));
                }
                // линк на последнюю страницу
                if (page <= totalPages) {
                    listPageNum(out, "_bbsbrokerMyItem", totalPages, enchantType, sortType, String.valueOf(totalPages));
                }
            }
            out.append("</tr>");
            out.append("</table>");

            int iter = 0;
            StringBuilder html = new StringBuilder();
            html.append("<table width=600>");

            if (items.size() > 0) {
                int count = 0;

                ListIterator<ExchangeItem> entries = items.listIterator((currentPage - 1) * MAX_ITEMS_PER_PAGE);

                while (entries.hasNext() && count < MAX_ITEMS_PER_PAGE) {
                    ExchangeItem currExchItem = entries.next();

                    iter++;
                    ItemTemplate item = ItemHolder.getInstance().getTemplate(currExchItem.getItemId());
                    if ((iter % 2) != 0) {
                        html.append("<tr>");
                    }
                    html.append("<td align=\"left\">");
                    html.append("<img src=icon.").append(item.getIcon()).append(" width=32 height=32 align=\"left\">");
                    html.append("</td>");
                    html.append("<td align=\"left\">");
                    if (currExchItem.getEnchantLevel() != 0) {
                        html.append("<a action=\"bypass _bbsbrokerRetrieve:").append(currExchItem.getObjectId()).append(";\"><font color=\"").append(getColorBiItemId(currExchItem.getItemId())).append("\">").append(item.getName()).append("</font></a><font color=\"FFFFFF\"> (+").append(currExchItem.getEnchantLevel()).append(") ").append(currExchItem.getCount()).append(" шт</font>");
                    } else {
                        html.append("<a action=\"bypass _bbsbrokerRetrieve:").append(currExchItem.getObjectId()).append(";\"><font color=\"").append(getColorBiItemId(currExchItem.getItemId())).append("\">").append(item.getName()).append("</font></a><font color=\"FFFFFF\"> ").append(currExchItem.getCount()).append(" шт</font>");
                    }
                    html.append("<br1>");

                    html.append(currExchItem.getAttFire() > 0 ? "<font color=\"00FF00\">+" + currExchItem.getAttFire() + " Fire </font>" : "");
                    html.append(currExchItem.getAttEath() > 0 ? "<font color=\"00FF00\">+" + currExchItem.getAttEath() + " Eath </font>" : "");
                    html.append(currExchItem.getAttWater() > 0 ? "<font color=\"00FF00\">+" + currExchItem.getAttWater() + " Water </font>" : "");
                    html.append(currExchItem.getAttWind() > 0 ? "<font color=\"00FF00\">+" + currExchItem.getAttWind() + " Wind </font>" : "");
                    html.append(currExchItem.getAttHoly() > 0 ? "<font color=\"00FF00\">+" + currExchItem.getAttHoly() + " Holy </font>" : "");
                    html.append(currExchItem.getAttUnholy() > 0 ? "<font color=\"00FF00\">+" + currExchItem.getAttUnholy() + " UnHoly </font>" : "");

                    html.append("<br1>");

                    html.append("<font color=\"62C2F2\">Цена </font><font color=\"D6D52F\">").append(currExchItem.getPrice()).append("</font><font color=\"FFFFFF\"> CRD</font>");
                    html.append("<font color=\"62C2F2\"> Снять с продажи</font>");
                    html.append("</td>");

                    if ((iter % 2) == 0) {
                        html.append("</tr>");
                        html.append("<tr></tr><tr></tr><tr></tr>");
                    } else {
                        html.append("<td></td><td></td>");
                    }

                    count++;
                }

            }

            html.append("</table>");

            String content = HtmCache.getInstance().getHtml(Config.BBS_HOME_DIR + "pages/exchange.htm", player);
            content = content.replace("%listNum%", out.toString());
            content = content.replace("%exchangeItem%", html.toString());
            ShowBoard.separateAndSend(content, player);
        }

        if (bypass.startsWith("_bbsbrokerBuy")) {
            StringTokenizer st2 = new StringTokenizer(bypass, ";");
            String[] mBypass = st2.nextToken().split(":");
            if (mBypass == null) {
                return;
            }

            int ItemObjID = Integer.parseInt(mBypass[1]);

            ExchangeItem eItem = ExchangeBroker.getItemsForObjectId(ItemObjID);
            ItemTemplate item = ItemHolder.getInstance().getTemplate(eItem.getItemId());

            HtmlMessage reply = new HtmlMessage(5);
            StringBuilder html = new StringBuilder("<html><body>");
            html.append("<center>");
            html.append("<font color=\"FFFF00\">[Покупка предмета]</font>");
            html.append("<br>");
            html.append("<tr>");
            html.append("<td align=\"left\">");
            html.append("<img src=icon.").append(item.getIcon()).append(" width=32 height=32 align=\"left\">");
            html.append("</td>");
            html.append("<td align=\"left\">");
            if (eItem.getEnchantLevel() != 0) {
                html.append("<font color=\"").append(getColorBiItemId(eItem.getItemId())).append("\">").append(item.getName()).append("</font></a><font color=\"FFFFFF\"> (+").append(eItem.getEnchantLevel()).append(") ").append(eItem.getCount()).append(" шт</font>");
            } else {
                html.append("<font color=\"").append(getColorBiItemId(eItem.getItemId())).append("\">").append(item.getName()).append("</font></a><font color=\"FFFFFF\"> ").append(eItem.getCount()).append(" шт</font>");
            }
            html.append("<br1>");
            html.append(eItem.getAttFire() > 0 ? "<font color=\"00FF00\">+" + eItem.getAttFire() + " Fire </font>" : "");
            html.append(eItem.getAttEath() > 0 ? "<font color=\"00FF00\">+" + eItem.getAttEath() + " Eath </font>" : "");
            html.append(eItem.getAttWater() > 0 ? "<font color=\"00FF00\">+" + eItem.getAttWater() + " Water </font>" : "");
            html.append(eItem.getAttWind() > 0 ? "<font color=\"00FF00\">+" + eItem.getAttWind() + " Wind </font>" : "");
            html.append(eItem.getAttHoly() > 0 ? "<font color=\"00FF00\">+" + eItem.getAttHoly() + " Holy </font>" : "");
            html.append(eItem.getAttUnholy() > 0 ? "<font color=\"00FF00\">+" + eItem.getAttUnholy() + " UnHoly </font>" : "");

            html.append("<br1>");
            html.append("<font color=\"62C2F2\">Продается</font><font color=\"FFFFFF\"> за </font><font color=\"D6D52F\">").append(eItem.getPrice()).append("</font><font color=\"FFFFFF\"> CRD</font>");
            html.append("</td>");
            html.append("</tr>");
            html.append("<button value=\"Купить\" action=\"bypass _bbsbrokerBuyItem:").append(eItem.getObjectId()).append(";\" width=100 height=26 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"/>");

            html.append("</center>");
            html.append("</body></html>");

            reply.setHtml(html.toString());
            player.sendPacket(reply);
        }

        if (bypass.startsWith("_bbsbrokerBuyItem")) {
            StringTokenizer st2 = new StringTokenizer(bypass, ";");
            String[] mBypass = st2.nextToken().split(":");
            if (mBypass == null) {
                return;
            }

            int ItemObjID = Integer.parseInt(mBypass[1]);

            ExchangeItem eItem = ExchangeBroker.getItemsForObjectId(ItemObjID);

            if (player.getObjectId() == eItem.getOwnerId()) {
                player.sendMessage("Воспользуйтесь возвратом предмета");
                return;
            }

            if (player.getBalans() < eItem.getPrice()) {
                player.sendMessage("Недостаточно валюты, пополните баланс");
                return;
            }

            final ItemInstance item = ItemFunctions.createItem(eItem.getItemId());
            if (item == null || eItem.getItemId() == ItemTemplate.ITEM_ID_ADENA) {
                return;
            }

            item.setEnchantLevel(eItem.getEnchantLevel());
            item.setCount(eItem.getCount());

            if (eItem.getAugmentationId() != 0) {
                item.setAugmentationId(eItem.getAugmentationId());
            }
            item.setAttributeElement(Element.FIRE, eItem.getAttFire());
            item.setAttributeElement(Element.EARTH, eItem.getAttEath());
            item.setAttributeElement(Element.HOLY, eItem.getAttHoly());
            item.setAttributeElement(Element.UNHOLY, eItem.getAttUnholy());
            item.setAttributeElement(Element.WATER, eItem.getAttWater());
            item.setAttributeElement(Element.WIND, eItem.getAttWind());

            player.setBalans(player.getBalans() - eItem.getPrice());
            Player seller = World.getPlayer(eItem.getOwnerId());
            if (seller != null && seller.isOnline()) {
                seller.setBalans(seller.getBalans() + eItem.getPrice());
            } else {
                ExchangeBroker.getInstance().addBalansOff(eItem.getOwnerId(), eItem.getPrice());
            }

            player.getInventory().addItem(item);
            SystemMessage2 smsg;
            if (eItem.getCount() == 1) {
                smsg = new SystemMessage2(SystemMsg.YOU_HAVE_OBTAINED_S1);
                smsg.addItemName(eItem.getItemId());
                player.sendPacket(smsg);
            } else {
                smsg = new SystemMessage2(SystemMsg.YOU_HAVE_OBTAINED_S2_S1);
                smsg.addItemName(eItem.getItemId());
                smsg.addInteger(eItem.getCount());
                player.sendPacket(smsg);
            }

            ExchangeBroker.deleteItem(ItemObjID);

            onBypassCommand(player, "_bbsbrokerAllList 1 0 0;");

            HtmlMessage reply = new HtmlMessage(5);
            StringBuilder html = new StringBuilder("<html><body>");
            html.append("<center>");
            html.append("<font color=\"FFFF00\">Поздравляем с приобретением предмета!</font>");
            html.append("</center>");
            html.append("</body></html>");

            reply.setHtml(html.toString());
            player.sendPacket(reply);
        }
        if (bypass.startsWith("_bbsbrokerSell")) {
            StringTokenizer st2 = new StringTokenizer(bypass, ";");
            String[] mBypass = st2.nextToken().split(":");
            if (mBypass == null) {
                return;
            }

            int ItemObjID = Integer.parseInt(mBypass[1]);

            ItemInstance eItem = player.getInventory().getItemByObjectId(ItemObjID);
            ItemTemplate item = ItemHolder.getInstance().getTemplate(eItem.getItemId());

            HtmlMessage reply = new HtmlMessage(5);
            StringBuilder html = new StringBuilder("<html><body>");
            html.append("<center>");
            html.append("<font color=\"FFFF00\">[Продажа предмета]</font>");
            html.append("<br>");
            html.append("<tr>");
            html.append("<td align=\"left\">");
            html.append("<img src=icon.").append(item.getIcon()).append(" width=32 height=32 align=\"left\">");
            html.append("</td>");
            html.append("<td align=\"left\">");
            if (eItem.getEnchantLevel() != 0) {
                html.append("<font color=\"").append(getColorBiItemId(eItem.getItemId())).append("\">").append(item.getName()).append("</font></a><font color=\"FFFFFF\"> (+").append(eItem.getEnchantLevel()).append(") ").append(eItem.getCount()).append(" шт</font>");
            } else {
                html.append("<font color=\"").append(getColorBiItemId(eItem.getItemId())).append("\">").append(item.getName()).append("</font></a><font color=\"FFFFFF\"> ").append(eItem.getCount()).append(" шт</font>");
            }
            html.append("<br1>");
            html.append(eItem.getAttributes().getFire() > 0 ? "<font color=\"00FF00\">+" + eItem.getAttributes().getFire() + " Fire </font>" : "");
            html.append(eItem.getAttributes().getEarth() > 0 ? "<font color=\"00FF00\">+" + eItem.getAttributes().getEarth() + " Eath </font>" : "");
            html.append(eItem.getAttributes().getWater() > 0 ? "<font color=\"00FF00\">+" + eItem.getAttributes().getWater() + " Water </font>" : "");
            html.append(eItem.getAttributes().getWind() > 0 ? "<font color=\"00FF00\">+" + eItem.getAttributes().getWind() + " Wind </font>" : "");
            html.append(eItem.getAttributes().getHoly() > 0 ? "<font color=\"00FF00\">+" + eItem.getAttributes().getHoly() + " Holy </font>" : "");
            html.append(eItem.getAttributes().getUnholy() > 0 ? "<font color=\"00FF00\">+" + eItem.getAttributes().getUnholy() + " UnHoly </font>" : "");

            html.append("<br1>");
            html.append("<font color=\"62C2F2\">Установите цену</font>");
            html.append("</td>");
            html.append("</tr>");

            html.append("<edit var=\"price\" width=80></td><td><button value=\"Продать\" action=\"bypass _bbsbrokerSellItem:").append(eItem.getObjectId()).append(": $price\" width=100 height=26 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\">");
            html.append("</center>");
            html.append("</body></html>");

            reply.setHtml(html.toString());
            player.sendPacket(reply);
        }
        if (bypass.startsWith("_bbsbrokerSellItem")) {

            StringTokenizer st2 = new StringTokenizer(bypass, ";");
            String[] mBypass = st2.nextToken().split(":");
            if (mBypass == null) {
                return;
            }
            int ItemObjID = Integer.parseInt(mBypass[1]);
            int price = Integer.parseInt(mBypass[2].substring(1));
            ItemInstance item = player.getInventory().getItemByObjectId(ItemObjID);

            ExchangeBroker.getInstance().addItemsSale(item, player, 1, price);
            player.getInventory().destroyItem(item);

            onBypassCommand(player, "_bbsbrokerMyList 1 0 0;");

            HtmlMessage reply = new HtmlMessage(5);
            StringBuilder html = new StringBuilder("<html><body>");
            html.append("<center>");
            html.append("<font color=\"FFFF00\">Ваш предмет успешно выставлен на продажу!</font>");
            html.append("</center>");
            html.append("</body></html>");

            reply.setHtml(html.toString());
            player.sendPacket(reply);
        }
        if (bypass.startsWith("_bbsbrokerRetrieve")) {
            StringTokenizer st2 = new StringTokenizer(bypass, ";");
            String[] mBypass = st2.nextToken().split(":");
            if (mBypass == null) {
                return;
            }

            int ItemObjID = Integer.parseInt(mBypass[1]);

            ExchangeItem eItem = ExchangeBroker.getItemsForObjectId(ItemObjID);
            ItemTemplate item = ItemHolder.getInstance().getTemplate(eItem.getItemId());

            HtmlMessage reply = new HtmlMessage(5);
            StringBuilder html = new StringBuilder("<html><body>");
            html.append("<center>");
            html.append("<font color=\"FFFF00\">[Возврат предмета]</font>");
            html.append("<br>");
            html.append("<tr>");
            html.append("<td align=\"left\">");
            html.append("<img src=icon.").append(item.getIcon()).append(" width=32 height=32 align=\"left\">");
            html.append("</td>");
            html.append("<td align=\"left\">");
            if (eItem.getEnchantLevel() != 0) {
                html.append("<font color=\"").append(getColorBiItemId(eItem.getItemId())).append("\">").append(item.getName()).append("</font></a><font color=\"FFFFFF\"> (+").append(eItem.getEnchantLevel()).append(") ").append(eItem.getCount()).append(" шт</font>");
            } else {
                html.append("<font color=\"").append(getColorBiItemId(eItem.getItemId())).append("\">").append(item.getName()).append("</font></a><font color=\"FFFFFF\"> ").append(eItem.getCount()).append(" шт</font>");
            }
            html.append("<br1>");
            html.append(eItem.getAttFire() > 0 ? "<font color=\"00FF00\">+" + eItem.getAttFire() + " Fire </font>" : "");
            html.append(eItem.getAttEath() > 0 ? "<font color=\"00FF00\">+" + eItem.getAttEath() + " Eath </font>" : "");
            html.append(eItem.getAttWater() > 0 ? "<font color=\"00FF00\">+" + eItem.getAttWater() + " Water </font>" : "");
            html.append(eItem.getAttWind() > 0 ? "<font color=\"00FF00\">+" + eItem.getAttWind() + " Wind </font>" : "");
            html.append(eItem.getAttHoly() > 0 ? "<font color=\"00FF00\">+" + eItem.getAttHoly() + " Holy </font>" : "");
            html.append(eItem.getAttUnholy() > 0 ? "<font color=\"00FF00\">+" + eItem.getAttUnholy() + " UnHoly </font>" : "");

            html.append("<br1>");
            html.append("<font color=\"62C2F2\">Продается</font><font color=\"FFFFFF\"> за </font><font color=\"D6D52F\">").append(eItem.getPrice()).append("</font><font color=\"FFFFFF\"> CRD</font>");
            html.append("</td>");
            html.append("</tr>");
            html.append("<button value=\"Вернуть\" action=\"bypass _bbsbrokerRetrieveItem:").append(eItem.getObjectId()).append(";\" width=100 height=26 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"/>");

            html.append("</center>");
            html.append("</body></html>");

            reply.setHtml(html.toString());
            player.sendPacket(reply);
        }
        if (bypass.startsWith("_bbsbrokerRetrieveItem")) {
            StringTokenizer st2 = new StringTokenizer(bypass, ";");
            String[] mBypass = st2.nextToken().split(":");
            if (mBypass == null) {
                return;
            }

            int ItemObjID = Integer.parseInt(mBypass[1]);

            ExchangeItem eItem = ExchangeBroker.getItemsForObjectId(ItemObjID);

            if (player.getObjectId() != eItem.getOwnerId()) {
                player.sendMessage("Запрещено");
                return;
            }

            final ItemInstance item = ItemFunctions.createItem(eItem.getItemId());
            if (item == null || eItem.getItemId() == ItemTemplate.ITEM_ID_ADENA) {
                return;
            }

            item.setEnchantLevel(eItem.getEnchantLevel());
            item.setCount(eItem.getCount());

            if (eItem.getAugmentationId() != 0) {
                item.setAugmentationId(eItem.getAugmentationId());
            }
            item.setAttributeElement(Element.FIRE, eItem.getAttFire());
            item.setAttributeElement(Element.EARTH, eItem.getAttEath());
            item.setAttributeElement(Element.HOLY, eItem.getAttHoly());
            item.setAttributeElement(Element.UNHOLY, eItem.getAttUnholy());
            item.setAttributeElement(Element.WATER, eItem.getAttWater());
            item.setAttributeElement(Element.WIND, eItem.getAttWind());

            player.getInventory().addItem(item);
            SystemMessage2 smsg;
            if (eItem.getCount() == 1) {
                smsg = new SystemMessage2(SystemMsg.YOU_HAVE_OBTAINED_S1);
                smsg.addItemName(eItem.getItemId());
                player.sendPacket(smsg);
            } else {
                smsg = new SystemMessage2(SystemMsg.YOU_HAVE_OBTAINED_S2_S1);
                smsg.addItemName(eItem.getItemId());
                smsg.addInteger(eItem.getCount());
                player.sendPacket(smsg);
            }

            ExchangeBroker.deleteItem(ItemObjID);

            onBypassCommand(player, "_bbsbrokerMyItem 1 0 0;");

            HtmlMessage reply = new HtmlMessage(5);
            StringBuilder html = new StringBuilder("<html><body>");
            html.append("<center>");
            html.append("<font color=\"FFFF00\">Вы изъяли предмет с биржы!</font>");
            html.append("</center>");
            html.append("</body></html>");

            reply.setHtml(html.toString());
            player.sendPacket(reply);

        }
    }

    private void listPageNum(StringBuilder out, String bypass, int curentPage, int enchantType, int sortType, String letter) {
        out.append("<td align=\"left\">");
        out.append("<a action=\"bypass ");
        out.append(bypass);
        out.append(" ");
        out.append(curentPage);
        out.append(" ");
        out.append(enchantType);
        out.append(" ");
        out.append(sortType);
        out.append("\">");
        out.append(letter);
        out.append("</a>");
        out.append("</td>");
    }

    @Override
    public void onWriteCommand(Player player, String bypass, String arg1, String arg2, String arg3, String arg4, String arg5) {
    }

    private static boolean CheckCondition(Player player) {
        if (player == null) {
            return false;
        }

        if (player.isDead()) {
            return false;
        }

        if ((player.getPvpFlag() != 0 || player.isInDuel() || player.isInCombat() || player.isAttackingNow())) {
            if (player.isLangRus()) {
                player.sendMessage("Во время боя нельзя использовать данную функцию.");
            } else {
                player.sendMessage("During combat, you can not use this feature.");
            }
            return false;
        }

        if (player.isInOlympiadMode()) {
            if (player.isLangRus()) {
                player.sendMessage("Во время Олимпиады нельзя использовать данную функцию.");
            } else {
                player.sendMessage("During the Olympics you can not use this feature.");
            }
            return false;
        }

        if (!Config.COMMUNITYBOARD_EXCHANGE_ENABLED) {
            if (player.isLangRus()) {
                player.sendMessage("Функция биржы отключена.");
            } else {
                player.sendMessage("Exchange off function.");
            }
            return false;
        }

        if (player.getTeam() != TeamType.NONE) {
            if (player.isLangRus()) {
                player.sendMessage("Нельзя использовать биржу во время эвентов.");
            } else {
                player.sendMessage("You can not use the exchange during Events.");
            }
            return false;
        }
        return true;
    }

    private String getColorBiItemId(int itemId) {
        String color = "F25202";

        if (org.apache.commons.lang3.ArrayUtils.contains(RARE_ITEMS, itemId)) {
            color = "FFFF00";
        }
        return color;
    }
}

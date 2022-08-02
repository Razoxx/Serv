package l2p.gameserver.tables;

import gnu.trove.map.hash.TIntObjectHashMap;
import l2p.gameserver.Config;
import l2p.gameserver.model.PetData;
import l2p.gameserver.model.items.ItemInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PetDataTable {

    private static final Logger _log = LoggerFactory.getLogger(PetDataTable.class);
    private static final PetDataTable _instance = new PetDataTable();

    public static final PetDataTable getInstance() {
        return _instance;
    }
    public final static int PET_WOLF_ID = 12077;
    public final static int HATCHLING_WIND_ID = 12311;
    public final static int HATCHLING_STAR_ID = 12312;
    public final static int HATCHLING_TWILIGHT_ID = 12313;
    public final static int STRIDER_WIND_ID = 12526;
    public final static int STRIDER_STAR_ID = 12527;
    public final static int STRIDER_TWILIGHT_ID = 12528;
    public final static int RED_STRIDER_WIND_ID = 16038;
    public final static int RED_STRIDER_STAR_ID = 16039;
    public final static int RED_STRIDER_TWILIGHT_ID = 16040;
    public final static int WYVERN_ID = 12621;
    public final static int BABY_BUFFALO_ID = 12780;
    public final static int BABY_KOOKABURRA_ID = 12781;
    public final static int BABY_COUGAR_ID = 12782;
    public final static int IMPROVED_BABY_BUFFALO_ID = 16034;
    public final static int IMPROVED_BABY_KOOKABURRA_ID = 16035;
    public final static int IMPROVED_BABY_COUGAR_ID = 16036;
    public final static int SIN_EATER_ID = 12564;
    public final static int GREAT_WOLF_ID = 16025;
    public final static int WGREAT_WOLF_ID = 16037;
    public final static int FENRIR_WOLF_ID = 16041;
    public final static int WFENRIR_WOLF_ID = 16042;
    public final static int LIGHT_PURPLE_MANED_HORSE_ID = 13130;
    public final static int TAWNY_MANED_LION_ID = 13146;
    public final static int STEAM_BEATLE_ID = 13147;
    public final static int AURA_BIRD_FALCON_ID = 13144;
    public final static int AURA_BIRD_OWL_ID = 13145;
    public final static int FOX_SHAMAN_ID = 16043;
    public final static int WILD_BEAST_FIGHTER_ID = 16044;
    public final static int WHITE_WEASEL_ID = 16045;
    public final static int FAIRY_PRINCESS_ID = 16046;
    public final static int OWL_MONK_ID = 16050;
    public final static int SPIRIT_SHAMAN_ID = 16051;
    public final static int TOY_KNIGHT_ID = 16052;
    public final static int TURTLE_ASCETIC_ID = 16053;
    public final static int DEINONYCHUS_ID = 16067;
    public final static int GUARDIANS_STRIDER_ID = 16068;
    public final static int MAGUEN_ID = 16071;
    public final static int ELITE_MAGUEN_ID = 16072;

    public final static int ROSE_DESELOPH_ID = 1562;
    public final static int ROSE_HYUM_ID = 1563;
    public final static int ROSE_REKANG_ID = 1564;
    public final static int ROSE_LILIAS_ID = 1565;
    public final static int ROSE_LAPHAM_ID = 1566;
    public final static int ROSE_MAPHUM_ID = 1567;

    public final static int IMPROVED_ROSE_DESELOPH_ID = 1568;
    public final static int IMPROVED_ROSE_HYUM_ID = 1569;
    public final static int IMPROVED_ROSE_REKANG_ID = 1570;
    public final static int IMPROVED_ROSE_LILIAS_ID = 1571;
    public final static int IMPROVED_ROSE_LAPHAM_ID = 1572;
    public final static int IMPROVED_ROSE_MAPHUM_ID = 1573;

    public final static int SUPER_FELINE_QUEEN_Z_ID = 1601;
    public final static int SUPER_KAT_THE_CAT_Z_ID = 1602;
    public final static int SUPER_MEW_THE_CAT_Z_ID = 1603;

    private final TIntObjectHashMap<PetData> _pets = new TIntObjectHashMap<PetData>();

    public PetData getInfo(int petNpcId, int level) {
        PetData result = null;
        while (result == null && level < 100) {
            result = _pets.get(petNpcId * 100 + level);
            level++;
        }

        return result;
    }

    public static enum L2Pet {

        WOLF(PET_WOLF_ID, 2375, 2515, false, 1, 12),
        HATCHLING_WIND(HATCHLING_WIND_ID, 3500, 4038, false, 1, 12),
        HATCHLING_STAR(HATCHLING_STAR_ID, 3501, 4038, false, 1, 12),
        HATCHLING_TWILIGHT(HATCHLING_TWILIGHT_ID, 3502, 4038, false, 1, 100),
        STRIDER_WIND(STRIDER_WIND_ID, 4422, 5168, true, 1, 12),
        STRIDER_STAR(STRIDER_STAR_ID, 4423, 5168, true, 1, 12),
        STRIDER_TWILIGHT(STRIDER_TWILIGHT_ID, 4424, 5168, true, 1, 100),
        RED_STRIDER_WIND(RED_STRIDER_WIND_ID, 10308, 5168, true, 1, 12),
        RED_STRIDER_STAR(RED_STRIDER_STAR_ID, 10309, 5168, true, 1, 12),
        RED_STRIDER_TWILIGHT(RED_STRIDER_TWILIGHT_ID, 10310, 5168, true, 1, 100),
        WYVERN(WYVERN_ID, 5249, 6316, true, 1, 12),
        GREAT_WOLF(GREAT_WOLF_ID, 9882, 9668, false, 55, 10),
        WGREAT_WOLF(WGREAT_WOLF_ID, 10307, 9668, true, 55, 12),
        FENRIR_WOLF(FENRIR_WOLF_ID, 10426, 9668, true, 70, 12),
        WFENRIR_WOLF(WFENRIR_WOLF_ID, 10611, 9668, true, 70, 12),
        BABY_BUFFALO(BABY_BUFFALO_ID, 6648, 7582, false, 1, 12),
        BABY_KOOKABURRA(BABY_KOOKABURRA_ID, 6650, 7582, false, 1, 12),
        BABY_COUGAR(BABY_COUGAR_ID, 6649, 7582, false, 1, 12),
        IMPROVED_BABY_BUFFALO(IMPROVED_BABY_BUFFALO_ID, 10311, 10425, false, 55, 12),
        IMPROVED_BABY_KOOKABURRA(IMPROVED_BABY_KOOKABURRA_ID, 10313, 10425, false, 55, 12),
        IMPROVED_BABY_COUGAR(IMPROVED_BABY_COUGAR_ID, 10312, 10425, false, 55, 12),
        SIN_EATER(SIN_EATER_ID, 4425, 2515, false, 1, 12),
        FOX_SHAMAN(FOX_SHAMAN_ID, 13020, Config.VITAMIN_PETS_FOOD_ID, false, 25, 12),
        FOX_SHAMAN_EVENT(FOX_SHAMAN_ID, 13306, Config.VITAMIN_PETS_FOOD_ID, false, 25, 12),
        WILD_BEAST_FIGHTER(WILD_BEAST_FIGHTER_ID, 13019, Config.VITAMIN_PETS_FOOD_ID, false, 25, 12),
        WILD_BEAST_FIGHTER_EVENT(WILD_BEAST_FIGHTER_ID, 13305, Config.VITAMIN_PETS_FOOD_ID, false, 25, 12),
        WHITE_WEASEL(WHITE_WEASEL_ID, 13017, Config.VITAMIN_PETS_FOOD_ID, false, 25, 12),
        WHITE_WEASEL_EVENT(WHITE_WEASEL_ID, 13303, Config.VITAMIN_PETS_FOOD_ID, false, 25, 12),
        FAIRY_PRINCESS(FAIRY_PRINCESS_ID, 13018, Config.VITAMIN_PETS_FOOD_ID, false, 25, 12),
        FAIRY_PRINCESS_EVENT(FAIRY_PRINCESS_ID, 13304, Config.VITAMIN_PETS_FOOD_ID, false, 25, 12),
        OWL_MONK(OWL_MONK_ID, 13550, Config.VITAMIN_PETS_FOOD_ID, false, 25, 12),
        OWL_MONK_EVENT(OWL_MONK_ID, 14063, Config.VITAMIN_PETS_FOOD_ID, false, 25, 12),
        SPIRIT_SHAMAN(SPIRIT_SHAMAN_ID, 14062, Config.VITAMIN_PETS_FOOD_ID, false, 25, 12),
        SOUL_MONK(SPIRIT_SHAMAN_ID, 13549, Config.VITAMIN_PETS_FOOD_ID, false, 25, 12),
        TOY_KNIGHT(TOY_KNIGHT_ID, 14061, Config.VITAMIN_PETS_FOOD_ID, false, 25, 12),
        TOY_KNIGHT_2(TOY_KNIGHT_ID, 13548, Config.VITAMIN_PETS_FOOD_ID, false, 25, 12),
        TURTLE_ASCETIC(TURTLE_ASCETIC_ID, 14064, Config.VITAMIN_PETS_FOOD_ID, false, 25, 12),
        TURTLE_ASCETIC_2(TURTLE_ASCETIC_ID, 13551, Config.VITAMIN_PETS_FOOD_ID, false, 25, 12),
        DEINONYCHUS(DEINONYCHUS_ID, 14828, 2515, false, 55, 12),
        GUARDIANS_STRIDER(GUARDIANS_STRIDER_ID, 14819, 5168, true, 55, 12),
        MAGUEN(MAGUEN_ID, 15488, 2515, false, 1, 10),
        ELITE_MAGUEN(ELITE_MAGUEN_ID, 15489, 2515, false, 1, 10),
        ROSE_DESELOPH(ROSE_DESELOPH_ID, 20908, Config.VITAMIN_PETS_FOOD_ID, false, 1, 12),
        ROSE_HYUM(ROSE_HYUM_ID, 20909, Config.VITAMIN_PETS_FOOD_ID, false, 1, 12),
        ROSE_REKANG(ROSE_REKANG_ID, 20910, Config.VITAMIN_PETS_FOOD_ID, false, 1, 12),
        ROSE_LILIAS(ROSE_LILIAS_ID, 20911, Config.VITAMIN_PETS_FOOD_ID, false, 1, 12),
        ROSE_LAPHAM(ROSE_LAPHAM_ID, 20912, Config.VITAMIN_PETS_FOOD_ID, false, 1, 12),
        ROSE_MAPHUM(ROSE_MAPHUM_ID, 20913, Config.VITAMIN_PETS_FOOD_ID, false, 1, 12),
        IMPROVED_ROSE_DESELOPH(IMPROVED_ROSE_DESELOPH_ID, 20915, Config.VITAMIN_PETS_FOOD_ID, false, 1, 12),
        IMPROVED_ROSE_HYUM(IMPROVED_ROSE_HYUM_ID, 20916, Config.VITAMIN_PETS_FOOD_ID, false, 1, 12),
        IMPROVED_ROSE_REKANG(IMPROVED_ROSE_REKANG_ID, 20917, Config.VITAMIN_PETS_FOOD_ID, false, 1, 12),
        IMPROVED_ROSE_LILIAS(IMPROVED_ROSE_LILIAS_ID, 20918, Config.VITAMIN_PETS_FOOD_ID, false, 1, 12),
        IMPROVED_ROSE_LAPHAM(IMPROVED_ROSE_LAPHAM_ID, 20919, Config.VITAMIN_PETS_FOOD_ID, false, 1, 12),
        IMPROVED_ROSE_MAPHUM(IMPROVED_ROSE_MAPHUM_ID, 20920, Config.VITAMIN_PETS_FOOD_ID, false, 1, 12),
        SUPER_FELINE_QUEEN_Z(SUPER_FELINE_QUEEN_Z_ID, 21917, Config.VITAMIN_PETS_FOOD_ID, false, 1, 12),
        SUPER_KAT_THE_CAT_Z(SUPER_KAT_THE_CAT_Z_ID, 21916, Config.VITAMIN_PETS_FOOD_ID, false, 1, 12),
        SUPER_MEW_THE_CAT_Z(SUPER_MEW_THE_CAT_Z_ID, 21918, Config.VITAMIN_PETS_FOOD_ID, false, 1, 12);

        private final int _npcId;
        private final int _controlItemId;
        private final int _foodId;
        private final boolean _isMountable;
        private final int _minLevel; // Уровень, ниже которого не может опускаться пет
        private final int _addFed; // На сколько процентов увеличивается полоска еды, при кормлении

        private L2Pet(int npcId, int controlItemId, int foodId, boolean isMountabe, int minLevel, int addFed) {
            _npcId = npcId;
            _controlItemId = controlItemId;
            _foodId = foodId;
            _isMountable = isMountabe;
            _minLevel = minLevel;
            _addFed = addFed;
        }

        public int getNpcId() {
            return _npcId;
        }

        public int getControlItemId() {
            return _controlItemId;
        }

        public int getFoodId() {
            return _foodId;
        }

        public boolean isMountable() {
            return _isMountable;
        }

        public int getMinLevel() {
            return _minLevel;
        }

        public int getAddFed() {
            return _addFed;
        }

    }

    public static int getControlItemId(int npcId) {
        for (L2Pet pet : L2Pet.values()) {
            if (pet.getNpcId() == npcId) {
                return pet.getControlItemId();
            }
        }
        return 1;
    }

    public static int getFoodId(int npcId) {
        for (L2Pet pet : L2Pet.values()) {
            if (pet.getNpcId() == npcId) {
                return pet.getFoodId();
            }
        }
        return 1;
    }

    public static boolean isMountable(int npcId) {
        for (L2Pet pet : L2Pet.values()) {
            if (pet.getNpcId() == npcId) {
                return pet.isMountable();
            }
        }
        return false;
    }

    public static int getMinLevel(int npcId) {
        for (L2Pet pet : L2Pet.values()) {
            if (pet.getNpcId() == npcId) {
                return pet.getMinLevel();
            }
        }
        return 1;
    }

    public static int getAddFed(int npcId) {
        for (L2Pet pet : L2Pet.values()) {
            if (pet.getNpcId() == npcId) {
                return pet.getAddFed();
            }
        }
        return 1;
    }

    public static int getSummonId(ItemInstance item) {
        for (L2Pet pet : L2Pet.values()) {
            if (pet.getControlItemId() == item.getItemId()) {
                return pet.getNpcId();
            }
        }
        return 0;
    }

    public static int[] getPetControlItems() {
        int[] items = new int[L2Pet.values().length];
        int i = 0;
        for (L2Pet pet : L2Pet.values()) {
            items[i++] = pet.getControlItemId();
        }
        return items;
    }

    public static boolean isPetControlItem(ItemInstance item) {
        for (L2Pet pet : L2Pet.values()) {
            if (pet.getControlItemId() == item.getItemId()) {
                return true;
            }
        }
        return false;
    }

    public static boolean isBabyPet(int id) {
        switch (id) {
            case BABY_BUFFALO_ID:
            case BABY_KOOKABURRA_ID:
            case BABY_COUGAR_ID:
            case ROSE_DESELOPH_ID:
            case ROSE_HYUM_ID:
            case ROSE_REKANG_ID:
            case ROSE_LILIAS_ID:
            case ROSE_LAPHAM_ID:
            case ROSE_MAPHUM_ID:
                return true;
            default:
                return false;
        }
    }

    public static boolean isImprovedBabyPet(int id) {
        switch (id) {
            case IMPROVED_BABY_BUFFALO_ID:
            case IMPROVED_BABY_KOOKABURRA_ID:
            case IMPROVED_BABY_COUGAR_ID:
            case FAIRY_PRINCESS_ID:
            case WHITE_WEASEL_ID:
            case TOY_KNIGHT_ID:
            case SPIRIT_SHAMAN_ID:
            case TURTLE_ASCETIC_ID:
            case IMPROVED_ROSE_DESELOPH_ID:
            case IMPROVED_ROSE_HYUM_ID:
            case IMPROVED_ROSE_REKANG_ID:
            case IMPROVED_ROSE_LILIAS_ID:
            case IMPROVED_ROSE_LAPHAM_ID:
            case IMPROVED_ROSE_MAPHUM_ID:
            case SUPER_FELINE_QUEEN_Z_ID:
            case SUPER_KAT_THE_CAT_Z_ID:
            case SUPER_MEW_THE_CAT_Z_ID:
                return true;
            default:
                return false;
        }
    }

    public static boolean isWolf(int id) {
        return id == PET_WOLF_ID;
    }

    public static boolean isHatchling(int id) {
        switch (id) {
            case HATCHLING_WIND_ID:
            case HATCHLING_STAR_ID:
            case HATCHLING_TWILIGHT_ID:
                return true;
            default:
                return false;
        }
    }

    public static boolean isStrider(int id) {
        switch (id) {
            case STRIDER_WIND_ID:
            case STRIDER_STAR_ID:
            case STRIDER_TWILIGHT_ID:
            case RED_STRIDER_WIND_ID:
            case RED_STRIDER_STAR_ID:
            case RED_STRIDER_TWILIGHT_ID:
            case GUARDIANS_STRIDER_ID:
                return true;
            default:
                return false;
        }
    }

    public static boolean isGWolf(int id) {
        switch (id) {
            case GREAT_WOLF_ID:
            case WGREAT_WOLF_ID:
            case FENRIR_WOLF_ID:
            case WFENRIR_WOLF_ID:
                return true;
            default:
                return false;
        }
    }

    public static boolean isMaguen(int id) {
        switch (id) {
            case ELITE_MAGUEN_ID:
            case MAGUEN_ID:
                return true;
            default:
                return false;
        }
    }

    public static boolean isVitaminPet(int id) {
        switch (id) {
            case FOX_SHAMAN_ID:
            case WILD_BEAST_FIGHTER_ID:
            case WHITE_WEASEL_ID:
            case FAIRY_PRINCESS_ID:
            case OWL_MONK_ID:
            case SPIRIT_SHAMAN_ID:
            case TOY_KNIGHT_ID:
            case TURTLE_ASCETIC_ID:
            case SUPER_FELINE_QUEEN_Z_ID:
            case SUPER_KAT_THE_CAT_Z_ID:
            case SUPER_MEW_THE_CAT_Z_ID:

            case IMPROVED_ROSE_DESELOPH_ID:
            case IMPROVED_ROSE_HYUM_ID:
            case IMPROVED_ROSE_REKANG_ID:
            case IMPROVED_ROSE_LILIAS_ID:
            case IMPROVED_ROSE_LAPHAM_ID:
            case IMPROVED_ROSE_MAPHUM_ID:

            case ROSE_DESELOPH_ID:
            case ROSE_HYUM_ID:
            case ROSE_REKANG_ID:
            case ROSE_LILIAS_ID:
            case ROSE_LAPHAM_ID:
            case ROSE_MAPHUM_ID:
                return true;
            default:
                return false;
        }
    }
}

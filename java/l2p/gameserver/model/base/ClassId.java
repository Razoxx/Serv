package l2p.gameserver.model.base;

/**
 * This class defines all classes (ex : human fighter, darkFighter...) that a
 * player can chose.<BR><BR>
 *
 * Data :<BR><BR>
 * <li>id : The Identifier of the class</li>
 * <li>isMage : True if the class is a mage class</li>
 * <li>race : The race of this class</li>
 * <li>parent : The parent ClassId for male or null if this class is the
 * root</li>
 * <li>parent2 : The parent2 ClassId for female or null if parent2 like
 * parent</li>
 * <li>level : The child level of this Class</li><BR><BR>
 */
public enum ClassId {

    fighter(0, false, Race.human, null, null, 1, null, "Воин"),
    warrior(1, false, Race.human, fighter, null, 2, null, "Воитель"),
    gladiator(2, false, Race.human, warrior, null, 3, ClassType2.Warrior, "Гладиатор"),
    warlord(3, false, Race.human, warrior, null, 3, ClassType2.Warrior, "Копейщик"),
    knight(4, false, Race.human, fighter, null, 2, null, "Рыцарь"),
    paladin(5, false, Race.human, knight, null, 3, ClassType2.Knight, "Паладин"),
    darkAvenger(6, false, Race.human, knight, null, 3, ClassType2.Knight, "Мститель"),
    rogue(7, false, Race.human, fighter, null, 2, null, "Разбойник"),
    treasureHunter(8, false, Race.human, rogue, null, 3, ClassType2.Rogue, "Искатель сокровищь"),
    hawkeye(9, false, Race.human, rogue, null, 3, ClassType2.Rogue, "Стрелок"),
    mage(10, true, Race.human, null, null, 1, null, "Мистик"),
    wizard(11, true, Race.human, mage, null, 2, null, "Маг"),
    sorceror(12, true, Race.human, wizard, null, 3, ClassType2.Wizard, "Волшебник"),
    necromancer(13, true, Race.human, wizard, null, 3, ClassType2.Wizard, "Некромант"),
    warlock(14, true, Race.human, wizard, null, 3, ClassType2.Summoner, "Колдун"),
    cleric(15, true, Race.human, mage, null, 2, null, "Клерик"),
    bishop(16, true, Race.human, cleric, null, 3, ClassType2.Healer, "Епископ"),
    prophet(17, true, Race.human, cleric, null, 3, ClassType2.Enchanter, "Проповедник"),
    elvenFighter(18, false, Race.elf, null, null, 1, null, "Светлый воин"),
    elvenKnight(19, false, Race.elf, elvenFighter, null, 2, null, "Светлый рыцарь"),
    templeKnight(20, false, Race.elf, elvenKnight, null, 3, ClassType2.Knight, "Рыцарь Евы"),
    swordSinger(21, false, Race.elf, elvenKnight, null, 3, ClassType2.Enchanter, "Менестрель"),
    elvenScout(22, false, Race.elf, elvenFighter, null, 2, null, "Разведчик"),
    plainsWalker(23, false, Race.elf, elvenScout, null, 3, ClassType2.Rogue, "Следопыт"),
    silverRanger(24, false, Race.elf, elvenScout, null, 3, ClassType2.Rogue, "Серебрянный рейнджер"),
    elvenMage(25, true, Race.elf, null, null, 1, null, "Светлый мистик"),
    elvenWizard(26, true, Race.elf, elvenMage, null, 2, null, "Светлый маг"),
    spellsinger(27, true, Race.elf, elvenWizard, null, 3, ClassType2.Wizard, "Певец заклинаний"),
    elementalSummoner(28, true, Race.elf, elvenWizard, null, 3, ClassType2.Summoner, "Последователь стихий"),
    oracle(29, true, Race.elf, elvenMage, null, 2, null, "Оракул Евы"),
    elder(30, true, Race.elf, oracle, null, 3, ClassType2.Healer, "Мудрец Евы"),
    darkFighter(31, false, Race.darkelf, null, null, 1, null, "Темный воин"),
    palusKnight(32, false, Race.darkelf, darkFighter, null, 2, null, "Темный рыцарь"),
    shillienKnight(33, false, Race.darkelf, palusKnight, null, 3, ClassType2.Knight, "Рыцарь Шилен"),
    bladedancer(34, false, Race.darkelf, palusKnight, null, 3, ClassType2.Enchanter, "Танцор смерти"),
    assassin(35, false, Race.darkelf, darkFighter, null, 2, null, "Ассасин"),
    abyssWalker(36, false, Race.darkelf, assassin, null, 3, ClassType2.Rogue, "Странник бездны"),
    phantomRanger(37, false, Race.darkelf, assassin, null, 3, ClassType2.Rogue, "Призрачный рейнджер"),
    darkMage(38, true, Race.darkelf, null, null, 1, null, "Темный мистик"),
    darkWizard(39, true, Race.darkelf, darkMage, null, 2, null, "Темный маг"),
    spellhowler(40, true, Race.darkelf, darkWizard, null, 3, ClassType2.Wizard, "Заклинатель ветра"),
    phantomSummoner(41, true, Race.darkelf, darkWizard, null, 3, ClassType2.Summoner, "Последователь тьмы"),
    shillienOracle(42, true, Race.darkelf, darkMage, null, 2, null, "Оракл Шилен"),
    shillienElder(43, true, Race.darkelf, shillienOracle, null, 3, ClassType2.Healer, "Мудрец Шилен"),
    orcFighter(44, false, Race.orc, null, null, 1, null, "Орк боец"),
    orcRaider(45, false, Race.orc, orcFighter, null, 2, null, "Налетчик"),
    destroyer(46, false, Race.orc, orcRaider, null, 3, ClassType2.Warrior, "Разрушитель"),
    orcMonk(47, false, Race.orc, orcFighter, null, 2, null, "Монах"),
    tyrant(48, false, Race.orc, orcMonk, null, 3, ClassType2.Warrior, "Отшельник"),
    orcMage(49, true, Race.orc, null, null, 1, null, "Адепт"),
    orcShaman(50, true, Race.orc, orcMage, null, 2, null, "Шаман"),
    overlord(51, true, Race.orc, orcShaman, null, 3, ClassType2.Enchanter, "Верховный шаман"),
    warcryer(52, true, Race.orc, orcShaman, null, 3, ClassType2.Enchanter, "Вестник войны"),
    dwarvenFighter(53, false, Race.dwarf, null, null, 1, null, "Подмастерье"),
    scavenger(54, false, Race.dwarf, dwarvenFighter, null, 2, null, "Собиратель"),
    bountyHunter(55, false, Race.dwarf, scavenger, null, 3, ClassType2.Warrior, "Охотник за наградой"),
    artisan(56, false, Race.dwarf, dwarvenFighter, null, 2, null, "Ремесленник"),
    warsmith(57, false, Race.dwarf, artisan, null, 3, ClassType2.Warrior, "Кузнец"),
    /*
     * Dummy Entries (id's already in decimal format)
     * btw FU NCSoft for the amount of work you put me
     * through to do this!!
     * <START>
     */
    multibuffer(58, true, Race.human, null, null, 1, null, "Мультибафер"),
    dummyEntry2(59, false, null, null, null, 0, null, null),
    dummyEntry3(60, false, null, null, null, 0, null, null),
    dummyEntry4(61, false, null, null, null, 0, null, null),
    dummyEntry5(62, false, null, null, null, 0, null, null),
    dummyEntry6(63, false, null, null, null, 0, null, null),
    dummyEntry7(64, false, null, null, null, 0, null, null),
    dummyEntry8(65, false, null, null, null, 0, null, null),
    dummyEntry9(66, false, null, null, null, 0, null, null),
    dummyEntry10(67, false, null, null, null, 0, null, null),
    dummyEntry11(68, false, null, null, null, 0, null, null),
    dummyEntry12(69, false, null, null, null, 0, null, null),
    dummyEntry13(70, false, null, null, null, 0, null, null),
    dummyEntry14(71, false, null, null, null, 0, null, null),
    dummyEntry15(72, false, null, null, null, 0, null, null),
    dummyEntry16(73, false, null, null, null, 0, null, null),
    dummyEntry17(74, false, null, null, null, 0, null, null),
    dummyEntry18(75, false, null, null, null, 0, null, null),
    dummyEntry19(76, false, null, null, null, 0, null, null),
    dummyEntry20(77, false, null, null, null, 0, null, null),
    dummyEntry21(78, false, null, null, null, 0, null, null),
    dummyEntry22(79, false, null, null, null, 0, null, null),
    dummyEntry23(80, false, null, null, null, 0, null, null),
    dummyEntry24(81, false, null, null, null, 0, null, null),
    dummyEntry25(82, false, null, null, null, 0, null, null),
    dummyEntry26(83, false, null, null, null, 0, null, null),
    dummyEntry27(84, false, null, null, null, 0, null, null),
    dummyEntry28(85, false, null, null, null, 0, null, null),
    dummyEntry29(86, false, null, null, null, 0, null, null),
    dummyEntry30(87, false, null, null, null, 0, null, null),
    /*
     * <END>
     * Of Dummy entries
     */
    duelist(88, false, Race.human, gladiator, null, 4, ClassType2.Warrior, "Дуелист"),
    dreadnought(89, false, Race.human, warlord, null, 4, ClassType2.Warrior, "Полководец"),
    phoenixKnight(90, false, Race.human, paladin, null, 4, ClassType2.Knight, "Рыцарь феникса"),
    hellKnight(91, false, Race.human, darkAvenger, null, 4, ClassType2.Knight, "Рыцарь ада"),
    sagittarius(92, false, Race.human, hawkeye, null, 4, ClassType2.Rogue, "Снайпер"),
    adventurer(93, false, Race.human, treasureHunter, null, 4, ClassType2.Rogue, "Авантюрист"),
    archmage(94, true, Race.human, sorceror, null, 4, ClassType2.Wizard, "Архимаг"),
    soultaker(95, true, Race.human, necromancer, null, 4, ClassType2.Wizard, "Пожиратель душ"),
    arcanaLord(96, true, Race.human, warlock, null, 4, ClassType2.Summoner, "Еретик"),
    cardinal(97, true, Race.human, bishop, null, 4, ClassType2.Healer, "Кардинал"),
    hierophant(98, true, Race.human, prophet, null, 4, ClassType2.Enchanter, "Апостол"),
    evaTemplar(99, false, Race.elf, templeKnight, null, 4, ClassType2.Knight, "Храмовник Евы"),
    swordMuse(100, false, Race.elf, swordSinger, null, 4, ClassType2.Enchanter, "Виртуоз"),
    windRider(101, false, Race.elf, plainsWalker, null, 4, ClassType2.Rogue, "Странник ветра"),
    moonlightSentinel(102, false, Race.elf, silverRanger, null, 4, ClassType2.Rogue, "Страж лун света"),
    mysticMuse(103, true, Race.elf, spellsinger, null, 4, ClassType2.Wizard, "Магистр магии"),
    elementalMaster(104, true, Race.elf, elementalSummoner, null, 4, ClassType2.Summoner, "Мастер стихий"),
    evaSaint(105, true, Race.elf, elder, null, 4, ClassType2.Healer, "Жрец Евы"),
    shillienTemplar(106, false, Race.darkelf, shillienKnight, null, 4, ClassType2.Knight, "Храмовник Шилен"),
    spectralDancer(107, false, Race.darkelf, bladedancer, null, 4, ClassType2.Enchanter, "Призрачный танцор"),
    ghostHunter(108, false, Race.darkelf, abyssWalker, null, 4, ClassType2.Rogue, "Призрачный охотник"),
    ghostSentinel(109, false, Race.darkelf, phantomRanger, null, 4, ClassType2.Rogue, "Страж теней"),
    stormScreamer(110, true, Race.darkelf, spellhowler, null, 4, ClassType2.Wizard, "Повелитель бури"),
    spectralMaster(111, true, Race.darkelf, phantomSummoner, null, 4, ClassType2.Summoner, "Владыка теней"),
    shillienSaint(112, true, Race.darkelf, shillienElder, null, 4, ClassType2.Healer, "Жрец Шилен"),
    titan(113, false, Race.orc, destroyer, null, 4, ClassType2.Warrior, "Титан"),
    grandKhauatari(114, false, Race.orc, tyrant, null, 4, ClassType2.Warrior, "Аватар"),
    dominator(115, true, Race.orc, overlord, null, 4, ClassType2.Enchanter, "Деспот"),
    doomcryer(116, true, Race.orc, warcryer, null, 4, ClassType2.Enchanter, "Пророк"),
    fortuneSeeker(117, false, Race.dwarf, bountyHunter, null, 4, ClassType2.Warrior, "Кладоискатель"),
    maestro(118, false, Race.dwarf, warsmith, null, 4, ClassType2.Warrior, "Мастер"),
    dummyEntry31(119, false, null, null, null, 0, null, null),
    dummyEntry32(120, false, null, null, null, 0, null, null),
    dummyEntry33(121, false, null, null, null, 0, null, null),
    dummyEntry34(122, false, null, null, null, 0, null, null),
    /**
     * Kamael
     */
    maleSoldier(123, false, Race.kamael, null, null, 1, null, "Грешник"),
    femaleSoldier(124, false, Race.kamael, null, null, 1, null, "Грешница"),
    trooper(125, false, Race.kamael, maleSoldier, null, 2, null, "Солдат"),
    warder(126, false, Race.kamael, femaleSoldier, null, 2, null, "Надзиратель"),
    berserker(127, false, Race.kamael, trooper, null, 3, ClassType2.Warrior, "Берсерк"),
    maleSoulbreaker(128, false, Race.kamael, trooper, null, 3, ClassType2.Warrior, "Палач"),
    femaleSoulbreaker(129, false, Race.kamael, warder, null, 3, ClassType2.Warrior, "Палач"),
    arbalester(130, false, Race.kamael, warder, null, 3, ClassType2.Rogue, "Арбалетчик"),
    doombringer(131, false, Race.kamael, berserker, null, 4, ClassType2.Warrior, "Каратель"),
    maleSoulhound(132, false, Race.kamael, maleSoulbreaker, null, 4, ClassType2.Warrior, "Инквизитор"),
    femaleSoulhound(133, false, Race.kamael, femaleSoulbreaker, null, 4, ClassType2.Warrior, "Инквизитор"),
    trickster(134, false, Race.kamael, arbalester, null, 4, ClassType2.Rogue, "Диверсант"),
    inspector(135, false, Race.kamael, trooper, warder, 3, ClassType2.Enchanter, "Инспектор"),
    judicator(136, false, Race.kamael, inspector, null, 4, ClassType2.Enchanter, "Арбитр");
    
    
    
    public static final ClassId[] VALUES = values();
    /**
     * The Identifier of the Class<?>
     */
    private final int _id;
    /**
     * True if the class is a mage class
     */
    private final boolean _isMage;
    /**
     * The Race object of the class
     */
    private final Race _race;
    /**
     * The parent ClassId for male or null if this class is a root
     */
    private final ClassId _parent;
    /**
     * The parent2 ClassId for female or null if parent2 class is parent
     */
    private final ClassId _parent2;
    private final ClassType2 _type2;
    private final int _level;

    private final String _nameRu;
    
    /**
     * Constructor<?> of ClassId.<BR><BR>
     */
    private ClassId(int id, boolean isMage, Race race, ClassId parent, ClassId parent2, int level, ClassType2 classType2, String nameRu) {
        _id = id;
        _isMage = isMage;
        _race = race;
        _parent = parent;
        _parent2 = parent2;
        _level = level;
        _type2 = classType2;
        _nameRu = nameRu;
    }

    /**
     * Return the Identifier of the Class.<BR><BR>
     */
    public final int getId() {
        return _id;
    }

    /**
     * Return True if the class is a mage class.<BR><BR>
     */
    public final boolean isMage() {
        return _isMage;
    }

    /**
     * Return the Race object of the class.<BR><BR>
     */
    public final Race getRace() {
        return _race;
    }

    /**
     * Return True if this Class<?> is a child of the selected ClassId.<BR><BR>
     *
     * @param cid The parent ClassId to check
     */
    public final boolean childOf(ClassId cid) {
        if (_parent == null) {
            return false;
        }

        if (_parent == cid || _parent2 == cid) {
            return true;
        }

        return _parent.childOf(cid);

    }

    /**
     * Return True if this Class<?> is equal to the selected ClassId or a child
     * of the selected ClassId.<BR><BR>
     *
     * @param cid The parent ClassId to check
     */
    public final boolean equalsOrChildOf(ClassId cid) {
        return this == cid || childOf(cid);
    }

    /**
     * Return the child level of this Class<?> (0=root, 1=child leve
     * 1...).<BR><BR>
     *
     * @param cid The parent ClassId to check
     */
    public final int level() {
        if (_parent == null) {
            return 0;
        }

        return 1 + _parent.level();
    }

    public final ClassId getParent(int sex) {
        return sex == 0 || _parent2 == null ? _parent : _parent2;
    }

    public final int getLevel() {
        return _level;
    }

    public ClassType2 getType2() {
        return _type2;
    }
    
    public final String getNameRu() {
        return _nameRu;
    }
}
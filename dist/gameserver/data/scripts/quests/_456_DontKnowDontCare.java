package quests;

import org.apache.commons.lang3.ArrayUtils;
import l2p.commons.util.Rnd;
import l2p.gameserver.model.GameObjectsStorage;
import l2p.gameserver.model.instances.NpcInstance;
import l2p.gameserver.model.quest.Quest;
import l2p.gameserver.model.quest.QuestState;
import l2p.gameserver.scripts.ScriptFile;

/**
 * ВНИМАНИЕ! Данный квест можно выполнять не только группой, но и командным
 * каналом, все персонажи в командном канале имеют шанс получить квестовые
 * предметы. После убийства боссов будут появляться специальные НПЦ - мертвые
 * тела боссов, для получения квестовых предметов необходимо будет "поговорить"
 * с этим НПЦ.
 */
public class _456_DontKnowDontCare extends Quest implements ScriptFile {

    private static final int[] SeparatedSoul = {32864, 32865, 32866, 32867, 32868, 32869, 32870};
    private static final int DrakeLordsEssence = 17251;
    private static final int BehemothLeadersEssence = 17252;
    private static final int DragonBeastsEssence = 17253;
    private static final int DrakeLord = 25725;
    private static final int BehemothLeader = 25726;
    private static final int DragonBeast = 25727;
    private static final int DrakeLordCorpse = 32884;
    private static final int BehemothLeaderCorpse = 32885;
    private static final int DragonBeastCorpse = 32886;
    //Reward set

    // Rewards: chance/id
    private static final int[][] REWARDS = {
        // armor, chance 0.1% each
        {100, 15743, 1},
        {100, 15744, 1},
        {100, 15745, 1},
        {100, 15746, 1},
        {100, 15747, 1},
        {100, 15748, 1},
        {100, 15749, 1},
        {100, 15750, 1},
        {100, 15751, 1},
        {100, 15752, 1},
        {100, 15753, 1},
        {100, 15754, 1},
        {100, 15755, 1},
        {100, 15756, 1},
        {100, 15757, 1},
        {100, 15758, 1},
        {100, 15759, 1},
        // accessory, chance 0.1% each
        {100, 15763, 1},
        {100, 15764, 1},
        {100, 15765, 1},
        // weapons, chance 0.05% each
        {50, 15558, 1},
        {50, 15559, 1},
        {50, 15560, 1},
        {50, 15561, 1},
        {50, 15562, 1},
        {50, 15563, 1},
        {50, 15564, 1},
        {50, 15565, 1},
        {50, 15566, 1},
        {50, 15567, 1},
        {50, 15568, 1},
        {50, 15569, 1},
        {50, 15570, 1},
        {50, 15571, 1},
        // BEWS, chance 0.55%
        {550, 6577, 1},
        // BEAS, chance 1%
        {1000, 6578, 1},
        // attributes, chance 5%
        {5000, 9552, 1},
        {5000, 9553, 1},
        {5000, 9554, 1},
        {5000, 9555, 1},
        {5000, 9556, 1},
        {5000, 9557, 1},
        // EWS, chance 1.75%
        {1750, 959, 1},
        // gemstone s
        {100000, 2134, 3}
    };

    private static final int CHANCE = 5;

    public _456_DontKnowDontCare() {
        super(PARTY_ALL);
        addStartNpc(SeparatedSoul);
        addTalkId(DrakeLordCorpse, BehemothLeaderCorpse, DragonBeastCorpse);
        addQuestItem(DrakeLordsEssence, BehemothLeadersEssence, DragonBeastsEssence);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        if (event.equalsIgnoreCase("sepsoul_q456_05.htm")) {
            st.setState(STARTED);
            st.setCond(1);
            st.playSound(SOUND_ACCEPT);
        } else if (event.equalsIgnoreCase("take_essense")) {
            if (st.getCond() == 1) {
                switch (npc.getNpcId()) {
                    case DrakeLordCorpse:
                        if (st.getQuestItemsCount(DrakeLordsEssence) < 1) {
                            st.giveItems(DrakeLordsEssence, 1);
                        }
                        break;
                    case BehemothLeaderCorpse:
                        if (st.getQuestItemsCount(BehemothLeadersEssence) < 1) {
                            st.giveItems(BehemothLeadersEssence, 1);
                        }
                        break;
                    case DragonBeastCorpse:
                        if (st.getQuestItemsCount(DragonBeastsEssence) < 1) {
                            st.giveItems(DragonBeastsEssence, 1);
                        }
                        break;
                    default:
                        break;
                }
                if (st.getQuestItemsCount(DrakeLordsEssence) > 0 && st.getQuestItemsCount(BehemothLeadersEssence) > 0 && st.getQuestItemsCount(DragonBeastsEssence) > 0) {
                    st.setCond(2);
                }
            }
            return null;
        } else if (event.equalsIgnoreCase("drake")) {
            if ((st.getQuestItemsCount(DrakeLordsEssence) < 1) && (st.getCond() == 1)) {
                if (st.getInt("KB456_DRAKE") != 1) {
                    if (Rnd.chance(CHANCE)) {
                        st.giveItems(DrakeLordsEssence, 1, false);
                        htmltext = "32864-13.htm";
                    } else {
                        htmltext = "32864-14.htm";
                    }
                    if ((st.getQuestItemsCount(DrakeLordsEssence) >= 1) && (st.getQuestItemsCount(BehemothLeadersEssence) >= 1) && (st.getQuestItemsCount(DragonBeastsEssence) >= 1)) {
                        st.set("cond", "2");
                        st.playSound(SOUND_MIDDLE);
                    }
                    st.set("KB456_DRAKE", 1);
                }
            } else {
                htmltext = "32864-10.htm";
            }
        } else if (event.equalsIgnoreCase("behemoth")) {
            if ((st.getQuestItemsCount(BehemothLeadersEssence) < 1) && (st.getCond() == 1)) {
                if (st.getInt("KB456_BEHEMOTH") != 1) {
                    if (Rnd.chance(CHANCE)) {
                        st.giveItems(BehemothLeadersEssence, 1, false);
                        htmltext = "32864-13.htm";
                    } else {
                        htmltext = "32864-14.htm";
                    }
                    if ((st.getQuestItemsCount(DrakeLordsEssence) >= 1) && (st.getQuestItemsCount(BehemothLeadersEssence) >= 1) && (st.getQuestItemsCount(DragonBeastsEssence) >= 1)) {
                        st.set("cond", "2");
                        st.playSound(SOUND_MIDDLE);
                    }
                    st.set("KB456_BEHEMOTH", 1);
                }
            } else {
                htmltext = "32864-10.htm";
            }
        } else if (event.equalsIgnoreCase("dragon")) {
            if ((st.getQuestItemsCount(DragonBeastsEssence) < 1) && (st.getCond() == 1)) {
                if (st.getInt("KB456_DRAGON") != 1) {
                    if (Rnd.chance(CHANCE)) {
                        st.giveItems(DragonBeastsEssence, 1, false);
                        htmltext = "32864-13.htm";
                    } else {
                        htmltext = "32864-14.htm";
                    }
                    if ((st.getQuestItemsCount(DrakeLordsEssence) >= 1) && (st.getQuestItemsCount(BehemothLeadersEssence) >= 1) && (st.getQuestItemsCount(DragonBeastsEssence) >= 1)) {
                        st.set("cond", "2");
                        st.playSound(SOUND_MIDDLE);
                    }
                    st.set("KB456_DRAGON", 1);
                }
            } else {
                htmltext = "32864-10.htm";
            }
        } else if (event.equalsIgnoreCase("sepsoul_q456_08.htm")) {
            st.takeAllItems(DrakeLordsEssence);
            st.takeAllItems(BehemothLeadersEssence);
            st.takeAllItems(DragonBeastsEssence);

            int random = Rnd.get(100000);
            for (int[] REWARDS1 : REWARDS) {
                random -= REWARDS1[0];
                if (random <= 0) {
                    st.giveItems(REWARDS1[1], REWARDS1[2]);
                    break;
                }
            }

            st.setState(COMPLETED);
            st.playSound(SOUND_FINISH);
            st.exitCurrentQuest(this);
        }

        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        if (ArrayUtils.contains(SeparatedSoul, npc.getNpcId())) {
            switch (st.getState()) {
                case CREATED:
                    if (st.isNowAvailable()) {
                        if (st.getPlayer().getLevel() >= 80) {
                            htmltext = "sepsoul_q456_01.htm";
                        } else {
                            htmltext = "sepsoul_q456_00.htm";
                            st.exitCurrentQuest(true);
                        }
                    } else {
                        htmltext = "sepsoul_q456_00a.htm";
                    }
                    break;
                case STARTED:
                    if (cond == 1) {
                        htmltext = "sepsoul_q456_06.htm";
                    } else if (cond == 2) {
                        htmltext = "sepsoul_q456_07.htm";
                    }
                    break;
            }
        } else if (npcId == DrakeLordCorpse) {
            if (st.getCond() == 1) {
                htmltext = "drake.htm";
            } else {
                htmltext = "drake_no.htm";
            }
        } else if (npcId == BehemothLeaderCorpse) {
            if (st.getCond() == 1) {
                htmltext = "behemoth.htm";
            } else {
                htmltext = "behemoth_no.htm";
            }
        } else if (npcId == DragonBeastCorpse) {
            if (st.getCond() == 1) {
                htmltext = "dragon.htm";
            } else {
                htmltext = "dragon_no.htm";
            }
        }

        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        if (st.getCond() == 1) {
            if (npcId == DrakeLord) {
                NpcInstance isQuest = GameObjectsStorage.getByNpcId(DrakeLordCorpse);
                if (isQuest == null) {
                    st.addSpawn(DrakeLordCorpse, 120000);
                    st.playSound(SOUND_MIDDLE);
                    st.startQuestTimer("DRAKE_LEADER_Fail", 120000);
                    st.set("KB456_DRAKE", 0);
                }
            } else if (npcId == BehemothLeader) {
                NpcInstance isQuest = GameObjectsStorage.getByNpcId(BehemothLeaderCorpse);
                if (isQuest == null) {
                    st.addSpawn(BehemothLeaderCorpse, 120000);
                    st.playSound(SOUND_MIDDLE);
                    st.startQuestTimer("BEHEMOTH_LEADER_Fail", 120000);
                    st.set("KB456_BEHEMOTH", 0);
                }
            } else if (npcId == DragonBeast) {
                NpcInstance isQuest = GameObjectsStorage.getByNpcId(DragonBeastCorpse);
                if (isQuest == null) {
                    st.addSpawn(DragonBeastCorpse, 120000);
                    st.playSound(SOUND_MIDDLE);
                    st.startQuestTimer("DRAGON_BEAST_Fail", 120000);
                    st.set("KB456_DRAGON", 0);
                }
            } else {
                st.playSound(SOUND_ITEMGET);
            }
        }
        return null;
    }

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }
}

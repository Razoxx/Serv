package l2p.gameserver.taskmanager;

import java.util.concurrent.Future;

import l2p.commons.threading.RunnableImpl;
import l2p.commons.threading.SteppingRunnableQueueManager;
import l2p.commons.util.Rnd;
import l2p.gameserver.Config;
import l2p.gameserver.ThreadPoolManager;
import l2p.gameserver.dao.AccountBonusDAO;
import l2p.gameserver.model.Player;
import l2p.gameserver.model.actor.instances.player.Bonus;
import l2p.gameserver.model.instances.NpcInstance;
import l2p.gameserver.serverpackets.components.CustomMessage;
import l2p.gameserver.serverpackets.ExBR_PremiumState;
import l2p.gameserver.serverpackets.ExShowScreenMessage;
import l2p.gameserver.serverpackets.ExShowScreenMessage.ScreenMessageAlign;

public class LazyPrecisionTaskManager extends SteppingRunnableQueueManager {

    private static final LazyPrecisionTaskManager _instance = new LazyPrecisionTaskManager();

    public static final LazyPrecisionTaskManager getInstance() {
        return _instance;
    }

    private LazyPrecisionTaskManager() {
        super(1000L);
        ThreadPoolManager.getInstance().scheduleAtFixedRate(this, 1000L, 1000L);
        //Очистка каждые 60 секунд
        ThreadPoolManager.getInstance().scheduleAtFixedRate(new RunnableImpl() {
            @Override
            public void runImpl() throws Exception {
                LazyPrecisionTaskManager.this.purge();
            }
        }, 60000L, 60000L);
    }

    public Future<?> addPCCafePointsTask(final Player player) {
        long delay = Config.ALT_PCBANG_POINTS_DELAY * 60000L;

        return scheduleAtFixedRate(new RunnableImpl() {
            @Override
            public void runImpl() throws Exception {
                if (player.isInOfflineMode() || player.getLevel() < Config.ALT_PCBANG_POINTS_MIN_LVL) {
                    return;
                }

                player.addPcBangPoints(Config.ALT_PCBANG_POINTS_BONUS, Config.ALT_PCBANG_POINTS_BONUS_DOUBLE_CHANCE > 0 && Rnd.chance(Config.ALT_PCBANG_POINTS_BONUS_DOUBLE_CHANCE));
            }
        }, delay, delay);
    }

    public Future<?> addVitalityRegenTask(final Player player) {
        long delay = 60000L;

        return scheduleAtFixedRate(new RunnableImpl() {
            @Override
            public void runImpl() throws Exception {
                if (player.isInOfflineMode() || !player.isInPeaceZone()) {
                    return;
                }

                player.setVitality(player.getVitality() + 1); // одно очко раз в минуту
            }
        }, delay, delay);
    }

    public Future<?> startBonusExpirationTask(final Player player) {
        long delay = player.getBonus().getBonusExpire() * 1000L - System.currentTimeMillis();

        return schedule(new RunnableImpl() {
            @Override
            public void runImpl() throws Exception {
                player.getBonus().setBonusId(0);

                if (player.getParty() != null) {
                    player.getParty().recalculatePartyData();
                }

                player.getListeners().onEndPremium(player);
                
                String msg = new CustomMessage("scripts.services.RateBonus.LuckEnded", player).toString();
                player.sendPacket(new ExShowScreenMessage(msg, 10000, ScreenMessageAlign.TOP_CENTER, true), new ExBR_PremiumState(player, false));
                player.sendMessage(msg);

                if (Config.SERVICES_RATE_TYPE == Bonus.BONUS_GLOBAL_ON_GAMESERVER) {
                    AccountBonusDAO.getInstance().endDate(player.getAccountName(), 0);
                }
            }
        }, delay);
    }

    public Future<?> addNpcAnimationTask(final NpcInstance npc) {
        return scheduleAtFixedRate(new RunnableImpl() {
            @Override
            public void runImpl() throws Exception {
                if (npc.isVisible() && !npc.isActionsDisabled() && !npc.isMoving && !npc.isInCombat()) {
                    npc.onRandomAnimation();
                }
            }
        }, 1000L, Rnd.get(Config.MIN_NPC_ANIMATION, Config.MAX_NPC_ANIMATION) * 1000L);
    }
}

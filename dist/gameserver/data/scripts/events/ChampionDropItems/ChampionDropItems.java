package events.ChampionDropItems;

import l2p.commons.util.Rnd;
import l2p.gameserver.Config;
import l2p.gameserver.listener.actor.OnDeathListener;
import l2p.gameserver.model.Creature;
import l2p.gameserver.model.actor.listener.CharListenerList;
import l2p.gameserver.model.instances.MonsterInstance;
import l2p.gameserver.scripts.Functions;
import l2p.gameserver.scripts.ScriptFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChampionDropItems extends Functions implements ScriptFile, OnDeathListener {

    private static final Logger _log = LoggerFactory.getLogger(ChampionDropItems.class);

    private static final boolean ChampionCDItemsAllowMinMaxPlayerLvl = Config.ChampionCDItemsAllowMinMaxPlayerLvl;
    private static final boolean ChampionCDItemsAllowMinMaxMobLvl = Config.ChampionCDItemsAllowMinMaxMobLvl;

    private static final int[] ChampionCDItemsId = Config.ChampionCDItemsId;
    private static final int[] ChampionCDItemsCountDropMin = Config.ChampionCDItemsCountDropMin;
    private static final int[] ChampionCDItemsCountDropMax = Config.ChampionCDItemsCountDropMax;
    private static final double[] ChampionCustomDropItemsChance = Config.ChampionCustomDropItemsChance;

    private static final int ChampionCDItemsMinPlayerLvl = Config.ChampionCDItemsMinPlayerLvl;
    private static final int ChampionCDItemsMaxPlayerLvl = Config.ChampionCDItemsMaxPlayerLvl;
    private static final int ChampionCDItemsMinMobLvl = Config.ChampionCDItemsMinMobLvl;
    private static final int ChampionCDItemsMaxMobLvl = Config.ChampionCDItemsMaxMobLvl;

    private static boolean _active = false;

    @Override
    public void onLoad() {
        CharListenerList.addGlobal(this);
        if (Config.AllowChampionCustomDropItems) {
            _active = true;
            _log.info("Loaded ChampionDropItems: ChampionDropItems [state: activated]");
        } else {
            _log.info("Loaded ChampionDropItems: ChampionDropItems [state: deactivated]");
        }
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    @Override
    public void onDeath(Creature cha, Creature killer) {
        if (cha.isChampion()) {
            if ((ChampionCDItemsAllowMinMaxPlayerLvl && checkValidate(killer, cha, true, false)) && (ChampionCDItemsAllowMinMaxMobLvl && checkValidate(killer, cha, false, true))) {
                dropItemChamp(cha, killer);
            } else if ((ChampionCDItemsAllowMinMaxPlayerLvl && checkValidate(killer, cha, true, false)) && !ChampionCDItemsAllowMinMaxMobLvl) {
                dropItemChamp(cha, killer);
            } else if (!ChampionCDItemsAllowMinMaxPlayerLvl && (ChampionCDItemsAllowMinMaxMobLvl && checkValidate(killer, cha, false, true))) {
                dropItemChamp(cha, killer);
            } else if (!ChampionCDItemsAllowMinMaxPlayerLvl && !ChampionCDItemsAllowMinMaxMobLvl) {
                dropItemChamp(cha, killer);
            }

        }

    }

    private boolean checkValidate(Creature killer, Creature mob, boolean lvlPlayer, boolean lvlMob) {
        if (mob == null || killer == null) {
            return false;
        }

        if (lvlPlayer && (killer.getLevel() >= ChampionCDItemsMinPlayerLvl && killer.getLevel() <= ChampionCDItemsMaxPlayerLvl)) {
            return true;
        }

        if (lvlMob && (mob.getLevel() >= ChampionCDItemsMinMobLvl && mob.getLevel() <= ChampionCDItemsMaxMobLvl)) {
            return true;
        }

        return false;
    }

    private void dropItemChamp(Creature cha, Creature killer) {
        if (_active && SimpleCheckDrop(cha, killer)) {
            for (int i = 0; i < ChampionCDItemsId.length; i++) {
                if (Rnd.chance(ChampionCustomDropItemsChance[i] * killer.getPlayer().getRateItems() * ((MonsterInstance) cha).getTemplate().rateHp)) {
                    ((MonsterInstance) cha).dropItem(killer.getPlayer(), ChampionCDItemsId[i], Rnd.get(ChampionCDItemsCountDropMin[i], ChampionCDItemsCountDropMax[i]));
                }
            }
        }
    }
}

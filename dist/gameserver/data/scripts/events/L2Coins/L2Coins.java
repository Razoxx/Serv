package events.L2Coins;

import java.util.ArrayList;
import java.util.List;
import l2p.gameserver.Announcements;
import l2p.gameserver.Config;
import l2p.gameserver.model.Creature;
import l2p.gameserver.model.Player;
import l2p.gameserver.model.SimpleSpawner;
import l2p.gameserver.model.instances.MonsterInstance;
import l2p.gameserver.model.instances.NpcInstance;
import l2p.gameserver.scripts.Functions;
import l2p.gameserver.scripts.ScriptFile;
import l2p.gameserver.utils.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class L2Coins extends Functions
        implements ScriptFile {

    private static final Logger _log = LoggerFactory.getLogger(L2Coins.class);
    private static long MOUSE_COIN_CHANCE = Config.EVENT_MOUSE_COIN_CHANCE;
    private static int MOUSE_COIN = Config.EVENT_MOUSE_COIN;
    private static int MOUSE_COIN_MIN_COUNT = Config.EVENT_MOUSE_COIN_MIN_COUNT;
    private static int MOUSE_COIN_MAX_COUNT = Config.EVENT_MOUSE_COIN_MAX_COUNT;
    private static final int EVENT_MANAGER_ID = 36608;
    private static final int EVENT_THREE_ID = 36609;
    private static List<SimpleSpawner> _spawns = new ArrayList();
    private static boolean _active = false;

    private void spawnEventManagers() {
        int[][] EVENT_MANAGERS = {{82247, 148605, -3472, 0}, {81923, 148916, -3482, 14902}, {81921, 148298, -3482, 47930}};

        SpawnNPCs(36608, EVENT_MANAGERS, _spawns);
    }

    private void spawnThree() {
        int[][] EVENT_THREE = {{82168, 148856, -3464, 0}, {81672, 148856, -3464, 0}, {81672, 148360, -3464, 0}, {82168, 148360, -3464, 0}};

        SpawnNPCs(36609, EVENT_THREE, _spawns);
    }

    private void unSpawnEventManagers() {
        deSpawnNPCs(_spawns);
    }

    private static boolean isActive() {
        return IsActive("L2Coins");
    }

    public void startEvent() {
        Player player = getSelf();
        if (!player.getPlayerAccess().IsEventGm) {
            return;
        }
        if (SetActive("L2Coins", true)) {
            spawnEventManagers();
            spawnThree();
            _log.info("Event 'L2Coins' started.");
            Announcements.getInstance().announceByCustomMessage("scripts.events.coins.AnnounceEventStarted", null);
        } else {
            player.sendMessage("Event 'L2Coins' already started.");
        }
        _active = true;

        show("admin/events/events.htm", player);
    }

    public void stopEvent() {
        Player player = getSelf();
        if (!player.getPlayerAccess().IsEventGm) {
            return;
        }
        if (SetActive("L2Coins", false)) {
            unSpawnEventManagers();
            _log.info("Event 'L2Coins' stopped.");
            Announcements.getInstance().announceByCustomMessage("scripts.events.coins.AnnounceEventStoped", null);
        } else {
            player.sendMessage("Event 'L2Coins' not started.");
        }
        _active = false;

        show("admin/events/events.htm", player);
    }

    public static void OnPlayerEnter(Player player) {
        if (_active) {
            Announcements.getInstance().announceToPlayerByCustomMessage(player, "scripts.events.coins.AnnounceEventStarted", null);
        }
    }

    @Override
    public void onLoad() {
        if (isActive()) {
            _active = true;
            spawnEventManagers();
            spawnThree();
            _log.info("Loaded Event: L2Coins [state: activated]");
        } else {
            _log.info("Loaded Event: L2Coins [state: deactivated]");
        }
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public static void onDeath(Creature character, Creature killer) {
        if ((_active) && (SimpleCheckDrop(character, killer)) && (character.getLevel() >= 80) && (character.isMonster()) && (!character.isRaid()) && (Math.abs(character.getLevel() - killer.getLevel()) < 3)) {
            ((NpcInstance) character).dropItem(killer.getPlayer(), MOUSE_COIN, Util.rollDrop(MOUSE_COIN_MIN_COUNT, MOUSE_COIN_MAX_COUNT, MOUSE_COIN_CHANCE * killer.getPlayer().getRateItems() * ((MonsterInstance) character).getTemplate().rateHp * 10000.0D, true));
        }
    }
}
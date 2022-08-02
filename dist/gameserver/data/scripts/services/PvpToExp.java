package services;

import l2p.commons.util.Rnd;
import l2p.gameserver.Config;
import l2p.gameserver.listener.actor.OnDeathListener;
import l2p.gameserver.listener.actor.player.OnPlayerEnterListener;
import l2p.gameserver.model.Creature;
import l2p.gameserver.model.GameObjectsStorage;
import l2p.gameserver.model.Player;
import l2p.gameserver.model.Skill;
import l2p.gameserver.model.actor.listener.CharListenerList;
import l2p.gameserver.model.items.ItemInstance;
import l2p.gameserver.scripts.ScriptFile;
import l2p.gameserver.serverpackets.SystemMessage2;
import l2p.gameserver.tables.SkillTable;
import l2p.gameserver.taskmanager.Task;
import l2p.gameserver.taskmanager.TaskManager;
import l2p.gameserver.taskmanager.TaskTypes;
import l2p.gameserver.utils.ItemFunctions;
import l2p.gameserver.utils.Location;
import l2p.gameserver.utils.Log;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PvpToExp extends Task implements ScriptFile, OnPlayerEnterListener, OnDeathListener {

    private static final Logger _log = LoggerFactory.getLogger(PvpToExp.class);
    private static final String NAME = "give_items";
    private static final int item_id_prem = 57;
    private static final int item_id = 57;

    // Rewards: chance/id
    private static final int[][] DROPITEMS = {
        // armor, chance 0.1% each
        {100, 31410, 1},
        {100, 31411, 1},
        {100, 31412, 1},
        {100, 31413, 1},
        {100, 31414, 1},
        {100, 31415, 1},
        {100, 31416, 1},
        {100, 31417, 1},
        {100, 31418, 1},
        {100, 31419, 1},
        {100, 31420, 1},
        {100, 31421, 1},
        {100, 31422, 1},
        {100, 31423, 1},
        {100, 31424, 1},
        {100, 31457, 1},
        {100, 31458, 1},
        {100, 31459, 1},
        {100, 31460, 1},
        {100, 31461, 1},
        {100, 31462, 1},
        {100, 31463, 1},
        {100, 31464, 1},
        {100, 31465, 1},
        {100, 31466, 1},
        {100, 31467, 1},
        {100, 31468, 1},
        {100, 31469, 1},
        {100, 31470, 1},
        // adena
        {75000, 57, 1000000}
    };

    @Override
    public void onLoad() {
        CharListenerList.addGlobal(this);
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    @Override
    public void onPlayerEnter(Player player) {
        Skill skill = SkillTable.getInstance().getInfo(251, 45);
        player.addSkill(skill);
        skill = SkillTable.getInstance().getInfo(252, 45);
        player.addSkill(skill);
        skill = SkillTable.getInstance().getInfo(253, 43);
        player.addSkill(skill);
        if (player.getVar(NAME) != null) {
            return;
        }
        if (player.hasBonus()) {

            ItemFunctions.addItem(player, item_id_prem, 1, true);
            player.setVar(NAME, 1, -1);

        } else {
            ItemFunctions.addItem(player, item_id, 1, true);
            player.setVar(NAME, 1, -1);
        }
    }

    @Override
    public void onDeath(Creature actor, Creature killer) {
        if (actor == null) {
            return;
        }

        if (killer == null) {
            return;
        }

        if (killer == actor) {
            return;
        }

        if (actor.getPvpFlag() <= 0) {
            return;
        }

        if (killer.isPlayer() && actor.isPlayer()) {
            int rate = 0;
            rate = actor.getPlayer().getVarInt("nascency");
            rate++;
            killer.addExpAndSp((2000000 * (rate)), 0);
            killer.broadcastCharInfo();

            int random = Rnd.get(100000);
            for (int[] REWARDS1 : DROPITEMS) {
                random -= REWARDS1[0];
                if (random <= 0) {
                    dropItem(killer.getPlayer(), actor.getPlayer(), REWARDS1[1], REWARDS1[2]);
                    break;
                }
            }
        }

    }

    public void dropItem(Player killer, Player actor, int itemId, long itemCount) {
        if (itemCount == 0 || killer == null) {
            return;
        }

        ItemInstance item;

        for (long i = 0; i < itemCount; i++) {
            item = ItemFunctions.createItem(itemId);
            // Set the Item quantity dropped if L2ItemInstance is stackable
            if (item.isStackable()) {
                i = itemCount; // Set so loop won't happent again
                item.setCount(itemCount); // Set item count
            }

            if (killer.isPlayable() && ((Config.AUTO_LOOT && Config.AUTO_LOOT_PK) || actor.isInFlyingTransform())) {
                killer.getPlayer().getInventory().addItem(item);
                Log.LogItem(actor, Log.Pickup, item);

                killer.getPlayer().sendPacket(SystemMessage2.obtainItems(item));
            } else {
                item.dropToTheGround(actor, Location.findAroundPosition(actor, Config.KARMA_RANDOM_DROP_LOCATION_LIMIT));
            }

        }
    }

    @Override
    public void initializate() {
        TaskManager.addUniqueTask(NAME, TaskTypes.TYPE_GLOBAL_TASK, "1", "00:00:00", StringUtils.EMPTY);
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void onTimeElapsed(TaskManager.ExecutedTask task) {
        for (Player player : GameObjectsStorage.getAllPlayersForIterate()) {
            player.unsetVar(NAME);
        }
    }

}

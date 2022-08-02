package npc.model;

import l2p.commons.util.Rnd;
import l2p.gameserver.model.Player;
import l2p.gameserver.model.instances.NpcInstance;
import l2p.gameserver.templates.npc.NpcTemplate;
import l2p.gameserver.utils.ItemFunctions;
import l2p.gameserver.utils.Location;
import l2p.gameserver.utils.NpcUtils;

/**
 * @author pchayka
 */
public final class DragonVortexInstance extends NpcInstance {

    private long _timer = 0L;
    private NpcInstance _boss;
    private Boolean _proverka = false;

    public DragonVortexInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (!canBypassCheck(player, this)) {
            return;
        }

        if (command.startsWith("request_boss")) {
            if (_timer + 30000 > System.currentTimeMillis()) {
                showChatWindow(player, "default/32871-3.htm");
                return;
            }

            if (_boss != null && _proverka) {
                showChatWindow(player, "default/32871-4.htm");
                return;
            }

            if (ItemFunctions.deleteItem(player, 17248, 1)) {
                int chance = Rnd.get(100);
                int bossToSpawn;
                int muscle_bomber = 25724;
                int shadow_summoner = 25722;
                int spike_slasher = 25723;
                int blackdagger_wing = 25721;
                int bleeding_fly = 25720;
                int dust_rider = 25719;
                int emerald_horn = 25718;

                if (chance < 3) {
                    bossToSpawn = muscle_bomber;
                } else if (chance < 8) {
                    bossToSpawn = shadow_summoner;
                } else if (chance < 15) {
                    bossToSpawn = spike_slasher;
                } else if (chance < 25) {
                    bossToSpawn = blackdagger_wing;
                } else if (chance < 45) {
                    bossToSpawn = bleeding_fly;
                } else if (chance < 67) {
                    bossToSpawn = dust_rider;
                } else {
                    bossToSpawn = emerald_horn;
                }

                _boss = NpcUtils.spawnSingle(bossToSpawn, Location.coordsRandomize(getLoc(), 300, 600), getReflection());
                _timer = System.currentTimeMillis();
            } else {
                showChatWindow(player, "default/32871-2.htm");
            }
        } else {
            super.onBypassFeedback(player, command);
        }
    }
}

package events.Kotatz;

import handler.items.ScriptItemHandler;
import l2p.commons.threading.RunnableImpl;
import l2p.gameserver.ThreadPoolManager;
import l2p.gameserver.data.xml.holder.NpcHolder;
import l2p.gameserver.handler.items.ItemHandler;
import l2p.gameserver.model.Playable;
import l2p.gameserver.model.Player;
import l2p.gameserver.model.SimpleSpawner;
import l2p.gameserver.model.World;
import l2p.gameserver.model.instances.NpcInstance;
import l2p.gameserver.model.items.ItemInstance;
import l2p.gameserver.scripts.ScriptFile;
import l2p.gameserver.serverpackets.components.SystemMsg;
import l2p.gameserver.serverpackets.SystemMessage;
import l2p.gameserver.templates.npc.NpcTemplate;

public class kotatzItem extends ScriptItemHandler implements ScriptFile {

    @Override
    public void onLoad() {
        ItemHandler.getInstance().registerItemHandler(this);
    }

    @Override
    public void onReload() {
        
    }

    @Override
    public void onShutdown() {
        
    }

    public class DeSpawnScheduleTimerTask extends RunnableImpl {

        SimpleSpawner spawnedKotatz = null;

        public DeSpawnScheduleTimerTask(SimpleSpawner spawn) {
            spawnedKotatz = spawn;
        }

        @Override
        public void runImpl() throws Exception {
            spawnedKotatz.deleteAll();
        }
    }
    private static int[] _itemIds = {20868 // Kotatz
    };
    private static int[] _npcIds = {127, 127
    };
    private static final int DESPAWN_TIME = 600000; //10 min

    @Override
    public boolean useItem(Playable playable, ItemInstance item, boolean ctrl) {
        Player activeChar = (Player) playable;
        NpcTemplate template = null;

        int itemId = item.getItemId();
        for (int i = 0; i < _itemIds.length; i++) {
            if (_itemIds[i] == itemId) {
                template = NpcHolder.getInstance().getTemplate(_npcIds[i]);
                break;
            }
        }

        for (NpcInstance npc : World.getAroundNpc(activeChar, 300, 200)) {
            if (npc.getNpcId() == _npcIds[0] || npc.getNpcId() == _npcIds[1]) {
                activeChar.sendPacket(new SystemMessage(SystemMsg.SINCE_S1_ALREADY_EXISTS_NEARBY_YOU_CANNOT_SUMMON_IT_AGAIN).addName(npc));
                return false;
            }
        }

        // Запрет на саммон елок слищком близко к другим НПЦ
        if (World.getAroundNpc(activeChar, 100, 200).size() > 0) {
            activeChar.sendPacket(SystemMsg.YOU_MAY_NOT_SUMMON_FROM_YOUR_CURRENT_LOCATION);
            return false;
        }

        if (template == null) {
            return false;
        }

        if (!activeChar.getInventory().destroyItem(item, 1L)) {
            return false;
        }

        SimpleSpawner spawn = new SimpleSpawner(template);
        spawn.setLoc(activeChar.getLoc());
        NpcInstance npc = spawn.doSpawn(false);
        npc.setTitle(activeChar.getName()); //FIXME Почему-то не устанавливается
        spawn.respawnNpc(npc);

        // АИ вещающее бафф регена устанавливается только для большой елки
        if (itemId == 20868) {
            npc.setAI(new kotatzAI(npc));
        }

        ThreadPoolManager.getInstance().schedule(new DeSpawnScheduleTimerTask(spawn), (activeChar.isInPeaceZone() ? DESPAWN_TIME / 3 : DESPAWN_TIME));
        return true;
    }

    @Override
    public int[] getItemIds() {
        return _itemIds;
    }
}

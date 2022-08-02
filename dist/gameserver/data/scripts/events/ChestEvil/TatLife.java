package events.ChestEvil;

import handler.items.ScriptItemHandler;
import l2p.gameserver.Config;
import l2p.gameserver.data.xml.holder.NpcHolder;
import l2p.gameserver.handler.items.ItemHandler;
import l2p.gameserver.model.GameObject;
import l2p.gameserver.model.Playable;
import l2p.gameserver.model.Player;
import l2p.gameserver.model.instances.MonsterInstance;
import l2p.gameserver.model.instances.NpcInstance;
import l2p.gameserver.model.items.ItemInstance;
import l2p.gameserver.scripts.ScriptFile;
import l2p.gameserver.templates.npc.NpcTemplate;

public class TatLife extends ScriptItemHandler implements ScriptFile {

    @Override
    public void onLoad() {
        if (Config.EVENT_CHEST_EVIL_ALLOW)
            ItemHandler.getInstance().registerItemHandler(this);
    }

    @Override
    public void onReload() {
        
    }

    @Override
    public void onShutdown() {
        
    }

    private static final int[] _itemIds = {Config.EVENT_CHEST_EVIL_RED, Config.EVENT_CHEST_EVIL_BLUE};
    private static final int _red = Config.EVENT_CHEST_EVIL_RED;
    private static final int _blue = Config.EVENT_CHEST_EVIL_BLUE;
    
    @Override
    public boolean useItem(Playable playable, ItemInstance item, boolean ctrl) {
        Player activeChar = (Player) playable;
        GameObject target = activeChar.getTarget();
        int itemId = item.getItemId();
        if (target == null ||!target.isMonster())
            return false;
        if (target.isChampion())
            return false;
        
        final NpcInstance pcTarget = (NpcInstance) target;
        int monsterId = pcTarget.getNpcId();
        
        
        NpcTemplate template = NpcHolder.getInstance().getTemplate(monsterId);
        
        if (template == null) {
            return false;
        }
        
        if (!activeChar.getInventory().destroyItem(item, 1L)) {
            return false;
        }
        
        NpcInstance npc = template.getNewInstance();
        npc.setHeading(pcTarget.getHeading());
        npc.setSpawnedLoc(pcTarget.getLoc());
        npc.setReflection(pcTarget.getReflection());
        ((MonsterInstance) npc).setChampion(itemId == _red ? 2 : itemId == _blue ? 1 : 0);
        npc.setCurrentHpMp(npc.getMaxHp(), npc.getMaxMp(), true);
        pcTarget.decayMe();
        npc.spawnMe(npc.getSpawnedLoc());
        return true;
    }

    @Override
    public int[] getItemIds() {
        return _itemIds;
    }
}

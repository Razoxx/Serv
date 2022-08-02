package npc.model;

import java.util.concurrent.Future;
import l2p.commons.threading.RunnableImpl;
import l2p.commons.util.Rnd;
import l2p.gameserver.Config;
import l2p.gameserver.ThreadPoolManager;
import l2p.gameserver.data.xml.holder.NpcHolder;
import l2p.gameserver.model.Player;
import l2p.gameserver.model.instances.NpcInstance;
import l2p.gameserver.scripts.Functions;
import l2p.gameserver.serverpackets.components.NpcString;
import l2p.gameserver.templates.npc.NpcTemplate;
import l2p.gameserver.utils.Location;

/**
 * @author VISTALL
 * @date 16:00/25.04.2012
 */
public class LuckyPigInstance extends NpcInstance {

    private int _pickCount;

    private Future<?> _task;

    public LuckyPigInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (!canBypassCheck(player, this)) {
            return;
        }

        if (command.equals("stop")) {
            if (_pickCount < 3) {
                showChatWindow(player, "luckpi_003.htm");
            } else {
                Functions.npcSayInRange(this, 600, NpcString.LUCKPY_NO_MORE_ADENA_OH);

                final int pickCount = _pickCount;

                onDecay();

                PrizeLuckyPigInstance luckyPigInstance = (PrizeLuckyPigInstance) NpcHolder.getInstance().getTemplate(pickCount >= 5 ? 2503 : 2502).getNewInstance();

                luckyPigInstance.setPickCount(pickCount);
                luckyPigInstance.setPigLevel(getLevel());
                luckyPigInstance.setSpawnedLoc(getLoc());
                luckyPigInstance.spawnMe(luckyPigInstance.getSpawnedLoc());
            }
        } else {
            super.onBypassFeedback(player, command);
        }
    }

    @Override
    public void showChatWindow(Player player, int val, Object... arg) {
        showChatWindow(player, "luckpi_001.htm");
    }

    @Override
    public void spawnMe(Location loc) {
        if (!Rnd.chance(getParameter("randRate", 100))) {
            onDecay();
            return;
        }

        spawnMe0(loc, null);

        _task = ThreadPoolManager.getInstance().schedule(new RunnableImpl() {
            @Override
            public void runImpl() throws Exception {
                onDecay();
            }
        }, Config.TIME_IF_NOT_FEED * 60 * 1000);
    }

    @Override
    protected void onDecay() {
        _pickCount = 0;
        if (_task != null) {
            _task.cancel(true);
        }

        super.onDecay();
    }

    public int getPickCount() {
        return _pickCount;
    }

    public void incPickCount() {
        _pickCount++;
    }

    public void decPickCount() {
        _pickCount--;
    }

    public void meFull() {
        Functions.npcSayInRange(this, 600, NpcString.LUCKPY_IM_FULL_THANKS_FOR_THE_YUMMY_ADENA_OH);

        final int pickCount = _pickCount;

        onDecay();

        PrizeLuckyPigInstance luckyPigInstance = (PrizeLuckyPigInstance) NpcHolder.getInstance().getTemplate(pickCount >= 5 ? 2503 : 2502).getNewInstance();

        luckyPigInstance.setPickCount(pickCount);
        luckyPigInstance.setPigLevel(getLevel());
        luckyPigInstance.setSpawnedLoc(getLoc());
        luckyPigInstance.spawnMe(luckyPigInstance.getSpawnedLoc());
    }
}

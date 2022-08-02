package ai;

import l2p.commons.math.random.RndSelector;
import l2p.commons.util.Rnd;
import l2p.gameserver.ai.DefaultAI;
import l2p.gameserver.model.Player;
import l2p.gameserver.model.World;
import l2p.gameserver.model.instances.NpcInstance;
import l2p.gameserver.serverpackets.L2GameServerPacket;
import l2p.gameserver.serverpackets.MagicSkillUse;
import l2p.gameserver.tables.SkillTable;

public class ChristmasTree extends DefaultAI {

    private static final RndSelector<Integer> SOUNDS = new RndSelector(5);

    private boolean _buffsEnabled = false;
    private int _timer = 0;

    public ChristmasTree(NpcInstance actor) {
        super(actor);
    }

    @Override
    protected boolean thinkActive() {
        NpcInstance actor = getActor();
        if (_buffsEnabled) {
            _timer += 1;
            if (_timer >= 180) {
                _timer = 0;
                if (actor == null) {
                    return true;
                }

                int skillId = 2139;
                for (Player player : World.getAroundPlayers(actor, 150, 100)) {
                    if (player != null && !player.isInZonePeace() && player.getEffectList().getEffectsBySkillId(skillId) == null) {
                        actor.doCast(SkillTable.getInstance().getInfo(skillId, 1), player, true);
                    }
                }

                if (Rnd.chance(50)) {
                    actor.broadcastPacketToOthers(new L2GameServerPacket[]{new MagicSkillUse(actor, actor, ((Integer) SOUNDS.select()).intValue(), 1, 500, 0L)});
                }
            }
        }
        return false;
    }

    @Override
    protected boolean randomWalk() {
        return false;
    }

    @Override
    protected void onEvtSpawn() {
        super.onEvtSpawn();
        _buffsEnabled = (!getActor().isInZonePeace());
        _timer = 0;
    }

    static {
        SOUNDS.add(Integer.valueOf(2140), 20);
        SOUNDS.add(Integer.valueOf(2142), 20);
        SOUNDS.add(Integer.valueOf(2145), 20);
        SOUNDS.add(Integer.valueOf(2147), 20);
        SOUNDS.add(Integer.valueOf(2149), 20);
    }
}

package ai.fog;

import java.util.List;
import l2p.commons.util.Rnd;
import l2p.gameserver.ai.CtrlEvent;
import l2p.gameserver.ai.Fighter;
import l2p.gameserver.model.Creature;
import l2p.gameserver.model.Effect;
import l2p.gameserver.model.Player;
import l2p.gameserver.model.World;
import l2p.gameserver.model.instances.NpcInstance;
import l2p.gameserver.tables.SkillTable;
import l2p.gameserver.utils.NpcUtils;
import org.apache.commons.lang3.ArrayUtils;

/**
 * @author n0nam3
 * @date 15/10/2010 import l2p.gameserver.utils.NpcUtils; import
 * org.apache.commons.lang3.ArrayUtils;
 * @comment Групповой AI для зоны Forge of the Gods
 */
public class GroupAI extends Fighter {

    private static final int[] RANDOM_SPAWN_MOBS = {
        18799,
        18800,
        18801,
        18802,
        18803
    };
    private static final int[] FOG_MOBS = {
        22634,
        22635,
        22636,
        22637,
        22638,
        22639,
        22640,
        22641,
        22642,
        22643,
        22644,
        22645,
        22646,
        22647,
        22648,
        22649
    };
    private static final int TAR_BEETLE = 18804;

    private static int TAR_BEETLE_SEARCH_RADIUS = 350; // search around players
    private static long _castReuse;
    private long _castReuseTimer;

    public GroupAI(NpcInstance actor) {
        super(actor);

        if (actor.getNpcId() == TAR_BEETLE) {
            AI_TASK_ATTACK_DELAY = 1250;
            actor.setIsInvul(true);
            actor.setHasChatWindow(false);
            _castReuse = SkillTable.getInstance().getInfo(6142, 1).getReuseDelay();
        } else if (ArrayUtils.contains(RANDOM_SPAWN_MOBS, actor.getNpcId())) {
            actor.startImmobilized();
        }
    }

    @Override
    protected boolean thinkActive() {
        NpcInstance actor = getActor();

        if (actor.getNpcId() != TAR_BEETLE) {
            return super.thinkActive();
        }

        if (_castReuseTimer + _castReuse < System.currentTimeMillis()) {
            List<Player> players = World.getAroundPlayers(actor, TAR_BEETLE_SEARCH_RADIUS, 200);
            if (players != null && players.size() > 0) {
                Player player = players.get(Rnd.get(players.size()));
                if (player.isInvisible()) {
                    return true;
                }
                int level = 0;
                for (Effect e : player.getEffectList().getAllEffects()) {
                    if (e.getSkill().getId() == 6142) {
                        level = e.getSkill().getLevel();
                    }
                }
                actor.doCast(SkillTable.getInstance().getInfo(6142, Math.min(level + 1, 3)), player, false);
                _castReuseTimer = System.currentTimeMillis();
            }
        }
        return true;
    }

    @Override
    protected void onEvtDead(Creature killer) {
        NpcInstance actor = getActor();

        if (ArrayUtils.contains(FOG_MOBS, actor.getNpcId()) && Rnd.chance(20)) {
            try {
                NpcInstance npc = NpcUtils.spawnSingle(RANDOM_SPAWN_MOBS[Rnd.get(RANDOM_SPAWN_MOBS.length)], actor.getLoc(), actor.getReflection(), 300000L);
                npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, killer, Rnd.get(1, 100));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        super.onEvtDead(killer);
    }

    @Override
    protected void onEvtAttacked(Creature attacker, int damage) {
        if (getActor().getNpcId() == TAR_BEETLE) {
            return;
        }
        super.onEvtAttacked(attacker, damage);
    }

    @Override
    protected void onEvtAggression(Creature target, int aggro) {
        if (getActor().getNpcId() == TAR_BEETLE) {
            return;
        }
        super.onEvtAggression(target, aggro);
    }

    @Override
    protected boolean checkTarget(Creature target, int range) {
        NpcInstance actor = getActor();
        if (ArrayUtils.contains(RANDOM_SPAWN_MOBS, getActor().getNpcId()) && target != null && !actor.isInRange(target, actor.getAggroRange())) {
            actor.getAggroList().remove(target, true);
            return false;
        }
        return super.checkTarget(target, range);
    }

    @Override
    protected boolean randomWalk() {
        return ArrayUtils.contains(RANDOM_SPAWN_MOBS, getActor().getNpcId()) || getActor().getNpcId() == TAR_BEETLE ? false : true;
    }
}

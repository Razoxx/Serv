package ai.suspiciousmerchant;

import l2p.commons.util.Rnd;
import l2p.gameserver.ai.DefaultAI;
import l2p.gameserver.model.Creature;
import l2p.gameserver.model.instances.NpcInstance;
import l2p.gameserver.utils.Location;

public class SuspiciousMerchantMarshland extends DefaultAI {

    static final Location[] points = {
        new Location(72172, -57044, -3096),
        new Location(72762, -56641, -3104),
        new Location(73234, -56119, -3120),
        new Location(74153, -55297, -3120),
        new Location(73190, -54615, -3120),
        new Location(72185, -53254, -3104),
        new Location(71689, -52675, -3088),
        new Location(70188, -52530, -3064),
        new Location(69588, -52830, -3080),
        new Location(68294, -52437, -3248),
        new Location(67596, -53787, -3224),
        new Location(67026, -54941, -3040),
        new Location(67596, -53787, -3224),
        new Location(68294, -52437, -3248),
        new Location(69588, -52830, -3080),
        new Location(70188, -52530, -3064),
        new Location(71689, -52675, -3088),
        new Location(72185, -53254, -3104),
        new Location(73190, -54615, -3120),
        new Location(74153, -55297, -3120),
        new Location(73234, -56119, -3120),
        new Location(72762, -56641, -3104),
        new Location(72762, -56641, -3104),};

    private int current_point = -1;
    private long wait_timeout = 0;
    private boolean wait = false;

    public SuspiciousMerchantMarshland(NpcInstance actor) {
        super(actor);
    }

    @Override
    public boolean isGlobalAI() {
        return true;
    }

    @Override
    protected boolean thinkActive() {
        NpcInstance actor = getActor();
        if (actor.isDead()) {
            return true;
        }

        if (_def_think) {
            doTask();
            return true;
        }

        if (actor.isMoving) {
            return true;
        }

        if (System.currentTimeMillis() > wait_timeout && (current_point > -1 || Rnd.chance(5))) {
            if (!wait) {
                switch (current_point) {
                    case 0:
                        wait_timeout = System.currentTimeMillis() + 30000;
                        wait = true;
                        return true;
                    case 3:
                        wait_timeout = System.currentTimeMillis() + 30000;
                        wait = true;
                        return true;
                    case 11:
                        wait_timeout = System.currentTimeMillis() + 60000;
                        wait = true;
                        return true;
                    case 19:
                        wait_timeout = System.currentTimeMillis() + 30000;
                        wait = true;
                        return true;
                    case 22:
                        wait_timeout = System.currentTimeMillis() + 30000;
                        wait = true;
                        return true;
                }
            }

            wait_timeout = 0;
            wait = false;
            current_point++;

            if (current_point >= points.length) {
                current_point = 0;
            }

            addTaskMove(points[current_point], false);
            doTask();
            return true;
        }

        if (randomAnimation()) {
            return true;
        }

        return false;
    }

    @Override
    protected void onEvtAttacked(Creature attacker, int damage) {
    }

    @Override
    protected void onEvtAggression(Creature target, int aggro) {
    }
}

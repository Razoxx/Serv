package quests;

import l2p.gameserver.data.xml.holder.EventHolder;
import l2p.gameserver.model.base.ClassId;
import l2p.gameserver.model.entity.events.EventType;
import l2p.gameserver.model.entity.events.impl.DominionSiegeRunnerEvent;
import l2p.gameserver.serverpackets.components.NpcString;

public class _734_PierceThroughAShield extends Dominion_KillSpecialUnitQuest {

    public _734_PierceThroughAShield() {
        super();
        DominionSiegeRunnerEvent runnerEvent = EventHolder.getInstance().getEvent(EventType.MAIN_EVENT, 1);
        runnerEvent.addBreakQuest(this);
    }

    @Override
    protected NpcString startNpcString() {
        return NpcString.DEFEAT_S1_ENEMY_KNIGHTS;
    }

    @Override
    protected NpcString progressNpcString() {
        return NpcString.YOU_HAVE_DEFEATED_S2_OF_S1_KNIGHTS;
    }

    @Override
    protected NpcString doneNpcString() {
        return NpcString.YOU_WEAKENED_THE_ENEMYS_DEFENSE;
    }

    @Override
    protected int getRandomMin() {
        return 10;
    }

    @Override
    protected int getRandomMax() {
        return 15;
    }

    @Override
    protected ClassId[] getTargetClassIds() {
        return new ClassId[]{
            ClassId.darkAvenger,
            ClassId.hellKnight,
            ClassId.paladin,
            ClassId.phoenixKnight,
            ClassId.templeKnight,
            ClassId.evaTemplar,
            ClassId.shillienKnight,
            ClassId.shillienTemplar
        };
    }

    @Override
    public void onLoad() {}

    @Override
    public void onReload() {}

    @Override
    public void onShutdown() {}
}

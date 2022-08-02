package quests;

import l2p.gameserver.data.xml.holder.EventHolder;
import l2p.gameserver.model.base.ClassId;
import l2p.gameserver.model.entity.events.EventType;
import l2p.gameserver.model.entity.events.impl.DominionSiegeRunnerEvent;
import l2p.gameserver.serverpackets.components.NpcString;

public class _738_DestroyKeyTargets extends Dominion_KillSpecialUnitQuest {

    public _738_DestroyKeyTargets() {
        super();
        DominionSiegeRunnerEvent runnerEvent = EventHolder.getInstance().getEvent(EventType.MAIN_EVENT, 1);
        runnerEvent.addBreakQuest(this);
    }

    @Override
    protected NpcString startNpcString() {
        return NpcString.DEFEAT_S1_WARSMITHS_AND_OVERLORDS;
    }

    @Override
    protected NpcString progressNpcString() {
        return NpcString.YOU_HAVE_DEFEATED_S2_OF_S1_WARSMITHS_AND_OVERLORDS;
    }

    @Override
    protected NpcString doneNpcString() {
        return NpcString.YOU_DESTROYED_THE_ENEMYS_PROFESSIONALS;
    }

    @Override
    protected int getRandomMin() {
        return 3;
    }

    @Override
    protected int getRandomMax() {
        return 8;
    }

    @Override
    protected ClassId[] getTargetClassIds() {
        return new ClassId[]{
            ClassId.necromancer,
            ClassId.swordSinger,
            ClassId.bladedancer,
            ClassId.overlord,
            ClassId.warsmith,
            ClassId.soultaker,
            ClassId.swordMuse,
            ClassId.spectralDancer,
            ClassId.dominator,
            ClassId.maestro,
            ClassId.inspector,
            ClassId.judicator
        };
    }
    
    @Override
    public void onLoad() {}

    @Override
    public void onReload() {}

    @Override
    public void onShutdown() {}
}

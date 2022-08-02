package quests;

import l2p.gameserver.data.xml.holder.EventHolder;
import l2p.gameserver.model.base.ClassId;
import l2p.gameserver.model.entity.events.EventType;
import l2p.gameserver.model.entity.events.impl.DominionSiegeRunnerEvent;
import l2p.gameserver.serverpackets.components.NpcString;

public class _737_DenyBlessings extends Dominion_KillSpecialUnitQuest {

    public _737_DenyBlessings() {
        super();
        DominionSiegeRunnerEvent runnerEvent = EventHolder.getInstance().getEvent(EventType.MAIN_EVENT, 1);
        runnerEvent.addBreakQuest(this);
    }

    @Override
    protected NpcString startNpcString() {
        return NpcString.DEFEAT_S1_HEALERS_AND_BUFFERS;
    }

    @Override
    protected NpcString progressNpcString() {
        return NpcString.YOU_HAVE_DEFEATED_S2_OF_S1_HEALERS_AND_BUFFERS;
    }

    @Override
    protected NpcString doneNpcString() {
        return NpcString.YOU_HAVE_WEAKENED_THE_ENEMYS_SUPPORT;
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
            ClassId.bishop,
            ClassId.prophet,
            ClassId.elder,
            ClassId.elder,
            ClassId.shillienElder,
            ClassId.cardinal,
            ClassId.hierophant,
            ClassId.evaSaint,
            ClassId.shillienSaint,
            ClassId.doomcryer
        };
    }
    
    @Override
    public void onLoad() {}

    @Override
    public void onReload() {}

    @Override
    public void onShutdown() {}
}

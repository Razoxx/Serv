package quests;

import l2p.gameserver.data.xml.holder.EventHolder;
import l2p.gameserver.model.base.ClassId;
import l2p.gameserver.model.entity.events.EventType;
import l2p.gameserver.model.entity.events.impl.DominionSiegeRunnerEvent;
import l2p.gameserver.serverpackets.components.NpcString;

public class _735_MakeSpearsDull extends Dominion_KillSpecialUnitQuest {

    public _735_MakeSpearsDull() {
        super();
        DominionSiegeRunnerEvent runnerEvent = EventHolder.getInstance().getEvent(EventType.MAIN_EVENT, 1);
        runnerEvent.addBreakQuest(this);
    }

    @Override
    protected NpcString startNpcString() {
        return NpcString.DEFEAT_S1_WARRIORS_AND_ROGUES;
    }

    @Override
    protected NpcString progressNpcString() {
        return NpcString.YOU_HAVE_DEFEATED_S2_OF_S1_WARRIORS_AND_ROGUES;
    }

    @Override
    protected NpcString doneNpcString() {
        return NpcString.YOU_WEAKENED_THE_ENEMYS_ATTACK;
    }

    @Override
    protected int getRandomMin() {
        return 15;
    }

    @Override
    protected int getRandomMax() {
        return 20;
    }

    @Override
    protected ClassId[] getTargetClassIds() {
        return new ClassId[]{
            ClassId.gladiator,
            ClassId.warlord,
            ClassId.treasureHunter,
            ClassId.hawkeye,
            ClassId.plainsWalker,
            ClassId.silverRanger,
            ClassId.abyssWalker,
            ClassId.phantomRanger,
            ClassId.destroyer,
            ClassId.tyrant,
            ClassId.bountyHunter,
            ClassId.duelist,
            ClassId.dreadnought,
            ClassId.sagittarius,
            ClassId.adventurer,
            ClassId.windRider,
            ClassId.moonlightSentinel,
            ClassId.ghostHunter,
            ClassId.ghostSentinel,
            ClassId.titan,
            ClassId.grandKhauatari,
            ClassId.fortuneSeeker,
            ClassId.berserker,
            ClassId.maleSoulbreaker,
            ClassId.femaleSoulbreaker,
            ClassId.arbalester,
            ClassId.doombringer,
            ClassId.maleSoulhound,
            ClassId.femaleSoulhound,
            ClassId.trickster
        };
    }
    
    @Override
    public void onLoad() {}

    @Override
    public void onReload() {}

    @Override
    public void onShutdown() {}
}

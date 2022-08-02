package quests;

import l2p.gameserver.data.xml.holder.EventHolder;
import l2p.gameserver.model.base.ClassId;
import l2p.gameserver.model.entity.events.EventType;
import l2p.gameserver.model.entity.events.impl.DominionSiegeRunnerEvent;
import l2p.gameserver.serverpackets.components.NpcString;

public class _736_WeakenTheMagic extends Dominion_KillSpecialUnitQuest {

    public _736_WeakenTheMagic() {
        super();
        DominionSiegeRunnerEvent runnerEvent = EventHolder.getInstance().getEvent(EventType.MAIN_EVENT, 1);
        runnerEvent.addBreakQuest(this);
    }

    @Override
    protected NpcString startNpcString() {
        return NpcString.DEFEAT_S1_WIZARDS_AND_SUMMONERS;
    }

    @Override
    protected NpcString progressNpcString() {
        return NpcString.YOU_HAVE_DEFEATED_S2_OF_S1_ENEMIES;
    }

    @Override
    protected NpcString doneNpcString() {
        return NpcString.YOU_WEAKENED_THE_ENEMYS_MAGIC;
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
            ClassId.sorceror,
            ClassId.warlock,
            ClassId.spellsinger,
            ClassId.elementalSummoner,
            ClassId.spellhowler,
            ClassId.phantomSummoner,
            ClassId.archmage,
            ClassId.arcanaLord,
            ClassId.mysticMuse,
            ClassId.elementalMaster,
            ClassId.stormScreamer,
            ClassId.spectralMaster
        };
    }
    
    @Override
    public void onLoad() {}

    @Override
    public void onReload() {}

    @Override
    public void onShutdown() {}
}

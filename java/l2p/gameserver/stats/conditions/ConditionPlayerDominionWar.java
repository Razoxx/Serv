package l2p.gameserver.stats.conditions;

import l2p.gameserver.model.entity.events.impl.DominionSiegeEvent;
import l2p.gameserver.stats.Env;

public class ConditionPlayerDominionWar
        extends Condition {

    private int _id;

    public ConditionPlayerDominionWar(int id) {
        _id = id;
    }

    @Override
    protected boolean testImpl(Env env) {
        DominionSiegeEvent dominionSiegeEvent = (DominionSiegeEvent) env.character.getEvent(DominionSiegeEvent.class);

        return (dominionSiegeEvent != null) && (dominionSiegeEvent.getId() == _id);
    }
}

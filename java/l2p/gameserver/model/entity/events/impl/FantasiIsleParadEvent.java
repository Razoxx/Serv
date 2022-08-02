package l2p.gameserver.model.entity.events.impl;

import l2p.commons.collections.MultiValueSet;
import l2p.gameserver.model.entity.events.GlobalEvent;

public class FantasiIsleParadEvent extends GlobalEvent {

    public FantasiIsleParadEvent(MultiValueSet<String> set) {
        super(set);
    }

    @Override
    public void reCalcNextTime(boolean onStart) {
        clearActions();
    }

    @Override
    protected long startTimeMillis() {
        return System.currentTimeMillis() + 30000L;
    }
}
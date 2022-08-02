package l2p.gameserver.utils;

import java.util.Comparator;
import l2p.gameserver.model.Effect;

public class EffectsComparator implements Comparator<Effect> {

    private static final EffectsComparator instance = new EffectsComparator(1, -1);
    private static final EffectsComparator reverse = new EffectsComparator(-1, 1);
    protected final int _greater;
    protected final int _smaller;

    public static EffectsComparator getInstance() {
        return instance;
    }

    public static EffectsComparator getReverseInstance() {
        return reverse;
    }

    protected EffectsComparator(int g, int s) {
        _greater = g;
        _smaller = s;
    }

    @Override
    public int compare(Effect e1, Effect e2) {
        boolean toggle1 = e1.getSkill().isToggle();
        boolean toggle2 = e2.getSkill().isToggle();
        if ((toggle1) && (toggle2)) {
            return compareStartTime(e1, e2);
        }
        if ((toggle1) || (toggle2)) {
            if (toggle1) {
                return _greater;
            }
            return _smaller;
        }
        boolean music1 = e1.getSkill().isMusic();
        boolean music2 = e2.getSkill().isMusic();
        if ((music1) && (music2)) {
            return compareStartTime(e1, e2);
        }
        if ((music1) || (music2)) {
            if (music1) {
                return _greater;
            }
            return _smaller;
        }
        boolean offensive1 = e1.isOffensive();
        boolean offensive2 = e2.isOffensive();
        if ((offensive1) && (offensive2)) {
            return compareStartTime(e1, e2);
        }
        if ((offensive1) || (offensive2)) {
            if (!offensive1) {
                return _greater;
            }
            return _smaller;
        }
        boolean trigger1 = e1.getSkill().isTrigger();
        boolean trigger2 = e2.getSkill().isTrigger();
        if ((trigger1) && (trigger2)) {
            return compareStartTime(e1, e2);
        }
        if ((trigger1) || (trigger2)) {
            if (trigger1) {
                return _greater;
            }
            return _smaller;
        }
        return compareStartTime(e1, e2);
    }

    protected int compareStartTime(Effect o1, Effect o2) {
        if (o1.getStartTime() > o2.getStartTime()) {
            return _greater;
        }
        if (o1.getStartTime() < o2.getStartTime()) {
            return _smaller;
        }
        return 0;
    }
}

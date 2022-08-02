package l2p.gameserver.utils;

import l2p.gameserver.model.Effect;

/**
 * Сортирует эффекты по группам, причем хербы помещаются в начало списка.
 * Используется для поиска эффекта который будет вытеснен.
 *
 */
public class HerbEffectsComparator extends EffectsComparator {

    private static final HerbEffectsComparator instance = new HerbEffectsComparator(1, -1);

    public static HerbEffectsComparator getInstance() {
        return instance;
    }

    protected HerbEffectsComparator(int g, int s) {
        super(g, s);
    }

    @Override
    public int compare(Effect e1, Effect e2) {
        boolean herb1 = e1.getSkill().isAbnormalInstant();
        boolean herb2 = e2.getSkill().isAbnormalInstant();

        if (herb1 && herb2) {
            return compareStartTime(e1, e2);
        }

        if (herb1 || herb2) {
            if (!herb1) {
                return _greater;
            } else {
                return _smaller;
            }
        }

        return super.compare(e1, e2);
    }
}

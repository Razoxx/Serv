package l2p.gameserver.skills.effects;

import java.util.List;

import l2p.commons.util.Rnd;
import l2p.gameserver.model.AggroList;
import l2p.gameserver.model.Creature;
import l2p.gameserver.model.Effect;
import l2p.gameserver.model.instances.MonsterInstance;
import l2p.gameserver.stats.Env;

public class EffectRandomHate extends Effect {

    public EffectRandomHate(Env env, EffectTemplate template) {
        super(env, template);
    }

    @Override
    public boolean checkCondition() {
        return getEffected().isMonster();
    }

    @Override
    public void onStart() {
        MonsterInstance monster = (MonsterInstance) getEffected();
        Creature mostHated = monster.getAggroList().getMostHated();
        if (mostHated == null) {
            return;
        }

        AggroList.AggroInfo mostAggroInfo = monster.getAggroList().get(mostHated);
        List<Creature> hateList = monster.getAggroList().getHateList(monster.getAggroRange());
        hateList.remove(mostHated);

        if (!hateList.isEmpty()) {
            AggroList.AggroInfo newAggroInfo = monster.getAggroList().get(hateList.get(Rnd.get(hateList.size())));
            final int oldHate = newAggroInfo.hate;

            newAggroInfo.hate = mostAggroInfo.hate;
            mostAggroInfo.hate = oldHate;
        }
    }

    @Override
    public boolean isHidden() {
        return true;
    }

    @Override
    protected boolean onActionTime() {
        return false;
    }
}

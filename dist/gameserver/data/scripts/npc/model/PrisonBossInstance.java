package npc.model;

import l2p.gameserver.model.Creature;
import l2p.gameserver.model.Skill;
import l2p.gameserver.model.instances.MonsterInstance;
import l2p.gameserver.templates.npc.NpcTemplate;

public class PrisonBossInstance extends MonsterInstance {

    public PrisonBossInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    protected void onReduceCurrentHp(double damage, Creature attacker, Skill skill, boolean awake, boolean standUp, boolean directHp) {
        int attacker_lvl = attacker.getLevel();
        int boss_lvl = this.getLevel();
        int lvl_diff = attacker_lvl - boss_lvl;
        if (lvl_diff >= 9) {
            attacker.getEffectList().stopAllEffects();
        }
        super.onReduceCurrentHp(damage, attacker, skill, awake, standUp, directHp);
    }

    @Override
    public boolean isRaid() {
        return false;
    }
    
    @Override
    public boolean isFearImmune() {
        return true;
    }

    @Override
    public boolean isParalyzeImmune() {
        return true;
    }

    @Override
    public boolean isLethalImmune() {
        return true;
    }

    @Override
    public boolean canChampion() {
        return false;
    }
    
    @Override
    protected void onDeath(Creature killer) {
        getMinionList().deleteMinions();

        super.onDeath(killer);
    }
}

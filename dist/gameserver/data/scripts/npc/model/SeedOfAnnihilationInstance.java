package npc.model;

import l2p.commons.util.Rnd;
import l2p.gameserver.data.xml.holder.NpcHolder;
import l2p.gameserver.model.Creature;
import l2p.gameserver.model.instances.MonsterInstance;
import l2p.gameserver.templates.npc.MinionData;
import l2p.gameserver.templates.npc.NpcTemplate;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * У монстров в Seed of Annihilation список минионов может быть разный.
 *
 * @author Bonux
 *
 */
public class SeedOfAnnihilationInstance extends MonsterInstance {

    private static final Logger _log = LoggerFactory.getLogger(SeedOfAnnihilationInstance.class);
    Boolean doSpawn = true;
    private static final int[] BISTAKON_MOBS = new int[]{22750, 22751, 22752, 22753};
    private static final int[] COKRAKON_MOBS = new int[]{22763, 22764, 22765};
    private static final int[][] BISTAKON_MINIONS = new int[][]{
        {22746, 22746, 22746},
        {22747, 22747, 22747},
        {22748, 22748, 22748},
        {22749, 22749, 22749}};
    private static final int[][] COKRAKON_MINIONS = new int[][]{
        {22760, 22760, 22761},
        {22760, 22760, 22762},
        {22761, 22761, 22760},
        {22761, 22761, 22762},
        {22762, 22762, 22760},
        {22762, 22762, 22761}};

    public SeedOfAnnihilationInstance(int objectId, NpcTemplate template) {
        super(objectId, template);

    }

    private static void addMinions(int[] minions, NpcTemplate template) {
        if (minions != null && minions.length > 0) {
            for (int id : minions) {
                template.addMinion(new MinionData(id, 1, 1));
            }
        }
    }

    @Override
    public void onSpawn() {
        super.onSpawn();

        if (getMinionList().hasAliveMinions()) {
            return;
        }
        NpcTemplate template = NpcHolder.getInstance().getTemplate(getNpcId());
        if (ArrayUtils.contains(BISTAKON_MOBS, getNpcId())) {
            int chance = Rnd.get(BISTAKON_MINIONS.length);
            addMinions(BISTAKON_MINIONS[chance], template);
        } else if (ArrayUtils.contains(COKRAKON_MOBS, getNpcId())) {
            int chance2 = Rnd.get(BISTAKON_MINIONS.length);
            addMinions(COKRAKON_MINIONS[Rnd.get(COKRAKON_MINIONS.length)], template);
        }
    }

    @Override
    protected void onDeath(Creature killer) {
        //TODO: Проверить на оффе, при убийстве главного миньёны анспавнятся или нет.
        //getMinionList().unspawnMinions();
        super.onDeath(killer);
    }

    @Override
    public boolean canChampion() {
        return false;
    }
}

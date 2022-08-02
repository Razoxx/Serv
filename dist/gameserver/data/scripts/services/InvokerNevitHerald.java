package services;

import l2p.gameserver.model.Player;
import l2p.gameserver.model.instances.NpcInstance;
import l2p.gameserver.scripts.Functions;
import l2p.gameserver.tables.SkillTable;

/**
 * После смерти Антараса или Валакаса на 3 часа появляется Вестник Невитта С
 * помощью «Вестника Невитта» можно получить баф «Сокрушение Дракона», на 3 часа
 * увеличивающий время действия «Благословения Невитта».
 */
public class InvokerNevitHerald extends Functions {

    public void getCrushingDragon() {
        Player player = getSelf();
        NpcInstance npc = getNpc();
        npc.doCast(SkillTable.getInstance().getInfo(23312, 1), player, true);
    }
}

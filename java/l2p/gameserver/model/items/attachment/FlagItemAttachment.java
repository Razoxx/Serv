package l2p.gameserver.model.items.attachment;

import l2p.gameserver.model.Creature;
import l2p.gameserver.model.Player;
import l2p.gameserver.model.Skill;

public interface FlagItemAttachment extends PickableAttachment {
    //FIXME [VISTALL] возможно переделать на слушатели игрока

    void onLogout(Player player);
    //FIXME [VISTALL] возможно переделать на слушатели игрока

    void onDeath(Player owner, Creature killer);

    void onOutTerritory(Player player);

    boolean canAttack(Player player);

    boolean canCast(Player player, Skill skill);
}

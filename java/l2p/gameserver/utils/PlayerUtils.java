package l2p.gameserver.utils;

import l2p.gameserver.model.Player;

public class PlayerUtils {

    public static void updateAttackableFlags(Player player) {
        player.broadcastRelation();
        if (player.getPet() != null) {
            player.getPet().broadcastCharInfo();
        }
    }
}

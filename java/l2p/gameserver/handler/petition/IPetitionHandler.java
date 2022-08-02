package l2p.gameserver.handler.petition;

import l2p.gameserver.model.Player;

public interface IPetitionHandler {

    void handle(Player player, int id, String txt);
}

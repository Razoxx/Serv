package l2p.gameserver.handler.chat;

import l2p.gameserver.serverpackets.components.ChatType;

public interface IChatHandler {

    void say();

    ChatType getType();
}

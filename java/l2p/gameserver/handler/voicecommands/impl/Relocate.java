package l2p.gameserver.handler.voicecommands.impl;

import java.util.List;

import l2p.gameserver.cache.Msg;
import l2p.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2p.gameserver.model.Player;
import l2p.gameserver.serverpackets.SystemMessage2;
import l2p.gameserver.skills.skillclasses.Call;
import l2p.gameserver.scripts.Functions;
import l2p.gameserver.utils.Location;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Relocate extends Functions implements IVoicedCommandHandler {

    private static final Logger _log = LoggerFactory.getLogger(Relocate.class);
    private final String[] _commandList = new String[]{"km-all-to-me"};

    @Override
    public String[] getVoicedCommandList() {
        return _commandList;
    }

    @Override
    public boolean useVoicedCommand(String command, Player activeChar, String target) {
        if (command.equalsIgnoreCase("km-all-to-me")) {
            if (!activeChar.isClanLeader()) {
                activeChar.sendPacket(Msg.ONLY_THE_CLAN_LEADER_IS_ENABLED);
                return false;
            }
            SystemMessage2 msg = Call.canSummonHere(activeChar);
            if (msg != null) {
                activeChar.sendPacket(msg);
                return false;
            }
            List<Player> players = activeChar.getClan().getOnlineMembers(activeChar.getObjectId());
            for (Player player : players) {
                if (Call.canBeSummoned(player) == null) {
                    player.summonCharacterRequest(activeChar, Location.findAroundPosition(activeChar, 100, 150), 5);
                }
            }
            return true;
        }
        return false;
    }
}
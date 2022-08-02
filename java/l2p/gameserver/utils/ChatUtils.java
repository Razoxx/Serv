package l2p.gameserver.utils;

import l2p.gameserver.Config;
import l2p.gameserver.model.GameObject;
import l2p.gameserver.model.Player;
import l2p.gameserver.model.World;
import l2p.gameserver.model.instances.NpcInstance;
import l2p.gameserver.serverpackets.NpcSay;
import l2p.gameserver.serverpackets.Say2;

public class ChatUtils {

    private static void say(Player activeChar, GameObject activeObject, Iterable<Player> players, int range, Say2 cs) {
        for (Player player : players) {
            if (!player.isBlockAll()) {
                GameObject obj = player.getObservePoint();
                if (obj == null) {
                    obj = player;
                }
                if ((activeObject.isInRangeZ(obj, range)) && (!player.isInBlockList(activeChar)) && (activeChar.canTalkWith(player))) {
                    player.sendPacket(cs);
                }
            }
        }
    }

    public static void say(Player activeChar, Say2 cs) {
        GameObject activeObject = activeChar.getObservePoint();
        if (activeObject == null) {
            activeObject = activeChar;
        }
        say(activeChar, activeObject, World.getAroundObservers(activeObject), Config.CHAT_RANGE, cs);
    }

    public static void say(Player activeChar, Iterable<Player> players, Say2 cs) {
        GameObject activeObject = activeChar.getObservePoint();
        if (activeObject == null) {
            activeObject = activeChar;
        }
        say(activeChar, activeObject, players, Config.CHAT_RANGE, cs);
    }

    public static void say(Player activeChar, int range, Say2 cs) {
        GameObject activeObject = activeChar.getObservePoint();
        if (activeObject == null) {
            activeObject = activeChar;
        }
        say(activeChar, activeObject, World.getAroundObservers(activeObject), range, cs);
    }

    public static void say(NpcInstance activeChar, Iterable<Player> players, int range, NpcSay cs) {
        for (Player player : players) {
            GameObject obj = player.getObservePoint();
            if (obj == null) {
                obj = player;
            }
            if (activeChar.isInRangeZ(obj, range)) {
                player.sendPacket(cs);
            }
        }
    }

    public static void say(NpcInstance activeChar, NpcSay cs) {
        say(activeChar, World.getAroundObservers(activeChar), Config.CHAT_RANGE, cs);
    }

    public static void say(NpcInstance activeChar, Iterable<Player> players, NpcSay cs) {
        say(activeChar, players, Config.CHAT_RANGE, cs);
    }

    public static void say(NpcInstance activeChar, int range, NpcSay cs) {
        say(activeChar, World.getAroundObservers(activeChar), range, cs);
    }

}

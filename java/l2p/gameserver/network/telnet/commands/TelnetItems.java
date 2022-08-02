package l2p.gameserver.network.telnet.commands;

import java.util.LinkedHashSet;
import java.util.Set;

import l2p.gameserver.model.GameObjectsStorage;
import l2p.gameserver.model.Player;
import l2p.gameserver.model.items.ItemInstance;
import l2p.gameserver.network.telnet.TelnetCommand;
import l2p.gameserver.network.telnet.TelnetCommandHolder;
import l2p.gameserver.serverpackets.SystemMessage2;
import l2p.gameserver.utils.ItemFunctions;
import l2p.gameserver.utils.Log;

public class TelnetItems implements TelnetCommandHolder {

    private Set<TelnetCommand> _commands = new LinkedHashSet<TelnetCommand>();

    public TelnetItems() {
        _commands.add(new TelnetCommand("add_item") {
            @Override
            public String getUsage() {
                return "add_item <char name> <itemId> <count>";
            }

            @Override
            public String handle(String[] args) {
                if (args.length == 0 || args[0].isEmpty() || args[1].isEmpty() || args[2].isEmpty()) {
                    return null;
                }
                Player player = GameObjectsStorage.getPlayer(args[0]);
                int itemID = Integer.parseInt(args[1]);
                long itemCount = Integer.parseInt(args[2]);
                if (player != null) {
                    createItem(player, itemID, itemCount);
                    return "Player " + player.getName() + " give item " + itemID + " count " + itemCount + ".\n\r";
                } else {
                    return "Player not found.\n\r";
                }
            }
        });
        _commands.add(new TelnetCommand("add_item_all") {
            @Override
            public String getUsage() {
                return "add_item_all <itemId> <count>";
            }

            @Override
            public String handle(String[] args) {
                if (args.length == 0 || args[0].isEmpty() || args[1].isEmpty()) {
                    return null;
                }
                int item_id = Integer.parseInt(args[0]);
                long item_count = Integer.parseInt(args[1]);
                for (Player player : GameObjectsStorage.getAllPlayersForIterate()) {
                    createItem(player, item_id, item_count);
                    return "All Players give item " + item_id + " count " + item_count + ".\n\r";
                }
                return "";
            }
        });
        _commands.add(new TelnetCommand("add_pcp") {
            @Override
            public String getUsage() {
                return "add_item <char name> <count>";
            }

            @Override
            public String handle(String[] args) {
                if (args.length == 0 || args[0].isEmpty() || args[1].isEmpty()) {
                    return null;
                }
                Player player = GameObjectsStorage.getPlayer(args[0]);
                int itemCount = Integer.parseInt(args[1]);
                if (player != null) {
                    player.addPcBangPoints(itemCount, false);
                    return "Player " + player.getName() + " give item PCPoints count " + itemCount + ".\n\r";
                } else {
                    return "Player not found.\n\r";
                }
            }
        });
        _commands.add(new TelnetCommand("add_pp") {
            @Override
            public String getUsage() {
                return "add_pp <char name> <count>";
            }

            @Override
            public String handle(String[] args) {
                if (args.length == 0 || args[0].isEmpty() || args[1].isEmpty()) {
                    return null;
                }
                Player player = GameObjectsStorage.getPlayer(args[0]);
                int itemCount = Integer.parseInt(args[1]);
                if (player != null) {
                    player.addPremiumPoints(itemCount);
                    return "Player " + player.getName() + " give premium items count " + itemCount + ".\n\r";
                } else {
                    return "Player not found.\n\r";
                }
            }
        });
    }

    private ItemInstance createItem(Player player, int itemId, long count) {
        ItemInstance createditem = ItemFunctions.createItem(itemId);
        createditem.setCount(count);
        Log.LogItem(player, Log.Create, createditem);
        player.getInventory().addItem(createditem);
        if (!createditem.isStackable()) {
            for (long i = 0; i < count - 1; i++) {
                createditem = ItemFunctions.createItem(itemId);
                Log.LogItem(player, Log.Create, createditem);
                player.getInventory().addItem(createditem);
            }
        }
        player.sendPacket(SystemMessage2.obtainItems(itemId, count, 0));
        return createditem;
    }

    @Override
    public Set<TelnetCommand> getCommands() {
        return _commands;
    }
}
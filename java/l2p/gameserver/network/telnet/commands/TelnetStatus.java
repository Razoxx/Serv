package l2p.gameserver.network.telnet.commands;

import java.lang.management.ManagementFactory;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.commons.lang3.time.DurationFormatUtils;
import l2p.commons.lang.StatsUtils;
import l2p.gameserver.Config;
import l2p.gameserver.GameTimeController;
import l2p.gameserver.Shutdown;
import l2p.gameserver.instancemanager.ReflectionManager;
import l2p.gameserver.model.World;
import l2p.gameserver.network.telnet.TelnetCommand;
import l2p.gameserver.network.telnet.TelnetCommandHolder;
import l2p.gameserver.tables.GmListTable;
import l2p.gameserver.utils.Util;

public class TelnetStatus implements TelnetCommandHolder {

    private Set<TelnetCommand> _commands = new LinkedHashSet<TelnetCommand>();

    public TelnetStatus() {
        _commands.add(new TelnetCommand("status", "s") {
            @Override
            public String getUsage() {
                return "status";
            }

            @Override
            public String handle(String[] args) {
                StringBuilder sb = new StringBuilder();
                int[] stats = World.getStats();

                sb.append("Server Status: ").append("\n\r");
                sb.append("Players: ................. ").append(stats[12]).append("/").append(Config.MAXIMUM_ONLINE_USERS).append("\n\r");
                sb.append("     Online: ............. ").append(stats[12] - stats[13]).append("\n\r");
                sb.append("     Offline: ............ ").append(stats[13]).append("\n\r");
                sb.append("     GM: ................. ").append(GmListTable.getAllGMs().size()).append("\n\r");
                sb.append("Objects: ................. ").append(stats[10]).append("\n\r");
                sb.append("Characters: .............. ").append(stats[11]).append("\n\r");
                sb.append("Summons: ................. ").append(stats[18]).append("\n\r");
                sb.append("Npcs: .................... ").append(stats[15]).append("/").append(stats[14]).append("\n\r");
                sb.append("Monsters: ................ ").append(stats[16]).append("\n\r");
                sb.append("Minions: ................. ").append(stats[17]).append("\n\r");
                sb.append("Doors: ................... ").append(stats[19]).append("\n\r");
                sb.append("Items: ................... ").append(stats[20]).append("\n\r");
                sb.append("Reflections: ............. ").append(ReflectionManager.getInstance().getAll().length).append("\n\r");
                sb.append("Regions: ................. ").append(stats[0]).append("\n\r");
                sb.append("     Active: ............. ").append(stats[1]).append("\n\r");
                sb.append("     Inactive: ........... ").append(stats[2]).append("\n\r");
                sb.append("     Null: ............... ").append(stats[3]).append("\n\r");
                sb.append("Game Time: ............... ").append(getGameTime()).append("\n\r");
                sb.append("Real Time: ............... ").append(getCurrentTime()).append("\n\r");
                sb.append("Start Time: .............. ").append(getStartTime()).append("\n\r");
                sb.append("Uptime: .................. ").append(getUptime()).append("\n\r");
                sb.append("Shutdown: ................ ").append(Util.formatTime(Shutdown.getInstance().getSeconds())).append("/").append(Shutdown.getInstance().getMode()).append("\n\r");
                sb.append("Threads: ................. ").append(Thread.activeCount()).append("\n\r");
                sb.append("RAM Used: ................ ").append(StatsUtils.getMemUsedMb()).append("\n\r");

                return sb.toString();
            }
        });
    }

    @Override
    public Set<TelnetCommand> getCommands() {
        return _commands;
    }

    public static String getGameTime() {
        int t = GameTimeController.getInstance().getGameTime();
        int h = t / 60;
        int m = t % 60;
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, h);
        cal.set(Calendar.MINUTE, m);
        return format.format(cal.getTime());
    }

    public static String getUptime() {
        return DurationFormatUtils.formatDurationHMS(ManagementFactory.getRuntimeMXBean().getUptime());
    }

    public static String getStartTime() {
        return new Date(ManagementFactory.getRuntimeMXBean().getStartTime()).toString();
    }

    public static String getCurrentTime() {
        return new Date().toString();
    }
}
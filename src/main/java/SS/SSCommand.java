package SS;

import org.abo876.excellentServerManager.ExcellentServerManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import utils.Chat;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class SSCommand implements CommandExecutor {
    private final ExcellentServerManager pl;
    private final Chat chat;
    public List<UUID> players = new ArrayList<>();

    public SSCommand(ExcellentServerManager pl) {
        this.pl = pl;
        this.chat = new Chat(pl);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        var player = (Player) sender;

        if (!player.hasPermission("esm.staff.command.ss")) {
            chat.send(player, "prefix", "no-permission");
            return true;

        }
        if (args.length < 1) {
            this.chat.sendList("command-list", player);
            return true;
        }

        Location SSLocation = pl.getSpawn().getLocation("SSSpawn");

        var targetUsername = args[0];
        var targetPlayer = Bukkit.getPlayer(targetUsername);

        if (targetPlayer == null) {
            this.chat.send(sender, "prefix", "player-notfound");
            return true;
        }
        if (pl.getSS().getConfigurationSection("SS_"+targetUsername) == null ||! players.equals(targetPlayer.getUniqueId())) {
            pl.getSS().set("SS_" + targetUsername + ".uuid", targetPlayer.getUniqueId().toString());
            pl.getSS().set("SS_" + targetUsername + ".Executor", player.getName());
            pl.saveSSConfig();
            pl.reloadSSConfig();
            players.add(targetPlayer.getUniqueId());
            if (SSLocation != null) {
                targetPlayer.teleport(SSLocation);
                player.sendMessage("§aTeleported %target_ss% to ss location".replace("%target_ss%", targetUsername));
                var time = pl.getConfig().getLong("SS.Time");

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        var banMessage = pl.getConfig().getString("SS.ban-message");
                        banMessage = ChatColor.translateAlternateColorCodes('&', banMessage);
                        banMessage = banMessage.replace("%target_ss%", targetUsername);
                        Bukkit.dispatchCommand(player, "ban " + targetPlayer.getName() + " " + banMessage);
                        this.cancel();
                    }
                }.runTaskLater(pl, time * 20L);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        targetPlayer.sendMessage("§cYou have been frozen for " + time + " seconds");
                        var ss = pl.getConfig().getString("SS.ss-discord");
                        ss = ss.replace("%target_ss%", targetUsername);
                        ss = ss.replace("%time%", String.valueOf(time));
                        ss = ChatColor.translateAlternateColorCodes('&', ss);
                        targetPlayer.sendMessage(ss);
                    }
                }.runTaskTimer(pl, time * 20L, 20L);
            }else {
                this.chat.send(sender, "prefix", "ss-no-ssspawn");
            }
        } else {
            var spawn = pl.getSpawn().getLocation("Spawn");
            targetPlayer.teleport(spawn);
            var ssremoved = pl.getConfig().getString("SS.ss-removed");
            ssremoved = ssremoved.replace("%target_ss%", targetUsername);
            ssremoved = ChatColor.translateAlternateColorCodes('&', ssremoved);
            player.sendMessage(ssremoved);
            targetPlayer.sendMessage(ssremoved);
            pl.getSS().set("SS_" + targetUsername, null);
            pl.saveSSConfig();
            pl.reloadSSConfig();
            players.remove(targetPlayer.getUniqueId());


        }

        return true;
    }

}
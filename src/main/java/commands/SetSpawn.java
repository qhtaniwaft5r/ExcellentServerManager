//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package commands;


import org.abo876.excellentServerManager.ExcellentServerManager;
import utils.Chat;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetSpawn implements CommandExecutor {
    private final ExcellentServerManager plugin;
    private final Chat chat;

    public SetSpawn(ExcellentServerManager plugin) {
        this.plugin = plugin;
        this.chat = new Chat(plugin);
    }

    public boolean onCommand( CommandSender sender, Command cmd,  String label,  String[] args) {

        if (sender instanceof Player) {
            if (sender.hasPermission("excellentservermanager.setspawn")) {
                this.setTheSpawn(sender);
            } else {
                this.chat.send(sender, "prefix", "no-permission");
            }
        } else {
            this.chat.send(sender, "prefix", "action-noconsole");
        }
        /*} else if (args[0].equalsIgnoreCase("plot")) {
            if (sender instanceof Player) {
                if (sender.hasPermission("excellentservermanager.setspawn")) {
                    this.setPlotSpawn(sender);
                } else {
                    this.chat.send(sender, "prefix", "no-permission");
                }
            } else {
                this.chat.send(sender, "prefix", "action-noconsole");
            }
        }else if (args[0].equalsIgnoreCase("afk")) {
            if (sender instanceof Player) {
                if (sender.hasPermission("excellentservermanager.setspawn")) {
                    this.setAfkSpawn(sender);
                } else {
                    this.chat.send(sender, "prefix", "no-permission");
                }
            } else {
                this.chat.send(sender, "prefix", "action-noconsole");
            }
        this.chat.sendList(sender, "command-list");
        */
        return true;
    }

    public void setTheSpawn(CommandSender sender) {
        if (this.plugin.getBoolean("Toggle.enable-setspawn-command")) {
            Player p = (Player)sender;
            Location l = p.getLocation();
            if (l.getWorld() != null) {
                Location spawn = new Location(l.getWorld(), l.getX(), l.getY(), l.getZ(), l.getYaw(), l.getPitch());
                this.plugin.getSpawn().set("Spawn", spawn);
                this.plugin.saveSpawnConfig();
                this.plugin.reloadSpawnConfig();
                this.chat.send(sender, "prefix", "setspawn-success");
            } else {
                this.chat.send(sender, "prefix", "world-not-loaded");
            }
        } else {
            this.chat.send(sender, "prefix", "command-disabled");
        }
    }
    public void setPlotSpawn(CommandSender sender) {
        if (this.plugin.getBoolean("Toggle.enable-setplot-command")) {
            Player p = (Player)sender;
            Location l = p.getLocation();
            if (l.getWorld() != null) {
                Location spawn = new Location(l.getWorld(), l.getX(), l.getY(), l.getZ(), l.getYaw(), l.getPitch());
                this.plugin.getSpawn().set("Afk", spawn);
                this.plugin.saveSpawnConfig();
                this.plugin.reloadSpawnConfig();
                this.chat.send(sender, "prefix", "setspawn-success");
            } else {
                this.chat.send(sender, "prefix", "world-not-loaded");
            }
        } else {
            this.chat.send(sender, "prefix", "command-disabled");
        }
    }
    public void setAfkSpawn(CommandSender sender) {
        if (this.plugin.getBoolean("Toggle.enable-setafk-command")) {
            Player p = (Player)sender;
            Location l = p.getLocation();
            if (l.getWorld() != null) {
                Location spawn = new Location(l.getWorld(), l.getX(), l.getY(), l.getZ(), l.getYaw(), l.getPitch());
                this.plugin.getSpawn().set("Afk", spawn);
                this.plugin.saveSpawnConfig();
                this.plugin.reloadSpawnConfig();
                this.chat.send(sender, "prefix", "setspawn-success");
            } else {
                this.chat.send(sender, "prefix", "world-not-loaded");
            }
        } else {
            this.chat.send(sender, "prefix", "command-disabled");
        }
    }
}

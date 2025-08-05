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

public class SetFirstSpawn implements CommandExecutor {
    private final ExcellentServerManager plugin;
    private final Chat chat;

    public SetFirstSpawn(ExcellentServerManager plugin) {
        this.plugin = plugin;
        this.chat = new Chat(plugin);
    }

    public boolean onCommand( CommandSender sender, Command cmd,  String label,  String[] args) {
        if (cmd.getName().equalsIgnoreCase("setfirstspawn")) {
            if (sender instanceof Player) {
                if (sender.hasPermission("nspawn.setfistspawn")) {
                    this.setTheFirstSpawn(sender);
                } else {
                    this.chat.send(sender, "prefix", "no-permission");
                }
            } else {
                this.chat.send(sender, "prefix", "action-noconsole");
            }
        }

        return true;
    }

    public void setTheFirstSpawn(CommandSender sender) {
        if (this.plugin.getBoolean("Toggle.enable-setfirstspawn-command")) {
            Player p = (Player)sender;
            Location l = p.getLocation();
            if (l.getWorld() != null) {
                Location firstspawn = new Location(l.getWorld(), l.getX(), l.getY(), l.getZ(), l.getYaw(), l.getPitch());
                this.plugin.getSpawn().set("FirstSpawn", firstspawn);
                this.plugin.saveSpawnConfig();
                this.plugin.reloadSpawnConfig();
                this.chat.send(sender, "prefix", "setfirstspawn-success");
            } else {
                this.chat.send(sender, "prefix", "world-not-loaded");
            }
        } else {
            this.chat.send(sender, "prefix", "command-disabled");
        }

    }
}

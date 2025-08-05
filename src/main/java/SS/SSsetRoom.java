//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package SS;

import org.abo876.excellentServerManager.ExcellentServerManager;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import utils.Chat;

public class SSsetRoom implements CommandExecutor {
    private final ExcellentServerManager plugin;
    private final Chat chat;

    public SSsetRoom(ExcellentServerManager plugin) {
        this.plugin = plugin;
        this.chat = new Chat(plugin);
    }

    public boolean onCommand(CommandSender sender, Command command, String s, String[] strings) {
        if (sender instanceof Player) {
            Player p = (Player)sender;
            if (p.hasPermission("op")) {
                this.chat.send(sender, "prefix", "ss-set-room");
                this.setSSSpawn(sender);
            } else {
                this.chat.send(sender, "prefix", "no-permission");
            }
        } else {
            this.chat.send(sender, "prefix", "action-noconsole");
        }

        return true;
    }

    public void setSSSpawn(CommandSender sender) {
        if (this.plugin.getBoolean("SS.SetCommand")) {
            Player p = (Player)sender;
            Location l = p.getLocation();
            if (l.getWorld() != null) {
                Location SSspawn = new Location(l.getWorld(), l.getX(), l.getY(), l.getZ(), l.getYaw(), l.getPitch());
                this.plugin.getSpawn().set("SSSpawn", SSspawn);
                this.plugin.saveSpawnConfig();
                this.plugin.reloadSpawnConfig();
                this.chat.send(sender, "prefix", "ss-set-room");
            } else {
                this.chat.send(sender, "prefix", "world-not-loaded");
            }
        } else {
            this.chat.send(sender, "prefix", "command-disabled");
        }

    }
}

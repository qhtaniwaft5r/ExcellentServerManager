//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package utils;


import java.util.Iterator;
import java.util.List;
import org.abo876.excellentServerManager.ExcellentServerManager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Chat {
    private final ExcellentServerManager plugin;

    public Chat(ExcellentServerManager plugin) {
        this.plugin = plugin;
    }

    public String formatColor(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    public void send(Player p, String s) {
        p.sendMessage(this.formatColor(s));
    }

    public void send(CommandSender c, String s) {
        c.sendMessage(this.formatColor(s));
    }

    public void send(CommandSender c, String prefix, String path) {
        if (!(c instanceof Player)) {
            if (this.getPath(path).length() > 0) {
                this.send(c, this.getPath(prefix) + this.getPath(path));
            }
        } else if (this.getPath(path).length() > 0) {
            this.send((Player)c, this.getPath(prefix) + this.getPath(path));
        }

    }

    public void send(Player p, String prefix, String path) {
        if (this.getPath(path).length() > 0) {
            this.send(p, this.getPath(prefix) + this.getPath(path));
        }

    }

    public void sendWithCd(Player p, String prefix, String path) {
        if (this.getPath(path).length() > 0) {
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', this.getPath(prefix) + this.getPath(path)).replaceAll("%cooldown%", String.valueOf(this.plugin.getConfig().getInt("Conditions.cooldown"))));
        }

    }

    public void sendWithTarget(CommandSender sender, String p, String prefix, String path) {
        if (this.getPath(path).length() > 0) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', this.getPath(prefix) + this.getPath(path)).replaceAll("%target%", p));
        }

    }

    public void sendWithTargetPlayer(CommandSender sender, Player p, String prefix, String path) {
        if (this.getPath(path).length() > 0) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', this.getPath(prefix) + this.getPath(path)).replaceAll("%target%", p.getName()));
        }

    }

    public void sendList(String path, CommandSender c) {
        List<String> list = this.plugin.getMsg().getStringList(path);
        Iterator var4 = list.iterator();

        while(var4.hasNext()) {
            String m = (String)var4.next();
            this.send(c, m);
        }

    }

    public String getPath(String path) {
        return this.plugin.getMsg().getString(path);
    }
}
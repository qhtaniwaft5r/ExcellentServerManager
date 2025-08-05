package commands;

import org.abo876.excellentServerManager.ExcellentServerManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BroadCast implements CommandExecutor {
    private final ExcellentServerManager pl;
    public BroadCast(ExcellentServerManager pl) {
        this.pl = pl;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if (pl.getConfig().getBoolean("BroadCast.Enable")) {
        if (sender.hasPermission("esm.staff.broadcast")) {
            String title = pl.getConfig().getString("BroadCast.Title");
            String msg = String.join(" ", args);
            msg = ChatColor.translateAlternateColorCodes('&', msg);
            if (title != null)
                title = ChatColor.translateAlternateColorCodes('&', title);
                pl.getServer().broadcastMessage(title);
            pl.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&', "       &l" + msg+"        "));
            for (Player p: Bukkit.getOnlinePlayers()){
                p.sendTitle(title, msg, pl.getConfig().getInt("BroadCast.Fadein"), pl.getConfig().getInt("BroadCast.Stay"), pl.getConfig().getInt("BroadCast.Fadeout"));
            }
        }}
        return true;
    }
}

package SS;

import org.abo876.excellentServerManager.ExcellentServerManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;


public class SSEvent implements Listener {
    private final ExcellentServerManager plugin;

    public SSEvent(ExcellentServerManager plugin) {
        this.plugin = plugin;
    }
    private SSCommand list(){
        return new SSCommand(plugin);
    }
    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        if (e.hasExplicitlyChangedBlock()) {
        if (plugin.getConfig().getConfigurationSection("SS_" + e.getPlayer().getName()) != null || list().players.equals(e.getPlayer().getUniqueId())) {
                e.setCancelled(true);
                e.getPlayer().sendMessage("§c§lYou can't Move Durning SS");
            }
        }
    }
    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {
        if (plugin.getConfig().getConfigurationSection("SS_" + e.getPlayer().getName()) != null|| list().players.equals(e.getPlayer().getUniqueId())) {
            e.setCancelled(true);
            e.getPlayer().sendMessage("§c§lYou can't Drop Items Durning SS");
        }
    }
    @EventHandler
    public void onBreak(PlayerHarvestBlockEvent e) {
        if (plugin.getConfig().getConfigurationSection("SS_" + e.getPlayer().getName()) != null|| list().players.equals(e.getPlayer().getUniqueId())) {
            e.setCancelled(true);
            e.getPlayer().sendMessage("§c§lYou can't Break Blocks Durning SS");
        }
    }
    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        if (plugin.getConfig().getConfigurationSection("SS_" + e.getPlayer().getName()) != null|| list().players.equals(e.getPlayer().getUniqueId())) {
            Player p = e.getPlayer();
            String reason = plugin.getConfig().getString("SS.Reason");
            if (reason != null) {
                reason = ChatColor.translateAlternateColorCodes('&', reason);
                Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "ban " + p.getName() + " " + reason);
            }else {
                Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "ban " + p.getName() + " left durning ss ExcellentServerManager");
            }
        }
    }
    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {
        if (plugin.getConfig().getConfigurationSection("SS_" + e.getPlayer().getName()) != null|| list().players.equals(e.getPlayer().getUniqueId())) {
            e.setCancelled(true);
            e.getPlayer().sendMessage("§c§lYou can't Run Commands Durning SS");
        }
    }
    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        if (plugin.getConfig().getConfigurationSection("SS_" + e.getPlayer().getName()) != null|| list().players.equals(e.getPlayer().getUniqueId())) {
            e.setCancelled(true);
            e.getPlayer().sendMessage("§c§lYou can't Chat Durning SS");
        }
    }
}

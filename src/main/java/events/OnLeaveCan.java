//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package events;


import org.abo876.excellentServerManager.ExcellentServerManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitTask;


public class OnLeaveCan implements Listener {
    private final ExcellentServerManager plugin;

    public OnLeaveCan(ExcellentServerManager plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e) {
        if (this.plugin.getPlayers().containsKey(e.getPlayer().getUniqueId())) {
            this.plugin.cancelTeleport((BukkitTask)this.plugin.getPlayers().get(e.getPlayer().getUniqueId()));
            this.plugin.removePlayer(e.getPlayer().getUniqueId());
        }

    }
}

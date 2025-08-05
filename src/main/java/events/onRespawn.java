//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package events;


import commands.Spawn;
import org.abo876.excellentServerManager.ExcellentServerManager;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scheduler.BukkitTask;


public class onRespawn implements Listener {
    private final ExcellentServerManager plugin;
    private final Spawn spawn;

    public onRespawn(ExcellentServerManager plugin) {
        this.plugin = plugin;
        this.spawn = new Spawn(plugin);
    }

    @EventHandler
    public void onPlayerDeath(PlayerRespawnEvent e) {
        if (this.plugin.getConfig().getBoolean("Conditions.spawn-onDeath") && this.plugin.getSpawn().contains("Spawn")) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, () -> {
                this.spawn.tpToSpawn(e.getPlayer(), false);
            });
            if (this.plugin.getPlayers().containsKey(e.getPlayer().getUniqueId())) {
                this.plugin.cancelTeleport((BukkitTask)this.plugin.getPlayers().get(e.getPlayer().getUniqueId()));
                this.plugin.removePlayer(e.getPlayer().getUniqueId());
            }
        }

    }
}

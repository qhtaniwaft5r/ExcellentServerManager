//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package events;


import org.abo876.excellentServerManager.ExcellentServerManager;
import utils.Chat;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitTask;


public class JointoSpawn implements Listener {
    private final ExcellentServerManager plugin;
    private final Chat chat;

    public JointoSpawn(ExcellentServerManager plugin) {
        this.plugin = plugin;
        this.chat = new Chat(plugin);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        if (e.getTo() != null) {
            if ((e.getFrom().getBlockX() != e.getTo().getBlockX() || e.getFrom().getBlockY() != e.getTo().getBlockY() || e.getFrom().getBlockZ() != e.getTo().getBlockZ()) && this.plugin.getPlayers().containsKey(e.getPlayer().getUniqueId())) {
                this.plugin.cancelTeleport((BukkitTask)this.plugin.getPlayers().get(e.getPlayer().getUniqueId()));
                this.plugin.removePlayer(e.getPlayer().getUniqueId());
                this.chat.send(e.getPlayer(), "prefix", "cancel-teleporting");
            }

        }
    }
}

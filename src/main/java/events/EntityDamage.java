//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package events;

import commands.Spawn;
import org.abo876.excellentServerManager.ExcellentServerManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;


public class EntityDamage implements Listener {
    private final ExcellentServerManager plugin;
    private final Spawn spawn;

    public EntityDamage(ExcellentServerManager plugin) {
        this.plugin = plugin;
        this.spawn = new Spawn(plugin);
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            if (e.getCause().equals(DamageCause.VOID) && this.plugin.getOnVoid()) {
                if (e.getDamage() != 3.4028234663852886E38) {
                    e.setCancelled(true);
                    this.spawn.tpFromVoidToSpawn((Player)e.getEntity(), false);
                }
            } else if (e.getCause().equals(DamageCause.FALL) && this.plugin.getOnFall() && this.plugin.getFallPlayers().contains(e.getEntity().getUniqueId())) {
                e.setCancelled(true);
                this.plugin.removeFallPlayer(e.getEntity().getUniqueId());
            }
        }

    }
}

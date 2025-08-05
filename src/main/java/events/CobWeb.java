package events;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

public class CobWeb implements Listener {
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        if (item != null && item.getType() == Material.FIREWORK_ROCKET) {
            Block block = player.getLocation().getBlock();
            if (block.getType() == Material.COBWEB) {
                event.setCancelled(true);
            }
        }

    }

    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        if (event.getEntity().getShooter() instanceof Player) {
            Player player = (Player)event.getEntity().getShooter();
            if (event.getEntity() instanceof Firework) {
                Block block = player.getLocation().getBlock();
                if (block.getType() == Material.COBWEB) {
                    event.setCancelled(true);
                }
            }
        }

    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Block block = player.getLocation().getBlock();
        if (block.getType() == Material.COBWEB && player.isGliding()) {
            player.setGliding(false);
        }

    }

}

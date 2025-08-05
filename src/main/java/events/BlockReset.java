package events;

import org.abo876.excellentServerManager.ExcellentServerManager;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.List;


public class BlockReset implements Listener {
    private final ExcellentServerManager plugin;

    public BlockReset(ExcellentServerManager plugin) {
        this.plugin = plugin;
    }
    private List<String> worlds;

    @EventHandler
    public void onPlaceBlock(BlockPlaceEvent e) {
        if (plugin.getBoolean("BlockReset.Enabled")) {
            worlds = plugin.getConfig().getStringList("BlockReset.World");
            Player p = e.getPlayer();
            if (!worlds.contains(e.getBlockPlaced().getWorld().getName())) return;
            if (p.getGameMode() == GameMode.SURVIVAL) {
                Block block = e.getBlock();
                int time = plugin.getConfig().getInt("BlockReset.Time");

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        block.setType(Material.AIR);
                    }
                }.runTaskLater(this.plugin, time * 20L);
            }
        }
        }
        }

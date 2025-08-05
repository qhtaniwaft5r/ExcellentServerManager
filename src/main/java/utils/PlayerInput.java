package utils;


import org.abo876.excellentServerManager.ExcellentServerManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;



public class PlayerInput implements Listener {

    private static final Map<UUID, PlayerInput> inputs = new HashMap<>();
    private final UUID uuid;


    private final InputRunnable runGo;
    private final InputRunnable runCancel;
    private final BukkitTask taskId;
    private boolean clearInput = true;

    public PlayerInput(final boolean clearInput, final Player p, final InputRunnable correct, final InputRunnable cancel) {
        this.uuid = p.getUniqueId();
        p.closeInventory();
        this.runGo = correct;
        this.runCancel = cancel;
        this.clearInput = clearInput;
        this.taskId = new BukkitRunnable() {
            public void run() {
                p.getPlayer().sendTitle(ChatColor.GRAY +"Please Send to The Chat", ChatColor.GRAY + "Your Input", 0, 21, 0);
            }
        }.runTaskTimer(ExcellentServerManager.getPlugin(), 0L, 20);

        this.register();
    }

    public static Listener getListener() {
        return new Listener() {
            @EventHandler(priority = EventPriority.HIGHEST)
            public void onPlayerChat(final AsyncPlayerChatEvent event) {
                final Player p = event.getPlayer();
                final String input = event.getMessage();
                final UUID uuid = p.getUniqueId();

                if (inputs.containsKey(uuid)) {
                    event.setCancelled(true);
                    handlePlayerInput(p, input, uuid);
                }
            }
        };
    }

    private static void handlePlayerInput(final Player p, String input, final UUID uuid) {
        final PlayerInput current = inputs.get(uuid);

        if (current.clearInput) {
            input = new Chat(ExcellentServerManager.getPlugin()).formatColor(input);
        }

        try {
            current.taskId.cancel();
            p.sendTitle("", "", 0, 1, 0);
            current.unregister();
            String cleanInput = new Chat(ExcellentServerManager.getPlugin()).formatColor(input);
            if (input.equalsIgnoreCase("cancel")) {
                p.sendMessage("has canceled the input");
                Bukkit.getScheduler().scheduleSyncDelayedTask(ExcellentServerManager.getPlugin(), () -> current.runCancel.run(cleanInput), 3);
            } else {
                Bukkit.getScheduler().scheduleSyncDelayedTask(ExcellentServerManager.getPlugin(), () -> current.runGo.run(cleanInput), 3);
            }
        } catch (final Exception e) {

            ExcellentServerManager.getPlugin().getLogger().warning(e.getMessage());
        }
    }

    private void register() {
        inputs.put(this.uuid, this);
    }

    private void unregister() {
        inputs.remove(this.uuid);
    }

    @FunctionalInterface
    public interface InputRunnable {
        void run(String input);
    }
}

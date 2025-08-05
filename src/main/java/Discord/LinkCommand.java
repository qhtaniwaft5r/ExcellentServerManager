package Discord;



import org.abo876.excellentServerManager.ExcellentServerManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;
import java.util.logging.Level;

public class LinkCommand implements CommandExecutor {

    private final ExcellentServerManager plugin;

    public LinkCommand(ExcellentServerManager plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (plugin.getDiscord().getBoolean("advanced.debug" , false)) plugin.getLogger().log(Level.INFO, "[DEBUG MODE] ✅ Start Link Command, checking if Discord is enabled");

    if (plugin.getDiscord().getBoolean("Discord.Enabled")) {
        if (plugin.getDiscord().getBoolean("advanced.debug" , false)) plugin.getLogger().log(Level.INFO, "[DEBUG MODE] ✅ Discord is enabled, Checking if sender is a player");

        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cOnly players can use this command!");
            return true;
        }

        UUID playerId = player.getUniqueId();

        if (plugin.getPlayerDiscordLinks().containsKey(playerId)) {
            if (plugin.getDiscord().getBoolean("advanced.debug" , false)) plugin.getLogger().log(Level.INFO, "[DEBUG MODE] ✅ checking if player is already linked");

            player.sendMessage("§cYou are already linked to Discord! Use /unlink to remove the link first.");
            return true;
        }

        if (plugin.getDiscord().getBoolean("advanced.debug" , false)) plugin.getLogger().log(Level.INFO, "[DEBUG MODE] ✅ Player is not linked, starting Generate Verification Code");

        // Generate verification code
        String code = plugin.generateVerificationCode();
        plugin.getPendingLinks().put(code, playerId.toString());

        player.sendMessage("§e" + "=".repeat(50));
        player.sendMessage("§6§l🔗 Discord Link Verification");
        player.sendMessage("§e" + "=".repeat(50));
        player.sendMessage("§fTo link your Discord account:");
        player.sendMessage("§f1. §bGo to the Discord server");
        player.sendMessage("§f2. §bType: §e!verify " + code);
        player.sendMessage("§f3. §bWait for confirmation");
        player.sendMessage("§e" + "=".repeat(50));
        player.sendMessage("§c⚠ Code expires in 5 minutes!");
        player.sendMessage("§a💡 After linking, you'll get Discord roles as game permissions!");
        if (plugin.getDiscord().getBoolean("advanced.debug" , false)) plugin.getLogger().log(Level.INFO, "[DEBUG MODE] ✅ Verification code sent to player, waiting for confirmation");

        // Remove code after 5 minutes
        new BukkitRunnable() {
            @Override
            public void run() {
                if (plugin.getPendingLinks().containsKey(code)) {
                    plugin.getPendingLinks().remove(code);
                    if (player.isOnline()) {
                        if (plugin.getDiscord().getBoolean("advanced.debug" , false)) plugin.getLogger().log(Level.INFO, "[DEBUG MODE] ✅ Code expired, sending message to player");

                        player.sendMessage("§c⏰ Your verification code has expired. Please use /link again.");
                    }
                }
            }
        }.runTaskLater(plugin, 6000); // 5 minutes = 6000 ticks
    }else {
        sender.sendMessage("§cDiscord is not enabled!");
    }
        return true;
    }
}
package Discord;


import net.luckperms.api.node.metadata.NodeMetadataKey;
import org.abo876.excellentServerManager.ExcellentServerManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;

import net.luckperms.api.model.user.User;

import java.util.UUID;
import java.util.logging.Level;

public class UnlinkCommand implements CommandExecutor {

    private final ExcellentServerManager plugin;

    public UnlinkCommand(ExcellentServerManager plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (plugin.getDiscord().getBoolean("advanced.debug" , false)) plugin.getLogger().log(Level.INFO, "[DEBUG MODE] ✅ Starting Unlink Command From " + sender.getName() + " , Checking if Discord is enabled");

        Player player = (Player) sender;
        if (plugin.getDiscord().getBoolean("Discord.Enabled")) {
            if (plugin.getDiscord().getBoolean("advanced.debug" , false)) plugin.getLogger().log(Level.INFO, "[DEBUG MODE] ✅ Checking if Sender is a player");

            if (!(sender instanceof Player)) {
                sender.sendMessage("§cOnly players can use this command!");
                return true;
            }
            if (plugin.getDiscord().getBoolean("advanced.debug" , false)) plugin.getLogger().log(Level.INFO, "[DEBUG MODE] ✅ The Sender is a player, Checking if the player is linked to Discord");

            UUID playerId = player.getUniqueId();

            if (!plugin.getPlayerDiscordLinks().containsKey(playerId)) {
                player.sendMessage("§cYou are not linked to Discord!");
                return true;
            }
            if (plugin.getDiscord().getBoolean("advanced.debug" , false)) plugin.getLogger().log(Level.INFO, "[DEBUG MODE] ✅ The player is linked to Discord, Unlinking...");

            // Remove Discord-synced permissions
            if (plugin.getLuckPerms() != null) {
                User user = plugin.getLuckPerms().getUserManager().getUser(playerId);
                if (user != null) {
                    NodeMetadataKey<String> discordSyncedKey = NodeMetadataKey.of("discord-synced", String.class);
                    user.data().clear(node -> {
                        node.getMetadata(discordSyncedKey);
                        plugin.removeVerifyPermission(playerId);
                        return true;
                    });
                    plugin.getLuckPerms().getUserManager().saveUser(user);
                }
            }

            // Remove the link
            plugin.getPlayerDiscordLinks().remove(playerId);
            plugin.saveLinks();

            player.sendMessage("§e" + "=".repeat(40));
            player.sendMessage("§a✅ Successfully Unlinked!");
            player.sendMessage("§e" + "=".repeat(40));
            player.sendMessage("§fYour Discord account has been unlinked.");
            player.sendMessage("§fAll Discord-synced permissions have been removed.");
            player.sendMessage("§e" + "=".repeat(40));
            if (plugin.getDiscord().getBoolean("advanced.debug" , false)) plugin.getLogger().log(Level.INFO, "[DEBUG MODE] ✅ Successfully Unlinked! "+player.getName());

        }else {
            player.sendMessage("§cDiscord is not enabled!");
        }
        return true;
    }
}
package Discord;


import org.abo876.excellentServerManager.ExcellentServerManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.List;
import java.util.logging.Level;

public class LinkedCommand implements CommandExecutor {

    private final ExcellentServerManager plugin;

    public LinkedCommand(ExcellentServerManager plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender,@NotNull Command command,@NotNull String label, String[] args) {
        if (plugin.getDiscord().getBoolean("advanced.debug" , false)) plugin.getLogger().log(Level.INFO, "[DEBUG MODE] ✅ Start Linked Command, checking if Discord is enabled");

        if (plugin.getDiscord().getBoolean("Discord.Enabled")) {
            if (plugin.getDiscord().getBoolean("advanced.debug" , false)) plugin.getLogger().log(Level.INFO, "[DEBUG MODE] ✅ Discord is enabled, checking if sender is Console");

            if (!(sender instanceof Player)) {
                sender.sendMessage("§cOnly players can use this command!");
                return true;
            }
            if (plugin.getDiscord().getBoolean("advanced.debug" , false)) plugin.getLogger().log(Level.INFO, "[DEBUG MODE] ✅ It's a player, checking if they are linked");

            Player player = (Player) sender;
            UUID playerId = player.getUniqueId();

            if (!plugin.getPlayerDiscordLinks().containsKey(playerId)) {
                player.sendMessage("§c❌ You are not linked to Discord!");
                player.sendMessage("§eUse /link to connect your account.");
                if (plugin.getDiscord().getBoolean("advanced.debug" , false)) plugin.getLogger().log(Level.INFO, "[DEBUG MODE] ✅ Player is not linked");

                return true;
            }
            if (plugin.getDiscord().getBoolean("advanced.debug", false)) plugin.getLogger().log(Level.INFO, "[DEBUG MODE] ✅ Player is linked");

            String discordId = plugin.getPlayerDiscordLinks().get(playerId);
            if (plugin.getGuild() != null) {
                Member member = plugin.getGuild().getMemberById(discordId);
                if (member != null) {
                    player.sendMessage("§e" + "=".repeat(45));
                    player.sendMessage("§a§l🔗 Discord Account Info");
                    player.sendMessage("§e" + "=".repeat(45));
                    player.sendMessage("§fLinked to: §b" + member.getUser().getName() + "#" + member.getUser().getDiscriminator());
                    player.sendMessage("§fNickname: §b" + (member.getNickname() != null ? member.getNickname() : "None"));

                    // Show Discord roles
                    List<Role> roles = member.getRoles();
                    if (!roles.isEmpty()) {
                        player.sendMessage("§fDiscord Roles:");
                        for (Role role : roles) {
                            String syncStatus = plugin.getRolePermissionMap().containsKey(role.getId()) ? "§a✓" : "§7-";
                            player.sendMessage("  " + syncStatus + " §f" + role.getName());
                        }
                    } else {
                        player.sendMessage("§fDiscord Roles: §7None");
                    }

                    player.sendMessage("§e" + "=".repeat(45));
                    player.sendMessage("§7§oUse /unlink to disconnect your account");

                } else {
                    player.sendMessage("§c❌ Linked Discord user not found in server!");
                    player.sendMessage("§eThey may have left the Discord server.");
                }
            } else {
                player.sendMessage("§c❌ Discord connection error!");
            }
        }
        return true;
    }
}
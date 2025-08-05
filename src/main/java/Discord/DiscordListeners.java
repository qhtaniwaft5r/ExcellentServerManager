package Discord;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.guild.GuildBanEvent;
import net.dv8tion.jda.api.events.guild.GuildUnbanEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleAddEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleRemoveEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.abo876.excellentServerManager.ExcellentServerManager;

import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class DiscordListeners extends ListenerAdapter implements Listener {
    private final ExcellentServerManager plugin;

    public DiscordListeners(ExcellentServerManager plugin) {
        this.plugin = plugin;
        debugLog("DiscordListeners initialized");
    }

    // Helper method for consistent debug logging
    private void debugLog(String message) {
        if (plugin.getDiscord().getBoolean("advanced.debug", false)) {
            plugin.getLogger().log(Level.INFO, "[DEBUG MODE] ✅ " + message);
        }
    }

    private void debugLog(String message, Object... args) {
        if (plugin.getDiscord().getBoolean("advanced.debug", false)) {
            plugin.getLogger().log(Level.INFO, "[DEBUG MODE] ✅ " + String.format(message, args));
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onChat(AsyncPlayerChatEvent ev) {
        debugLog("Chat event triggered for player: %s", ev.getPlayer().getName());

        String eventMessage = ev.getMessage();
        Player p = ev.getPlayer();

        debugLog("Chat message: '%s'", eventMessage);
        debugLog("Discord enabled: %s, Chat relay enabled: %s",
                plugin.getDiscord().getBoolean("Discord.Enabled"),
                plugin.getConfig().getBoolean("settings.chat-relay"));

        if (plugin.getDiscord().getBoolean("Discord.Enabled") && plugin.getConfig().getBoolean("settings.chat-relay")) {
            debugLog("Relaying chat message to Discord");
            plugin.onChat(p, eventMessage);
        } else {
            debugLog("Chat relay skipped - conditions not met");
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onDeath(PlayerDeathEvent ev) {
        Player p = ev.getEntity();
        debugLog("Death event triggered for player: %s", p.getName());
        debugLog("Death message: %s", ev.getDeathMessage());

        if (plugin.getConfig().getBoolean("Discord.Enabled")) {
            debugLog("Processing death event for Discord");
            plugin.onDeath(p, ev);
        } else {
            debugLog("Death event processing skipped - Discord disabled");
        }
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent ev) {
        Player p = ev.getPlayer();
        String eventCommand = ev.getMessage();

        debugLog("Command event triggered by player: %s", p.getName());
        debugLog("Command: %s", eventCommand);

        if (plugin.getBoolean("Discord.Enabled")) {
            debugLog("Processing command for Discord");
            plugin.onCommand(p, eventCommand);
        } else {
            debugLog("Command processing skipped - Discord disabled");
        }
    }

    @EventHandler
    public void onBan(PlayerQuitEvent event) {
        debugLog("Player quit event triggered for: %s", event.getPlayer().getName());
        debugLog("Checking if player is banned");

        if (event.getPlayer().isBanned()) {
            debugLog("Player %s is banned", event.getPlayer().getName());
            var playerId = event.getPlayer().getUniqueId();
            debugLog("Player UUID: %s", playerId.toString());
            debugLog("Checking if player is linked to Discord and if Discord is enabled");

            if (plugin.getPlayerDiscordLinks().containsKey(playerId) && plugin.getConfig().getBoolean("Discord.Enabled")) {
                debugLog("Player is linked to Discord and Discord is enabled");
                String discordId = plugin.getPlayerDiscordLinks().get(playerId);
                debugLog("Discord ID: %s", discordId);
                debugLog("Checking if guild is null");

                if (plugin.getGuild() != null) {
                    debugLog("Guild is not null");
                    Member member = plugin.getGuild().getMemberById(discordId);
                    debugLog("Checking if member is null");

                    if (member != null) {
                        debugLog("Member is not null: %s", member.getEffectiveName());
                        debugLog("Processing Discord ban");

                        try {
                            BanList banList = Bukkit.getBanList(BanList.Type.NAME);
                            var banEntry = banList.getBanEntry(event.getPlayer().getName());

                            if (banEntry != null && banEntry.getExpiration() != null) {
                                debugLog("Ban expiration: %s", banEntry.getExpiration().toString());
                                member.ban(Integer.parseInt(banEntry.getExpiration().toString()), TimeUnit.HOURS).queue(
                                        success -> debugLog("Successfully banned Discord user: %s", member.getEffectiveName()),
                                        error -> debugLog("Failed to ban Discord user: %s", error.getMessage())
                                );
                            } else {
                                debugLog("Ban entry or expiration is null");
                            }
                        } catch (Exception e) {
                            debugLog("Error processing Discord ban: %s", e.getMessage());
                        }
                    } else {
                        debugLog("Discord member not found in guild");
                    }
                } else {
                    debugLog("Guild is null");
                }
            } else {
                debugLog("Player not linked to Discord or Discord disabled");
            }
        } else {
            debugLog("Player %s is not banned", event.getPlayer().getName());
        }
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        debugLog("Message received from user: %s", event.getAuthor().getName());
        debugLog("Message content: '%s'", event.getMessage().getContentRaw());
        debugLog("Channel: %s", event.getChannel().getName());

        if (plugin.getDiscord().getBoolean("Discord.Enabled")) {
            debugLog("Discord is enabled, processing message");

            if (event.getAuthor().isBot() || event.isWebhookMessage()) {
                debugLog("Message from bot or webhook, ignoring");
                return;
            }

            if (event.getChannel().equals(plugin.getChatChannel())) {
                debugLog("Message is from designated chat channel");
                String message = event.getMessage().getContentRaw();

                if (message.startsWith("!verify ")) {
                    debugLog("Processing verification command");
                    handleVerification(event, message);
                } else {
                    debugLog("Processing regular chat message");
                    if (plugin.getDiscord().getBoolean("settings.chat-relay")) {
                        debugLog("Chat relay enabled, forwarding to Minecraft");
                        plugin.onChatCommand(event);
                    } else {
                        debugLog("Chat relay disabled");
                    }
                }
            } else {
                debugLog("Message not from designated chat channel");
            }
        } else {
            debugLog("Discord is disabled, ignoring message");
        }
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        debugLog("Slash command received: %s", event.getName());
        debugLog("User: %s", event.getUser().getName());

        if (plugin.getDiscord().getBoolean("Discord.Enabled")) {
            if (event.getGuild() == null) {
                debugLog("Command not from guild, ignoring");
                return;
            }

            debugLog("Processing slash command: %s", event.getName());

            switch (event.getName()) {
                case "players":
                    debugLog("Executing players command");
                    List<Player> onlinePlayers = (List<Player>) plugin.getServer().getOnlinePlayers();
                    ArrayList<String> playerNames = new ArrayList<>();
                    for (Player player : onlinePlayers) {
                        playerNames.add(player.getName());
                    }
                    debugLog("Found %d online players: %s", onlinePlayers.size(), playerNames);
                    event.reply("There Is : " + onlinePlayers.size() + " \n" + "Players: ```" + String.join("\n", playerNames) + "```").setEphemeral(true).queue();
                    break;

                case "op":
                    String opPlayer = Objects.requireNonNull(event.getOption("player")).getAsString().replace(" ", "").trim();
                    debugLog("Executing op command for player: %s", opPlayer);
                    plugin.getServer().getScheduler().runTask(plugin, bukkitTask -> plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "op " + opPlayer));
                    event.reply("Op : " + opPlayer).setEphemeral(true).queue();
                    break;

                case "bc":
                    String bcMessage = Objects.requireNonNull(event.getOption("message")).getAsString();
                    debugLog("Executing broadcast command with message: %s", bcMessage);
                    plugin.getServer().getScheduler().runTask(plugin, bukkitTask -> plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "bc " + bcMessage));
                    event.reply("BroadCast " + bcMessage).setEphemeral(true).queue();
                    break;

                case "deop":
                    String deopPlayer = Objects.requireNonNull(event.getOption("player")).getAsString().replace(" ", "").trim();
                    debugLog("Executing deop command for player: %s", deopPlayer);
                    plugin.getServer().getScheduler().runTask(plugin, bukkitTask -> plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "deop " + deopPlayer));
                    event.reply("Deop " + deopPlayer).setEphemeral(true).queue();
                    break;

                case "mute":
                    String mutePlayer = Objects.requireNonNull(event.getOption("player")).getAsString().replace(" ", "").trim();
                    debugLog("Executing mute command for player: %s", mutePlayer);
                    plugin.getServer().getScheduler().runTask(plugin, bukkitTask -> plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "mute " + mutePlayer));
                    event.reply("Mute " + mutePlayer).setEphemeral(true).queue();
                    break;

                case "unmute":
                    String unmutePlayer = Objects.requireNonNull(event.getOption("player")).getAsString().replace(" ", "").trim();
                    debugLog("Executing unmute command for player: %s", unmutePlayer);
                    plugin.getServer().getScheduler().runTask(plugin, bukkitTask -> plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "unmute " + unmutePlayer));
                    event.reply("Unmute " + unmutePlayer).setEphemeral(true).queue();
                    break;

                case "ban":
                    String banPlayer = Objects.requireNonNull(event.getOption("player")).getAsString().replace(" ", "").trim();
                    debugLog("Executing ban command for player: %s", banPlayer);
                    plugin.getServer().getScheduler().runTask(plugin, bukkitTask -> plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "ban " + banPlayer));
                    event.reply("Ban " + banPlayer).setEphemeral(true).queue();
                    break;

                case "command":
                    String customCommand = Objects.requireNonNull(event.getOption("command")).getAsString();
                    debugLog("Executing custom command: %s", customCommand);
                    plugin.getServer().getScheduler().runTask(plugin, bukkitTask -> plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), customCommand));
                    event.reply("Command : " + customCommand).setEphemeral(true).queue();
                    break;

                case "unban":
                    String unbanPlayer = Objects.requireNonNull(event.getOption("player")).getAsString().replace(" ", "").trim();
                    debugLog("Executing unban command for player: %s", unbanPlayer);
                    plugin.getServer().getScheduler().runTask(plugin, bukkitTask -> plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "unban " + unbanPlayer));
                    event.reply("Unban " + unbanPlayer).setEphemeral(true).queue();
                    break;

                case "kick":
                    String kickPlayer = Objects.requireNonNull(event.getOption("player")).getAsString().replace(" ", "").trim();
                    debugLog("Executing kick command for player: %s", kickPlayer);
                    plugin.getServer().getScheduler().runTask(plugin, bukkitTask -> plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "kick " + kickPlayer));
                    event.reply("Kicked " + kickPlayer).setEphemeral(true).queue();
                    break;

                default:
                    debugLog("Unknown command: %s", event.getName());
                    event.reply("I can't handle that command right now :(").setEphemeral(true).queue();
            }
        } else {
            debugLog("Discord disabled, ignoring slash command");
        }
    }

    private void handleVerification(MessageReceivedEvent event, String message) {
        debugLog("Processing verification request from user: %s", event.getAuthor().getName());

        if (plugin.getDiscord().getBoolean("Discord.Enabled")) {
            String code = message.substring(8).trim();
            debugLog("Verification code: %s", code);

            if (plugin.getPendingLinks().containsKey(code)) {
                debugLog("Verification code found in pending links");
                String playerUUID = plugin.getPendingLinks().get(code);
                debugLog("Player UUID: %s", playerUUID);

                Player player = plugin.getServer().getPlayer(UUID.fromString(playerUUID));

                if (player != null && player.isOnline()) {
                    debugLog("Player %s is online, proceeding with verification", player.getName());

                    plugin.getPlayerDiscordLinks().put(UUID.fromString(playerUUID), event.getAuthor().getId());
                    plugin.getPendingLinks().remove(code);
                    plugin.saveLinks();
                    debugLog("Player linked and pending code removed");

                    Member member = event.getGuild().getMember(event.getAuthor());
                    if (member != null) {
                        debugLog("Discord member found, syncing roles and nickname");

                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                try {
                                    // Update nickname
                                    String nickname = player.getName() + " | " + member.getEffectiveName();
                                    debugLog("Setting nickname to: %s", nickname);

                                    member.modifyNickname(nickname).queue(
                                            success -> debugLog("Nickname updated successfully"),
                                            error -> debugLog("Failed to update nickname: %s", error.getMessage())
                                    );

                                    if (plugin.getDiscord().getBoolean("settings.role-change-notifications")) {
                                        event.getChannel().sendMessage("✅ Successfully Synced roles for **" + player.getName() + "**!").queue();
                                    }

                                    if (plugin.getDiscord().getBoolean("settings.sync-roles")) {
                                        debugLog("Syncing player roles");
                                        plugin.syncPlayerRoles(player, member);
                                    }

                                    // Add player role
                                    Long playerRoleId = plugin.getDiscord().getLong("Discord.Roles.Player");
                                    if (playerRoleId != null) {
                                        debugLog("Adding player role: %d", playerRoleId);
                                        plugin.getGuild().addRoleToMember(member, Objects.requireNonNull(plugin.getGuild().getRoleById(playerRoleId))).queue();
                                    }

                                    plugin.addVerifyPermission(playerUUID);
                                    debugLog("Verification process completed successfully");

                                } catch (Exception e) {
                                    debugLog("Error during verification process: %s", e.getMessage());
                                }
                            }
                        }.runTask(plugin);
                    } else {
                        debugLog("Discord member not found in guild");
                    }

                    event.getChannel().sendMessage("✅ Successfully linked to **" + player.getName() + "**!").queue();
                    player.sendMessage("§aSuccessfully linked to Discord user: " + event.getAuthor().getName());
                } else {
                    debugLog("Player not found or offline");
                    event.getChannel().sendMessage("❌ Player not found or offline!").queue();
                }
            } else {
                debugLog("Invalid or expired verification code");
                event.getChannel().sendMessage("❌ Invalid or expired verification code!").queue();
            }
        } else {
            debugLog("Discord disabled, ignoring verification request");
        }
    }

    @Override
    public void onGuildMemberRoleAdd(@NotNull GuildMemberRoleAddEvent event) {
        debugLog("Role add event for user: %s", event.getUser().getName());

        if (plugin.getDiscord().getBoolean("Discord.Enabled")) {
            String discordId = event.getUser().getId();
            UUID playerId = getPlayerIdFromDiscord(discordId);
            var channel = plugin.getChatChannel();

            List<String> roleNames = new ArrayList<>();
            for (Role role : event.getRoles()) {
                roleNames.add(role.getName());
            }
            debugLog("Roles added: %s", String.join(", ", roleNames));

            if (channel != null) {
                if (playerId != null) {
                    debugLog("Player found for Discord ID: %s", playerId.toString());
                    Player player = Bukkit.getPlayer(playerId);

                    if (player != null && player.isOnline()) {
                        debugLog("Player %s is online, syncing roles", player.getName());

                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                if (plugin.getDiscord().getBoolean("settings.role-change-notifications")) {
                                    channel.sendMessage("✅ Successfully Synced roles for **" + player.getName() + " User: " + "<@" + discordId + ">" + "**!").queue();
                                }
                                if (plugin.getDiscord().getBoolean("settings.sync-roles")) {
                                    debugLog("Syncing roles for player: %s", player.getName());
                                    plugin.syncPlayerRoles(player, event.getMember());
                                }
                            }
                        }.runTask(plugin);

                        player.sendMessage("§aDiscord roles added: " + String.join(", ", roleNames));
                    } else {
                        debugLog("Player not online");
                    }
                } else {
                    debugLog("No player found for Discord ID: %s", discordId);
                }
            } else {
                debugLog("Chat channel is null");
            }
        } else {
            debugLog("Discord disabled, ignoring role add event");
        }
    }

    @Override
    public void onGuildMemberRoleRemove(@NotNull GuildMemberRoleRemoveEvent event) {
        debugLog("Role remove event for user: %s", event.getUser().getName());

        if (plugin.getDiscord().getBoolean("Discord.Enabled")) {
            String discordId = event.getUser().getId();
            UUID playerId = getPlayerIdFromDiscord(discordId);
            var channel = plugin.getChatChannel();

            List<String> roleNames = new ArrayList<>();
            for (Role role : event.getRoles()) {
                roleNames.add(role.getName());
            }
            debugLog("Roles removed: %s", String.join(", ", roleNames));

            if (playerId != null) {
                debugLog("Player found for Discord ID: %s", playerId.toString());
                Player player = Bukkit.getPlayer(playerId);

                if (player != null && player.isOnline()) {
                    debugLog("Player %s is online, syncing roles", player.getName());

                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            if (plugin.getDiscord().getBoolean("settings.role-change-notifications")) {
                                channel.sendMessage("✅ Successfully Synced roles for **" + player.getName() + " User: " + "<@" + discordId + ">" + "**!").queue();
                            }
                            if (plugin.getDiscord().getBoolean("settings.sync-roles")) {
                                debugLog("Syncing roles for player: %s", player.getName());
                                plugin.syncPlayerRoles(player, event.getMember());
                            }
                            plugin.addVerifyPermission(player.getUniqueId().toString());
                        }
                    }.runTask(plugin);

                    player.sendMessage("§cDiscord roles removed: " + String.join(", ", roleNames));
                } else {
                    debugLog("Player not online");
                }
            } else {
                debugLog("No player found for Discord ID: %s", discordId);
                if (channel != null) {
                    channel.sendMessage("<@" + discordId + "> **does not verify his account");
                }
            }
        } else {
            debugLog("Discord disabled, ignoring role remove event");
        }
    }

    @Override
    public void onGuildBan(@NotNull GuildBanEvent event) {
        debugLog("Guild ban event for user: %s", event.getUser().getName());

        if (plugin.getDiscord().getBoolean("Discord.Enabled")) {
            String discordId = event.getUser().getId();
            debugLog("Discord ID: %s", discordId);

            event.getGuild().retrieveBan(event.getUser()).queue(ban -> {
                String reason = ban.getReason();
                if (reason == null) reason = "No reason provided";
                debugLog("Ban reason: %s", reason);

                String finalReason = reason;
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        debugLog("Processing Minecraft ban for Discord user: %s", discordId);
                        plugin.banPlayerFromDiscord(discordId, finalReason);
                    }
                }.runTask(plugin);
            }, error -> debugLog("Failed to retrieve ban info: %s", error.getMessage()));
        } else {
            debugLog("Discord disabled, ignoring ban event");
        }
    }

    @Override
    public void onGuildUnban(@NotNull GuildUnbanEvent event) {
        debugLog("Guild unban event for user: %s", event.getUser().getName());

        if (plugin.getDiscord().getBoolean("Discord.Enabled")) {
            String discordId = event.getUser().getId();
            debugLog("Discord ID: %s", discordId);

            new BukkitRunnable() {
                @Override
                public void run() {
                    debugLog("Processing Minecraft unban for Discord user: %s", discordId);
                    plugin.unbanPlayerFromDiscord(discordId);
                }
            }.runTask(plugin);
        } else {
            debugLog("Discord disabled, ignoring unban event");
        }
    }

    private UUID getPlayerIdFromDiscord(String discordId) {
        debugLog("Looking up player ID for Discord ID: %s", discordId);

        if (plugin.getDiscord().getBoolean("Discord.Enabled")) {
            for (java.util.Map.Entry<UUID, String> entry : plugin.getPlayerDiscordLinks().entrySet()) {
                if (entry.getValue().equals(discordId)) {
                    debugLog("Found player ID: %s", entry.getKey().toString());
                    return entry.getKey();
                }
            }
            debugLog("No player ID found for Discord ID: %s", discordId);
        }
        return null;
    }
}
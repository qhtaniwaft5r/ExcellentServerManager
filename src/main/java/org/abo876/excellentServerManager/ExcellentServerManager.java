package org.abo876.excellentServerManager;

import Discord.*;
import SS.SSCommand;
import SS.SSEvent;
import SS.SSsetRoom;
import commands.*;
import events.*;
import net.dv8tion.jda.api.*;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.InteractionContextType;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.managers.AudioManager;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.data.DataMutateResult;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.InheritanceNode;
import net.luckperms.api.node.types.PermissionNode;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import utils.GUI;
import utils.ServerVersion;

import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.logging.*;
import java.util.stream.Collectors;

public final class ExcellentServerManager extends JavaPlugin {
    private static ExcellentServerManager plugin;
    private FileConfiguration spawnConfig;
    private FileConfiguration msgConfig;
    private FileConfiguration ssConfig;
    private FileConfiguration discordConfig;
    private final Map<UUID, BukkitTask> players = new HashMap<>();
    private final ArrayList<UUID> fall = new ArrayList<>();
    private final ArrayList<UUID> voidPlayer = new ArrayList<>();
    private boolean onMoveEnabled = true;
    public static JDA jda;
    private boolean onFall;
    private boolean onJoin;
    private boolean onVoid;
    private FileConfiguration linksConfig;
    private File linksFile;
    private final Map<UUID, String> playerDiscordLinks = new HashMap<>();
    private final Map<String, String> pendingLinks = new HashMap<>();
    private TextChannel chatChannel;
    private Guild guild;
    private LuckPerms luckPerms;
    private final Map<String, String> rolePermissionMap = new HashMap<>();

    public ExcellentServerManager() {
    }

    // Helper method for consistent debug logging
    private void debugLog(String message) {
        if (getDiscord().getBoolean("advanced.debug", false)) {
            getLogger().log(Level.INFO, "[DEBUG MODE] ✅ " + message);
        }
    }

    private void debugLog(String message, Object... args) {
        if (getDiscord().getBoolean("advanced.debug", false)) {
            getLogger().log(Level.INFO, "[DEBUG MODE] ✅ " + String.format(message, args));
        }
    }

    @Override
    public void onEnable() {
        super.saveDefaultConfig();



        this.spawnConfig = this.loadCustomConfig("spawn.yml", new File(this.getDataFolder(), "spawn.yml"));
        this.ssConfig = this.loadCustomConfig("ssplayers.yml", new File(this.getDataFolder(), "ssplayers.yml"));
        this.msgConfig = this.loadCustomConfig("messages.yml", new File(this.getDataFolder(), "messages.yml"));
        this.discordConfig = this.loadCustomConfig("discord.yml", new File(this.getDataFolder(), "discord.yml"));
        debugLog("Plugin initialization started");
        debugLog("Configuration files loaded");

        saveSpawnConfig();
        saveSSConfig();
        saveDiscordConfig();
        saveDefaultConfig();

        String[] lines = new String[]{
                "+==========================+",
                "|                          |",
                "|                          |",
                "|  §c§lExcellentServerManager  §r|",
                "|        §bBy §3Abo 876        §r|",
                "|         §b§lAlpha 2.3        §r|",
                "|                          |",
                "|                          |",
                "+==========================+"
        };

        if (ServerVersion.isCurrentLower(ServerVersion.v1_8_R1)) {
            this.getLogger().log(Level.WARNING, "[ExcellentServerManager] You're running in an unsupported version, <1.7.X is very outdated! Please update to at least 1.8+");
            this.getServer().getPluginManager().disablePlugin(this);
        } else {
            plugin = this;
            for (String line : lines) {
                plugin.getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', line));
            }

            if (plugin.getDiscord().getBoolean("Discord.Enabled")) {
                initializeDiscordBot();
            } else {
                debugLog("Discord integration disabled in config");
            }

            this.registerCommands();
            this.registerEvents();
        }
    }

    private void initializeDiscordBot() {
        debugLog("Starting Discord Bot initialization...");

        String token = getDiscord().getString("Discord.Token");
        if (token == null || token.isEmpty()) {
            getLogger().severe("Discord token is missing or empty!");
            return;
        }

        debugLog("Discord token found, initializing JDA...");

        try {
            jda = JDABuilder.createDefault(token)
                    .setActivity(Activity.playing(Objects.requireNonNull(getDiscord().getString("Discord.Activity"))))
                    .setStatus(OnlineStatus.DO_NOT_DISTURB)
                    .enableIntents(GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MEMBERS)
                    .addEventListeners(new DiscordListeners(this))
                    .build()
                    .awaitReady();

            debugLog("JDA initialized successfully");

            setupGuildAndChannels();
            setupDiscordCommands();
            setupLinksFile();
            loadLinks();
            setupLuckPerms();
            setupRolePermissions();
            onServerStart();
            saveLinks();

            if (getDiscord().getBoolean("advanced.debug")) {
                debugSetup();
            }

        } catch (InterruptedException e) {
            getLogger().severe("❌ Failed to start JDA: " + e.getMessage());
            debugLog("JDA failed to start: %s", e.getMessage());
            e.printStackTrace();
            jda = null;
        } catch (Exception e) {
            getLogger().severe("❌ Unexpected error during Discord initialization: " + e.getMessage());
            debugLog("Unexpected error during Discord initialization: %s", e.getMessage());
            e.printStackTrace();
        }
    }

    private void setupGuildAndChannels() {
        debugLog("Setting up guild and channels...");

        var guildId = getDiscord().getLong("Discord.guild-id");
        var channelId = getDiscord().getLong("Discord.chat-channel-id");
        var voicechannelId = getDiscord().getLong("Discord.Voice-ChannelID");

        debugLog("Guild ID: %d, Channel ID: %d, Voice Channel ID: %d", guildId, channelId, voicechannelId);

        guild = jda.getGuildById(guildId);
        debugLog("Checking guild...");

        if (guild != null) {
            debugLog("Successfully found guild: %s", guild.getName());

            VoiceChannel voiceChannel = guild.getVoiceChannelById(voicechannelId);
            chatChannel = guild.getTextChannelById(channelId);

            if (chatChannel == null) {
                getLogger().warning("Chat channel not found with ID: " + channelId);
                return;
            }

            debugLog("Chat channel found: %s", chatChannel.getName());

            setupVoiceConnection(voiceChannel);
        } else {
            getLogger().severe("Guild not found with ID: " + guildId);
        }
    }

    private void setupVoiceConnection(VoiceChannel voiceChannel) {
        debugLog("Setting up voice connection...");

        // Check permissions
        debugLog("Checking permissions for Discord Bot...");
        if (!Objects.requireNonNull(getGuild()).getSelfMember().hasPermission(chatChannel, Permission.VOICE_CONNECT)) {
            getLogger().warning("Bot does not have permission to join voice channel!");
            chatChannel.sendMessage("I do not have permissions to join a voice channel!").queue();
            return;
        }

        debugLog("Bot has voice connect permissions");

        // Check if voice channel exists
        debugLog("Checking if voice channel is null...");
        if (voiceChannel == null) {
            getLogger().warning("Voice channel is null");
            chatChannel.sendMessage("The voice channel is null").queue();
            return;
        }

        debugLog("Voice channel is not null: %s", voiceChannel.getName());

        try {
            // Connect to voice channel
            AudioManager audioManager = getGuild().getAudioManager();
            audioManager.openAudioConnection(voiceChannel);
            audioManager.setAutoReconnect(true);
            audioManager.setSelfDeafened(true);

            debugLog("Successfully joined voice channel: %s", voiceChannel.getName());
        } catch (Exception e) {
            getLogger().severe("Failed to join voice channel: " + e.getMessage());
            debugLog("Failed to join voice channel: %s", e.getMessage());
        }
    }

    private void setupDiscordCommands() {
        debugLog("Setting up Discord slash commands...");

        try {
            var commands = jda.updateCommands();
            debugLog("Adding commands to Discord Bot...");

            commands.addCommands(
                    Commands.slash("unmute", "unmute Command")
                            .addOption(OptionType.STRING, "player", "the player you want to unmute", true)
                            .setContexts(InteractionContextType.GUILD)
                            .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR))
            );

            commands.addCommands(
                    Commands.slash("bc", "BroadCast Command")
                            .addOption(OptionType.STRING, "message", "the message do you want to BroadCast", true)
                            .setContexts(InteractionContextType.GUILD)
                            .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR))
            );

            commands.addCommands(
                    Commands.slash("op", "op Command")
                            .addOption(OptionType.STRING, "player", "the player you want to op", true)
                            .setContexts(InteractionContextType.GUILD)
                            .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR))
            );

            commands.addCommands(
                    Commands.slash("ban", "Ban Command")
                            .addOption(OptionType.STRING, "player", "the player you want to ban", true)
                            .setContexts(InteractionContextType.GUILD)
                            .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR))
            );

            commands.addCommands(
                    Commands.slash("deop", "Deop Command")
                            .addOption(OptionType.STRING, "player", "the player you want to deop", true)
                            .setContexts(InteractionContextType.GUILD)
                            .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR))
            );

            commands.addCommands(
                    Commands.slash("command", "make the console run A command")
                            .addOption(OptionType.STRING, "command", "the command you want to run", true)
                            .setContexts(InteractionContextType.GUILD)
                            .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR))
            );

            commands.addCommands(
                    Commands.slash("players", "List of players")
                            .setContexts(InteractionContextType.GUILD)
            );

            commands.addCommands(
                    Commands.slash("unban", "UnBan Command")
                            .addOption(OptionType.STRING, "player", "the player you want to unban", true)
                            .setContexts(InteractionContextType.GUILD)
                            .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR))
            );

            commands.addCommands(
                    Commands.slash("mute", "Mute Command")
                            .addOption(OptionType.STRING, "player", "the player you want to mute", true)
                            .setContexts(InteractionContextType.GUILD)
                            .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR))
            );

            commands.addCommands(
                    Commands.slash("kick", "Kick Player Command")
                            .addOption(OptionType.STRING, "player", "the Player who to Kick", true)
                            .setContexts(InteractionContextType.GUILD)
                            .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR))
            );

            commands.queue(
                    success -> debugLog("Successfully registered %d Discord commands", success.size()),
                    error -> debugLog("Failed to register Discord commands: %s", error.getMessage())
            );

        } catch (Exception e) {
            getLogger().severe("Error setting up Discord commands: " + e.getMessage());
            debugLog("Error setting up Discord commands: %s", e.getMessage());
        }
    }

    @Override
    public void onDisable() {
        getLogger().info("Shutting down ExcellentServerManager...");
        debugLog("Plugin shutdown initiated");

        try {
            // Clear GUI data first
            GUI.clearAllPlayerSelections();
            debugLog("GUI data cleared");

            debugLog("Sending shutdown message to Discord Bot...");
            onServerStop();
            debugLog("Successfully sent shutdown message to Discord Bot");

            debugLog("Shutting down Discord Bot...");
            shutdownDiscordConnection();
            debugLog("Successfully shut down Discord Bot");

        } catch (Exception e) {
            getLogger().severe("Error during plugin shutdown: " + e.getMessage());
            debugLog("Error during plugin shutdown: %s", e.getMessage());
            e.printStackTrace();
        }

        getLogger().info("ExcellentServerManager disabled successfully");
    }

    private void shutdownDiscordConnection() {
        debugLog("Starting shutdown sequence...");

        if (jda == null) {
            getLogger().warning("JDA was null on plugin disable, skipping shutdown.");
            debugLog("JDA is null, skipping shutdown");
            return;
        }

        try {
            // Step 1: Disconnect from voice channel safely
            if (getGuild() != null) {
                AudioManager audioManager = getGuild().getAudioManager();
                if (audioManager != null && audioManager.isConnected()) {
                    debugLog("Disconnecting Bot from Voice Channel: %s", audioManager.getConnectedChannel().getName());
                    audioManager.closeAudioConnection();
                    Thread.sleep(500); // Wait for clean disconnect
                    debugLog("Successfully disconnected from voice channel");
                }
            }

            // Step 2: Cancel all pending requests
            debugLog("Canceling all pending requests");
            jda.cancelRequests();

            // Step 3: Proper shutdown with timeout
            debugLog("Shutting down JDA");
            jda.shutdown();

            // Wait for clean shutdown (max 5 seconds)
            if (!jda.awaitShutdown(5, java.util.concurrent.TimeUnit.SECONDS)) {
                debugLog("JDA did not shut down cleanly, forcing shutdown");
                jda.shutdownNow();
            } else {
                debugLog("JDA shut down cleanly");
            }

        } catch (InterruptedException e) {
            getLogger().warning("Thread interrupted during Discord shutdown");
            debugLog("Thread interrupted during Discord shutdown");
            Thread.currentThread().interrupt();

            // Force shutdown if interrupted
            if (jda != null) {
                debugLog("Force shutting down JDA due to interruption");
                jda.shutdownNow();
            }

        } catch (Exception e) {
            getLogger().severe("Error shutting down Discord connection: " + e.getMessage());
            debugLog("Error shutting down Discord connection: %s", e.getMessage());
            e.printStackTrace();

            // Force shutdown on any error
            if (jda != null) {
                try {
                    debugLog("Force shutting down JDA due to error");
                    jda.shutdownNow();
                } catch (Exception ex) {
                    getLogger().severe("Failed to force shutdown JDA: " + ex.getMessage());
                    debugLog("Failed to force shutdown JDA: %s", ex.getMessage());
                }
            }
        } finally {
            debugLog("Setting JDA to null");
            jda = null;
        }
    }

    // Discord Event Methods with Debug
    private String cleanName(String name) {
        return name.replace("§4", "").replace("§r", "");
    }

    private String getAvatarUrl(String playerName) {
        return "https://mc-heads.net/avatar/" + cleanName(playerName) + "/100/head.png";
    }

    private EmbedBuilder createBasicEmbed(String title, String description, String authorName) {
        String cleanAuthor = cleanName(authorName);
        String avatarUrl = getAvatarUrl(cleanAuthor);
        debugLog("Creating Discord embed for: %s", cleanAuthor);

        return new EmbedBuilder()
                .setTitle("**" + cleanAuthor + "**")
                .setDescription(description)
                .setAuthor(cleanAuthor, null, avatarUrl)
                .setThumbnail(avatarUrl);
    }

    private final Random random = new Random();

    private Color getRandomColor() {
        float hue = random.nextFloat();
        float saturation = 0.7f + random.nextFloat() * 0.3f;
        float brightness = 0.7f + random.nextFloat() * 0.3f;
        return Color.getHSBColor(hue, saturation, brightness);
    }

    public void onChat(Player player, String message) {
        if (!plugin.getDiscord().getBoolean("Discord.Enabled")) return;

        debugLog("Processing chat message from player: %s", player.getName());
        debugLog("Message content: '%s'", message);

        Color color = getRandomColor();
        String cleanName = cleanName(player.getName());
        String description = cleanName + " has Chat : ```" + message + "```";

        debugLog("Creating embed for chat message");
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("**" + cleanName + "**")
                .setDescription(description)
                .setColor(color);

        try {
            debugLog("Sending embed to chat channel");
            chatChannel.sendMessageEmbeds(embed.build()).queue(
                    success -> debugLog("Successfully sent chat embed to channel"),
                    error -> debugLog("Failed to send chat embed: %s", error.getMessage())
            );

            Long chatLogChannelId = getDiscord().getLong("Chat");
            if (chatLogChannelId != null) {
                TextChannel logChannel = getGuild().getTextChannelById(chatLogChannelId);
                if (logChannel != null) {
                    debugLog("Sending embed to log channel");
                    logChannel.sendMessageEmbeds(embed.build()).queue(
                            success -> debugLog("Successfully sent chat embed to log channel"),
                            error -> debugLog("Failed to send chat embed to log channel: %s", error.getMessage())
                    );
                } else {
                    debugLog("Log channel not found with ID: %d", chatLogChannelId);
                }
            }
        } catch (Exception e) {
            debugLog("Error sending chat embed: %s", e.getMessage());
        }
    }

    public void onJoin(Player player, PlayerJoinEvent event) {
        if (!plugin.getDiscord().getBoolean("Discord.Enabled")) return;

        debugLog("Processing join event for player: %s", player.getName());

        Long joinChannelId = getDiscord().getLong("Join");
        if (joinChannelId == null) {
            debugLog("Join channel ID not configured");
            return;
        }

        TextChannel channel = jda.getTextChannelById(joinChannelId);
        if (channel == null) {
            getLogger().warning("Join channel is null");
            debugLog("Join channel not found with ID: %d", joinChannelId);
            return;
        }

        Collection<? extends Player> players = plugin.getServer().getOnlinePlayers();
        int maxPlayers = plugin.getServer().getMaxPlayers();

        String cleanName = cleanName(player.getName());
        String description = "**" + event.getJoinMessage() + " " + players.size() + "/" + maxPlayers + "**";

        debugLog("Creating join embed for player: %s (%d/%d players)", cleanName, players.size(), maxPlayers);

        EmbedBuilder embed = createBasicEmbed(cleanName, description, cleanName);

        debugLog("Sending join embed to channel");
        channel.sendMessageEmbeds(embed.build()).queue(
                success -> debugLog("Successfully sent join embed"),
                error -> debugLog("Failed to send join embed: %s", error.getMessage())
        );
    }

    public void onServerStart() {
        if (!plugin.getDiscord().getBoolean("Discord.Enabled")) return;

        debugLog("Processing server start event");

        // Server status message
        if (getDiscord().getBoolean("Server-Status.Enabled")) {
            debugLog("Server status notifications enabled");

            Long statusChannelId = getDiscord().getLong("Server-Status.ChannelID");
            if (statusChannelId != null) {
                TextChannel statusChannel = jda.getTextChannelById(statusChannelId);
                if (statusChannel != null) {
                    String ip = getDiscord().getString("Server-Status.Server-Ip");
                    long port = getDiscord().getLong("Server-Status.Server-Port");
                    String message = "**# > 🇸🇦\n السيرفر فتح حياكم ! \n# > 🇺🇸\n  - The server is open !\n  - Server IP : " + ip + ":" + port + "\n||@everyone @here||**";

                    debugLog("Sending server status message to channel");
                    statusChannel.sendMessage(message).queue(
                            success -> debugLog("Successfully sent server status message"),
                            error -> debugLog("Failed to send server status message: %s", error.getMessage())
                    );
                } else {
                    debugLog("Server status channel not found with ID: %d", statusChannelId);
                }
            } else {
                debugLog("Server status channel ID not configured");
            }
        }

        // Server start log
        Long serverChannelId = getDiscord().getLong("Server");
        if (serverChannelId != null) {
            TextChannel channel = jda.getTextChannelById(serverChannelId);
            if (channel != null) {
                debugLog("Creating server start embed");

                EmbedBuilder embed = new EmbedBuilder()
                        .setTitle("**Server Start**")
                        .setDescription("# **Server Start**")
                        .setAuthor(plugin.getServer().getIp() + ":" + plugin.getServer().getPort(), null, "https://mc-heads.net/avatar/Rebxtingg/100/head.png")
                        .setThumbnail("https://mc-heads.net/avatar/Rebxtingg/100/head.png");

                debugLog("Sending server start embed to channel");
                channel.sendMessageEmbeds(embed.build()).queue(
                        success -> debugLog("Successfully sent server start embed"),
                        error -> debugLog("Failed to send server start embed: %s", error.getMessage())
                );
            } else {
                debugLog("Server log channel not found with ID: %d", serverChannelId);
            }
        } else {
            debugLog("Server log channel ID not configured");
        }
    }

    public void onServerStop() {
        if (!plugin.getDiscord().getBoolean("Discord.Enabled")) return;

        debugLog("Processing server stop event");

        if (jda == null) {
            getLogger().warning("JDA instance is null in onServerStop(), skipping Discord message.");
            debugLog("JDA is null, cannot send server stop message");
            return;
        }

        Long serverChannelId = getDiscord().getLong("Server");
        if (serverChannelId == null) {
            debugLog("Server log channel ID not configured");
            return;
        }

        TextChannel channel = jda.getTextChannelById(serverChannelId);
        if (channel == null) {
            getLogger().warning("Discord channel with ID " + serverChannelId + " not found.");
            debugLog("Server log channel not found with ID: %d", serverChannelId);
            return;
        }

        debugLog("Creating server stop embed");
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("**Server Stop**")
                .setDescription("# **Server Stop**")
                .setAuthor(plugin.getServer().getName(), null, "https://mc-heads.net/avatar/Rebxtingg/100/head.png")
                .setThumbnail("https://mc-heads.net/avatar/Rebxtingg/100/head.png");

        debugLog("Sending server stop embed to channel");
        channel.sendMessageEmbeds(embed.build()).queue(
                success -> debugLog("Successfully sent server stop embed"),
                error -> debugLog("Failed to send server stop embed: %s", error.getMessage())
        );
    }

    public void onQuit(Player player, PlayerQuitEvent event) {
        if (!plugin.getDiscord().getBoolean("Discord.Enabled")) return;

        debugLog("Processing quit event for player: %s", player.getName());

        Long leaveChannelId = getDiscord().getLong("Leave");
        if (leaveChannelId == null) {
            debugLog("Leave channel ID not configured");
            return;
        }

        TextChannel channel = jda.getTextChannelById(leaveChannelId);
        if (channel == null) {
            getLogger().warning("Quit channel is null");
            debugLog("Leave channel not found with ID: %d", leaveChannelId);
            return;
        }

        Collection<? extends Player> players = plugin.getServer().getOnlinePlayers();
        int maxPlayers = plugin.getServer().getMaxPlayers();

        String cleanName = cleanName(player.getName());
        String description = "**" + event.getQuitMessage() + "\n " + players.size() + "/" + maxPlayers + "**";

        debugLog("Creating quit embed for player: %s (%d/%d players)", cleanName, players.size(), maxPlayers);

        EmbedBuilder embed = createBasicEmbed(cleanName, description, cleanName);

        debugLog("Sending quit embed to channel");
        channel.sendMessageEmbeds(embed.build()).queue(
                success -> debugLog("Successfully sent quit embed"),
                error -> debugLog("Failed to send quit embed: %s", error.getMessage())
        );
    }

    public void onDeath(Player player, PlayerDeathEvent event) {
        if (!plugin.getDiscord().getBoolean("Discord.Enabled")) return;

        debugLog("Processing death event for player: %s", player.getName());
        debugLog("Death message: %s", event.getDeathMessage());

        Long deathChannelId = getDiscord().getLong("Death");
        if (deathChannelId == null) {
            debugLog("Death channel ID not configured");
            return;
        }

        TextChannel channel = jda.getTextChannelById(deathChannelId);
        if (channel == null) {
            getLogger().warning("Death channel is null");
            debugLog("Death channel not found with ID: %d", deathChannelId);
            return;
        }

        String cleanName = cleanName(player.getName());
        String description = "**" + event.getDeathMessage() + "**";

        debugLog("Creating death embed for player: %s", cleanName);

        EmbedBuilder embed = createBasicEmbed(cleanName, description, cleanName);

        debugLog("Sending death embed to channel");
        channel.sendMessageEmbeds(embed.build()).queue(
                success -> debugLog("Successfully sent death embed"),
                error -> debugLog("Failed to send death embed: %s", error.getMessage())
        );
    }

    public void onCommand(Player player, String command) {
        if (!plugin.getDiscord().getBoolean("Discord.Enabled")) return;

        debugLog("Processing command log for player: %s", player.getName());
        debugLog("Command executed: %s", command);

        Long channelId;
        String actionDescription;
        String cleanName = cleanName(player.getName());

        // Determine which channel and description to use based on command
        if (command.startsWith("/op") || command.startsWith("/deop")) {
            debugLog("Command categorized as Op/Deop");
            channelId = getDiscord().getLong("command-Op");
            actionDescription = "has given Op/Deop to a player";
        } else if (command.startsWith("/lp") || command.startsWith("/luckperms")) {
            debugLog("Command categorized as LuckPerms");
            channelId = getDiscord().getLong("command-LuckPerms");
            actionDescription = "has given a Rank to a player";
        } else if (command.startsWith("/irp") || command.startsWith("/inventoryrollbackplus") || command.startsWith("/inventoryrollback") || command.startsWith("/ir")) {
            debugLog("Command categorized as IRP");
            channelId = getDiscord().getLong("command-irp");
            actionDescription = "has IRP'd a player";
        } else if (command.startsWith("/ban") || command.startsWith("/unban") || command.startsWith("/banip") || command.startsWith("/unbanip") || command.startsWith("/tempban")) {
            debugLog("Command categorized as Ban/Unban");
            channelId = getDiscord().getLong("command-ban");
            actionDescription = "has banned/unbanned a player";
        } else if (command.startsWith("/kick")) {
            debugLog("Command categorized as Kick");
            channelId = getDiscord().getLong("command-kick");
            actionDescription = "has kicked a player";
        } else if (command.startsWith("/mute") || command.startsWith("/unmute")) {
            debugLog("Command categorized as Mute/Unmute");
            channelId = getDiscord().getLong("command-mute");
            actionDescription = "has muted/unmuted a player";
        } else if (command.startsWith("/invsee") || command.startsWith("/enderchestsee") || command.startsWith("/ec")) {
            debugLog("Command categorized as InvSee");
            channelId = getDiscord().getLong("command-invsee");
            actionDescription = "has viewed a player's inventory";
        } else if (command.startsWith("/ss") || command.startsWith("/setss")) {
            debugLog("Command categorized as SS/SetSS");
            channelId = getDiscord().getLong("command-ss");
            actionDescription = "has SS/SetSS a player";
        } else {
            debugLog("Command categorized as Full Command Log");
            channelId = getDiscord().getLong("command-Full");
            actionDescription = "has run a command";
        }

        if (channelId == null) {
            debugLog("Channel ID not configured for command category");
            return;
        }

        TextChannel channel = jda.getTextChannelById(channelId);
        if (channel == null) {
            getLogger().warning("Command channel is null for command: " + command);
            debugLog("Command channel not found with ID: %d", channelId);
            return;
        }

        String description = cleanName + " " + actionDescription + ":\n```\n" + command + "\n```";
        debugLog("Creating command embed for player: %s", cleanName);

        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("**" + cleanName + "**")
                .setDescription(description)
                .setAuthor(cleanName, null, getAvatarUrl(cleanName))
                .setThumbnail(getAvatarUrl(cleanName));

        debugLog("Sending command embed to channel");
        channel.sendMessageEmbeds(embed.build()).queue(
                success -> debugLog("Successfully sent command embed"),
                error -> debugLog("Failed to send command embed: %s", error.getMessage())
        );
    }

    public void onChatCommand(MessageReceivedEvent event) {
        if (!plugin.getDiscord().getBoolean("Discord.Enabled")) return;

        debugLog("Processing chat relay from Discord user: %s", event.getAuthor().getName());
        debugLog("Message content: '%s'", event.getMessage().getContentRaw());

        if (plugin.getDiscord().getBoolean("settings.chat-relay")) {
            debugLog("Chat relay is enabled");

            if (!event.getAuthor().isBot() && !event.isWebhookMessage()) {
                String messageContent = event.getMessage().getContentRaw();

                if (event.getChannel().asTextChannel().equals(chatChannel)) {
                    debugLog("Relaying Discord message to Minecraft chat");
                    plugin.getServer().broadcastMessage("§f[§9§lDiscord§f] §6<§e§l" + event.getAuthor().getName() + "§6> §f: " + messageContent);
                    debugLog("Successfully relayed message to Minecraft");
                } else {
                    debugLog("Message received from non-chat channel: %s", event.getChannel().getName());
                    plugin.getLogger().info("Discord message received from a channel " + event.getChannel().getName() + " : " + event.getMessage().getContentRaw());
                }
            } else {
                debugLog("Ignoring message from bot or webhook");
            }
        } else {
            debugLog("Chat relay is disabled");
        }
    }

    // LuckPerms Integration Methods with Debug
    private void setupLuckPerms() {
        debugLog("Starting LuckPerms integration setup");

        try {
            luckPerms = LuckPermsProvider.get();
            getLogger().info("LuckPerms integration enabled!");
            debugLog("Successfully connected to LuckPerms");
        } catch (IllegalStateException e) {
            getLogger().warning("LuckPerms not found! Role synchronization will be disabled.");
            debugLog("LuckPerms not found, disabling role sync");
            getDiscord().set("settings.sync-roles", false);
        } catch (Exception e) {
            getLogger().severe("Error setting up LuckPerms integration: " + e.getMessage());
            debugLog("Error setting up LuckPerms: %s", e.getMessage());
        }
    }

    private void setupRolePermissions() {
        debugLog("Setting up role permissions mapping");

        if (getDiscord().getConfigurationSection("role-permissions") != null) {
            for (String roleId : getDiscord().getConfigurationSection("role-permissions").getKeys(false)) {
                String permission = getDiscord().getConfigurationSection("role-permissions").getString(roleId);
                rolePermissionMap.put(roleId, permission);
                debugLog("Mapped role %s to permission: %s", roleId, permission);
            }
            debugLog("Loaded %d role permission mappings", rolePermissionMap.size());
        } else {
            getLogger().warning("Missing 'role-permissions' section in discord.yml!");
            debugLog("No role-permissions section found in discord.yml");
        }
    }

    private void setupLinksFile() {
        debugLog("Setting up links file");

        linksFile = new File(getDataFolder(), "links.yml");
        if (!linksFile.exists()) {
            try {
                linksFile.createNewFile();
                debugLog("Created new links.yml file");
            } catch (IOException e) {
                getLogger().severe("Could not create links.yml file: " + e.getMessage());
                debugLog("Failed to create links.yml: %s", e.getMessage());
            }
        } else {
            debugLog("Links file already exists");
        }

        linksConfig = YamlConfiguration.loadConfiguration(linksFile);
        debugLog("Links configuration loaded");
    }

    private void loadLinks() {
        debugLog("Loading player Discord links");

        if (linksConfig.getConfigurationSection("links") != null) {
            for (String uuid : linksConfig.getConfigurationSection("links").getKeys(false)) {
                String discordId = linksConfig.getString("links." + uuid);
                playerDiscordLinks.put(UUID.fromString(uuid), discordId);
                debugLog("Loaded link: Player %s -> Discord %s", uuid, discordId);
            }
            debugLog("Loaded %d player Discord links", playerDiscordLinks.size());
        } else {
            debugLog("No existing links found in configuration");
        }
    }

    public void saveLinks() {
        debugLog("Saving player Discord links");

        for (Map.Entry<UUID, String> entry : playerDiscordLinks.entrySet()) {
            linksConfig.set("links." + entry.getKey().toString(), entry.getValue());
        }

        try {
            linksConfig.save(linksFile);
            debugLog("Successfully saved %d Discord links", playerDiscordLinks.size());
        } catch (IOException e) {
            getLogger().severe("Could not save links.yml: " + e.getMessage());
            debugLog("Failed to save links.yml: %s", e.getMessage());
        }
    }

    public void syncPlayerRoles(Player player, Member member) {
        debugLog("Starting role sync for player: %s (Discord: %s)", player.getName(), member.getEffectiveName());

        if (luckPerms == null) {
            debugLog("LuckPerms is null, cannot sync roles");
            System.err.println("LuckPerms is null!");
            return;
        }

        debugLog("Loading user data from LuckPerms");
        User user = luckPerms.getUserManager().loadUser(player.getUniqueId(), player.getName()).join();
        if (user == null) {
            debugLog("Failed to load user data from LuckPerms");
            return;
        }

        debugLog("Getting current user groups");
        Set<String> currentGroups = user.getNodes().stream()
                .filter(node -> node.getType() == NodeType.INHERITANCE)
                .map(node -> ((InheritanceNode) node).getGroupName())
                .collect(Collectors.toSet());

        debugLog("Current groups: %s", currentGroups);

        debugLog("Determining groups that should be assigned");
        Set<String> shouldHaveGroups = new HashSet<>();
        shouldHaveGroups.add("default"); // Always keep default group

        for (Role role : member.getRoles()) {
            String roleId = role.getId();
            debugLog("Processing Discord role: %s (ID: %s)", role.getName(), roleId);

            if (rolePermissionMap.containsKey(roleId)) {
                String groupName = rolePermissionMap.get(roleId);
                debugLog("Role %s maps to group: %s", role.getName(), groupName);

                // Verify group exists
                if (luckPerms.getGroupManager().getGroup(groupName) != null) {
                    shouldHaveGroups.add(groupName);
                    debugLog("Added group to should-have list: %s", groupName);
                } else {
                    debugLog("Group doesn't exist in LuckPerms: %s", groupName);
                    System.err.println("❌ Group doesn't exist in LuckPerms: " + groupName);
                }
            } else {
                debugLog("No mapping found for role: %s", role.getName());
            }
        }

        debugLog("Should have groups: %s", shouldHaveGroups);

        // Find Discord-managed groups
        Set<String> discordManagedGroups = new HashSet<>(rolePermissionMap.values());
        debugLog("Discord-managed groups: %s", discordManagedGroups);

        boolean madeChanges = false;

        // Remove Discord-managed groups that shouldn't be there
        debugLog("Removing unnecessary Discord-managed groups");
        for (String groupName : discordManagedGroups) {
            if (currentGroups.contains(groupName) && !shouldHaveGroups.contains(groupName)) {
                debugLog("Removing group: %s", groupName);

                List<Node> nodesToRemove = user.getNodes().stream()
                        .filter(node -> node.getType() == NodeType.INHERITANCE)
                        .filter(node -> ((InheritanceNode) node).getGroupName().equals(groupName))
                        .collect(Collectors.toList());

                for (Node nodeToRemove : nodesToRemove) {
                    DataMutateResult result = user.data().remove(nodeToRemove);
                    if (result.wasSuccessful()) {
                        madeChanges = true;
                        debugLog("Successfully removed inheritance node for group: %s", groupName);
                    } else {
                        debugLog("Failed to remove inheritance node for group: %s", groupName);
                    }
                }
            }
        }

        // Add groups that should be there but aren't
        debugLog("Adding missing groups");
        for (String groupName : shouldHaveGroups) {
            if (!currentGroups.contains(groupName)) {
                debugLog("Adding group: %s", groupName);

                InheritanceNode groupNode = InheritanceNode.builder(groupName).build();
                DataMutateResult result = user.data().add(groupNode);

                if (result.wasSuccessful()) {
                    madeChanges = true;
                    debugLog("Successfully added group: %s", groupName);
                } else {
                    debugLog("Failed to add group: %s", groupName);
                }
            }
        }

        if (madeChanges) {
            debugLog("Saving changes to LuckPerms");
            try {
                luckPerms.getUserManager().saveUser(user).join();
                luckPerms.getUserManager().cleanupUser(user);
                debugLog("Successfully saved user changes");

                // Show final result after a delay
                Bukkit.getScheduler().runTaskLater(this, () -> {
                    User updatedUser = luckPerms.getUserManager().getUser(player.getUniqueId());
                    if (updatedUser != null) {
                        Set<String> finalGroups = updatedUser.getNodes().stream()
                                .filter(node -> node.getType() == NodeType.INHERITANCE)
                                .map(node -> ((InheritanceNode) node).getGroupName())
                                .collect(Collectors.toSet());
                        debugLog("Final groups for %s: %s", player.getName(), finalGroups);
                    }
                }, 20L);

            } catch (Exception e) {
                debugLog("Failed to save user changes: %s", e.getMessage());
                System.err.println("Failed to save: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            debugLog("No changes needed for player: %s", player.getName());
        }
    }

    // Ban/Unban Methods with Debug
    public void banPlayerFromDiscord(String discordId, String reason) {
        if (!getDiscord().getBoolean("Discord.Enabled")) return;

        debugLog("Processing Discord ban sync for Discord ID: %s", discordId);
        debugLog("Ban reason: %s", reason != null ? reason : "No reason provided");

        if (getDiscord().getBoolean("settings.sync-bans")) {
            debugLog("Ban sync is enabled");

            if (reason == null) reason = "No reason provided";

            UUID playerId = getPlayerIdFromDiscord(discordId);
            if (playerId != null) {
                Player player = Bukkit.getPlayer(playerId);
                String playerName = player != null ? player.getName() : "Unknown";

                debugLog("Banning player: %s (UUID: %s)", playerName, playerId);

                try {
                    BanList banList = Bukkit.getBanList(BanList.Type.NAME);
                    banList.addBan(playerName, reason, null, "Discord Ban Sync");

                    // Kick if online
                    if (player != null && player.isOnline()) {
                        debugLog("Player is online, kicking: %s", playerName);
                        player.kickPlayer("§cYou have been banned from Discord!\n§cReason: " + reason);
                    }

                    debugLog("Successfully banned player %s for reason: %s", playerName, reason);
                } catch (Exception e) {
                    debugLog("Error banning player %s: %s", playerName, e.getMessage());
                }
            } else {
                debugLog("No player found for Discord ID: %s", discordId);
            }
        } else {
            debugLog("Ban sync is disabled");
        }
    }

    public void unbanPlayerFromDiscord(String discordId) {
        if (!getDiscord().getBoolean("Discord.Enabled")) return;

        debugLog("Processing Discord unban sync for Discord ID: %s", discordId);

        if (getDiscord().getBoolean("settings.sync-bans")) {
            debugLog("Unban sync is enabled");

            UUID playerId = getPlayerIdFromDiscord(discordId);
            if (playerId != null) {
                Player player = Bukkit.getPlayer(playerId);
                String playerName = player != null ? player.getName() : "Unknown";

                debugLog("Unbanning player: %s (UUID: %s)", playerName, playerId);

                try {
                    BanList banList = Bukkit.getBanList(BanList.Type.NAME);
                    banList.pardon(playerName);

                    getLogger().info("Player " + playerName + " unbanned due to Discord unban");
                    debugLog("Successfully unbanned player: %s", playerName);
                } catch (Exception e) {
                    debugLog("Error unbanning player %s: %s", playerName, e.getMessage());
                }
            } else {
                debugLog("No player found for Discord ID: %s", discordId);
            }
        } else {
            debugLog("Unban sync is disabled");
        }
    }

    private UUID getPlayerIdFromDiscord(String discordId) {
        debugLog("Looking up player ID for Discord ID: %s", discordId);

        for (Map.Entry<UUID, String> entry : playerDiscordLinks.entrySet()) {
            if (entry.getValue().equals(discordId)) {
                debugLog("Found player ID %s for Discord ID %s", entry.getKey(), discordId);
                return entry.getKey();
            }
        }

        debugLog("No player ID found for Discord ID: %s", discordId);
        return null;
    }

    // Utility Methods with Debug
    public Member getLinkedDiscord(Player player) {
        debugLog("Getting linked Discord member for player: %s", player.getName());

        String discordId = playerDiscordLinks.get(player.getUniqueId());
        if (discordId == null) {
            debugLog("No Discord link found for player: %s", player.getName());
            return null;
        }

        debugLog("Found Discord ID %s for player %s", discordId, player.getName());

        if (guild != null) {
            Member member = guild.getMemberById(discordId);
            if (member != null) {
                debugLog("Found Discord member: %s", member.getEffectiveName());
            } else {
                debugLog("Discord member not found in guild for ID: %s", discordId);
            }
            return member;
        } else {
            debugLog("Guild is null, cannot get Discord member");
            return null;
        }
    }

    public String generateVerificationCode() {
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

        for (int i = 0; i < 6; i++) {
            code.append(chars.charAt(random.nextInt(chars.length())));
        }

        String generatedCode = code.toString();
        debugLog("Generated verification code: %s", generatedCode);
        return generatedCode;
    }

    // Permission Methods with Debug
    public void addPermission(UUID playerUUID, String permission) {
        debugLog("Adding permission %s to player %s", permission, playerUUID);

        luckPerms.getUserManager().loadUser(playerUUID).thenAccept(user -> {
            if (user != null) {
                PermissionNode node = PermissionNode.builder(permission)
                        .value(true)
                        .build();

                user.data().add(node);
                luckPerms.getUserManager().saveUser(user);
                debugLog("Successfully added permission %s to player %s", permission, playerUUID);
            } else {
                debugLog("Failed to load user %s for permission addition", playerUUID);
            }
        });
    }

    public void addVerifyPermission(String playerUUID) {
        debugLog("Adding verify permission to player: %s", playerUUID);

        luckPerms.getUserManager().loadUser(UUID.fromString(playerUUID)).thenAccept(user -> {
            if (user != null) {
                PermissionNode node = PermissionNode.builder("excellentservermanager.player.verify")
                        .value(true)
                        .build();

                user.data().add(node);
                luckPerms.getUserManager().saveUser(user);
                debugLog("Successfully added verify permission to player: %s", playerUUID);
            } else {
                debugLog("Failed to load user %s for verify permission addition", playerUUID);
            }
        });
    }

    public void removePermission(UUID playerUUID, String permission) {
        debugLog("Removing permission %s from player %s", permission, playerUUID);

        luckPerms.getUserManager().loadUser(playerUUID).thenAccept(user -> {
            if (user != null) {
                user.data().clear(NodeType.PERMISSION.predicate(permission::equals));
                luckPerms.getUserManager().saveUser(user);
                debugLog("Successfully removed permission %s from player %s", permission, playerUUID);
            } else {
                debugLog("Failed to load user %s for permission removal", playerUUID);
            }
        });
    }

    public void removeVerifyPermission(UUID playerUUID) {
        debugLog("Removing verify permission from player: %s", playerUUID);

        luckPerms.getUserManager().loadUser(playerUUID).thenAccept(user -> {
            if (user != null) {
                user.data().clear(NodeType.PERMISSION.predicate("excellentservermanager.player.verify"::equals));
                luckPerms.getUserManager().saveUser(user);
                debugLog("Successfully removed verify permission from player: %s", playerUUID);
            } else {
                debugLog("Failed to load user %s for verify permission removal", playerUUID);
            }
        });
    }

    public CompletableFuture<Boolean> hasPermission(UUID playerUUID, String permission) {
        debugLog("Checking permission %s for player %s", permission, playerUUID);

        return luckPerms.getUserManager().loadUser(playerUUID).thenApply(user -> {
            if (user != null) {
                boolean hasPermission = user.getCachedData().getPermissionData()
                        .checkPermission(permission).asBoolean();
                debugLog("Player %s has permission %s: %s", playerUUID, permission, hasPermission);
                return hasPermission;
            }
            debugLog("Failed to load user %s for permission check", playerUUID);
            return false;
        });
    }

    // Debug and Testing Methods
    public void debugSetup() {
        debugLog("Running setup debug check");

        System.out.println("=== SETUP DEBUG ===");
        System.out.println("LuckPerms: " + (luckPerms != null ? "✅ Connected" : "❌ NULL"));
        System.out.println("JDA: " + (jda != null ? "✅ Connected" : "❌ NULL"));

        if (guild != null) {
            System.out.println("Guild ID: " + guild.getId());
            System.out.println("Guild Name: " + guild.getName());
        } else {
            System.out.println("Guild: ❌ NULL");
        }

        System.out.println("Role Permission Map: " + rolePermissionMap);
        System.out.println("Player Discord Links: " + playerDiscordLinks.size() + " links");

        if (luckPerms != null) {
            System.out.println("Available LuckPerms groups:");
            luckPerms.getGroupManager().getLoadedGroups().forEach(group -> {
                System.out.println("  - " + group.getName());
            });
        }

        debugLog("Setup debug check completed");
    }

    public void syncAllOnlinePlayers() {
        debugLog("Starting sync for all online players");

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (guild != null) {
                Member member = getLinkedDiscord(player);
                if (member != null) {
                    debugLog("Syncing roles for player: %s", player.getName());
                    syncPlayerRoles(player, member);
                } else {
                    debugLog("No Discord link found for player: %s", player.getName());
                }
            }
        }

        debugLog("Completed sync for all online players");
    }

    // Event Registration with Debug
    public void registerEvents() {
        debugLog("Registering plugin events");

        PluginManager pm = getServer().getPluginManager();

        if (!this.getBoolean("Toggle.disable-onMoveEvent")) {
            pm.registerEvents(new Move(this), this);
            debugLog("Registered Move event listener");
        } else {
            this.onMoveEnabled = false;
            debugLog("Move event listener disabled in config");
        }

        this.onFall = this.getBoolean("Conditions.prevent-falldamage");
        this.onJoin = this.getBoolean("Conditions.spawn-onJoin");
        this.onVoid = this.getBoolean("Conditions.spawn-onVoid");

        if (plugin.getDiscord().getBoolean("Discord.Enabled")) {
            debugLog("Registering Discord event listeners");
            pm.registerEvents(new DiscordListeners(this), this);
        } else {
            debugLog("Discord event listeners not registered - Discord disabled");
        }

        // Register other event listeners
        pm.registerEvents(new CobWeb(), this);
        pm.registerEvents(new OnLeaveCan(this), this);
        pm.registerEvents(new onJoin(this), this);
        pm.registerEvents(new onRespawn(this), this);
        pm.registerEvents(new EntityDamage(this), this);
        pm.registerEvents(new LeaveMSG(this), this);
        pm.registerEvents(new JoinMSG(this), this);
        pm.registerEvents(new JointoSpawn(this), this);
        pm.registerEvents(new BlockReset(this), this);
        pm.registerEvents(new Mentions(), this);
        pm.registerEvents(new SSEvent(this), this);
        pm.registerEvents(new GUIListener(this), this);

        debugLog("All event listeners registered");
    }

    public void registerCommands() {
        debugLog("Registering plugin commands");

        Objects.requireNonNull(this.getCommand("nightvision")).setExecutor(new NightVision(this));
        Objects.requireNonNull(getCommand("link")).setExecutor(new LinkCommand(this));
        Objects.requireNonNull(getCommand("unlink")).setExecutor(new UnlinkCommand(this));
        Objects.requireNonNull(getCommand("linked")).setExecutor(new LinkedCommand(this));
        Objects.requireNonNull(getCommand("excellentservermanager")).setExecutor(new ExcellentServerManagerCMD(this));
        Objects.requireNonNull(getCommand("excellentservermanager")).setTabCompleter(new ExcellentServerManagerCMD(this));
        Objects.requireNonNull(getCommand("setspawn")).setExecutor(new SetSpawn(this));
        Objects.requireNonNull(getCommand("setfirstspawn")).setExecutor(new SetFirstSpawn(this));
        Objects.requireNonNull(getCommand("spawn")).setExecutor(new Spawn(this));
        Objects.requireNonNull(getCommand("ss")).setExecutor(new SSCommand(this));
        Objects.requireNonNull(getCommand("setss")).setExecutor(new SSsetRoom(this));
        Objects.requireNonNull(getCommand("bc")).setExecutor(new BroadCast(this));

        debugLog("All commands registered");
    }

    // Configuration Methods (keeping existing functionality)
    public FileConfiguration loadCustomConfig(String resourceName, File out) {
        try {
            InputStream in = this.getResource(resourceName);

            if (!out.exists()) {
                this.getDataFolder().mkdirs();
                out.createNewFile();
            }

            FileConfiguration file = YamlConfiguration.loadConfiguration(out);

            if (in != null) {
                InputStreamReader inReader = new InputStreamReader(in);
                file.setDefaults(YamlConfiguration.loadConfiguration(inReader));
                file.options().copyDefaults(true);
                file.save(out);
            }

            return file;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Getters
    public Map<UUID, String> getPlayerDiscordLinks() { return playerDiscordLinks; }
    public Map<String, String> getPendingLinks() { return pendingLinks; }
    public TextChannel getChatChannel() { return chatChannel; }
    public Guild getGuild() { return guild; }
    public LuckPerms getLuckPerms() { return luckPerms; }
    public Map<String, String> getRolePermissionMap() { return rolePermissionMap; }

    // Configuration getters
    public FileConfiguration getMsg() { return this.msgConfig; }
    public FileConfiguration getSpawn() { return this.spawnConfig; }
    public FileConfiguration getSS() { return this.ssConfig; }
    public FileConfiguration getDiscord() { return this.discordConfig; }

    // Reload methods with debug
    public void reloadAllConfigs() {
        debugLog("Reloading all configurations");

        super.saveDefaultConfig();
        super.reloadConfig();
        this.spawnConfig = this.loadCustomConfig("spawn.yml", new File(this.getDataFolder(), "spawn.yml"));
        this.ssConfig = this.loadCustomConfig("ssplayers.yml", new File(this.getDataFolder(), "ssplayers.yml"));
        this.msgConfig = this.loadCustomConfig("messages.yml", new File(this.getDataFolder(), "messages.yml"));
        this.discordConfig = this.loadCustomConfig("discord.yml", new File(this.getDataFolder(), "discord.yml"));
        saveResource("spawn.yml", false);
        saveResource("ssplayers.yml", false);
        saveResource("messages.yml", false);
        saveResource("discord.yml", false);
        setupRolePermissions();
        debugLog("Saving Discord links after config reload");
        saveLinks();

        if (this.getBoolean("Toggle.disable-onMoveEvent") && this.onMoveEnabled) {
            PlayerMoveEvent.getHandlerList().unregister((Plugin) this);
            this.onMoveEnabled = false;
            debugLog("Disabled move event listener");
        } else if (!this.getBoolean("Toggle.disable-onMoveEvent") && !this.onMoveEnabled) {
            this.getServer().getPluginManager().registerEvents(new Move(this), this);
            this.onMoveEnabled = true;
            debugLog("Enabled move event listener");
        }

        this.onFall = this.getBoolean("Conditions.prevent-falldamage");
        this.onJoin = this.getBoolean("Conditions.spawn-onJoin");
        this.onVoid = this.getBoolean("Conditions.spawn-onVoid");

        debugLog("Configuration reload completed");
    }

    // Save methods
    public void saveSpawnConfig() {
        if (spawnConfig == null) {
            saveResource("spawn.yml", false);
        }
        try {
            getSpawn().save(new File(getDataFolder(), "spawn.yml"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void saveDiscordConfig() {
        if (discordConfig == null) {
            saveResource("discord.yml", false);
        }
        try {
            getDiscord().save(new File(getDataFolder(), "discord.yml"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void saveSSConfig() {
        if (ssConfig == null) {
            saveResource("ssplayers.yml", false);
        }
        try {
            getSS().save(new File(getDataFolder(), "ssplayers.yml"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Individual config reload methods
    public void reloadSpawnConfig() {
        debugLog("Reloading spawn configuration");
        this.spawnConfig = this.loadCustomConfig("spawn.yml", new File(this.getDataFolder(), "spawn.yml"));
    }

    public void reloadMsgConfig() {
        debugLog("Reloading messages configuration");
        this.msgConfig = this.loadCustomConfig("messages.yml", new File(this.getDataFolder(), "messages.yml"));
    }

    public void reloadSSConfig() {
        debugLog("Reloading SS configuration");
        this.ssConfig = this.loadCustomConfig("ssplayers.yml", new File(this.getDataFolder(), "ssplayers.yml"));
    }

    public void reloadDiscordConfig() {
        debugLog("Reloading Discord configuration");
        this.discordConfig = this.loadCustomConfig("discord.yml", new File(this.getDataFolder(), "discord.yml"));
    }

    // Utility getters
    public boolean getBoolean(String s) {
        return this.getConfig().getBoolean(s);
    }

    public boolean getOnJoin() {
        return this.onJoin;
    }

    public boolean getOnVoid() {
        return this.onVoid;
    }

    public boolean getOnFall() {
        return this.onFall;
    }

    public Map<UUID, BukkitTask> getPlayers() {
        return this.players;
    }

    public void addPlayer(UUID u, BukkitTask b) {
        this.players.put(u, b);
    }

    public void removePlayer(UUID u) {
        this.players.remove(u);
    }

    public void cancelTeleport(BukkitTask task) {
        task.cancel();
    }

    public ArrayList<UUID> getFallPlayers() {
        return this.fall;
    }

    public void addFallPlayer(UUID u) {
        this.fall.add(u);
    }

    public void removeFallPlayer(UUID u) {
        this.fall.remove(u);
    }

    public ArrayList<UUID> getVoidPlayers() {
        return this.voidPlayer;
    }

    public void addVoidPlayer(UUID u) {
        this.voidPlayer.add(u);
    }

    public void removeVoidPlayer(UUID u) {
        this.voidPlayer.remove(u);
    }

    public static ExcellentServerManager getPlugin() {
        return plugin;
    }
}
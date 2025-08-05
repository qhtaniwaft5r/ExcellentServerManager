//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//
package events;

import net.dv8tion.jda.api.entities.Member;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.abo876.excellentServerManager.ExcellentServerManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.UUID;

public class JoinMSG implements Listener {
    private final ExcellentServerManager plugin;
    public JoinMSG (ExcellentServerManager plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        var ff = e.getPlayer();

        plugin.getServer().dispatchCommand(ff, "nv");
        boolean tf2 = this.plugin.getConfig().getBoolean("Title.Enable");
        if (ff.isOnline()) {
            if (tf2) {
                String jm2 = this.plugin.getConfig().getString("Title.UPCase");
                jm2 = jm2.replace("%player%", e.getPlayer().getDisplayName());
                jm2 = jm2.replace("[player]", e.getPlayer().getDisplayName());
                String jm3 = this.plugin.getConfig().getString("Title.DownCase");
                jm3 = jm3.replace("%player%", e.getPlayer().getDisplayName());
                jm3 = jm3.replace("[player]", e.getPlayer().getDisplayName());
                int jm4 = this.plugin.getConfig().getInt("Title.Fadein");
                int jm5 = this.plugin.getConfig().getInt("Title.Stay");
                int jm6 = this.plugin.getConfig().getInt("Title.Fadeout");

                if (jm4 != 0 && jm5 != 0 && jm6 != 0) {
                    ff.sendTitle(ChatColor.translateAlternateColorCodes('&', jm2), ChatColor.translateAlternateColorCodes('&', jm3), jm4, jm5, jm6);
                }

                for (int i = 5; i >= 0; i--) {
                    if (i == 0) {
                        ff.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.translateAlternateColorCodes('&', "&b&lWelcome &e" + e.getPlayer().getDisplayName())));
                    }
                }

            }
            boolean tf = this.plugin.getConfig().getBoolean("Join.Enable");
            if (tf) {
                String jm = this.plugin.getConfig().getString("Join.JoinMessage");
                assert jm != null;
                jm = jm.replace("%player%", e.getPlayer().getDisplayName());
                jm = jm.replace("[player]", e.getPlayer().getDisplayName());
                e.setJoinMessage(ChatColor.translateAlternateColorCodes('&', jm));
            } else {
                e.setJoinMessage(" ");
            }
        }
        UUID playerId = e.getPlayer().getUniqueId();
        if (plugin.getDiscord().getBoolean("Discord.Enabled")) {
            plugin.onJoin(ff, e);
        if (plugin.getPlayerDiscordLinks().containsKey(playerId)) {
            String discordId = plugin.getPlayerDiscordLinks().get(playerId);
            if (plugin.getGuild() != null) {
                Member member = plugin.getGuild().getMemberById(discordId);
                if (member != null) {
                    ff.sendMessage("§a✓ You are linked to Discord as: " + member.getUser().getName());
                    if (plugin.getChatChannel() != null) {
                        if (plugin.getDiscord().getBoolean("settings.join-leave-messages")) {
                            plugin.getChatChannel().sendMessage("**" + ff.getName() + "** joined the server!").queue();
                        }
                    }
                }
            } else {
                ff.sendMessage("§eYou are not linked to Discord. Use /link to connect your account!");
            }

                plugin.onJoin(ff, e);
            }
        }



    }
}
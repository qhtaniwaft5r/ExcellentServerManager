//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package events;

import org.abo876.excellentServerManager.ExcellentServerManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class LeaveMSG implements Listener {
    private final ExcellentServerManager plugin;

    public LeaveMSG(ExcellentServerManager plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Boolean tf = this.plugin.getConfig().getBoolean("Leave.Enable");
        if (tf){
            String qm = this.plugin.getConfig().getString("Leave.LeaveMessage");
            qm = qm.replace("%player%", event.getPlayer().getDisplayName());
            qm = qm.replace("[player]", event.getPlayer().getDisplayName());
            if (qm != null) {
                event.setQuitMessage(ChatColor.translateAlternateColorCodes('&', qm));
            }
        }
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();
        plugin.onQuit(player, event);
        if (plugin.getPlayerDiscordLinks().containsKey(playerId) && plugin.getChatChannel() != null) {
            if (plugin.getDiscord().getBoolean("settings.join-leave-messages")){
            plugin.getChatChannel().sendMessage("**" + player.getName() + "** left the server!").queue();
        }}

    }
}

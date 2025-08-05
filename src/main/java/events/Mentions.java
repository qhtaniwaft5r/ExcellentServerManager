package events;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Arrays;

public class Mentions implements Listener {
    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        String originalMessage = event.getMessage();
        Player sender = event.getPlayer();

        Component message = Component.text(sender.getName() + ": ").color(NamedTextColor.GRAY);
        String[] words = originalMessage.split(" ");

        for (String word : words) {
            if (word.startsWith("@") && word.length() > 1) {
                String namePart = word.substring(1);

                OfflinePlayer match = Arrays.stream(Bukkit.getOfflinePlayers())
                        .filter(player -> player.getName() != null && player.getName().equalsIgnoreCase(namePart))
                        .findFirst()
                        .orElse(null);



                if (match != null) {
                    boolean isOnline = match.isOnline();
                    String matchedName = match.getName();


                    Component mention = Component.text("@" + matchedName + " ")
                            .color(isOnline ? NamedTextColor.GREEN : NamedTextColor.RED)
                            .decorate(TextDecoration.BOLD);

                    message = message.append(mention);

                    // ✅ Sound to sender
                    sender.playSound(sender.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 1.2f);

                    // ✅ Action bar to sender
                    sender.sendActionBar(Component.text("You mentioned @" + matchedName)
                            .color(NamedTextColor.AQUA));

                    // ✅ Sound & action bar to mentioned (if online)
                    if (match instanceof Player mentionedPlayer) {
                        mentionedPlayer.playSound(mentionedPlayer.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 1f);

                        mentionedPlayer.sendActionBar(Component.text("You were mentioned by @" + sender.getName())
                                .color(NamedTextColor.YELLOW));
                    }

                    continue;
                }
            }

            // Not a mention, just append
            message = message.append(Component.text(word + " "));
        }

        // Cancel default and send custom message
        event.setCancelled(true);
        Component finalMessage = message;
        Bukkit.getOnlinePlayers().forEach(p -> p.sendMessage(finalMessage));
    }

}

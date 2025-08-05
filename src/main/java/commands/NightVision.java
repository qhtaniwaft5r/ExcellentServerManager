package commands;

import org.abo876.excellentServerManager.ExcellentServerManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import utils.Chat;

import java.util.*;

public class NightVision implements CommandExecutor {
    private final ExcellentServerManager plugin;
    private final Chat chat;
    private final ArrayList<UUID> players = new ArrayList<>();

    public NightVision(ExcellentServerManager plugin) {
        this.plugin = plugin;
        this.chat = new Chat(plugin);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        var config = plugin.getConfig();
        if (!(sender instanceof Player p)) {
            chat.send(sender, "prefix", "action-noconsole");
            return false;
        }
        if (config.getBoolean("NightVision.Enabled")) {
            setNightVision(p, !players.contains(p.getUniqueId()));
        }else{
            p.sendActionBar(ChatColor.translateAlternateColorCodes('&', config.getString("NightVision.DisabledMessage", "&c&lNight Vision Disabled")));
        }
        return true;
    }


    public void setNightVision(Player p, boolean enabled){ {
        if (enabled) {
            p.addPotionEffect(PotionEffectType.NIGHT_VISION.createEffect(PotionEffect.INFINITE_DURATION, 1));
            players.add(p.getUniqueId());
            p.sendActionBar(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("NightVision.EnableMessage", "&a&lNight Vision Enabled")));
        }else {
            p.removePotionEffect(PotionEffectType.NIGHT_VISION);
            players.remove(p.getUniqueId());
            p.sendActionBar(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("NightVision.DisableMessage", "&a&lNight Vision Disabled")));
        }
    }
}
}

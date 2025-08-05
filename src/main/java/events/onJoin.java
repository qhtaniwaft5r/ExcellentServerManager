//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package events;

import commands.Spawn;
import org.abo876.excellentServerManager.ExcellentServerManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Random;

public class onJoin implements Listener {
    private final ExcellentServerManager plugin;
    private final Spawn spawn;

    public onJoin(ExcellentServerManager plugin) {
        this.plugin = plugin;
        this.spawn = new Spawn(plugin);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        if (this.plugin.getOnJoin()) {
            if (this.plugin.getSpawn().contains("FirstSpawn") && !e.getPlayer().hasPlayedBefore()) {
                this.spawn.tpToFirstSpawn(e.getPlayer(), false);
            } else if (this.plugin.getSpawn().contains("Spawn")) {
                this.spawn.tpToSpawn(e.getPlayer(), false);
            }
    }



        }
        }

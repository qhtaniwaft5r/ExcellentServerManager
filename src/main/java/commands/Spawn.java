//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package commands;

import org.abo876.excellentServerManager.ExcellentServerManager;
import org.bukkit.*;
import utils.Chat;
import utils.ServerVersion;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;


public class Spawn implements CommandExecutor {
    private final ExcellentServerManager plugin;
    private final Chat chat;

    public Spawn(ExcellentServerManager plugin) {
        this.plugin = plugin;
        this.chat = new Chat(plugin);
    }

    public static void access$000(Spawn this$0) {
    }

    public boolean onCommand( CommandSender sender, Command cmd, String label,  String[] args) {
        if (cmd.getName().equalsIgnoreCase("spawn")) {
            if (sender instanceof Player) {
                if (this.plugin.getConfig().getBoolean("Toggle.spawn-permission")) {
                    if (sender.hasPermission("nspawn.spawn")) {
                        this.checkSpawnCd((Player)sender);
                    } else {
                        this.chat.send(sender, "prefix", "no-permission");
                    }
                } else {
                    this.checkSpawnCd((Player)sender);
                }
            } else {
                this.chat.send(sender, "prefix", "action-noconsole");
            }
        }

        return true;
    }

    public void checkSpawnCd(Player p) {
        if (this.plugin.getConfig().getInt("Conditions.cooldown") > 0) {
            if (this.plugin.getConfig().getBoolean(("Conditions.bypass-cooldown-ops")) && p.hasPermission("op")) {
                this.tpToSpawn(p, true);
            } else if (this.plugin.getConfig().getBoolean("Conditions.check-location")) {
                this.tpWithCooldownLoc(p, p.getLocation().getBlockX(), p.getLocation().getBlockY(), p.getLocation().getBlockZ());
            } else {
                this.tpWithCooldown(p);
            }
        } else {
            this.tpToSpawn(p, true);
        }

    }

    public void tpWithCooldown(final Player p) {
        if (this.plugin.getSpawn().contains("Spawn")) {
            if (this.plugin.getPlayers().containsKey(p.getUniqueId())) {
                return;
            }

            this.chat.sendWithCd(p, "prefix", "spawn-cooldown");
            BukkitTask b = (new BukkitRunnable() {
                public void run() {
                    if (Spawn.this.plugin.getPlayers().containsKey(p.getUniqueId())) {
                        Spawn.this.tpToSpawn(p, true);
                        Spawn.this.plugin.removePlayer(p.getUniqueId());
                        this.cancel();
                    }

                }
            }).runTaskLater(this.plugin, (long)this.plugin.getConfig().getInt("Conditions.cooldown") * 20L);
            if (b.isSync()) {
                this.plugin.addPlayer(p.getUniqueId(), b);
            }
        } else {
            this.chat.send(p, "prefix", "spawn-notfound");
        }

    }

    public void tpWithCooldownLoc(final Player p, final int x, final int y, final int z) {
        if (this.plugin.getSpawn().contains("Spawn")) {
            if (this.plugin.getPlayers().containsKey(p.getUniqueId())) {
                return;
            }

            this.chat.sendWithCd(p, "prefix", "spawn-cooldown");
            BukkitTask b = (new BukkitRunnable() {
                public void run() {
                    if (Spawn.this.plugin.getPlayers().containsKey(p.getUniqueId())) {
                        if (x == p.getLocation().getBlockX() && y == p.getLocation().getBlockY() && z == p.getLocation().getBlockZ()) {
                            Spawn.this.tpToSpawn(p, true);
                        } else {
                            Spawn.this.chat.send(p, "prefix", "cancel-teleporting");
                        }

                        Spawn.this.plugin.removePlayer(p.getUniqueId());
                        this.cancel();
                    }

                }
            }).runTaskLater(this.plugin, (long)this.plugin.getConfig().getInt("Conditions.cooldown") * 20L);
            if (b.isSync()) {
                this.plugin.addPlayer(p.getUniqueId(), b);
            }
        } else {
            this.chat.send(p, "prefix", "spawn-notfound");
        }

    }

    private void teleportManager(Player p, String spawn, Boolean msg) {
        if (msg) {
            this.chat.send(p, "prefix", "spawn-teleporting");
        }

        if (this.plugin.getOnFall() && this.plugin.getConfig().getInt("Conditions.period-nofalldamage") > 0) {
            this.noFallDamageTimer(p.getUniqueId());
        }
        if (ServerVersion.isCurrentLower(ServerVersion.v1_14_R1)) {
            p.teleport((Location)Objects.requireNonNull(this.plugin.getSpawn().get(spawn)));
        } else {
            p.teleport((Location)Objects.requireNonNull((Location)this.plugin.getSpawn().getObject(spawn, Location.class)));
        }

        if (ServerVersion.isCurrentLower(ServerVersion.v1_9_R1)) {
            if (this.plugin.getBoolean("Effect.enabled")){
                try {
                    p.playEffect(p.getLocation(), Effect.valueOf(this.plugin.getConfig().getString("Effect.particle")), 0);
                } catch (IllegalArgumentException var6){
                    p.playEffect(p.getLocation(), Effect.valueOf(this.plugin.getConfig().getString("Effect.particle")), 0);
                }
            }
            if (this.plugin.getBoolean("Sound.enabled")) {
                try {
                    p.playSound(p.getLocation(), Sound.valueOf(this.plugin.getConfig().getString("Sound.music")), (float)this.plugin.getConfig().getDouble("Sound.volume"), (float)this.plugin.getConfig().getDouble("Sound.pitch"));
                } catch (IllegalArgumentException var6) {
                    p.playSound(p.getLocation(), Sound.valueOf("LEVEL_UP"), 3.0F, 0.5F);
                }
            }
        } else if (this.plugin.getBoolean("Sound.enabled")) {
            try {
                p.playSound(p.getLocation(), Sound.valueOf(this.plugin.getConfig().getString("Sound.music")), (float)this.plugin.getConfig().getDouble("Sound.volume"), (float)this.plugin.getConfig().getDouble("Sound.pitch"));
            } catch (IllegalArgumentException var5) {
                p.playSound(p.getLocation(), Sound.valueOf("ENTITY_PLAYER_LEVELUP"), 3.0F, 0.5F);
            }
        }


    }

    public void tpToSpawn(Player p, Boolean msg) {
        if (p.isOnline()) {
            if (this.plugin.getSpawn().contains("Spawn")) {
                this.teleportManager(p, "Spawn", msg);
            } else if (msg) {
                this.chat.send(p, "prefix", "spawn-notfound");
            }

        }
    }

    public void tpFromVoidToSpawn(final Player p, Boolean msg) {
        if (p.isOnline()) {
            if (this.plugin.getSpawn().contains("Spawn")) {
                if (p.getLocation().getY() < -65.0 && !this.plugin.getVoidPlayers().contains(p.getUniqueId())) {
                    this.plugin.addVoidPlayer(p.getUniqueId());
                    this.teleportManager(p, "Spawn", msg);
                    (new BukkitRunnable() {
                        public void run() {
                            Spawn.this.plugin.removeVoidPlayer(p.getUniqueId());
                            this.cancel();
                        }
                    }).runTaskLater(this.plugin, 20L);
                }
            } else if (msg) {
                this.chat.send(p, "prefix", "spawn-notfound");
            }

        }
    }

    public void tpToFirstSpawn(Player p, Boolean msg) {
        this.teleportManager(p, "FirstSpawn", msg);
    }

    public void tpAllToSpawn() {
        final List<Player> players = new ArrayList(Bukkit.getOnlinePlayers());
        (new BukkitRunnable() {
            public void run() {
                Iterator var1;
                Player p;
                int i;
                if (!Spawn.this.plugin.getBoolean("Conditions.prevent-tpall-ops")) {
                    if (players.size() <= 5) {
                        var1 = players.iterator();

                        while(var1.hasNext()) {
                            p = (Player)var1.next();
                            tpToSpawn(p, false);
                        }

                        players.clear();
                        this.cancel();
                    } else {
                        for(i = 0; i < 5; ++i) {
                            Spawn.this.tpToSpawn((Player)players.get(i), false);
                            players.remove(players.get(i));
                        }
                    }
                } else if (players.size() <= 5) {
                    var1 = players.iterator();

                    while(var1.hasNext()) {
                        p = (Player)var1.next();
                        if (!p.hasPermission("excellentservermanager.tpall")) {
                            Spawn.this.tpToSpawn(p, false);
                        }
                    }

                    players.clear();
                    this.cancel();
                } else {
                    for(i = 0; i < 5; ++i) {
                        if (!((Player)players.get(i)).hasPermission("excellentservermanager.tpall")) {
                            Spawn.this.tpToSpawn((Player)players.get(i), false);
                        }

                        players.remove(players.get(i));
                    }
                }

            }
        }).runTaskTimer(this.plugin, 0L, 10L);
    }

    public void noFallDamageTimer(final UUID u) {
        this.plugin.removeFallPlayer(u);
        this.plugin.addFallPlayer(u);
        (new BukkitRunnable() {
            public void run() {
                Spawn.this.plugin.removeFallPlayer(u);
            }
        }).runTaskLater(this.plugin, (long)this.plugin.getConfig().getInt("Conditions.period-nofalldamage") * 20L);
    }

}

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package commands;

import org.abo876.excellentServerManager.ExcellentServerManager;
import org.bukkit.command.TabCompleter;
import utils.Chat;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import utils.GUI;

import java.util.ArrayList;
import java.util.List;


public class ExcellentServerManagerCMD implements CommandExecutor, TabCompleter {
    private final ExcellentServerManager plugin;
    private final Chat chat;
    private final Spawn spawn;
    private final SetFirstSpawn setfirstspawn;
    private final SetSpawn setspawn;
    //private final Afk afk;

    public ExcellentServerManagerCMD(ExcellentServerManager plugin) {
        this.plugin = plugin;
        this.chat = new Chat(plugin);
        this.spawn = new Spawn(plugin);
        this.setfirstspawn = new SetFirstSpawn(plugin);
        this.setspawn = new SetSpawn(plugin);
        //this.afk = new Afk(plugin);
    }

    public boolean onCommand( CommandSender sender, Command cmd,  String label, String[] args) {
            if (sender instanceof Player) {

                if (this.plugin.getBoolean("Toggle.esmspawn-permission")) {
                    if (sender.hasPermission("excellentservermanager.spawn.*")) {
                        this.checkCommand(sender, args);
                    } else {
                        this.chat.send(sender, "prefix", "no-permission");
                    }
                } else {
                    this.checkCommand(sender, args);

                    }
            } else {
                this.checkCommandConsole(sender, args);
            }


        return true;
    }

    public void checkCommand(CommandSender sender, String[] args) {
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("reload")) {
                if (sender.hasPermission("excellentservermanager.reload")) {
                    this.plugin.reloadAllConfigs();
                    this.chat.send(sender, "prefix", "config-reloaded");
                } else {
                    this.chat.send(sender, "prefix", "no-permission");
                }
            } else if (args[0].equalsIgnoreCase("setspawn")) {
                if (sender.hasPermission("excellentservermanager.spawn.setspawn")) {
                    this.setspawn.setTheSpawn(sender);
                } else {
                    this.chat.send(sender, "prefix", "no-permission");
                }
            } else if (args[0].equalsIgnoreCase("setfirstspawn")) {
                if (sender.hasPermission("excellentservermanager.spawn.setfirstspawn")) {
                    this.setfirstspawn.setTheFirstSpawn(sender);
                } else {
                    this.chat.send(sender, "prefix", "no-permission");
                }
            } else if (args[0].equalsIgnoreCase("tp")) {
                if (sender.hasPermission("excellentservermanager.spawn.tp")) {
                    if (args.length == 2) {
                        if (this.plugin.getSpawn().contains("Spawn")) {
                            Player jug = Bukkit.getPlayer(args[1]);
                            if (jug != null) {
                                if (this.plugin.getPlayers().containsKey(jug.getUniqueId())) {
                                    this.plugin.cancelTeleport((BukkitTask)this.plugin.getPlayers().get(jug.getUniqueId()));
                                    this.plugin.removePlayer(jug.getUniqueId());
                                }

                                this.spawn.tpToSpawn(jug, false);
                                this.chat.sendWithTargetPlayer(sender, jug, "prefix", "tp-success");
                            } else {
                                this.chat.sendWithTarget(sender, args[1], "prefix", "player-notfound");
                            }
                        } else {
                            this.chat.send(sender, "prefix", "spawn-notfound");
                        }
                    } else {
                        this.chat.send(sender, "prefix", "tp-usage");
                    }
                } else {
                    this.chat.send(sender, "prefix", "no-permission");
                }
            } else if (args[0].equalsIgnoreCase("tpall")) {
                if (sender.hasPermission("excellentservermanager.spawn.tpall")) {
                    if (this.plugin.getSpawn().contains("Spawn")) {
                        this.spawn.tpAllToSpawn();
                        this.chat.send(sender, "prefix", "spawn-tpall");
                    } else {
                        this.chat.send(sender, "prefix", "spawn-notfound");
                    }
                } else {
                    this.chat.send(sender, "prefix", "no-permission");
                }
            } else if (args[0].equalsIgnoreCase("spawn")) {
                if (this.plugin.getBoolean("Toggle.spawn-permission")) {
                    if (sender.hasPermission("excellentservermanager.spawn.spawn")) {
                        this.spawn.checkSpawnCd((Player)sender);
                    } else {
                        this.chat.send(sender, "prefix", "no-permission");
                    }
                } else {
                    this.spawn.checkSpawnCd((Player)sender);
                }
            } else if (args[0].equalsIgnoreCase("firstspawn")) {
                if (sender.hasPermission("excellentservermanager.spawn.firstspawn")) {
                    if (this.plugin.getSpawn().contains("FirstSpawn")) {
                        this.spawn.tpToFirstSpawn((Player)sender, true);
                    } else {
                        this.chat.send(sender, "prefix", "spawn-notfound");
                    }
                } else {
                    this.chat.send(sender, "prefix", "no-permission");
                }
            }else if (args[0].equalsIgnoreCase("help")){
                Player p = (Player) sender;
                this.chat.sendList("command-list" ,p);
            }else if (args[0].equalsIgnoreCase("menu")) {
                new GUI(plugin).mainGUI((Player)sender);
            }/* else if (args[0].equalsIgnoreCase("setafk")) {
                if (sender.hasPermission("excellentservermanager.afk.setafk")) {
                    this.setspawn.setAfkSpawn(sender);
                } else {
                    this.chat.send(sender, "prefix", "no-permission");
                }
            } else if (args[0].equalsIgnoreCase("afk")) {
                if (this.plugin.getBoolean("Toggle.spawn-permission")) {
                    if (sender.hasPermission("excellentservermanager.afk.afk")) {
                        this.spawn.checkSpawnCd((Player)sender);
                    } else {
                        this.chat.send(sender, "prefix", "no-permission");
                    }
                } else {
                    //afk.checkSpawnCd((Player)sender);
                }
            } else if (args[0].equalsIgnoreCase("tpafk")) {
                if (sender.hasPermission("excellentservermanager.afk.tpafk")) {
                    if (args.length == 2) {
                        if (this.plugin.getSpawn().contains("Afk")) {
                            Player jug = Bukkit.getPlayer(args[1]);
                            if (jug != null) {
                                if (this.plugin.getPlayers().containsKey(jug.getUniqueId())) {
                                    this.plugin.cancelTeleport((BukkitTask)this.plugin.getPlayers().get(jug.getUniqueId()));
                                    this.plugin.removePlayer(jug.getUniqueId());
                                }

                                this.spawn.tpToSpawn(jug, false);
                                this.chat.sendWithTargetPlayer(sender, jug, "prefix", "tp-success");
                            } else {
                                this.chat.sendWithTarget(sender, args[1], "prefix", "player-notfound");
                            }
                        } else {
                            this.chat.send(sender, "prefix", "spawn-notfound");
                        }
                    } else {
                        this.chat.send(sender, "prefix", "tp-usage");
                    }
                } else {
                    this.chat.send(sender, "prefix", "no-permission");
                }
            } else if (args[0].equalsIgnoreCase("setplot")) {
                if (sender.hasPermission("op")) {
                    this.setspawn.setPlotSpawn(sender);
                } else {
                    this.chat.send(sender, "prefix", "no-permission");
                }
            } else if (args[0].equalsIgnoreCase("plot")){
                if (this.plugin.getBoolean("Toggle.spawn-permission")) {
                    if (sender.hasPermission("op")) {
                        this.spawn.checkSpawnCd((Player)sender);
                    } else {
                        this.chat.send(sender, "prefix", "no-permission");
                    }
                } else {
                    // afk.checkSpawnCd((Player)sender);
                }
            } else if (args[0].equalsIgnoreCase("tpplot")) {
                
            }*/
        } else {
            this.chat.sendList("command-list",sender);
        }

    }

    public void checkCommandConsole(CommandSender sender, String[] args) {
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("reload")) {
                this.plugin.reloadAllConfigs();
                this.chat.send(sender, "prefix", "config-reloaded");
            } else if (args[0].equalsIgnoreCase("setspawn")) {
                this.chat.send(sender, "prefix", "action-noconsole");
            } else if (args[0].equalsIgnoreCase("setfirstspawn")) {
                this.chat.send(sender, "prefix", "action-noconsole");
            } else if (args[0].equalsIgnoreCase("tp")) {
                if (args.length == 2) {
                    if (this.plugin.getSpawn().contains("Spawn")) {
                        Player jug = Bukkit.getPlayer(args[1]);
                        if (jug != null) {
                            if (this.plugin.getPlayers().containsKey(jug.getUniqueId())) {
                                this.plugin.cancelTeleport((BukkitTask)this.plugin.getPlayers().get(jug.getUniqueId()));
                                this.plugin.removePlayer(jug.getUniqueId());
                            }

                            this.spawn.tpToSpawn(jug, false);
                            this.chat.sendWithTargetPlayer(sender, jug, "prefix", "tp-success");
                        } else {
                            this.chat.sendWithTarget(sender, args[1], "prefix", "player-notfound");
                        }
                    } else {
                        this.chat.send(sender, "prefix", "spawn-notfound");
                    }
                } else {
                    this.chat.send(sender, "prefix", "tp-usage");
                }
            } else if (args[0].equalsIgnoreCase("tpall")) {
                if (this.plugin.getSpawn().contains("Spawn")) {
                    this.spawn.tpAllToSpawn();
                    this.chat.send(sender, "prefix", "spawn-tpall");
                } else {
                    this.chat.send(sender, "prefix", "spawn-notfound");
                }
            } else if (args[0].equalsIgnoreCase("spawn")) {
                this.chat.send(sender, "prefix", "action-noconsole");
            } else if (args[0].equalsIgnoreCase("firstspawn")) {
                this.chat.send(sender, "prefix", "action-noconsole");
            } else if (args[0].equalsIgnoreCase("menu")) {
                new GUI(plugin).mainGUI((Player)sender);
            }
        } else {
            this.chat.sendList("command-list", sender);
        }

    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        List<String> list = new ArrayList();

        if (strings.length == 1) {
            list.add("reload");
            list.add("tp");
            list.add("tpall");
            list.add("setspawn");
            list.add("setfirstspawn");
            list.add("spawn");
            list.add("firstspawn");
            list.add("help");
            //list.add("setafk");
            //list.add("afk");
            list.add("menu");
        }

        if (strings.length == 2) {
            if (strings[0].equalsIgnoreCase("tp")) {
                ArrayList<String> list2 = new ArrayList();
                for (Player player : Bukkit.getOnlinePlayers()) {
                    list2.add(player.getName());
                }
                list.addAll(list2);

            }
        }
        return list.stream()
                .filter(option -> option.toLowerCase().startsWith(strings[strings.length - 1].toLowerCase()))
                .toList();
    }
}

package events;

import commands.SetFirstSpawn;
import commands.SetSpawn;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.abo876.excellentServerManager.ExcellentServerManager;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.GUI;

import java.util.*;



public class GUIListener implements Listener {
    private static final Map<UUID, Set<String>> playerSelectedWorlds = new HashMap<>();
    private static final String CONFIRM_ITEM_NAME = "&a&lConfirm Selection";
    private static final String CONFIRM_ITEM_LORE = "&7Click to save selected worlds";
    private static final int CONFIRM_SLOT = 26;
    private final ExcellentServerManager plugin;
    private SetSpawn spawn;
    private SetFirstSpawn setfirstspawn;
    private GUI gui;
    private final Map<UUID, String> awaitingChatInput = new HashMap<>();
    private static final String WORLD_SELECTOR_TITLE = "§e§lSelect World";
    private static final int BACK_SLOT_WORLD = 18;



    private static final Set<Integer> WORLD_GUI_FILLER_SLOTS = new HashSet<>(Arrays.asList(
            0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 19, 20, 21, 22, 23, 24, 25, 26
    ));





    public GUIListener(ExcellentServerManager plugin) {
        this.plugin = plugin;
        this.spawn = new SetSpawn(plugin);
        this.setfirstspawn = new SetFirstSpawn(plugin);
        this.gui = new GUI(plugin);

    }
    @EventHandler
    public void onClick(InventoryClickEvent e) {
        var p = (Player) e.getWhoClicked();
        if (e.getClickedInventory() != null) {
            if (e.getView().getTitle().equals(ChatColor.YELLOW + "ExcellentServerManager Main Menu")) {
                e.setCancelled(true);
                switch (e.getSlot()) {
                    case 40:
                        p.closeInventory();
                        gui.configGUI(p);
                        break;
                    case 25:
                        setfirstspawn.setTheFirstSpawn(e.getWhoClicked());
                        break;
                    case 23:
                        spawn.setTheSpawn(e.getWhoClicked());
                        break;
                    case 21:
                        p.closeInventory();
                        gui.spawnGUI(p);
                        break;
                    case 19:
                        p.closeInventory();
                        gui.joinleaveGUI(p);
                        break;
                    default:
                }
            } else if (e.getView().getTitle().equals(ChatColor.YELLOW + "ExcellentServerManager Configuration")) {
                e.setCancelled(true);
                switch (e.getSlot()) {
                    case 36:
                        p.closeInventory();
                        gui.mainGUI(p);
                        break;
                    case 11:
                        p.closeInventory();
                        gui.ssGUI(p);
                        break;
                    case 29:
                        p.closeInventory();
                        gui.resetblockGUI(p);
                        break;
                    case 15:
                        p.closeInventory();
                        gui.broadcastGUI(p);
                        break;
                    case 33:
                        p.closeInventory();
                        gui.nightvisionGUI(p);
                        break;
                    case 32:

                        break;

                }

            } else if (e.getView().getTitle().equals(ChatColor.YELLOW + "Spawn Configuration")) {
                e.setCancelled(true);
                switch (e.getSlot()) {
                    case 45:
                        p.closeInventory();
                        gui.mainGUI(p);
                        break;
                    case 10:
                        p.closeInventory();

                        Component message = Component.text(ChatColor.GREEN + "True").hoverEvent(HoverEvent.showText(Component.text(ChatColor.GREEN + "Click to set on death back to the spawn True")))
                                .clickEvent(ClickEvent.runCommand("/spawn_death_back_true"));
                        Component message2 = Component.text(ChatColor.RED + "False").hoverEvent(HoverEvent.showText(Component.text(ChatColor.RED + "Click to set on death back to the spawn False")))
                                .clickEvent(ClickEvent.runCommand("/spawn_death_back_False"));
                        p.sendMessage(ChatColor.YELLOW + "please click true or false");
                        p.sendMessage(" ");
                        p.sendMessage(message);
                        p.sendMessage(" ");
                        p.sendMessage(message2);
                        break;
                    case 20:
                        p.closeInventory();

                        Component message3 = Component.text(ChatColor.GREEN + "True").hoverEvent(HoverEvent.showText(Component.text(ChatColor.GREEN + "Click to set on join back to the spawn True")))
                                .clickEvent(ClickEvent.runCommand("/spawn_join_back_true"));
                        Component message4 = Component.text(ChatColor.RED + "False").hoverEvent(HoverEvent.showText(Component.text(ChatColor.RED + "Click to set on join back to the spawn False")))
                                .clickEvent(ClickEvent.runCommand("/spawn_join_back_False"));
                        p.sendMessage(ChatColor.YELLOW + "please click true or false");
                        p.sendMessage(" ");
                        p.sendMessage(message3);
                        p.sendMessage(" ");
                        p.sendMessage(message4);
                        break;
                    case 28:
                        p.closeInventory();
                        Component message5 = Component.text(ChatColor.GREEN + "True").hoverEvent(HoverEvent.showText(Component.text(ChatColor.GREEN + "Click to set on join back to the spawn True")))
                                .clickEvent(ClickEvent.runCommand("/spawn_fall_back_true"));
                        Component message6 = Component.text(ChatColor.RED + "False").hoverEvent(HoverEvent.showText(Component.text(ChatColor.RED + "Click to set on join back to the spawn False")))
                                .clickEvent(ClickEvent.runCommand("/spawn_fall_back_False"));
                        p.sendMessage(ChatColor.YELLOW + "please click true or false");
                        p.sendMessage(" ");
                        p.sendMessage(message5);
                        p.sendMessage(" ");
                        p.sendMessage(message6);
                        break;
                    case 13:
                        p.closeInventory();
                        Component message7 = Component.text(ChatColor.GREEN + "True").hoverEvent(HoverEvent.showText(Component.text(ChatColor.GREEN + "Click to set bypass op cooldown tp to the spawn True")))
                                .clickEvent(ClickEvent.runCommand("/spawn_cooldown_op_true"));
                        Component message8 = Component.text(ChatColor.RED + "False").hoverEvent(HoverEvent.showText(Component.text(ChatColor.RED + "Click to set bypass op cooldown tp to the spawn False")))
                                .clickEvent(ClickEvent.runCommand("/spawn_cooldown_op_False"));
                        p.sendMessage(ChatColor.YELLOW + "please click true or false");
                        p.sendMessage(" ");
                        p.sendMessage(message7);
                        p.sendMessage(" ");
                        p.sendMessage(message8);
                        break;
                    case 22:
                        p.closeInventory();
                        Component message9 = Component.text(ChatColor.GREEN + "True").hoverEvent(HoverEvent.showText(Component.text(ChatColor.GREEN + "Click to set che the location for the player before tp him to the spawn True")))
                                .clickEvent(ClickEvent.runCommand("/spawn_check_location_true"));
                        Component message10 = Component.text(ChatColor.RED + "False").hoverEvent(HoverEvent.showText(Component.text(ChatColor.RED + "Click to set che the location for the player before tp him to the spawn False")))
                                .clickEvent(ClickEvent.runCommand("/spawn_check_location_False"));
                        p.sendMessage(ChatColor.YELLOW + "please click true or false");
                        p.sendMessage(" ");
                        p.sendMessage(message9);
                        p.sendMessage(" ");
                        p.sendMessage(message10);
                        break;
                    case 31:
                        p.closeInventory();
                        awaitingChatInput.put(p.getUniqueId(), "spawn_cooldown");
                        p.sendMessage(ChatColor.YELLOW + "Please send a message to enter the spawn string:");
                        break;
                    case 16:
                        p.closeInventory();
                        awaitingChatInput.put(p.getUniqueId(), "spawn_period_nofalldamage");
                        p.sendMessage(ChatColor.YELLOW + "Please send a message to enter the spawn string:");
                        break;
                    case 24:
                        p.closeInventory();
                        Component message11 = Component.text(ChatColor.GREEN + "True").hoverEvent(HoverEvent.showText(Component.text(ChatColor.GREEN + "Click to set prevent fall damage to True That's mean cancel it")))
                                .clickEvent(ClickEvent.runCommand("/spawn_prevent_falldamage_true"));
                        Component message12 = Component.text(ChatColor.RED + "False").hoverEvent(HoverEvent.showText(Component.text(ChatColor.RED + "Click to set prevent fall damage to False That's mean allow it")))
                                .clickEvent(ClickEvent.runCommand("/spawn_prevent_falldamage_False"));
                        p.sendMessage(ChatColor.YELLOW + "please click true or false");
                        p.sendMessage(" ");
                        p.sendMessage(message11);
                        p.sendMessage(" ");
                        p.sendMessage(message12);
                        break;
                    case 34:
                        p.closeInventory();
                        Component message13 = Component.text(ChatColor.GREEN + "True").hoverEvent(HoverEvent.showText(Component.text(ChatColor.GREEN + "Click to set prevent op tp to the spawn to True That's mean cancel it")))
                                .clickEvent(ClickEvent.runCommand("/spawn_prevent_op_tp_true"));
                        Component message14 = Component.text(ChatColor.RED + "False").hoverEvent(HoverEvent.showText(Component.text(ChatColor.RED + "Click to set prevent op tp to the spawn to False That's mean allow it")))
                                .clickEvent(ClickEvent.runCommand("/spawn_prevent_op_tp_false"));
                        p.sendMessage(ChatColor.YELLOW + "please click true or false");
                        p.sendMessage(" ");
                        p.sendMessage(message13);
                        p.sendMessage(" ");
                        p.sendMessage(message14);
                        break;

                }

            } else if (e.getView().getTitle().equals(ChatColor.YELLOW + "Join&Leave Message Configuration")) {
                e.setCancelled(true);
                switch (e.getSlot()) {
                    case 11:
                        p.closeInventory();
                        awaitingChatInput.put(p.getUniqueId(), "leave_message_string");
                        p.sendMessage(ChatColor.YELLOW + "Please send a message to enter the leave message string:");
                        break;
                    case 15:
                        p.closeInventory();
                        awaitingChatInput.put(p.getUniqueId(), "join_message");
                        p.sendMessage(ChatColor.YELLOW + "Please send a message to enter the join message string:");
                        break;
                    case 29:
                        p.closeInventory();
                        Component message3 = Component.text(ChatColor.GREEN + "True").hoverEvent(HoverEvent.showText(Component.text(ChatColor.GREEN + "Click to set leave message to True")))
                                .clickEvent(ClickEvent.runCommand("/msg_leave_true"));
                        Component message4 = Component.text(ChatColor.RED + "False").hoverEvent(HoverEvent.showText(Component.text(ChatColor.RED + "Click to set leave message to False")))
                                .clickEvent(ClickEvent.runCommand("/msg_leave_false"));
                        p.sendMessage(ChatColor.YELLOW + "please click true or false");
                        p.sendMessage(" ");
                        p.sendMessage(message3);
                        p.sendMessage(" ");
                        p.sendMessage(message4);
                        break;
                    case 33:
                        p.closeInventory();
                        gui.joinBooleanIntegerGUI(p);
                        break;
                    case 24:
                        p.closeInventory();
                        gui.joinTitleGUI(p);
                        break;
                    case 36:
                        p.closeInventory();
                        gui.mainGUI(p);
                        break;
                }
            } else if (e.getView().getTitle().equals(ChatColor.YELLOW + "Join Boolean&Integer Configuration")) {
                e.setCancelled(true);
                switch (e.getSlot()) {
                    case 18:
                        p.closeInventory();
                        gui.joinleaveGUI(p);
                        break;
                    case 10:
                        p.closeInventory();
                        Component message = Component.text(ChatColor.GREEN + "True").hoverEvent(HoverEvent.showText(Component.text(ChatColor.GREEN + "Click to set title join to True")))
                                .clickEvent(ClickEvent.runCommand("/title_join_true"));
                        Component message2 = Component.text(ChatColor.RED + "False").hoverEvent(HoverEvent.showText(Component.text(ChatColor.RED + "Click to set title join to False")))
                                .clickEvent(ClickEvent.runCommand("/title_join_False"));
                        p.sendMessage(ChatColor.YELLOW + "please click true or false");
                        p.sendMessage(" ");
                        p.sendMessage(message);
                        p.sendMessage(" ");
                        p.sendMessage(message2);
                        break;
                    case 16:
                        p.closeInventory();
                        Component message3 = Component.text(ChatColor.GREEN + "True").hoverEvent(HoverEvent.showText(Component.text(ChatColor.GREEN + "Click to set message join to True")))
                                .clickEvent(ClickEvent.runCommand("/msg_join_true"));
                        Component message4 = Component.text(ChatColor.RED + "False").hoverEvent(HoverEvent.showText(Component.text(ChatColor.RED + "Click to set message join to False")))
                                .clickEvent(ClickEvent.runCommand("/msg_join_False"));
                        p.sendMessage(ChatColor.YELLOW + "please click true or false");
                        p.sendMessage(" ");
                        p.sendMessage(message3);
                        p.sendMessage(" ");
                        p.sendMessage(message4);
                        break;
                    case 14:
                        p.closeInventory();
                        awaitingChatInput.put(p.getUniqueId(), "title_fadein");
                        p.sendTitle(ChatColor.GOLD + "Enter The Time", "Please enter the fade in time", 0, 100, 10);

                        break;
                    case 13:
                        p.closeInventory();
                        awaitingChatInput.put(p.getUniqueId(), "title_stay");
                        p.sendTitle(ChatColor.GOLD + "Enter The Time", "Please enter the stay time", 0, 100, 10);

                        break;
                    case 12:
                        p.closeInventory();
                        awaitingChatInput.put(p.getUniqueId(), "title_fadeout");
                        p.sendTitle(ChatColor.GOLD + "Enter The Time", "Please enter the fadeout time", 0, 100, 10);

                        break;


                }
            } else if (e.getView().getTitle().equals(ChatColor.YELLOW + "Join Title Configuration")) {
                e.setCancelled(true);
                switch (e.getSlot()) {
                    case 11:
                        p.closeInventory();
                        awaitingChatInput.put(p.getUniqueId(), "title_upcase");
                        p.sendTitle(ChatColor.GOLD + "Enter The Up Case", "Please send the title up case", 0, 100, 10);
                        break;
                    case 15:
                        p.closeInventory();
                        awaitingChatInput.put(p.getUniqueId(), "title_downcase");
                        p.sendTitle(ChatColor.GOLD + "Enter The Down Case", "Please send the title down case", 0, 100, 10);
                        break;
                    case 18:
                        p.closeInventory();
                        gui.joinleaveGUI(p);
                        break;
                }
            } else if (e.getView().getTitle().equals(ChatColor.YELLOW + "Reset Block Configuration")) {
                e.setCancelled(true);
                switch (e.getSlot()) {
                    case 30:
                        p.closeInventory();
                        Component message = Component.text(ChatColor.GREEN + "True").hoverEvent(HoverEvent.showText(Component.text(ChatColor.GREEN + "Click to set reset block to True")))
                                .clickEvent(ClickEvent.runCommand("/resetblock_true"));
                        Component message2 = Component.text(ChatColor.RED + "False").hoverEvent(HoverEvent.showText(Component.text(ChatColor.RED + "Click to set reset block to False")))
                                .clickEvent(ClickEvent.runCommand("/resetblock_false"));
                        p.sendMessage(ChatColor.YELLOW + "please click true or false");
                        p.sendMessage(" ");
                        p.sendMessage(message);
                        p.sendMessage(" ");
                        p.sendMessage(message2);
                        break;
                    case 15:
                        p.closeInventory();
                        awaitingChatInput.put(p.getUniqueId(), "resetblock_time");
                        p.sendTitle(ChatColor.GOLD + "Enter The Time", "Please enter the time", 0, 100, 10);
                        break;
                    case 13:
                        p.closeInventory();
                        gui.openWorldChooser(p);
                        break;
                    case 36:
                        p.closeInventory();
                        gui.configGUI(p);
                        break;
                }

            } else if (e.getView().getTitle().equals(ChatColor.YELLOW + "Night Vision Configuration")) {


                e.setCancelled(true);

            } else if (e.getView().getTitle().equals(WORLD_SELECTOR_TITLE)) {

                e.setCancelled(true);

                Player player = (Player) e.getWhoClicked();
                int slot = e.getSlot();
                UUID playerId = player.getUniqueId();

                // Check if clicked on filler slots
                if (WORLD_GUI_FILLER_SLOTS.contains(slot)) {
                    return;
                }

                // Handle back button
                if (slot == BACK_SLOT_WORLD) {
                    playerSelectedWorlds.remove(playerId); // Clear selection on back
                    player.closeInventory();
                    gui.resetblockGUI(player);
                    return;
                }

                // Handle confirm button
                if (slot == CONFIRM_SLOT) {
                    // Debug: Check if we even get here
                    plugin.getLogger().info("DEBUG: Confirm button clicked by " + player.getName());

                    Set<String> selectedWorlds = playerSelectedWorlds.get(playerId);

                    // Debug: Check selected worlds
                    plugin.getLogger().info("DEBUG: Selected worlds for player: " + selectedWorlds);

                    if (selectedWorlds != null && !selectedWorlds.isEmpty()) {
                        // Debug: Show worlds before conversion
                        plugin.getLogger().info("DEBUG: Converting worlds to list: " + selectedWorlds);

                        // Convert Set to List for config saving
                        List<String> worldsList = new ArrayList<>(selectedWorlds);

                        // Debug: Show converted list
                        plugin.getLogger().info("DEBUG: Converted to list: " + worldsList);

                        // Debug: Show config path and current value
                        plugin.getLogger().info("DEBUG: Current config value: " + plugin.getConfig().getStringList("BlockReset.World"));

                        // Save selected worlds to config
                        plugin.getConfig().set("BlockReset.World", worldsList);

                        // Debug: Show config value after setting
                        plugin.getLogger().info("DEBUG: Config value after set: " + plugin.getConfig().getStringList("BlockReset.World"));

                        try {
                            // Debug: Before save
                            plugin.getLogger().info("DEBUG: Attempting to save config...");
                            plugin.saveConfig();
                            plugin.getLogger().info("DEBUG: Config saved successfully");

                            // Debug: After save, check if it's still there
                            plugin.reloadConfig();
                            plugin.getLogger().info("DEBUG: Config reloaded, value is now: " + plugin.getConfig().getStringList("BlockReset.World"));

                            // Debug: Check config file location
                            plugin.getLogger().info("DEBUG: Config file location: " + plugin.getConfig().getCurrentPath());
                            plugin.getLogger().info("DEBUG: Plugin data folder: " + plugin.getDataFolder().getAbsolutePath());

                            player.sendMessage("§aSelected worlds have been saved to configuration!");
                            player.sendMessage("§7Saved worlds: " + String.join(", ", selectedWorlds));

                        } catch (Exception ex) {
                            plugin.getLogger().severe("DEBUG: Error saving config: " + ex.getMessage());
                            player.sendMessage("§cError saving configuration: " + ex.getMessage());
                            ex.printStackTrace();
                        }

                        playerSelectedWorlds.remove(playerId);
                        player.closeInventory();
                        gui.resetblockGUI(player);
                    } else {
                        plugin.getLogger().info("DEBUG: No worlds selected or selectedWorlds is null");
                        player.sendMessage("§cNo worlds selected! Please select at least one world.");
                    }
                    return;
                }

                // Handle world selection/deselection
                ItemStack clickedItem = e.getCurrentItem();
                if (clickedItem == null || clickedItem.getItemMeta() == null) {
                    return;
                }

                String displayName = clickedItem.getItemMeta().getDisplayName();
                if (displayName == null || displayName.isEmpty()) {
                    return;
                }

                String worldName = ChatColor.stripColor(displayName);

                // Skip if this is not a world item (check if it's a button)
                if (worldName.equals("Confirm Selection") || worldName.equals("Back") || worldName.equals(" ")) {
                    return;
                }

                World world = Bukkit.getWorld(worldName);

                if (world != null) {
                    Set<String> selectedWorlds = playerSelectedWorlds.get(playerId);
                    if (selectedWorlds == null) {
                        selectedWorlds = new HashSet<>();
                        playerSelectedWorlds.put(playerId, selectedWorlds);
                    }

                    // Toggle world selection
                    if (selectedWorlds.contains(worldName)) {
                        selectedWorlds.remove(worldName);
                        // Remove enchantment
                        gui.removeEnchantmentGlow(clickedItem);
                        player.sendMessage("§cDeselected world: " + worldName);
                    } else {
                        selectedWorlds.add(worldName);
                        // Add enchantment
                        gui.addEnchantmentGlow(clickedItem);
                        player.sendMessage("§aSelected world: " + worldName);
                    }

                    // Update the item in the inventory
                    e.getInventory().setItem(slot, clickedItem);
                } else {
                    player.sendMessage("§cWorld not found: " + worldName);
                }
            }
        }
    }
    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        if (awaitingChatInput.containsKey(player.getUniqueId())) {
            event.setCancelled(true);
            String input = event.getMessage();
            Bukkit.getScheduler().runTask(plugin, () -> {
                switch (awaitingChatInput.get(uuid)) {
                    case "join_message":
                        event.setCancelled(true);
                        awaitingChatInput.remove(uuid);
                        plugin.getConfig().set("Join.JoinMessage", input);
                        plugin.saveConfig();
                        player.sendMessage(ChatColor.GREEN + "Join message set to: " + ChatColor.AQUA + input);
                        gui.joinleaveGUI(player);
                        break;
                    case "title_fadein":
                        event.setCancelled(true);
                        if (isInteger(input)) {
                            awaitingChatInput.remove(uuid);
                            plugin.getConfig().set("Title.Fadein", Integer.parseInt(input));
                            plugin.saveConfig();
                            player.sendMessage(ChatColor.GREEN + "Title fadein set to: " + ChatColor.AQUA + input);
                            gui.joinBooleanIntegerGUI(player);
                        } else {
                            player.sendMessage(ChatColor.RED + "Please enter a number");
                        }
                        break;
                    case "title_stay":
                        event.setCancelled(true);
                        if (isInteger(input)) {
                            awaitingChatInput.remove(uuid);
                            plugin.getConfig().set("Title.Stay", Integer.parseInt(input));
                            plugin.saveConfig();
                            player.sendMessage(ChatColor.GREEN + "Title stay set to: " + ChatColor.AQUA + input);
                            gui.joinBooleanIntegerGUI(player);
                        } else {
                            player.sendMessage(ChatColor.RED + "Please enter a number");
                        }
                        break;
                    case "title_fadeout":
                        event.setCancelled(true);
                        if (isInteger(input)) {
                            awaitingChatInput.remove(uuid);
                            plugin.getConfig().set("Title.Fadeout", Integer.parseInt(input));
                            plugin.saveConfig();
                            player.sendMessage(ChatColor.GREEN + "Title fadeout set to: " + ChatColor.AQUA + input);
                            gui.joinBooleanIntegerGUI(player);
                        } else {
                            player.sendMessage(ChatColor.RED + "Please enter a number");
                        }
                        break;
                    case "leave_message_string":
                        event.setCancelled(true);
                        awaitingChatInput.remove(uuid);
                        plugin.getConfig().set("Leave.LeaveMessage", input);
                        plugin.saveConfig();
                        player.sendMessage(ChatColor.GREEN + "Leave message set to: " + ChatColor.AQUA + input);
                        gui.joinleaveGUI(player);
                        break;
                    case "title_upcase":
                        event.setCancelled(true);
                        awaitingChatInput.remove(uuid);
                        plugin.getConfig().set("Title.UPCase", input);
                        plugin.saveConfig();
                        player.sendMessage(ChatColor.GREEN + "Title upcase set to: " + ChatColor.AQUA + input);
                        gui.joinTitleGUI(player);
                        break;
                    case "title_downcase":
                        event.setCancelled(true);
                        awaitingChatInput.remove(uuid);
                        plugin.getConfig().set("Title.DownCase", input);
                        plugin.saveConfig();
                        player.sendMessage(ChatColor.GREEN + "Title downcase set to: " + ChatColor.AQUA + input);
                        gui.joinTitleGUI(player);

                        break;
                    case "spawn_cooldown":
                        event.setCancelled(true);
                        if (isInteger(input)) {
                            awaitingChatInput.remove(uuid);
                            plugin.getConfig().set("Conditions.cooldown", Integer.parseInt(input));
                            plugin.saveConfig();
                            player.sendMessage(ChatColor.GREEN + "Spawn cooldown set to: " + ChatColor.AQUA + input);
                            gui.spawnGUI(player);
                        } else {
                            player.sendMessage(ChatColor.RED + "Please enter a number");
                        }
                        break;
                    case "spawn_period_nofalldamage":
                        event.setCancelled(true);
                        if (isInteger(input)) {
                            awaitingChatInput.remove(uuid);
                            plugin.getConfig().set("Conditions.period-nofalldamage", Integer.parseInt(input));
                            plugin.saveConfig();
                            player.sendMessage(ChatColor.GREEN + "Spawn period nofalldamage set to: " + ChatColor.AQUA + input);
                            gui.spawnGUI(player);
                        } else {
                            player.sendMessage(ChatColor.RED + "Please enter a number");
                        }
                        break;
                    case "resetblock_time":
                        event.setCancelled(true);
                        if (isInteger(input)) {
                            awaitingChatInput.remove(uuid);
                            plugin.getConfig().set("BlockReset.Time", Integer.parseInt(input));
                            plugin.saveConfig();
                            player.sendMessage(ChatColor.GREEN + "Resetblock time set to: " + ChatColor.AQUA + input);
                            gui.resetblockGUI(player);
                        } else {
                            player.sendMessage(ChatColor.RED + "Please enter a number");
                        }
                        break;
                    default:
                        event.setCancelled(true);
                        player.sendMessage(ChatColor.RED + "Unknown input context: " + input);

                }
            });
        }
    }
    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        var p = event.getPlayer();
        var m = event.getMessage();


        if (event.getMessage().startsWith("/title_join_true")) {
            event.setCancelled(true);
            plugin.getConfig().set("Title.Enable", true);
            plugin.saveConfig();
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a&lDone The Title is now &2&lenabled"));

            gui.joinBooleanIntegerGUI(p);
        }else if (event.getMessage().startsWith("/title_join_False")) {
            event.setCancelled(true);
            plugin.getConfig().set("Title.Enable", false);
            plugin.saveConfig();
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a&lDone The Title is now &4&ldisabled"));

            gui.joinBooleanIntegerGUI(p);
        }else if (event.getMessage().startsWith("/msg_join_true")) {
            event.setCancelled(true);
            plugin.getConfig().set("Join.Enable", true);
            plugin.saveConfig();
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a&lDone The Join message is now &2&lenabled"));
            gui.joinBooleanIntegerGUI(p);
        }else if (event.getMessage().startsWith("/msg_join_False")) {
            event.setCancelled(true);
            plugin.getConfig().set("Join.Enable", false);
            plugin.saveConfig();
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a&lDone The Join message is now &4&ldisabled"));
            gui.joinBooleanIntegerGUI(p);
        }else if (event.getMessage().startsWith("/msg_leave_false")) {
            event.setCancelled(true);
            plugin.getConfig().set("Leave.Enable", false);
            plugin.saveConfig();
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a&lDone The Leave message is now &4&ldisabled"));
            gui.joinleaveGUI(p);
        }else if (event.getMessage().startsWith("/msg_leave_true")) {
            event.setCancelled(true);
            plugin.getConfig().set("Leave.Enable", true);
            plugin.saveConfig();
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a&lDone The Leave message is now &2&lenabled"));
            gui.joinleaveGUI(p);
        } else if (m.startsWith("/spawn_death_back_false")) {
            event.setCancelled(true);
            plugin.getConfig().set("Conditions.spawn-onDeath", false);
            plugin.saveConfig();
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a&lDone The player death back to spawn is now &4disabled"));
            gui.spawnGUI(p);
        }else if (m.startsWith("/spawn_death_back_true")) {
            event.setCancelled(true);
            plugin.getConfig().set("Conditions.spawn-onDeath", true);
            plugin.saveConfig();
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a&lDone The player death back to spawn is now &2&lenabled"));
            gui.spawnGUI(p);
        } else if (m.startsWith("/spawn_join_back_false")) {
            event.setCancelled(true);
            plugin.getConfig().set("Conditions.spawn-onJoin", false);
            plugin.saveConfig();
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a&lDone The player join back to spawn is now &4disabled"));
            gui.spawnGUI(p);
        }else if (m.startsWith("/spawn_join_back_true")) {
            event.setCancelled(true);
            plugin.getConfig().set("Conditions.spawn-onJoin", true);
            plugin.saveConfig();
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a&lDone The player join back to spawn is now &2&lenabled"));
            gui.spawnGUI(p);
        }else if (m.startsWith("/spawn_fall_back_false")) {
            event.setCancelled(true);
            plugin.getConfig().set("Conditions.spawn-onVoid", false);
            plugin.saveConfig();
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a&lDone The player fall to the void back to spawn is now &4disabled"));
            gui.spawnGUI(p);
        }else if (m.startsWith("/spawn_fall_back_true")) {
            event.setCancelled(true);
            plugin.getConfig().set("Conditions.spawn-onVoid", true);
            plugin.saveConfig();
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a&lDone The player fall to the void back to spawn is now &2&lenabled"));
            gui.spawnGUI(p);
        }else if (m.startsWith("/spawn_prevent_op_tp_true")) {
            event.setCancelled(true);
            plugin.getConfig().set("Conditions.prevent-tpall-ops", true);
            gui.spawnGUI(p);
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a&lDone Preventing ops from teleporting all players to spawn is now &2&lenabled"));
        }else if (m.startsWith("/spawn_prevent_op_tp_false")) {
            event.setCancelled(true);
            plugin.getConfig().set("Conditions.prevent-tpall-ops", false);
            gui.spawnGUI(p);
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a&lDone Preventing ops from teleporting all players to spawn is now &4&ldisabled"));
        } else if (m.startsWith("/resetblock_true")) {
            event.setCancelled(true);
            plugin.getConfig().set("BlockReset.Enabled", true);
            gui.resetblockGUI(p);
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a&lDone The Block Reset is now &2&lenabled"));
        } else if (m.startsWith("/resetblock_false")) {
            event.setCancelled(true);
            plugin.getConfig().set("BlockReset.Enabled", false);
            gui.resetblockGUI(p);
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a&lDone The Block Reset is now &4&ldisabled"));
        }
    }
    public boolean isInteger(String input) {
        try {
            Integer.parseInt(input);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void toggleWorldInConfig(String worldName) {
        List<String> allowedWorlds = plugin.getConfig().getStringList("BlockReset.World");

        if (allowedWorlds.contains(worldName)) {
            allowedWorlds.remove(worldName);
        } else {
            allowedWorlds.add(worldName);
        }

        plugin.getConfig().set("BlockReset.World", allowedWorlds);
        plugin.saveConfig();
    }
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        gui.clearPlayerSelection(event.getPlayer().getUniqueId());
    }


    /**
     * Handle configuration cancel (call this when player goes back)
     */
    public void handleConfigurationCancel(Player player) {
        gui.clearPlayerSelection(player.getUniqueId());
    }

    /**
     * Handle configuration finish (call this when player confirms)
     */
    public void handleConfigurationFinish(Player player) {
        gui.clearPlayerSelection(player.getUniqueId());
    }


}

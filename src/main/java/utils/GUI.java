package utils;


import org.abo876.excellentServerManager.ExcellentServerManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.*;


import static org.bukkit.Bukkit.createInventory;


public class GUI  { // Implement Listener

    private final ExcellentServerManager plugin;
    private static final String RESET_BLOCK_GUI_TITLE = ChatColor.YELLOW + "Reset Block Configuration";
    private static final String WORLD_SELECTOR_TITLE = "§e§lSelect World";
    private static final String BACK_ITEM_NAME = "&c&lBack";
    private static final String BACK_ITEM_LORE = "&7Click to go back";

    private static final int RESET_ENABLED_SLOT = 30;
    private static final int RESET_TIME_SLOT = 15;
    private static final int RESET_WORLD_SLOT = 13;
    private static final int BACK_SLOT_MAIN = 36;
    private static final int BACK_SLOT_WORLD = 18;


    private static final Map<UUID, Set<String>> playerSelectedWorlds = new HashMap<>();
    private static final int CONFIRM_SLOT = 26;private static final String CONFIRM_ITEM_NAME = "&a&lConfirm Selection";
    private static final String CONFIRM_ITEM_LORE = "&7Click to save selected worlds";

    // Filler slots as constants for better performance (updated to exclude confirm slot)
    private static final Set<Integer> MAIN_GUI_FILLER_SLOTS = new HashSet<>(Arrays.asList(
            1, 2, 3, 4, 5, 6, 7, 9, 17, 18, 27, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44
    ));

    private static final Set<Integer> WORLD_GUI_FILLER_SLOTS = new HashSet<>(Arrays.asList(
            0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 19, 20, 21, 22, 23, 24, 25
    )); // Removed slot 26 for confirm button

    public GUI(ExcellentServerManager plugin) {
        this.plugin = plugin;
    }


    public void mainGUI(Player p) {
        var inv = createInventory(p, 45, ChatColor.YELLOW + "ExcellentServerManager Main Menu");

        inv.setItem(40, createItem(Material.COMPARATOR, "&e&lExcellentServerManager Configuration", true, "&7Click to open ExcellentServerManager Configuration"));
        inv.setItem(25, createItem(Material.YELLOW_CONCRETE, "&e&lSet Spawn", "&7Click to set Spawn"));
        inv.setItem(23, createItem(Material.YELLOW_CONCRETE_POWDER, "&e&lSet First Spawn", "&7Click to set First Spawn"));
        inv.setItem(21, createItem(Material.ORANGE_CONCRETE, "&e&lSpawn Configuration", "&7Click to open Spawn Configuration"));
        inv.setItem(19, createItem(Material.ORANGE_CONCRETE_POWDER, "&e&lJoin & Leave Message Configuration", "&7Click to open Join & Leave Message Configuration"));
        inv.setItem(4, createItem(Material.YELLOW_STAINED_GLASS_PANE, "&e&lExellentServerManager Main Menu", true, "Here you can Configure the ExcellentServerManager Plugin"));




        p.openInventory(inv);
    }

    public void configGUI(Player p) {
        var inv = createInventory(p, 45, ChatColor.YELLOW + "ExcellentServerManager Configuration");
        filler(inv, 1, 2, 3, 4, 5, 6, 7, 9, 18, 27, 36, 37, 38, 39, 40, 41, 42, 43, 44, 17, 26, 35);
        inv.setItem(36, createItem(Material.RED_STAINED_GLASS_PANE, "&c&lBack", true, "&7Click to go back"));
        inv.setItem(11, createItem(Material.REDSTONE, "&e&lSS Configuration", true, "&7Click to open SS Configuration"));
        inv.setItem(29, createItem(Material.REDSTONE, "&e&lReset Block Configuration", true, "&7Click to open Reset Block Configuration"));
        inv.setItem(15, createItem(Material.REDSTONE, "&e&lBroad Cast Configuration", true, "&7Click to open Broad Cast Configuration"));
        inv.setItem(33, createItem(Material.REDSTONE, "&e&lNight Vision Configuration", true, "&7Click to open Night Vision Configuration"));
        p.openInventory(inv);
    }

    public void spawnGUI(Player p) {
        var inv = createInventory(p, 54, ChatColor.YELLOW + "Spawn Configuration");
        filler(inv, 0,1,2,3,4,5,6,7,8,9,18,27,36,46,47,48,49,50,51,52,53,44,35,26,17);
        inv.setItem(45, createItem(Material.RED_STAINED_GLASS_PANE, "&c&lBack", true, "&7Click to go back"));

        inv.setItem(10, createItem(Material.REDSTONE, "&e&lOn Death Back to Spawn Enabled?",true, "&7Click to Edit On Death Back to Spawn Enabled?"));
        inv.setItem(20, createItem(Material.REDSTONE, "&e&lOn Join Back to Spawn Enabled?",true, "&7Click to Edit On Join Back to Spawn Enabled?"));
        inv.setItem(28, createItem(Material.REDSTONE, "&e&lOn Fall to The Void Back to Spawn Enabled?",true, "&7Click to Edit On Fall to The Void Back to Spawn Enabled?"));

        inv.setItem(13, createItem(Material.REDSTONE, "&e&lCoolDown For Ops Disabled?",true, "&7Click to Edit CoolDown For Ops disabled?", "&6&lThat for the op player when he run /spawn there is cooldown for hem?"));
        inv.setItem(22, createItem(Material.REDSTONE, "&e&lCheck the Location Enabled?" ,true, "&7Click to Edit Check the Location Enabled?","&6&lThat for the laging player if there location is not correct?"));

        inv.setItem(31, createItem(Material.REDSTONE, "&e&lEdit the CoolDown Time",true, "&7Click to Edit the CoolDown Time", "&6&lThat for the player when he run /spawn there is cooldown for hem?"));

        inv.setItem(16, createItem(Material.REDSTONE, "&e&lWhen the player take a fall damage wait",true, "&e&lWhen the player take a fall damage wait &7" + plugin.getConfig().getInt("Conditions.period-nofalldamage") + " seconds to cooldown to run /spawn"));
        inv.setItem(24, createItem(Material.REDSTONE, "&e&lCancel the Fall Damage",true, "&7Click to Edit the Fall Damage", "&6&lThat for the player when he run /spawn and take a fall damage he get no fall damage?"));
        inv.setItem(34, createItem(Material.REDSTONE, "&e&lCancel TpAll Ops?" ,true,"&7Click to Edit TpAll Ops?","&6&lThat for The Command /ess tpall?"));

        p.openInventory(inv);
    }

    public void joinleaveGUI(Player p) {
        var inv = createInventory(p, 45, ChatColor.YELLOW + "Join&Leave Message Configuration");
        filler(inv, 1, 2, 3, 4, 5, 6, 7, 9, 18, 27, 36, 37, 38, 39, 40, 41, 42, 43, 44, 17, 26, 35, 13, 22, 31);
        inv.setItem(0, createItem(Material.YELLOW_CONCRETE, "&eLeave Message Suction", true));

        inv.setItem(11, createItem(Material.REDSTONE, "&e&lLeave Message String ",true, "&7Click to Edit Leave Message String "));
        inv.setItem(29, createItem(Material.REDSTONE, "&e&lLeave Boolean ",true, "&7Click to Edit Leave Boolean "));

        inv.setItem(8, createItem(Material.ORANGE_CONCRETE, "&6Join Message Suction", true));

        inv.setItem(15, createItem(Material.REDSTONE, "&6&lJoin Message String Editor",true, "&7Click to Edit Join Message String"));
        inv.setItem(24, createItem(Material.REDSTONE, "&6&lJoin Title String Editor",true, "&7Click to open Join Title String Editor"));
        inv.setItem(33, createItem(Material.REDSTONE, "&6&lJoin Boolean & Integer Editor",true, "&7Click to open Join Boolean & Integer Editor"));
        inv.setItem(36, createItem(Material.RED_STAINED_GLASS_PANE, "&c&lBack",true , "&7Click to go back"));
        p.openInventory(inv);
    }

    public void joinTitleGUI(Player p) {
        var inv = createInventory(p, 27, ChatColor.YELLOW + "Join Title Configuration");
        filler(inv, 0, 1, 2, 3,4, 5, 6, 7, 8, 19, 20, 21, 22, 23, 24, 25, 26, 17, 9);

        inv.setItem(11, createItem(Material.REDSTONE, "&e&lUp Case",true, "&7Click to Edit the Title Up Case", "&7When Click Send in The Chat The Title Up Case", "&7Now the title is ", plugin.getConfig().getString("Title.UPCase")));
        inv.setItem(15, createItem(Material.REDSTONE, "&e&lDown Case",true, "&7Click to Edit the Title Down Case", "&7When Click Send in The Chat The Title Down Case", "&7Now the title is ", plugin.getConfig().getString("Title.DownCase")));
        inv.setItem(18, createItem(Material.RED_STAINED_GLASS_PANE, "&c&lBack",true, "&7Click to go back"));
        p.openInventory(inv);
    }

    public void joinBooleanIntegerGUI(Player p) {
        var inv = createInventory(p, 27, ChatColor.YELLOW + "Join Boolean&Integer Configuration");
        filler(inv, 0, 1, 2, 3 ,4, 5, 6, 7, 8, 19, 20, 21, 22, 23, 24, 25, 26, 17, 9);

        inv.setItem(10, createItem(Material.REDSTONE, "&e&lJoin Title Enabled?",true, "&7Click to Edit Join Title Enabled?"));
        inv.setItem(16, createItem(Material.REDSTONE, "&e&lJoin Message Enabled?",true, "&7Click to Edit Join Message Enabled?"));

        inv.setItem(14, createItem(Material.REDSTONE, "&e&lJoin Title Fade In Time",true, "&7Click to Edit Join Title Fade In Time"));
        inv.setItem(13, createItem(Material.REDSTONE, "&e&lJoin Title Stay Time",true, "&7Click to Edit Join Title Stay Time"));
        inv.setItem(12, createItem(Material.REDSTONE, "&e&lJoin Title Fade Out Time",true, "&7Click to Edit Join Title Fade Out Time"));
        inv.setItem(18, createItem(Material.RED_STAINED_GLASS_PANE, "&e&lBack",true,     "&7Click to go Back"));
        p.openInventory(inv);
    }

    public void nightvisionGUI(Player p) {
        var inv = createInventory(p, 27, ChatColor.YELLOW + "Night Vision Configuration");
        filler(inv, 0, 1, 2, 3,4, 5, 6, 7, 8, 19, 20, 21, 22, 23, 24, 25, 26, 17, 9);

        inv.setItem(11, createItem(Material.REDSTONE, "&e&lNight Vision Enabled?",true, "&7Click to Edit Night Vision Enabled?"));
        inv.setItem(13, createItem(Material.REDSTONE, "&e&lNight Vision Fade In Time",true, "&7Click to Edit Night Vision Fade In Time"));
        inv.setItem(15, createItem(Material.RED_STAINED_GLASS_PANE, "&c&lBack",true, "&7Click to go back"));
        p.openInventory(inv);
    }



    public void broadcastGUI(Player p) {
        var inv = createInventory(p, 45, ChatColor.YELLOW + "Broadcast Configuration");
        filler(inv, 1, 2, 3, 4, 5, 6, 7, 9, 18, 27, 36, 37, 38, 39, 40, 41, 42, 43, 44, 17, 26, 35);

        inv.setItem(30, createItem(Material.REDSTONE, "&e&lBroadCast Enabled?",true, "&7Click to Edit BroadCast Enabled?"));
        inv.setItem(32, createItem(Material.REDSTONE, "&e&lBroaCast Title",true, "&7Click to Edit BroaCast Title"));
        inv.setItem(15, createItem(Material.REDSTONE, "&c&lBroadCast Fade In Time ",true, "&7Click to Edit BroadCast Fade In Time"));
        inv.setItem(13, createItem(Material.REDSTONE, "&c&lBroadCast Fade Out Time",true, "&7Click to Edit BroadCast Fade Out Time"));
        inv.setItem(11, createItem(Material.REDSTONE, "&c&lBroadCast Stay Time",true, "&7Click to Edit BroadCast Stay Time"));
        inv.setItem(36, createItem(Material.RED_STAINED_GLASS_PANE, "&c&lBack",true, "&7Click to go back"));
        p.openInventory(inv);
    }



    public void ssGUI(Player p) {
        var inv = createInventory(p, 45, ChatColor.YELLOW + "SS Configuration");

        p.openInventory(inv);
    }


    public void resetblockGUI(Player player) {
        Inventory inv = createInventory(player, 45, RESET_BLOCK_GUI_TITLE);

        // Apply filler items
        applyFillerItems(inv, MAIN_GUI_FILLER_SLOTS);

        // Set main configuration items
        inv.setItem(RESET_ENABLED_SLOT, createItem(Material.REDSTONE, "&e&lReset Block Enabled?", true, "&7Click to Edit Reset Block Enabled?"));
        inv.setItem(RESET_TIME_SLOT, createItem(Material.REDSTONE, "&c&lReset Block Time", true, "&7Click to Edit Reset Block Time"));
        inv.setItem(RESET_WORLD_SLOT, createItem(Material.REDSTONE, "&c&lReset Block World", true, "&7Click to Edit Reset Block World"));
        inv.setItem(BACK_SLOT_MAIN, createItem(Material.RED_STAINED_GLASS_PANE, BACK_ITEM_NAME, true, BACK_ITEM_LORE));

        player.openInventory(inv);
    }
    public void openWorldChooser(Player player) {
        List<World> worlds = Bukkit.getWorlds();

        // Initialize selected worlds for player if not exists
        UUID playerId = player.getUniqueId();
        if (!playerSelectedWorlds.containsKey(playerId)) {
            // Load currently configured worlds
            List<String> configuredWorlds = plugin.getConfig().getStringList("BlockReset.World");
            playerSelectedWorlds.put(playerId, new HashSet<>(configuredWorlds));
        }

        // Use fixed 27 slot inventory for consistency
        Inventory gui = Bukkit.createInventory(player, 27, WORLD_SELECTOR_TITLE);

        // Apply filler items
        applyFillerItems(gui, WORLD_GUI_FILLER_SLOTS);

        // Add back button
        gui.setItem(BACK_SLOT_WORLD, createItem(Material.RED_STAINED_GLASS_PANE, BACK_ITEM_NAME, false, BACK_ITEM_LORE));

        // Add confirm button (green block in bottom right)
        gui.setItem(CONFIRM_SLOT, createItem(Material.GREEN_CONCRETE, CONFIRM_ITEM_NAME, false, CONFIRM_ITEM_LORE));

        // Add world items
        Set<String> selectedWorlds = playerSelectedWorlds.get(playerId);
        for (World world : worlds) {
            ItemStack worldItem = getItemStack(world);

            // Add enchantment if world is selected
            if (selectedWorlds.contains(world.getName())) {
                addEnchantmentGlow(worldItem);
            }

            // Find first available slot that's not a filler, back button, or confirm button
            for (int i = 10; i < 18; i++) { // Use middle row for worlds
                if (gui.getItem(i) == null) {
                    gui.setItem(i, worldItem);
                    break;
                }
            }
        }

        player.openInventory(gui);
    }

    /**
     * Applies filler items to specified slots
     */
    private void applyFillerItems(Inventory inventory, Set<Integer> slots) {
        ItemStack fillerItem = createFillerItem();
        for (int slot : slots) {
            if (slot < inventory.getSize()) {
                inventory.setItem(slot, fillerItem);
            }
        }
    }
    public static void clearPlayerSelection(UUID playerId) {
        playerSelectedWorlds.remove(playerId);
    }
    public static void clearAllPlayerSelections() {
        playerSelectedWorlds.clear();
    }
    private ItemStack createFillerItem() {
        return createItem(Material.GRAY_STAINED_GLASS_PANE, " ", false, "");
    }
    private ItemStack getItemStack(World world) {
        // Create item based on world environment
        Material material;
        switch (world.getEnvironment()) {
            case NETHER:
                material = Material.NETHERRACK;
                break;
            case THE_END:
                material = Material.END_STONE;
                break;
            default:
                material = Material.GRASS_BLOCK;
                break;
        }
        return createItem(material, world.getName()); // Use world name as item name and lore for GUI;
    }

    public void filler(Inventory inv, int... ints) {
        var item = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        var itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(" "); // Single space for filler item name
        item.setItemMeta(itemMeta);
        for (int i : ints) {
            inv.setItem(i, item);
        }
    }

    private ItemStack createItem(Material material, String name, String... lore) {
        var item = new ItemStack(material);
        var itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        List<String> lores = new ArrayList<>();
        for (String s : lore) {
            lores.add(ChatColor.translateAlternateColorCodes('&', s));
        }
        itemMeta.setLore(lores);
        item.setItemMeta(itemMeta);
        return item;
    }

    private ItemStack createItem(Material material, String name, boolean visible_Enchantment, String... lore) {
        var item = new ItemStack(material);
        var itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        List<String> lores = new ArrayList<>();

        for (String s : lore) {
            lores.add(ChatColor.translateAlternateColorCodes('&', s));
        }
        itemMeta.setLore(lores);
        if (visible_Enchantment) {
            itemMeta.addEnchant(Enchantment.CHANNELING, 1, true);
            itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        item.setItemMeta(itemMeta);
        return item;
    }

    private ItemStack createItem(Material material, String name) {
        var item = new ItemStack(material);
        var itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        item.setItemMeta(itemMeta);
        return item;
    }

    private ItemStack createItem(Material material, String name, boolean ivsible_Enchantment) {
        var item = new ItemStack(material);
        var itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        if (ivsible_Enchantment) {
            itemMeta.addEnchant(Enchantment.CHANNELING, 1, true);
            itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        item.setItemMeta(itemMeta);
        return item;
    }
    public void addEnchantmentGlow(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.addEnchant(Enchantment.DURABILITY, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            item.setItemMeta(meta);
        }
    }
    public void removeEnchantmentGlow(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.removeEnchant(Enchantment.DURABILITY);
            meta.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
            item.setItemMeta(meta);
        }
    }

}
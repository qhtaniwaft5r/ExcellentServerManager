package utils;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Items {
    static ItemStack createItem(Material material, String name, String... lore) {
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



    private ItemStack createItem(Material material, String name, boolean ivsible_Enchantment, String... lore) {
        var item = new ItemStack(material);
        var itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        List<String> lores = new ArrayList<>();

        for (String s : lore) {
            lores.add(ChatColor.translateAlternateColorCodes('&', s));
        }
        itemMeta.setLore(lores);
        if (ivsible_Enchantment) {
            itemMeta.addEnchant(Enchantment.CHANNELING, 1, true);
            itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        item.setItemMeta(itemMeta);
        return item;
    }

    private static ItemStack createItem(Material material, String name) {
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
}

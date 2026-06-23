package com.mguhc.utils;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemBuilder {

    private final ItemStack item;
    private final ItemMeta meta;

    public ItemBuilder(Material material) {
        this.item = new ItemStack(material);
        this.meta = item.getItemMeta();
    }

    public ItemBuilder(Material material, int amount) {
        this.item = new ItemStack(material, amount);
        this.meta = item.getItemMeta();
    }

    public ItemBuilder(Material material, int amount, short durability) {
        this.item = new ItemStack(material, amount, durability);
        this.meta = item.getItemMeta();
    }

    public ItemBuilder name(String displayName) {
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName));
        return this;
    }

    public ItemBuilder enchant(Enchantment enchantment, int level) {
        meta.addEnchant(enchantment, level, true);
        return this;
    }

    public ItemBuilder clearEnchants() {
        meta.getEnchants().keySet().forEach(e -> meta.removeEnchant(e));
        return this;
    }

    public ItemStack build() {
        item.setItemMeta(meta);
        return item;
    }
}

package com.playares.luxe.crate.data;

import com.playares.commons.item.ItemBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import java.util.Map;

@AllArgsConstructor
public final class CrateLoot {
    @Getter public final String name;
    @Getter public final Material material;
    @Getter public final short durability;
    @Getter public final int amount;
    @Getter public final int chance;
    @Getter public final Map<Enchantment, Integer> enchantments;

    public ItemStack getItem() {
        if (material.equals(Material.ENCHANTED_BOOK)) {
            final ItemStack book = new ItemBuilder()
                    .setName(name)
                    .setMaterial(material)
                    .setAmount(amount)
                    .addLore(ChatColor.GREEN + "" + chance + "% chance")
                    .build();

            final EnchantmentStorageMeta meta = (EnchantmentStorageMeta)book.getItemMeta();

            enchantments.keySet().forEach(enchantment -> {
                final int level = enchantments.get(enchantment);
                meta.addStoredEnchant(enchantment, level, false);
            });

            book.setItemMeta(meta);
            return book;
        }

        return new ItemBuilder()
                .setMaterial(material)
                .setName(name)
                .setData(durability)
                .setAmount(amount)
                .addEnchant(enchantments)
                .addLore(ChatColor.GREEN + "" + chance + "% chance")
                .build();
    }

    public ItemStack getItemAsDrop() {
        if (material.equals(Material.ENCHANTED_BOOK)) {
            final ItemStack book = new ItemBuilder()
                    .setName(name)
                    .setMaterial(material)
                    .setAmount(amount)
                    .build();

            final EnchantmentStorageMeta meta = (EnchantmentStorageMeta)book.getItemMeta();

            enchantments.keySet().forEach(enchantment -> {
                final int level = enchantments.get(enchantment);
                meta.addStoredEnchant(enchantment, level, false);
            });

            book.setItemMeta(meta);
            return book;
        }

        return new ItemBuilder()
                .setMaterial(material)
                .setData(durability)
                .setAmount(amount)
                .addEnchant(enchantments)
                .build();
    }
}
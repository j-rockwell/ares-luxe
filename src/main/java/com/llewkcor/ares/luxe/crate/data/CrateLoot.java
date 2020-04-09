package com.llewkcor.ares.luxe.crate.data;

import com.llewkcor.ares.commons.item.ItemBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

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
        return new ItemBuilder()
                .setMaterial(material)
                .setData(durability)
                .setAmount(amount)
                .addEnchant(enchantments)
                .build();
    }
}
package com.playares.luxe.crate.data;

import com.google.common.collect.Lists;
import com.playares.commons.item.ItemBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Random;

@AllArgsConstructor
public final class Crate {
    @Getter public final String name;
    @Getter public final String displayName;
    @Getter public final String dailyPermission;
    @Getter public final Color fireworkColor;
    @Getter public final List<CrateLoot> loot;

    public ItemStack getItem() {
        return new ItemBuilder()
                .setMaterial(Material.ENDER_CHEST)
                .setName(displayName)
                .addEnchant(Enchantment.DURABILITY, 1)
                .addFlag(ItemFlag.HIDE_ENCHANTS)
                .build();
    }

    public ItemStack getItem(int amount) {
        return new ItemBuilder()
                .setMaterial(Material.ENDER_CHEST)
                .setAmount(amount)
                .setName(displayName)
                .addEnchant(Enchantment.DURABILITY, 1)
                .addFlag(ItemFlag.HIDE_ENCHANTS)
                .build();
    }

    public boolean match(ItemStack item) {
        return item.hasItemMeta() && item.getItemMeta().getDisplayName().equals(displayName) && item.getType().equals(Material.ENDER_CHEST);
    }

    public List<CrateLoot> getItems() {
        final List<CrateLoot> result = Lists.newArrayList();

        for (int i = 0; i < 100; i++) {
            for (CrateLoot item : loot) {
                if (result.size() >= 3) {
                    return result;
                }

                final int roll = (int)Math.round(Math.random() * 100);

                if (roll <= item.getChance()) {
                    result.add(item);
                }
            }
        }

        if (result.isEmpty()) {
            result.add(loot.get(Math.abs(new Random().nextInt(loot.size()))));
        }

        return result;
    }
}
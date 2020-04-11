package com.llewkcor.ares.luxe.crate;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.llewkcor.ares.commons.location.BLocatable;
import com.llewkcor.ares.commons.logger.Logger;
import com.llewkcor.ares.commons.promise.SimplePromise;
import com.llewkcor.ares.commons.remap.RemappedEnchantment;
import com.llewkcor.ares.commons.util.bukkit.Scheduler;
import com.llewkcor.ares.commons.util.bukkit.Worlds;
import com.llewkcor.ares.commons.util.general.Configs;
import com.llewkcor.ares.luxe.crate.data.Crate;
import com.llewkcor.ares.luxe.crate.data.CrateLoot;
import com.llewkcor.ares.luxe.crate.menu.CratePreviewMenu;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang.StringUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.IllegalFormatException;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
public final class CrateHandler {
    @Getter public final CrateManager manager;

    /**
     * Loads all crates to memory
     */
    public void loadCrates() {
        if (!manager.getCrateRepository().isEmpty()) {
            manager.getCrateRepository().clear();
        }

        final YamlConfiguration config = Configs.getConfig(manager.getPlugin(), "crates");

        for (String crateId : config.getConfigurationSection("crates").getKeys(false)) {
            final List<CrateLoot> loot = Lists.newArrayList();
            final String displayName = ChatColor.translateAlternateColorCodes('&', config.getString("crates." + crateId + ".display_name"));
            final String dailyPermission = config.getString("crates." + crateId + ".daily_permission");
            Color fireworkColor = Color.WHITE;

            if (config.get("crates." + crateId + ".firework_color") != null) {
                try {
                    final int r = config.getInt("crates." + crateId + ".firework_color.r");
                    final int g = config.getInt("crates." + crateId + ".firework_color.g");
                    final int b = config.getInt("crates." + crateId + ".firework_color.b");

                    fireworkColor = Color.fromRGB(r, g, b);
                } catch (IllegalFormatException ex) {
                    Logger.error("Invalid firework color for " + crateId + ", using white");
                }
            }

            for (String lootId : config.getConfigurationSection("crates." + crateId + ".loot").getKeys(false)) {
                final String path = "crates." + crateId + ".loot." + lootId;

                Material material = Material.getMaterial(config.getInt(path + ".id"));

                if (material == null) {
                    Logger.error("Invalid material id for " + crateId + ":" + lootId);
                    continue;
                }

                String name = ChatColor.RESET + StringUtils.capitalize(material.name().toLowerCase().replace("_", " "));
                int durability = 0;
                int amount = 1;
                int chance = 0;
                final Map<Enchantment, Integer> enchantments = Maps.newHashMap();

                if (config.get(path + ".name") != null) {
                    name = ChatColor.translateAlternateColorCodes('&', config.getString(path + ".name"));
                }

                if (config.get(path + ".durability") != null) {
                    durability = config.getInt(path + ".durability");
                }

                if (config.get(path + ".amount") != null) {
                    amount = config.getInt(path + ".amount");
                }

                if (config.get(path + ".chance") != null) {
                    chance = config.getInt(path + ".chance");
                }

                if (config.get(path + ".enchantments") != null) {
                    for (String enchantmentName : config.getConfigurationSection(path + ".enchantments").getKeys(false)) {
                        final Enchantment enchantment = RemappedEnchantment.getEnchantmentByName(enchantmentName);
                        final int level = config.getInt(path + ".enchantments." + enchantmentName);

                        if (enchantment == null) {
                            Logger.error("Invalid enchantment for " + crateId + ":" + lootId);
                            continue;
                        }

                        enchantments.put(enchantment, level);
                    }
                }

                loot.add(new CrateLoot(name, material, (short)durability, amount, chance, enchantments));
            }

            final Crate crate = new Crate(crateId, displayName, dailyPermission, fireworkColor, loot);
            manager.getCrateRepository().add(crate);
        }

        Logger.print("Loaded " + manager.getCrateRepository().size() + " Crates");
    }

    /**
     * Gives the provided player a crate
     * @param sender CommandSender
     * @param username Receiving Username
     * @param crateName Crate name
     * @param amountName Amount of crates
     * @param promise Promise
     */
    public void giveCrate(CommandSender sender, String username, String crateName, String amountName, SimplePromise promise) {
        final Player player = Bukkit.getPlayer(username);
        final Crate crate = manager.getCrateByName(crateName);
        final int amount;

        try {
            amount = Integer.parseInt(amountName);
        } catch (NumberFormatException ex) {
            promise.fail("Invalid amount");
            return;
        }

        if (player == null || !player.isOnline()) {
            promise.fail("Player not found");
            return;
        }

        if (crate == null) {
            promise.fail("Crate not found");
            return;
        }

        if (player.getInventory().firstEmpty() == -1) {
            promise.fail(player.getName() + "'s inventory is full");
            return;
        }

        player.getInventory().addItem(crate.getItem(amount));
        player.sendMessage(ChatColor.AQUA + "You received " + ChatColor.RESET + "x" + amount + " " + crate.getDisplayName());

        Logger.print(sender.getName() + " gave " + player.getName() + " " + amount + " " + crate.getDisplayName());

        promise.success();
    }

    /**
     * Handles viewing a crates inventory
     * @param player Player
     * @param crateName Crate
     * @param promise Promise
     */
    public void viewCrate(Player player, String crateName, SimplePromise promise) {
        final Crate crate = manager.getCrateByName(crateName);

        if (crate == null) {
            promise.fail("Crate not found");
            return;
        }

        final CratePreviewMenu menu = new CratePreviewMenu(manager.getPlugin(), player, crate);
        menu.open();
        promise.success();
    }

    /**
     * Handles opening a crate
     * @param player Player
     * @param block Block
     * @param crate Crate
     */
    public void openCrate(Player player, Block block, Crate crate) {
        final List<CrateLoot> loot = crate.getItems();
        final BLocatable locatable = new BLocatable(block);

        manager.getPlacedCrates().add(locatable);

        player.sendMessage(ChatColor.GRAY + "Opening a " + crate.getDisplayName());

        new Scheduler(manager.getPlugin()).sync(() -> {
            final FireworkEffect effect = FireworkEffect.builder().withColor(crate.getFireworkColor()).withFlicker().build();
            Worlds.spawnFirework(manager.getPlugin(), block.getLocation().add(0.5, 0.0, 0.5), 1, 10L, effect);
        }).delay(20L).run();

        new Scheduler(manager.getPlugin()).sync(() -> {
            final FireworkEffect effect = FireworkEffect.builder().withColor(crate.getFireworkColor()).withFlicker().build();
            Worlds.spawnFirework(manager.getPlugin(), block.getLocation().add(0.5, 0.0, 0.5), 2, 10L, effect);
        }).delay(40L).run();

        new Scheduler(manager.getPlugin()).sync(() -> {
            final FireworkEffect effect = FireworkEffect.builder().withColor(crate.getFireworkColor()).withFlicker().build();
            Worlds.spawnFirework(manager.getPlugin(), block.getLocation().add(0.5, 0.0, 0.5), 3, 10L, effect);

            block.breakNaturally(new ItemStack(Material.AIR));

            loot.forEach(item -> {

                block.getWorld().dropItem(block.getLocation().add(0.5, 1.0, 0.5), item.getItemAsDrop());

                if (player.isOnline()) {
                    player.sendMessage(ChatColor.GRAY + "Received " + ChatColor.RESET + "x" + item.getAmount() + " " + item.getName() + ChatColor.GRAY + "!");
                }
            });

            manager.getPlacedCrates().remove(locatable);
        }).delay(60L).run();
    }

    /**
     * Handles listing all crates
     * @param player Viewer
     */
    public void listCrates(Player player) {
        final List<String> names = Lists.newArrayList();

        manager.getCrateRepository().forEach(crate -> names.add(crate.getDisplayName()));

        player.sendMessage(ChatColor.GOLD + "Available Crates (" + ChatColor.YELLOW + names.size() + ChatColor.GOLD + ")");
        player.sendMessage(Joiner.on(ChatColor.RESET + ", ").join(names));
    }
}
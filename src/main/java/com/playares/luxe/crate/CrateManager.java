package com.playares.luxe.crate;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.playares.luxe.LuxeService;
import com.playares.luxe.crate.data.Crate;
import com.playares.luxe.crate.listener.CrateListener;
import com.playares.commons.location.BLocatable;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Set;
import java.util.stream.Collectors;

public final class CrateManager {
    @Getter public final LuxeService luxe;
    @Getter public final CrateHandler handler;
    @Getter public final Set<Crate> crateRepository;
    @Getter public final Set<BLocatable> placedCrates;

    public CrateManager(LuxeService luxe) {
        this.luxe = luxe;
        this.handler = new CrateHandler(this);
        this.crateRepository = Sets.newHashSet();
        this.placedCrates = Sets.newConcurrentHashSet();

        luxe.getOwner().registerListener(new CrateListener(this));
    }

    /**
     * Returns a Crate matching the provided name
     * @param name Crate name
     * @return Crate
     */
    public Crate getCrateByName(String name) {
        return crateRepository.stream().filter(crate -> ChatColor.stripColor(crate.getName()).equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    /**
     * Returns a Crate matching the provided ItemStack
     * @param item ItemStack
     * @return Crate
     */
    public Crate getCrateByItem(ItemStack item) {
        return crateRepository.stream().filter(crate -> crate.match(item)).findFirst().orElse(null);
    }

    /**
     * Returns an Immutable Set containing all crates the provided player has access to
     * @param player Player
     * @return Immutable Set of Crates
     */
    public ImmutableSet<Crate> getCrateByPermission(Player player) {
        return ImmutableSet.copyOf(crateRepository.stream().filter(crate -> crate.getDailyPermission() != null && player.hasPermission(crate.getDailyPermission())).collect(Collectors.toSet()));
    }

    /**
     * Returns true if the provided block is a placed crate block
     * @param block Block
     * @return True if it is a crate
     */
    public boolean isPlacedCrate(Block block) {
        return placedCrates.stream().anyMatch(crate ->
                        crate.getX() == block.getX() &&
                        crate.getY() == block.getY() &&
                        crate.getZ() == block.getZ() &&
                        crate.getWorldName().equalsIgnoreCase(block.getWorld().getName()));
    }
}
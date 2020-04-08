package com.llewkcor.ares.luxe.crate.listener;

import com.llewkcor.ares.luxe.crate.CrateManager;
import com.llewkcor.ares.luxe.crate.data.Crate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

@AllArgsConstructor
public final class CrateListener implements Listener {
    @Getter public final CrateManager manager;

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.isCancelled()) {
            return;
        }

        final Player player = event.getPlayer();
        final ItemStack item = event.getItemInHand();
        final Block block = event.getBlockPlaced();

        if (item == null || block == null || item.getType().equals(Material.AIR) || block.getType().equals(Material.AIR)) {
            return;
        }

        final Crate crate = manager.getCrateByItem(item);

        if (crate == null) {
            return;
        }

        manager.getHandler().openCrate(player, block, crate);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        final Block block = event.getBlock();
        final boolean isPlacedCrate = (block != null && block.getType().equals(Material.ENDER_CHEST) && manager.isPlacedCrate(block));

        if (isPlacedCrate) {
            event.setCancelled(true);
        }
    }
}
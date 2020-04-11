package com.llewkcor.ares.luxe.rewards.menu;

import com.google.common.collect.Lists;
import com.llewkcor.ares.commons.menu.ClickableItem;
import com.llewkcor.ares.commons.menu.Menu;
import com.llewkcor.ares.commons.util.general.Time;
import com.llewkcor.ares.core.player.data.account.AresAccount;
import com.llewkcor.ares.luxe.Luxe;
import com.llewkcor.ares.luxe.crate.data.Crate;
import com.llewkcor.ares.luxe.rewards.data.Claimable;
import com.llewkcor.ares.luxe.rewards.data.ClaimableCrate;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public final class RewardMenu extends Menu {
    @Getter public final Luxe luxe;
    @Getter public final Player viewer;

    public RewardMenu(Luxe plugin, Player player, Player viewer) {
        super(plugin, player, (viewer.getUniqueId().equals(player.getUniqueId()) ? "Your Rewards" : player.getName() + "'s Rewards"), 6);
        this.luxe = plugin;
        this.viewer = viewer;
    }

    @Override
    public void open() {
        update();
        super.open();
    }

    private void update() {
        clear();

        final List<Claimable> rewards = Lists.newArrayList(luxe.getRewardManager().getClaimableByPlayer(player));
        rewards.sort(Comparator.comparing(Claimable::getExpire));
        Collections.reverse(rewards);

        final AresAccount account = luxe.getCore().getPlayerManager().getAccountByBukkitID(player.getUniqueId());
        int pos = 0;

        if (account == null) {
            viewer.closeInventory();
            viewer.sendMessage(ChatColor.RED + "Account not found");
            return;
        }

        for (Claimable reward : rewards) {
            if (pos >= 53) {
                break;
            }

            if (reward instanceof ClaimableCrate) {
                final ClaimableCrate crate = (ClaimableCrate)reward;
                final Crate crateItem = luxe.getCrateManager().getCrateByName(crate.getCrateName());
                final ItemStack icon = crateItem.getItem(crate.getAmount());
                final ItemMeta meta = icon.getItemMeta();

                meta.setLore(Arrays.asList(ChatColor.GREEN + "Click to claim this reward", ChatColor.GRAY + "Expires " + Time.convertToDate(new Date(reward.getExpire()))));
                icon.setItemMeta(meta);

                addItem(new ClickableItem(icon, pos, click -> {
                    if (!account.isSpawned()) {
                        viewer.closeInventory();
                        viewer.sendMessage(ChatColor.RED + "Rewards can not be claimed in spawn");
                        return;
                    }

                    if (player.getInventory().firstEmpty() == -1) {
                        viewer.closeInventory();
                        viewer.sendMessage(ChatColor.RED + "Your inventory is full");
                        return;
                    }

                    luxe.getRewardManager().getHandler().consumeReward(viewer, reward);
                    update();
                }));
            }

            pos += 1;
        }
    }
}

package com.playares.luxe.rewards.menu;

import com.google.common.collect.Lists;
import com.playares.luxe.LuxeService;
import com.playares.luxe.crate.data.Crate;
import com.playares.luxe.rewards.data.Claimable;
import com.playares.luxe.rewards.data.ClaimableCrate;
import com.playares.luxe.rewards.event.PlayerClaimRewardEvent;
import com.playares.commons.menu.ClickableItem;
import com.playares.commons.menu.Menu;
import com.playares.commons.services.account.AccountService;
import com.playares.commons.services.account.data.AresAccount;
import com.playares.commons.util.general.Time;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public final class RewardMenu extends Menu {
    @Getter public final LuxeService luxe;
    @Getter public final Player viewer;

    public RewardMenu(LuxeService luxe, Player player, Player viewer) {
        super(luxe.getOwner(), player, (viewer.getUniqueId().equals(player.getUniqueId()) ? "Your Rewards" : player.getName() + "'s Rewards"), 6);
        this.luxe = luxe;
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
        final AccountService accountService = (AccountService)luxe.getOwner().getService(AccountService.class);

        rewards.sort(Comparator.comparing(Claimable::getExpire));
        Collections.reverse(rewards);

        final AresAccount account = accountService.getAccountByBukkitID(player.getUniqueId());
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
                    final PlayerClaimRewardEvent rewardEvent = new PlayerClaimRewardEvent(player, crate);
                    Bukkit.getPluginManager().callEvent(rewardEvent);

                    if (rewardEvent.isCancelled()) {
                        viewer.closeInventory();
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

package com.llewkcor.ares.luxe.rewards;

import com.google.common.collect.ImmutableList;
import com.llewkcor.ares.commons.logger.Logger;
import com.llewkcor.ares.commons.promise.FailablePromise;
import com.llewkcor.ares.commons.promise.SimplePromise;
import com.llewkcor.ares.commons.util.bukkit.Players;
import com.llewkcor.ares.commons.util.bukkit.Scheduler;
import com.llewkcor.ares.commons.util.general.Time;
import com.llewkcor.ares.core.player.data.account.AresAccount;
import com.llewkcor.ares.luxe.crate.data.Crate;
import com.llewkcor.ares.luxe.rewards.data.Claimable;
import com.llewkcor.ares.luxe.rewards.data.ClaimableCrate;
import com.llewkcor.ares.luxe.rewards.data.ClaimableDAO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@AllArgsConstructor
public final class RewardHandler {
    @Getter public final RewardManager manager;

    /**
     * Handles loading a players rewards from the database to memory
     * @param player Player
     */
    public void load(Player player, SimplePromise promise) {
        new Scheduler(manager.getPlugin()).async(() -> {
            final ImmutableList<Claimable> rewards = ClaimableDAO.getRewards(manager.getPlugin().getCore().getDatabaseInstance(), player.getUniqueId());
            manager.getClaimableRepository().addAll(rewards);

            new Scheduler(manager.getPlugin()).sync(promise::success).run();
        }).run();
    }

    /**
     * Handles saving all Claimable instances from memory to database
     * @param blocking Block the current thread
     */
    public void saveAll(boolean blocking) {
        if (blocking) {
            Logger.warn("Blocking the current thread while saving claimable rewards to database!");

            final List<Claimable> toDelete = manager.getClaimableRepository().stream().filter(Claimable::isExpired).collect(Collectors.toList());
            final List<Claimable> toSave = manager.getClaimableRepository().stream().filter(claim -> !claim.isExpired()).collect(Collectors.toList());

            ClaimableDAO.deleteRewards(manager.getPlugin().getCore().getDatabaseInstance(), toDelete);
            ClaimableDAO.setRewards(manager.getPlugin().getCore().getDatabaseInstance(), toSave);

            Logger.print("Deleted " + toDelete.size() + " Claimable Rewards");
            Logger.print("Saved " + toSave.size() + " Claimable Rewards");

            return;
        }

        new Scheduler(manager.getPlugin()).async(() -> {
            final List<Claimable> toDelete = manager.getClaimableRepository().stream().filter(Claimable::isExpired).collect(Collectors.toList());
            final List<Claimable> toSave = manager.getClaimableRepository().stream().filter(claim -> !claim.isExpired()).collect(Collectors.toList());

            ClaimableDAO.deleteRewards(manager.getPlugin().getCore().getDatabaseInstance(), toDelete);
            ClaimableDAO.setRewards(manager.getPlugin().getCore().getDatabaseInstance(), toSave);

            new Scheduler(manager.getPlugin()).sync(() -> {
                Logger.print("Deleted " + toDelete.size() + " Claimable Rewards");
                Logger.print("Saved " + toSave.size() + " Claimable Rewards");
            }).run();
        }).run();
    }

    /**
     * Handles delivering new daily rewards to a player
     * @param player Player
     */
    public void attemptDailyLoot(Player player) {
        final Set<Crate> toReceive = manager.getPlugin().getCrateManager().getCrateByPermission(player);

        if (toReceive.isEmpty()) {
            return;
        }

        toReceive.forEach(crate -> {
            final ClaimableCrate claimableCrate = new ClaimableCrate(player.getUniqueId(), "1x " + crate.getDisplayName(), 1, crate.getName(), (Time.now() + (manager.getDailyRewardExpireSeconds() * 1000L)));
            manager.getClaimableRepository().add(claimableCrate);
        });

        Logger.print("Delivered " + toReceive.size() + " Daily Rewards to " + player.getName());
    }

    /**
     * Handles deliving a claimable reward to a player
     * @param player Player
     * @param claimable Claimable Reward
     */
    public void consumeReward(Player player, Claimable claimable) {
        manager.getClaimableRepository().remove(claimable);
        new Scheduler(manager.getPlugin()).async(() -> ClaimableDAO.deleteRewards(manager.getPlugin().getCore().getDatabaseInstance(), Collections.singletonList(claimable))).run();

        if (claimable instanceof ClaimableCrate) {
            final ClaimableCrate crate = (ClaimableCrate)claimable;
            final Crate crateItem = manager.getPlugin().getCrateManager().getCrateByName(crate.getCrateName());

            if (crateItem == null) {
                player.sendMessage(ChatColor.RED + "This crate no longer exists");
                Logger.error(player.getName() + " attempted to claim a " + crate.getDescription() + " but the crate does not exist");
                return;
            }

            player.getInventory().addItem(crateItem.getItem(claimable.getAmount()));
        }

        // Load other claimable types here...

        player.sendMessage(ChatColor.AQUA + "Claimed reward: " + ChatColor.YELLOW + claimable.getDescription());

        Players.playSound(player, Sound.NOTE_PLING);
        Players.spawnEffect(player, player.getLocation().add(0, 1.0, 0), Effect.HEART, 20, 3);
    }

    /**
     * Handles giving a reward to a player
     * @param sender Sender
     * @param username Rewarded Username
     * @param rewardType Reward Type
     * @param rewardName Reward Name
     * @param amountName Rewarded Amount
     * @param promise Promise
     */
    public void giveReward(CommandSender sender, String username, String rewardType, String rewardName, String amountName, SimplePromise promise) {
        final int amount;

        try {
            amount = Integer.parseInt(amountName);
        } catch (NumberFormatException ex) {
            promise.fail("Invalid amount");
            return;
        }

        manager.getPlugin().getCore().getPlayerManager().getAccountByUsername(username, new FailablePromise<AresAccount>() {
            @Override
            public void success(AresAccount aresAccount) {
                if (aresAccount == null) {
                    promise.fail("Player not found");
                    return;
                }

                final Player rewarded = Bukkit.getPlayer(aresAccount.getBukkitId());

                if (rewardType.equalsIgnoreCase("crate")) {
                    final Crate crateItem = manager.getPlugin().getCrateManager().getCrateByName(rewardName);
                    final ClaimableCrate crate = new ClaimableCrate(aresAccount.getBukkitId(), "" + amount + "x " + StringUtils.capitalize(crateItem.getName().toLowerCase() + " Crate"), amount, crateItem.getName(), (Time.now() + (manager.getDailyRewardExpireSeconds() * 1000L)));

                    manager.getClaimableRepository().add(crate);
                    new Scheduler(manager.getPlugin()).async(() -> ClaimableDAO.setRewards(manager.getPlugin().getCore().getDatabaseInstance(), Collections.singletonList(crate))).run();

                    if (rewarded != null && rewarded.isOnline()) {
                        rewarded.sendMessage(ChatColor.GREEN + "You received a new reward! Received: " + crate.getDescription());
                        Players.playSound(rewarded, Sound.NOTE_BASS);
                    }

                    Logger.print(sender.getName() + " rewarded " + aresAccount.getUsername() + " (" + aresAccount.getBukkitId().toString() + ") " + crate.getDescription());

                    promise.success();
                    return;
                }

                promise.fail("Invalid reward type");
                // Load more types here...
            }

            @Override
            public void fail(String s) {
                promise.fail(s);
            }
        });
    }
}
package com.llewkcor.ares.luxe.rewards.listener;

import com.llewkcor.ares.commons.logger.Logger;
import com.llewkcor.ares.commons.promise.FailablePromise;
import com.llewkcor.ares.commons.promise.SimplePromise;
import com.llewkcor.ares.commons.util.bukkit.Scheduler;
import com.llewkcor.ares.core.player.data.account.AresAccount;
import com.llewkcor.ares.luxe.rewards.RewardManager;
import com.llewkcor.ares.luxe.rewards.data.Claimable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;

import java.util.Set;

@AllArgsConstructor
public final class RewardListener implements Listener {
    @Getter public final RewardManager manager;

    @EventHandler
    public void onLoginAttempt(PlayerLoginEvent event) {
        final Player player = event.getPlayer();

        if (!event.getResult().equals(PlayerLoginEvent.Result.ALLOWED)) {
            return;
        }

        manager.getPlugin().getCore().getPlayerManager().getAccountByBukkitID(player.getUniqueId(), new FailablePromise<AresAccount>() {
            @Override
            public void success(AresAccount aresAccount) {
                if (aresAccount == null) {
                    Logger.warn("Failed to load daily rewards for " + player.getName() + ": Account not found");
                    return;
                }

                if (manager.isDailyLogin(player)) {
                    manager.getHandler().attemptDailyLoot(player);
                }
            }

            @Override
            public void fail(String s) {
                Logger.warn("Failed to load daily rewards for " + player.getName() + ": " + s);
            }
        });
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        final Set<Claimable> rewards = manager.getClaimableByPlayer(player);

        if (rewards.isEmpty()) {
            return;
        }

        manager.getHandler().load(player, new SimplePromise() {
            @Override
            public void success() {
                new Scheduler(manager.getPlugin()).sync(() -> {
                    if (!player.isOnline()) {
                        return;
                    }

                    player.sendMessage(ChatColor.GREEN + "You have rewards waiting to be claimed! Type /rewards to access your reward inventory");
                }).delay(5 * 20L).run();
            }

            @Override
            public void fail(String s) {}
        });


    }
}

package com.playares.luxe.rewards.listener;

import com.playares.luxe.rewards.RewardManager;
import com.playares.luxe.rewards.data.Claimable;
import com.playares.commons.promise.SimplePromise;
import com.playares.commons.util.bukkit.Scheduler;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Set;

@AllArgsConstructor
public final class RewardListener implements Listener {
    @Getter public final RewardManager manager;

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
                new Scheduler(manager.getLuxe().getOwner()).sync(() -> {
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

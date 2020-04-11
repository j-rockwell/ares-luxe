package com.llewkcor.ares.luxe.rewards;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.llewkcor.ares.commons.util.general.Configs;
import com.llewkcor.ares.commons.util.general.Time;
import com.llewkcor.ares.core.player.data.account.AresAccount;
import com.llewkcor.ares.luxe.Luxe;
import com.llewkcor.ares.luxe.rewards.data.Claimable;
import com.llewkcor.ares.luxe.rewards.listener.RewardListener;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.stream.Collectors;

public final class RewardManager {
    @Getter public final Luxe plugin;
    @Getter public final RewardHandler handler;
    @Getter public final Set<Claimable> claimableRepository;

    @Getter public int dailyRewardExpireSeconds;

    public RewardManager(Luxe plugin) {
        this.plugin = plugin;
        this.handler = new RewardHandler(this);
        this.claimableRepository = Sets.newConcurrentHashSet();

        final YamlConfiguration config = Configs.getConfig(plugin, "crates");

        this.dailyRewardExpireSeconds = config.getInt("settings.daily_drop_expire_seconds");

        Bukkit.getPluginManager().registerEvents(new RewardListener(this), plugin);
    }

    /**
     * Returns true if the provided player is logging in for the first time
     * in the last 24 hours
     * @param player Player
     * @return True if last reward was 24 hours ago
     */
    public boolean isDailyLogin(Player player) {
        final AresAccount account = plugin.getCore().getPlayerManager().getAccountByBukkitID(player.getUniqueId());

        if (account == null) {
            return false;
        }

        return (Time.now() - account.getLastLogin()) >= (43200 * 1000L);
    }

    /**
     * Returns an Immutable Set of Claimables matching the provided Player
     * @param player Bukkit Player
     * @return Immutable Set of Claimables
     */
    public ImmutableSet<Claimable> getClaimableByPlayer(Player player) {
        return ImmutableSet.copyOf(claimableRepository.stream().filter(claim -> claim.getOwnerId().equals(player.getUniqueId())).collect(Collectors.toSet()));
    }
}
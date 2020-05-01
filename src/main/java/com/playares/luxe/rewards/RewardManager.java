package com.playares.luxe.rewards;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.playares.luxe.LuxeService;
import com.playares.luxe.rewards.data.Claimable;
import com.playares.luxe.rewards.listener.RewardListener;
import com.playares.commons.util.general.Configs;
import lombok.Getter;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.stream.Collectors;

public final class RewardManager {
    @Getter public final LuxeService luxe;
    @Getter public final RewardHandler handler;
    @Getter public final Set<Claimable> claimableRepository;

    @Getter public int dailyRewardExpireSeconds;

    public RewardManager(LuxeService luxe) {
        this.luxe = luxe;
        this.handler = new RewardHandler(this);
        this.claimableRepository = Sets.newConcurrentHashSet();

        final YamlConfiguration config = Configs.getConfig(luxe.getOwner(), "crates");
        this.dailyRewardExpireSeconds = config.getInt("settings.daily_drop_expire_seconds");

        luxe.getOwner().registerListener(new RewardListener(this));
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
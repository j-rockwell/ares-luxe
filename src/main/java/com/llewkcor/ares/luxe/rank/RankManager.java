package com.llewkcor.ares.luxe.rank;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.llewkcor.ares.luxe.Luxe;
import com.llewkcor.ares.luxe.rank.data.Rank;
import com.llewkcor.ares.luxe.rank.listener.RankListener;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public final class RankManager {
    @Getter public final Luxe plugin;
    @Getter public final RankHandler handler;
    @Getter public final Set<Rank> rankRepository;

    public RankManager(Luxe plugin) {
        this.plugin = plugin;
        this.handler = new RankHandler(this);
        this.rankRepository = Sets.newHashSet();

        Bukkit.getPluginManager().registerEvents(new RankListener(this), plugin);
    }

    /**
     * Returns a Rank matching the provided name
     * @param name Rank Name
     * @return Rank
     */
    public Rank getRankByName(String name) {
        return rankRepository.stream().filter(rank -> rank.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    /**
     * Returns an Immutable Set of Ranks the provided Player has access to
     * @param player Player
     * @return Immutable Set of Ranks
     */
    public ImmutableSet<Rank> getRanks(Player player) {
        return ImmutableSet.copyOf(rankRepository.stream().filter(rank -> player.hasPermission(rank.getPermission())).collect(Collectors.toSet()));
    }

    /**
     * Returns the highest rank the provided player has access to
     * @param player Player
     * @return Rank
     */
    public Rank getHighestRank(Player player) {
        final List<Rank> ranks = Lists.newArrayList(getRanks(player));

        if (ranks.isEmpty()) {
            return null;
        }

        ranks.sort(Comparator.comparingInt(Rank::getWeight));
        Collections.reverse(ranks);

        return ranks.get(0);
    }

    /**
     * Returns the formatted name of the provided Player
     * @param player Player
     * @return Display Name
     */
    public String formatName(Player player) {
        final Rank rank = getHighestRank(player);

        if (rank == null) {
            return player.getName();
        }

        return rank.getPrefix() + player.getName();
    }
}
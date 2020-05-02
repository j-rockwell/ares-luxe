package com.playares.luxe.rank.listener;

import com.playares.luxe.rank.RankManager;
import com.playares.luxe.rank.data.Rank;
import com.playares.commons.event.ProcessedChatEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scoreboard.Team;

@AllArgsConstructor
public final class RankListener implements Listener {
    @Getter public RankManager manager;

    @EventHandler
    public void onProcessedChat(ProcessedChatEvent event) {
        event.setDisplayName(manager.formatName(event.getPlayer()) + ChatColor.RESET);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        final Rank rank = manager.getHighestRank(player);

        if (manager.getRankScoreboard() == null) {
            return;
        }

        if (rank != null) {
            final Team team = manager.getRankScoreboard().getTeam(rank.getName());
            team.addEntry(player.getName());
        }

        player.setScoreboard(manager.getRankScoreboard());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        final Player player = event.getPlayer();

        if (manager.getRankScoreboard() == null) {
            return;
        }

        manager.getRankScoreboard().getTeams().forEach(team -> {
            if (team.hasEntry(player.getName())) {
                team.removeEntry(player.getName());
            }
        });
    }
}
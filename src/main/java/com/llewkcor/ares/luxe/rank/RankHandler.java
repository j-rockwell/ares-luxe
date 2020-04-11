package com.llewkcor.ares.luxe.rank;

import com.llewkcor.ares.commons.logger.Logger;
import com.llewkcor.ares.commons.util.general.Configs;
import com.llewkcor.ares.luxe.rank.data.Rank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

@AllArgsConstructor
public final class RankHandler {
    @Getter public final RankManager manager;

    /**
     * Handles loading all ranks from file
     */
    public void loadRanks() {
        if (!manager.getRankRepository().isEmpty()) {
            manager.getRankRepository().clear();
        }

        final YamlConfiguration config = Configs.getConfig(manager.getPlugin(), "ranks");

        for (String rankId : config.getConfigurationSection("ranks").getKeys(false)) {
            final String path = "ranks." + rankId + ".";
            final String name = config.getString(path + "name");
            final String displayName = config.getString(path + "display_name");
            final String prefix = config.getString(path + "prefix");
            final String permission = config.getString(path + "permission");
            final int weight = config.getInt(path + "weight");

            if (name == null) {
                Logger.error("Rank name null");
                continue;
            }

            if (displayName == null) {
                Logger.error("Rank display name null");
                continue;
            }

            if (prefix == null) {
                Logger.error("Rank prefix null");
                continue;
            }

            if (permission == null) {
                Logger.error("Rank permission null");
                continue;
            }

            final Rank rank = new Rank(name, ChatColor.translateAlternateColorCodes('&', displayName), ChatColor.translateAlternateColorCodes('&', prefix), permission, weight);

            manager.getRankRepository().add(rank);
        }

        Logger.print("Loaded " + manager.getRankRepository().size() + " Ranks");
    }
}

package com.playares.luxe;

import com.google.common.collect.Lists;
import com.playares.commons.util.general.Configs;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.List;

public final class LuxeConfig {
    @Getter public final LuxeService luxe;

    @Getter public int shoutInterval;
    @Getter public int premiumSlotAccessStart;
    @Getter public String storeLink;
    @Getter public List<String> joinMotd;
    @Getter public List<String> pingMotd;
    @Getter public List<String> tabHeader;
    @Getter public List<String> tabFooter;

    public LuxeConfig(LuxeService luxe) {
        this.luxe = luxe;
    }

    public void load() {
        final YamlConfiguration config = Configs.getConfig(luxe.getOwner(), "luxe");

        this.shoutInterval = config.getInt("premium_features.shout_cooldown");
        this.premiumSlotAccessStart = config.getInt("premium_features.premium_slot_start");
        this.storeLink = config.getString("premium_features.premium_store_link");

        this.tabHeader = Lists.newArrayList();
        this.tabFooter = Lists.newArrayList();
        this.joinMotd = Lists.newArrayList();
        this.pingMotd = Lists.newArrayList();

        for (String line : config.getStringList("tab_display.header")) {
            tabHeader.add(ChatColor.translateAlternateColorCodes('&', line));
        }

        for (String line : config.getStringList("tab_display.footer")) {
            tabFooter.add(ChatColor.translateAlternateColorCodes('&', line));
        }

        for (String line : config.getStringList("motd.server_list")) {
            pingMotd.add(ChatColor.translateAlternateColorCodes('&', line));
        }

        for (String line : config.getStringList("motd.join")) {
            joinMotd.add(ChatColor.translateAlternateColorCodes('&', line));
        }
    }
}

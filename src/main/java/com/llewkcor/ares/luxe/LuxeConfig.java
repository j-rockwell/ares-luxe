package com.llewkcor.ares.luxe;

import com.llewkcor.ares.commons.util.general.Configs;
import lombok.Getter;
import org.bukkit.configuration.file.YamlConfiguration;

public final class LuxeConfig {
    @Getter public final Luxe plugin;

    @Getter public int shoutInterval;
    @Getter public int premiumSlotAccessStart;
    @Getter public String storeLink;

    public LuxeConfig(Luxe plugin) {
        this.plugin = plugin;
    }

    public void load() {
        final YamlConfiguration config = Configs.getConfig(plugin, "config");

        this.shoutInterval = config.getInt("premium_features.shout_cooldown");
        this.premiumSlotAccessStart = config.getInt("premium_features.premium_slot_start");
        this.storeLink = config.getString("premium_features.premium_store_link");
    }
}

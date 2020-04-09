package com.llewkcor.ares.luxe;

import co.aikar.commands.PaperCommandManager;
import com.llewkcor.ares.luxe.command.CrateCommand;
import com.llewkcor.ares.luxe.crate.CrateManager;
import com.llewkcor.ares.luxe.listener.LuxeListener;
import com.llewkcor.ares.luxe.rank.RankManager;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class Luxe extends JavaPlugin {
    @Getter public CrateManager crateManager;
    @Getter public RankManager rankManager;

    @Getter protected LuxeConfig configuration;

    @Getter protected PaperCommandManager commandManager;

    @Override
    public void onEnable() {
        this.configuration = new LuxeConfig(this);
        configuration.load();

        this.commandManager = new PaperCommandManager(this);
        this.crateManager = new CrateManager(this);
        this.rankManager = new RankManager(this);

        crateManager.getHandler().loadCrates();
        rankManager.getHandler().loadRanks();

        commandManager.registerCommand(new CrateCommand(this));

        Bukkit.getPluginManager().registerEvents(new LuxeListener(this), this);
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }
}
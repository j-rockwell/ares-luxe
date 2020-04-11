package com.llewkcor.ares.luxe;

import co.aikar.commands.PaperCommandManager;
import com.llewkcor.ares.core.Ares;
import com.llewkcor.ares.luxe.command.CrateCommand;
import com.llewkcor.ares.luxe.command.LuxeCommand;
import com.llewkcor.ares.luxe.command.RewardCommand;
import com.llewkcor.ares.luxe.crate.CrateManager;
import com.llewkcor.ares.luxe.listener.LuxeListener;
import com.llewkcor.ares.luxe.rank.RankManager;
import com.llewkcor.ares.luxe.rewards.RewardManager;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class Luxe extends JavaPlugin {
    @Getter protected Ares core;

    @Getter public CrateManager crateManager;
    @Getter public RankManager rankManager;
    @Getter public RewardManager rewardManager;

    @Getter protected LuxeConfig configuration;

    @Getter protected PaperCommandManager commandManager;

    @Override
    public void onEnable() {
        this.core = (Ares)Bukkit.getPluginManager().getPlugin("ares-core");

        this.configuration = new LuxeConfig(this);
        configuration.load();

        this.commandManager = new PaperCommandManager(this);
        this.crateManager = new CrateManager(this);
        this.rankManager = new RankManager(this);
        this.rewardManager = new RewardManager(this);

        crateManager.getHandler().loadCrates();
        rankManager.getHandler().loadRanks();

        commandManager.registerCommand(new CrateCommand(this));
        commandManager.registerCommand(new LuxeCommand(this));
        commandManager.registerCommand(new RewardCommand(this));

        Bukkit.getPluginManager().registerEvents(new LuxeListener(this), this);
    }

    @Override
    public void onDisable() {
        rewardManager.getHandler().saveAll(true);
    }
}
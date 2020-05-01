package com.playares.luxe;

import co.aikar.commands.PaperCommandManager;
import com.playares.luxe.command.CrateCommand;
import com.playares.luxe.command.LuxeCommand;
import com.playares.luxe.command.RewardCommand;
import com.playares.luxe.crate.CrateManager;
import com.playares.luxe.listener.LuxeListener;
import com.playares.luxe.rank.RankManager;
import com.playares.luxe.rewards.RewardManager;
import com.playares.commons.AresPlugin;
import com.playares.commons.AresService;
import lombok.Getter;

public final class LuxeService implements AresService {
    @Getter public final AresPlugin owner;
    @Getter public final String databaseName;
    @Getter public final String name = "Luxe";

    @Getter public CrateManager crateManager;
    @Getter public RankManager rankManager;
    @Getter public RewardManager rewardManager;

    @Getter protected LuxeConfig configuration;
    @Getter protected PaperCommandManager commandManager;

    public LuxeService(AresPlugin owner, String databaseName) {
        this.owner = owner;
        this.databaseName = databaseName;
    }

    @Override
    public void start() {
        this.configuration = new LuxeConfig(this);
        configuration.load();

        this.crateManager = new CrateManager(this);
        this.rankManager = new RankManager(this);
        this.rewardManager = new RewardManager(this);

        crateManager.getHandler().loadCrates();
        rankManager.getHandler().loadRanks();
        rankManager.getHandler().setupScoreboard();

        owner.registerCommand(new CrateCommand(this));
        owner.registerCommand(new LuxeCommand(this));
        owner.registerCommand(new RewardCommand(this));

        owner.registerListener(new LuxeListener(this));
    }

    @Override
    public void stop() {
        rewardManager.getHandler().saveAll(true);
    }
}
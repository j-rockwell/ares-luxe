package com.llewkcor.ares.luxe;

import co.aikar.commands.PaperCommandManager;
import com.llewkcor.ares.luxe.command.CrateCommand;
import com.llewkcor.ares.luxe.crate.CrateManager;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

public final class Luxe extends JavaPlugin {
    @Getter public CrateManager crateManager;

    @Getter protected PaperCommandManager commandManager;

    @Override
    public void onEnable() {
        this.commandManager = new PaperCommandManager(this);
        this.crateManager = new CrateManager(this);

        crateManager.getHandler().loadCrates();

        commandManager.registerCommand(new CrateCommand(this));
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }
}
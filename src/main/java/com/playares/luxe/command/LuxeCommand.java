package com.playares.luxe.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import com.playares.luxe.LuxeService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

@CommandAlias("luxe")
@AllArgsConstructor
public final class LuxeCommand extends BaseCommand {
    @Getter public final LuxeService luxe;

    @Subcommand("reload")
    @Description("Reload Luxe Configuration")
    @CommandPermission("luxe.reload")
    public void onReload(CommandSender sender) {
        sender.sendMessage(ChatColor.GRAY + "Reloading luxe.yml...");
        luxe.getConfiguration().load();

        sender.sendMessage(ChatColor.GRAY + "Reloading ranks.yml...");
        luxe.getRankManager().getHandler().loadRanks();

        sender.sendMessage(ChatColor.GRAY + "Reloading crates.yml...");
        luxe.getCrateManager().getHandler().loadCrates();
    }
}

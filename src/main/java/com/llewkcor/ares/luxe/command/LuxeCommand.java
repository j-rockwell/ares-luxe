package com.llewkcor.ares.luxe.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import com.llewkcor.ares.luxe.Luxe;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

@CommandAlias("luxe")
@AllArgsConstructor
public final class LuxeCommand extends BaseCommand {
    @Getter public final Luxe plugin;

    @Subcommand("reload")
    @Description("Reload Luxe Configuration")
    @CommandPermission("luxe.reload")
    public void onReload(CommandSender sender) {
        sender.sendMessage(ChatColor.GRAY + "Reloading config.yml...");
        plugin.getConfiguration().load();

        sender.sendMessage(ChatColor.GRAY + "Reloading ranks.yml...");
        plugin.getRankManager().getHandler().loadRanks();

        sender.sendMessage(ChatColor.GRAY + "Reloading crates.yml...");
        plugin.getCrateManager().getHandler().loadCrates();
    }
}

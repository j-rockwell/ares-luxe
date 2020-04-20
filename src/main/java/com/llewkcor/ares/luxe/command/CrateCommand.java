package com.llewkcor.ares.luxe.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.llewkcor.ares.commons.promise.SimplePromise;
import com.llewkcor.ares.luxe.Luxe;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("crate|lootbox")
@AllArgsConstructor
public final class CrateCommand extends BaseCommand {
    @Getter public final Luxe plugin;

    @Subcommand("list")
    @Description("View a list of all available crate types")
    public void onList(Player player) {
        plugin.getCrateManager().getHandler().listCrates(player);
    }

    @Subcommand("give")
    @CommandPermission("luxe.crate.give")
    @Description("Give a player crates")
    @Syntax("<username> <crate> <amount>")
    public void onGive(CommandSender sender, String username, String crateName, String amountName) {
        plugin.getCrateManager().getHandler().giveCrate(sender, username, crateName, amountName, new SimplePromise() {
            @Override
            public void success() {
                sender.sendMessage(ChatColor.GREEN + "Delivered crates successfully");
            }

            @Override
            public void fail(String s) {
                sender.sendMessage(ChatColor.RED + s);
            }
        });
    }

    @Subcommand("view")
    @Description("View the contents of a crate")
    @Syntax("<crate>")
    public void onView(Player player, String crateName) {
        plugin.getCrateManager().getHandler().viewCrate(player, crateName, new SimplePromise() {
            @Override
            public void success() {}

            @Override
            public void fail(String s) {
                player.sendMessage(ChatColor.RED + s);
            }
        });
    }
}
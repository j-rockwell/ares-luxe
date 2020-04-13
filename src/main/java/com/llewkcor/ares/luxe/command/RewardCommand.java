package com.llewkcor.ares.luxe.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.llewkcor.ares.commons.promise.SimplePromise;
import com.llewkcor.ares.luxe.Luxe;
import com.llewkcor.ares.luxe.rewards.menu.RewardMenu;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@AllArgsConstructor
@CommandAlias("reward|rewards")
public final class RewardCommand extends BaseCommand {
    @Getter public final Luxe plugin;

    @CommandAlias("reward|rewards")
    @Description("View your rewards")
    public void onRewards(Player player, @Optional String username) {
        if (username != null) {
            if (!player.hasPermission("luxe.rewards.view")) {
                player.sendMessage(ChatColor.RED + "You do not have permission to perform this action");
                return;
            }

            final Player viewed = Bukkit.getPlayer(username);

            if (viewed == null) {
                player.sendMessage(ChatColor.RED + "Player not found");
                return;
            }

            final RewardMenu menu = new RewardMenu(plugin, viewed, player);

            return;
        }

        final RewardMenu menu = new RewardMenu(plugin, player, player);
        menu.open();
    }

    @Subcommand("give")
    @Description("Reward a player an item")
    @CommandPermission("luxe.rewards.give")
    @Syntax("<username> <crate/item> <reward name> <amount>")
    public void onGive(CommandSender sender, String username, @Values("crate") String type, String rewardName, String amountName) {
        plugin.getRewardManager().getHandler().giveReward(sender, username, type, rewardName, amountName, new SimplePromise() {
            @Override
            public void success() {
                sender.sendMessage(ChatColor.GREEN + "Reward delivered successfully");
            }

            @Override
            public void fail(String s) {
                sender.sendMessage(ChatColor.RED + s);
            }
        });
    }
}

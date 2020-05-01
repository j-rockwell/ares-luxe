package com.playares.luxe.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.playares.luxe.LuxeService;
import com.playares.luxe.rewards.menu.RewardMenu;
import com.playares.commons.promise.SimplePromise;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@AllArgsConstructor
@CommandAlias("reward|rewards")
public final class RewardCommand extends BaseCommand {
    @Getter public final LuxeService luxe;

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

            final RewardMenu menu = new RewardMenu(luxe, viewed, player);

            return;
        }

        final RewardMenu menu = new RewardMenu(luxe, player, player);
        menu.open();
    }

    @Subcommand("give")
    @Description("Reward a player an item")
    @CommandPermission("luxe.rewards.give")
    @Syntax("<username> <crate/item> <reward name> <amount>")
    public void onGive(CommandSender sender, String username, @Values("crate") String type, String rewardName, String amountName) {
        luxe.getRewardManager().getHandler().giveReward(sender, username, type, rewardName, amountName, new SimplePromise() {
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

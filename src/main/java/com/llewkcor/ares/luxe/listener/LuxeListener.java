package com.llewkcor.ares.luxe.listener;

import com.google.common.base.Joiner;
import com.llewkcor.ares.commons.util.bukkit.Scheduler;
import com.llewkcor.ares.luxe.Luxe;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.server.ServerListPingEvent;

@AllArgsConstructor
public final class LuxeListener implements Listener {
    @Getter public final Luxe plugin;

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
        final Player player = event.getPlayer();

        if (Bukkit.getOnlinePlayers().size() >= plugin.getConfiguration().getPremiumSlotAccessStart() && !player.hasPermission("luxe.slotaccess")) {
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, Joiner.on(ChatColor.RESET + "\n").join(
                    ChatColor.RED + "Server is full",
                    ChatColor.RESET + "",
                    ChatColor.RED + "Purchase a rank to bypass this restriction",
                    ChatColor.AQUA + plugin.getConfiguration().getStoreLink()));
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        new Scheduler(plugin).sync(() -> plugin.getConfiguration().getJoinMotd().forEach(player::sendMessage)).delay(2L).run();
    }

    @EventHandler
    public void onServerPing(ServerListPingEvent event) {
        event.setMotd(Joiner.on("\n").join(plugin.getConfiguration().getPingMotd()));
    }
}
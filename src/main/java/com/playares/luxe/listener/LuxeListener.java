package com.playares.luxe.listener;

import com.comphenix.protocol.ProtocolLibrary;
import com.google.common.base.Joiner;
import com.playares.luxe.LuxeService;
import com.playares.luxe.util.LuxeUtil;
import com.playares.commons.util.bukkit.Scheduler;
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
    @Getter public final LuxeService luxe;

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
        final Player player = event.getPlayer();

        if (Bukkit.getOnlinePlayers().size() >= luxe.getConfiguration().getPremiumSlotAccessStart() && !player.hasPermission("luxe.slotaccess")) {
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, Joiner.on(ChatColor.RESET + "\n").join(
                    ChatColor.RED + "Server is full",
                    ChatColor.RESET + "",
                    ChatColor.RED + "Purchase a rank to bypass this restriction",
                    ChatColor.AQUA + luxe.getConfiguration().getStoreLink()));
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        new Scheduler(luxe.getOwner()).sync(() -> luxe.getConfiguration().getJoinMotd().forEach(player::sendMessage)).delay(2L).run();
    }

    @EventHandler
    public void onServerPing(ServerListPingEvent event) {
        event.setMotd(Joiner.on("\n").join(luxe.getConfiguration().getPingMotd()));
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();

        final String header = Joiner.on(ChatColor.RESET + "\n").join(luxe.getConfiguration().getTabHeader());
        final String footer = Joiner.on(ChatColor.RESET + "\n").join(luxe.getConfiguration().getTabFooter());

        LuxeUtil.sendTab(ProtocolLibrary.getProtocolManager(), player, header, footer);
    }
}
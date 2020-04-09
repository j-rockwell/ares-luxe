package com.llewkcor.ares.luxe.rank.listener;

import com.llewkcor.ares.commons.event.ProcessedChatEvent;
import com.llewkcor.ares.luxe.rank.RankManager;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

@AllArgsConstructor
public final class RankListener implements Listener {
    @Getter public RankManager manager;

    @EventHandler
    public void onProcessedChat(ProcessedChatEvent event) {
        event.setDisplayName(manager.formatName(event.getPlayer()) + ChatColor.RESET);
    }
}
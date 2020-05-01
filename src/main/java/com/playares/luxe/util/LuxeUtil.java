package com.playares.luxe.util;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.playares.commons.logger.Logger;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;

public final class LuxeUtil {
    /**
     * Handles sending a custom Tab Header & Footer to the provided player
     * @param manager ProtocolManager
     * @param player Player
     * @param header Tab Header
     * @param footer Tab Footer
     */
    public static void sendTab(ProtocolManager manager, Player player, String header, String footer) {
        final PacketContainer packet = manager.createPacket(PacketType.Play.Server.PLAYER_LIST_HEADER_FOOTER);

        packet.getChatComponents()
                .write(0, WrappedChatComponent.fromText(header))
                .write(1, WrappedChatComponent.fromText(footer));

        try {
            manager.sendServerPacket(player, packet);
        } catch (InvocationTargetException ex) {
            Logger.error("Failed to send Tab update packet to " + player.getName());
        }
    }
}

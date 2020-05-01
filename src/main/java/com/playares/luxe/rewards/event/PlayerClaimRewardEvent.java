package com.playares.luxe.rewards.event;

import com.playares.luxe.rewards.data.Claimable;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public final class PlayerClaimRewardEvent extends PlayerEvent implements Cancellable {
    @Getter public static final HandlerList handlerList = new HandlerList();

    @Getter public Claimable reward;
    @Getter @Setter public boolean cancelled;

    public PlayerClaimRewardEvent(Player who, Claimable reward) {
        super(who);
        this.reward = reward;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
}

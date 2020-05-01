package com.playares.luxe.rewards.data;

import com.playares.commons.util.general.Time;

import java.util.UUID;

public interface Claimable {
    /**
     * Returns the UUID of the Claimable Object
     * @return UUID
     */
    UUID getUniqueId();

    /**
     * Returns the Owner Bukkit UUID of the Claimable Object
     * @return UUID
     */
    UUID getOwnerId();

    /**
     * Returns the reward description for this Claimable Object
     * @return Description
     */
    String getDescription();

    /**
     * Returns the amount rewarded of this Claimable Object
     * @return Amount
     */
    int getAmount();

    /**
     * Returns the time in milliseconds when this Claimable Object expires
     * @return Expire Time
     */
    long getExpire();

    /**
     * Returns true if this Claimable Object is expired
     * @return True if expired
     */
    default boolean isExpired() {
        return getExpire() <= Time.now();
    }
}

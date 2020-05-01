package com.playares.luxe.rank.data;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public final class Rank {
    @Getter public final String name;
    @Getter public final String displayName;
    @Getter public final String prefix;
    @Getter public final String scoreboardPrefix;
    @Getter public final String permission;
    @Getter public final int weight;
}

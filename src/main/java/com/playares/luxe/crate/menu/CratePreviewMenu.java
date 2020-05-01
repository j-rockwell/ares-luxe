package com.playares.luxe.crate.menu;

import com.google.common.collect.Lists;
import com.playares.luxe.LuxeService;
import com.playares.luxe.crate.data.Crate;
import com.playares.luxe.crate.data.CrateLoot;
import com.playares.commons.menu.ClickableItem;
import com.playares.commons.menu.Menu;
import org.bukkit.entity.Player;

import java.util.Comparator;
import java.util.List;

public final class CratePreviewMenu extends Menu {
    public CratePreviewMenu(LuxeService luxe, Player player, Crate crate) {
        super(luxe.getOwner(), player, crate.getDisplayName(), 3);

        final List<CrateLoot> loot = Lists.newArrayList(crate.getLoot());
        loot.sort(Comparator.comparing(CrateLoot::getName));

        for (int i = 0; i < crate.getLoot().size(); i++) {
            final CrateLoot item = crate.getLoot().get(i);

            addItem(new ClickableItem(item.getItem(), i, click -> {}));
        }
    }
}
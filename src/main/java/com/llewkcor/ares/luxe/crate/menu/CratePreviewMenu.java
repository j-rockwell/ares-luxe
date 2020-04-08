package com.llewkcor.ares.luxe.crate.menu;

import com.google.common.collect.Lists;
import com.llewkcor.ares.commons.menu.ClickableItem;
import com.llewkcor.ares.commons.menu.Menu;
import com.llewkcor.ares.luxe.Luxe;
import com.llewkcor.ares.luxe.crate.data.Crate;
import com.llewkcor.ares.luxe.crate.data.CrateLoot;
import org.bukkit.entity.Player;

import java.util.Comparator;
import java.util.List;

public final class CratePreviewMenu extends Menu {
    public CratePreviewMenu(Luxe plugin, Player player, Crate crate) {
        super(plugin, player, crate.getDisplayName(), 3);

        final List<CrateLoot> loot = Lists.newArrayList(crate.getLoot());
        loot.sort(Comparator.comparing(CrateLoot::getName));

        for (int i = 0; i < crate.getLoot().size(); i++) {
            final CrateLoot item = crate.getLoot().get(i);

            addItem(new ClickableItem(item.getItem(), i, click -> {}));
        }
    }
}
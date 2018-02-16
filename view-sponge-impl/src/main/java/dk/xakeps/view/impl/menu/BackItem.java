package dk.xakeps.view.impl.menu;

import dk.xakeps.view.api.menu.AbstractMenuItem;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;

public class BackItem extends AbstractMenuItem {
    private final SpongeMenuManager menuManager;

    BackItem(ItemStack icon, SpongeMenuManager menuManager) {
        super(icon);
        this.menuManager = menuManager;
    }

    @Override
    public void onClick(Player player) {
        menuManager.openPreviousMenu(player);
    }
}

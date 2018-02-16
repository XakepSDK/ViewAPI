package dk.xakeps.view.api.menu;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;

public interface MenuItem {
    void setIcon(ItemStack icon);
    ItemStack getIcon();

    void onClick(Player player);
}

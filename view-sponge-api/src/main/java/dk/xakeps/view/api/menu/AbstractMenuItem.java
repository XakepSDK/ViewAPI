package dk.xakeps.view.api.menu;

import org.spongepowered.api.item.inventory.ItemStack;

import java.util.Objects;

public abstract class AbstractMenuItem implements MenuItem {
    private ItemStack icon;

    public AbstractMenuItem(ItemStack icon) {
        this.icon = Objects.requireNonNull(icon, "Icon can't be null!");
    }

    @Override
    public void setIcon(ItemStack icon) {
        this.icon = icon;
    }

    @Override
    public ItemStack getIcon() {
        return icon;
    }
}

package dk.xakeps.view.api.menu;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;

import java.util.Optional;

public interface MenuManager {
    Menu createMenu(String id, Text title, int rowsCount);
    Menu createMenu(Player player, String id, Text title, int rowsCount);

    PagedMenu createMenu(String id, Text title, int rowsCount, int pagesCount);
    PagedMenu createMenu(Player player, String id, Text title, int rowsCount, int pagesCount);

    Optional<Menu> getMenuById(String id);
    default Optional<Menu> getMenuById(Player player, String id) {
        return getMenuById(player.getUniqueId().toString() + '-' + id);
    }

    Optional<PagedMenu> getPagedMenuById(String id);
    default Optional<PagedMenu> getPagedMenuById(Player player, String id) {
        return getPagedMenuById(player.getUniqueId().toString() + '-' + id);
    }

    Optional<MenuView> getMenuView(Player player);

    MenuItem getBackwardsItem(ItemStack icon);

    boolean openPreviousMenu(Player player);
}

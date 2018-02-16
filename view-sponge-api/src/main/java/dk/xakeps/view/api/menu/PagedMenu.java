package dk.xakeps.view.api.menu;

import org.spongepowered.api.entity.living.player.Player;

import java.util.Map;
import java.util.Optional;

public interface PagedMenu {
    Optional<Menu> getMenu(int page);
    boolean open(Player player, int page);
    void clear();
    Map<Integer, Menu> getPages();
}

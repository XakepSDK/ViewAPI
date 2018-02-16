package dk.xakeps.view.impl.menu;

import dk.xakeps.view.api.menu.Menu;
import dk.xakeps.view.api.menu.PagedMenu;
import dk.xakeps.view.impl.SpongeView;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.*;

public class SpongePagedMenu implements PagedMenu {
    private final String id;
    private final Map<Integer, Menu> menuPages;

    SpongePagedMenu(SpongeView plugin, String id, Player player, Text title, int rows, int pages) {
        this.id = id;
        this.menuPages = new HashMap<>(pages);
        for (int page = 0; page < pages; page++) {
            SpongeMenu menu = new SpongeMenu(plugin, this, page, id, player, title, rows);
            menuPages.put(page, menu);
        }
    }

    @Override
    public Optional<Menu> getMenu(int page) {
        return Optional.ofNullable(menuPages.get(page));
    }

    @Override
    public boolean open(Player player, int page) {
        return getMenu(page)
                .map(m -> m.open(player))
                .orElse(false);
    }

    @Override
    public void clear() {
        menuPages.values().forEach(Menu::clear);
    }

    @Override
    public Map<Integer, Menu> getPages() {
        return Collections.unmodifiableMap(new HashMap<>(menuPages));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SpongePagedMenu pagedMenu = (SpongePagedMenu) o;
        return Objects.equals(id, pagedMenu.id)
                && Objects.equals(menuPages.size(), pagedMenu.menuPages.size());
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, menuPages.size());
    }
}

package dk.xakeps.view.impl.menu;

import dk.xakeps.view.api.menu.Menu;
import dk.xakeps.view.api.menu.MenuView;
import dk.xakeps.view.api.menu.PagedMenu;

import java.util.Objects;
import java.util.Optional;

public class SpongeMenuView implements MenuView {
    private final MenuView previousView;
    private final PagedMenu pagedMenu;
    private final Menu menu;
    private final int page;

    SpongeMenuView(MenuView previousView, PagedMenu pagedMenu, Menu menu, int page) {
        this.previousView = previousView;
        this.pagedMenu = pagedMenu;
        this.menu = menu;
        this.page = page;
    }

    @Override
    public Optional<MenuView> getPreviousView() {
        return Optional.ofNullable(previousView);
    }

    @Override
    public Optional<PagedMenu> getPagedMenu() {
        return Optional.ofNullable(pagedMenu);
    }

    @Override
    public Menu getMenu() {
        return menu;
    }

    @Override
    public int getPage() {
        return page;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SpongeMenuView that = (SpongeMenuView) o;
        return page == that.page &&
                Objects.equals(previousView, that.previousView) &&
                Objects.equals(pagedMenu, that.pagedMenu) &&
                Objects.equals(menu, that.menu);
    }

    @Override
    public int hashCode() {

        return Objects.hash(previousView, pagedMenu, menu, page);
    }
}

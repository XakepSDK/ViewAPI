package dk.xakeps.view.api.menu;

import java.util.Optional;

public interface MenuView {
    Optional<MenuView> getPreviousView();
    Optional<PagedMenu> getPagedMenu();
    Menu getMenu();
    int getPage();
}

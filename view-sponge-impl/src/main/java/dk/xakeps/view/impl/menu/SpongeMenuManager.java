package dk.xakeps.view.impl.menu;

import com.google.common.collect.Multimaps;
import com.google.common.collect.SetMultimap;
import dk.xakeps.view.api.menu.*;
import dk.xakeps.view.impl.SpongeView;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;

import java.util.*;

public class SpongeMenuManager implements MenuManager {
    private final SpongeView pluign;
    private final Map<String, Menu> menus;
    private final Map<String, PagedMenu> pagedMenus;
    private final Map<UUID, MenuView> menuViews;
    private final SetMultimap<UUID, String> playerMenus;
    private final MenuViewCleaner menuViewCleaner;

    public SpongeMenuManager(SpongeView pluign) {
        this.pluign = pluign;
        this.menus = new HashMap<>();
        this.pagedMenus = new HashMap<>();
        this.menuViews = new HashMap<>();
        this.playerMenus = Multimaps.newSetMultimap(new HashMap<>(), HashSet::new);
        this.menuViewCleaner = new MenuViewCleaner(this);
        Task.builder().execute(this.menuViewCleaner).intervalTicks(1).submit(pluign);
    }

    @Override
    public Menu createMenu(String id, Text title, int rowsCount) {
        SpongeMenu spongeMenu = new SpongeMenu(pluign, null, 0, id, null, title, rowsCount);
        menus.put(id, spongeMenu);
        return spongeMenu;
    }

    @Override
    public Menu createMenu(Player player, String id, Text title, int rowsCount) {
        id = player.getUniqueId().toString() + '-' + id;
        SpongeMenu spongeMenu = new SpongeMenu(pluign, null, 0, id, player, title, rowsCount);
        menus.put(id, spongeMenu);
        playerMenus.put(player.getUniqueId(), id);
        return spongeMenu;
    }

    @Override
    public PagedMenu createMenu(String id, Text title, int rowsCount, int pagesCount) {
        SpongePagedMenu pagedMenu = new SpongePagedMenu(pluign, id, null, title, rowsCount, pagesCount);
        pagedMenus.put(id, pagedMenu);
        return pagedMenu;
    }

    @Override
    public PagedMenu createMenu(Player player, String id, Text title, int rowsCount, int pagesCount) {
        id = player.getUniqueId().toString() + '-' + id;
        SpongePagedMenu pagedMenu = new SpongePagedMenu(pluign, id, player, title, rowsCount, pagesCount);
        pagedMenus.put(id, pagedMenu);
        playerMenus.put(player.getUniqueId(), id);
        return pagedMenu;
    }

    @Override
    public Optional<Menu> getMenuById(String id) {
        return Optional.ofNullable(menus.get(id));
    }

    @Override
    public Optional<PagedMenu> getPagedMenuById(String id) {
        return Optional.ofNullable(pagedMenus.get(id));
    }

    @Override
    public Optional<MenuView> getMenuView(Player player) {
        return Optional.ofNullable(menuViews.get(player.getUniqueId()));
    }

    @Override
    public MenuItem getBackwardsItem(ItemStack icon) {
        return new BackItem(icon, this);
    }

    @Override
    public boolean openPreviousMenu(Player player) {
        Optional<MenuView> menuView = getMenuView(player);
        if (menuView.isPresent()) {
            MenuView view = menuView.get();
            Optional<MenuView> previousView = view.getPreviousView();
            if (previousView.isPresent()) {
                boolean opened = previousView.get().getMenu().open(player);
                if(opened) {
                    setMenuView(player, previousView.get());
                }
                return opened;
            }
        }
        return false;
    }

    public void setMenuView(Player player, MenuView menuView) {
        menuViews.put(player.getUniqueId(), menuView);
    }

    public MenuViewCleaner getMenuViewCleaner() {
        return menuViewCleaner;
    }

    public void clearMenuViews(UUID uuid) {
        menuViews.remove(uuid);
    }

    @Listener
    public void onPlayerQuit(ClientConnectionEvent.Disconnect event, @Root Player player) {
        Set<String> menus = playerMenus.removeAll(player.getUniqueId());
        if(menus != null) {
            menuViews.remove(player.getUniqueId());
            for (String menuId : menus) {
                menus.remove(menuId);
                pagedMenus.remove(menuId);
            }
        }
    }
}

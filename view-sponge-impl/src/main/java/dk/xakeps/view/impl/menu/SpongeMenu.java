package dk.xakeps.view.impl.menu;

import dk.xakeps.view.api.menu.Menu;
import dk.xakeps.view.api.menu.MenuItem;
import dk.xakeps.view.api.menu.MenuView;
import dk.xakeps.view.impl.SpongeView;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.event.item.inventory.InteractInventoryEvent;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.InventoryArchetypes;
import org.spongepowered.api.item.inventory.property.*;
import org.spongepowered.api.item.inventory.query.QueryOperationTypes;
import org.spongepowered.api.item.inventory.transaction.SlotTransaction;
import org.spongepowered.api.text.Text;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

public class SpongeMenu implements Menu {
    private final SpongeMenuManager menuManager;
    private final SpongePagedMenu pagedMenu;
    private final int page;
    private final String id;
    private final Inventory inventory;
    private final Map<SlotIndex, MenuItem> items;
    private final int size;

    SpongeMenu(SpongeView plugin, SpongePagedMenu pagedMenu, int page, String id, Player player, Text title, int rows) {
        this.menuManager = plugin.getMenuManager();
        this.pagedMenu = pagedMenu;
        this.page = page;
        this.id = id;
        Inventory.Builder builder = Inventory.builder()
                .of(InventoryArchetypes.CHEST)
                .property(InventoryTitle.of(title))
                .property(InventoryDimension.of(9, rows))
                .listener(InteractInventoryEvent.class, new CancelListener())
                .listener(ClickInventoryEvent.Primary.class, new ClickListener());
        if(player != null) builder.forCarrier(player);
        this.inventory = builder.build(plugin);
        this.items = new HashMap<>();
        this.size = rows * 9;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setItem(MenuItem item, SlotPos slotPos) {
        SlotIndex slotIndex = posToIndex(slotPos);
        setItem(item, slotIndex);
    }

    @Override
    public void setItem(MenuItem item, SlotIndex slotIndex) {
        items.put(slotIndex, item);
    }

    @Override
    public boolean open(Player player) {
        Optional<MenuView> menuView = menuManager.getMenuView(player);
        boolean opened = player.openInventory(inventory).isPresent();
        if(opened) {
            menuManager.getMenuViewCleaner().removeFromQueue(player);
            menuManager.setMenuView(player, new SpongeMenuView(menuView.orElse(null), pagedMenu, this, page));
        }
        return opened;
    }

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public void syncIcons() {
        inventory.clear();
        items.forEach((slotIndex, menuItem) -> inventory.query(QueryOperationTypes.INVENTORY_PROPERTY.of(slotIndex)).offer(menuItem.getIcon()));
    }

    @Override
    public boolean isSet(SlotIndex index) {
        return items.containsKey(index);
    }

    @Override
    public boolean isSet(SlotPos slotPos) {
        return isSet(posToIndex(slotPos));
    }

    @Override
    public void clear() {
        items.clear();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SpongeMenu that = (SpongeMenu) o;
        return page == that.page &&
                Objects.equals(pagedMenu, that.pagedMenu) &&
                Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {

        return Objects.hash(pagedMenu, page, id);
    }

    private static SlotIndex posToIndex(SlotPos slotPos) {
        return SlotIndex.of(slotPos.getY() * 9 + slotPos.getX());
    }

    private final class CancelListener implements Consumer<InteractInventoryEvent> {

        @Override
        public void accept(InteractInventoryEvent event) {
            event.setCancelled(!(!(event instanceof ClickInventoryEvent.Primary)
                    && (event instanceof InteractInventoryEvent.Open || event instanceof InteractInventoryEvent.Close)));
            if(event instanceof InteractInventoryEvent.Close) {
                Optional<Player> playerOpt = event.getCause().first(Player.class);
                playerOpt.ifPresent(p -> menuManager.getMenuViewCleaner().addToQueue(p));
            }
        }
    }

    private final class ClickListener implements Consumer<ClickInventoryEvent.Primary> {

        @Override
        public void accept(ClickInventoryEvent.Primary event) {
            event.setCancelled(true);
            Optional<Player> playerOpt = event.getCause().first(Player.class);
            if(!playerOpt.isPresent()) return;
            if (event.getTransactions().size() == 1) {
                Player player = playerOpt.get();
                SlotTransaction slotTransaction = event.getTransactions().get(0);
                Optional<SlotIndex> property = slotTransaction.getSlot().getProperty(SlotIndex.class, AbstractInventoryProperty.getDefaultKey(SlotIndex.class));
                if (property.isPresent()) {
                    MenuItem menuItem = items.get(property.get());
                    if(menuItem != null) menuItem.onClick(player);
                }
            }
        }
    }
}

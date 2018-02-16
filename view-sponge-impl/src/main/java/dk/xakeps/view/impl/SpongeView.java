package dk.xakeps.view.impl;

import dk.xakeps.view.api.menu.AbstractMenuItem;
import dk.xakeps.view.api.menu.Menu;
import dk.xakeps.view.api.menu.MenuManager;
import dk.xakeps.view.api.sidebar.SidebarManager;
import dk.xakeps.view.impl.menu.SpongeMenuManager;
import dk.xakeps.view.impl.sidebar.SpongeSidebar;
import dk.xakeps.view.impl.sidebar.SpongeSidebarManager;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.message.MessageChannelEvent;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.property.SlotIndex;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;

@Plugin(id = "view-sponge", name = "View-sponge", description = "Manages sidebars", version = "2.0", authors = "Xakep_SDK")
public class SpongeView {
    private final SpongeSidebarManager sidebarManager;
    private final SpongeMenuManager menuManager;

    public SpongeView() {
        this.sidebarManager = new SpongeSidebarManager();
        this.menuManager = new SpongeMenuManager(this);
    }

    @Listener
    public void onServerStart(GameInitializationEvent event) {
        Sponge.getScheduler().createTaskBuilder().async().intervalTicks(1).execute(new SidebarUpdater(sidebarManager)).submit(this);
        Sponge.getServiceManager().setProvider(this, SidebarManager.class, sidebarManager);
        Sponge.getServiceManager().setProvider(this, MenuManager.class, menuManager);
        Sponge.getEventManager().registerListeners(this, sidebarManager);
        Sponge.getEventManager().registerListeners(this, menuManager);
    }

    @Listener
    public void chat(MessageChannelEvent.Chat event, @Root Player player) {

        Menu menu = menuManager.createMenu("test", Text.of("Test menu"), 6);
        for (int i = 0; i < 54; i++) {
            final int pos = i;
            menu.setItem(new AbstractMenuItem(ItemStack.of(ItemTypes.DIAMOND, 1)) {
                @Override
                public void onClick(Player player) {
                    player.sendMessage(Text.of("Clicked slot=" + pos));
                }
            }, SlotIndex.of(pos));
        }
        menu.syncIcons();
        menu.open(player);
    }

    public SpongeMenuManager getMenuManager() {
        return menuManager;
    }

    private static final class SidebarUpdater implements Runnable {

        private final SpongeSidebarManager sidebarManager;

        private SidebarUpdater(SpongeSidebarManager sidebarManager) {
            this.sidebarManager = sidebarManager;
        }

        @Override
        public void run() {
            for (Player player : Sponge.getServer().getOnlinePlayers()) {
                sidebarManager.getSidebar(player).ifPresent(sidebar -> ((SpongeSidebar) sidebar).update());
            }
        }
    }
}

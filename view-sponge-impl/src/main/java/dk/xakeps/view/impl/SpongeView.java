package dk.xakeps.view.impl;

import dk.xakeps.view.api.menu.MenuManager;
import dk.xakeps.view.api.sidebar.SidebarManager;
import dk.xakeps.view.impl.menu.SpongeMenuManager;
import dk.xakeps.view.impl.sidebar.SpongeSidebar;
import dk.xakeps.view.impl.sidebar.SpongeSidebarManager;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;

import javax.inject.Inject;

@Plugin(id = "view-sponge", name = "View-sponge", description = "Manages sidebars", version = "2.2", authors = "Xakep_SDK")
public class SpongeView {
    private SpongeSidebarManager sidebarManager;
    private SpongeMenuManager menuManager;

    @Inject
    public SpongeView(PluginContainer container) {
        this.sidebarManager = new SpongeSidebarManager();
        this.menuManager = new SpongeMenuManager(container, this);
    }

    @Listener
    public void onServerStart(GameInitializationEvent event) {
        Sponge.getScheduler().createTaskBuilder().async().intervalTicks(1).execute(new SidebarUpdater(sidebarManager)).submit(this);
        Sponge.getServiceManager().setProvider(this, SidebarManager.class, sidebarManager);
        Sponge.getServiceManager().setProvider(this, MenuManager.class, menuManager);
        Sponge.getEventManager().registerListeners(this, sidebarManager);
        Sponge.getEventManager().registerListeners(this, menuManager);
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

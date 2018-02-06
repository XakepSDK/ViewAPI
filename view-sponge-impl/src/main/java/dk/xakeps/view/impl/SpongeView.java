package dk.xakeps.view.impl;

import dk.xakeps.view.api.SidebarManager;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.EventContextKeys;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.plugin.Plugin;

@Plugin(id = "view-sponge", name = "View-sponge", description = "Manages sidebars", version = "1.3", authors = "Xakep_SDK")
public class SpongeView {
    private final SpongeSidebarManager sidebarManager;

    public SpongeView() {
        sidebarManager = new SpongeSidebarManager();
    }

    @Listener
    public void onServerStart(GameStartedServerEvent event) {
        Sponge.getScheduler().createTaskBuilder().async().intervalTicks(1).execute(new Updater(sidebarManager)).submit(this);
        Sponge.getServiceManager().setProvider(this, SidebarManager.class, sidebarManager);
        Sponge.getEventManager().registerListeners(this, sidebarManager);
    }

    private static final class Updater implements Runnable {

        private final SpongeSidebarManager sidebarManager;

        private Updater(SpongeSidebarManager sidebarManager) {
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

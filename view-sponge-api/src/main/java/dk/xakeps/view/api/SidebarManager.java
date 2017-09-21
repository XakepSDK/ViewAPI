package dk.xakeps.view.api;

import org.spongepowered.api.entity.living.player.Player;

import java.util.Optional;

public interface SidebarManager {
    Sidebar createSidebar(Player player);
    Optional<Sidebar> getSidebar(Player player);
    void removeSidebar(Player player);
}

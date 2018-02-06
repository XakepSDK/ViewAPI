package dk.xakeps.view.api;

import org.spongepowered.api.entity.living.player.Player;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.Consumer;

public interface SidebarManager {
    Sidebar createSidebar(Player player);
    Sidebar createSidebar(Player player, @Nullable Consumer<Sidebar> preRegisterListener);
    Optional<Sidebar> getSidebar(Player player);
    void removeSidebar(Player player);
}

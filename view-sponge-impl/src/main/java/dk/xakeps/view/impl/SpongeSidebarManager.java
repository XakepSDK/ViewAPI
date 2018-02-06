package dk.xakeps.view.impl;

import dk.xakeps.view.api.Sidebar;
import dk.xakeps.view.api.SidebarManager;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.scoreboard.Scoreboard;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

public class SpongeSidebarManager implements SidebarManager {
    private final Map<UUID, SpongeSidebar> sidebars;

    SpongeSidebarManager() {
        this.sidebars = new HashMap<>();
    }

    @Override
    public Sidebar createSidebar(Player player) {
        return createSidebar(player, null);
    }

    @Override
    public Sidebar createSidebar(Player player, Consumer<Sidebar> preRegisterListener) {
        UUID uuid = player.getUniqueId();
        if (sidebars.containsKey(uuid)) {
            throw new IllegalStateException("Already initialized!");
        }
        if(!player.isOnline()) {
            throw new IllegalStateException("Player is offline!");
        }
        SpongeSidebar spongeSidebar = new SpongeSidebar(player);
        if(preRegisterListener != null) preRegisterListener.accept(spongeSidebar);
        sidebars.put(uuid, spongeSidebar);
        return spongeSidebar;
    }

    @Override
    public Optional<Sidebar> getSidebar(Player player) {
        if(player.isOnline()) {
            return Optional.ofNullable(sidebars.get(player.getUniqueId()));
        } else {
            sidebars.remove(player.getUniqueId());
            return Optional.empty();
        }
    }

    @Override
    public void removeSidebar(Player player) {
        sidebars.remove(player.getUniqueId());
        player.setScoreboard(Scoreboard.builder().build());
    }

    @Listener
    public void onPlayerDisconnect(ClientConnectionEvent.Disconnect event, @Root Player player) {
        sidebars.remove(player.getUniqueId());
    }
}

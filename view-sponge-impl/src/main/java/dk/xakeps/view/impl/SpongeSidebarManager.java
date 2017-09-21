package dk.xakeps.view.impl;

import dk.xakeps.view.api.Sidebar;
import dk.xakeps.view.api.SidebarManager;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.scoreboard.Scoreboard;

import java.util.Map;
import java.util.Optional;
import java.util.WeakHashMap;

public class SpongeSidebarManager implements SidebarManager {
    private final Map<Player, SpongeSidebar> sidebars;

    public SpongeSidebarManager() {
        this.sidebars = new WeakHashMap<>();
    }

    @Override
    public Sidebar createSidebar(Player player) {
        if (sidebars.containsKey(player)) {
            throw new IllegalStateException("Already initialized!");
        }
        if(!player.isOnline()) {
            throw new IllegalStateException("Player is offline!");
        }
        SpongeSidebar spongeSidebar = new SpongeSidebar(player);
        sidebars.put(player, spongeSidebar);
        return spongeSidebar;
    }

    @Override
    public Optional<Sidebar> getSidebar(Player player) {
        if(player.isOnline()) {
            return Optional.ofNullable(sidebars.get(player));
        } else {
            sidebars.remove(player);
            return Optional.empty();
        }
    }

    @Override
    public void removeSidebar(Player player) {
        player.setScoreboard(Scoreboard.builder().build());
    }
}

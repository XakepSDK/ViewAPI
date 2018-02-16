package dk.xakeps.view.impl.menu;

import org.spongepowered.api.entity.living.player.Player;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class MenuViewCleaner implements Runnable {
    private final SpongeMenuManager menuManager;
    private final Map<UUID, Long> queue;

    MenuViewCleaner(SpongeMenuManager menuManager) {
        this.menuManager = menuManager;
        this.queue = new HashMap<>();
    }

    @Override
    public void run() {
        Iterator<Map.Entry<UUID, Long>> iterator = queue.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<UUID, Long> next = iterator.next();
            if (next.getValue() < System.currentTimeMillis() - 100) {
                menuManager.clearMenuViews(next.getKey());
                iterator.remove();
            }
        }
    }

    public void addToQueue(Player player) {
        queue.put(player.getUniqueId(), System.currentTimeMillis());
    }

    public void removeFromQueue(Player player) {
        queue.remove(player.getUniqueId());
    }
}

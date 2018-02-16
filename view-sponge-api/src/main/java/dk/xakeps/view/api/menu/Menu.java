package dk.xakeps.view.api.menu;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.property.SlotIndex;
import org.spongepowered.api.item.inventory.property.SlotPos;

public interface Menu {
    String getId();

    void setItem(MenuItem item, SlotPos slotPos);

    void setItem(MenuItem item, SlotIndex slotIndex);

    boolean open(Player player);

    int getSize();

    void syncIcons();

    boolean isSet(SlotIndex index);

    boolean isSet(SlotPos slotPos);

    void clear();
}

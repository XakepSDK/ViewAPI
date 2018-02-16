package dk.xakeps.view.api.sidebar;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.scoreboard.objective.displaymode.ObjectiveDisplayMode;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public interface Sidebar {
    void setTitle(SidebarText title);

    void addLine(SidebarText sidebarText);
    void setLine(int line, SidebarText sidebarText);

    SidebarText getEmptyText();

    void clear();

    void setBelowNameText(int score, SidebarText sidebarText, ObjectiveDisplayMode displayMode);
    void clearBelowNameText();

    Player getViewer();
    void setUpdateListener(@Nullable Consumer<Sidebar> updateListener);
}

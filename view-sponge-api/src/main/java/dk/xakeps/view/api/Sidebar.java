package dk.xakeps.view.api;

import org.spongepowered.api.scoreboard.objective.displaymode.ObjectiveDisplayMode;

public interface Sidebar {
    void setTitle(SidebarText title);

    void addLine(SidebarText sidebarText);
    void setLine(int line, SidebarText sidebarText);

    SidebarText getEmptyText();

    void clear();

    void setBelowNameText(int score, SidebarText sidebarText, ObjectiveDisplayMode displayMode);
    void clearBelowNameText();
}

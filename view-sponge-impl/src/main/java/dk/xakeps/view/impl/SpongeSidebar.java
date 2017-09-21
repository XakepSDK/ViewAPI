package dk.xakeps.view.impl;

import dk.xakeps.view.api.Sidebar;
import dk.xakeps.view.api.SidebarText;
import dk.xakeps.view.api.StaticText;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.scoreboard.Score;
import org.spongepowered.api.scoreboard.Scoreboard;
import org.spongepowered.api.scoreboard.critieria.Criteria;
import org.spongepowered.api.scoreboard.displayslot.DisplaySlots;
import org.spongepowered.api.scoreboard.objective.Objective;
import org.spongepowered.api.scoreboard.objective.displaymode.ObjectiveDisplayMode;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class SpongeSidebar implements Sidebar {
    private final Player player;
    private final Scoreboard scoreboard;
    private final Map<Integer, SidebarText> textMap;
    private final Ring<TextColor> emptyLinesRing;

    private Objective sidebar;
    private Objective sidebarBuffer;

    private Objective belowName;
    private int belowNameScore;

    private SidebarText title;
    private SidebarText belowNameText;

    private final Object lock = new Object();

    public SpongeSidebar(Player player) {
        this.player = player;
        this.scoreboard = Scoreboard.builder().build();
        this.textMap = new HashMap<>();

        this.emptyLinesRing = new Ring<>(TextColor.class, 7, new TextColorCycler());

        this.sidebar = Objective.builder()
                .criterion(Criteria.DUMMY)
                .displayName(Text.EMPTY)
                .name("Sidebar")
                .build();
        this.sidebarBuffer = Objective.builder()
                .criterion(Criteria.DUMMY)
                .displayName(Text.EMPTY)
                .name("SidebarBuffer")
                .build();

        this.belowName = Objective.builder()
                .criterion(Criteria.DUMMY)
                .displayName(Text.EMPTY)
                .name("SidebarBelowName")
                .build();

        this.title = new StaticText(Text.EMPTY);

        scoreboard.addObjective(sidebar);
        scoreboard.addObjective(sidebarBuffer);
        scoreboard.addObjective(belowName);

        player.setScoreboard(scoreboard);
    }

    @Override
    public void setTitle(SidebarText title) {
        this.title = title;
    }

    @Override
    public void addLine(SidebarText sidebarText) {
        Optional<Integer> max = textMap.keySet().stream().max(Integer::compareTo);
        int pos = max.orElse(-1) + 1;
        setLine(pos, sidebarText);
    }

    @Override
    public void setLine(int line, SidebarText sidebarText) {
        textMap.put(line, sidebarText);
    }

    @Override
    public SidebarText getEmptyText() {
        TextColor[] textColors = emptyLinesRing.nextRing();
        Text.Builder textBuilder = Text.builder();
        for (TextColor textColor : textColors) {
            if(textColor != null) {
                textBuilder.append(Text.builder().color(textColor).build());
            }
        }
        return new StaticText(textBuilder.build());
    }

    @Override
    public void clear() {
        textMap.clear();
        clearBelowNameText();
    }

    @Override
    public void setBelowNameText(int score, SidebarText sidebarText, ObjectiveDisplayMode displayMode) {
        this.belowNameText = sidebarText;
        this.belowNameScore = score;
        this.belowName.setDisplayMode(displayMode);
        this.scoreboard.updateDisplaySlot(this.belowName, DisplaySlots.BELOW_NAME);
    }

    @Override
    public void clearBelowNameText() {
        this.belowNameText = null;
        for (Score score : this.belowName.getScores().values()) {
            belowName.removeScore(score);
        }
        this.belowName.setDisplayName(Text.EMPTY);
        this.scoreboard.clearSlot(DisplaySlots.BELOW_NAME);
    }

    void update() {
        synchronized (lock) {
            updateBuffer();
            swap();
            updateBuffer();
            if(belowNameText != null) {
                belowName.setDisplayName(belowNameText.getText());
                for (Player player1 : Sponge.getServer().getOnlinePlayers()) {
                    belowName.getOrCreateScore(player1.getTeamRepresentation()).setScore(belowNameScore);
                }
            }
        }
    }

    private void updateBuffer() {
        for (Score score : sidebarBuffer.getScores().values()) {
            sidebarBuffer.removeScore(score);
        }
        for (Map.Entry<Integer, SidebarText> entry : textMap.entrySet()) {
            sidebarBuffer.getOrCreateScore(entry.getValue().getText())
                    .setScore(textMap.size() - entry.getKey());
            sidebarBuffer.setDisplayName(title.getText());
        }
    }

    private void swap() {
        scoreboard.updateDisplaySlot(sidebarBuffer, DisplaySlots.SIDEBAR);

        Objective temp = sidebarBuffer;
        this.sidebarBuffer = sidebar;
        this.sidebar = temp;
    }

}

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
import java.util.UUID;
import java.util.function.Consumer;

public class SpongeSidebar implements Sidebar {
    private final UUID viwerId;
    private final Scoreboard scoreboard;
    private final Map<Integer, SidebarText> textMap;
    private final Ring<TextColor> emptyLinesRing;

    private Objective sidebar;
    private Objective sidebarBuffer;

    private Objective belowName;
    private int belowNameScore;

    private SidebarText title;
    private SidebarText belowNameText;

    private Consumer<Sidebar> consumer;

    private final Object lock = new Object();

    SpongeSidebar(Player player) {
        this.viwerId = player.getUniqueId();
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
        synchronized (lock) {
            Optional<Integer> max = textMap.keySet().stream().max(Integer::compareTo);
            int pos = max.orElse(-1) + 1;
            setLine(pos, sidebarText);
        }
    }

    @Override
    public void setLine(int line, SidebarText sidebarText) {
        synchronized (lock) {
            textMap.put(line, sidebarText);
        }
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
        synchronized (lock) {
            textMap.clear();
            clearBelowNameText();
        }
    }

    @Override
    public void setBelowNameText(int score, SidebarText sidebarText, ObjectiveDisplayMode displayMode) {
        synchronized (lock) {
            this.belowNameText = sidebarText;
            this.belowNameScore = score;
            this.belowName.setDisplayMode(displayMode);
            this.scoreboard.updateDisplaySlot(this.belowName, DisplaySlots.BELOW_NAME);
        }
    }

    @Override
    public void clearBelowNameText() {
        synchronized (lock) {
            this.belowNameText = null;
            for (Score score : this.belowName.getScores().values()) {
                belowName.removeScore(score);
            }
            this.belowName.setDisplayName(Text.EMPTY);
            this.scoreboard.clearSlot(DisplaySlots.BELOW_NAME);
        }
    }

    @Override
    public Player getViewer() {
        return Sponge.getServer().getPlayer(viwerId).orElseThrow(() -> new IllegalStateException("Player is offline!"));
    }

    @Override
    public void registerUpdateListener(Consumer<Sidebar> consumer) {
        this.consumer = consumer;
    }

    void update() {
        if(consumer != null) consumer.accept(this);
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

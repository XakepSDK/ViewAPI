package dk.xakeps.view.impl;

import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

public class TextColorCycler implements Ring.RingCycler<TextColor> {
    private static final TextColor[] COLORS = {
            TextColors.NONE,      TextColors.AQUA,         TextColors.BLACK,
            TextColors.BLUE,      TextColors.DARK_AQUA,    TextColors.DARK_BLUE,
            TextColors.DARK_GRAY, TextColors.DARK_GREEN,   TextColors.DARK_PURPLE,
            TextColors.DARK_RED,  TextColors.GOLD,         TextColors.GRAY,
            TextColors.GREEN,     TextColors.LIGHT_PURPLE, TextColors.RED,
            TextColors.RESET,     TextColors.WHITE,        TextColors.YELLOW
    };
    private static final Map<TextColor, TextColor> NEXT_COLORS = new HashMap<>(COLORS.length);

    static {
        for (int i = 0; i < COLORS.length; i++) {
            if(i == COLORS.length - 1) {
                NEXT_COLORS.put(COLORS[i], COLORS[0]);
            } else {
                NEXT_COLORS.put(COLORS[i], COLORS[i+1]);
            }
        }
    }

    @Override
    public boolean isLast(TextColor textColor) {
        return textColor == TextColors.YELLOW;
    }

    @Nonnull
    @Override
    public TextColor getNext(TextColor textColor) {
        if(textColor == null) return TextColors.NONE;
        return NEXT_COLORS.get(textColor);
    }
}

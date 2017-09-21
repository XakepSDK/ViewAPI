package dk.xakeps.view.api;

import org.spongepowered.api.text.Text;

public class StaticText implements SidebarText {
    private final Text text;

    public StaticText(Text text) {
        this.text = text;
    }

    @Override
    public Text getText() {
        return text;
    }

    @Override
    public void setText(Text text) {
        throw new UnsupportedOperationException("Can't change static text");
    }
}

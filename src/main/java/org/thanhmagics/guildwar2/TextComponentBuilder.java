package org.thanhmagics.guildwar2;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;

public class TextComponentBuilder {

    private final String message;

    private TextComponent description;
    private boolean underline;
    private ClickEvent onClick;

    public TextComponentBuilder(String message) {
        this.message = message;
    }

    public TextComponentBuilder setDescription(String string) {
        this.description = new TextComponent(ChatColor.translateAlternateColorCodes('&',string));
        return this;
    }

    public TextComponentBuilder setUnderline(boolean underline) {
        this.underline = underline;
        return this;
    }

    public TextComponentBuilder setClickEvent(ClickEvent clickEvent) {
        this.onClick = clickEvent;
        return this;
    }

    public TextComponent build() {
        TextComponent text = new TextComponent(message);
        if (description != null) {
            text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[]{description}));
        }
        text.setUnderlined(underline);
        if (onClick != null) {
            text.setClickEvent(onClick);
        }
        return text;
    }
}
package com.github.jarva.velocitycarbondiscord.util;

import net.dv8tion.jda.api.entities.Member;

import java.awt.*;

public class DiscordUtil {
    public static final String COLOR_WHITE = "#" + Integer.toHexString(Color.white.getRGB()).substring(2);

    public static String getHexColor(Member member) {
        Color color = member.getColor();
        if (color == null) color = Color.white;
        return "#" + Integer.toHexString(color.getRGB()).substring(2);
    }
}

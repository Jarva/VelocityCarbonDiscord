package com.github.jarva.velocitycarbondiscord.util;

import io.github.miniplaceholders.api.MiniPlaceholders;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

import static net.kyori.adventure.text.minimessage.MiniMessage.miniMessage;

public class PlaceholderUtil {
    public static String plainText(Component input) {
        return PlainTextComponentSerializer.plainText().serialize(input);
    }
    public static Component resolvePlaceholders(String input) {
        return resolvePlaceholders(input, MiniPlaceholders.getGlobalPlaceholders());
    }
    public static Component resolvePlaceholders(String input, TagResolver expansion) {
        return resolvePlaceholders(input, expansion, MiniPlaceholders.getGlobalPlaceholders());
    }
    public static Component resolvePlaceholders(String input, Audience audience) {
        return resolvePlaceholders(input, MiniPlaceholders.getAudienceGlobalPlaceholders(audience));
    }
    public static Component resolvePlaceholders(String input, TagResolver expansion, Audience audience) {
        return resolvePlaceholders(input, expansion, MiniPlaceholders.getAudienceGlobalPlaceholders(audience));
    }
    public static Component resolvePlaceholders(String input, TagResolver... tagResolvers) {
        return miniMessage().deserialize(input, tagResolvers);
    }
    public static Tag wrapString(String input) {
        return Tag.preProcessParsed(input);
    }
}

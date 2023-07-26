package com.github.jarva.velocitycarbondiscord.config;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

@ConfigSerializable
public class Minecraft {
    public String format;
    @Setting("discord_format")
    public String discordFormat;
    @Setting("username_format")
    public String usernameFormat;
    @Setting("mention_format")
    public String mentionFormat;
    @Setting("attachment_format")
    public String attachmentFormat;
    @Setting("reply_format")
    public String replyFormat;
}

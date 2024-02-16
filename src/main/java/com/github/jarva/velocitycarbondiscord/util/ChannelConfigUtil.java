package com.github.jarva.velocitycarbondiscord.util;

import com.github.jarva.velocitycarbondiscord.config.Config;

public class ChannelConfigUtil {
    private final Config config;
    private final Config.Channel channel;
    public ChannelConfigUtil(Config config, Config.Channel channel) {
        this.config = config;
        this.channel = channel;
    }

    public Boolean shouldBroadcastEvents() {
        return channel.broadcastEvents;
    }

    public String channelId() {
        return channel.discord.channelId != null ? channel.discord.channelId : config.discord.channelId;
    }
    public Boolean showBotMessages() {
        return channel.discord.showBotMessages != null ? channel.discord.showBotMessages : config.discord.showBotMessages;
    }
    public Boolean showAttachmentsIngame() {
        return channel.discord.showAttachmentsIngame != null ? channel.discord.showAttachmentsIngame : config.discord.showAttachmentsIngame;
    }
    public Boolean enableMentions() {
        return channel.discord.enableMentions != null ? channel.discord.enableMentions : config.discord.enableMentions;
    }

    public Boolean enableEveryoneAndHere() {
        return channel.discord.enableEveryoneAndHere != null ? channel.discord.enableEveryoneAndHere : config.discord.enableEveryoneAndHere;
    }

    // Minecraft Formats
    public String attachmentFormat() {
        return channel.minecraft.attachmentFormat != null ? channel.minecraft.attachmentFormat : config.minecraft.attachmentFormat;
    }
    public String discordFormat() {
        return channel.minecraft.discordFormat != null ? channel.minecraft.discordFormat : config.minecraft.discordFormat;
    }
    public String replyFormat() {
        return channel.minecraft.replyFormat != null ? channel.minecraft.replyFormat : config.minecraft.replyFormat;
    }
    public String usernameFormat() {
        return channel.minecraft.usernameFormat != null ? channel.minecraft.usernameFormat : config.minecraft.usernameFormat;
    }
    public String mentionFormat() {
        return channel.minecraft.mentionFormat != null ? channel.minecraft.mentionFormat : config.minecraft.mentionFormat;
    }
    public String format() {
        return channel.minecraft.format != null ? channel.minecraft.format : config.minecraft.format;
    }

    // Webhook Settings
    public Boolean preferWebhook() {
        return channel.discord.preferWebhook != null ? channel.discord.preferWebhook : config.discord.preferWebhook;
    }
    public String webhookUrl() {
        return channel.discord.webhook.url != null ? channel.discord.webhook.url : config.discord.webhook.url;
    }
    public String webhookAvatarUrl() {
        return channel.discord.webhook.avatarUrl != null ? channel.discord.webhook.avatarUrl : config.discord.webhook.avatarUrl;
    }
    public String webhookUsername() {
        return channel.discord.webhook.username != null ? channel.discord.webhook.username : config.discord.webhook.username;
    }
    public String webhookMessage() {
        return channel.discord.webhook.message != null ? channel.discord.webhook.message : config.discord.webhook.message;
    }

    // Discord formats
    public String join() {
        return channel.discord.messages.join != null ? channel.discord.messages.join : config.discord.messages.join;
    }
    public String chat() {
        return channel.discord.messages.chat != null ? channel.discord.messages.chat : config.discord.messages.chat;
    }
    public String leave() {
        return channel.discord.messages.leave != null ? channel.discord.messages.leave : config.discord.messages.leave;
    }
    public String disconnect() {
        return channel.discord.messages.disconnect != null ? channel.discord.messages.disconnect : config.discord.messages.disconnect;
    }
    public String start() {
        return channel.discord.messages.start != null ? channel.discord.messages.start : config.discord.messages.start;
    }
    public String shutdown() {
        return channel.discord.messages.shutdown != null ? channel.discord.messages.shutdown : config.discord.messages.shutdown;
    }
    public String serverSwitch() {
        return channel.discord.messages.serverSwitch != null ? channel.discord.messages.serverSwitch : config.discord.messages.serverSwitch;
    }
    public String advancementDefault() {
        return channel.discord.messages.advancementDefault != null ? channel.discord.messages.advancementDefault : config.discord.messages.advancementDefault;
    }
    public String advancementGoal() {
        return channel.discord.messages.advancementGoal != null ? channel.discord.messages.advancementGoal : config.discord.messages.advancementGoal;
    }
    public String advancementTask() {
        return channel.discord.messages.advancementTask != null ? channel.discord.messages.advancementTask : config.discord.messages.advancementTask;
    }
    public String advancementChallenge() {
        return channel.discord.messages.advancementChallenge != null ? channel.discord.messages.advancementChallenge : config.discord.messages.advancementChallenge;
    }
    public String death() {
        return channel.discord.messages.death != null ? channel.discord.messages.death : config.discord.messages.death;
    }
}

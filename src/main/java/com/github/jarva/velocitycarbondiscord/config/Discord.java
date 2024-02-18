package com.github.jarva.velocitycarbondiscord.config;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

@ConfigSerializable
public class Discord {
    public String token;
    public Messages messages;
    public Webhook webhook;
    @Setting("channel_id")
    public String channelId;
    @Setting("show_bot_messages")
    public Boolean showBotMessages;
    @Setting("show_attachments_ingame")
    public Boolean showAttachmentsIngame;

    @Setting("show_activity")
    public Boolean showActivity;

    @Setting("activity_text")
    public String activityText;

    @Setting("enable_mentions")
    public Boolean enableMentions;

    @Setting("enable_everyone_and_here")
    public Boolean enableEveryoneAndHere;

    @Setting("prefer_webhook")
    public Boolean preferWebhook;

    @ConfigSerializable
    public static class Webhook {
        public String url;
        @Setting("avatar_url")
        public String avatarUrl;
        public String username;
        public String message;
    }

    @ConfigSerializable
    public static class Messages {

        @Setting("chat_message")
        public String chat;
        @Setting("join_message")
        public String join;
        @Setting("leave_message")
        public String leave;
        @Setting("disconnect_message")
        public String disconnect;
        @Setting("shutdown_message")
        public String shutdown;
        @Setting("start_message")
        public String start;
        @Setting("server_switch_message")
        public String serverSwitch;
        @Setting("advancement_default_message")
        public String advancementDefault;
        @Setting("advancement_challenge_message")
        public String advancementChallenge;
        @Setting("advancement_goal_message")
        public String advancementGoal;
        @Setting("advancement_task_message")
        public String advancementTask;
        @Setting("death_message")
        public String death;
    }
}
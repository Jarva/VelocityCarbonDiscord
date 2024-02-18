package com.github.jarva.velocitycarbondiscord.discord;

import cc.unilock.yeplib.api.AdvancementType;
import com.github.jarva.velocitycarbondiscord.VelocityCarbonDiscord;
import com.github.jarva.velocitycarbondiscord.config.Config;
import com.github.jarva.velocitycarbondiscord.util.ChannelConfigUtil;
import com.github.jarva.velocitycarbondiscord.util.DiscordUtil;
import com.github.jarva.velocitycarbondiscord.util.PlaceholderUtil;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.event.proxy.ListenerBoundEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import dev.vankka.mcdiscordreserializer.minecraft.MinecraftSerializer;
import net.draycia.carbon.api.CarbonChatProvider;
import net.draycia.carbon.api.channels.ChatChannel;
import net.draycia.carbon.api.event.CarbonEventSubscription;
import net.draycia.carbon.api.event.events.CarbonChatEvent;
import net.draycia.carbon.api.users.CarbonPlayer;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.text.MessageFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageListener extends ListenerAdapter {
    private final ProxyServer server;
    private final ChannelConfigUtil config;
    private final Key channelName;
    public final CarbonEventSubscription<CarbonChatEvent> subscription;
    public YepListener yep = null;
    private String webhookId = null;
    private IncomingWebhookClient webhookClient = null;
    private static final Pattern WEBHOOK_ID_REGEX = Pattern.compile("^https://discord\\.com/api/webhooks/(\\d+)/.+$");

    public MessageListener(ProxyServer server, Config config, Config.Channel channel) {
        this.server = server;
        this.config = new ChannelConfigUtil(config, channel);
        this.channelName = Key.key(channel.name);

        if (this.config.webhookUrl() != null) {
            try {
                this.webhookClient = WebhookClient.createClient(VelocityCarbonDiscord.getDiscord().getClient(), this.config.webhookUrl());
                final Matcher matcher = WEBHOOK_ID_REGEX.matcher(this.config.webhookUrl());
                this.webhookId = matcher.find() ? matcher.group(1) : null;
            } catch (IllegalArgumentException err) {
                VelocityCarbonDiscord.getLogger().error("Invalid webhook URL for channel {}", this.channelName);
            }
        }

        this.subscription = CarbonChatProvider.carbonChat().eventHandler().subscribe(CarbonChatEvent.class, 0, true, this::onPlayerChat);
        if (VelocityCarbonDiscord.yeplib) {
            this.yep = new YepListener(this);
        }
        VelocityCarbonDiscord.getLogger().info("Created listener for channels {} {}", this.channelName, this.config.channelId());
    }

    public JDA getClient() {
        return VelocityCarbonDiscord.getDiscord().getClient();
    }

    @Override
    public void onMessageReceived(@Nonnull MessageReceivedEvent event) {
        if (!event.isFromType(ChannelType.TEXT)) {
            return;
        }

        TextChannel textChannel = event.getChannel().asTextChannel();
        if (!textChannel.getId().equals(this.config.channelId())) return;

        User author = event.getAuthor();
        if (!this.config.showBotMessages() && author.isBot()) {
            return;
        }

        if (author.getId().equals(getClient().getSelfUser().getId()) || author.getId().equals(this.webhookId)) {
            return;
        }

        Message message = event.getMessage();
        MessageReference ref = message.getMessageReference();

        Member member = message.getMember();
        if (member == null) {
            return;
        }

        Component content = MinecraftSerializer.INSTANCE.serialize(message.getContentDisplay());

        String hex = DiscordUtil.getHexColor(member);
        TagResolver.Builder builder = TagResolver.builder()
                .tag("role_color", PlaceholderUtil.wrapString(hex))
                .tag("username", PlaceholderUtil.wrapString(author.getName()))
                .tag("nickname", PlaceholderUtil.wrapString(member.getEffectiveName()))
                .tag("message", Tag.selfClosingInserting(content));

        Component attachments = Component.empty();
        if (this.config.showAttachmentsIngame()) {
            for (Message.Attachment attachment : message.getAttachments()) {
                TagResolver attachmentExpansion = builder.tag("attachment_url", PlaceholderUtil.wrapString(attachment.getUrl())).build();
                Component chunk = PlaceholderUtil.resolvePlaceholders(this.config.attachmentFormat(), attachmentExpansion);
                attachments = attachments.append(chunk);
            }
        }

        Component attachmentSpacer = message.getContentDisplay().isBlank() ? Component.empty() : Component.space();

        Component reply_chunk = Component.empty();
        if (ref != null) {
            Message refMessage = ref.getMessage() == null ? ref.resolve().complete() : ref.getMessage();
            User refAuthor = refMessage.getAuthor();
            Member refMember = refMessage.getMember();
            if (refMember != null) {
                reply_chunk = PlaceholderUtil.resolvePlaceholders(this.config.replyFormat(), builder
                        .tag("reply_username", PlaceholderUtil.wrapString(refAuthor.getName()))
                        .tag("reply_nickname", PlaceholderUtil.wrapString(refMember.getEffectiveName()))
                        .tag("reply_message", PlaceholderUtil.wrapString(refMessage.getContentDisplay()))
                        .tag("reply_role_color", PlaceholderUtil.wrapString(DiscordUtil.getHexColor(refMember)))
                        .tag("reply_url", PlaceholderUtil.wrapString(refMessage.getJumpUrl()))
                        .build());
            }
        }
        Component discord_chunk = PlaceholderUtil.resolvePlaceholders(this.config.discordFormat(), builder.build());
        Component username_chunk = PlaceholderUtil.resolvePlaceholders(this.config.usernameFormat(), builder.build());

        Component rendered = PlaceholderUtil.resolvePlaceholders(this.config.format(), builder
                .tag("discord_format", Tag.selfClosingInserting(discord_chunk))
                .tag("username_format", Tag.selfClosingInserting(username_chunk))
                .tag("attachments", Tag.selfClosingInserting(attachmentSpacer.append(attachments)))
                .tag("reply_format", Tag.selfClosingInserting(reply_chunk))
                .build()
        );

        sendMessageToMinecraft(rendered);
    }

    public void onPlayerChat(CarbonChatEvent event) {
        if (!event.chatChannel().key().equals(channelName)) return;

        TagResolver resolver = TagResolver.builder()
                .tag("username", PlaceholderUtil.wrapString(event.sender().username()))
                .tag("displayname", Tag.selfClosingInserting(event.sender().displayName()))
                .tag("message", Tag.selfClosingInserting(event.message()))
                .build();

        Component renderedMessage = PlaceholderUtil.resolvePlaceholders(this.config.preferWebhook() ? this.config.webhookMessage() : this.config.chat(), resolver, event.sender());
        if (this.config.enableMentions()) {
            AbstractMap.SimpleEntry<Component, Component> mentions = parseMentions(renderedMessage);
            renderedMessage = mentions.getKey();
            event.message(mentions.getValue());
        }
        if (!this.config.enableEveryoneAndHere()) {
            renderedMessage = parseEveryoneAndHere(renderedMessage);
        }
        renderedMessage = parseXaero(renderedMessage);
        sendMessageToDiscord(renderedMessage, event.sender());
    }

    public void onPlayerAdvancement(Player player, AdvancementType type, String title, String description) {
        CarbonChatProvider.carbonChat().userManager().user(player.getUniqueId()).thenAccept(carbonPlayer -> {
            if (!carbonPlayer.selectedChannel().key().equals(channelName)) return;

            TagResolver resolver = TagResolver.builder()
                    .tag("username", PlaceholderUtil.wrapString(carbonPlayer.username()))
                    .tag("displayname", Tag.selfClosingInserting(carbonPlayer.displayName()))
                    .tag("title", PlaceholderUtil.wrapString(title))
                    .tag("description", PlaceholderUtil.wrapString(description))
                    .build();

            String format = switch (type) {
                case CHALLENGE -> this.config.advancementChallenge();
                case GOAL -> this.config.advancementGoal();
                case TASK -> this.config.advancementTask();
                default -> this.config.advancementDefault();
            };

            Component renderedMessage = PlaceholderUtil.resolvePlaceholders(format, resolver, carbonPlayer);
            sendMessageToDiscord(renderedMessage);
        });
    }

    public void onPlayerDeath(Player player, String message) {
        CarbonChatProvider.carbonChat().userManager().user(player.getUniqueId()).thenAccept(carbonPlayer -> {
            if (!carbonPlayer.selectedChannel().key().equals(channelName)) return;

            TagResolver resolver = TagResolver.builder()
                    .tag("username", PlaceholderUtil.wrapString(carbonPlayer.username()))
                    .tag("displayname", Tag.selfClosingInserting(carbonPlayer.displayName()))
                    .tag("message", PlaceholderUtil.wrapString(message))
                    .build();

            Component renderedMessage = PlaceholderUtil.resolvePlaceholders(this.config.death(), resolver, carbonPlayer);
            sendMessageToDiscord(renderedMessage);
        });
    }

    @Subscribe
    public void onConnect(ServerConnectedEvent event) {
        if (!config.shouldBroadcastEvents()) return;
        String username = event.getPlayer().getUsername();
        String server = event.getServer().getServerInfo().getName();
        Optional<RegisteredServer> previousServer = event.getPreviousServer();

        String message = previousServer.isPresent() ? this.config.serverSwitch() : this.config.join();

        TagResolver.Builder builder = TagResolver.builder()
                .tag("username", PlaceholderUtil.wrapString(username))
                .tag("server", PlaceholderUtil.wrapString(server));
        if (previousServer.isPresent()) {
            builder = builder
                    .tag("previous_server", PlaceholderUtil.wrapString(previousServer.get().getServerInfo().getName()));
        }

        Component renderedMessage = PlaceholderUtil.resolvePlaceholders(message, builder.build(), event.getPlayer());
        sendMessageToDiscord(renderedMessage);
    }

    @Subscribe
    public void onDisconnect(DisconnectEvent event) {
        if (!config.shouldBroadcastEvents()) return;
        if (!event.getLoginStatus().equals(DisconnectEvent.LoginStatus.SUCCESSFUL_LOGIN)) return;

        Player player = event.getPlayer();
        String username = player.getUsername();
        Optional<ServerConnection> server = player.getCurrentServer();

        TagResolver.Builder builder = TagResolver.builder()
                .tag("username", PlaceholderUtil.wrapString(username));
        if (server.isPresent()) {
            builder = builder
                    .tag("server", PlaceholderUtil.wrapString(server.get().getServerInfo().getName()));
        }

        String message = server.isPresent() ? this.config.leave() : this.config.disconnect();

        Component renderedMessage = PlaceholderUtil.resolvePlaceholders(message, builder.build(), event.getPlayer());
        sendMessageToDiscord(renderedMessage);
    }

    @Subscribe
    public void onShutdown(ProxyShutdownEvent event) {
        if (!config.shouldBroadcastEvents()) return;
        Component renderedMessage = PlaceholderUtil.resolvePlaceholders(this.config.shutdown());
        sendMessageToDiscord(renderedMessage);
    }

    @Subscribe
    public void onStart(ListenerBoundEvent event) {
        if (!config.shouldBroadcastEvents()) return;
        Component renderedMessage = PlaceholderUtil.resolvePlaceholders(this.config.start());
        sendMessageToDiscord(renderedMessage);
    }

    private Component parseXaero(Component message) {
        Pattern xaeroPattern = Pattern.compile("xaero-waypoint:([^:]+):[^:]{1,2}:(-?\\d+):(-?\\d+):(-?\\d+)");
        String msg = PlaceholderUtil.plainText(message);
        Matcher match = xaeroPattern.matcher(msg);
        if (match.find()) {
            return Component.text(MessageFormat.format("Waypoint Shared: <{0}> {1} {2} {3}", match.group(1), match.group(2), match.group(3), match.group(4)));
        }
        return message;
    }

    private static final Pattern everyoneAndHerePattern = Pattern.compile("@(?<ping>everyone|here)");
    private Component parseEveryoneAndHere(Component message) {
        return message.replaceText(
                TextReplacementConfig.builder().match(everyoneAndHerePattern).replacement((match, build) -> {
                    return build.content(match.group().replace("@", "@\u200B"));
                }).build()
        );
    }

    private static final Pattern mentionPattern = Pattern.compile("@([a-zA-Z0-9_]{2,16})");
    private AbstractMap.SimpleEntry<Component, Component> parseMentions(Component message) {
        HashMap<String, Component> matches = new HashMap<String, Component>();
        Component discord = message.replaceText(
            TextReplacementConfig.builder().match(mentionPattern).replacement((match, build) -> {
                Optional<Member> member = getClient().getTextChannelById(this.config.channelId()).getGuild().getMembersByEffectiveName(match.group(1), true).stream().findFirst();
                member.ifPresent(value -> {
                    String hex = DiscordUtil.getHexColor(value);
                    TagResolver resolver = TagResolver.builder()
                            .tag("username", PlaceholderUtil.wrapString(value.getUser().getName()))
                            .tag("nickname", PlaceholderUtil.wrapString(value.getEffectiveName()))
                            .tag("role_color", PlaceholderUtil.wrapString(hex))
                            .build();
                    Component mention = PlaceholderUtil.resolvePlaceholders(this.config.mentionFormat(), resolver);
                    matches.put(match.group(), mention);
                });
                return member.map(value -> build.content(value.getAsMention())).orElse(build);
            }).build()
        );
        Component ingame = message.replaceText(
                TextReplacementConfig.builder()
                        .match(mentionPattern)
                        .replacement((match, build) -> matches.containsKey(match.group()) ? matches.get(match.group()) : build).build()
        );
        return new AbstractMap.SimpleEntry<>(discord, ingame);
    }

    private void sendMessageToMinecraft(Component message) {
        ChatChannel channel = CarbonChatProvider.carbonChat().channelRegistry().channel(channelName);
        if (channel == null) return;
        CarbonChatProvider.carbonChat().server().players().stream()
                .filter(player -> channel.equals(player.selectedChannel()))
                .filter(player -> channel.hearingPermitted(player).permitted())
                .forEach(player -> player.sendMessage(message));
        CarbonChatProvider.carbonChat().server().console().sendMessage(message);
    }

    private void sendMessageToDiscord(Component message) {
        sendMessageToDiscord(message, null);
    }

    private void sendMessageToDiscord(Component message, @Nullable CarbonPlayer player) {
        if (!this.config.preferWebhook() || this.config.webhookUsername() == null || this.config.webhookAvatarUrl() == null || this.webhookClient == null || player == null) {
            sendBotMessageToDiscord(message);
        } else {
            sendWebhookToDiscord(message, player);
        }
    }

    private void sendBotMessageToDiscord(Component message) {
        TextChannel textChannel = getClient().getTextChannelById(this.config.channelId());
        if (textChannel != null) {
            textChannel.sendMessage(PlaceholderUtil.plainText(message)).queue();
        } else {
            VelocityCarbonDiscord.getLogger().error("Unable to find channel {}", this.config.channelId());
        }
    }

    private void sendWebhookToDiscord(Component message, CarbonPlayer player) {
        TagResolver resolver = TagResolver.builder()
                .tag("username", PlaceholderUtil.wrapString(player.username()))
                .tag("displayname", Tag.selfClosingInserting(player.displayName()))
                .tag("uuid", PlaceholderUtil.wrapString(player.uuid().toString()))
                .build();

        Component usernameComponent = PlaceholderUtil.resolvePlaceholders(this.config.webhookUsername(), resolver, player);
        Component avatarComponent = PlaceholderUtil.resolvePlaceholders(this.config.webhookAvatarUrl(), resolver, player);
        MessageCreateData webhookMessage = new MessageCreateBuilder()
                .setContent(PlaceholderUtil.plainText(message))
                .build();
        webhookClient.sendMessage(webhookMessage)
                .setAvatarUrl(PlaceholderUtil.plainText(avatarComponent))
                .setUsername(PlaceholderUtil.plainText(usernameComponent))
                .queue();
    }
}

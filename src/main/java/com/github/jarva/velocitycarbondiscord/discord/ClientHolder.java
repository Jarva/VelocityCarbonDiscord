package com.github.jarva.velocitycarbondiscord.discord;

import com.github.jarva.velocitycarbondiscord.VelocityCarbonDiscord;
import com.github.jarva.velocitycarbondiscord.config.Config;
import com.github.jarva.velocitycarbondiscord.util.PlaceholderUtil;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.proxy.ProxyServer;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ClientHolder extends ListenerAdapter {
    private final JDA jda;
    private final List<MessageListener> listeners = new ArrayList<>();
    private final ProxyServer server;
    private final Config config;
    private int lastPlayerCount = -1;

    public ClientHolder(ProxyServer server, Config config) {
        this.server = server;
        this.config = config;

        JDABuilder builder = JDABuilder
                .createDefault(config.discord.token)
                .setChunkingFilter(ChunkingFilter.ALL)
                .enableIntents(GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT)
                .setMemberCachePolicy(MemberCachePolicy.ALL);

        try {
            jda = builder.build();
            VelocityCarbonDiscord.getLogger().info("Created discord client");
        } catch (Exception e) {
            VelocityCarbonDiscord.getLogger().error("Failed to login to discord: " + e);
            throw new RuntimeException("Failed to login to discord: ", e);
        }
    }

    private void updateActivityMessage() {
        if (this.config.discord.showActivity) {
            final int playerCount = this.server.getPlayerCount();

            if (this.lastPlayerCount != playerCount) {
                TagResolver resolver = TagResolver.builder().tag("amount", PlaceholderUtil.wrapString(String.valueOf(playerCount))).build();
                Component activityText = PlaceholderUtil.resolvePlaceholders(this.config.discord.activityText, resolver);
                jda.getPresence()
                        .setActivity(
                                Activity.playing(PlaceholderUtil.plainText(activityText))
                        );
                this.lastPlayerCount = playerCount;
            }
        }
    }

    public void addListeners() {
        if (listeners.size() > 0 || config.channels.size() == 0) return;
        listeners.addAll(config.channels.stream().filter(channel -> channel.enabled).map(channel -> new MessageListener(server, config, channel)).toList());
    }

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        this.updateActivityMessage();
    }

    @Subscribe
    public void onConnect(ServerConnectedEvent event) {
        this.updateActivityMessage();
    }

    @Subscribe
    public void onDisconnect(DisconnectEvent event) {
        this.updateActivityMessage();
    }

    public void shutdown(VelocityCarbonDiscord instance) {
        this.jda.removeEventListener(this);
        for (MessageListener listener : listeners) {
            listener.subscription.dispose();
            this.server.getEventManager().unregisterListener(instance, listener);
            if (VelocityCarbonDiscord.yeplib) {
                this.server.getEventManager().unregisterListener(instance, listener.yep);
            }
            this.jda.removeEventListener(listener);
        }
        listeners.clear();
        this.jda.shutdown();
        try {
            this.jda.awaitShutdown();
            VelocityCarbonDiscord.getLogger().info("Shutdown Discord Connection");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void initialize(VelocityCarbonDiscord instance) {
        addListeners();
        for (MessageListener listener : listeners) {
            this.jda.addEventListener(listener);
            this.server.getEventManager().register(instance, listener);
            if (VelocityCarbonDiscord.yeplib) {
                this.server.getEventManager().register(instance, listener.yep);
            }
        }
        this.jda.addEventListener(this);
    }

    public JDA getClient() {
        return this.jda;
    }
}

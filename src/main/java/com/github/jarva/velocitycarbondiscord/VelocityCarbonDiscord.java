package com.github.jarva.velocitycarbondiscord;

import com.github.jarva.velocitycarbondiscord.commands.ReloadCommand;
import com.github.jarva.velocitycarbondiscord.config.Config;
import com.github.jarva.velocitycarbondiscord.discord.ClientHolder;
import com.google.inject.Inject;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import org.slf4j.Logger;

import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.file.Path;

@Plugin(
        id = "velocitycarbondiscord",
        name = "VelocityCarbonDiscord",
        version = BuildConstants.VERSION,
        authors = {"Jarva"},
        dependencies = {
                @Dependency(id = "carbonchat"),
                @Dependency(id = "miniplaceholders")
        }
)
public class VelocityCarbonDiscord {
    public static boolean yeplib = false;

    private static VelocityCarbonDiscord instance;
    private final ProxyServer server;
    private final @DataDirectory Path dataDirectory;
    @Nullable
    private ClientHolder discord;

    private final Logger logger;
    private boolean initialized = false;

    @Inject
    public VelocityCarbonDiscord(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory) {
        this.server = server;
        this.logger = logger;
        this.dataDirectory = dataDirectory;
        instance = this;

        logger.info("Loading " + BuildConstants.NAME + " v" + BuildConstants.VERSION);

        this.reload();
    }

    public void reload() {
        if (discord != null) this.discord.shutdown(this);
        if (initialized) this.unregisterAll();

        Config config;
        try {
            config = Config.load(dataDirectory);
        } catch (IOException err) {
            err.printStackTrace();
            config = null;
        }

        if (config != null && !Config.firstRun) {
            this.discord = new ClientHolder(this.server, config);
        }

        if (initialized) this.registerAll();

        logger.info("Loaded " + BuildConstants.NAME + " v" + BuildConstants.VERSION);
    }

    @Nullable
    public static ClientHolder getDiscord() {
        return instance.discord;
    }

    public static Logger getLogger() {
        return instance.logger;
    }

    public static VelocityCarbonDiscord getInstance() {
        return instance;
    }

    public static void reloadConfig() {
        instance.reload();
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        yeplib = server.getPluginManager().isLoaded("yeplib");
        this.registerAll();
        this.registerCommands(server.getCommandManager());
        initialized = true;
    }

    private void registerCommands(CommandManager manager) {
        CommandMeta meta = manager.metaBuilder("discordreload")
            .aliases("vdiscordreload")
            .plugin(this)
            .build();

        BrigadierCommand reload = ReloadCommand.createBrigadierCommand(this.server);
        manager.register(meta, reload);
    }

    private void registerAll() {
        if (this.discord != null) {
            this.register(this.discord);
            this.discord.initialize(this);
        }
    }

    private void unregisterAll() {
        if (this.discord != null) this.unregister(discord);
    }

    private void register(Object x) {
        this.server.getEventManager().register(this, x);
    }

    private void unregister(Object x) {
        this.server.getEventManager().unregisterListener(this, x);
    }
}

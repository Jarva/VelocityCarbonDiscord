package com.github.jarva.velocitycarbondiscord.config;

import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Objects;

@ConfigSerializable
public class Config {

    public static boolean firstRun = true;

    public static Config load(Path dir) throws IOException {
        Files.createDirectories(dir);
        Path configFile = dir.resolve("config.yaml");
        Config.firstRun = !Files.exists(configFile);
        if (Config.firstRun) {
            try (InputStream in = Config.class.getResourceAsStream("/config.yaml")) {
                Files.copy(Objects.requireNonNull(in), configFile);
            } catch (IOException e) {
                throw new RuntimeException("Unable to write default configuration file");
            }
        }
        YamlConfigurationLoader loader = YamlConfigurationLoader.builder()
                .nodeStyle(NodeStyle.BLOCK)
                .path(configFile)
                .build();

        CommentedConfigurationNode root = loader.load();
        return root.get(Config.class);
    }

    public Discord discord;
    public Minecraft minecraft;
    public ArrayList<Channel> channels;

    @ConfigSerializable
    public static class Channel {
        public String name;
        public boolean enabled;
        @Setting("broadcast_events")
        public boolean broadcastEvents;
        public Discord discord;
        public Minecraft minecraft;
    }
}

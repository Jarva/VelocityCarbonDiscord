package com.github.jarva.velocitycarbondiscord.commands;

import com.github.jarva.velocitycarbondiscord.VelocityCarbonDiscord;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.ProxyServer;

public class ReloadCommand {
    public static BrigadierCommand createBrigadierCommand(final ProxyServer proxy) {
        LiteralCommandNode<CommandSource> reload = LiteralArgumentBuilder
                .<CommandSource>literal("velocityreload")
                .requires(source -> source.hasPermission("velocitydiscord.reload"))
                .executes(context -> {
                    VelocityCarbonDiscord.reloadConfig();

                    return Command.SINGLE_SUCCESS;
                }).build();
        return new BrigadierCommand(reload);
    }
}

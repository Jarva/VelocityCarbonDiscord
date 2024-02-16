package com.github.jarva.velocitycarbondiscord.discord;

import cc.unilock.yeplib.api.event.YepMessageEvent;
import com.github.jarva.velocitycarbondiscord.VelocityCarbonDiscord;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;

import java.util.List;

public class YepListener {
    // Format: id␞player_username␟adv_title␟adv_description
    private static final List<String> ADVANCEMENT_TYPES = List.of("adv_default", "adv_goal", "adv_task", "adv_challenge");
    // Format: id␞player_username␟death_message
    private static final String DEATH = "death";

    private final MessageListener listener;
    private final ProxyServer server;

    public YepListener(MessageListener listener, ProxyServer server) {
        this.listener = listener;
        this.server = server;
    }

    @Subscribe
    public void onYepMessage(YepMessageEvent event) {
        if (!event.getType().getNamespace().equals("vcd")) return;

        List<String> parameters = event.getParameters();

        if (!(parameters.size() == 2 || parameters.size() == 3)) {
            VelocityCarbonDiscord.getLogger().warn("Invalid Yep message: " + event);
        }

        Player player = this.server.getPlayer(parameters.get(0)).orElse(null);

        if (player == null) {
            VelocityCarbonDiscord.getLogger().warn("Invalid Yep message: " + event);
            VelocityCarbonDiscord.getLogger().warn("Player not connected to proxy!?");
            return;
        }

        String type = event.getType().getName();

        if (parameters.size() == 2) {
            if (DEATH.equals(type)) {
                listener.onPlayerDeath(player, parameters.get(1));
            } else {
                VelocityCarbonDiscord.getLogger().warn("Invalid Yep message: " + event);
            }
        } else if (parameters.size() == 3) {
            if (ADVANCEMENT_TYPES.contains(type)) {
                String title = parameters.get(1);
                String description = parameters.get(2);

                listener.onPlayerAdvancement(player, type, title, description);
            } else {
                VelocityCarbonDiscord.getLogger().warn("Invalid Yep message: " + event);
            }
        }
    }
}

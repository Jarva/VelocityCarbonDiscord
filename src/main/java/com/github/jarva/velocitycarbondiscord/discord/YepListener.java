package com.github.jarva.velocitycarbondiscord.discord;

import cc.unilock.yeplib.api.event.YepAdvancementEvent;
import cc.unilock.yeplib.api.event.YepDeathEvent;
import com.velocitypowered.api.event.Subscribe;

public class YepListener {
    private final MessageListener listener;

    public YepListener(MessageListener listener) {
        this.listener = listener;
    }

    @Subscribe
    public void onYepAdvancement(YepAdvancementEvent event) {
        listener.onPlayerAdvancement(event.getPlayer(), event.getAdvType(), event.getTitle(), event.getDescription());
    }

    @Subscribe
    public void onYepDeath(YepDeathEvent event) {
        listener.onPlayerDeath(event.getPlayer(), event.getMessage());
    }
}

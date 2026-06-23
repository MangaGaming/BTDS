package com.mguhc.listeners;

import com.mguhc.BeatTheDS;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class TestListener implements Listener {

    private final BeatTheDS plugin;

    public TestListener(BeatTheDS plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();
        if (message.equals("/phase")) {
            player.sendMessage(plugin.getPhase());
        }
    }
}

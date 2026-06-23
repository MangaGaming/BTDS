package com.mguhc.listeners.scenario;

import com.mguhc.BeatTheDS;
import com.mguhc.effects.MessageManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

public class DayCycleScenario implements Listener {

    private boolean isDay = true;
    private final BeatTheDS plugin;

    public DayCycleScenario(BeatTheDS plugin) {
        this.plugin = plugin;
    }

    public void startDayNightCycle() {
        int durationTicks = plugin.getGameConfig().getDayCycleDurationMinutes() * 60 * 20;

        new BukkitRunnable() {
            @Override
            public void run() {
                World world = Bukkit.getWorld(plugin.getGameConfig().getWorldName());
                if (world == null) return;

                MessageManager messages = plugin.getMessageManager();

                if (isDay) {
                    world.setTime(0);
                    messages.sendDayStarting(plugin.getDayNumber() + 1);
                    plugin.setDayNumber(plugin.getDayNumber() + 1);
                    plugin.resetShieldCooldowns();
                } else {
                    world.setTime(13000);
                    messages.sendNightFalling();
                }
                isDay = !isDay;
            }
        }.runTaskTimer(plugin, 0, durationTicks);
    }
}

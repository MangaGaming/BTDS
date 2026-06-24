package com.mguhc.effects;

import com.mguhc.BeatTheDS;
import io.puharesource.mc.titlemanager.api.v2.TitleManagerAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class BossBarManager {

    private final BeatTheDS plugin;
    private BukkitRunnable updateTask;

    public BossBarManager(BeatTheDS plugin) {
        this.plugin = plugin;
    }

    public void createSantaHealthBar() {
        if (updateTask != null) {
            updateTask.cancel();
        }

        updateTask = new BukkitRunnable() {
            @Override
            public void run() {
                updateBar();
            }
        };
        updateTask.runTaskTimer(plugin, 0L, 5L);
    }

    private void updateBar() {
    }

    private String createProgressBar(int percentage, int totalBars) {
        int filledBars = (int) ((percentage / 100.0) * totalBars);
        int emptyBars = totalBars - filledBars;

        StringBuilder bar = new StringBuilder("§c");
        for (int i = 0; i < filledBars; i++) {
            bar.append("|");
        }
        bar.append("§7");
        for (int i = 0; i < emptyBars; i++) {
            bar.append("|");
        }
        bar.append(" §f").append(percentage).append("%");

        return bar.toString();
    }

    public void addPlayer(Player player) {
        // TitleManager handles this via action bar
    }

    public void removePlayer(Player player) {
        // TitleManager handles this via action bar
    }

    public void removeAll() {
        // TitleManager handles this via action bar
    }

    public void reset() {
        if (updateTask != null) {
            updateTask.cancel();
            updateTask = null;
        }
    }
}

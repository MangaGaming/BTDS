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
        Player santa = plugin.getSanta();
        if (santa == null || !santa.isOnline()) {
            return;
        }

        double health = santa.getHealth();
        double maxHealth = santa.getMaxHealth();
        int hearts = (int) Math.ceil(health / 2);
        int percentage = (int) Math.max(0, Math.min(100, (health / maxHealth) * 100));

        String bar = createProgressBar(percentage, 20);
        String title = "§c§l❤ Père Noël §7- §f" + hearts + "❤ " + bar;

        TitleManagerAPI api = BeatTheDS.api;
        if (api != null) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                api.sendActionbar(p, title);
            }
        }
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

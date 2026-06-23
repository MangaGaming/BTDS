package com.mguhc.effects;

import com.mguhc.BeatTheDS;
import io.puharesource.mc.titlemanager.api.v2.TitleManagerAPI;
import org.bukkit.*;
import org.bukkit.entity.Player;

public class EffectsManager {

    private final BeatTheDS plugin;

    public EffectsManager(BeatTheDS plugin) {
        this.plugin = plugin;
    }

    public void sendTitle(Player player, String title, String subtitle) {
        TitleManagerAPI api = BeatTheDS.api;
        if (api != null) {
            api.sendTitle(player, title, 10, 40, 10);
        }
    }

    public void sendTitle(String title, String subtitle) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            sendTitle(p, title, subtitle);
        }
    }

    public void sendActionBar(Player player, String message) {
        TitleManagerAPI api = BeatTheDS.api;
        if (api != null) {
            api.sendActionbar(player, message);
        }
    }

    public void sendActionBar(String message) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            sendActionBar(p, message);
        }
    }

    public void playSound(Player player, Sound sound, float volume, float pitch) {
        player.playSound(player.getLocation(), sound, volume, pitch);
    }

    public void playSound(Sound sound, float volume, float pitch) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.playSound(p.getLocation(), sound, volume, pitch);
        }
    }

    public void playSoundToAll(Sound sound) {
        playSound(sound, 1.0f, 1.0f);
    }

    private void playEffect(Location loc, Effect effect) {
        loc.getWorld().spigot().playEffect(loc, effect);
    }

    public void spawnTeleportEffect(Player player) {
        Location loc = player.getLocation();
        for (int i = 0; i < 20; i++) {
            double x = loc.getX() + (Math.random() - 0.5) * 2;
            double y = loc.getY() + Math.random() * 2;
            double z = loc.getZ() + (Math.random() - 0.5) * 2;
            playEffect(new Location(player.getWorld(), x, y, z), Effect.ENDER_SIGNAL);
        }
        playSound(player, Sound.ENDERMAN_TELEPORT, 1.0f, 1.0f);
    }

    public void spawnDeathEffect(Player player) {
        Location loc = player.getLocation();
        for (int i = 0; i < 15; i++) {
            double x = loc.getX() + (Math.random() - 0.5) * 2;
            double y = loc.getY() + Math.random() * 2;
            double z = loc.getZ() + (Math.random() - 0.5) * 2;
            playEffect(new Location(player.getWorld(), x, y, z), Effect.SMOKE);
        }
        playSound(player, Sound.WITHER_DEATH, 1.0f, 1.0f);
    }

    public void spawnKillEffect(Player killer) {
        Location loc = killer.getLocation();
        for (int i = 0; i < 10; i++) {
            double x = loc.getX() + (Math.random() - 0.5) * 2;
            double y = loc.getY() + 2 + Math.random();
            double z = loc.getZ() + (Math.random() - 0.5) * 2;
            playEffect(new Location(killer.getWorld(), x, y, z), Effect.HAPPY_VILLAGER);
        }
        playSound(killer, Sound.LEVEL_UP, 1.0f, 1.2f);
    }

    public void spawnShieldActivateEffect(Player player) {
        Location loc = player.getLocation();
        for (int i = 0; i < 20; i++) {
            double x = loc.getX() + (Math.random() - 0.5) * 2;
            double y = loc.getY() + Math.random() * 2;
            double z = loc.getZ() + (Math.random() - 0.5) * 2;
            playEffect(new Location(player.getWorld(), x, y, z), Effect.MOBSPAWNER_FLAMES);
        }
        playSound(player, Sound.EXPLODE, 1.0f, 1.0f);
    }

    public void spawnDashEffect(Player player) {
        Location loc = player.getLocation();
        for (int i = 0; i < 15; i++) {
            double x = loc.getX() + (Math.random() - 0.5) * 1.5;
            double y = loc.getY() + Math.random();
            double z = loc.getZ() + (Math.random() - 0.5) * 1.5;
            playEffect(new Location(player.getWorld(), x, y, z), Effect.CLOUD);
        }
        playSound(player, Sound.GHAST_SCREAM, 1.0f, 1.5f);
    }

    public void spawnDoubleDamageEffect(Player player) {
        Location loc = player.getLocation();
        for (int i = 0; i < 15; i++) {
            double x = loc.getX() + (Math.random() - 0.5) * 2;
            double y = loc.getY() + 1 + Math.random();
            double z = loc.getZ() + (Math.random() - 0.5) * 2;
            playEffect(new Location(player.getWorld(), x, y, z), Effect.CRIT);
        }
        playSound(player, Sound.ZOMBIE_METAL, 1.0f, 0.8f);
    }

    public void spawnSanctuaryCaptureEffect(Location loc) {
        if (loc.getWorld() == null) return;
        for (int i = 0; i < 30; i++) {
            double x = loc.getX() + (Math.random() - 0.5) * 3;
            double y = loc.getY() + 2 + Math.random() * 2;
            double z = loc.getZ() + (Math.random() - 0.5) * 3;
            playEffect(new Location(loc.getWorld(), x, y, z), Effect.EXPLOSION_LARGE);
        }
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.playSound(loc, Sound.ENDERDRAGON_GROWL, 0.5f, 1.2f);
        }
    }

    public void spawnVictoryFireworks(Player player) {
        Location loc = player.getLocation();
        for (int i = 0; i < 5; i++) {
            new org.bukkit.scheduler.BukkitRunnable() {
                @Override
                public void run() {
                    double x = loc.getX() + (Math.random() - 0.5) * 10;
                    double z = loc.getZ() + (Math.random() - 0.5) * 10;
                    Location fireworkLoc = new Location(loc.getWorld(), x, loc.getY() + 5, z);
                    for (int j = 0; j < 20; j++) {
                        double fx = fireworkLoc.getX() + (Math.random() - 0.5) * 2;
                        double fy = fireworkLoc.getY() + Math.random() * 2;
                        double fz = fireworkLoc.getZ() + (Math.random() - 0.5) * 2;
                        playEffect(new Location(loc.getWorld(), fx, fy, fz), Effect.FIREWORKS_SPARK);
                    }
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        p.playSound(fireworkLoc, Sound.FIREWORK_LAUNCH, 1.0f, 1.0f);
                    }
                }
            }.runTaskLater(plugin, i * 10L);
        }
    }

    public void spawnParticles(Player player, Effect effect, int count) {
        Location loc = player.getLocation();
        for (int i = 0; i < count; i++) {
            double x = loc.getX() + (Math.random() - 0.5) * 2;
            double y = loc.getY() + 1 + Math.random();
            double z = loc.getZ() + (Math.random() - 0.5) * 2;
            playEffect(new Location(player.getWorld(), x, y, z), effect);
        }
    }
}

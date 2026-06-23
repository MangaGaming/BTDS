package com.mguhc.listeners;

import com.mguhc.BeatTheDS;
import com.mguhc.GameState;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class LutinPurificateurResurrection implements Listener {

    public static final Set<UUID> pendingResurrections = new HashSet<>();

    private final BeatTheDS plugin;
    private final Map<UUID, Integer> resurrectionCount = new HashMap<>();
    private final Map<UUID, Collection<PotionEffect>> savedEffects = new HashMap<>();

    public LutinPurificateurResurrection(BeatTheDS plugin) {
        this.plugin = plugin;
    }

    public void reset() {
        resurrectionCount.clear();
        savedEffects.clear();
        pendingResurrections.clear();
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Player killer = victim.getKiller();

        if (killer == null || !plugin.isSanta(killer)) return;
        if (!plugin.isLutinPurificateur(victim)) return;
        if (!plugin.getGameState().isPlaying()) return;

        UUID uuid = victim.getUniqueId();
        int resurrections = resurrectionCount.getOrDefault(uuid, 0);
        int maxResurrections = plugin.getGameConfig().getLutinPuriMaxResurrections();

        if (resurrections >= maxResurrections) {
            victim.sendMessage(ChatColor.RED + "Tu as atteint ta dernière vie ! Mort définitive.");
            return;
        }

        savedEffects.put(uuid, new ArrayList<>(victim.getActivePotionEffects()));

        event.setDeathMessage(null);
        event.getDrops().clear();
        event.setKeepInventory(true);
        event.setKeepLevel(true);

        resurrectionCount.put(uuid, resurrections + 1);
        pendingResurrections.add(uuid);

        double maxHealth = plugin.getGameConfig().getLutinPuriMaxHealth();
        double healthLoss = plugin.getGameConfig().getLutinPuriHealthLossPerResurrection();
        double newMaxHealth = maxHealth - (healthLoss * (resurrections + 1));

        plugin.getEffectsManager().spawnParticles(victim, org.bukkit.Effect.MOBSPAWNER_FLAMES, 30);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (victim.isOnline()) {
                    victim.spigot().respawn();

                    victim.setMaxHealth(newMaxHealth);
                    victim.setHealth(newMaxHealth);

                    Collection<PotionEffect> effects = savedEffects.remove(uuid);
                    if (effects != null) {
                        for (PotionEffect effect : effects) {
                            if (effect.getDuration() > 0) {
                                victim.addPotionEffect(effect);
                            }
                        }
                    }

                    int currentHearts = (int) (newMaxHealth / 2);
                    int remaining = maxResurrections - resurrections;
                    victim.sendMessage(ChatColor.LIGHT_PURPLE + "Tu ressuscites avec " + currentHearts + " cœurs !");
                    victim.sendMessage(ChatColor.GRAY + "Vies restantes : " + remaining);

                    plugin.getEffectsManager().playSound(victim, org.bukkit.Sound.EXPLODE, 1.0f, 1.0f);
                }
            }
        }.runTaskLater(plugin, 1L);
    }
}

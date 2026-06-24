package com.mguhc.listeners;

import com.mguhc.BeatTheDS;
import com.mguhc.GameState;
import com.mguhc.effects.EffectsManager;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DoubleDamageListener implements Listener {

    private final BeatTheDS plugin;
    private final Map<UUID, Integer> hitCount = new HashMap<>();

    public DoubleDamageListener(BeatTheDS plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerHit(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) return;
        if (!(event.getEntity() instanceof org.bukkit.entity.LivingEntity)) return;

        Player player = (Player) event.getDamager();

        if (!plugin.isLutinNormal(player)) return;
        if (!player.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE)) return;

        UUID uuid = player.getUniqueId();
        int count = hitCount.getOrDefault(uuid, 0) + 1;
        hitCount.put(uuid, count);

        int displayCount = (count - 1) % 20 + 1;

        plugin.getEffectsManager().setActionBar(player,
                "§aCoup : §f" + displayCount + "§a/20",
                EffectsManager.ActionBarPriority.LOW);

        if (count % 20 == 0) {
            event.setDamage(event.getDamage() * 2.0);
            plugin.getEffectsManager().spawnDoubleDamageEffect(player);
            player.sendMessage("§aCoup critique spécial ! Dégâts doublés !");
        }
    }

    public void reset() {
        hitCount.clear();
    }
}

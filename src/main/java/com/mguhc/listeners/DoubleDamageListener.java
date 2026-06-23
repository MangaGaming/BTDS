package com.mguhc.listeners;

import com.mguhc.BeatTheDS;
import com.mguhc.GameState;
import io.puharesource.mc.titlemanager.api.v2.TitleManagerAPI;
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

        TitleManagerAPI api = BeatTheDS.api;
        if (api != null) {
            api.sendActionbar(player, "§aCoup : §f" + displayCount + "§a/20");
        }

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

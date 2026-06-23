package com.mguhc.listeners;

import com.mguhc.BeatTheDS;
import com.mguhc.GameState;
import com.mguhc.effects.EffectsManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DashListener implements Listener {

    private final BeatTheDS plugin;
    private final Map<UUID, Long> cooldowns = new HashMap<>();
    private static final String ITEM_NAME = ChatColor.DARK_AQUA + "Dash";

    public DashListener(BeatTheDS plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Player player = event.getPlayer();
        ItemStack handItem = player.getInventory().getItemInHand();

        if (handItem == null || handItem.getType() == Material.AIR) return;

        ItemMeta meta = handItem.getItemMeta();
        if (meta == null || !meta.hasDisplayName()) return;
        if (!meta.getDisplayName().equals(ITEM_NAME)) return;

        if (!player.hasPotionEffect(PotionEffectType.SPEED)) return;
        if (!plugin.isLutinNormal(player)) {
            player.sendMessage("§cSeuls les Lutins peuvent utiliser le Dash !");
            event.setCancelled(true);
            return;
        }

        UUID uuid = player.getUniqueId();
        long now = System.currentTimeMillis();
        long cooldownMillis = plugin.getGameConfig().getDashCooldownMinutes() * 60 * 1000L;
        long end = cooldowns.getOrDefault(uuid, 0L);

        if (now < end) {
            long sec = (end - now) / 1000;
            player.sendMessage("§cDash indisponible encore " + sec + " secondes.");
            event.setCancelled(true);
            return;
        }

        Vector dir = player.getLocation().getDirection().normalize().multiply(plugin.getGameConfig().getDashPower());
        dir.setY(plugin.getGameConfig().getDashYBoost());
        player.setVelocity(dir);

        plugin.getEffectsManager().spawnDashEffect(player);
        player.sendMessage("§aDash utilisé ! Prochain dash dans " + plugin.getGameConfig().getDashCooldownMinutes() + " minutes.");

        cooldowns.put(uuid, now + cooldownMillis);
        event.setCancelled(true);
    }

    public void reset() {
        cooldowns.clear();
    }
}

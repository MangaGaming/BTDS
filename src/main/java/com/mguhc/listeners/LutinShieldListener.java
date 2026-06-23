package com.mguhc.listeners;

import com.mguhc.BeatTheDS;
import com.mguhc.GameState;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class LutinShieldListener implements Listener {

    private final BeatTheDS plugin;
    private final Set<UUID> onCooldown = new HashSet<>();
    private static final String ITEM_NAME = ChatColor.LIGHT_PURPLE + "Bouclier Lutin";

    public LutinShieldListener(BeatTheDS plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Player player = event.getPlayer();
        ItemStack hand = player.getInventory().getItemInHand();

        if (hand == null || hand.getType() == Material.AIR) return;
        if (!hand.hasItemMeta() || !hand.getItemMeta().hasDisplayName()) return;
        if (!hand.getItemMeta().getDisplayName().equals(ITEM_NAME)) return;

        if (!plugin.isLutinNormal(player)) {
            player.sendMessage("§cSeuls les Lutins peuvent utiliser ce bouclier !");
            event.setCancelled(true);
            return;
        }

        if (!player.hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE)) {
            player.sendMessage("§cTu dois avoir l'effet §6Résistance §cpour activer ce pouvoir !");
            event.setCancelled(true);
            return;
        }

        if (onCooldown.contains(player.getUniqueId())) {
            player.sendMessage("§cCe pouvoir est en recharge jusqu'au prochain jour !");
            event.setCancelled(true);
            return;
        }

        int duration = plugin.getGameConfig().getShieldDurationTicks();
        int amplifier = plugin.getGameConfig().getShieldAmplifier();

        player.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
        player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, duration, amplifier));

        plugin.getEffectsManager().spawnShieldActivateEffect(player);

        new BukkitRunnable() {
            @Override
            public void run() {
                player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 0));
            }
        }.runTaskLater(plugin, duration + 2);

        onCooldown.add(player.getUniqueId());
        player.sendMessage("§dInvincibilité activée pendant " + (duration / 20) + " secondes !");
        event.setCancelled(true);
    }

    public void resetCooldowns() {
        onCooldown.clear();
        Bukkit.getOnlinePlayers().forEach(p -> p.sendMessage("§aLe pouvoir des Lutins est à nouveau disponible !"));
    }

    public void reset() {
        onCooldown.clear();
    }
}

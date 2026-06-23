package com.mguhc.listeners.scenario;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class NotchAppleListener implements Listener {

    @EventHandler
    private void OnConsume(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        if (item.getType().equals(Material.GOLDEN_APPLE) && item.getDurability() == 1) {
            event.setCancelled(true);
            player.removePotionEffect(PotionEffectType.REGENERATION);
            player.removePotionEffect(PotionEffectType.ABSORPTION);
            player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 30*20, 4));
            player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 120*20, 0));
            player.setSaturation(player.getSaturation() + 4);

        }
    }
}

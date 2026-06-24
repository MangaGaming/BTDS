package com.mguhc.listeners;

import com.mguhc.BeatTheDS;
import com.mguhc.GameState;
import com.mguhc.effects.EffectsManager;
import com.mguhc.effects.MessageManager;
import io.puharesource.mc.titlemanager.api.v2.TitleManagerAPI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Iterator;
import java.util.UUID;

public class BarreListener implements Listener {

    private final BeatTheDS plugin;
    private int santaHits;
    private int lastHits;
    private String bar = "§5||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||";
    public boolean hasBarre = true;

    public BarreListener(BeatTheDS plugin) {
        this.plugin = plugin;
        removeGoldSwordRecipes();
    }

    private void removeGoldSwordRecipes() {
        Iterator<org.bukkit.inventory.Recipe> it = Bukkit.recipeIterator();
        while (it.hasNext()) {
            org.bukkit.inventory.Recipe recipe = it.next();
            if (recipe.getResult().getType() == Material.GOLD_SWORD) {
                it.remove();
            }
        }
    }

    public void reset() {
        santaHits = 0;
        lastHits = 0;
        bar = "§5||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||";
        hasBarre = true;
    }

    @EventHandler
    public void onHit(EntityDamageByEntityEvent event) {
        if (event.getDamager().getType() != EntityType.PLAYER || event.getEntityType() != EntityType.PLAYER) {
            return;
        }

        if (!hasBarre) return;
        if (!plugin.getGameState().isPlaying()) return;

        Player player = (Player) event.getEntity();
        Player damager = (Player) event.getDamager();

        ItemStack itemInHand = damager.getItemInHand();
        boolean isLutinPuri = plugin.isLutinPurificateur(damager);
        boolean isGoldSword = itemInHand != null && itemInHand.getType() == Material.GOLD_SWORD;

        if (!isLutinPuri && !isGoldSword) return;

        UUID santaUUID = plugin.getSantaUUID();
        if (santaUUID == null || !player.getUniqueId().equals(santaUUID)) return;

        if (santaHits < plugin.getGameConfig().getDarknessBarMaxHits()) {
            lastHits++;
            santaHits++;
            updateBar();
        } else {
            hasBarre = false;
            plugin.getMessageManager().sendDarknessBarBroken();
            Player santaPlayer = Bukkit.getPlayer(santaUUID);
            if (santaPlayer != null && santaPlayer.isOnline()) {
                santaPlayer.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 0));
                santaPlayer.setHealth(santaPlayer.getMaxHealth());
            }
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        UUID santaUUID = plugin.getSantaUUID();

        if (santaUUID == null || !player.getUniqueId().equals(santaUUID)) return;
        if (!hasBarre) return;
        if (!plugin.getGameState().isPlaying()) return;

        event.setKeepInventory(true);
        event.setKeepLevel(true);
        event.setDeathMessage(null);

        plugin.getMessageManager().sendSantaRevived();

        new BukkitRunnable() {
            @Override
            public void run() {
                if (player.isOnline()) {
                    player.spigot().respawn();

                    player.setMaxHealth(plugin.getGameConfig().getSantaMaxHealth());
                    player.setHealth(plugin.getGameConfig().getSantaMaxHealth());

                    for (String effectName : plugin.getGameConfig().getSantaEffects()) {
                        PotionEffectType type = PotionEffectType.getByName(effectName);
                        if (type != null) {
                            player.addPotionEffect(new PotionEffect(type, Integer.MAX_VALUE, 0));
                        }
                    }

                    plugin.getEffectsManager().spawnParticles(player, org.bukkit.Effect.MOBSPAWNER_FLAMES, 50);
                }
            }
        }.runTaskLater(plugin, 1L);
    }

    private void updateBar() {
        TitleManagerAPI api = BeatTheDS.api;
        if (api == null) return;

        for (Player p : Bukkit.getOnlinePlayers()) {
            if (santaHits >= plugin.getGameConfig().getDarknessBarMaxHits()) {
                api.setFooter(p, "");
                return;
            }
            if (lastHits == 3 && santaHits > 0) {
                lastHits = 0;
                bar = removeLastBar(bar);
            }
            api.setFooter(p, bar + getPercentage() + "%");
        }
    }

    private String removeLastBar(String input) {
        if (input == null || input.isEmpty()) return input;
        int index = input.lastIndexOf("|");
        if (index == -1) return input;
        return input.substring(0, index) + input.substring(index + 1);
    }

    private int getPercentage() {
        long count = bar.chars().filter(ch -> ch == '|').count();
        return (int) Math.min(100, count);
    }
}

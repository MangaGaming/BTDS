package com.mguhc.listeners;

import com.mguhc.BeatTheDS;
import com.mguhc.GameState;
import com.mguhc.PlayerManager;
import com.mguhc.effects.EffectsManager;
import com.mguhc.effects.MessageManager;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class CoffreListener implements Listener {

    private final BeatTheDS plugin;
    private final Location chestLocation;
    private Player currentPlayerCrocheting;
    private boolean santaChestExists = false;
    private boolean isChestLocked = false;
    private BukkitRunnable lockpickTask;

    public CoffreListener(BeatTheDS plugin) {
        this.plugin = plugin;
        this.chestLocation = new Location(
                Bukkit.getWorld(plugin.getGameConfig().getWorldName()),
                plugin.getGameConfig().getChestX(),
                plugin.getGameConfig().getChestY(),
                plugin.getGameConfig().getChestZ());
    }

    public void reset() {
        santaChestExists = false;
        isChestLocked = false;
        currentPlayerCrocheting = null;
        if (lockpickTask != null) {
            lockpickTask.cancel();
            lockpickTask = null;
        }
        if (chestLocation.getWorld() != null) {
            chestLocation.getBlock().setType(Material.AIR);
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        if (!plugin.getGameState().isPlaying()) return;

        event.setDeathMessage(null);

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            onlinePlayer.playSound(onlinePlayer.getLocation(), Sound.WITHER_DEATH, 1.0F, 1.0F);
        }

        Player killer = event.getEntity().getKiller();
        PlayerManager pm = plugin.getPlayerManager();

        if (killer != null) {
            pm.addKill(killer);
            plugin.getMessageManager().sendKillMessage(player, killer);
            plugin.getEffectsManager().spawnKillEffect(killer);
            plugin.getMessageManager().sendKillScore(killer, pm.getKills(killer));
        } else {
            plugin.getMessageManager().sendKillMessage(player, null);
        }

        plugin.getEffectsManager().spawnDeathEffect(player);

        if (plugin.isSanta(player)) {
            chestLocation.getBlock().setType(Material.CHEST);
            santaChestExists = true;

            Chest chest = (Chest) chestLocation.getBlock().getState();
            chest.getInventory().addItem(new ItemStack(Material.GOLD_INGOT, 5));
        }

        if (player.equals(currentPlayerCrocheting)) {
            cancelLockpick();
            player.sendMessage(ChatColor.RED + "Vous êtes mort, le crochetage du coffre a été annulé.");
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        if (event.getPlayer().equals(currentPlayerCrocheting)) {
            cancelLockpick();
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block clickedBlock = event.getClickedBlock();

        if (clickedBlock == null || !santaChestExists) return;
        if (!clickedBlock.getLocation().equals(chestLocation)) return;
        if (clickedBlock.getType() != Material.CHEST) return;

        event.setCancelled(true);

        if (!isChestLocked && player.getGameMode() == GameMode.SURVIVAL) {
            player.sendMessage(ChatColor.GOLD + "Le coffre se crochette, veuillez attendre " + plugin.getGameConfig().getChestLockpickDurationSeconds() + " secondes !");
            isChestLocked = true;
            currentPlayerCrocheting = player;

            final int[] timer = {0};
            int duration = plugin.getGameConfig().getChestLockpickDurationSeconds();
            int interval = plugin.getGameConfig().getChestLockcheckIntervalTicks();

            lockpickTask = new BukkitRunnable() {
                @Override
                public void run() {
                    if (isPlayerInCrochetageZone(player)) {
                        if (timer[0] >= duration * 20 / interval) {
                            this.cancel();
                            Bukkit.broadcastMessage(ChatColor.GREEN + player.getName() + " a gagné le coffre !");
                            plugin.getEffectsManager().playSoundToAll(Sound.LEVEL_UP);
                            player.getWorld().getBlockAt(chestLocation).setType(Material.AIR);
                            santaChestExists = false;
                            isChestLocked = false;
                            currentPlayerCrocheting = null;
                        }
                    } else {
                        cancelLockpick();
                        player.sendMessage(ChatColor.RED + "Vous avez annulé le crochetage du coffre en sortant de la zone !");
                        Bukkit.broadcastMessage(ChatColor.RED + player.getName() + " a annulé le crochetage du coffre ! Il est resté " + timer[0] + " secondes");
                        this.cancel();
                    }
                    timer[0]++;
                }
            };
            lockpickTask.runTaskTimer(plugin, 0, interval);
        }
    }

    private void cancelLockpick() {
        isChestLocked = false;
        currentPlayerCrocheting = null;
        if (lockpickTask != null) {
            lockpickTask.cancel();
            lockpickTask = null;
        }
    }

    private boolean isPlayerInCrochetageZone(Player player) {
        Location loc = player.getLocation();
        double x = loc.getX();
        double z = loc.getZ();

        return (x >= plugin.getGameConfig().getChestLockpickRangeXMin() &&
                x <= plugin.getGameConfig().getChestLockpickRangeXMax() &&
                z >= plugin.getGameConfig().getChestLockpickRangeZMin() &&
                z <= plugin.getGameConfig().getChestLockpickRangeZMax());
    }
}

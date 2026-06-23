package com.mguhc.listeners;

import com.mguhc.BeatTheDS;
import com.mguhc.GameState;
import com.mguhc.config.GameConfig;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class SanctuaireManager implements Listener {

    private final BeatTheDS plugin;
    private final Map<String, Location> sanctuaires = new HashMap<>();
    private final Map<Location, String> positionToSanctuaire = new HashMap<>();
    private final Map<String, Integer> timeAccumulated = new HashMap<>();
    private final Map<String, Integer> captureProgress = new HashMap<>();
    private final Map<UUID, String> capturing = new HashMap<>();
    private final Set<String> captured = new HashSet<>();
    private boolean blackUnlocked = false;
    private BukkitRunnable captureTask;
    private BukkitRunnable particleTask;

    public SanctuaireManager(BeatTheDS plugin) {
        this.plugin = plugin;
        GameConfig config = plugin.getGameConfig();
        String worldName = config.getWorldName();

        String[] names = {"rouge", "bleu", "vert", "jaune", "noir"};
        for (String name : names) {
            Location loc = new Location(
                    Bukkit.getWorld(worldName),
                    config.getSanctuaryX(name),
                    config.getSanctuaryY(name),
                    config.getSanctuaryZ(name));
            sanctuaires.put(name, loc);

            Location blockLoc = loc.clone();
            blockLoc.setX(blockLoc.getBlockX());
            blockLoc.setY(blockLoc.getBlockY());
            blockLoc.setZ(blockLoc.getBlockZ());
            positionToSanctuaire.put(blockLoc, name);

            timeAccumulated.put(name, 0);
            captureProgress.put(name, 0);
        }
    }

    public void reset() {
        captured.clear();
        capturing.clear();
        blackUnlocked = false;
        for (String name : sanctuaires.keySet()) {
            timeAccumulated.put(name, 0);
            captureProgress.put(name, 0);
        }
        if (captureTask != null) {
            captureTask.cancel();
            captureTask = null;
        }
        if (particleTask != null) {
            particleTask.cancel();
            particleTask = null;
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (!plugin.getGameState().isPlaying()) return;

        Player player = event.getPlayer();
        if (!plugin.isSanta(player)) return;

        Block block = event.getClickedBlock();
        if (block == null) return;

        Material blockType = block.getType();
        if (blockType != Material.STANDING_BANNER && blockType != Material.WALL_BANNER) return;

        Location blockLoc = block.getLocation();
        blockLoc.setX(blockLoc.getBlockX());
        blockLoc.setY(blockLoc.getBlockY());
        blockLoc.setZ(blockLoc.getBlockZ());

        String sanctuaire = positionToSanctuaire.get(blockLoc);
        if (sanctuaire == null) return;

        if ("noir".equals(sanctuaire) && !blackUnlocked) {
            player.sendMessage("§8Le sanctuaire noir n'est pas encore accessible !");
            return;
        }

        if (captured.contains(sanctuaire)) {
            player.sendMessage("§cCe sanctuaire est déjà capturé !");
            return;
        }

        if (capturing.containsKey(player.getUniqueId())) {
            player.sendMessage("§cVous capturez déjà un sanctuaire !");
            return;
        }

        capturing.put(player.getUniqueId(), sanctuaire);
        player.sendMessage("§a§lCapture en cours du sanctuaire §f" + sanctuaire + "§a§l...");
        player.sendMessage("§7Restez près de la bannière pour capturer.");
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (!plugin.getGameState().isPlaying()) return;
        if (!plugin.isSanta(event.getPlayer())) return;

        UUID uuid = event.getPlayer().getUniqueId();
        if (!capturing.containsKey(uuid)) return;

        String sanctuaire = capturing.get(uuid);
        Location sanctLoc = sanctuaires.get(sanctuaire);
        if (sanctLoc == null) return;

        if (event.getPlayer().getLocation().distance(sanctLoc) > 5.0) {
            capturing.remove(uuid);
            timeAccumulated.put(sanctuaire, 0);
            captureProgress.put(sanctuaire, 0);
            event.getPlayer().sendMessage("§c§lCapture annulée ! Vous êtes trop loin du sanctuaire.");
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (!plugin.getGameState().isPlaying()) return;

        Block block = event.getBlock();
        Material blockType = block.getType();
        Location blockLoc = block.getLocation();
        blockLoc.setX(blockLoc.getBlockX());
        blockLoc.setY(blockLoc.getBlockY());
        blockLoc.setZ(blockLoc.getBlockZ());

        if (positionToSanctuaire.containsKey(blockLoc) &&
                (blockType == Material.STANDING_BANNER || blockType == Material.WALL_BANNER)) {
            event.setCancelled(true);
            event.getPlayer().sendMessage("§cVous ne pouvez pas casser les bannières de sanctuaire !");
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (!plugin.getGameState().isPlaying()) return;

        Block block = event.getBlock();
        Location blockLoc = block.getLocation();
        blockLoc.setX(blockLoc.getBlockX());
        blockLoc.setY(blockLoc.getBlockY());
        blockLoc.setZ(blockLoc.getBlockZ());

        if (positionToSanctuaire.containsKey(blockLoc)) {
            event.setCancelled(true);
            event.getPlayer().sendMessage("§cVous ne pouvez pas poser de blocs sur les sanctuaires !");
        }
    }

    public void startCaptureSystem() {
        int ticks = plugin.getGameConfig().getSanctuaryCaptureTicks();

        captureTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (!plugin.getGameState().isPlaying()) return;

                for (Map.Entry<UUID, String> entry : new HashMap<>(capturing).entrySet()) {
                    UUID uuid = entry.getKey();
                    String sanctuaire = entry.getValue();

                    Player santa = Bukkit.getPlayer(uuid);
                    if (santa == null || !santa.isOnline()) {
                        capturing.remove(uuid);
                        continue;
                    }

                    Location sanctLoc = sanctuaires.get(sanctuaire);
                    if (sanctLoc == null) continue;

                    if (santa.getLocation().distance(sanctLoc) <= 5.0) {
                        int currentTicks = timeAccumulated.get(sanctuaire);
                        timeAccumulated.put(sanctuaire, currentTicks + 1);

                        double secondsPerPoint = "noir".equals(sanctuaire)
                                ? plugin.getGameConfig().getSanctuarySecondsPerPointNoir()
                                : plugin.getGameConfig().getSanctuarySecondsPerPointNormal();
                        int totalPoints = (int) ((currentTicks + 1) * 0.05 / secondsPerPoint);
                        captureProgress.put(sanctuaire, Math.min(100, totalPoints));

                        int percentage = Math.min(100, totalPoints);
                        if (percentage % 25 == 0 && percentage > 0 && percentage < 100) {
                            plugin.getEffectsManager().sendActionBar(santa,
                                    "§d§lCapture §f" + sanctuaire + " §7- §f" + percentage + "%");
                        }

                        if (totalPoints >= 100 && !captured.contains(sanctuaire)) {
                            captured.add(sanctuaire);
                            capturing.remove(uuid);

                            plugin.getMessageManager().sendSanctuaryCaptured(sanctuaire);
                            plugin.getEffectsManager().spawnSanctuaryCaptureEffect(sanctLoc);
                            plugin.getEffectsManager().playSoundToAll(org.bukkit.Sound.NOTE_PLING);

                            if (captured.contains("rouge") && captured.contains("bleu") &&
                                    captured.contains("vert") && captured.contains("jaune") && !blackUnlocked) {
                                blackUnlocked = true;
                                Bukkit.broadcastMessage("§8§l🌑 Le sanctuaire noir s'est révélé !");
                                plugin.getEffectsManager().sendTitle("§8§lLe sanctuaire noir s'est révélé !", "");
                                plugin.getEffectsManager().playSoundToAll(org.bukkit.Sound.ENDERDRAGON_GROWL);
                            }
                        }
                    }
                }
            }
        };
        captureTask.runTaskTimer(plugin, 0L, ticks);

        particleTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (!plugin.getGameState().isPlaying()) return;

                for (Map.Entry<UUID, String> entry : capturing.entrySet()) {
                    Player santa = Bukkit.getPlayer(entry.getKey());
                    if (santa == null || !santa.isOnline()) continue;

                    Location sanctLoc = sanctuaires.get(entry.getValue());
                    if (sanctLoc == null) continue;

                    if (santa.getLocation().distance(sanctLoc) <= 5.0) {
                        plugin.getEffectsManager().spawnParticles(santa, org.bukkit.Effect.MOBSPAWNER_FLAMES, 5);
                    }
                }
            }
        };
        particleTask.runTaskTimer(plugin, 0L, 10L);
    }

    public int getProgressPoints(String sanctuaire) {
        return captureProgress.getOrDefault(sanctuaire, 0);
    }

    public boolean isCaptured(String sanctuaire) {
        return captured.contains(sanctuaire);
    }

    public boolean isBlackUnlocked() {
        return blackUnlocked;
    }
}

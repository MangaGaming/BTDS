package com.mguhc.listeners;

import com.mguhc.BeatTheDS;
import com.mguhc.GameState;
import com.mguhc.PlayerManager;
import com.mguhc.effects.EffectsManager;
import com.mguhc.scoreboard.UHCScoreboard;
import com.mguhc.utils.SkullUtils;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ConfigListener implements Listener {

    private static final String START_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjFlOTc0YTI2MDhiZDZlZTU3ZjMzNDg1NjQ1ZGQ5MjJkMTZiNGEzOTc0NGViYWI0NzUzZjRkZWI0ZWY3ODIifX19";
    private static final String MEETUP_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzBjZjc0ZTI2MzhiYTVhZDMyMjM3YTM3YjFkNzZhYTEyM2QxODU0NmU3ZWI5YTZiOTk2MWU0YmYxYzNhOTE5In19fQ";
    private static final String SANTA_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2UxZjVjMDM1MDEwMGQ1NWY5NWQxNzNhZTliODQ4ODJhNTAyNmMwOTVkODhjY2E1ZjliOGU4OTM1NjJhMDZjZiJ9fX0";

    private final BeatTheDS plugin;
    private final Map<UUID, String> playerInputState = new HashMap<>();

    public ConfigListener(BeatTheDS plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        UHCScoreboard scoreboard = new UHCScoreboard(plugin, player);
        scoreboard.createScoreboard(player);

        updateTabColor(player);

        if (plugin.getGameState().isWaiting()) {
            plugin.getPlayerManager().addPlayer(player);
            BeatTheDS.clearAll(player);
            player.teleport(new Location(player.getWorld(),
                    plugin.getGameConfig().getSpawnX(),
                    plugin.getGameConfig().getSpawnY(),
                    plugin.getGameConfig().getSpawnZ()));

            if (player.isOp()) {
                player.getInventory().setItem(4, getConfigItem());
            }

            player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 255));
            player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, Integer.MAX_VALUE, 255));

            int count = plugin.getPlayerManager().getPlayerUUIDs().size();
            player.sendMessage("§6§lBienvenue §f" + player.getName() + " §6! §7(Joueurs: " + count + ")");
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        plugin.getPlayerManager().removePlayer(uuid);
        playerInputState.remove(uuid);
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();

        if (LutinPurificateurResurrection.pendingResurrections.contains(player.getUniqueId())) {
            LutinPurificateurResurrection.pendingResurrections.remove(player.getUniqueId());
            return;
        }

        if (plugin.isSanta(player) && plugin.barreListener != null && plugin.barreListener.hasBarre) {
            plugin.getMessageManager().sendSantaRevived();
            return;
        }

        if (plugin.getGameState().isPlaying()) {
            player.setGameMode(GameMode.SPECTATOR);
            plugin.getBossBarManager().addPlayer(player);
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        if (item != null && item.equals(getConfigItem())) {
            event.setCancelled(true);
            openConfigInventory(player);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        ItemStack item = event.getItemInHand();
        if (item != null && item.equals(getConfigItem())) {
            event.setCancelled(true);
        }
    }

    private void openConfigInventory(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 18, "§9§lConfiguration");

        inventory.setItem(4, getStartItem());
        inventory.setItem(12, getMeetupItem());
        inventory.setItem(14, getSantaItem());

        player.openInventory(inventory);
    }

    @EventHandler
    public void onConfigInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        ItemStack item = event.getCurrentItem();
        if (item == null) return;

        if (item.equals(getStartItem())) {
            plugin.startGame(player);
            player.closeInventory();
        } else if (item.equals(getMeetupItem())) {
            plugin.toggleMeetup(player);
            player.closeInventory();
        } else if (item.equals(getSantaItem())) {
            playerInputState.put(player.getUniqueId(), "bts.setsanta");
            player.closeInventory();
            player.sendMessage("§aÉcrivez le nom du joueur que vous voulez mettre Père Noël");
        }
    }

    @EventHandler
    public void onChat(@SuppressWarnings("deprecation") PlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();
        String inputState = playerInputState.get(player.getUniqueId());

        if (inputState != null && inputState.equals("bts.setsanta")) {
            Player target = Bukkit.getPlayer(message);
            if (target != null) {
                plugin.setSanta(target);
                player.sendMessage(ChatColor.GREEN + "Le Santa a été sélectionné : " + target.getName());
            } else {
                player.sendMessage(ChatColor.RED + "Joueur non trouvé !");
            }
            playerInputState.remove(player.getUniqueId());
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onRegen(EntityRegainHealthEvent event) {
        if (!(event.getEntity() instanceof Player)) return;

        Player p = (Player) event.getEntity();
        if (event.getRegainReason() == EntityRegainHealthEvent.RegainReason.SATIATED ||
                event.getRegainReason() == EntityRegainHealthEvent.RegainReason.REGEN) {
            UUID santaUUID = plugin.getSantaUUID();
            if (santaUUID != null && !santaUUID.equals(p.getUniqueId())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onConsume(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        org.bukkit.inventory.ItemStack item = event.getItem();

        if (item.getType() == Material.GOLDEN_APPLE && item.getDurability() == 0 && player.getMaxHealth() >= 40) {
            UUID santaUUID = plugin.getSantaUUID();
            if (santaUUID != null && !santaUUID.equals(player.getUniqueId())) {
                event.setCancelled(true);

                player.removePotionEffect(PotionEffectType.ABSORPTION);
                player.removePotionEffect(PotionEffectType.REGENERATION);
                player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 160, 1));
                player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 2400, 0));
                player.setSaturation(player.getSaturation() + 4);

                ItemStack goldenApple = new ItemStack(Material.GOLDEN_APPLE, 1);
                player.getInventory().removeItem(goldenApple);
            }
        }
    }

    private void updateTabColor(Player player) {
        Scoreboard scoreboard = player.getScoreboard();
        if (scoreboard == null || scoreboard.equals(Bukkit.getScoreboardManager().getMainScoreboard())) {
            scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
            player.setScoreboard(scoreboard);
        }

        Team santaTeam = scoreboard.getTeam("Santa");
        if (santaTeam == null) {
            santaTeam = scoreboard.registerNewTeam("Santa");
        }
        santaTeam.setPrefix("§c§l[Santa] §c");

        Team lutinTeam = scoreboard.getTeam("Lutin");
        if (lutinTeam == null) {
            lutinTeam = scoreboard.registerNewTeam("Lutin");
        }
        lutinTeam.setPrefix("§3§l[Lutin] §3");

        Team puriTeam = scoreboard.getTeam("Puri");
        if (puriTeam == null) {
            puriTeam = scoreboard.registerNewTeam("Puri");
        }
        puriTeam.setPrefix("§5§l[Puri] §5");
    }

    private ItemStack getConfigItem() {
        return SkullUtils.createPlayerHead(
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjFlOTc0YTI2MDhiZDZlZTU3ZjMzNDg1NjQ1ZGQ5MjJkMTZiNGEzOTc0NGViYWI0NzUzZjRkZWI0ZWY3ODIifX19",
                "§6§lConfig");
    }

    public static ItemStack getStartItem() {
        return SkullUtils.createPlayerHead(START_TEXTURE, "§a§lLancer");
    }

    public static ItemStack getMeetupItem() {
        return SkullUtils.createPlayerHead(MEETUP_TEXTURE, "§7§lMeetup");
    }

    public static ItemStack getSantaItem() {
        return SkullUtils.createPlayerHead(SANTA_TEXTURE, "§c§lDéfinir le Père Noël");
    }
}

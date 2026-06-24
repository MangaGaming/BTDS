package com.mguhc.effects;

import com.mguhc.BeatTheDS;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MessageManager {

    private final BeatTheDS plugin;

    public MessageManager(BeatTheDS plugin) {
        this.plugin = plugin;
    }

    public void sendKillMessage(Player victim, Player killer) {
        String victimName = victim.getName();
        String killerName = killer != null ? killer.getName() : "§7l'obscurité";

        String victimRole = getRolePrefix(victim);
        String killerRole = killer != null ? getRolePrefix(killer) : "";

        String message;
        if (plugin.isSanta(victim)) {
            message = "§c§l☠ Santa §7a été vaincu par " + killerRole + killerName;
        } else if (killer != null && plugin.isSanta(killer)) {
            message = "§c§l⚔ Santa §7a tué " + victimRole + victimName;
        } else if (killer != null) {
            message = victimRole + victimName + " §7a été tué par " + killerRole + killerName;
        } else {
            message = victimRole + victimName + " §7a été éliminé";
        }

        Bukkit.broadcastMessage("§8[§6§lBTDS§8] " + message);
    }

    public void sendKillScore(Player player, int kills) {
        EffectsManager effects = plugin.getEffectsManager();
        effects.setActionBar(player, "§a+1 Kill §7| §fTotal: " + kills, EffectsManager.ActionBarPriority.LOW);
        plugin.getServer().getScheduler().runTaskLater(plugin, () ->
                effects.clearActionBar(player, EffectsManager.ActionBarPriority.LOW), 60L);
    }

    public void sendRoleReveal(Player player, String role) {
        EffectsManager effects = plugin.getEffectsManager();

        switch (role.toLowerCase()) {
            case "santa":
                effects.sendTitle(player, "§c§lTu es le PÈRE NOËL !", "§7Élimine tous les Lutins");
                effects.playSound(player, Sound.WITHER_SPAWN, 1.0f, 1.0f);
                break;
            case "lutin":
                effects.sendTitle(player, "§3§lTu es un LUTIN !", "§7Élimine le Père Noël");
                effects.playSound(player, Sound.LEVEL_UP, 1.0f, 1.0f);
                break;
            case "lutinpurificateur":
                effects.sendTitle(player, "§5§lTu es un LUTIN PURIFICATEUR !", "§7Tu peux résister à la mort");
                effects.playSound(player, Sound.ENDERMAN_TELEPORT, 1.0f, 1.0f);
                break;
        }
    }

    public void sendGameStartCountdown(int count) {
        EffectsManager effects = plugin.getEffectsManager();
        String color = count <= 3 ? "§c" : "§e";
        effects.sendTitle(color + "§l" + count, "");
        effects.playSoundToAll(Sound.NOTE_PLING);
    }

    public void sendGameStartGo() {
        EffectsManager effects = plugin.getEffectsManager();
        effects.sendTitle("§a§lGO!", "§7Que le meilleur gagne");
        effects.playSoundToAll(Sound.FIREWORK_LAUNCH);
    }

    public void sendNightFalling() {
        EffectsManager effects = plugin.getEffectsManager();
        effects.sendTitle("§8§lLa nuit tombe...", "§7Les ténèbres approchent");
        effects.playSoundToAll(Sound.AMBIENCE_CAVE);
    }

    public void sendDayStarting(int dayNumber) {
        EffectsManager effects = plugin.getEffectsManager();
        effects.sendTitle("§e§lJour " + dayNumber, "§7Le soleil se lève");
        effects.playSoundToAll(Sound.NOTE_PLING);
    }

    public void sendSanctuaryCaptured(String name) {
        EffectsManager effects = plugin.getEffectsManager();
        effects.sendTitle("§d§lSanctuaire " + name + " capturé !", "");
    }

    public void sendDarknessBarBroken() {
        EffectsManager effects = plugin.getEffectsManager();
        effects.sendTitle("§5§lLa barre de noirceur a été défaite !", "§7Le Père Noël redevient lumineux");
        effects.playSoundToAll(Sound.WITHER_DEATH);
    }

    public void sendSantaRevived() {
        EffectsManager effects = plugin.getEffectsManager();
        effects.sendTitle("§c§lLe Père Noël a ressuscité !", "§5Il est encore trop sombre pour mourir");
        effects.playSoundToAll(Sound.WITHER_SPAWN);
    }

    public void sendVictory(String winner) {
        EffectsManager effects = plugin.getEffectsManager();

        for (Player p : Bukkit.getOnlinePlayers()) {
            if (winner.equals("lutins")) {
                effects.sendTitle(p, "§3§lVICTOIRE!", "§7Les Lutins ont gagné");
            } else if (winner.equals("santa")) {
                effects.sendTitle(p, "§c§lVICTOIRE!", "§7Le Père Noël a gagné");
            } else {
                effects.sendTitle(p, "§7§lMATCH NUL", "");
            }
        }
    }

    public void sendGameStats() {
        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage("§6§l═══════════════════════════════");
        Bukkit.broadcastMessage("§6         STATISTIQUES");
        Bukkit.broadcastMessage("§6§l═══════════════════════════════");

        Player santa = plugin.getSanta();
        if (santa != null) {
            Bukkit.broadcastMessage("§c§lSanta: §f" + santa.getName() + " §7(Kills: " + plugin.getPlayerManager().getKills(santa) + ")");
        }

        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage("§3§lTop Kills:");

        java.util.List<java.util.Map.Entry<UUID, Integer>> sorted = new java.util.ArrayList<>(getSortedKills());
        int limit = Math.min(3, sorted.size());
        for (int i = 0; i < limit; i++) {
            java.util.Map.Entry<UUID, Integer> entry = sorted.get(i);
            Player p = Bukkit.getPlayer(entry.getKey());
            if (p != null) {
                String medal = i == 0 ? "§6§l1er" : i == 1 ? "§7§l2ème" : "§c§l3ème";
                Bukkit.broadcastMessage(medal + " §f" + p.getName() + " §7- " + entry.getValue() + " kills");
            }
        }

        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage("§7Temps de jeu: §f" + formatTime(plugin.getTimePassed()));
        Bukkit.broadcastMessage("§6§l═══════════════════════════════");
    }

    private java.util.List<java.util.Map.Entry<UUID, Integer>> getSortedKills() {
        java.util.Map<UUID, Integer> kills = new HashMap<>();
        for (UUID uuid : plugin.getPlayerManager().getPlayerUUIDs()) {
            kills.put(uuid, plugin.getPlayerManager().getKills(uuid));
        }

        java.util.List<java.util.Map.Entry<UUID, Integer>> sorted = new java.util.ArrayList<>(kills.entrySet());
        sorted.sort((a, b) -> b.getValue() - a.getValue());
        return sorted;
    }

    private String getRolePrefix(Player player) {
        if (plugin.isSanta(player)) return "§c§l[Santa] §c";
        if (plugin.isLutinPurificateur(player)) return "§5§l[Puri] §5";
        if (plugin.isLutinNormal(player)) return "§3§l[Lutin] §3";
        return "";
    }

    private String formatTime(int time) {
        int minutes = time / 60;
        int seconds = time % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
}

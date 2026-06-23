package com.mguhc.scoreboard;

import com.mguhc.BeatTheDS;
import com.mguhc.GameState;
import com.mguhc.PlayerManager;
import com.mguhc.listeners.SanctuaireManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UHCScoreboard {

    private final BeatTheDS plugin;

    public UHCScoreboard(BeatTheDS plugin, Player player) {
        this.plugin = plugin;
    }

    public void createScoreboard(Player player) {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        Scoreboard scoreboard = manager.getNewScoreboard();
        UUID playerID = player.getUniqueId();

        Objective objective = scoreboard.registerNewObjective("uhc", "dummy");
        objective.setDisplayName("§3Beat §fThe §7Santa");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        player.setScoreboard(scoreboard);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline()) {
                    this.cancel();
                    return;
                }

                for (String entry : new ArrayList<>(objective.getScoreboard().getEntries())) {
                    objective.getScoreboard().resetScores(entry);
                }

                int score = 18;

                objective.getScore("§6Jour §2: " + getDayString()).setScore(score--);
                objective.getScore("§6Temps §2: " + formatTime(plugin.getTimePassed())).setScore(score--);
                objective.getScore(ChatColor.GRAY + "______________").setScore(score--);
                objective.getScore("§6Pvp §2: " + getPvpString()).setScore(score--);
                objective.getScore("§6Bordure §2: " + getBorderSizeString()).setScore(score--);
                objective.getScore(ChatColor.GRAY + "§f______________").setScore(score--);
                objective.getScore("§6Joueurs §2: " + getOnlinePlayersString()).setScore(score--);
                objective.getScore("§6Lutin §2: " + getLutinString()).setScore(score--);
                objective.getScore("§6Santa §2: " + getSantaString()).setScore(score--);

                if (plugin.sanctuaireManager != null) {
                    objective.getScore(ChatColor.GRAY + "§f§f§f______________").setScore(score--);
                    addSanctuaireLine(objective, "rouge", "§c", score--);
                    addSanctuaireLine(objective, "bleu", "§9", score--);
                    addSanctuaireLine(objective, "vert", "§a", score--);
                    addSanctuaireLine(objective, "jaune", "§e", score--);
                    if (plugin.sanctuaireManager.isBlackUnlocked()) {
                        addSanctuaireLine(objective, "noir", "§8", score--);
                    }
                }

                objective.getScore(ChatColor.GRAY + "§f§f______________").setScore(score--);
                objective.getScore("§6Kills §2: " + getKillsString(playerID)).setScore(score);

                updateNametags(scoreboard);
                player.setScoreboard(scoreboard);
            }
        }.runTaskTimer(plugin, 0, 20);
    }

    private void addSanctuaireLine(Objective obj, String name, String colorCode, int score) {
        if (plugin.sanctuaireManager == null) return;

        int progress = plugin.sanctuaireManager.getProgressPoints(name);
        String displayName;

        if (plugin.sanctuaireManager.isCaptured(name)) {
            displayName = colorCode + "✓ Sanctuaire " + capitalize(name) + " §a100%";
        } else {
            displayName = colorCode + " Sanctuaire " + capitalize(name) + " §f" + progress + "%";
        }

        obj.getScore(displayName).setScore(score);
    }

    private void updateNametags(Scoreboard scoreboard) {
        UUID santaUUID = plugin.getSantaUUID();
        for (UUID uuid : plugin.getPlayerManager().getPlayerUUIDs()) {
            Player p = Bukkit.getPlayer(uuid);
            if (p == null) continue;

            if (santaUUID != null && uuid.equals(santaUUID)) {
                Team santaTeam = scoreboard.getTeam("Santa");
                if (santaTeam == null) {
                    santaTeam = scoreboard.registerNewTeam("Santa");
                }
                santaTeam.setPrefix("§cSanta ");
                santaTeam.addEntry(p.getName());
            } else {
                Team lutinTeam = scoreboard.getTeam("Lutin");
                if (lutinTeam == null) {
                    lutinTeam = scoreboard.registerNewTeam("Lutin");
                }
                lutinTeam.setPrefix("§3Lutin ");
                lutinTeam.addEntry(p.getName());
            }
        }
    }

    private String getKillsString(UUID playerID) {
        return String.valueOf(plugin.getPlayerManager().getKills(playerID));
    }

    private String getLutinString() {
        int total = getOnlinePlayerCount();
        return String.valueOf(Math.max(0, total - 1));
    }

    private String getSantaString() {
        return plugin.getSantaUUID() != null ? "1" : "Pas de Santa";
    }

    private String getPvpString() {
        if (plugin.isMeetupEnabled()) return "Activé";
        return plugin.getTimePassed() >= plugin.getGameConfig().getPvpDelayMinutes() * 60 ? "Activé" : "Désactivé";
    }

    private String getDayString() {
        return String.valueOf(plugin.getDayNumber());
    }

    private String getBorderSizeString() {
        double borderSize = Bukkit.getWorld(plugin.getGameConfig().getWorldName()).getWorldBorder().getSize();
        return String.valueOf((int) borderSize);
    }

    private String getOnlinePlayersString() {
        return String.valueOf(getOnlinePlayerCount());
    }

    private int getOnlinePlayerCount() {
        int count = 0;
        for (UUID uuid : plugin.getPlayerManager().getPlayerUUIDs()) {
            Player p = Bukkit.getPlayer(uuid);
            if (p != null && p.isOnline() && p.getGameMode() == GameMode.SURVIVAL) {
                count++;
            }
        }
        return count;
    }

    private String formatTime(int time) {
        int minutes = time / 60;
        int seconds = time % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}

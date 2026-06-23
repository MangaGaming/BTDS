package com.mguhc.manager;

import com.mguhc.config.GameConfig;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

public class RoleManager {

    private final GameConfig config;
    private UUID santa;
    private final Set<UUID> lutins = new HashSet<>();
    private final Set<UUID> lutinsPuri = new HashSet<>();
    private final Set<UUID> allPlayers = new HashSet<>();

    public RoleManager(GameConfig config) {
        this.config = config;
    }

    public void assignRoles(Player santaPlayer, List<Player> players) {
        reset();

        this.santa = santaPlayer.getUniqueId();
        allPlayers.add(santaPlayer.getUniqueId());

        Set<UUID> nonSanta = new HashSet<>();
        for (Player p : players) {
            if (!p.equals(santaPlayer)) {
                nonSanta.add(p.getUniqueId());
                allPlayers.add(p.getUniqueId());
            }
        }

        List<UUID> uuidList = new ArrayList<>(nonSanta);
        Collections.shuffle(uuidList);
        int splitSize = (int) Math.ceil(uuidList.size() * (config.getLutinPuriPercentage() / 100.0));

        lutinsPuri.addAll(uuidList.subList(0, Math.min(splitSize, uuidList.size())));
        lutins.addAll(nonSanta);
        lutins.removeAll(lutinsPuri);
    }

    public void assignRolesNoPurificateurs(Player santaPlayer, List<Player> players) {
        reset();

        this.santa = santaPlayer.getUniqueId();
        allPlayers.add(santaPlayer.getUniqueId());

        for (Player p : players) {
            if (!p.equals(santaPlayer)) {
                lutins.add(p.getUniqueId());
                allPlayers.add(p.getUniqueId());
            }
        }
    }

    public void reset() {
        santa = null;
        lutins.clear();
        lutinsPuri.clear();
        allPlayers.clear();
    }

    public boolean isSanta(Player player) {
        return player != null && santa != null && player.getUniqueId().equals(santa);
    }

    public boolean isSanta(UUID uuid) {
        return uuid != null && uuid.equals(santa);
    }

    public boolean isLutinNormal(Player player) {
        return player != null && lutins.contains(player.getUniqueId());
    }

    public boolean isLutinPurificateur(Player player) {
        return player != null && lutinsPuri.contains(player.getUniqueId());
    }

    public UUID getSantaUUID() {
        return santa;
    }

    public Player getSantaPlayer() {
        return santa != null ? Bukkit.getPlayer(santa) : null;
    }

    public Set<UUID> getLutins() {
        return Collections.unmodifiableSet(lutins);
    }

    public Set<UUID> getLutinsPuri() {
        return Collections.unmodifiableSet(lutinsPuri);
    }

    public Set<UUID> getAllPlayers() {
        return Collections.unmodifiableSet(allPlayers);
    }

    public void removePlayer(UUID uuid) {
        lutins.remove(uuid);
        lutinsPuri.remove(uuid);
        allPlayers.remove(uuid);
        if (uuid.equals(santa)) {
            santa = null;
        }
    }
}

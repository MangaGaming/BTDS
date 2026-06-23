package com.mguhc;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

public class PlayerManager {

    private static PlayerManager instance;

    private final Set<UUID> players = new HashSet<>();
    private final Map<UUID, Integer> killMap = new HashMap<>();

    public PlayerManager() {
        instance = this;
    }

    public void addPlayer(Player p) {
        players.add(p.getUniqueId());
    }

    public void removePlayer(Player p) {
        players.remove(p.getUniqueId());
        killMap.remove(p.getUniqueId());
    }

    public void removePlayer(UUID uuid) {
        players.remove(uuid);
        killMap.remove(uuid);
    }

    public boolean isInPlayers(Player p) {
        return players.contains(p.getUniqueId());
    }

    public boolean isInPlayers(UUID uuid) {
        return players.contains(uuid);
    }

    public List<Player> getOnlinePlayers() {
        List<Player> online = new ArrayList<>();
        for (UUID uuid : players) {
            Player p = Bukkit.getPlayer(uuid);
            if (p != null && p.isOnline()) {
                online.add(p);
            }
        }
        return online;
    }

    public List<Player> getPlayers() {
        return getOnlinePlayers();
    }

    public Set<UUID> getPlayerUUIDs() {
        return Collections.unmodifiableSet(players);
    }

    public int getKills(Player player) {
        return killMap.getOrDefault(player.getUniqueId(), 0);
    }

    public int getKills(UUID uuid) {
        return killMap.getOrDefault(uuid, 0);
    }

    public void addKill(Player player) {
        UUID uuid = player.getUniqueId();
        killMap.put(uuid, killMap.getOrDefault(uuid, 0) + 1);
    }

    public void addKill(UUID uuid) {
        killMap.put(uuid, killMap.getOrDefault(uuid, 0) + 1);
    }

    public void reset() {
        players.clear();
        killMap.clear();
    }

    public static PlayerManager getInstance() {
        return instance;
    }
}

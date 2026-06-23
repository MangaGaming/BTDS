package com.mguhc;

import com.mguhc.config.GameConfig;
import com.mguhc.effects.BossBarManager;
import com.mguhc.effects.EffectsManager;
import com.mguhc.effects.MessageManager;
import com.mguhc.listeners.*;
import com.mguhc.listeners.scenario.*;
import com.mguhc.manager.GearManager;
import com.mguhc.manager.RoleManager;
import io.puharesource.mc.titlemanager.api.v2.TitleManagerAPI;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class BeatTheDS extends JavaPlugin implements Listener {

    public static TitleManagerAPI api;
    private static BeatTheDS instance;

    private GameState gameState = GameState.WAITING;
    private GameConfig gameConfig;

    private PlayerManager playerManager;
    private RoleManager roleManager;
    private GearManager gearManager;
    private EffectsManager effectsManager;
    private MessageManager messageManager;
    private BossBarManager bossBarManager;

    public SanctuaireManager sanctuaireManager;
    public BarreListener barreListener;
    public CoffreListener coffreListener;
    public LutinPurificateurResurrection resurrectionListener;
    public LutinShieldListener lutinShieldListener;

    private int timePassed = 0;
    private int dayNumber = 0;
    private boolean meetupEnabled = false;
    private UUID selectedSantaUUID;
    private BukkitRunnable timerRunnable;
    private boolean countdownActive = false;

    public static final boolean TEST_MODE_NO_LUTINSP = false;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        gameConfig = new GameConfig(getConfig());
        playerManager = new PlayerManager();
        roleManager = new RoleManager(gameConfig);
        gearManager = new GearManager(gameConfig);
        effectsManager = new EffectsManager(this);
        messageManager = new MessageManager(this);
        bossBarManager = new BossBarManager(this);

        if (Bukkit.getPluginManager().isPluginEnabled("TitleManager")) {
            api = (TitleManagerAPI) Bukkit.getPluginManager().getPlugin("TitleManager");
        } else {
            getLogger().warning("TitleManager non trouvé — action bar désactivée.");
            api = null;
        }

        for (World world : Bukkit.getWorlds()) {
            world.setGameRuleValue("announceAdvancements", "false");
            world.setGameRuleValue("doDaylightCycle", "false");
            world.setDifficulty(Difficulty.NORMAL);
        }

        registerListeners();
    }

    @Override
    public void onDisable() {
        resetGame();
    }

    private void registerListeners() {
        coffreListener = new CoffreListener(this);
        barreListener = new BarreListener(this);
        resurrectionListener = new LutinPurificateurResurrection(this);

        getServer().getPluginManager().registerEvents(new ConfigListener(this), this);
        getServer().getPluginManager().registerEvents(coffreListener, this);
        getServer().getPluginManager().registerEvents(barreListener, this);
        getServer().getPluginManager().registerEvents(new DashListener(this), this);
        getServer().getPluginManager().registerEvents(new DoubleDamageListener(this), this);
        lutinShieldListener = new LutinShieldListener(this);
        getServer().getPluginManager().registerEvents(lutinShieldListener, this);
        getServer().getPluginManager().registerEvents(resurrectionListener, this);
        this.sanctuaireManager = new SanctuaireManager(this);
        getServer().getPluginManager().registerEvents(sanctuaireManager, this);
        getServer().getPluginManager().registerEvents(new TestListener(this), this);

        getServer().getPluginManager().registerEvents(new CutCleanScenario(), this);
        getServer().getPluginManager().registerEvents(new HastyBoysScenario(), this);
        getServer().getPluginManager().registerEvents(new NoStoneVariantScenario(), this);
        getServer().getPluginManager().registerEvents(new NotchAppleListener(), this);
    }

    public void setSanta(Player target) {
        if (target != null) {
            selectedSantaUUID = target.getUniqueId();
        }
    }

    public void toggleMeetup(Player player) {
        meetupEnabled = !meetupEnabled;
        String status = meetupEnabled ? "activé" : "désactivé";
        player.sendMessage(ChatColor.YELLOW + "Le mode meetup est maintenant " + status + " !");
    }

    public void startGame(Player player) {
        if (countdownActive) return;

        List<Player> playerList = playerManager.getOnlinePlayers();
        if (playerList.isEmpty()) return;

        countdownActive = true;

        Player santaPlayer;
        if (selectedSantaUUID != null) {
            santaPlayer = Bukkit.getPlayer(selectedSantaUUID);
            if (santaPlayer == null || !santaPlayer.isOnline()) {
                santaPlayer = playerList.get(new Random().nextInt(playerList.size()));
            }
        } else {
            santaPlayer = playerList.get(new Random().nextInt(playerList.size()));
        }

        final Player finalSanta = santaPlayer;

        new BukkitRunnable() {
            int count = 5;

            @Override
            public void run() {
                if (count > 0) {
                    messageManager.sendGameStartCountdown(count);
                    count--;
                } else {
                    this.cancel();
                    countdownActive = false;
                    gameState = GameState.PLAYING;
                    messageManager.sendGameStartGo();
                    initializeGame(finalSanta, playerList);
                }
            }
        }.runTaskTimer(this, 0L, 20L);
    }

    private void initializeGame(Player santaPlayer, List<Player> playerList) {
        String worldName = gameConfig.getWorldName();
        Bukkit.getWorld(worldName).setTime(0);

        DayCycleScenario dayCycle = new DayCycleScenario(this);
        dayCycle.startDayNightCycle();
        dayNumber = 0;
        timePassed = 0;

        if (timerRunnable != null) {
            timerRunnable.cancel();
        }
        timerRunnable = new BukkitRunnable() {
            @Override
            public void run() {
                timePassed++;
            }
        };
        timerRunnable.runTaskTimer(this, 0L, 20L);

        clearAll(santaPlayer);

        if (TEST_MODE_NO_LUTINSP) {
            roleManager.assignRolesNoPurificateurs(santaPlayer, playerList);
        } else {
            roleManager.assignRoles(santaPlayer, playerList);
        }

        messageManager.sendRoleReveal(santaPlayer, "santa");
        gearManager.giveSantaAbilities(santaPlayer);
        if (meetupEnabled) {
            gearManager.giveMeetupGear(santaPlayer);
        } else {
            gearManager.giveSantaGear(santaPlayer);
        }

        effectsManager.spawnTeleportEffect(santaPlayer);
        teleportPlayerToRandomLocation(santaPlayer);

        for (UUID uuid : roleManager.getLutinsPuri()) {
            Player p = Bukkit.getPlayer(uuid);
            if (p != null && p.isOnline()) {
                clearAll(p);
                messageManager.sendRoleReveal(p, "lutinpurificateur");
                effectsManager.spawnTeleportEffect(p);
                teleportPlayerToRandomLocation(p);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        gearManager.giveLutinPuriAbilities(p);
                        if (meetupEnabled) {
                            gearManager.giveMeetupGear(p);
                        } else {
                            gearManager.giveLutinGear(p);
                        }
                    }
                }.runTaskLater(BeatTheDS.this, 10 * 20);
            }
        }

        for (UUID uuid : roleManager.getLutins()) {
            Player p = Bukkit.getPlayer(uuid);
            if (p != null && p.isOnline()) {
                clearAll(p);
                messageManager.sendRoleReveal(p, "lutin");
                effectsManager.spawnTeleportEffect(p);
                teleportPlayerToRandomLocation(p);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        gearManager.giveLutinAbilities(p);
                        if (meetupEnabled) {
                            gearManager.giveMeetupGear(p);
                        } else {
                            gearManager.giveLutinGear(p);
                        }
                    }
                }.runTaskLater(BeatTheDS.this, 10 * 20);
            }
        }

        bossBarManager.createSantaHealthBar();
        this.sanctuaireManager.startCaptureSystem();
    }

    public void endGame() {
        gameState = GameState.ENDED;

        if (timerRunnable != null) {
            timerRunnable.cancel();
            timerRunnable = null;
        }

        bossBarManager.reset();

        Player santa = roleManager.getSantaPlayer();
        if (santa != null && santa.isOnline()) {
            messageManager.sendVictory("lutins");
            effectsManager.spawnVictoryFireworks(santa);
        } else {
            messageManager.sendVictory("santa");
        }

        messageManager.sendGameStats();

        new BukkitRunnable() {
            @Override
            public void run() {
                resetGame();
            }
        }.runTaskLater(this, 200L);
    }

    public void resetGame() {
        gameState = GameState.WAITING;
        meetupEnabled = false;
        selectedSantaUUID = null;
        timePassed = 0;
        dayNumber = 0;
        countdownActive = false;

        if (timerRunnable != null) {
            timerRunnable.cancel();
            timerRunnable = null;
        }

        roleManager.reset();
        playerManager.reset();
        resurrectionListener.reset();
        barreListener.reset();
        coffreListener.reset();
        if (lutinShieldListener != null) lutinShieldListener.reset();
        if (sanctuaireManager != null) sanctuaireManager.reset();
        bossBarManager.reset();

        for (Player p : Bukkit.getOnlinePlayers()) {
            clearAll(p);
            p.teleport(new Location(p.getWorld(),
                    gameConfig.getSpawnX(),
                    gameConfig.getSpawnY(),
                    gameConfig.getSpawnZ()));
            p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 255));
            p.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, Integer.MAX_VALUE, 255));
        }
    }

    public static void clearAll(Player player) {
        if (player == null) return;
        player.getInventory().clear();
        for (PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }
        player.setMaxHealth(20);
        player.setHealth(20);
        player.setFoodLevel(20);
        player.setSaturation(20);
        player.setGameMode(GameMode.SURVIVAL);
        player.getInventory().setArmorContents(null);
    }

    public void teleportPlayerToRandomLocation(Player player) {
        if (player == null) return;
        Random random = new Random();
        int x = random.nextInt(gameConfig.getTeleportMaxX() - gameConfig.getTeleportMinX()) + gameConfig.getTeleportMinX();
        int z = random.nextInt(gameConfig.getTeleportMaxZ() - gameConfig.getTeleportMinZ()) + gameConfig.getTeleportMinZ();
        player.addPotionEffect(new PotionEffect(
                PotionEffectType.DAMAGE_RESISTANCE,
                gameConfig.getTeleportResistanceDuration(),
                gameConfig.getTeleportResistanceLevel()));
        player.teleport(new Location(player.getWorld(), x, gameConfig.getTeleportY(), z));
    }

    public int getTimePassed() {
        return timePassed;
    }

    public int getDayNumber() {
        return dayNumber;
    }

    public void setDayNumber(int n) {
        dayNumber = n;
    }

    public GameState getGameState() {
        return gameState;
    }

    public String getPhase() {
        return gameState.name();
    }

    public GameConfig getGameConfig() {
        return gameConfig;
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    public RoleManager getRoleManager() {
        return roleManager;
    }

    public GearManager getGearManager() {
        return gearManager;
    }

    public EffectsManager getEffectsManager() {
        return effectsManager;
    }

    public MessageManager getMessageManager() {
        return messageManager;
    }

    public BossBarManager getBossBarManager() {
        return bossBarManager;
    }

    public boolean isMeetupEnabled() {
        return meetupEnabled;
    }

    public boolean isSanta(Player player) {
        return roleManager.isSanta(player);
    }

    public boolean isLutinNormal(Player player) {
        return roleManager.isLutinNormal(player);
    }

    public boolean isLutinPurificateur(Player player) {
        return roleManager.isLutinPurificateur(player);
    }

    public UUID getSantaUUID() {
        return roleManager.getSantaUUID();
    }

    public Player getSanta() {
        return roleManager.getSantaPlayer();
    }

    public void resetShieldCooldowns() {
        if (lutinShieldListener != null) {
            lutinShieldListener.resetCooldowns();
        }
    }

    public static BeatTheDS getInstance() {
        return instance;
    }
}

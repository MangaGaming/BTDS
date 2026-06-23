package com.mguhc.config;

import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public class GameConfig {

    private final FileConfiguration config;

    public GameConfig(FileConfiguration config) {
        this.config = config;
    }

    public int getPvpDelayMinutes() {
        return config.getInt("game.pvp-delay-minutes", 30);
    }

    public String getWorldName() {
        return config.getString("game.world-name", "world");
    }

    public int getSpawnX() {
        return config.getInt("game.spawn-x", 0);
    }

    public int getSpawnY() {
        return config.getInt("game.spawn-y", 80);
    }

    public int getSpawnZ() {
        return config.getInt("game.spawn-z", -6);
    }

    public int getSantaMaxHealth() {
        return config.getInt("santa.max-health", 40);
    }

    public List<String> getSantaEffects() {
        return config.getStringList("santa.effects");
    }

    public String getSantaHelmet() {
        return config.getString("santa.gear.helmet", "IRON_HELMET");
    }

    public String getSantaChestplate() {
        return config.getString("santa.gear.chestplate", "DIAMOND_CHESTPLATE");
    }

    public String getSantaLeggings() {
        return config.getString("santa.gear.leggings", "IRON_LEGGINGS");
    }

    public String getSantaBoots() {
        return config.getString("santa.gear.boots", "IRON_BOOTS");
    }

    public int getSantaPickaxeEnchantLevel() {
        return config.getInt("santa.gear.pickaxe-enchant-level", 3);
    }

    public int getSantaGoldenApples() {
        return config.getInt("santa.gear.golden-apples", 5);
    }

    public int getSantaFoodAmount() {
        return config.getInt("santa.gear.food-amount", 64);
    }

    public int getSantaBooks() {
        return config.getInt("santa.gear.books", 7);
    }

    public int getLutinExtraHealthChance() {
        return config.getInt("lutin.extra-health-chance", 15);
    }

    public int getLutinExtraHealthAmount() {
        return config.getInt("lutin.extra-health-amount", 40);
    }

    public int getLutinSpeedChance() {
        return config.getInt("lutin.speed-chance", 50);
    }

    public List<String> getLutinEffects() {
        return config.getStringList("lutin.effects");
    }

    public String getLutinHelmet() {
        return config.getString("lutin.gear.helmet", "IRON_HELMET");
    }

    public String getLutinChestplate() {
        return config.getString("lutin.gear.chestplate", "IRON_CHESTPLATE");
    }

    public String getLutinLeggings() {
        return config.getString("lutin.gear.leggings", "IRON_LEGGINGS");
    }

    public String getLutinBoots() {
        return config.getString("lutin.gear.boots", "IRON_BOOTS");
    }

    public int getLutinPickaxeEnchantLevel() {
        return config.getInt("lutin.gear.pickaxe-enchant-level", 3);
    }

    public int getLutinGoldenApples() {
        return config.getInt("lutin.gear.golden-apples", 3);
    }

    public int getLutinFoodAmount() {
        return config.getInt("lutin.gear.food-amount", 64);
    }

    public int getLutinBooks() {
        return config.getInt("lutin.gear.books", 3);
    }

    public int getLutinPuriPercentage() {
        return config.getInt("lutin-purificateur.percentage", 25);
    }

    public int getLutinPuriMaxHealth() {
        return config.getInt("lutin-purificateur.max-health", 30);
    }

    public int getLutinPuriMaxResurrections() {
        return config.getInt("lutin-purificateur.max-resurrections", 5);
    }

    public double getLutinPuriHealthLossPerResurrection() {
        return config.getDouble("lutin-purificateur.health-loss-per-resurrection", 2.0);
    }

    public List<String> getLutinPuriEffects() {
        return config.getStringList("lutin-purificateur.effects");
    }

    public String getMeetupHelmet() {
        return config.getString("meetup.gear.helmet", "DIAMOND_HELMET");
    }

    public int getMeetupHelmetProt() {
        return config.getInt("meetup.gear.helmet-prot", 3);
    }

    public String getMeetupChestplate() {
        return config.getString("meetup.gear.chestplate", "DIAMOND_CHESTPLATE");
    }

    public int getMeetupChestplateProt() {
        return config.getInt("meetup.gear.chestplate-prot", 2);
    }

    public String getMeetupLeggings() {
        return config.getString("meetup.gear.leggings", "DIAMOND_LEGGINGS");
    }

    public int getMeetupLeggingsProt() {
        return config.getInt("meetup.gear.leggings-prot", 3);
    }

    public String getMeetupBoots() {
        return config.getString("meetup.gear.boots", "DIAMOND_BOOTS");
    }

    public int getMeetupBootsProt() {
        return config.getInt("meetup.gear.boots-prot", 2);
    }

    public String getMeetupSword() {
        return config.getString("meetup.gear.sword", "DIAMOND_SWORD");
    }

    public int getMeetupSwordSharp() {
        return config.getInt("meetup.gear.sword-sharp", 3);
    }

    public String getMeetupPickaxe() {
        return config.getString("meetup.gear.pickaxe", "DIAMOND_PICKAXE");
    }

    public int getMeetupPickaxeEff() {
        return config.getInt("meetup.gear.pickaxe-eff", 3);
    }

    public int getMeetupGoldenApples() {
        return config.getInt("meetup.gear.golden-apples", 12);
    }

    public int getMeetupFoodAmount() {
        return config.getInt("meetup.gear.food-amount", 64);
    }

    public int getMeetupWoodAmount() {
        return config.getInt("meetup.gear.wood-amount", 64);
    }

    public boolean getMeetupBow() {
        return config.getBoolean("meetup.gear.bow", true);
    }

    public int getMeetupBowPower() {
        return config.getInt("meetup.gear.bow-power", 3);
    }

    public int getMeetupArrows() {
        return config.getInt("meetup.gear.arrows", 64);
    }

    public int getTeleportMinX() {
        return config.getInt("teleport.min-x", 400);
    }

    public int getTeleportMaxX() {
        return config.getInt("teleport.max-x", 601);
    }

    public int getTeleportMinZ() {
        return config.getInt("teleport.min-z", 400);
    }

    public int getTeleportMaxZ() {
        return config.getInt("teleport.max-z", 601);
    }

    public int getTeleportY() {
        return config.getInt("teleport.y", 100);
    }

    public int getTeleportResistanceDuration() {
        return config.getInt("teleport.resistance-duration", 100);
    }

    public int getTeleportResistanceLevel() {
        return config.getInt("teleport.resistance-level", 255);
    }

    public int getSanctuaryCaptureTicks() {
        return config.getInt("sanctuaries.capture-ticks", 5);
    }

    public double getSanctuarySecondsPerPointNormal() {
        return config.getDouble("sanctuaries.seconds-per-point-normal", 1.8);
    }

    public double getSanctuarySecondsPerPointNoir() {
        return config.getDouble("sanctuaries.seconds-per-point-noir", 3.0);
    }

    public int getSanctuaryX(String name) {
        return config.getInt("sanctuaries." + name + ".x", 0);
    }

    public int getSanctuaryY(String name) {
        return config.getInt("sanctuaries." + name + ".y", 4);
    }

    public int getSanctuaryZ(String name) {
        return config.getInt("sanctuaries." + name + ".z", 0);
    }

    public int getDarknessBarMaxHits() {
        return config.getInt("darkness-bar.max-hits", 300);
    }

    public int getChestX() {
        return config.getInt("chest.x", 0);
    }

    public int getChestY() {
        return config.getInt("chest.y", 108);
    }

    public int getChestZ() {
        return config.getInt("chest.z", -43);
    }

    public int getChestLockpickRangeXMin() {
        return config.getInt("chest.lockpick-range-x-min", -1);
    }

    public int getChestLockpickRangeXMax() {
        return config.getInt("chest.lockpick-range-x-max", 1);
    }

    public int getChestLockpickRangeZMin() {
        return config.getInt("chest.lockpick-range-z-min", -44);
    }

    public int getChestLockpickRangeZMax() {
        return config.getInt("chest.lockpick-range-z-max", -42);
    }

    public int getChestLockpickDurationSeconds() {
        return config.getInt("chest.lockpick-duration-seconds", 60);
    }

    public int getChestLockcheckIntervalTicks() {
        return config.getInt("chest.lockcheck-interval-ticks", 5);
    }

    public int getDayCycleDurationMinutes() {
        return config.getInt("day-cycle.duration-minutes", 15);
    }

    public int getDashCooldownMinutes() {
        return config.getInt("dash.cooldown-minutes", 3);
    }

    public double getDashPower() {
        return config.getDouble("dash.power", 5.0);
    }

    public double getDashYBoost() {
        return config.getDouble("dash.y-boost", 0.4);
    }

    public int getShieldDurationTicks() {
        return config.getInt("shield.duration-ticks", 200);
    }

    public int getShieldAmplifier() {
        return config.getInt("shield.amplifier", 100);
    }
}

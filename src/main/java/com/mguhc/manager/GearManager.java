package com.mguhc.manager;

import com.mguhc.config.GameConfig;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;
import java.util.Random;

public class GearManager {

    private final GameConfig config;

    public GearManager(GameConfig config) {
        this.config = config;
    }

    public void giveSantaGear(Player player) {
        if (player == null) return;

        player.getInventory().addItem(new ItemStack(Material.getMaterial(config.getSantaHelmet())));
        player.getInventory().addItem(new ItemStack(Material.getMaterial(config.getSantaChestplate())));
        player.getInventory().addItem(new ItemStack(Material.getMaterial(config.getSantaLeggings())));
        player.getInventory().addItem(new ItemStack(Material.getMaterial(config.getSantaBoots())));

        ItemStack pickaxe = new ItemStack(Material.DIAMOND_PICKAXE);
        ItemMeta pickaxeMeta = pickaxe.getItemMeta();
        pickaxeMeta.addEnchant(Enchantment.DIG_SPEED, config.getSantaPickaxeEnchantLevel(), true);
        pickaxe.setItemMeta(pickaxeMeta);
        player.getInventory().addItem(pickaxe);

        player.getInventory().addItem(new ItemStack(Material.GOLDEN_APPLE, config.getSantaGoldenApples()));
        player.getInventory().addItem(new ItemStack(Material.COOKED_BEEF, config.getSantaFoodAmount()));
        player.getInventory().addItem(new ItemStack(Material.BOOK, config.getSantaBooks()));
    }

    public void giveLutinGear(Player player) {
        if (player == null) return;

        player.getInventory().addItem(new ItemStack(Material.getMaterial(config.getLutinHelmet())));
        player.getInventory().addItem(new ItemStack(Material.getMaterial(config.getLutinChestplate())));
        player.getInventory().addItem(new ItemStack(Material.getMaterial(config.getLutinLeggings())));
        player.getInventory().addItem(new ItemStack(Material.getMaterial(config.getLutinBoots())));

        ItemStack pickaxe = new ItemStack(Material.DIAMOND_PICKAXE);
        ItemMeta pickaxeMeta = pickaxe.getItemMeta();
        pickaxeMeta.addEnchant(Enchantment.DIG_SPEED, config.getLutinPickaxeEnchantLevel(), true);
        pickaxe.setItemMeta(pickaxeMeta);
        player.getInventory().addItem(pickaxe);

        player.getInventory().addItem(new ItemStack(Material.COOKED_BEEF, config.getLutinFoodAmount()));
        player.getInventory().addItem(new ItemStack(Material.BOOK, config.getLutinBooks()));
        player.getInventory().addItem(new ItemStack(Material.GOLDEN_APPLE, config.getLutinGoldenApples()));
    }

    public void giveMeetupGear(Player player) {
        if (player == null) return;

        ItemStack helmet = new ItemStack(Material.getMaterial(config.getMeetupHelmet()));
        ItemMeta helmetMeta = helmet.getItemMeta();
        helmetMeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, config.getMeetupHelmetProt(), true);
        helmet.setItemMeta(helmetMeta);
        player.getInventory().setHelmet(helmet);

        ItemStack chestplate = new ItemStack(Material.getMaterial(config.getMeetupChestplate()));
        ItemMeta chestMeta = chestplate.getItemMeta();
        chestMeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, config.getMeetupChestplateProt(), true);
        chestplate.setItemMeta(chestMeta);
        player.getInventory().setChestplate(chestplate);

        ItemStack leggings = new ItemStack(Material.getMaterial(config.getMeetupLeggings()));
        ItemMeta legMeta = leggings.getItemMeta();
        legMeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, config.getMeetupLeggingsProt(), true);
        leggings.setItemMeta(legMeta);
        player.getInventory().setLeggings(leggings);

        ItemStack boots = new ItemStack(Material.getMaterial(config.getMeetupBoots()));
        ItemMeta bootsMeta = boots.getItemMeta();
        bootsMeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, config.getMeetupBootsProt(), true);
        boots.setItemMeta(bootsMeta);
        player.getInventory().setBoots(boots);

        ItemStack sword = new ItemStack(Material.getMaterial(config.getMeetupSword()));
        ItemMeta swordMeta = sword.getItemMeta();
        swordMeta.addEnchant(Enchantment.DAMAGE_ALL, config.getMeetupSwordSharp(), true);
        sword.setItemMeta(swordMeta);
        player.getInventory().addItem(sword);

        player.getInventory().addItem(new ItemStack(Material.GOLDEN_APPLE, config.getMeetupGoldenApples()));
        player.getInventory().addItem(new ItemStack(Material.COOKED_BEEF, config.getMeetupFoodAmount()));
        player.getInventory().addItem(new ItemStack(Material.WATER_BUCKET, 1));

        ItemStack pickaxe = new ItemStack(Material.getMaterial(config.getMeetupPickaxe()));
        ItemMeta pickaxeMeta = pickaxe.getItemMeta();
        pickaxeMeta.addEnchant(Enchantment.DIG_SPEED, config.getMeetupPickaxeEff(), true);
        pickaxe.setItemMeta(pickaxeMeta);
        player.getInventory().addItem(pickaxe);

        player.getInventory().addItem(new ItemStack(Material.WOOD, config.getMeetupWoodAmount()));

        if (config.getMeetupBow()) {
            ItemStack bow = new ItemStack(Material.BOW);
            ItemMeta bowMeta = bow.getItemMeta();
            bowMeta.addEnchant(Enchantment.ARROW_DAMAGE, config.getMeetupBowPower(), true);
            bow.setItemMeta(bowMeta);
            player.getInventory().addItem(bow);
            player.getInventory().addItem(new ItemStack(Material.ARROW, config.getMeetupArrows()));
        }
    }

    public void giveDashFeather(Player player) {
        if (player == null) return;
        ItemStack feather = new ItemStack(Material.FEATHER);
        ItemMeta meta = feather.getItemMeta();
        meta.setDisplayName(ChatColor.DARK_AQUA + "Dash");
        java.util.List<String> lore = new java.util.ArrayList<>();
        lore.add(ChatColor.GRAY + "Clic droit pour dash vers l'avant");
        lore.add(ChatColor.GRAY + "Cooldown: " + config.getDashCooldownMinutes() + " minutes");
        meta.setLore(lore);
        feather.setItemMeta(meta);
        player.getInventory().addItem(feather);
    }

    public void giveLutinShield(Player player) {
        if (player == null) return;
        ItemStack star = new ItemStack(Material.NETHER_STAR);
        ItemMeta meta = star.getItemMeta();
        meta.setDisplayName(ChatColor.LIGHT_PURPLE + "Bouclier Lutin");
        java.util.List<String> lore = new java.util.ArrayList<>();
        lore.add(ChatColor.GRAY + "Clic droit pour activer l'invincibilité");
        lore.add(ChatColor.GRAY + "Durée: " + (config.getShieldDurationTicks() / 20) + " secondes");
        lore.add(ChatColor.GRAY + "Recharge: une fois par jour");
        meta.setLore(lore);
        star.setItemMeta(meta);
        player.getInventory().addItem(star);
    }

    public void giveSantaAbilities(Player player) {
        if (player == null) return;
        player.setMaxHealth(config.getSantaMaxHealth());
        player.setHealth(config.getSantaMaxHealth());

        List<String> effects = config.getSantaEffects();
        for (String effectName : effects) {
            PotionEffectType type = PotionEffectType.getByName(effectName);
            if (type != null) {
                player.addPotionEffect(new PotionEffect(type, Integer.MAX_VALUE, 0));
            }
        }
    }

    public void giveLutinAbilities(Player player) {
        if (player == null) return;
        Random random = new Random();

        if (random.nextInt(100) < config.getLutinExtraHealthChance()) {
            player.setMaxHealth(config.getLutinExtraHealthAmount());
            player.setHealth(config.getLutinExtraHealthAmount());
        } else {
            List<String> effects = config.getLutinEffects();
            if (!effects.isEmpty()) {
                String effectName = effects.get(random.nextInt(effects.size()));
                PotionEffectType type = PotionEffectType.getByName(effectName);
                if (type != null) {
                    player.addPotionEffect(new PotionEffect(type, Integer.MAX_VALUE, 0));
                }
            }
        }

        if (player.hasPotionEffect(PotionEffectType.SPEED)) {
            giveDashFeather(player);
        }
        if (player.hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE)) {
            giveLutinShield(player);
        }
    }

    public void giveLutinPuriAbilities(Player player) {
        if (player == null) return;
        player.setMaxHealth(config.getLutinPuriMaxHealth());
        player.setHealth(config.getLutinPuriMaxHealth());

        List<String> effects = config.getLutinPuriEffects();
        if (!effects.isEmpty()) {
            Random random = new Random();
            String effectName = effects.get(random.nextInt(effects.size()));
            PotionEffectType type = PotionEffectType.getByName(effectName);
            if (type != null) {
                player.addPotionEffect(new PotionEffect(type, Integer.MAX_VALUE, 0));
            }
        }
    }
}

package com.mguhc.listeners;

import com.mguhc.BeatTheDS;
import net.minecraft.server.v1_8_R3.EntityInsentient;
import net.minecraft.server.v1_8_R3.EntityVillager;
import net.minecraft.server.v1_8_R3.MerchantRecipe;
import net.minecraft.server.v1_8_R3.MerchantRecipeList;
import net.minecraft.server.v1_8_R3.PathfinderGoalSelector;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftVillager;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BlocAmeListener implements Listener {

    private static final String ANGE_NAME = ChatColor.BLUE + "L'ange";
    private static final String EPEE_PURIFICATRICE = ChatColor.GOLD + "Epée Purificatrice";
    private static final String SPHERES_LUMIERES = ChatColor.AQUA + "Sphères de Lumières";
    private static final String POTION_DEMON = ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "Potion du Démon";

    private final BeatTheDS plugin;
    private boolean spawned = false;

    public BlocAmeListener(BeatTheDS plugin) {
        this.plugin = plugin;
        removeGlassRecipes();
    }

    private void removeGlassRecipes() {
        Iterator<Recipe> it = Bukkit.getServer().recipeIterator();
        while (it.hasNext()) {
            Recipe recipe = it.next();
            if (recipe.getResult().getType() == Material.GLASS) {
                it.remove();
            }
        }
    }

    private ItemStack createPotionDemon() {
        ItemStack potionItem = new ItemStack(Material.POTION, 1, (short) 8229);
        PotionMeta potionMeta = (PotionMeta) potionItem.getItemMeta();
        potionMeta.setDisplayName(POTION_DEMON);
        List<String> potionLore = new ArrayList<>();
        potionLore.add(ChatColor.RED + "Une potion dangereuse bénie par l'ange");
        potionLore.add(ChatColor.GRAY + "Absorption II (5min), Regen II (1min)");
        potionLore.add(ChatColor.GRAY + "Force I (5min) — Speed I si Force déjà active");
        potionMeta.setLore(potionLore);
        potionMeta.addCustomEffect(new PotionEffect(PotionEffectType.ABSORPTION, 6000, 1), true);
        potionMeta.addCustomEffect(new PotionEffect(PotionEffectType.REGENERATION, 1200, 1), true);
        potionItem.setItemMeta(potionMeta);
        return potionItem;
    }

    private void spawnAngeVillager() {
        Location loc = new Location(Bukkit.getWorlds().get(0), 53, 87, 30);

        Villager villager = (Villager) loc.getWorld().spawnEntity(loc, EntityType.VILLAGER);
        villager.setCustomName(ANGE_NAME);
        villager.setCustomNameVisible(true);
        villager.setRemoveWhenFarAway(false);
        villager.setMaxHealth(2048);
        villager.setHealth(2048);
        Bukkit.getLogger().info("[BTDS] L'ange spawné en " + loc.getBlockX() + " " + loc.getBlockY() + " " + loc.getBlockZ());

        CraftVillager craftVillager = (CraftVillager) villager;
        EntityVillager nmsVillager = craftVillager.getHandle();

        try {
            Field goalField = EntityInsentient.class.getDeclaredField("goalSelector");
            goalField.setAccessible(true);
            PathfinderGoalSelector goalSelector = (PathfinderGoalSelector) goalField.get(nmsVillager);
            Field goalSet = PathfinderGoalSelector.class.getDeclaredField("b");
            goalSet.setAccessible(true);
            ((java.util.LinkedHashSet<?>) goalSet.get(goalSelector)).clear();
        } catch (Exception e) {
        }

        try {
            Field targetField = EntityInsentient.class.getDeclaredField("targetSelector");
            targetField.setAccessible(true);
            PathfinderGoalSelector targetSelector = (PathfinderGoalSelector) targetField.get(nmsVillager);
            Field targetSet = PathfinderGoalSelector.class.getDeclaredField("b");
            targetSet.setAccessible(true);
            ((java.util.LinkedHashSet<?>) targetSet.get(targetSelector)).clear();
        } catch (Exception e) {
        }

        try {
            Field tradeListField = EntityVillager.class.getDeclaredField("br");
            tradeListField.setAccessible(true);
            MerchantRecipeList tradeList = new MerchantRecipeList();
            tradeListField.set(nmsVillager, tradeList);

            ItemStack netherStar1 = new ItemStack(Material.NETHER_STAR, 31);
            ItemStack epéeItem = new ItemStack(Material.GOLD_SWORD);
            ItemMeta epéeMeta = epéeItem.getItemMeta();
            epéeMeta.setDisplayName(EPEE_PURIFICATRICE);
            List<String> epéeLore = new ArrayList<>();
            epéeLore.add(ChatColor.YELLOW + "Une arme bénie par l'ange");
            epéeMeta.setLore(epéeLore);
            epéeMeta.addEnchant(Enchantment.DAMAGE_ALL, 7, true);
            epéeItem.setItemMeta(epéeMeta);

            tradeList.add(new MerchantRecipe(
                    CraftItemStack.asNMSCopy(netherStar1),
                    CraftItemStack.asNMSCopy(epéeItem)));

            ItemStack netherStar2 = new ItemStack(Material.NETHER_STAR, 31);
            ItemStack sphèresItem = new ItemStack(Material.SNOW_BALL, 16);
            ItemMeta sphèresMeta = sphèresItem.getItemMeta();
            sphèresMeta.setDisplayName(SPHERES_LUMIERES);
            List<String> sphèresLore = new ArrayList<>();
            sphèresLore.add(ChatColor.AQUA + "Des sphères de lumière pure");
            sphèresLore.add(ChatColor.GRAY + "Inflige un knockback dévastateur");
            sphèresMeta.setLore(sphèresLore);
            sphèresItem.setItemMeta(sphèresMeta);

            tradeList.add(new MerchantRecipe(
                    CraftItemStack.asNMSCopy(netherStar2),
                    CraftItemStack.asNMSCopy(sphèresItem)));

            ItemStack netherStar3 = new ItemStack(Material.NETHER_STAR, 31);
            tradeList.add(new MerchantRecipe(
                    CraftItemStack.asNMSCopy(netherStar3),
                    CraftItemStack.asNMSCopy(createPotionDemon())));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void reset() {
        spawned = false;
        for (Entity entity : Bukkit.getWorlds().get(0).getEntities()) {
            if (entity instanceof Villager && entity.getCustomName() != null
                    && entity.getCustomName().equals(ANGE_NAME)) {
                entity.remove();
            }
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (!spawned) {
            spawned = true;
            spawnAngeVillager();
        }
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof Player) return;
        if (entity instanceof Villager) return;
        if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.CUSTOM) return;

        Location loc = entity.getLocation();
        Location center = new Location(loc.getWorld(), 0, loc.getY(), 0);
        if (loc.distance(center) <= 75) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Villager) {
            if (event.getEntity().getCustomName() != null && event.getEntity().getCustomName().equals(ANGE_NAME)) {
                event.setCancelled(true);
                event.setDamage(0);
            }
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Snowball)) {
            if (event.getEntity() instanceof Villager) {
                if (event.getEntity().getCustomName() != null && event.getEntity().getCustomName().equals(ANGE_NAME)) {
                    event.setCancelled(true);
                    event.setDamage(0);
                }
            }
            return;
        }
        if (!(event.getDamager() instanceof Snowball)) return;
        if (!(event.getEntity() instanceof Player)) return;

        Snowball snowball = (Snowball) event.getDamager();
        if (!(snowball.getShooter() instanceof Player)) return;

        Player shooter = (Player) snowball.getShooter();
        Player hit = (Player) event.getEntity();

        ItemStack item = shooter.getItemInHand();
        if (item == null || item.getType() != Material.SNOW_BALL) return;
        if (!item.hasItemMeta() || !item.getItemMeta().hasDisplayName()) return;
        if (!item.getItemMeta().getDisplayName().equals(SPHERES_LUMIERES)) return;
        if (hit.equals(shooter)) return;

        event.setCancelled(true);

        Vector direction = hit.getLocation().toVector()
                .subtract(shooter.getLocation().toVector()).normalize().multiply(2.5).setY(1.0);
        hit.setVelocity(direction);
        hit.damage(4.0);
        hit.getWorld().playEffect(hit.getLocation(), org.bukkit.Effect.MOBSPAWNER_FLAMES, 0);

        int amount = item.getAmount() - 1;
        if (amount <= 0) {
            shooter.setItemInHand(new ItemStack(Material.AIR));
        } else {
            item.setAmount(amount);
        }
    }

    @EventHandler
    public void onConsume(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item == null || item.getType() != Material.POTION) return;
        if (!item.hasItemMeta() || !item.getItemMeta().hasDisplayName()) return;
        if (!item.getItemMeta().getDisplayName().equals(POTION_DEMON)) return;

        boolean isSanta = plugin.isSanta(player);

        player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 6000, 1), true);
        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 1200, 1), true);

        if (!isSanta) {
            if (player.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE)) {
                player.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 6000, 0), true);
            } else {
                player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 6000, 0), true);
            }
        }
    }
}

package com.niknuv.chestRef;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ChestManager {

    private final ChestRef plugin;

    private final Map<String, RefillableChest> chests = new HashMap<>();
    private final Map<String, BukkitTask> activeRefillTasks = new HashMap<>();

    public void addChest(String id, RefillableChest chest) {
        chests.put(id, chest);
    }

    public RefillableChest getChest(String id) {
        return chests.get(id);
    }

    public ChestManager(ChestRef plugin) {
        this.plugin = plugin;
    }

    public void registrarCofre(String id, Location location, long intervaloSegundos) {
        if (location.getBlock().getType() == Material.CHEST) {
            org.bukkit.block.Chest chestBlock = (org.bukkit.block.Chest) location.getBlock().getState();
            ItemStack[] originalContents = chestBlock.getInventory().getContents();
            ItemStack[] contentsCopy = new ItemStack[originalContents.length];

            for (int i = 0; i < originalContents.length; i++) {
                ItemStack item = originalContents[i];
                contentsCopy[i] = (item != null) ? item.clone() : null;
            }

            RefillableChest chest = new RefillableChest(id, location, contentsCopy, intervaloSegundos);
            chests.put(id, chest);
            scheduleRefill(chest);
            Bukkit.getLogger().info("Cofre '" + id + "' programado para recarga cada " + chest.getRefillInterval() + " segundos.");
        }
    }

    public RefillableChest getCofre(String id) {
        return chests.get(id);
    }

    public void rellenarCofre(String id) {
        RefillableChest chest = chests.get(id);
        if (chest == null) return;

        Location loc = chest.getLocation();

        Bukkit.getScheduler().runTask(plugin, () -> {
            if (loc.getBlock().getType() == Material.CHEST) {
                org.bukkit.block.Chest chestBlock = (org.bukkit.block.Chest) loc.getBlock().getState();
                chestBlock.getInventory().setContents(chest.getContents());
            }
        });
    }
    public Collection<RefillableChest> getAllChests() {
        return chests.values();  // Devuelve todos los cofres registrados
    }
    public void saveChests() {
        File file = new File(plugin.getDataFolder(), "chests.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        for (RefillableChest chest : chests.values()) {
            String path = "chests." + chest.getId();
            config.set(path + ".world", chest.getLocation().getWorld().getName());
            config.set(path + ".x", chest.getLocation().getBlockX());
            config.set(path + ".y", chest.getLocation().getBlockY());
            config.set(path + ".z", chest.getLocation().getBlockZ());
            config.set(path + ".interval", chest.getRefillInterval());

            // Guardar los ítems como lista
            List<String> items = new ArrayList<>();
            for (ItemStack item : chest.getContents()) {
                items.add(item.getType().toString() + ":" + item.getAmount());
            }
            config.set(path + ".contents", items);
        }

        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void loadChests() {
        File file = new File(plugin.getDataFolder(), "chests.yml");
        if (!file.exists()) return;

        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        if (!config.contains("chests")) return;

        for (String id : config.getConfigurationSection("chests").getKeys(false)) {
            String path = "chests." + id;
            String worldName = config.getString(path + ".world");
            int x = config.getInt(path + ".x");
            int y = config.getInt(path + ".y");
            int z = config.getInt(path + ".z");
            long interval = config.getLong(path + ".interval");

            // Cargar los contenidos del cofre
            List<String> itemsList = config.getStringList(path + ".contents");
            ItemStack[] contents = new ItemStack[itemsList.size()];

            for (int i = 0; i < itemsList.size(); i++) {
                String itemData = itemsList.get(i);
                String[] parts = itemData.split(":");
                Material material = Material.getMaterial(parts[0]);
                int amount = Integer.parseInt(parts[1]);
                contents[i] = new ItemStack(material, amount);
            }

            Location loc = new Location(Bukkit.getWorld(worldName), x, y, z);
            RefillableChest chest = new RefillableChest(id, loc, contents, interval);
            chests.put(id, chest);
        }
    }
    public void removeChest(String id) {
        if (chests.containsKey(id)) {
            chests.remove(id);
            saveChests();  // Guarda los cambios después de eliminar el cofre
            Bukkit.getLogger().info("Cofre '" + id + "' ha sido eliminado.");
        }
    }
    public String getChestIdByLocation(Location location) {
        for (Map.Entry<String, RefillableChest> entry : chests.entrySet()) {
            if (entry.getValue().getLocation().equals(location)) {
                return entry.getKey();
            }
        }
        return null;
    }
    public void scheduleRefill(RefillableChest chest) {
        String chestId = chest.getId();

        // Si ya hay una tarea programada, cancélala
        if (activeRefillTasks.containsKey(chestId)) {
            BukkitTask existingTask = activeRefillTasks.get(chestId);
            if (existingTask != null) {
                existingTask.cancel();
            }
        }

        // Actualizar el tiempo de última interacción
        chest.setLastInteractedTime(System.currentTimeMillis());

        long delayTicks = chest.getRefillInterval() * 20L; // Convertir segundos a ticks

        BukkitTask task = Bukkit.getScheduler().runTaskLater(plugin, () -> {
            rellenarCofre(chestId);
            activeRefillTasks.remove(chestId);
        }, delayTicks);

        activeRefillTasks.put(chestId, task);
    }
    public void cancelRefill(String chestId) {
        BukkitTask task = activeRefillTasks.get(chestId);
        if (task != null) {
            task.cancel();
            activeRefillTasks.remove(chestId);
        }
    }
}

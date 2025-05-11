package com.niknuv.chestRef;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class ChestRef extends JavaPlugin {
    private ChestManager chestManager;
    @Override
    public void onEnable() {
        chestManager = new ChestManager(this);
        getCommand("chestrefill").setExecutor(new ChestRefCommand(chestManager));
        getServer().getPluginManager().registerEvents(new ChestInteractionListener(chestManager), this);
        getLogger().info("ChestReff Habilitado");
        new ChestAutoRefiller(chestManager).runTaskTimer(this, 0L, 400L);
        chestManager.loadChests();
        if (!getDataFolder().exists()) getDataFolder().mkdir();
        checkAndCreateFile();
    }

    @Override
    public void onDisable() {
        getLogger().info("ChestReff Deshabilitado");
        chestManager.saveChests();
    }
    public void checkAndCreateFile() {
        File file = new File(getDataFolder(), "chests.yml"); // Usa getDataFolder() para obtener la carpeta del plugin
        if (!file.exists()) {
            saveResource("chests.yml", false);  // Crea el archivo si no existe
        }
    }
}

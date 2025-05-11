package com.niknuv.chestRef;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class ChestAutoRefiller extends BukkitRunnable {

    private final ChestManager chestManager;

    public ChestAutoRefiller(ChestManager chestManager) {
        this.chestManager = chestManager;
    }

    @Override
    public void run() {
        long currentTime = System.currentTimeMillis();

        for (RefillableChest chest : chestManager.getAllChests()) {
            long elapsed = (currentTime - chest.getLastInteractedTime()) / 1000;

            if (elapsed >= chest.getRefillInterval()) {
                chestManager.rellenarCofre(chest.getId());
                chest.setLastInteractedTime(currentTime); // Reiniciamos el tiempo
                //Bukkit.getLogger().info("Cofre '" + chest.getId() + "' fue rellenado automáticamente.");
            }
        }
    }
}

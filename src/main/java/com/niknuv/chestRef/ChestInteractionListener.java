package com.niknuv.chestRef;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

public class ChestInteractionListener implements Listener {

    private final ChestManager chestManager;

    public ChestInteractionListener(ChestManager chestManager) {
        this.chestManager = chestManager;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        // Asegúrate de que el jugador interactúa con un cofre
        if (event.getClickedInventory() == null) return;

        // Verificar que el cofre está registrado en la ubicación
        String chestId = getChestIdFromLocation(event.getWhoClicked().getLocation());
        if (chestId != null) {
            RefillableChest chest = chestManager.getChest(chestId);
            if (chest != null) {
                // Actualizar el tiempo de la última interacción
                chest.setLastInteractedTime(System.currentTimeMillis());
            }
        }
    }

    // Función para obtener el ID del cofre desde la ubicación
    private String getChestIdFromLocation(Location location) {
        for (RefillableChest chest : chestManager.getAllChests()) {
            if (chest.getLocation().equals(location)) {
                return chest.getId();
            }
        }
        return null;
    }
}

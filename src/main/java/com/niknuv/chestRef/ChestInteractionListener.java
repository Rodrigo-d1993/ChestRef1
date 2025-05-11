package com.niknuv.chestRef;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

public class ChestInteractionListener implements Listener {

    private final ChestManager chestManager;

    public ChestInteractionListener(ChestManager chestManager) {
        this.chestManager = chestManager;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getClickedInventory() == null) return;

        String chestId = getChestIdFromLocation(event.getWhoClicked().getLocation());
        if (chestId != null) {
            RefillableChest chest = chestManager.getChest(chestId);
            if (chest != null) {
                // Si un jugador hace clic en el cofre, iniciar el temporizador
                if (event.getAction() == InventoryAction.PICKUP_ALL ||
                        event.getAction() == InventoryAction.PICKUP_HALF ||
                        event.getAction() == InventoryAction.PICKUP_ONE) {
                    chestManager.scheduleRefill(chest);
                }
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

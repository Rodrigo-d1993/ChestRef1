package com.niknuv.chestRef;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

public class RefillableChest {

    private final String id;
    private final Location location;
    private final ItemStack[] contents;
    private final long refillInterval;
    private boolean autoRefill;
    private long lastInteractedTime;  // Nuevo atributo para almacenar el tiempo de la última interacción

    public RefillableChest(String id, Location location, ItemStack[] contents, long refillInterval) {
        this.id = id;
        this.location = location;
        this.contents = contents;
        this.refillInterval = refillInterval;
        this.autoRefill = false;
        this.lastInteractedTime = System.currentTimeMillis(); // Inicializa con el tiempo actual
    }

    public String getId() {
        return id;
    }

    public Location getLocation() {
        return location;
    }

    public ItemStack[] getContents() {
        return contents;
    }

    public long getRefillInterval() {
        return refillInterval;
    }

    public boolean isAutoRefill() {
        return autoRefill;
    }

    public void setAutoRefill(boolean autoRefill) {
        this.autoRefill = autoRefill;
    }

    // Métodos para obtener y establecer el tiempo de la última interacción
    public long getLastInteractedTime() {
        return lastInteractedTime;
    }

    public void setLastInteractedTime(long time) {
        this.lastInteractedTime = time;
    }
}
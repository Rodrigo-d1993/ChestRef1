package com.niknuv.chestRef;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ChestRefCommand implements CommandExecutor {

    private final ChestManager chestManager;

    public ChestRefCommand(ChestManager chestManager) {
        this.chestManager = chestManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "Uso: /chestrefill <add|fill> [argumentos]");
            return true;
        }

        String subcommand = args[0].toLowerCase();

        if (subcommand.equals("fill")) {
            if (args.length < 2) {
                sender.sendMessage(ChatColor.RED + "Uso: /chestrefill fill <id>");
                return true;
            }

            String id = args[1];
            if (chestManager.getCofre(id) == null) {
                sender.sendMessage(ChatColor.RED + "No existe un cofre con el ID '" + id + "'.");
                return true;
            }

            chestManager.rellenarCofre(id);
            sender.sendMessage(ChatColor.GREEN + "Cofre '" + id + "' rellenado correctamente.");
            return true;

        } else if (subcommand.equals("add")) {
            if (args.length < 7) {
                sender.sendMessage(ChatColor.RED + "Uso: /chestrefill add <id> <world> <x> <y> <z> <tiempoSegundos>");
                return true;
            }

            String id = args[1];
            String worldName = args[2];
            int x, y, z;
            long tiempo;

            try {
                x = Integer.parseInt(args[3]);
                y = Integer.parseInt(args[4]);
                z = Integer.parseInt(args[5]);
                tiempo = Long.parseLong(args[6]);
            } catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.RED + "Las coordenadas y el tiempo deben ser numéricos.");
                return true;
            }

            World world = Bukkit.getWorld(worldName);
            if (world == null) {
                sender.sendMessage(ChatColor.RED + "El mundo '" + worldName + "' no existe.");
                return true;
            }

            Location location = new Location(world, x, y, z);

            if (!(location.getBlock().getType() == Material.CHEST)) {
                sender.sendMessage(ChatColor.RED + "No hay un cofre en esa ubicación.");
                return true;
            }

            // Asegúrate de que el sender sea un jugador si quieres usar esto, o puedes quitar esta verificación
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "Solo los jugadores pueden usar este comando para registrar cofres.");
                return true;
            }

            Player player = (Player) sender;

            // Ahora obtenemos el contenido real del cofre en la ubicación, no el inventario del jugador
            org.bukkit.block.Chest chestBlock = (org.bukkit.block.Chest) location.getBlock().getState();
            ItemStack[] contents = chestBlock.getInventory().getContents();

            // Registra el cofre con el contenido real
            chestManager.registrarCofre(id, location, tiempo);

            sender.sendMessage(ChatColor.GREEN + "Cofre '" + id + "' registrado con éxito...");
            return true;
        } else if (subcommand.equals("remove")) {
            if (args.length < 2) {
                sender.sendMessage(ChatColor.RED + "Uso: /chestrefill remove <id>");
                return true;
            }

            String id = args[1];

            // Verificar si el cofre existe
            RefillableChest chest = chestManager.getCofre(id);
            if (chest == null) {
                sender.sendMessage(ChatColor.RED + "El cofre con ID '" + id + "' no está registrado.");
                return true;
            }

            // Eliminar el cofre de la lista
            chestManager.removeChest(id);
            sender.sendMessage(ChatColor.GREEN + "Cofre '" + id + "' eliminado correctamente.");

            return true;
        }return true;
    }
}
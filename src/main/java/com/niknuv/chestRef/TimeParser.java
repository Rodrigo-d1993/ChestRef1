package com.niknuv.chestRef;

public class TimeParser {

    // Aquí ahora solo devolvemos los segundos directamente
    public static long parseTime(String input) {
        try {
            return Long.parseLong(input);  // Retorna el número directamente como segundos
        } catch (NumberFormatException e) {
            return 0;  // Si no es válido, retornamos 0
        }
    }
}

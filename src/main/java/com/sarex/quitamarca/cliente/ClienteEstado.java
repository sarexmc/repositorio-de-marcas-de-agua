package com.sarex.quitamarca.cliente;

import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Estado solo de cliente: texturas ocultas (sincronizadas) y texturas vistas hace poco. */
public class ClienteEstado {

    public static class Visto {
        public int anchoPx, altoPx;
        public long ultimaVez;
    }

    public static volatile Set<ResourceLocation> ocultas = Set.of();
    public static final Map<ResourceLocation, Visto> vistos = new HashMap<>();

    public static void fijarOcultas(List<ResourceLocation> lista) {
        ocultas = new HashSet<>(lista);
    }

    public static void registrar(ResourceLocation tex, int anchoPx, int altoPx) {
        Visto v = vistos.computeIfAbsent(tex, k -> new Visto());
        v.anchoPx = anchoPx;
        v.altoPx = altoPx;
        v.ultimaVez = System.currentTimeMillis();
    }

    public static void limpiar() {
        ocultas = Set.of();
        vistos.clear();
    }
}

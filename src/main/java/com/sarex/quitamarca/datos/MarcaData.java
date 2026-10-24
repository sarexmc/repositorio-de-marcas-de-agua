package com.sarex.quitamarca.datos;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.LinkedHashSet;
import java.util.Set;

/** Lista de texturas ocultas, guardada en el mundo del servidor. */
public class MarcaData extends SavedData {
    private final Set<ResourceLocation> ocultas = new LinkedHashSet<>();

    public static MarcaData get(MinecraftServer server) {
        return server.overworld().getDataStorage()
                .computeIfAbsent(MarcaData::load, MarcaData::new, "quitamarca_ocultas");
    }

    public static MarcaData load(CompoundTag tag) {
        MarcaData d = new MarcaData();
        ListTag lista = tag.getList("ocultas", Tag.TAG_STRING);
        for (int i = 0; i < lista.size(); i++) {
            ResourceLocation r = ResourceLocation.tryParse(lista.getString(i));
            if (r != null) d.ocultas.add(r);
        }
        return d;
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        ListTag lista = new ListTag();
        for (ResourceLocation r : ocultas) lista.add(StringTag.valueOf(r.toString()));
        tag.put("ocultas", lista);
        return tag;
    }

    public Set<ResourceLocation> getOcultas() { return ocultas; }

    public boolean ocultar(ResourceLocation r) {
        boolean cambio = ocultas.add(r);
        if (cambio) setDirty();
        return cambio;
    }

    public boolean mostrar(ResourceLocation r) {
        boolean cambio = ocultas.remove(r);
        if (cambio) setDirty();
        return cambio;
    }

    public void limpiar() {
        if (!ocultas.isEmpty()) {
            ocultas.clear();
            setDirty();
        }
    }
}

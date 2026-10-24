package com.sarex.quitamarca.red;

import com.sarex.quitamarca.cliente.ClienteEstado;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.List;
import java.util.function.Supplier;

/** Servidor -> cliente: lista completa de texturas ocultas. */
public record SyncPacket(List<ResourceLocation> lista) {

    public static void encode(SyncPacket p, FriendlyByteBuf buf) {
        buf.writeCollection(p.lista, FriendlyByteBuf::writeResourceLocation);
    }

    public static SyncPacket decode(FriendlyByteBuf buf) {
        return new SyncPacket(buf.readList(FriendlyByteBuf::readResourceLocation));
    }

    public static void handle(SyncPacket p, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() ->
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClienteEstado.fijarOcultas(p.lista())));
        ctx.get().setPacketHandled(true);
    }
}

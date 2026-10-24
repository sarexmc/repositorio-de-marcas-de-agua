package com.sarex.quitamarca.red;

import com.sarex.quitamarca.QuitaMarca;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.Optional;

public class Red {
    private static final String VERSION = "1";

    // acceptMissingOr: un cliente sin el mod puede entrar, pero no verá nada oculto.
    public static final SimpleChannel CANAL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(QuitaMarca.MODID, "main"),
            () -> VERSION,
            NetworkRegistry.acceptMissingOr(VERSION),
            NetworkRegistry.acceptMissingOr(VERSION));

    public static void registrar() {
        CANAL.registerMessage(0, SyncPacket.class, SyncPacket::encode, SyncPacket::decode,
                SyncPacket::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
    }
}

package com.sarex.quitamarca;

import com.sarex.quitamarca.red.Red;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/**
 * QuitaMarca: lista las texturas (PNG) que se dibujan en pantalla y permite
 * ocultarlas para todos los jugadores del servidor con /marca ocultar.
 */
@Mod(QuitaMarca.MODID)
public class QuitaMarca {
    public static final String MODID = "quitamarca";

    public QuitaMarca() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        modBus.addListener((net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent e) ->
                e.enqueueWork(Red::registrar));
    }
}

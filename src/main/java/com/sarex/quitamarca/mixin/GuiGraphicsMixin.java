package com.sarex.quitamarca.mixin;

import com.sarex.quitamarca.cliente.ClienteEstado;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiGraphics.class)
public abstract class GuiGraphicsMixin {

    /** Tamaño (en píxeles reales) a partir del cual se elimina cualquier imagen. */
    private static final int LIMITE_PX = 2000;

    @Inject(method = "innerBlit(Lnet/minecraft/resources/ResourceLocation;IIIIIFFFF)V",
            at = @At("HEAD"), cancellable = true)
    private void quitamarca$filtrar(ResourceLocation tex, int x1, int x2, int y1, int y2,
                                    int z, float u0, float u1, float v0, float v1,
                                    CallbackInfo ci) {
        double escala = Minecraft.getInstance().getWindow().getGuiScale();
        double anchoPx = Math.abs(x2 - x1) * escala;
        double altoPx = Math.abs(y2 - y1) * escala;

        // Para /marcaescanear: se apuntan las texturas que no son de vanilla.
        if (!"minecraft".equals(tex.getNamespace())) {
            ClienteEstado.registrar(tex, (int) anchoPx, (int) altoPx);
        }

        if (ClienteEstado.ocultas.contains(tex) || anchoPx >= LIMITE_PX || altoPx >= LIMITE_PX) {
            ci.cancel();
        }
    }
}

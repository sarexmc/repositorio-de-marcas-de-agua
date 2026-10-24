package com.sarex.quitamarca.cliente;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.sarex.quitamarca.QuitaMarca;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.ChatFormatting;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Map;

@Mod.EventBusSubscriber(modid = QuitaMarca.MODID, value = Dist.CLIENT)
public class EventosCliente {

    @SubscribeEvent
    public static void alRegistrarComandos(RegisterClientCommandsEvent e) {
        e.getDispatcher().register(Commands.literal("marcaescanear")
                .executes(ctx -> escanear(ctx, 5))
                .then(Commands.argument("segundos", IntegerArgumentType.integer(1, 60))
                        .executes(ctx -> escanear(ctx, IntegerArgumentType.getInteger(ctx, "segundos")))));
    }

    private static int escanear(CommandContext<CommandSourceStack> ctx, int segundos) {
        long desde = System.currentTimeMillis() - segundos * 1000L;
        int total = 0;

        ctx.getSource().sendSuccess(() -> Component.literal(
                "Texturas dibujadas en pantalla (ultimos " + segundos + " s, sin las de minecraft):")
                .withStyle(ChatFormatting.GOLD), false);

        for (Map.Entry<ResourceLocation, ClienteEstado.Visto> en : ClienteEstado.vistos.entrySet()) {
            ResourceLocation id = en.getKey();
            ClienteEstado.Visto v = en.getValue();
            if (v.ultimaVez < desde) continue;
            total++;

            boolean oculta = ClienteEstado.ocultas.contains(id);
            MutableComponent linea = Component.literal("- " + id + "  " + v.anchoPx + "x" + v.altoPx + " px  ")
                    .withStyle(ChatFormatting.GRAY);
            if (oculta) {
                linea.append(Component.literal("[oculta - mostrar]").withStyle(s -> s
                        .withColor(ChatFormatting.GREEN)
                        .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/marca mostrar " + id))
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                Component.literal("Volver a mostrarla para todos")))));
            } else {
                linea.append(Component.literal("[ocultar para todos]").withStyle(s -> s
                        .withColor(ChatFormatting.RED)
                        .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/marca ocultar " + id))
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                Component.literal("Ponerla en false para todo el servidor (requiere OP)")))));
            }
            ctx.getSource().sendSuccess(() -> linea, false);
        }

        if (total == 0) {
            ctx.getSource().sendSuccess(() -> Component.literal(
                    "No se vio ninguna textura. Ten abierta la pantalla con la marca de agua y repite.")
                    .withStyle(ChatFormatting.YELLOW), false);
        }
        return total;
    }

    @SubscribeEvent
    public static void alSalir(ClientPlayerNetworkEvent.LoggingOut e) {
        ClienteEstado.limpiar();
    }
}

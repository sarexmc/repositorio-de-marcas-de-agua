package com.sarex.quitamarca;

import com.mojang.brigadier.context.CommandContext;
import com.sarex.quitamarca.datos.MarcaData;
import com.sarex.quitamarca.red.Red;
import com.sarex.quitamarca.red.SyncPacket;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

import java.util.ArrayList;

@Mod.EventBusSubscriber(modid = QuitaMarca.MODID)
public class EventosServidor {

    @SubscribeEvent
    public static void alRegistrarComandos(RegisterCommandsEvent e) {
        e.getDispatcher().register(Commands.literal("marca")
                .requires(s -> s.hasPermission(2))
                .then(Commands.literal("ocultar")
                        .then(Commands.argument("textura", ResourceLocationArgument.id())
                                .executes(EventosServidor::ocultar)))
                .then(Commands.literal("mostrar")
                        .then(Commands.argument("textura", ResourceLocationArgument.id())
                                .suggests((ctx, b) -> SharedSuggestionProvider.suggestResource(
                                        MarcaData.get(ctx.getSource().getServer()).getOcultas(), b))
                                .executes(EventosServidor::mostrar)))
                .then(Commands.literal("lista").executes(EventosServidor::lista))
                .then(Commands.literal("limpiar").executes(EventosServidor::limpiar)));
    }

    private static int ocultar(CommandContext<CommandSourceStack> ctx) {
        ResourceLocation id = ResourceLocationArgument.getId(ctx, "textura");
        if (id.getNamespace().equals("minecraft")) {
            ctx.getSource().sendFailure(Component.literal(
                    "No se pueden ocultar texturas de minecraft (rompería el HUD)."));
            return 0;
        }
        MinecraftServer server = ctx.getSource().getServer();
        boolean nuevo = MarcaData.get(server).ocultar(id);
        sincronizar(server);
        ctx.getSource().sendSuccess(() -> Component.literal(
                nuevo ? "Textura oculta para todos: " + id : "Ya estaba oculta: " + id), true);
        return 1;
    }

    private static int mostrar(CommandContext<CommandSourceStack> ctx) {
        ResourceLocation id = ResourceLocationArgument.getId(ctx, "textura");
        MinecraftServer server = ctx.getSource().getServer();
        boolean quitada = MarcaData.get(server).mostrar(id);
        sincronizar(server);
        ctx.getSource().sendSuccess(() -> Component.literal(
                quitada ? "Textura visible otra vez: " + id : "No estaba oculta: " + id), true);
        return quitada ? 1 : 0;
    }

    private static int lista(CommandContext<CommandSourceStack> ctx) {
        var ocultas = MarcaData.get(ctx.getSource().getServer()).getOcultas();
        if (ocultas.isEmpty()) {
            ctx.getSource().sendSuccess(() -> Component.literal("No hay texturas ocultas."), false);
        } else {
            ctx.getSource().sendSuccess(() -> Component.literal(
                    "Texturas ocultas (" + ocultas.size() + "): " + String.join(", ",
                            ocultas.stream().map(ResourceLocation::toString).toList())), false);
        }
        return ocultas.size();
    }

    private static int limpiar(CommandContext<CommandSourceStack> ctx) {
        MinecraftServer server = ctx.getSource().getServer();
        MarcaData.get(server).limpiar();
        sincronizar(server);
        ctx.getSource().sendSuccess(() -> Component.literal("Todas las texturas vuelven a verse."), true);
        return 1;
    }

    private static void sincronizar(MinecraftServer server) {
        Red.CANAL.send(PacketDistributor.ALL.noArg(),
                new SyncPacket(new ArrayList<>(MarcaData.get(server).getOcultas())));
    }

    @SubscribeEvent
    public static void alEntrar(PlayerEvent.PlayerLoggedInEvent e) {
        if (e.getEntity() instanceof ServerPlayer sp) {
            Red.CANAL.send(PacketDistributor.PLAYER.with(() -> sp),
                    new SyncPacket(new ArrayList<>(MarcaData.get(sp.server).getOcultas())));
        }
    }
}

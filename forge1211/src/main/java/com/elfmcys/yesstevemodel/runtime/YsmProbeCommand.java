package com.elfmcys.yesstevemodel.runtime;

import com.elfmcys.yesstevemodel.YesSteveModel;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;

public final class YsmProbeCommand {
    private static boolean registered;

    private YsmProbeCommand() {
    }

    public static synchronized void register() {
        if (registered) {
            return;
        }

        RegisterClientCommandsEvent.BUS.addListener(YsmProbeCommand::registerCommand);
        registered = true;
    }

    private static void registerCommand(RegisterClientCommandsEvent event) {
        event.getDispatcher().register(
            Commands.literal("ysm_probe")
                .executes(context -> probeCurrent(context.getSource()))
                .then(
                    Commands.argument("pack", StringArgumentType.greedyString())
                        .suggests(YsmProbeCommand::suggestPacks)
                        .executes(context -> probePack(context.getSource(), StringArgumentType.getString(context, "pack")))
                )
        );
    }

    private static int probeCurrent(CommandSourceStack source) {
        YsmActiveSelection selection = YsmClientRuntime.activeSelection();
        if (selection == null || !selection.runtimeApplied()) {
            source.sendFailure(Component.literal("No YSM pack is currently active."));
            return 0;
        }

        return reportSelection(source, selection);
    }

    private static int probePack(CommandSourceStack source, String packTarget) {
        return YsmModelProbe.resolveDescriptor(packTarget).map(descriptor -> {
            YsmClientRuntime.apply(descriptor, descriptor.defaultTextureId());
            YsmActiveSelection selection = YsmClientRuntime.activeSelection();
            if (selection == null || !selection.runtimeApplied()) {
                source.sendFailure(Component.literal("YSM did not keep an active model after applying " + descriptor.id()));
                return 0;
            }
            if (!selection.compiledPack().descriptor().id().equals(descriptor.id())) {
                source.sendFailure(
                    Component.literal(
                        "Requested " + descriptor.id() + " but runtime kept " + selection.compiledPack().descriptor().id()
                    )
                );
                return 0;
            }

            return reportSelection(source, selection);
        }).orElseGet(() -> {
            source.sendFailure(Component.literal("Unknown YSM pack: " + packTarget));
            return 0;
        });
    }

    private static int reportSelection(CommandSourceStack source, YsmActiveSelection selection) {
        try {
            YsmModelProbe.YsmModelProbeStats stats = YsmModelProbe.inspect(selection);
            String summary = String.format(
                "YSM probe %s [%s]: bones=%d faces=%d surfaces=%d objects=%d roots=%d",
                stats.packId(),
                stats.textureId(),
                stats.bones(),
                stats.faces(),
                stats.surfaces(),
                stats.objects(),
                stats.rootBones()
            );
            source.sendSuccess(() -> Component.literal(summary), false);
            YesSteveModel.oOoOoO0OoOOoo00ooO0oO00o.info(summary);
            return Command.SINGLE_SUCCESS;
        } catch (IOException exception) {
            source.sendFailure(Component.literal("YSM probe failed: " + exception.getMessage()));
            YesSteveModel.oOoOoO0OoOOoo00ooO0oO00o.error("YSM probe failed", exception);
            return 0;
        }
    }

    private static CompletableFuture<Suggestions> suggestPacks(
        CommandContext<CommandSourceStack> context,
        SuggestionsBuilder builder
    ) {
        for (YsmPackDescriptor descriptor : YsmClientRuntime.packs()) {
            builder.suggest(descriptor.id());
            if (!descriptor.legacyModelId().equals(descriptor.id())) {
                builder.suggest(descriptor.legacyModelId());
            }
        }
        return builder.buildFuture();
    }
}

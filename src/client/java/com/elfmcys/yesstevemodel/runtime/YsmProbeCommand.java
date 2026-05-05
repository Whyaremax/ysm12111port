package com.elfmcys.yesstevemodel.runtime;

import com.elfmcys.yesstevemodel.YesSteveModel;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;

public final class YsmProbeCommand {
    private YsmProbeCommand() {
    }

    public static void register() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(
            ClientCommandManager.literal("ysm_probe")
                .executes(context -> probeCurrent(context.getSource()))
                .then(
                    ClientCommandManager.argument("pack", StringArgumentType.greedyString())
                        .suggests(YsmProbeCommand::suggestPacks)
                        .executes(context -> probePack(context.getSource(), StringArgumentType.getString(context, "pack")))
                )
        ));
    }

    private static int probeCurrent(FabricClientCommandSource source) {
        YsmActiveSelection selection = YsmClientRuntime.activeSelection();
        if (selection == null || !selection.runtimeApplied()) {
            source.sendError(Text.literal("No YSM pack is currently active."));
            return 0;
        }

        return reportSelection(source, selection);
    }

    private static int probePack(FabricClientCommandSource source, String packTarget) {
        return YsmModelProbe.resolveDescriptor(packTarget).map(descriptor -> {
            YsmClientRuntime.apply(descriptor, descriptor.defaultTextureId());
            YsmActiveSelection selection = YsmClientRuntime.activeSelection();
            if (selection == null || !selection.runtimeApplied()) {
                source.sendError(Text.literal("YSM did not keep an active model after applying " + descriptor.id()));
                return 0;
            }
            if (!selection.compiledPack().descriptor().id().equals(descriptor.id())) {
                source.sendError(
                    Text.literal(
                        "Requested " + descriptor.id() + " but runtime kept " + selection.compiledPack().descriptor().id()
                    )
                );
                return 0;
            }

            return reportSelection(source, selection);
        }).orElseGet(() -> {
            source.sendError(Text.literal("Unknown YSM pack: " + packTarget));
            return 0;
        });
    }

    private static int reportSelection(FabricClientCommandSource source, YsmActiveSelection selection) {
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
            source.sendFeedback(Text.literal(summary));
            YesSteveModel.oOoOoO0OoOOoo00ooO0oO00o.info(summary);
            return Command.SINGLE_SUCCESS;
        } catch (IOException exception) {
            source.sendError(Text.literal("YSM probe failed: " + exception.getMessage()));
            YesSteveModel.oOoOoO0OoOOoo00ooO0oO00o.error("YSM probe failed", exception);
            return 0;
        }
    }

    private static CompletableFuture<Suggestions> suggestPacks(
        CommandContext<FabricClientCommandSource> context,
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

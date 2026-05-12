package com.vomiter.piglinhuntfix;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

@EventBusSubscriber(modid = PiglinHuntFix.MOD_ID)
public class Config {

    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    // ===== ModConfigSpec values =====
    private static final ModConfigSpec.BooleanValue PIGLIN_USE_SPECTRAL_ARROW_VALUE;
    private static final ModConfigSpec.BooleanValue GLOWING_HOGLIN_BROADCAST_HUNTING_VALUE;

    // ===== Cached primitives =====
    public static boolean PIGLIN_USE_SPECTRAL_ARROW;
    public static boolean GLOWING_HOGLIN_BROADCAST_HUNTING;

    static {
        BUILDER.push("general");

        PIGLIN_USE_SPECTRAL_ARROW_VALUE = BUILDER
                .comment("Allow piglins to use spectral arrows.")
                .define("piglinUseSpectralArrow", true);

        GLOWING_HOGLIN_BROADCAST_HUNTING_VALUE = BUILDER
                .comment("Allow glowing hoglins to broadcast hunting signal.")
                .define("glowingHoglinBroadcastHunting", true);

        BUILDER.pop();
    }

    static final ModConfigSpec SPEC = BUILDER.build();

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        if (event.getConfig().getSpec() != SPEC) return;

        // cache values
        PIGLIN_USE_SPECTRAL_ARROW = PIGLIN_USE_SPECTRAL_ARROW_VALUE.get();
        GLOWING_HOGLIN_BROADCAST_HUNTING = GLOWING_HOGLIN_BROADCAST_HUNTING_VALUE.get();
    }
}
package com.vomiter.piglinhuntfix;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod.EventBusSubscriber(modid = PiglinHuntFix.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config {

    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    // ===== ForgeConfigSpec values =====
    private static final ForgeConfigSpec.BooleanValue PIGLIN_USE_SPECTRAL_ARROW_VALUE;
    private static final ForgeConfigSpec.BooleanValue GLOWING_HOGLIN_BROADCAST_HUNTING_VALUE;

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

    static final ForgeConfigSpec SPEC = BUILDER.build();

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        if (event.getConfig().getSpec() != SPEC) return;

        // cache values
        PIGLIN_USE_SPECTRAL_ARROW = PIGLIN_USE_SPECTRAL_ARROW_VALUE.get();
        GLOWING_HOGLIN_BROADCAST_HUNTING = GLOWING_HOGLIN_BROADCAST_HUNTING_VALUE.get();
    }
}
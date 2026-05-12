package com.vomiter.piglinhuntfix;

import com.mojang.logging.LogUtils;
import com.vomiter.piglinhuntfix.common.event.EventHandler;
import com.vomiter.piglinhuntfix.data.ModDataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.IExtensionPoint;
import org.slf4j.Logger;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddReloadListenerEvent;


@Mod(PiglinHuntFix.MOD_ID)
public class PiglinHuntFix
{
    // Define mod id in a common place for everything to reference
    public static final String MOD_ID = "piglinhuntfix";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static ResourceLocation modLoc(String path){
        return PHFHelpers.id(PiglinHuntFix.MOD_ID, path);
    }

    public PiglinHuntFix(ModContainer mod, IEventBus modBus) {
        EventHandler.init();
        modBus.addListener(this::commonSetup);
        modBus.addListener(ModDataGenerator::generateData);
        mod.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
    }

}

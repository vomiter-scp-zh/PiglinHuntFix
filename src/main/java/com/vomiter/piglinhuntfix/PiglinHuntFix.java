package com.vomiter.piglinhuntfix;

import com.mojang.logging.LogUtils;
import com.vomiter.piglinhuntfix.common.event.EventHandler;
import com.vomiter.piglinhuntfix.data.ModDataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkConstants;
import org.slf4j.Logger;

@Mod(PiglinHuntFix.MOD_ID)
public class PiglinHuntFix
{
    // Define mod id in a common place for everything to reference
    public static final String MOD_ID = "piglinhuntfix";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static ResourceLocation modLoc(String path){
        return PHFHelpers.id(PiglinHuntFix.MOD_ID, path);
    }

    public PiglinHuntFix(FMLJavaModLoadingContext context) {
        EventHandler.init();
        IEventBus modBus = context.getModEventBus();
        modBus.addListener(this::commonSetup);
        modBus.addListener(ModDataGenerator::generateData);
        context.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
        context.registerExtensionPoint(
                IExtensionPoint.DisplayTest.class,
                () -> new IExtensionPoint.DisplayTest(
                        () -> NetworkConstants.IGNORESERVERONLY,
                        (remoteVersionString, isServer) -> true
                )
        );
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
    }

}

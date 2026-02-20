package com.vomiter.piglinhuntfix;

import net.minecraft.resources.ResourceLocation;

public class PHFHelpers {
    public static ResourceLocation id(String namespace, String path){
        return new ResourceLocation(namespace, path);
    }

    public static ResourceLocation id(String path){
        return id(PiglinHuntFix.MOD_ID, path);
    }
}

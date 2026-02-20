package com.vomiter.piglinhuntfix.mixin;

import com.vomiter.piglinhuntfix.core.PHFCore;
import net.minecraft.world.entity.ai.behavior.OneShot;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.monster.piglin.StartHuntingHoglin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(StartHuntingHoglin.class)
public abstract class StartHuntingMixin {

    @Inject(method = "create", at = @At("HEAD"), cancellable = true)
    private static void overrideCreate(CallbackInfoReturnable<OneShot<Piglin>> cir) {
        cir.setReturnValue(PHFCore.createStartHunting());
    }

}

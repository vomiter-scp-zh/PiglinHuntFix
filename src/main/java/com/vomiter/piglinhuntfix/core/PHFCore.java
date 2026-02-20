package com.vomiter.piglinhuntfix.core;

import com.vomiter.piglinhuntfix.accessor.PiglinAIMethodAccessor;
import net.minecraft.world.entity.ai.behavior.OneShot;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.monster.hoglin.Hoglin;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;
import net.minecraft.world.entity.monster.piglin.Piglin;

public class PHFCore {
    public static OneShot<Piglin> createStartHunting() {
        return BehaviorBuilder.create((ctx) -> {
            return ctx.group(
                    ctx.present(MemoryModuleType.NEAREST_VISIBLE_HUNTABLE_HOGLIN),
                    ctx.absent(MemoryModuleType.ANGRY_AT),
                    ctx.absent(MemoryModuleType.HUNTED_RECENTLY),
                    ctx.registered(MemoryModuleType.NEAREST_VISIBLE_ADULT_PIGLINS)
            ).apply(ctx, (huntableHoglin, angryAt, huntedRecently, nearbyAdultPiglins) -> {
                return (level, piglin, gameTime) -> {
                    if (piglin.isBaby()) return false;

                    // ✅ 修正點：Optional<Boolean> 要取「值」，不是取「是否存在」
                    final boolean anyNearbyAdultHuntedRecently = ctx.tryGet(nearbyAdultPiglins)
                            .map(list -> list.stream().anyMatch(PHFCore::hasHuntedRecently))
                            .orElse(false);

                    if (anyNearbyAdultHuntedRecently) return false;

                    Hoglin hoglin = ctx.get(huntableHoglin);
                    PiglinAIMethodAccessor.accessSetAngerTarget(piglin, hoglin);
                    PiglinAIMethodAccessor.accessDontKillAnyMoreHoglinsForAWhile(piglin);
                    PiglinAIMethodAccessor.accessBroadcastAngerTarget(piglin, hoglin);

                    ctx.tryGet(nearbyAdultPiglins).ifPresent((list) -> {
                        list.forEach(PiglinAIMethodAccessor::accessDontKillAnyMoreHoglinsForAWhile);
                    });

                    return true;
                };
            });
        });
    }

    private static boolean hasHuntedRecently(AbstractPiglin piglin) {
        return piglin.getBrain().hasMemoryValue(MemoryModuleType.HUNTED_RECENTLY);
    }

}

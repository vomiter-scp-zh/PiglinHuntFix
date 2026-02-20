package com.vomiter.piglinhuntfix.accessor;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;
import net.minecraft.world.entity.monster.piglin.PiglinAi;

public class PiglinAIMethodAccessor extends PiglinAi {
    public static void accessSetAngerTarget(AbstractPiglin p_34925_, LivingEntity p_34926_){
        setAngerTarget(p_34925_, p_34926_);
    }

    public static void accessDontKillAnyMoreHoglinsForAWhile(AbstractPiglin p_34923_){
        dontKillAnyMoreHoglinsForAWhile(p_34923_);
    }

    public static void accessBroadcastAngerTarget(AbstractPiglin p_34896_, LivingEntity p_34897_) {
        broadcastAngerTarget(p_34896_, p_34897_);
    }


}

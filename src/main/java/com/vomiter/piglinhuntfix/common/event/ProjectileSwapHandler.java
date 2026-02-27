package com.vomiter.piglinhuntfix.common.event;

import com.vomiter.piglinhuntfix.Config;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.projectile.SpectralArrow;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;

public class ProjectileSwapHandler {

    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        if(!Config.PIGLIN_USE_SPECTRAL_ARROW) return;
        final Level level = event.getLevel();
        if (level.isClientSide) return;

        final Entity e = event.getEntity();

        // 只處理「普通箭」；避免把 spectral arrow 自己再換一次造成遞迴
        if (!(e instanceof Arrow arrow)) return;
        if (e instanceof SpectralArrow) return; // 理論上不會成立（Arrow != SpectralArrow），但留著防未來改動

        // 只針對 piglin（含 piglin brute） zombified piglin 不在 AbstractPiglin）
        if (!(arrow.getOwner() instanceof AbstractPiglin piglin)) return;
        if(!isPiglinTargetingHoglin(piglin)) return;

        // 取消原本箭的加入
        event.setCanceled(true);

        // 生成 spectral arrow
        final SpectralArrow spectral = new SpectralArrow(EntityType.SPECTRAL_ARROW, level);

        // 位置 / 旋轉：用原本箭的 spawn 狀態
        spectral.moveTo(arrow.getX(), arrow.getY(), arrow.getZ(), arrow.getYRot(), arrow.getXRot());
        spectral.setDeltaMovement(arrow.getDeltaMovement());

        // owner / shooter 關聯
        spectral.setOwner(arrow.getOwner());

        // 盡可能複製 AbstractArrow 的核心參數
        copyArrowCommonFlags(arrow, spectral);

        // 加入世界（注意：這會再觸發 JoinLevelEvent，但 spectral 不會走到上面的 Arrow 分支）
        level.addFreshEntity(spectral);

        // 可選：把原箭標記移除（理論上 setCanceled 就不會加進世界，但保守起見）
        // arrow.discard();
    }

    private static void copyArrowCommonFlags(AbstractArrow src, AbstractArrow dst) {
        // 傷害 / 擊退 / 暴擊 / 穿刺 / 拾取
        dst.setBaseDamage(src.getBaseDamage());
        dst.setKnockback(src.getKnockback());
        dst.setCritArrow(src.isCritArrow());
        dst.setPierceLevel(src.getPierceLevel());
        dst.pickup = src.pickup;

        // 著火（如果原箭帶火）
        final int fireTicks = src.getRemainingFireTicks();
        if (fireTicks > 0) dst.setRemainingFireTicks(fireTicks);

        // 從十字弓射出（AbstractArrow 有這個欄位/旗標時）
        // 1.20.1 Mojang mappings 下有 isShotFromCrossbow()/setShotFromCrossbow(boolean)
        try {
            boolean fromCrossbow = (boolean) AbstractArrow.class
                    .getMethod("isShotFromCrossbow")
                    .invoke(src);
            AbstractArrow.class
                    .getMethod("setShotFromCrossbow", boolean.class)
                    .invoke(dst, fromCrossbow);
        } catch (Throwable ignored) {
            // 若 mappings/環境不同，就跳過；不影響主要功能
        }
    }

    public static boolean isPiglinTargetingHoglin(AbstractPiglin piglin) {
        var brain = piglin.getBrain();
        if (!brain.hasMemoryValue(MemoryModuleType.ATTACK_TARGET)) return false;

        LivingEntity target = brain.getMemory(MemoryModuleType.ATTACK_TARGET).orElse(null);
        if(target == null) return false;
        return target.getType().equals(EntityType.HOGLIN);
    }
}
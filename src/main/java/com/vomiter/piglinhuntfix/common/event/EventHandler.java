package com.vomiter.piglinhuntfix.common.event;

import com.vomiter.piglinhuntfix.Config;
import com.vomiter.piglinhuntfix.accessor.PiglinAIMethodAccessor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.hoglin.Hoglin;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.IEventBus;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class EventHandler {
    private static final String TAG_LAST_PIGLIN_UUID = "piglinhuntfix:last_piglin_uuid";
    private static final String TAG_LAST_PIGLIN_TIME = "piglinhuntfix:last_piglin_time";

    private static final int PULSE_INTERVAL_TICKS = 100;

    // 記錄的攻擊者有效 30 秒
    private static final int LAST_ATTACKER_TTL_TICKS = 20 * 30;

    // 招募半徑
    private static final double RECRUIT_RADIUS = 24.0;

    public static void init() {
        final IEventBus bus = MinecraftForge.EVENT_BUS;
        bus.addListener(EventHandler::onRegisterCommands);
        bus.addListener(EventHandler::onHoglinTick);
        bus.addListener(EventHandler::onHoglinHurt);
        bus.addListener(ProjectileSwapHandler::onEntityJoinLevel);
    }

    public static void onRegisterCommands(RegisterCommandsEvent event) {
        // ModCommand.register(event.getDispatcher());
    }

    // 1) Hoglin 被打到時，記錄最後攻擊者 piglin
    public static void onHoglinHurt(LivingHurtEvent event) {
        if(!Config.GLOWING_HOGLIN_BROADCAST_HUNTING) return;
        if (event.getEntity().level().isClientSide) return;
        if (!(event.getEntity() instanceof Hoglin hoglin)) return;

        Entity src = event.getSource().getEntity();
        if (!(src instanceof AbstractPiglin piglin)) return;

        // 只記錄成人 piglin
        if (piglin.isBaby()) return;

        CompoundTag tag = hoglin.getPersistentData();
        tag.putUUID(TAG_LAST_PIGLIN_UUID, piglin.getUUID());
        tag.putInt(TAG_LAST_PIGLIN_TIME, hoglin.tickCount);
    }

    // 2) Hoglin 發光時，每隔一段時間找 piglin 來 broadcast
    public static void onHoglinTick(LivingEvent.LivingTickEvent event) {
        if(!Config.GLOWING_HOGLIN_BROADCAST_HUNTING) return;
        LivingEntity entity = event.getEntity();
        if (entity.level().isClientSide) return;
        if (!(entity instanceof Hoglin hoglin)) return;

        // 你要用「發光 hoglin」當觸發器
        if (!hoglin.isCurrentlyGlowing()) return;

        // 節流
        if (hoglin.tickCount % PULSE_INTERVAL_TICKS != 0) return;

        ServerLevel level = (ServerLevel) hoglin.level();

        AbstractPiglin recruiter = findRecruiterPiglin(level, hoglin).orElse(null);
        if (recruiter == null) return;

        // 讓這隻 piglin 以 hoglin 為目標並廣播
        PiglinAIMethodAccessor.accessSetAngerTarget(recruiter, hoglin);
        PiglinAIMethodAccessor.accessBroadcastAngerTarget(recruiter, hoglin);

        // 之後再決定要不要 accessDontKillAnyMoreHoglinsForAWhile(recruiter);
        // 會把 HUNTED_RECENTLY 打上去，導致後續狩獵啟動/加入更保守。
    }

    private static Optional<AbstractPiglin> findRecruiterPiglin(ServerLevel level, Hoglin hoglin) {
        // A) 優先：最後攻擊者 piglin（TTL 內）
        UUID lastUuid = null;
        int lastTime = -1;

        CompoundTag tag = hoglin.getPersistentData();
        if (tag.hasUUID(TAG_LAST_PIGLIN_UUID) && tag.contains(TAG_LAST_PIGLIN_TIME)) {
            lastUuid = tag.getUUID(TAG_LAST_PIGLIN_UUID);
            lastTime = tag.getInt(TAG_LAST_PIGLIN_TIME);
        }

        if (lastUuid != null && lastTime >= 0 && (hoglin.tickCount - lastTime) <= LAST_ATTACKER_TTL_TICKS) {
            Entity e = level.getEntity(lastUuid);
            if (e instanceof AbstractPiglin p && p.isAlive() && !p.isBaby()) {
                // 限制距離，避免遠距離 recruitment
                if (p.distanceToSqr(hoglin) <= (RECRUIT_RADIUS * RECRUIT_RADIUS)) {
                    return Optional.of(p);
                }
            }
        }

        // B) 找不到就抓最近的 adult piglin
        AABB box = hoglin.getBoundingBox().inflate(RECRUIT_RADIUS);
        List<AbstractPiglin> candidates = level.getEntitiesOfClass(AbstractPiglin.class, box, p ->
                p.isAlive() && !p.isBaby()
        );

        return candidates.stream()
                .min(Comparator.comparingDouble(p -> p.distanceToSqr(hoglin)));
    }
}
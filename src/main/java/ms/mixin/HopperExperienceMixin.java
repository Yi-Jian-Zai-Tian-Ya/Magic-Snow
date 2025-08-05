package ms.mixin;

import ms.ModConfigs;
import ms.system.ExperiencePool;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.vehicle.MinecartHopper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.Hopper;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.phys.AABB;
import org.apache.logging.log4j.LogManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(HopperBlockEntity.class)
public abstract class HopperExperienceMixin
{
    @Inject(
            method = "suckInItems",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void onSuckInItems(Level level, Hopper hopper, CallbackInfoReturnable<Boolean> cir)
    {
        try
        {
            if (!ModConfigs.ENABLE_POOLING.get()) return;
            if (!(level instanceof ServerLevel serverLevel)) return;

            BlockPos pos = getHopperPos(hopper, level);
            if (pos == null) return;

            AABB area = getHopperArea(hopper, level);
            if (area == null) return;

            List<ExperienceOrb> orbs = level.getEntitiesOfClass(
                    ExperienceOrb.class, area, ExperienceOrb::isAlive
            );

            if (orbs.isEmpty()) return;

            int gained = orbs.stream().mapToInt(orb -> orb.getValue() * ((ExperienceOrbAccessor) orb).getCount()).sum();

            orbs.forEach(ExperienceOrb::discard);

            ExperiencePool pool = ExperiencePool.get(serverLevel);
            int total = pool.addExperience(pos, gained);
            int bottles = total / 10;

            if (bottles > 0)
            {
                pool.takeExperience(pos, bottles * 10);
                spawnBottles(level, pos, bottles);
            }
        }
        catch (Exception e)
        {
            LogManager.getLogger().error("Error in experience conversion", e);
        }
    }

    private static BlockPos getHopperPos(Hopper hopper, Level level)
    {
        if (hopper instanceof HopperBlockEntity) return ((HopperBlockEntity) hopper).getBlockPos();
        else if (hopper instanceof Entity entity) return BlockPos.containing(entity.position());
        return null;
    }

    private static AABB getHopperArea(Hopper hopper, Level level)
    {
        try
        {
            if (hopper instanceof HopperBlockEntity blockEntity)
            {
                BlockPos pos = blockEntity.getBlockPos();
                return new AABB(pos).inflate(0.5);
            }
            else if (hopper instanceof Entity entity)
            {
                return entity.getBoundingBox()
                        .inflate(0.5)
                        .expandTowards(0, 1, 0);
            }
        }
        catch (Exception e) {
            LogManager.getLogger().error("Failed to get hopper area", e);
        }
        return null;
    }

    private static void spawnBottles(Level level, BlockPos pos, int count)
    {
        BlockPos spawnPos = findSpawnPosition(level, pos);
        ItemStack bottleStack = new ItemStack(Items.EXPERIENCE_BOTTLE, count);

        ItemEntity item = new ItemEntity(
                level,
                spawnPos.getX() + 0.5,
                spawnPos.getY() + 0.5,
                spawnPos.getZ() + 0.5,
                bottleStack
        );
        level.addFreshEntity(item);
    }

    private static BlockPos findSpawnPosition(Level level, BlockPos basePos)
    {
        BlockPos above = basePos.above();
        return level.getBlockState(above).isAir() ? above : basePos;
    }
}
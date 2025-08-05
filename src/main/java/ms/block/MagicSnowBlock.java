package ms.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.PowderSnowBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

import javax.annotation.Nullable;

public class MagicSnowBlock extends PowderSnowBlock
{
    public MagicSnowBlock(Properties properties)
    {
        super(properties);
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity)
    {
        super.entityInside(state, level, pos, entity);

        if (entity instanceof LivingEntity living)
        {
            living.setTicksFrozen(living.getTicksRequiredToFreeze());
            if (living.tickCount % 10 == 0) living.hurt(level.damageSources().freeze(), 5.0F);
        }
    }

    @Override
    public ItemStack pickupBlock(LevelAccessor level, BlockPos pos, BlockState state)
    {
        level.setBlock(pos, Blocks.AIR.defaultBlockState(), 11);
        if (!level.isClientSide()) level.gameEvent(null, GameEvent.FLUID_PICKUP, pos);
        return new ItemStack(ms.MagicSnow.MAGIC_POWDER_SNOW_BUCKET.get());
    }
}
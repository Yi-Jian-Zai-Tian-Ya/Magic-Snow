package ms.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.Hopper;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Hopper.class)
public interface HopperAccessor extends IHopperEntityAccessor
{
    @Override
    default Entity getEntity()
    {
        if (this instanceof HopperBlockEntity) return null;
        try
        {
            return ((Entity) this);
        }
        catch (ClassCastException e)
        {
            return null;
        }
    }
}
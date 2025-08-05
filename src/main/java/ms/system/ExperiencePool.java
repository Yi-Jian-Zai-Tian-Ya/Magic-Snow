package ms.system;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.HashMap;
import java.util.Map;

public class ExperiencePool extends SavedData
{
    private final Map<BlockPos, Integer> pool = new HashMap<>();

    public static ExperiencePool get(ServerLevel level)
    {
        return level.getDataStorage().computeIfAbsent(
                ExperiencePool::load,
                ExperiencePool::new,
                "experience_pool"
        );
    }

    public static ExperiencePool load(CompoundTag tag)
    {
        ExperiencePool data = new ExperiencePool();
        CompoundTag pools = tag.getCompound("pools");
        for (String key : pools.getAllKeys()) data.pool.put(BlockPos.of(Long.parseLong(key)), pools.getInt(key));
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag)
    {
        CompoundTag pools = new CompoundTag();
        pool.forEach((pos, xp) -> pools.putInt(pos.asLong() + "", xp));
        tag.put("pools", pools);
        return tag;
    }

    public int addExperience(BlockPos pos, int amount)
    {
        int total = pool.getOrDefault(pos, 0) + amount;
        pool.put(pos, total);
        setDirty();
        return total;
    }

    public int takeExperience(BlockPos pos, int amount)
    {
        int current = pool.getOrDefault(pos, 0);
        if (current >= amount)
        {
            pool.put(pos, current - amount);
            setDirty();
            return amount;
        }
        return 0;
    }
}
package ms;

import net.minecraftforge.common.ForgeConfigSpec;

public class ModConfigs
{
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.BooleanValue ENABLE_POOLING;

    static {
        BUILDER.push("Experience Conversion");

        ENABLE_POOLING = BUILDER
                .comment("Enable experience pooling system")
                .define("enablePooling", true);

        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}
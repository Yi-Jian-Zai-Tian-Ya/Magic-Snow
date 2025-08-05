package ms;

import ms.block.KingAirBlock;
import ms.block.MagicSnowBlock;
import ms.item.MagicSnowBucketItem;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod(MagicSnow.MODID)
public class MagicSnow {
    public static final String MODID = "ms";

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

    public static final RegistryObject<Block> MAGIC_POWDER_SNOW = BLOCKS.register("magic_snow",
            () -> new MagicSnowBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.SNOW)
                    .strength(0.25F)
                    .sound(SoundType.SNOW)
                    .dynamicShape()
                    .noCollission()
                    .noOcclusion()));
    public static final RegistryObject<Block> KING_AIR_BLOCK = BLOCKS.register("king_air_block",
            KingAirBlock::new);

    public static final RegistryObject<Item> MAGIC_POWDER_SNOW_BUCKET = ITEMS.register("magic_snow_bucket",
            () -> new MagicSnowBucketItem(MAGIC_POWDER_SNOW.get(),
                    SoundEvents.POWDER_SNOW_PLACE,
                    new Item.Properties()
                            .craftRemainder(Items.BUCKET)
                            .stacksTo(1)));
    public static final RegistryObject<Item> KING_AIR_BLOCK_ITEM = ITEMS.register("king_air_block",
            () -> new BlockItem(KING_AIR_BLOCK.get(),
                    new Item.Properties()));

    public MagicSnow(FMLJavaModLoadingContext context)
    {
        context.registerConfig(ModConfig.Type.SERVER, ModConfigs.SPEC);
        var modBus = context.getModEventBus();
        BLOCKS.register(modBus);
        ITEMS.register(modBus);
        modBus.addListener(this::addCreative);
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event)
    {
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES)
        {
            event.accept(MAGIC_POWDER_SNOW_BUCKET);
        }
        if (event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS)
        {
            event.accept(KING_AIR_BLOCK_ITEM);
        }
    }
}
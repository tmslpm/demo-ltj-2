package com.jtorleonstudios.libraryferret;

import com.jtorleonstudios.libraryferret.blocks.Blocks;
import com.jtorleonstudios.libraryferret.conf.Configuration;
import com.jtorleonstudios.libraryferret.conf.Props;
import com.jtorleonstudios.libraryferret.effect.StatusEffects;
import com.jtorleonstudios.libraryferret.items.Items;
import com.jtorleonstudios.libraryferret.worldgen.structures.AwesomeStructure;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectionContext;
import net.fabricmc.fabric.api.biome.v1.ModificationPhase;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.structure.v1.FabricStructureBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.BlockEntityType.Builder;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.chunk.StructureConfig;
import net.minecraft.world.gen.feature.ConfiguredStructureFeature;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.FeatureConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class LibraryFerret implements ModInitializer {
  public static boolean isLOADED = false;
  public static final String MOD_ID = "libraryferret";
  public static final Logger LOGGER = LogManager.getLogger();

  private static final Props ENABLED_FERRET_ITEM_GROUP = Props.create("creative", "enable_item_group_ferret", false);

  public static final Configuration CONF = new Configuration(MOD_ID, ENABLED_FERRET_ITEM_GROUP);
  public static Optional<ItemGroup> FERRET_ITEM_GROUP = Optional.empty();

  public void onInitialize() {
    isLOADED = false;
    LOGGER.info("Initialize mod Library Ferret");
    this.setupOptionalFieldByConfiguration();
    StatusEffects.register();
    Items.register();
    Blocks.registerBlocks();
    CommonEvent.registerEvent();
    isLOADED = true;
    LOGGER.info("Initialized mod Library Ferret");
  }

  private void setupOptionalFieldByConfiguration() {
    if (CONF.getBoolOrDefault(ENABLED_FERRET_ITEM_GROUP))
      FERRET_ITEM_GROUP = Optional.of(FabricItemGroupBuilder.create(identifier(MOD_ID)).icon(() -> new ItemStack(net.minecraft.block.Blocks.CHEST)).build());
  }

  @SuppressWarnings("unused")
  public static boolean isLoaded() {
    return isLOADED;
  }

  public static Identifier identifier(String path) {
    return new Identifier(MOD_ID, path);
  }

  public static <T extends Block> T registerBlock(Identifier identifier, T block, ItemGroup itemGroup) {
    Registry.register(Registry.BLOCK, identifier, block);
    registerItem(identifier, new BlockItem(block, itemGroup == null ? new FabricItemSettings() : (new FabricItemSettings()).group(itemGroup)));
    return block;
  }

  public static <T extends BlockEntity> BlockEntityType<T> registerBlockEntity(Identifier identifier, Block block, Supplier<T> entity) {
    return Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(identifier.getNamespace(), identifier.getPath() + "_entity"), Builder.create(entity, new Block[]{block}).build(null));
  }

  public static <T extends StatusEffect> T registerEffect(T effect, Identifier identifier) {
    return Registry.register(Registry.STATUS_EFFECT, identifier, effect);
  }

  public static <T extends Item> T registerItem(Identifier identifier, T item) {
    return Registry.register(Registry.ITEM, identifier, item);
  }

  @Contract("_, _, _ -> param1")
  @SuppressWarnings("unused")
  public static @NotNull AwesomeStructure registerStructure(AwesomeStructure structureFeature, StructureConfig structureConfig, Predicate<BiomeSelectionContext> biomesSelector) {
    FabricStructureBuilder
            .create(new Identifier(structureFeature.getModIdOwner(), structureFeature.getId()), structureFeature)
            .step(structureFeature.getDefaultGenerationStepFeature()).defaultConfig(structureConfig)
            .superflatFeature(structureFeature.configure(FeatureConfig.DEFAULT))
            .adjustsSurface()
            .register();

    ConfiguredStructureFeature<?, ?> configuredStructureFeature = structureFeature.configure(DefaultFeatureConfig.DEFAULT);
    Registry.register(
            BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE,
            new Identifier(structureFeature.getModIdOwner(), "configured_" + structureFeature.getId()),
            configuredStructureFeature
    );

    BiomeModifications
            .create(new Identifier(structureFeature.getModIdOwner(), structureFeature.getId() + "_addition"))
            .add(ModificationPhase.ADDITIONS, biomesSelector, (ctx) -> ctx.getGenerationSettings().addBuiltInStructure(configuredStructureFeature));

    LibraryFerret.LOGGER.info(structureFeature.getModIdOwner() + ":" + structureFeature.getId()
            + " => JSON start pool: " + structureFeature.getIdentifierStartPool().toString()
            + ", Configuration[Spacing: " + structureConfig.getSpacing()
            + ", Separation: " + structureConfig.getSeparation()
            + ", Salt: " + structureConfig.getSalt()
            + "]"
    );

    return structureFeature;
  }

}

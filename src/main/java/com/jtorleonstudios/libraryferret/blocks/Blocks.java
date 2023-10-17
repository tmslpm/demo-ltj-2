
package com.jtorleonstudios.libraryferret.blocks;

import com.jtorleonstudios.libraryferret.LibraryFerret;
import net.minecraft.block.AbstractBlock.Settings;
import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemGroup;
import net.minecraft.sound.BlockSoundGroup;

import static com.jtorleonstudios.libraryferret.LibraryFerret.*;

public abstract class Blocks {
  public static Block FAKE_IRON_BLOCK;
  public static Block FAKE_GOLD_BLOCK;
  public static Block FAKE_EMERALD_BLOCK;
  public static Block FAKE_DIAMOND_BLOCK;
  public static Block FAKE_NETHERITE_BLOCK;
  public static UnbreakableBeacon<UnbreakableIronBeaconEntity> UNBREAKABLE_IRON_BEACON;
  public static BlockEntityType<UnbreakableIronBeaconEntity> UNBREAKABLE_IRON_BEACON_ENTITY;
  public static UnbreakableBeacon<UnbreakableGoldBeaconEntity> UNBREAKABLE_GOLD_BEACON;
  public static BlockEntityType<UnbreakableGoldBeaconEntity> UNBREAKABLE_GOLD_BEACON_ENTITY;
  public static UnbreakableBeacon<UnbreakableEmeraldBeaconEntity> UNBREAKABLE_EMERALD_BEACON;
  public static BlockEntityType<UnbreakableEmeraldBeaconEntity> UNBREAKABLE_EMERALD_BEACON_ENTITY;
  public static UnbreakableBeacon<UnbreakableDiamondBeaconEntity> UNBREAKABLE_DIAMOND_BEACON;
  public static BlockEntityType<UnbreakableDiamondBeaconEntity> UNBREAKABLE_DIAMOND_BEACON_ENTITY;
  public static UnbreakableBeacon<UnbreakableNetheriteBeaconEntity> UNBREAKABLE_NETHERITE_BEACON;
  public static BlockEntityType<UnbreakableNetheriteBeaconEntity> UNBREAKABLE_NETHERITE_BEACON_ENTITY;

  public static void registerBlocks() {
    ItemGroup ferretGroup = LibraryFerret.FERRET_ITEM_GROUP.orElse(null);
    registerFakeBlock(ferretGroup);
    registerBeacon(ferretGroup);
  }

  private static void registerBeacon(ItemGroup ferretGroup) {
    UNBREAKABLE_IRON_BEACON = registerBlock(identifier("unbreakable_iron_beacon"), new UnbreakableBeacon<>(MapColor.IRON_GRAY, UnbreakableIronBeaconEntity::new), ferretGroup);
    UNBREAKABLE_IRON_BEACON_ENTITY = registerBlockEntity(identifier("unbreakable_iron_beacon"), UNBREAKABLE_IRON_BEACON, UnbreakableIronBeaconEntity::new);
    UNBREAKABLE_GOLD_BEACON = registerBlock(identifier("unbreakable_gold_beacon"), new UnbreakableBeacon<>(MapColor.GOLD, UnbreakableGoldBeaconEntity::new), ferretGroup);
    UNBREAKABLE_GOLD_BEACON_ENTITY = registerBlockEntity(identifier("unbreakable_gold_beacon"), UNBREAKABLE_GOLD_BEACON, UnbreakableGoldBeaconEntity::new);
    UNBREAKABLE_EMERALD_BEACON = registerBlock(identifier("unbreakable_emerald_beacon"), new UnbreakableBeacon<>(MapColor.EMERALD_GREEN, UnbreakableEmeraldBeaconEntity::new), ferretGroup);
    UNBREAKABLE_EMERALD_BEACON_ENTITY = registerBlockEntity(identifier("unbreakable_emerald_beacon"), UNBREAKABLE_EMERALD_BEACON, UnbreakableEmeraldBeaconEntity::new);
    UNBREAKABLE_DIAMOND_BEACON = registerBlock(identifier("unbreakable_diamond_beacon"), new UnbreakableBeacon<>(MapColor.DIAMOND_BLUE, UnbreakableDiamondBeaconEntity::new), ferretGroup);
    UNBREAKABLE_DIAMOND_BEACON_ENTITY = registerBlockEntity(identifier("unbreakable_diamond_beacon"), UNBREAKABLE_DIAMOND_BEACON, UnbreakableDiamondBeaconEntity::new);
    UNBREAKABLE_NETHERITE_BEACON = registerBlock(identifier("unbreakable_netherite_beacon"), new UnbreakableBeacon<>(MapColor.BLACK, UnbreakableNetheriteBeaconEntity::new), ferretGroup);
    UNBREAKABLE_NETHERITE_BEACON_ENTITY = registerBlockEntity(identifier("unbreakable_netherite_beacon"), UNBREAKABLE_NETHERITE_BEACON, UnbreakableNetheriteBeaconEntity::new);
  }

  private static void registerFakeBlock(ItemGroup ferretGroup) {
    FAKE_IRON_BLOCK = registerBlock(identifier("fake_iron_block"), new Block(Settings.of(Material.METAL, MapColor.IRON_GRAY).requiresTool().strength(5.0F, 6.0F).sounds(BlockSoundGroup.METAL)), ferretGroup);
    FAKE_GOLD_BLOCK = registerBlock(identifier("fake_gold_block"), new Block(Settings.of(Material.METAL, MapColor.GOLD).requiresTool().strength(3.0F, 6.0F).sounds(BlockSoundGroup.METAL)), ferretGroup);
    FAKE_EMERALD_BLOCK = registerBlock(identifier("fake_emerald_block"), new Block(Settings.of(Material.METAL, MapColor.EMERALD_GREEN).requiresTool().strength(5.0F, 6.0F).sounds(BlockSoundGroup.METAL)), ferretGroup);
    FAKE_DIAMOND_BLOCK = registerBlock(identifier("fake_diamond_block"), new Block(Settings.of(Material.METAL, MapColor.DIAMOND_BLUE).requiresTool().strength(5.0F, 6.0F).sounds(BlockSoundGroup.METAL)), ferretGroup);
    FAKE_NETHERITE_BLOCK = registerBlock(identifier("fake_netherite_block"), new Block(Settings.of(Material.METAL, MapColor.BLACK).requiresTool().strength(50.0F, 1200.0F).sounds(BlockSoundGroup.NETHERITE)), ferretGroup);
  }
}

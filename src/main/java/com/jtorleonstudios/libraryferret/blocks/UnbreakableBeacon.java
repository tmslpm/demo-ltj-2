
package com.jtorleonstudios.libraryferret.blocks;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.BeaconBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.MapColor;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;
import java.util.function.Supplier;

public class UnbreakableBeacon<T extends BlockEntity> extends BeaconBlock {

  private final Supplier<T> entityCreator;

  public UnbreakableBeacon(MapColor mapColor, Supplier<T> entityCreator) {
    super(FabricBlockSettings.of(Material.GLASS, mapColor).strength(3.0F).luminance(15).solidBlock((state, world, pos) -> false).nonOpaque());
    this.entityCreator = entityCreator;
  }

  @Override
  public T createBlockEntity(BlockView world) {
    return entityCreator.get();
  }

  @Override
  public ActionResult onUse(BlockState state, @NotNull World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
    return world.isClient ? ActionResult.SUCCESS : ActionResult.CONSUME;
  }

  @Override
  public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
    super.onPlaced(world, pos, state, placer, itemStack);
    world.playSound((PlayerEntity) null, pos, SoundEvents.BLOCK_BEACON_POWER_SELECT, SoundCategory.BLOCKS, 1.0F, 1.0F);
  }
}
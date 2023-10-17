package com.jtorleonstudios.libraryferret.blocks;

import com.jtorleonstudios.libraryferret.effect.StatusEffects;

import java.util.Iterator;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.Box;

public class UnbreakableDiamondBeaconEntity extends BlockEntity implements Tickable {
  public UnbreakableDiamondBeaconEntity() {
    super(Blocks.UNBREAKABLE_DIAMOND_BEACON_ENTITY);
  }

  public void tick() {
    if (this.world != null && !this.world.isClient) {
      Box box = (new Box(this.pos)).expand(50.0).stretch(0.0, this.world.getHeight(), 0.0);

      for (PlayerEntity playerEntity : this.world.getNonSpectatingEntities(PlayerEntity.class, box)) {
        playerEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.UNBREAKABLE_CURSE, 5, 1, true, true));
      }
    }

  }
}

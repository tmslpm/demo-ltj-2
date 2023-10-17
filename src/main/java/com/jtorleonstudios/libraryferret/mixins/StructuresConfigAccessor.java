package com.jtorleonstudios.libraryferret.mixins;


import java.util.Map;

import net.minecraft.world.gen.chunk.StructureConfig;
import net.minecraft.world.gen.chunk.StructuresConfig;
import net.minecraft.world.gen.feature.StructureFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({StructuresConfig.class})
public interface StructuresConfigAccessor {
  @Accessor("structures")
  void setStructures(Map<StructureFeature<?>, StructureConfig> var1);
}


package com.jtorleonstudios.libraryferret;

import com.jtorleonstudios.libraryferret.blocks.Blocks;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.render.RenderLayer;

@Environment(EnvType.CLIENT)
public class LibraryFerretClient implements ClientModInitializer {

  public void onInitializeClient() {
    BlockRenderLayerMap.INSTANCE.putBlock(Blocks.UNBREAKABLE_IRON_BEACON, RenderLayer.getCutout());
    BlockRenderLayerMap.INSTANCE.putBlock(Blocks.UNBREAKABLE_GOLD_BEACON, RenderLayer.getCutout());
    BlockRenderLayerMap.INSTANCE.putBlock(Blocks.UNBREAKABLE_EMERALD_BEACON, RenderLayer.getCutout());
    BlockRenderLayerMap.INSTANCE.putBlock(Blocks.UNBREAKABLE_DIAMOND_BEACON, RenderLayer.getCutout());
    BlockRenderLayerMap.INSTANCE.putBlock(Blocks.UNBREAKABLE_NETHERITE_BEACON, RenderLayer.getCutout());
  }
}

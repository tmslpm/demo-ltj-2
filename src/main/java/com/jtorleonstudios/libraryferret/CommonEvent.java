
package com.jtorleonstudios.libraryferret;

import com.jtorleonstudios.libraryferret.blocks.UnbreakableBeacon;
import com.jtorleonstudios.libraryferret.effect.StatusEffects;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;

public class CommonEvent {

    public CommonEvent() {
    }

    public static void registerEvent() {
        PlayerBlockBreakEvents.BEFORE.register((world, player, pos, state, entity) -> !player.hasStatusEffect(StatusEffects.UNBREAKABLE_CURSE) || player.isCreative() || state.getBlock() instanceof UnbreakableBeacon);
    }
}

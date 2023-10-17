package com.jtorleonstudios.libraryferret.effect;

import com.jtorleonstudios.libraryferret.LibraryFerret;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectType;

public final class StatusEffects {
    public static StatusEffect UNBREAKABLE_CURSE;

    public StatusEffects() {
    }

    public static void register() {
        UNBREAKABLE_CURSE = LibraryFerret.registerEffect(new StatusEffect(StatusEffectType.NEUTRAL, 715204) {
            public boolean canApplyUpdateEffect(int duration, int amplifier) {
                return true;
            }
        }, LibraryFerret.identifier("unbreakable_curse"));
    }
}


package com.jtorleonstudios.libraryferret.items;

import com.jtorleonstudios.libraryferret.LibraryFerret;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;

public final class Items {
    public static Item EMERALD_COIN;
    public static Item IRON_COIN;
    public static Item GOLD_COIN;
    public static Item DIAMOND_COIN;
    public static Item NETHERITE_COIN;

    public Items() {
    }

    public static final void register() {
        IRON_COIN = registerBasicItem("iron_coins_jtl");
        EMERALD_COIN = registerBasicItem("emerald_coins_jtl");
        GOLD_COIN = registerBasicItem("gold_coins_jtl");
        DIAMOND_COIN = registerBasicItem("diamond_coins_jtl");
        NETHERITE_COIN = registerBasicItem("netherite_coins_jtl");
    }

    public static final Item registerBasicItem(String name) {
        FabricItemSettings itemProperties = new FabricItemSettings();
        if (LibraryFerret.FERRET_ITEM_GROUP.isPresent()) {
            itemProperties.group((ItemGroup)LibraryFerret.FERRET_ITEM_GROUP.get());
        }

        return LibraryFerret.registerItem(LibraryFerret.identifier(name), new Item(itemProperties));
    }
}

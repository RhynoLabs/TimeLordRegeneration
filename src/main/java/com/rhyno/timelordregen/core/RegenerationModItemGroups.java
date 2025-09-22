package com.rhyno.timelordregen.core;

import com.rhyno.timelordregen.RegenerationMod;
import dev.amble.lib.container.impl.ItemGroupContainer;
import dev.amble.lib.itemgroup.AItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class RegenerationModItemGroups implements ItemGroupContainer {

    public static final AItemGroup REGEN = AItemGroup.builder(RegenerationMod.id("item_group"))
            .icon(() -> new ItemStack(RegenerationModItems.ELIXIR_OF_LIFE))
            .build();
}

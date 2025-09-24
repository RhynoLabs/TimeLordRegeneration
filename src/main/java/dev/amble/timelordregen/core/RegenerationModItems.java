package dev.amble.timelordregen.core;

import dev.amble.lib.container.impl.ItemContainer;
import dev.amble.lib.datagen.util.NoEnglish;
import dev.amble.lib.item.AItemSettings;
import net.minecraft.item.Item;

public class RegenerationModItems extends ItemContainer {

    @NoEnglish
    public static final Item ELIXIR_OF_LIFE = new Item(new AItemSettings().group(RegenerationModItemGroups.REGEN).maxCount(16));
}

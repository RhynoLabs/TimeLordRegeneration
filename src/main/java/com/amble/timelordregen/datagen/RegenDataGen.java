package com.amble.timelordregen.datagen;

import com.amble.timelordregen.core.RegenerationModBlocks;
import com.amble.timelordregen.core.RegenerationModItemGroups;
import com.amble.timelordregen.core.RegenerationModItems;
import com.amble.timelordregen.datagen.providers.RegenerationModAchivementProvider;
import com.amble.timelordregen.datagen.providers.RegenerationModModelGen;
import com.amble.timelordregen.datagen.providers.RegenerationRecipeProvider;
import dev.amble.lib.datagen.lang.AmbleLanguageProvider;
import dev.amble.lib.datagen.lang.LanguageType;
import dev.amble.lib.datagen.loot.AmbleBlockLootTable;
import dev.amble.lib.datagen.sound.AmbleSoundProvider;
import dev.amble.lib.datagen.tag.AmbleBlockTagProvider;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class RegenDataGen implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator gen) {
		FabricDataGenerator.Pack pack = gen.createPack();

		genLang(pack);
        genModels(pack);
        generateRecipes(pack);
        generateSoundData(pack);
        generateAchivement(pack);
        genTags(pack);
        genLoot(pack);
	}

    private void generateAchivement(FabricDataGenerator.Pack pack) {
        pack.addProvider(RegenerationModAchivementProvider::new);
    }

    public void generateSoundData(FabricDataGenerator.Pack pack) {
        pack.addProvider((((output, registriesFuture) -> new AmbleSoundProvider(output))));
    }

    private void genTags(FabricDataGenerator.Pack pack) {
        pack.addProvider((((output, registriesFuture) -> new AmbleBlockTagProvider(output, registriesFuture).withBlocks(RegenerationModBlocks.class))));
    }

    private void genLoot(FabricDataGenerator.Pack pack) {
        pack.addProvider((((output, registriesFuture) -> new AmbleBlockLootTable(output).withBlocks(RegenerationModBlocks.class))));
    }

    private void genModels(FabricDataGenerator.Pack pack) {
        pack.addProvider(((output, registriesFuture) -> {
            RegenerationModModelGen provider = new RegenerationModModelGen(output);
            provider.withBlocks(RegenerationModBlocks.class);
            provider.withItems(RegenerationModItems.class);
            return provider;
        }));
    }
	private void genLang(FabricDataGenerator.Pack pack) {
		genEnglish(pack);
	}

	private void genEnglish(FabricDataGenerator.Pack pack) {
		pack.addProvider((((output, registry) -> {
			AmbleLanguageProvider provider = new AmbleLanguageProvider(output, LanguageType.EN_US);

            // Item Groups
            provider.addTranslation(RegenerationModItemGroups.REGEN,"Regeneration Mod");

			// Items
            provider.addTranslation(RegenerationModItems.ELIXIR_OF_LIFE,"Elixir of Life");

			return provider;
		})));
	}

    public void generateRecipes(FabricDataGenerator.Pack pack) {
        pack.addProvider((((output, registriesFuture) -> {
            RegenerationRecipeProvider provider = new RegenerationRecipeProvider(output);
            return provider;

        })));
    }
}

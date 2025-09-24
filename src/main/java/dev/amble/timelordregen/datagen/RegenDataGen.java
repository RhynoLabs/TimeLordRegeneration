package dev.amble.timelordregen.datagen;

import dev.amble.timelordregen.core.RegenerationModBlocks;
import dev.amble.timelordregen.core.RegenerationModItemGroups;
import dev.amble.timelordregen.core.RegenerationModItems;
import dev.amble.timelordregen.datagen.providers.*;
import dev.amble.lib.datagen.lang.AmbleLanguageProvider;
import dev.amble.lib.datagen.lang.LanguageType;
import dev.amble.lib.datagen.loot.AmbleBlockLootTable;
import dev.amble.lib.datagen.sound.AmbleSoundProvider;
import dev.amble.timelordregen.datagen.providers.*;
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
        pack.addProvider(RegenerationBlockTagProvider::new);
        pack.addProvider(RegenerationItemTagProvider::new);
    }

    private void genLoot(FabricDataGenerator.Pack pack) {
        pack.addProvider((((output, registriesFuture) -> new AmbleBlockLootTable(output).withBlocks(RegenerationModBlocks.class))));
    }

    private void genModels(FabricDataGenerator.Pack pack) {
        pack.addProvider(((output, registriesFuture) -> {
            RegenerationModModelGen provider = new RegenerationModModelGen(output);

            // Yes this is confusing but oh well - Loqor
            provider.registerLogBlock(RegenerationModBlocks.CADON_LOG, RegenerationModBlocks.CADON_WOOD);
            provider.registerLogBlock(RegenerationModBlocks.STRIPPED_CADON_LOG, RegenerationModBlocks.STRIPPED_CADON_WOOD);
            provider.registerBlockSet(new BlockSetRecord(
                    RegenerationModBlocks.CADON_PLANKS,
                    RegenerationModBlocks.CADON_STAIRS,
                    RegenerationModBlocks.CADON_SLAB,
                    RegenerationModBlocks.CADON_FENCE,
                    RegenerationModBlocks.CADON_FENCE_GATE,
                    RegenerationModBlocks.CADON_TRAPDOOR,
                    RegenerationModBlocks.CADON_DOOR,
                    RegenerationModBlocks.CADON_PRESSURE_PLATE,
                    RegenerationModBlocks.CADON_BUTTON
            ));
            provider.registerSimpleBlock(RegenerationModBlocks.CADON_LEAVES);

            return provider;
        }));
    }
	private void genLang(FabricDataGenerator.Pack pack) {
		genEnglish(pack);
	}

	private void genEnglish(FabricDataGenerator.Pack pack) {
		pack.addProvider((((output, registry) -> {
			AmbleLanguageProvider provider = new AmbleLanguageProvider(output, LanguageType.EN_US);

            // Commands
            provider.addTranslation("command.regen.name","regen");
            provider.addTranslation("command.regenui.name","regenui");
            provider.addTranslation("command.regen.data.error","Regeneration data not found.");
            provider.addTranslation("command.regen.triggered","Regeneration triggered!");
            provider.addTranslation("command.regen.fail","No regenerations left or already regenerating.");

            // GUI
            provider.addTranslation("gui.regen.settings.title","Regeneration Settings");
            provider.addTranslation("gui.regen.settings.remaining","Remaining Regenerations: %s");

            // Item Groups
            provider.addTranslation(RegenerationModItemGroups.REGEN,"Regeneration Mod");

			// Items
            provider.addTranslation(RegenerationModItems.ELIXIR_OF_LIFE,"Elixir of Life");

            // Blocks
            provider.addTranslation(RegenerationModBlocks.CADON_LOG, "Cadon Log");
            provider.addTranslation(RegenerationModBlocks.STRIPPED_CADON_LOG, "Stripped Cadon Log");
            provider.addTranslation(RegenerationModBlocks.CADON_WOOD, "Cadon Wood");
            provider.addTranslation(RegenerationModBlocks.STRIPPED_CADON_WOOD, "Stripped Cadon Wood");
            provider.addTranslation(RegenerationModBlocks.CADON_PLANKS, "Cadon Planks");
            provider.addTranslation(RegenerationModBlocks.CADON_LEAVES, "Cadon Leaves");
            provider.addTranslation(RegenerationModBlocks.CADON_STAIRS, "Cadon Stairs");
            provider.addTranslation(RegenerationModBlocks.CADON_SLAB, "Cadon Slab");
            provider.addTranslation(RegenerationModBlocks.CADON_FENCE, "Cadon Fence");
            provider.addTranslation(RegenerationModBlocks.CADON_FENCE_GATE, "Cadon Fence Gate");
            provider.addTranslation(RegenerationModBlocks.CADON_DOOR, "Cadon Door");
            provider.addTranslation(RegenerationModBlocks.CADON_TRAPDOOR, "Cadon Trapdoor");
            provider.addTranslation(RegenerationModBlocks.CADON_PRESSURE_PLATE, "Cadon Pressure Plate");
            provider.addTranslation(RegenerationModBlocks.CADON_BUTTON, "Cadon Button");

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

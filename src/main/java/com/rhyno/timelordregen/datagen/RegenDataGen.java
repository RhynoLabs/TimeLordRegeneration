package com.rhyno.timelordregen.datagen;

import dev.amble.lib.datagen.lang.AmbleLanguageProvider;
import dev.amble.lib.datagen.lang.LanguageType;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class RegenDataGen implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator gen) {
		FabricDataGenerator.Pack pack = gen.createPack();

		genLang(pack);
	}

	private void genLang(FabricDataGenerator.Pack pack) {
		genEnglish(pack);
	}

	private void genEnglish(FabricDataGenerator.Pack pack) {
		pack.addProvider((((output, registry) -> {
			AmbleLanguageProvider provider = new AmbleLanguageProvider(output, LanguageType.EN_US);

			// nothing yet it seems

			return provider;
		})));
	}
}

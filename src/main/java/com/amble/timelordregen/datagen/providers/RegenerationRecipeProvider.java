package com.amble.timelordregen.datagen.providers;

import com.amble.timelordregen.RegenerationMod;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.block.Block;
import net.minecraft.data.server.recipe.*;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

public class RegenerationRecipeProvider extends FabricRecipeProvider {
    public List<ShapelessRecipeJsonBuilder> shapelessRecipes = new ArrayList<>();
    public List<ShapedRecipeJsonBuilder> shapedRecipes = new ArrayList<>();
    public HashMap<SmithingTransformRecipeJsonBuilder, Identifier> smithingTransformRecipes = new HashMap<>();
    public HashMap<ShapelessRecipeJsonBuilder, Identifier> shapelessRecipesWithNameHashMap = new HashMap<>();
    public HashMap<ShapedRecipeJsonBuilder, Identifier> shapedRecipesWithNameHashMap = new HashMap<>();
    public HashMap<SingleItemRecipeJsonBuilder, Identifier> stonecutting = new HashMap<>();
    public List<CookingRecipeJsonBuilder> blasting = new ArrayList<>();


    public RegenerationRecipeProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generate(Consumer<RecipeJsonProvider> exporter) {
        for (ShapelessRecipeJsonBuilder shapelessRecipeJsonBuilder : shapelessRecipes) {
            shapelessRecipeJsonBuilder.offerTo(exporter);
        }
        for (ShapedRecipeJsonBuilder shapedRecipeJsonBuilder : shapedRecipes) {
            shapedRecipeJsonBuilder.offerTo(exporter);
        }
        shapelessRecipesWithNameHashMap.forEach((builder, id) -> builder.offerTo(exporter, id));

        smithingTransformRecipes.forEach((smithingTransformRecipeJsonBuilder, identifier) -> {
            smithingTransformRecipeJsonBuilder.offerTo(exporter, identifier);
        });
        shapedRecipesWithNameHashMap.forEach((builder, id) -> builder.offerTo(exporter, id));

        stonecutting.forEach((stonecuttingRecipeJsonBuilder, identifier) -> {
            stonecuttingRecipeJsonBuilder.offerTo(exporter, identifier);
        });

        for (CookingRecipeJsonBuilder cookingRecipeJsonBuilder : blasting) {
            cookingRecipeJsonBuilder.offerTo(exporter);
        }
    }

    public void addShapelessRecipe(ShapelessRecipeJsonBuilder builder) {
        if (!shapelessRecipes.contains(builder)) {
            shapelessRecipes.add(builder);
        }
    }

    public void addShapelessRecipe(ShapelessRecipeJsonBuilder builder, Identifier id) {
        shapelessRecipesWithNameHashMap.put(builder, id);
    }

    public void addSmithingTransformRecipe(SmithingTransformRecipeJsonBuilder builder, Identifier id) {
        smithingTransformRecipes.put(builder, id);
    }

    public void addShapedRecipe(ShapedRecipeJsonBuilder builder, Identifier id) {
        shapedRecipesWithNameHashMap.put(builder, id);
    }

    public void addShapedRecipe(ShapedRecipeJsonBuilder builder) {
        if (!shapedRecipes.contains(builder)) {
            shapedRecipes.add(builder);
        }
    }

    public void addStonecutting(Block in, Block out, int count) {
        Identifier id = getStonecuttingIdentifier(in, out);

        stonecutting.put(SingleItemRecipeJsonBuilder.createStonecutting(Ingredient.ofItems(in), RecipeCategory.BUILDING_BLOCKS, out, count).criterion("has_block", VanillaRecipeProvider.conditionsFromItem(in)), id);
    }
    public void addStonecutting(Block in, Block out) {
        addStonecutting(in, out, 1);
    }

    private Identifier getStonecuttingIdentifier(Block in, Block out) {
        return RegenerationMod.id(fixupBlockKey(in.getTranslationKey()) + "_to_" + fixupBlockKey(out.getTranslationKey()) + "_stonecutting");
    }
    private String fixupBlockKey(String key) {
        return key.substring(key.lastIndexOf(".") + 1);
    }

    public void addBlastFurnaceRecipe(CookingRecipeJsonBuilder cookingBuilder) {
        if (!blasting.contains(cookingBuilder)) {
            blasting.add(cookingBuilder);
        }
    }
}


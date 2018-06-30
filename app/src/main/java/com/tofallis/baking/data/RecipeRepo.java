package com.tofallis.baking.data;

import android.util.Log;

import com.tofallis.baking.DiskIOExecutor;
import com.tofallis.baking.network.Ingredient;
import com.tofallis.baking.network.Recipe;
import com.tofallis.baking.network.Step;

import org.threeten.bp.OffsetDateTime;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * DB/API wrapper for Recipe objects
 */
@Singleton
public class RecipeRepo {

    private static final String TAG = RecipeRepo.class.getSimpleName();

    private final RecipeDao mRecipeDao;
    private final IngredientDao mIngredientDao;
    private final StepDao mStepDao;

    @Inject
    public RecipeRepo(RecipeDao recipeDao, IngredientDao ingredientDao, StepDao stepDao) {
        mRecipeDao = recipeDao;
        mIngredientDao = ingredientDao;
        mStepDao = stepDao;
    }

    public void updateRecipes(final List<Recipe> recipes) {
        final List<RecipeStore> recipeStores = new ArrayList<>();
        final List<IngredientStore> ingredientStores = new ArrayList<>();
        final List<StepStore> stepStores = new ArrayList<>();
        for (Recipe recipe : recipes) {
            recipeStores.add(apiToDb(recipe));
            final int recipeId = recipe.getId();
            for (Ingredient i : recipe.getIngredients()) {
                ingredientStores.add(apiToDb(i, recipeId));
            }
            for (Step s : recipe.getSteps()) {
                stepStores.add(apiToDb(s, recipeId));
            }
        }
        final RecipeStore[] recipesArray = recipeStores.toArray(new RecipeStore[recipes.size()]);
        Log.d(TAG, "Num Recipes: " + recipesArray.length);
        Log.d(TAG, "All Recipes: " + recipesArray.toString());

        final IngredientStore[] ingredientsArray = ingredientStores.toArray(new IngredientStore[0]);
        Log.d(TAG, "Num Ingredients: " + ingredientsArray.length);
        final StepStore[] stepsArray = stepStores.toArray(new StepStore[0]);
        Log.d(TAG, "Num Steps: " + stepsArray.length);

        DiskIOExecutor.execute(() -> {
            mRecipeDao.updateRecipe(recipesArray);
            mIngredientDao.updateIngredients(ingredientsArray);
            mStepDao.updateSteps(stepsArray);
        });
    }

    private RecipeStore apiToDb(Recipe recipe) {
        OffsetDateTime lastSync = OffsetDateTime.now();
        return new RecipeStore(recipe.getId(), recipe.getName(), recipe.getServings(), recipe.getImage(), lastSync);
    }

    private IngredientStore apiToDb(Ingredient i, int recipeId) {
        IngredientStore ingredientStore = new IngredientStore();
        ingredientStore.setIngredient(i.getIngredient());
        ingredientStore.setMeasure(i.getMeasure());
        ingredientStore.setQuantity(i.getQuantity());
        ingredientStore.setRecipeId(recipeId);
        return ingredientStore;
    }

    private StepStore apiToDb(Step s, int recipeId) {
        StepStore stepStore = new StepStore();
        stepStore.setId(s.getId());
        stepStore.setDescription(s.getDescription());
        stepStore.setShortDescription(s.getShortDescription());
        stepStore.setThumbnailUrl(s.getThumbnailUrl());
        stepStore.setVideoUrl(s.getVideoUrl());
        stepStore.setRecipeId(recipeId);
        return stepStore;
    }
}

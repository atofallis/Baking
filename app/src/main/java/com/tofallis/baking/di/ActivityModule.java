package com.tofallis.baking.di;

import com.tofallis.baking.ui.RecipeDetailActivity;
import com.tofallis.baking.ui.recipe_list.RecipeListActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class ActivityModule {

    @ContributesAndroidInjector
    abstract RecipeListActivity contributeRecipeListActivity();

    @ContributesAndroidInjector
    abstract RecipeDetailActivity contributeRecipeDetailActivity();

}

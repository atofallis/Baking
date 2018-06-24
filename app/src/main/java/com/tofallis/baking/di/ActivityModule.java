package com.tofallis.baking.di;

import com.tofallis.baking.ui.RecipeDetailActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class ActivityModule {

    @ContributesAndroidInjector
    abstract RecipeDetailActivity contributeRecipeDetailActivity();

}

package com.tofallis.baking.ui;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import com.tofallis.baking.data.AppDatabase;

public class RecipeViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private AppDatabase mDb;
    private int mRecipeId;

    public RecipeViewModelFactory(AppDatabase db, int recipeId) {
        mDb = db;
        mRecipeId = recipeId;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        //noinspection unchecked
        return (T) new RecipeIdViewModel(mDb, mRecipeId);
    }
}

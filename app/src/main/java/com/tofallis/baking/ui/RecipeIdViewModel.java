package com.tofallis.baking.ui;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import com.tofallis.baking.data.AppDatabase;
import com.tofallis.baking.data.IngredientStore;
import com.tofallis.baking.data.StepStore;

import java.util.List;

public class RecipeIdViewModel extends ViewModel {

    protected int mRecipeId;
    protected AppDatabase mDb;
    private LiveData<List<StepStore>> mStepLiveData;
    private LiveData<List<IngredientStore>> mIngredientLiveData;

    public RecipeIdViewModel(@NonNull AppDatabase db, int recipeId) {
        mDb = db;
        mRecipeId = recipeId;
    }

    public LiveData<List<StepStore>> getStepLiveData() {
        if (mStepLiveData == null) {
            mStepLiveData = mDb.stepDao().getSteps(mRecipeId);
        }
        return mStepLiveData;
    }

    public LiveData<List<IngredientStore>> getIngredientLiveData() {
        if (mIngredientLiveData == null) {
            mIngredientLiveData = mDb.ingredientDao().getIngredients(mRecipeId);
        }
        return mIngredientLiveData;
    }
}

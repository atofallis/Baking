package com.tofallis.baking.network;

import android.util.Log;

import com.tofallis.baking.api.listener.RecipeListener;
import com.tofallis.baking.data.RecipeRepo;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class DataManager {

    private static final String TAG = DataManager.class.getSimpleName();

    private List<RecipeListener> mRecipeListenerList = new ArrayList<>();
    private CompositeDisposable mDisposable = new CompositeDisposable();

    private Scheduler getSubscribeOnScheduler() {
        return Schedulers.io();
    }

    private Scheduler getObserveOnScheduler() {
        return AndroidSchedulers.mainThread();
    }

    @Inject
    Retrofit mRetrofit;

    @Inject
    RecipeRepo mRecipeRepo;

    private DataManager() {
        throw new InstantiationError("cannot use default ctor");
    }

    public DataManager(Retrofit retrofit, RecipeRepo repo) {
        mRetrofit = retrofit;
        mRecipeRepo = repo;
    }

    public void fetchRecipes() {
        RecipeInterface results = mRetrofit.create(RecipeInterface.class);

        mDisposable.add(results.getRecipes()
                .subscribeOn(getSubscribeOnScheduler())
                .observeOn(getObserveOnScheduler())
                .subscribe(
                        this::handleRecipesSuccess,
                        this::handleRecipesError
                )
        );
    }

    public void addRecipeListener(RecipeListener listener) {
        mRecipeListenerList.add(listener);
    }

    public void removeRecipeListener(RecipeListener listener) {
        mRecipeListenerList.remove(listener);
        mDisposable.dispose();
    }

    private void handleRecipesSuccess(List<Recipe> results) {
        Log.d(TAG, "Recipe results successfully retrieved from network, updating DB..");
        mRecipeRepo.updateRecipes(results);
        for (RecipeListener listener : mRecipeListenerList) {
            listener.onNetworkSuccess();
        }
    }

    private void handleRecipesError(Throwable error) {
        for (RecipeListener listener : mRecipeListenerList) {
            listener.onNetworkError(error);
        }
    }
}

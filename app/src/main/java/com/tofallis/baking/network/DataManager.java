package com.tofallis.baking.network;

import com.tofallis.baking.api.listener.RecipeListener;
import com.tofallis.baking.data.Recipe;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class DataManager {

    private List<RecipeListener> mRecipeListenerList = new ArrayList<>();
    private List<Recipe> mRecipeResults = new ArrayList<>();

    private CompositeDisposable mDisposable = new CompositeDisposable();

    public List<Recipe> getRecipeResults() {
        return mRecipeResults;
    }

    private Scheduler getSubscribeOnScheduler() {
        return Schedulers.io();
    }

    private Scheduler getObserveOnScheduler() {
        return AndroidSchedulers.mainThread();
    }

    @Inject
    Retrofit mRetrofit;

    DataManager() {
        throw new InstantiationError("cannot use default ctor");
    }

    public DataManager(Retrofit retrofit) {
        mRetrofit = retrofit;
    }

    public void fetchRecipes() {
        RecipeInterface results = mRetrofit.create(RecipeInterface.class);

        mDisposable.add(results.getRecipes()
                .subscribeOn(getSubscribeOnScheduler())
                .observeOn(getObserveOnScheduler())
                .subscribe(
                        this::notifyRecipeListeners,
                        this::handleRecipesError
                )
        );
    }

    public void addRecipeListener(RecipeListener listener) {
        mRecipeListenerList.add(listener);
    }

    public void removeRecipeListener(RecipeListener listener) {
        mRecipeListenerList.remove(listener);
    }

    private void notifyRecipeListeners(List<Recipe> results) {
        mRecipeResults = results;
        for (RecipeListener listener : mRecipeListenerList) {
            listener.onSuccess();
        }
    }

    private void handleRecipesError(Throwable error) {
        for (RecipeListener listener : mRecipeListenerList) {
            listener.onNetworkError(error);
        }
    }
}

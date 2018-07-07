package com.tofallis.baking.ui.recipe_list;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import com.tofallis.baking.data.AppDatabase;
import com.tofallis.baking.data.RecipeStore;
import com.tofallis.baking.network.DataManager;

import org.threeten.bp.OffsetDateTime;

import java.util.List;

public class RecipeViewModel extends AndroidViewModel {

    private static final String TAG = RecipeViewModel.class.getSimpleName();
    private LiveData<List<RecipeStore>> mRecipeLiveData;
    private OffsetDateTime mLastInMemorySync;
    private boolean mNetworkError = false;
    private final AppDatabase mDb;

    public RecipeViewModel(@NonNull Application application) {
        super(application);
        mDb = AppDatabase.getDatabase(this.getApplication());
        mRecipeLiveData = mDb.recipeDao().getRecipes();
    }

    public LiveData<List<RecipeStore>> getRecipeLiveData() {
        return mRecipeLiveData;
    }

    public void fetchFromNetworkIfNeeded(DataManager dm) {

        // Hard-coded TTL of 1 day for now
        final OffsetDateTime threshold = OffsetDateTime.now().minusDays(1);

        if (mLastInMemorySync != null && mLastInMemorySync.isAfter(threshold)) {
            Log.d(TAG, "All recipes up to date - no network request performed");
            return;
        }

        List<RecipeStore> results = mRecipeLiveData.getValue();
        if (mNetworkError || results == null || results.size() == 0) {
            fetchFromNetwork(dm);
            return;
        }

        OffsetDateTime oldestLastSync = null;
        for (RecipeStore result : results) {
            final OffsetDateTime resultLastSync = result.getLastSync();
            if (resultLastSync.isBefore(threshold)) {
                Log.d(TAG, "result.lastSync: " + resultLastSync);
                fetchFromNetwork(dm);
                return;
            } else if (oldestLastSync == null || resultLastSync.isBefore(oldestLastSync)) {
                oldestLastSync = resultLastSync;
            }
        }
        mLastInMemorySync = oldestLastSync;
        Log.d(TAG, "All cached recipes up to date - no network request performed");
    }

    private void fetchFromNetwork(DataManager dm) {
        Log.d(TAG, "Fetching results from network");
        mNetworkError = false;
        dm.fetchRecipes();
    }

    public void recipeRequestSuccess() {
        mLastInMemorySync = OffsetDateTime.now();
        mNetworkError = false;
    }

    public boolean checkCachedDataOnNetworkError() {
        mLastInMemorySync = null;
        mNetworkError = true;
        return mRecipeLiveData.getValue().size() > 0;
    }

    public void retryFromNetworkIfNeeded(DataManager dataManager) {
        if (mNetworkError) {
            fetchFromNetworkIfNeeded(dataManager);
        }
    }
}

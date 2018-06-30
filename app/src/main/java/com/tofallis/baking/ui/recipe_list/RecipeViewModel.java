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
        if (results == null || results.size() == 0) {
            Log.d(TAG, "Fetching initial results from network");
            dm.fetchRecipes();
            return;
        }

        OffsetDateTime oldestLastSync = null;
        for (RecipeStore result : results) {
            final OffsetDateTime resultLastSync = result.getLastSync();
            if (resultLastSync.isBefore(threshold)) {
                Log.d(TAG, "Refreshing data from network. result.lastSync: " + resultLastSync);
                dm.fetchRecipes();
                return;
            } else if (oldestLastSync == null || resultLastSync.isBefore(oldestLastSync)) {
                oldestLastSync = resultLastSync;
            }
        }
        mLastInMemorySync = oldestLastSync;
        Log.d(TAG, "All cached recipes up to date - no network request performed");
    }

    public void recipeRequestSuccess() {
        mLastInMemorySync = OffsetDateTime.now();
    }

    public void recipeRequestError() {
        mLastInMemorySync = null;
    }
}

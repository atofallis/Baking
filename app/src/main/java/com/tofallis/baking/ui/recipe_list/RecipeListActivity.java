package com.tofallis.baking.ui.recipe_list;

import android.arch.lifecycle.ViewModelProviders;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.tofallis.baking.R;
import com.tofallis.baking.data.RecipeStore;
import com.tofallis.baking.network.DataManager;
import com.tofallis.baking.network.RecipeListener;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.AndroidInjection;

public class RecipeListActivity extends AppCompatActivity implements RecipeListener {

    private static final String TAG = RecipeListActivity.class.getName();

    @Inject
    DataManager mDataManager;

    @BindView(R.id.rvRecipes)
    RecyclerView mRecipes;

    private RecipeAdapter mAdapter;
    private RecipeViewModel mRecipeViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);

        mDataManager.addRecipeListener(this);
        mRecipeViewModel = ViewModelProviders.of(this).get(RecipeViewModel.class);
        mRecipeViewModel.getRecipeLiveData().observe(this, this::onNewRecipeData);
        setContentView(R.layout.recipe_list_layout);

        ButterKnife.bind(this);

        mAdapter = new RecipeAdapter(this);
        mRecipes.setAdapter(mAdapter);

        setRecyclerViewLayoutManager(getResources().getConfiguration());
    }

    @Override
    protected void onResume() {
        super.onResume();
        mRecipeViewModel.retryFromNetworkIfNeeded(mDataManager);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDataManager.removeRecipeListener(this);
    }

    private void setRecyclerViewLayoutManager(Configuration newConfig) {
        if (newConfig.screenWidthDp <= 640) {
            Log.d(TAG, "setRecyclerViewLayoutManager:: Phone");
            mRecipes.setLayoutManager(new GridLayoutManager(this, 1));
        } else {
            Log.d(TAG, "setRecyclerViewLayoutManager:: Tablet");
            mRecipes.setLayoutManager(new GridLayoutManager(this, 3));
        }
    }

    private void onNewRecipeData(List<RecipeStore> recipes) {
        mRecipeViewModel.fetchFromNetworkIfNeeded(mDataManager);
        mAdapter.setRecipes(recipes);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onNetworkSuccess() {
        mRecipeViewModel.recipeRequestSuccess();
    }

    @Override
    public void onNetworkError(Throwable error) {
        Log.d(TAG, "Network Error: " + error.getMessage());
        if (mRecipeViewModel.checkCachedDataOnNetworkError()) {
            Toast.makeText(this, R.string.toast_no_recipes, Toast.LENGTH_LONG).show();
        }
    }
}

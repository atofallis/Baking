package com.tofallis.baking.ui.recipe_list;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.tofallis.baking.R;
import com.tofallis.baking.api.listener.RecipeListener;
import com.tofallis.baking.network.DataManager;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);

        // TODO - call as part of Data manager network/DB sync
        mDataManager.addRecipeListener(this);
        mDataManager.fetchRecipes();

        setContentView(R.layout.recipe_list_layout);

        ButterKnife.bind(this);

        mAdapter = new RecipeAdapter(this);
        mRecipes.setAdapter(mAdapter);

        setRecyclerViewLayoutManager(getResources().getConfiguration());

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

    @Override
    public void onSuccess() {
        mAdapter.setRecipes(mDataManager.getRecipeResults());
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onNetworkError(Throwable error) {

    }
}

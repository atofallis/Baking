package com.tofallis.baking.ui.recipe_detail;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.tofallis.baking.R;
import com.tofallis.baking.data.AppDatabase;
import com.tofallis.baking.ui.RecipeIdViewModel;
import com.tofallis.baking.ui.RecipeViewModelFactory;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.AndroidInjection;

import static com.tofallis.baking.ui.RecipeConstants.EXTRA_RECIPE_ID;

public class RecipeDetailActivity extends AppCompatActivity {

    private static final String TAG = RecipeDetailActivity.class.getName();

    @BindView(R.id.rvSteps)
    RecyclerView mSteps;
    RecipeDetailAdapter mRecipeDetailAdapter;

    @BindView(R.id.rvIngredients)
    RecyclerView mIngredients;
    IngredientAdapter mIngredientAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.recipe_detail_step_list_layout);
        ButterKnife.bind(this);

        mRecipeDetailAdapter = new RecipeDetailAdapter(this);
        mSteps.setAdapter(mRecipeDetailAdapter);
        mSteps.setLayoutManager(new GridLayoutManager(this, 1));

        mIngredientAdapter = new IngredientAdapter();
        mIngredients.setAdapter(mIngredientAdapter);
        mIngredients.setLayoutManager(new LinearLayoutManager(this));

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(EXTRA_RECIPE_ID)) {
            // populate the UI
            RecipeViewModelFactory factory = new RecipeViewModelFactory(AppDatabase.getDatabase(getApplicationContext()), intent.getIntExtra(EXTRA_RECIPE_ID, -1));
            final RecipeIdViewModel steps = ViewModelProviders.of(this, factory).get(RecipeIdViewModel.class);
            steps.getStepLiveData().observe(this, stepStoreList -> {
                mRecipeDetailAdapter.setSteps(stepStoreList);
                mRecipeDetailAdapter.notifyDataSetChanged();
            });
            final RecipeIdViewModel ingredients = ViewModelProviders.of(this, factory).get(RecipeIdViewModel.class);
            ingredients.getIngredientLiveData().observe(this, ingredientStoreList -> {
                mIngredientAdapter.setIngredientStoreList(ingredientStoreList);
                mIngredientAdapter.notifyDataSetChanged();
            });
        }
    }
}

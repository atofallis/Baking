package com.tofallis.baking.ui.recipe_detail;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tofallis.baking.R;
import com.tofallis.baking.data.AppDatabase;
import com.tofallis.baking.ui.RecipeIdViewModel;
import com.tofallis.baking.ui.RecipeViewModelFactory;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecipeDetailFragment extends Fragment {

    private static final String BUNDLE_RECIPE_ID = "bundle_recipe_id";

    private int mRecipeId;

    @BindView(R.id.rvSteps)
    RecyclerView mSteps;
    RecipeDetailAdapter mRecipeDetailAdapter;

    @BindView(R.id.rvIngredients)
    RecyclerView mIngredients;
    IngredientAdapter mIngredientAdapter;

    @VisibleForTesting // flag any direct production usage as unexpected
    public RecipeDetailFragment() {
    }

    public static RecipeDetailFragment newInstance(int recipeId) {
        RecipeDetailFragment fragment = new RecipeDetailFragment();
        Bundle args = new Bundle();
        args.putInt(BUNDLE_RECIPE_ID, recipeId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Bundle args = getArguments();
        if (args == null) {
            throw new InstantiationError();
        }
        mRecipeId = Objects.requireNonNull(args.getInt(BUNDLE_RECIPE_ID));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.recipe_detail_fragment, container, false);
        ButterKnife.bind(this, rootView);

        if (savedInstanceState != null) {
            mRecipeId = Objects.requireNonNull(savedInstanceState.getInt(BUNDLE_RECIPE_ID));
        }

        mRecipeDetailAdapter = new RecipeDetailAdapter(getContext());
        mSteps.setAdapter(mRecipeDetailAdapter);
        mSteps.setLayoutManager(new GridLayoutManager(getContext(), 1));

        mIngredientAdapter = new IngredientAdapter();
        mIngredients.setAdapter(mIngredientAdapter);
        mIngredients.setLayoutManager(new LinearLayoutManager(getContext()));

        // populate the UI
        RecipeViewModelFactory factory = new RecipeViewModelFactory(AppDatabase.getDatabase(getContext().getApplicationContext()), mRecipeId);
        final RecipeIdViewModel recipe = ViewModelProviders.of(this, factory).get(RecipeIdViewModel.class);
        recipe.getStepLiveData().observe(this, stepStoreList -> {
            mRecipeDetailAdapter.setSteps(stepStoreList);
            mRecipeDetailAdapter.notifyDataSetChanged();
        });
        recipe.getIngredientLiveData().observe(this, ingredientStoreList -> {
            mIngredientAdapter.setIngredientStoreList(ingredientStoreList);
            mIngredientAdapter.notifyDataSetChanged();
        });

        return rootView;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(BUNDLE_RECIPE_ID, mRecipeId);
    }
}

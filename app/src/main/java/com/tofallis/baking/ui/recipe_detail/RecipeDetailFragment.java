package com.tofallis.baking.ui.recipe_detail;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
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

import static com.tofallis.baking.ui.RecipeConstants.EXTRA_RECIPE_ID;

public class RecipeDetailFragment extends Fragment {

    @BindView(R.id.rvIngredients)
    RecyclerView mIngredients;
    IngredientAdapter mIngredientAdapter;

    @BindView(R.id.rvSteps)
    RecyclerView mSteps;
    RecipeDetailAdapter mRecipeDetailAdapter;

    private int mRecipeId;
    OnStepClickListener mClickListener;

    private RecipeIdViewModel mRecipeViewModel;

    @VisibleForTesting // flag any direct production usage as unexpected
    public RecipeDetailFragment() {
    }

    public static RecipeDetailFragment newInstance(int recipeId) {
        RecipeDetailFragment fragment = new RecipeDetailFragment();
        Bundle args = new Bundle();
        args.putInt(EXTRA_RECIPE_ID, recipeId);
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
        mRecipeId = Objects.requireNonNull(args.getInt(EXTRA_RECIPE_ID));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mClickListener = (OnStepClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnStepClickListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.recipe_detail_fragment, container, false);
        ButterKnife.bind(this, rootView);

        if (savedInstanceState != null) {
            mRecipeId = Objects.requireNonNull(savedInstanceState.getInt(EXTRA_RECIPE_ID));
        }

        mRecipeDetailAdapter = new RecipeDetailAdapter(mClickListener);
        mSteps.setAdapter(mRecipeDetailAdapter);
        mSteps.setLayoutManager(new GridLayoutManager(getContext(), 1));

        mIngredientAdapter = new IngredientAdapter();
        mIngredients.setAdapter(mIngredientAdapter);
        mIngredients.setLayoutManager(new LinearLayoutManager(getContext()));

        // populate the UI
        setupViewModel();
        return rootView;
    }

    // Util to refresh views on RecipeStepActivity -> RecipeDetailActivity 'up' navigation on Phone
    public void refreshViews() {
        if (mRecipeViewModel == null) {
            setupViewModel();
        } else {
            mRecipeDetailAdapter.setSteps(mRecipeViewModel.getStepLiveData().getValue());
            mRecipeDetailAdapter.notifyDataSetChanged();
            mIngredientAdapter.setIngredientStoreList(mRecipeViewModel.getIngredientLiveData().getValue());
            mIngredientAdapter.notifyDataSetChanged();
        }
    }

    private void setupViewModel() {
        RecipeViewModelFactory factory = new RecipeViewModelFactory(AppDatabase.getDatabase(getContext().getApplicationContext()), mRecipeId);
        mRecipeViewModel = ViewModelProviders.of(this, factory).get(RecipeIdViewModel.class);
        mRecipeViewModel.getStepLiveData().observe(this, stepStoreList -> {
            mRecipeDetailAdapter.setSteps(stepStoreList);
            mRecipeDetailAdapter.notifyDataSetChanged();
        });
        mRecipeViewModel.getIngredientLiveData().observe(this, ingredientStoreList -> {
            mIngredientAdapter.setIngredientStoreList(ingredientStoreList);
            mIngredientAdapter.notifyDataSetChanged();
        });
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(EXTRA_RECIPE_ID, mRecipeId);
    }

    public interface OnStepClickListener {
        void onRecipeStepClicked(int position);
    }
}

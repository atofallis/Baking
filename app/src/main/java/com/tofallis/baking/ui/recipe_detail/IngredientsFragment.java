package com.tofallis.baking.ui.recipe_detail;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tofallis.baking.R;
import com.tofallis.baking.data.IngredientStore;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static android.view.View.GONE;
import static com.tofallis.baking.ui.RecipeIngredientsWidget.MAX_INGREDIENTS_TO_DISPLAY;
import static com.tofallis.baking.ui.RecipeIngredientsWidget.getIngredientRowResourceIdFromIndex;

public class IngredientsFragment extends Fragment {

    private static final String BUNDLE_RECIPE_ID = "bundle_recipe_id";
    private static final String BUNDLE_RECIPE_NAME = "bundle_recipe_name";
    private static final String BUNDLE_INGREDIENTS = "bundle_ingredients";

    private int mRecipeId;
    private String mRecipeName;
    private List<IngredientStore> mIngredientStoreList = new ArrayList<>();

    @VisibleForTesting // flag any direct production usage as unexpected
    public IngredientsFragment() {
    }

    public static IngredientsFragment newInstance(List<IngredientStore> ingredientStores, String recipeName) {
        IngredientsFragment fragment = new IngredientsFragment();
        Bundle args = new Bundle();
        args.putInt(BUNDLE_RECIPE_ID, ingredientStores.get(0).getRecipeId());
        args.putString(BUNDLE_RECIPE_NAME, recipeName);
        args.putParcelableArrayList(BUNDLE_INGREDIENTS, (ArrayList<? extends Parcelable>) ingredientStores);
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
        mRecipeName = Objects.requireNonNull(args.getString(BUNDLE_RECIPE_NAME));
        mIngredientStoreList = args.getParcelableArrayList(BUNDLE_INGREDIENTS);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.recipe_ingredients_widget, container, false);

        if (savedInstanceState != null) {
            mRecipeId = Objects.requireNonNull(savedInstanceState.getInt(BUNDLE_RECIPE_ID));
            mRecipeName = Objects.requireNonNull(savedInstanceState.getString(BUNDLE_RECIPE_NAME));
        }

        TextView recipeName = rootView.findViewById(R.id.recipe_title);
        recipeName.setText(mRecipeName);

        // TODO - make sure we display all the ingredients!!
        for (int i = 0; i < mIngredientStoreList.size() && i < MAX_INGREDIENTS_TO_DISPLAY; i++) {
            final IngredientStore ingredient = mIngredientStoreList.get(i);
            View rowParent = rootView.findViewById(getIngredientRowResourceIdFromIndex(i));
            TextView ingredientText = rowParent.findViewById(R.id.ingredient);
            ingredientText.setText(ingredient.getIngredient());
            TextView quantityText = rowParent.findViewById(R.id.quantity);
            quantityText.setText(getContext().getString(
                    R.string.appwidget_quantity_text,
                    ingredient.getQuantity(),
                    ingredient.getMeasure()));
        }
        if (mIngredientStoreList.size() >= MAX_INGREDIENTS_TO_DISPLAY) {
            View rowParent = rootView.findViewById(R.id.last_row);
            TextView ingredientText = rowParent.findViewById(R.id.ingredient);
            ingredientText.setVisibility(GONE);
            TextView quantityText = rowParent.findViewById(R.id.quantity);
            quantityText.setVisibility(GONE);
            TextView ingredientComment = rowParent.findViewById(R.id.ingredient_comment);
            ingredientComment.setText(getContext().getString(R.string.appwidget_text_see_more));
            ingredientComment.setVisibility(View.VISIBLE);
        }
        return rootView;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(BUNDLE_RECIPE_ID, mRecipeId);
        outState.putString(BUNDLE_RECIPE_NAME, mRecipeName);
        outState.putParcelableArrayList(BUNDLE_INGREDIENTS, (ArrayList<? extends Parcelable>) mIngredientStoreList);
    }
}

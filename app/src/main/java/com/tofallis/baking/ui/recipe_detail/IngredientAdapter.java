package com.tofallis.baking.ui.recipe_detail;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tofallis.baking.R;
import com.tofallis.baking.data.IngredientStore;

import java.util.ArrayList;
import java.util.List;

public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.ViewHolder> {

    private static final String TAG = IngredientAdapter.class.getSimpleName();
    // store list of Recipes from the api query.
    private List<IngredientStore> mIngredientStoreList = new ArrayList<>();

    public void setIngredientStoreList(List<IngredientStore> ingredientStoreList) {
        mIngredientStoreList = ingredientStoreList;
    }

    @NonNull
    @Override
    public IngredientAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View stepsView = inflater.inflate(R.layout.recipe_ingredients_card, parent, false);

        // Return a new holder instance
        return new ViewHolder(stepsView);
    }

    @Override
    public void onBindViewHolder(@NonNull IngredientAdapter.ViewHolder holder, final int position) {

        final IngredientStore ingredient = mIngredientStoreList.get(position);
        holder.mQuantity.setText(holder.mQuantity.getContext().getString(
                R.string.appwidget_quantity_text,
                ingredient.getQuantity(),
                ingredient.getMeasure()));
        holder.mIngredient.setText(ingredient.getIngredient());
    }

    @Override
    public int getItemCount() {
        return mIngredientStoreList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView mIngredient;
        TextView mQuantity;

        ViewHolder(View itemView) {
            super(itemView);
            mIngredient = itemView.findViewById(R.id.ingredient);
            mQuantity = itemView.findViewById(R.id.quantity);
        }
    }
}

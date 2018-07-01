package com.tofallis.baking.ui.recipe_detail;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tofallis.baking.R;
import com.tofallis.baking.data.StepStore;
import com.tofallis.baking.ui.RecipeConstants;

import java.util.ArrayList;
import java.util.List;

public class RecipeDetailAdapter extends RecyclerView.Adapter<RecipeDetailAdapter.ViewHolder> {

    private static final String TAG = RecipeDetailAdapter.class.getSimpleName();
    private Context mContext;
    // store list of Recipes from the api query.
    private List<StepStore> mSteps = new ArrayList<>();

    private List<StepStore> getSteps() {
        return mSteps;
    }

    public void setSteps(List<StepStore> steps) {
        mSteps = steps;
    }

    public RecipeDetailAdapter(Context context) {
        mContext = context;
    }

    @NonNull
    @Override
    public RecipeDetailAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View stepsView = inflater.inflate(R.layout.recipe_detail_step_card, parent, false);

        // Return a new holder instance
        return new ViewHolder(stepsView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeDetailAdapter.ViewHolder holder, final int position) {

        final StepStore s = mSteps.get(position);
        final String stepShortDescription = s.getShortDescription();
        holder.mStepShortDescription.setText(stepShortDescription);
    }

    @Override
    public int getItemCount() {
        return mSteps.size();
    }

    /**
     * Creates a new ImageView for each item referenced by the adapter
     */

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView mStepShortDescription;

        ViewHolder(View itemView) {
            super(itemView);
            mStepShortDescription = itemView.findViewById(R.id.recipe_step_short_description);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                int recipeId = getSteps().get(position).getRecipeId();
                Intent intent = new Intent(mContext, RecipeDetailActivity.class);
                intent.putExtra(RecipeConstants.EXTRA_RECIPE_ID, recipeId);
                mContext.startActivity(intent);
            }
        }
    }
}
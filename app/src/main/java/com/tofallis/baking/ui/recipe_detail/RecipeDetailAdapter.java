package com.tofallis.baking.ui.recipe_detail;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tofallis.baking.R;
import com.tofallis.baking.data.StepStore;
import com.tofallis.baking.ui.recipe_step.RecipeStepActivity;

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

    RecipeDetailAdapter(Context context) {
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

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView mStepShortDescription;

        ViewHolder(View itemView) {
            super(itemView);
            mStepShortDescription = itemView.findViewById(R.id.recipe_step_short_description);

            mStepShortDescription.setOnClickListener(v -> {
                int position = getAdapterPosition();
                Log.d(TAG, "Pos: " + position);
                if (position != RecyclerView.NO_POSITION) {
                    RecipeStepActivity.startStepActivity(mContext, position, getSteps().get(position).getRecipeId());
                }
            });
        }
    }
}

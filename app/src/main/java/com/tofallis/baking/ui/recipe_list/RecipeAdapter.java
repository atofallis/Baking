package com.tofallis.baking.ui.recipe_list;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.DrawableRes;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.tofallis.baking.R;
import com.tofallis.baking.data.Recipe;
import com.tofallis.baking.ui.RecipeDetailActivity;

import java.util.ArrayList;
import java.util.List;

import static android.util.TypedValue.COMPLEX_UNIT_DIP;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.ViewHolder> {

    private static final String TAG = RecipeAdapter.class.getSimpleName();
    private Context mContext;
    // store list of Movies from the api query.
    private List<Recipe> mRecipes = new ArrayList<>();

    public List<Recipe> getRecipes() {
        return mRecipes;
    }

    public void setRecipes(List<Recipe> recipes) {
        mRecipes = recipes;
        Log.i(TAG, "Recipes:: " + mRecipes.toString());
    }

    public RecipeAdapter(Context context) {
        mContext = context;
    }

    @Override
    public RecipeAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View recipeView = inflater.inflate(R.layout.recipe_card, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(recipeView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecipeAdapter.ViewHolder holder, final int position) {

        final Recipe r = mRecipes.get(position);
        final String recipeName = r.getName();
        holder.mRecipeTitle.setText(recipeName);
//        if(recipeName.length() >= 10) {
//            holder.mRecipeTitle.setTextSize(COMPLEX_UNIT_DIP, 60);
//        }
        holder.mNumServings.setText(mContext.getString(R.string.servings, r.getServings()));

        if (holder.mBackgroundImage == null) {
            // If the view is not recycled, this creates a new ImageView to hold an image
            holder.mBackgroundImage = new ImageView(mContext);
            holder.mBackgroundImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
        }

        String url = r.getImage();
        final @DrawableRes int placeholder = R.drawable.ic_launcher_background;
        if (url == null || url.isEmpty()) {
            holder.mBackgroundImage.setImageResource(placeholder);
        } else {
            Picasso.with(mContext)
                    .load(url)
                    .placeholder(placeholder) // TODO - better placeholders
                    .error(R.drawable.ic_launcher_foreground)
                    .into(holder.mBackgroundImage);
        }
    }

    @Override
    public int getItemCount() {
        return mRecipes.size();
    }

    /**
     * Creates a new ImageView for each item referenced by the adapter
     */

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView mBackgroundImage;
        TextView mRecipeTitle;
        TextView mNumServings;

        ViewHolder(View itemView) {
            super(itemView);
            mBackgroundImage = itemView.findViewById(R.id.background_image);
            mBackgroundImage.setOnClickListener(this);
            mRecipeTitle = itemView.findViewById(R.id.recipe_title_text);
            mNumServings = itemView.findViewById(R.id.num_servings_text);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                Recipe r = getRecipes().get(position);
                Intent intent = new Intent(mContext, RecipeDetailActivity.class);
                // TODO... intent.putExtra(Recipe.ID, r.getId());
                mContext.startActivity(intent);
            }
        }
    }
}

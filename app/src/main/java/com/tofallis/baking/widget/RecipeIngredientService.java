package com.tofallis.baking.widget;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.tofallis.baking.R;
import com.tofallis.baking.provider.RecipeIngredientContract;
import com.tofallis.baking.ui.RecipeConstants;

import static com.tofallis.baking.provider.RecipeIngredientContract.BASE_CONTENT_URI;
import static com.tofallis.baking.provider.RecipeIngredientContract.PATH_RECIPES;
import static com.tofallis.baking.ui.RecipeConstants.EXTRA_RECIPE_ID;

public class RecipeIngredientService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new GridRemoteViewsFactory(this.getApplicationContext());
    }
}

class GridRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    Context mContext;
    Cursor mCursor;

    public GridRemoteViewsFactory(Context context) {
        mContext = context;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {
        // Query the list of all recipes
        Uri contentQuery = BASE_CONTENT_URI.buildUpon().appendPath(PATH_RECIPES).build();
        if (mCursor != null) {
            mCursor.close();
        }
        mCursor = mContext.getContentResolver().query(
                contentQuery,
                null,
                null,
                null,
                null);
    }

    @Override
    public void onDestroy() {
        if (mCursor != null) {
            mCursor.close();
        }
    }

    @Override
    public int getCount() {
        if (mCursor == null) {
            return 0;
        }
        return mCursor.getCount();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        // Create a RemoteView using the plant_widget layout,
        // setting the fillInIntent for widget_plant_image with the plant ID as extras
        if (getCount() == 0) {
            return null;
        }
        // get recipe ingredient by position
        mCursor.moveToPosition(position);
        int idIndex = mCursor.getColumnIndex(RecipeIngredientContract.IngredientEntry.COLUMN_RECIPE_ID);
        int qtyIndex = mCursor.getColumnIndex(RecipeIngredientContract.IngredientEntry.COLUMN_QUANTITY);
        int measureIndex = mCursor.getColumnIndex(RecipeIngredientContract.IngredientEntry.COLUMN_MEASURE);
        int ingredientIndex = mCursor.getColumnIndex(RecipeIngredientContract.IngredientEntry.COLUMN_INGREDIENT);

        int recipeId = mCursor.getInt(idIndex);
        double quantity = mCursor.getDouble(qtyIndex);
        String measure = mCursor.getString(measureIndex);
        String ingredient = mCursor.getString(ingredientIndex);

        RemoteViews ingredientRow = new RemoteViews(mContext.getPackageName(), R.layout.ingredient_row_layout);

        ingredientRow.setTextViewText(R.id.recipe_number, Integer.toString(recipeId));
        ingredientRow.setTextViewText(R.id.ingredient, ingredient);
        ingredientRow.setTextViewText(R.id.quantity, mContext.getString(
                R.string.appwidget_quantity_text, quantity, measure));

        // Fill in the onClick PendingIntent Template using the specific recipeId
        Bundle extras = new Bundle();
        extras.putInt(EXTRA_RECIPE_ID, recipeId);
        final String recipeWidgetTitle = "Recipe #" + recipeId;
        extras.putString(RecipeConstants.EXTRA_RECIPE_NAME, recipeWidgetTitle);
        Intent fillInIntent = new Intent();
        fillInIntent.putExtras(extras);
        ingredientRow.setOnClickFillInIntent(R.id.ingredient_widget_row, fillInIntent);

        return ingredientRow;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1; // Treat all items in the GridView the same
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}

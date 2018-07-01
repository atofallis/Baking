package com.tofallis.baking.ui;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.IdRes;
import android.view.View;
import android.widget.RemoteViews;

import com.tofallis.baking.R;
import com.tofallis.baking.network.Ingredient;
import com.tofallis.baking.network.Recipe;
import com.tofallis.baking.ui.recipe_list.RecipeListActivity;

/**
 * Implementation of App Widget functionality.
 */
public class RecipeIngredientsWidget extends AppWidgetProvider {

    private static final int MAX_INGREDIENTS_TO_DISPLAY = 10;

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId, Recipe recipe) {
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.recipe_ingredients_widget);
        views.setTextViewText(R.id.recipe_title, recipe.getName());

        views.removeAllViews(R.id.row_1);
        views.removeAllViews(R.id.row_2);
        views.removeAllViews(R.id.row_3);
        views.removeAllViews(R.id.row_4);
        views.removeAllViews(R.id.row_5);
        views.removeAllViews(R.id.row_6);
        views.removeAllViews(R.id.row_7);
        views.removeAllViews(R.id.row_8);
        views.removeAllViews(R.id.row_9);
        views.removeAllViews(R.id.last_row);

        RemoteViews firstRow = new RemoteViews(context.getPackageName(), R.layout.ingredient_row_comment_layout);
        if (recipe.getIngredients() == null || recipe.getIngredients().size() == 0) {
            firstRow.setTextViewText(R.id.ingredient_comment, context.getString(R.string.appwidget_text_error_no_ingredients));
            firstRow.setViewVisibility(R.id.ingredient_comment, View.VISIBLE);
            firstRow.setViewVisibility(R.id.ingredient, View.GONE);
            firstRow.setViewVisibility(R.id.quantity, View.GONE);
            views.addView(R.id.row_1, firstRow);
        } else {
            RemoteViews row = new RemoteViews(context.getPackageName(), R.layout.ingredient_row_layout);
            for (int i=0; i < recipe.getIngredients().size() && i < MAX_INGREDIENTS_TO_DISPLAY; i++) {
                final Ingredient ingredient = recipe.getIngredients().get(i);
                row.setTextViewText(R.id.ingredient, ingredient.getIngredient());
                row.setTextViewText(R.id.quantity, context.getString(
                                R.string.appwidget_quantity_text,
                                ingredient.getQuantity(),
                                ingredient.getMeasure()));
                views.addView(getIngredientRowResourceIdFromIndex(i), row);
            }
            if (recipe.getIngredients().size() >= MAX_INGREDIENTS_TO_DISPLAY) {
                RemoteViews lastRow = new RemoteViews(context.getPackageName(), R.layout.ingredient_row_comment_layout);
                lastRow.setTextViewText(R.id.ingredient_comment, context.getString(R.string.appwidget_text_see_more));
                lastRow.setViewVisibility(R.id.ingredient_comment, View.VISIBLE);
                lastRow.setViewVisibility(R.id.ingredient, View.GONE);
                lastRow.setViewVisibility(R.id.quantity, View.GONE);
                views.addView(R.id.last_row, lastRow);
            }
        }

        Intent i = new Intent(context, RecipeListActivity.class);
        PendingIntent pi = PendingIntent.getActivity(context, 0, i, 0);
        views.setOnClickPendingIntent(R.id.widget_layout, pi);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            // TODO - set recipe correctly!
            updateAppWidget(context, appWidgetManager, appWidgetId, new Recipe());
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    private static @IdRes int getIngredientRowResourceIdFromIndex(int index) {
        switch (index) {
            case 0:
                return R.id.row_1;
            case 1:
                return R.id.row_2;
            case 2:
                return R.id.row_3;
            case 3:
                return R.id.row_4;
            case 4:
                return R.id.row_5;
            case 5:
                return R.id.row_6;
            case 6:
                return R.id.row_7;
            case 7:
                return R.id.row_8;
            case 8:
                return R.id.row_9;
            case 9: // save the last row for "Click to see more" text or equivalent
            default:
                throw new IndexOutOfBoundsException("Only expecting " + MAX_INGREDIENTS_TO_DISPLAY + " ingredient rows");
        }
    }
}


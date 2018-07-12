package com.tofallis.baking.widget;

import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.tofallis.baking.R;
import com.tofallis.baking.ui.recipe_detail.RecipeDetailActivity;
import com.tofallis.baking.ui.recipe_list.RecipeListActivity;

/**
 * Implementation of App Widget functionality.
 */
public class RecipeIngredientWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.recipe_ingredients_widget);

        // click event handler for the title, launches recipe list
        Intent i = new Intent(context, RecipeListActivity.class);
        PendingIntent pi = PendingIntent.getActivity(context, 0, i, 0);
        views.setOnClickPendingIntent(R.id.widget_layout, pi);

        Intent intent = new Intent(context, RecipeIngredientService.class);
        views.setRemoteAdapter(R.id.widgetListView, intent);

        // template to handle the click listener for each item
        Intent clickIntentTemplate = new Intent(context, RecipeDetailActivity.class);
        PendingIntent clickPendingIntentTemplate = TaskStackBuilder.create(context)
                .addNextIntentWithParentStack(clickIntentTemplate)
                .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setPendingIntentTemplate(R.id.widgetListView, clickPendingIntentTemplate);

        views.setEmptyView(R.id.widgetListView, R.id.widget_fallback);
        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
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

    public static void sendRefreshBroadcast(Context context) {
        Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        intent.setComponent(new ComponentName(context, RecipeIngredientWidget.class));
        context.sendBroadcast(intent);
    }

    @Override
    public void onReceive(final Context context, Intent intent) {
        final String action = intent.getAction();
        if (action.equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE)) {
            // refresh all your widgets
            AppWidgetManager mgr = AppWidgetManager.getInstance(context);
            ComponentName cn = new ComponentName(context, RecipeIngredientWidget.class);
            mgr.notifyAppWidgetViewDataChanged(mgr.getAppWidgetIds(cn), R.id.widgetListView);
        }
        super.onReceive(context, intent);
    }
}


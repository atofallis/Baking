package com.tofallis.baking.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.tofallis.baking.data.AppDatabase;
import com.tofallis.baking.data.IngredientDao;

public class RecipeIngredientContentProvider extends ContentProvider {

    // Define final integer constants for the directory of ingredients.
    // It's convention to use 100, 200, 300, etc for directories,
    // and related ints (101, 102, ..) for items in that directory.
    public static final int RECIPE_INGREDIENTS = 100;
    public static final int RECIPE_INGREDIENTS_WITH_ID = 101;

    // Declare a static variable for the Uri matcher that you construct
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private static final String TAG = RecipeIngredientContentProvider.class.getName();
    private IngredientDao mIngredientDao;

    // Define a static buildUriMatcher method that associates URI's with their int match

    public static UriMatcher buildUriMatcher() {
        // Initialize a UriMatcher
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        // Add URI matches
        uriMatcher.addURI(RecipeIngredientContract.AUTHORITY, RecipeIngredientContract.PATH_RECIPES, RECIPE_INGREDIENTS);
        uriMatcher.addURI(RecipeIngredientContract.AUTHORITY, RecipeIngredientContract.PATH_RECIPES + "/#", RECIPE_INGREDIENTS_WITH_ID);
        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        mIngredientDao = AppDatabase.getDatabase(getContext()).ingredientDao();
        return true;
    }

    /***
     * Handles requests for data by URI
     *
     * @param uri
     * @param projection
     * @param selection
     * @param selectionArgs
     * @param sortOrder
     * @return
     */
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        // Write URI match code and set a variable to return a Cursor
        int match = sUriMatcher.match(uri);
        final Cursor retCursor;
        switch (match) {
            case RECIPE_INGREDIENTS:
                retCursor = mIngredientDao.selectAll();
                break;
            case RECIPE_INGREDIENTS_WITH_ID:
                retCursor = mIngredientDao.selectById((int) ContentUris.parseId(uri));
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public String getType(@NonNull Uri uri) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        throw new UnsupportedOperationException("Not implemented");
    }
}

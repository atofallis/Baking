package com.tofallis.baking.provider;

import android.net.Uri;
import android.provider.BaseColumns;

public class RecipeIngredientContract {
    // The authority, which is how your code knows which Content Provider to access
    public static final String AUTHORITY = "com.tofallis.baking";

    // The base content URI = "content://" + <authority>
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    // Define the possible paths for accessing data in this contract
    public static final String PATH_RECIPES = "recipes";

    public static final long INVALID_RECIPE_ID = -1;

    public static final class IngredientEntry implements BaseColumns {

        // TaskEntry content URI = base content URI + path
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_RECIPES).build();

        public static final String TABLE_NAME = "ingredient";
        public static final String COLUMN_QUANTITY = "quantity";
        public static final String COLUMN_MEASURE = "measure";
        public static final String COLUMN_INGREDIENT = "ingredient";
        public static final String COLUMN_RECIPE_ID = "recipeId";
    }
}

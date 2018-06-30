package com.tofallis.baking.data;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

@Database(entities = {RecipeStore.class, IngredientStore.class, StepStore.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase INSTANCE;

    public abstract RecipeDao recipeDao();
    public abstract IngredientDao ingredientDao();
    public abstract StepDao stepDao();

    public static AppDatabase getDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE =
                    // TODO - use persistent DB once we expect version 1 to be stable
//                Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "Baking")
                    Room.inMemoryDatabaseBuilder(context.getApplicationContext(), AppDatabase.class)
                        .build();
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        if(INSTANCE != null) {
            INSTANCE.close();
        }
        INSTANCE = null;
    }
}

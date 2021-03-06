package com.tofallis.baking.data;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.TypeConverters;
import android.database.Cursor;

import java.util.List;

@TypeConverters({DateConverter.class})
@Dao
public interface IngredientDao {

    // query for main app
    @Query("SELECT * FROM ingredient WHERE recipeId = :recipeId")
    LiveData<List<IngredientStore>> getIngredients(int recipeId);

    // query for widget <=> content provider (single recipe)
    @Query("SELECT * FROM ingredient WHERE recipeId = :recipeId")
    Cursor selectById(int recipeId);

    // query for widget <=> content provider (all recipes)
    @Query("SELECT * FROM ingredient ORDER BY recipeId, id")
    Cursor selectAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void updateIngredients(IngredientStore... ingredient);

    @Query("DELETE FROM ingredient WHERE recipeId = :recipeId")
    void deleteRecipeIngredients(int recipeId);
}

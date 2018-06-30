package com.tofallis.baking.data;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.TypeConverters;

@TypeConverters({DateConverter.class})
@Dao
public interface IngredientDao {

    @Query("SELECT * FROM ingredient WHERE recipeId = :recipeId")
    LiveData<IngredientStore> getIngredients(int recipeId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void updateIngredients(IngredientStore... ingredient);

    @Query("DELETE FROM ingredient WHERE recipeId = :recipeId")
    void deleteRecipeIngredients(int recipeId);
}

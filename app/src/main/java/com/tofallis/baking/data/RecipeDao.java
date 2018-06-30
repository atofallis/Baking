package com.tofallis.baking.data;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.TypeConverters;

import java.util.List;

@TypeConverters({DateConverter.class})
@Dao
public interface RecipeDao {

    @Query("SELECT * FROM recipe")
    LiveData<List<RecipeStore>> getRecipes();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void updateRecipe(RecipeStore... recipe);

    @Query("DELETE FROM recipe")
    void deleteAll();
}

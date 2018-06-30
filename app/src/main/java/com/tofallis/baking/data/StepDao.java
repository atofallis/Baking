package com.tofallis.baking.data;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.TypeConverters;

@TypeConverters({DateConverter.class})
@Dao
public interface StepDao {

    @Query("SELECT * FROM step WHERE recipeId = :recipeId")
    LiveData<StepStore> getSteps(int recipeId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void updateSteps(StepStore... step);

    @Query("DELETE FROM step WHERE recipeId = :recipeId")
    void deleteRecipeSteps(int recipeId);
}

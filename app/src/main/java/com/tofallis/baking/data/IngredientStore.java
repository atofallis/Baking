package com.tofallis.baking.data;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import static android.arch.persistence.room.ForeignKey.CASCADE;

@Entity(tableName = "ingredient", foreignKeys = {
        @ForeignKey(entity = RecipeStore.class,
                parentColumns = "id",
                childColumns = "recipeId",
                onDelete = CASCADE)})
public class IngredientStore {

    @PrimaryKey(autoGenerate = true)
    public int id;

    private String quantity;

    private String measure;

    private String ingredient;

    private int recipeId;

    @Ignore
    public IngredientStore() {
        // allow lazy init but explicitly @Ignore for Room.
    }

    public IngredientStore(int id, String quantity, String measure, String ingredient, int recipeId) {
        this.id = id;
        this.quantity = quantity;
        this.measure = measure;
        this.ingredient = ingredient;
        this.recipeId = recipeId;
    }

    public int getId() {
        return id;
    }

    public String getQuantity() {
        return quantity;
    }

    public String getMeasure() {
        return measure;
    }

    public String getIngredient() {
        return ingredient;
    }

    public int getRecipeId() {
        return recipeId;
    }

//    public void setId(int id) {
//        this.id = id;
//    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public void setMeasure(String measure) {
        this.measure = measure;
    }

    public void setIngredient(String ingredient) {
        this.ingredient = ingredient;
    }

    public void setRecipeId(int recipeId) {
        this.recipeId = recipeId;
    }
}

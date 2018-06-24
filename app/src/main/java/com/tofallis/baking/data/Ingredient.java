package com.tofallis.baking.data;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Ingredient {
    @SerializedName("quantity") private String quantity;

    @SerializedName("measure") private String measure;

    @SerializedName("ingredients") private List<Ingredient> ingredients;

    public String getQuantity() {
        return quantity;
    }

    public String getMeasure() {
        return measure;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }
}

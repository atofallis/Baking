package com.tofallis.baking.network;

import com.google.gson.annotations.SerializedName;

public class Ingredient {
    @SerializedName("quantity") private String quantity;

    @SerializedName("measure") private String measure;

    @SerializedName("ingredient") private String ingredient;

    public String getQuantity() {
        return quantity;
    }

    public String getMeasure() {
        return measure;
    }

    public String getIngredient() {
        return ingredient;
    }
}

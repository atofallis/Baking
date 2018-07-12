package com.tofallis.baking.data;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

import static android.arch.persistence.room.ForeignKey.CASCADE;
import static com.tofallis.baking.provider.RecipeIngredientContract.IngredientEntry.TABLE_NAME;

@Entity(tableName = TABLE_NAME, foreignKeys = {
        @ForeignKey(entity = RecipeStore.class,
                parentColumns = "id",
                childColumns = "recipeId",
                onDelete = CASCADE)})
public class IngredientStore implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    public int id;

    private double quantity;

    private String measure;

    private String ingredient;

    private int recipeId;

    @Ignore
    public IngredientStore() {
        // allow lazy init but explicitly @Ignore for Room.
    }

    public IngredientStore(int id, double quantity, String measure, String ingredient, int recipeId) {
        this.id = id;
        this.quantity = quantity;
        this.measure = measure;
        this.ingredient = ingredient;
        this.recipeId = recipeId;
    }

    protected IngredientStore(Parcel in) {
        id = in.readInt();
        quantity = in.readDouble();
        measure = in.readString();
        ingredient = in.readString();
        recipeId = in.readInt();
    }

    public static final Creator<IngredientStore> CREATOR = new Creator<IngredientStore>() {
        @Override
        public IngredientStore createFromParcel(Parcel in) {
            return new IngredientStore(in);
        }

        @Override
        public IngredientStore[] newArray(int size) {
            return new IngredientStore[size];
        }
    };

    public int getId() {
        return id;
    }

    public double getQuantity() {
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

    public void setQuantity(double quantity) {
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeDouble(quantity);
        dest.writeString(measure);
        dest.writeString(ingredient);
        dest.writeInt(recipeId);
    }
}

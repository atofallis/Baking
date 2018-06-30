package com.tofallis.baking.data;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import org.threeten.bp.OffsetDateTime;

@TypeConverters({DateConverter.class})
@Entity(tableName = "recipe")
public class RecipeStore {

    @PrimaryKey private int id;
    private String name;
    private int servings;
    private String image;
    private OffsetDateTime lastSync;

    @Ignore
    public RecipeStore() {
        throw new UnsupportedOperationException();
    }

    public RecipeStore(int id, String name, int servings, String image, OffsetDateTime lastSync) {
        this.id = id;
        this.name = name;
        this.servings = servings;
        this.image = image;
        this.lastSync = lastSync;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getServings() {
        return servings;
    }

    public String getImage() {
        return image;
    }

    public OffsetDateTime getLastSync() {
        return lastSync;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setServings(int servings) {
        this.servings = servings;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setLastSync(OffsetDateTime sync) {
        lastSync = sync;
    }
}

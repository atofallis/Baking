package com.tofallis.baking.data;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import static android.arch.persistence.room.ForeignKey.CASCADE;

@Entity(tableName = "step", foreignKeys = {
        @ForeignKey(entity = RecipeStore.class,
                parentColumns = "id",
                childColumns = "recipeId",
                onDelete = CASCADE)})
public class StepStore {

    @PrimaryKey(autoGenerate = true)
    public int uid;

    private int id;

    private String shortDescription;

    private String description;

    private String videoUrl;

    private String thumbnailUrl;

    private int recipeId;

    @Ignore
    public StepStore() {
        // allow lazy init but explicitly @Ignore for Room
    }

    public StepStore(int uid, int id, String shortDescription, String description, String videoUrl, int recipeId) {
        this.uid = uid;
        this.id = id;
        this.shortDescription = shortDescription;
        this.description = description;
        this.videoUrl = videoUrl;
        this.recipeId = recipeId;
    }

    public int getUid() {
        return uid;
    }

    public int getId() {
        return id;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public String getDescription() {
        return description;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public int getRecipeId() {
        return recipeId;
    }

//    public void setUid(int uid) {
//        this.uid = uid;
//    }

    public void setId(int id) {
        this.id = id;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public void setRecipeId(int recipeId) {
        this.recipeId = recipeId;
    }
}

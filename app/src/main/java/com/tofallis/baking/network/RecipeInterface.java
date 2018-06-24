package com.tofallis.baking.network;

import com.tofallis.baking.data.Recipe;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;

public interface RecipeInterface {
    @GET("android-baking-app-json")
    Observable<List<Recipe>> getRecipes();
}

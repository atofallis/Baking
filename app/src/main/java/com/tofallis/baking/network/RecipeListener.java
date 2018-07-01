package com.tofallis.baking.network;

public interface RecipeListener {
    void onNetworkSuccess();
    void onNetworkError(Throwable error);
}

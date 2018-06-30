package com.tofallis.baking.api.listener;

public interface RecipeListener {
    void onNetworkSuccess();
    void onNetworkError(Throwable error);
}

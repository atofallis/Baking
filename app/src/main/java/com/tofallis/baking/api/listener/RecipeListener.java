package com.tofallis.baking.api.listener;

public interface RecipeListener {
    void onSuccess();
    void onNetworkError(Throwable error);
}

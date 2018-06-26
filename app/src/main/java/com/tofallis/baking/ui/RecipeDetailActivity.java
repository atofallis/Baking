package com.tofallis.baking.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.tofallis.baking.network.DataManager;

import javax.inject.Inject;

import dagger.android.AndroidInjection;

public class RecipeDetailActivity extends AppCompatActivity {

    @Inject
    DataManager mDataManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
    }
}

package com.tofallis.baking;

import android.app.Activity;
import android.app.Application;

import com.tofallis.baking.di.DaggerRecipeApplicationComponent;

import javax.inject.Inject;

import dagger.Module;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasActivityInjector;

@Module
public class RecipeApplication extends Application implements HasActivityInjector {

    @Inject
    DispatchingAndroidInjector<Activity> dai;

    @Override
    public void onCreate() {
        super.onCreate();
        DaggerRecipeApplicationComponent.builder().application(this).build().inject(this);
    }

    @Override
    public DispatchingAndroidInjector<Activity> activityInjector() {
        return dai;
    }
}

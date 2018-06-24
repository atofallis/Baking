package com.tofallis.baking.di;

import android.app.Application;

import com.tofallis.baking.RecipeApplication;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjectionModule;

@Singleton
@Component(modules = {
        AndroidInjectionModule.class,
        RecipeModule.class,
        ActivityModule.class})
public interface RecipeApplicationComponent {
    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder application(Application application);
        RecipeApplicationComponent build();
    }

    void inject(RecipeApplication app);
}

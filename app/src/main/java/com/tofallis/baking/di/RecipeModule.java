package com.tofallis.baking.di;

import android.app.Application;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tofallis.baking.BuildConfig;
import com.tofallis.baking.network.DataManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
class RecipeModule {

    @Provides
    HttpLoggingInterceptor provideHttpLoggingInterceptor() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return loggingInterceptor;
    }

    @Provides
    Interceptor provideBasicInterceptor(Application app) {
        return chain -> {
            HttpUrl url = chain.request().url().newBuilder()
                    .build();

            Request req = chain.request().newBuilder().url(url).build();
            return chain.proceed(req);
        };
    }

    @Provides
    OkHttpClient provideBasicOkHttpClient(Interceptor requestInterceptor,
                                          HttpLoggingInterceptor logging) {
        return new OkHttpClient.Builder()
                .addInterceptor(requestInterceptor)
                .addInterceptor(logging)
                .build();
    }

    @Provides
    Gson provideGson() {
        return new GsonBuilder().create();
    }

    @Provides
    Retrofit provideBasicRetrofit(OkHttpClient client, Gson gson, Application app) {
        return new Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    @Provides
    @Singleton
    DataManager provideDataManager(Retrofit basicRetrofit) {
        return new DataManager(basicRetrofit);
    }
}

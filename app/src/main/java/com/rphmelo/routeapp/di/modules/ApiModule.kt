package com.rphmelo.routeapp.di.modules

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.rphmelo.routeapp.BuildConfig.BASE_URL_DIRECTIONS
import com.rphmelo.routeapp.data.remote.ApiDirectionsService
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
class ApiModule {

    @Provides
    @Singleton
    fun provideGson(): Gson = GsonBuilder().create()

    @Provides
    @Singleton
    fun provideApi(gson: Gson, okHttpClient: OkHttpClient): ApiDirectionsService {
        return Retrofit.Builder()
            .baseUrl(BASE_URL_DIRECTIONS)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build().create(ApiDirectionsService::class.java)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(loggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun providesHttpLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor(
            HttpLoggingInterceptor.Logger {
                    Timber.d(it)
            }
        ).apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }
}
package com.rphmelo.routeapp.di.modules

import android.content.Context
import com.rphmelo.routeapp.RouteApp
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule {
    @Provides
    @Singleton
    fun provideContext(routeApp: RouteApp): Context {
        return routeApp.applicationContext
    }
}
package com.rphmelo.routeapp.di.components

import android.app.Application
import com.rphmelo.routeapp.RouteApp
import com.rphmelo.routeapp.di.modules.ActivityModule
import com.rphmelo.routeapp.di.modules.AppModule
import com.rphmelo.routeapp.di.modules.LocationModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules = [
    AndroidSupportInjectionModule::class,
    ActivityModule::class,
    LocationModule::class,
    AppModule::class
])
interface AppComponent {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: RouteApp): Builder
        fun build(): AppComponent
    }

    fun inject(app: RouteApp)
}
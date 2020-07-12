package com.rphmelo.routeapp.di.modules

import com.rphmelo.routeapp.ui.MapsActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityModule {
    @ContributesAndroidInjector
    internal abstract fun contributeMapsActivity(): MapsActivity
}
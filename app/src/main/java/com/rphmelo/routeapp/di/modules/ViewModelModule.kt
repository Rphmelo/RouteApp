package com.rphmelo.routeapp.di.modules

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rphmelo.routeapp.di.keys.ViewModelKey
import com.rphmelo.routeapp.ui.MapsViewModel
import com.rphmelo.routeapp.util.FactoryViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module(includes = [LocationModule::class])
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(MapsViewModel::class)
    abstract fun bindMapsViewModel(mapsViewModel: MapsViewModel): ViewModel

    @Binds
    abstract fun bindViewModelFactory(factory: FactoryViewModel): ViewModelProvider.Factory
}
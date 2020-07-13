package com.rphmelo.routeapp.di.modules

import android.content.Context
import android.content.Intent
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import dagger.Module
import dagger.Provides

@Module(includes = [AppModule::class])
class PlacesModule {

    @Provides
    fun providePlacesClient(context: Context): PlacesClient {
        return Places.createClient(context)
    }

    @Provides
    fun provideAutoCompleteIntent(context: Context): Intent {
        val fields: List<Place.Field> = listOf(
            Place.Field.ID,
            Place.Field.NAME,
            Place.Field.ADDRESS,
            Place.Field.LAT_LNG
        )

        return Autocomplete.IntentBuilder(
            AutocompleteActivityMode.FULLSCREEN, fields
        ).setCountry("BR").build(context)
    }


}
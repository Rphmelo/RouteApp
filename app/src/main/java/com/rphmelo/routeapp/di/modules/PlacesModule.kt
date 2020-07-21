package com.rphmelo.routeapp.di.modules

import android.content.Context
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.rphmelo.routeapp.common.Constants.PLACES_API_COUNTRY
import dagger.Module
import dagger.Provides

@Module(includes = [AppModule::class])
class PlacesModule {

    @Provides
    fun providePlacesClient(context: Context): PlacesClient {
        return Places.createClient(context)
    }

    @Provides
    fun provideAutoCompleteIntent(): Autocomplete.IntentBuilder {
        val fields: List<Place.Field> = listOf(
            Place.Field.ID,
            Place.Field.NAME,
            Place.Field.ADDRESS,
            Place.Field.LAT_LNG
        )

        return Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
            .setCountry(PLACES_API_COUNTRY)

    }
}
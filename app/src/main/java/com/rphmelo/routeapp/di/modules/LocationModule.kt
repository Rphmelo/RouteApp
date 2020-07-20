package com.rphmelo.routeapp.di.modules

import android.content.Context
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import com.rphmelo.routeapp.Constants.LOCATION_UPDATE_FASTEST_INTERVAL
import com.rphmelo.routeapp.Constants.LOCATION_UPDATE_INTERVAL
import dagger.Module
import dagger.Provides

@Module(includes = [AppModule::class])
class LocationModule {

    @Provides
    fun provideFusedLocationProviderClient(context: Context): FusedLocationProviderClient {
        return LocationServices.getFusedLocationProviderClient(context)
    }

    @Provides
    fun provideLocationSettingsClient(context: Context): SettingsClient {
        return LocationServices.getSettingsClient(context)
    }

    @Provides
    fun provideLocationSettingsRequestBuilder(locationRequest: LocationRequest): LocationSettingsRequest.Builder {
        return LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
    }

    @Provides
    fun provideTaskLocationSettingsResponse(
        client: SettingsClient,
        builder: LocationSettingsRequest.Builder
    ): Task<LocationSettingsResponse> {
        return client.checkLocationSettings(builder.build())
    }

    @Provides
    fun provideLocationRequest(): LocationRequest {
        return LocationRequest().apply {
            interval = LOCATION_UPDATE_INTERVAL
            fastestInterval = LOCATION_UPDATE_FASTEST_INTERVAL
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
    }
}
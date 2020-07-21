# RouteApp
An app that shows a route between a place and the user location.

# Instructions
Before building this app, you need to add an apiKeys.gradle file that constains the keys needed for working with google maps.

This file has to have these values:

```
//PUT YOUR API KEY HERE
ext {
    google_maps_api_key = ""
    places_api_key = ""
    directions_api_key = ""
}
```

You can get these keys at https://console.cloud.google.com/apis

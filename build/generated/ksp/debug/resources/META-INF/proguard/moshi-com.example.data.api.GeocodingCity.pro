-keepnames class com.example.data.api.GeocodingCity
-if class com.example.data.api.GeocodingCity
-keep class com.example.data.api.GeocodingCityJsonAdapter {
    public <init>(com.squareup.moshi.Moshi);
}

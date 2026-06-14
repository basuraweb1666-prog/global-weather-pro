-keepnames class com.example.data.api.GeocodingResponse
-if class com.example.data.api.GeocodingResponse
-keep class com.example.data.api.GeocodingResponseJsonAdapter {
    public <init>(com.squareup.moshi.Moshi);
}

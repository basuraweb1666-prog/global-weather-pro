-keepnames class com.example.data.api.WeatherResponse
-if class com.example.data.api.WeatherResponse
-keep class com.example.data.api.WeatherResponseJsonAdapter {
    public <init>(com.squareup.moshi.Moshi);
}

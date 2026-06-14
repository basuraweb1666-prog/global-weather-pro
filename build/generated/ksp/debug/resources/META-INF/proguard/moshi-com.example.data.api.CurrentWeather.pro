-keepnames class com.example.data.api.CurrentWeather
-if class com.example.data.api.CurrentWeather
-keep class com.example.data.api.CurrentWeatherJsonAdapter {
    public <init>(com.squareup.moshi.Moshi);
}

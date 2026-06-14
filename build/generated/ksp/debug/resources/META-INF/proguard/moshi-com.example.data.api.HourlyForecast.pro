-keepnames class com.example.data.api.HourlyForecast
-if class com.example.data.api.HourlyForecast
-keep class com.example.data.api.HourlyForecastJsonAdapter {
    public <init>(com.squareup.moshi.Moshi);
}

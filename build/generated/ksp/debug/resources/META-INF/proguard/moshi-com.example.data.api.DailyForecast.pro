-keepnames class com.example.data.api.DailyForecast
-if class com.example.data.api.DailyForecast
-keep class com.example.data.api.DailyForecastJsonAdapter {
    public <init>(com.squareup.moshi.Moshi);
}

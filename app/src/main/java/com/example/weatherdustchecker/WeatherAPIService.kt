package com.example.weatherdustchecker

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query


interface WeatherAPIService {
    @GET("/data/2.5/weather")
    fun getWeatherStatusInfo( //URL 정보들, 추상메서드
        @Query("appid") appId: String,
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("units") units: String="metric"
    ) : Call<OpenWeatherAPIJSONResponseFromGSON>

}

data class OpenWeatherAPIJSONResponseFromGSON(val main: Map<String, String>, val weather: List<Map<String, String>>)
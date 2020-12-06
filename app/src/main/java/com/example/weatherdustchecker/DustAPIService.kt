package com.example.weatherdustchecker

import android.util.Log
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.lang.reflect.Type

interface DustAPIService {
    @GET("/feed/geo:{lat};{lon}/")
    fun getDustStatusInfo( //URL 정보들, 추상메서드
        @Path("lat") lat: Double,
        @Path("lon") lon: Double,
        @Query("token") token: String
    ) : Call<DustCheckResponseFromGSON>
}

data class DustCheckResponseFromGSON(val pm10: Int?, val pm25: Int?, val pm10Status : String, val pm25Status: String)

class DustCheckerResponseDeserializerGSON : JsonDeserializer<DustCheckResponseFromGSON>{
    private val checkCategory = {aqi: Int? -> when(aqi){
        null -> "알 수 없음"
        in (0 ..50)->"매우 좋음"
        in (51 ..100)->"좋음"
        in (101 ..150)->"보통"
        in (151 ..200)->"나쁨"
        else -> "매우 나쁨"
    }}

    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): DustCheckResponseFromGSON {
        val root = json?.asJsonObject

        val dataNode = root?.getAsJsonObject("data")
        var iaqiNode = dataNode?.getAsJsonObject("iaqi")
        var pm10Node = iaqiNode?.getAsJsonObject("pm10")
        var pm25Node = iaqiNode?.getAsJsonObject("pm25")
        var pm10 = pm10Node?.get("v")?.asInt
        var pm25 = pm25Node?.get("v")?.asInt

        return DustCheckResponseFromGSON(pm10!!, pm25!!, checkCategory(pm10), checkCategory(pm25))
    }
}
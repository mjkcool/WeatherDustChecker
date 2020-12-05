package com.example.weatherdustchecker

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import java.net.URL

class WeatherPageFragment:Fragment(){

    @JsonIgnoreProperties(ignoreUnknown=true)
    data class OpenWeatherAPIJSONResponse(val main: Map<String, String>, val weather: List<Map<String, String>>)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?)
    : View? {
        var view = inflater.inflate(R.layout.weather_page_fragment, container, false)
        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val weatherImage = view.findViewById<ImageView>(R.id.weather_icon)
        val statusText = view.findViewById<TextView>(R.id.weather_status_title)
        val tempText = view.findViewById<TextView>(R.id.weather_temp_text)

        val lat = arguments!!.getDouble("lat") //위도
        val lon = arguments!!.getDouble("lon") //경도
        val url = "http://api.openweathermap.org/data/2.5/weather?lat=${lat}&lon=${lon}&appid=${APIKey.OpenWeatherAPIKey}&units=metric"

        val apiCallback = object : APICall.APICallback{
            override fun onComplete(result: String) {
                //역직렬화 (JSON)
                val mapper = jacksonObjectMapper()
                val data = mapper?.readValue<OpenWeatherAPIJSONResponse>(result)

                val temp = data.main.get("temp") //온도
                Log.d("mytag", "온도-${temp}")
                tempText.text = temp

                val id = data.weather[0].get("id")
                if(id != null){
                    statusText.text = when{
                        id.startsWith("2") -> {
                            weatherImage.setImageResource(R.drawable.flash)
                            "천둥번개"
                        }
                        id.startsWith("3") -> {
                            weatherImage.setImageResource(R.drawable.rain)
                            "이슬비"
                        }
                        id.startsWith("5") -> {
                            weatherImage.setImageResource(R.drawable.raining)
                            "비"
                        }
                        id.startsWith("6") -> {
                            weatherImage.setImageResource(R.drawable.snow)
                            "눈"
                        }
                        id.startsWith("7") -> {
                            weatherImage.setImageResource(R.drawable.cloudy)
                            "흐림"
                        }
                        id.equals("800") -> {
                            weatherImage.setImageResource(R.drawable.sun)
                            "화창"
                        }
                        id.startsWith("8") -> {
                            weatherImage.setImageResource(R.drawable.cloud)
                            "구름 낌"
                        }
                        else -> "알 수 없음"
                    }
                }
            }
        }
        val call = APICall(apiCallback)
        call.execute(URL(url))
    }

    companion object{
        fun newInstnace(lat:Double, lng:Double):WeatherPageFragment{
            val fragment = WeatherPageFragment()

            val args = Bundle()
            args.putDouble("lat", lat)
            args.putDouble("lng", lng)
            fragment.arguments = args

            return fragment
        }
    }
}
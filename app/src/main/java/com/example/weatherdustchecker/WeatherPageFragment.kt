package com.example.weatherdustchecker

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.URL

class WeatherPageFragment:Fragment(){
    lateinit var weatherImage : ImageView
    lateinit var statusText : TextView
    lateinit var tempText : TextView

    @JsonIgnoreProperties(ignoreUnknown=true)
    data class OpenWeatherAPIJSONResponse(val main: Map<String, String>, val weather: List<Map<String, String>>)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?)
    : View? {
        var view = inflater.inflate(R.layout.weather_page_fragment, container, false)
        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        weatherImage = view.findViewById<ImageView>(R.id.weather_icon)
        statusText = view.findViewById<TextView>(R.id.weather_status_title)
        tempText = view.findViewById<TextView>(R.id.weather_temp_text)

        val lat = arguments!!.getDouble("lat") //위도
        val lon = arguments!!.getDouble("lon") //경도

        //GSON
        val retrofit = Retrofit.Builder() //생성자X 빌더 객체를 이용해 객체 생성
            .baseUrl("http://api.openweathermap.org")
            .addConverterFactory(GsonConverterFactory.create()).build()

        val apiService : WeatherAPIService = retrofit.create(WeatherAPIService::class.java)
        val apiCallForData = apiService.getWeatherStatusInfo(APIKey.OpenWeatherAPIKey, lat, lon)

        apiCallForData.enqueue(object : Callback<OpenWeatherAPIJSONResponseFromGSON>{
            override fun onResponse(
                call: Call<OpenWeatherAPIJSONResponseFromGSON>,
                response: Response<OpenWeatherAPIJSONResponseFromGSON>
            ) {
                TODO("Not yet implemented")
            }

            override fun onFailure(call: Call<OpenWeatherAPIJSONResponseFromGSON>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })


        /*
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
        */
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

    fun startAnimation(){
        val fadeIn : Animation = AnimationUtils.loadAnimation(activity, R.anim.fade_in)
        weatherImage.startAnimation(fadeIn)
    }
}
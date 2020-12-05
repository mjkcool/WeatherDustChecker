package com.example.weatherdustchecker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import java.net.URL


class DustPageFragment : Fragment() {


    //사용전 초기화 필요
    lateinit var statusImage : ImageView
    lateinit var pm25StatusText : TextView
    lateinit var pm25IntensityText : TextView
    lateinit var pm10StatusText : TextView
    lateinit var pm10IntensityText : TextView


    @JsonDeserialize(using=DustCheckerResponseDeserializer::class)
    data class DustCheckResponse(val pm10: Int?, val pm25: Int?, val pm10Status : String, val pm25Status: String)

    class DustCheckerResponseDeserializer : StdDeserializer<DustCheckResponse>(DustCheckResponse::class.java){
        private val checkCategory = {aqi: Int? -> when(aqi){
            null -> "알 수 없음"
            in (0 ..50)->"매우 좋음"
            in (51 ..100)->"좋음"
            in (101 ..150)->"보통"
            in (151 ..200)->"나쁨"
            else -> "매우 나쁨"
        }}

        override fun deserialize(p: JsonParser?, ctxt: DeserializationContext?): DustCheckResponse {
            val node : JsonNode? = p?.codec?.readTree<JsonNode>(p)

            var dataNode : JsonNode? = node?.get("data")
            var iaqiNode = dataNode?.get("iaqi")
            var pm10Node = iaqiNode?.get("pm10")
            var pm25Node = iaqiNode?.get("pm25")
            var pm10 = pm10Node?.get("v")?.asInt()
            var pm25 = pm25Node?.get("v")?.asInt()

            // (2)
            var pm10Status = checkCategory(pm10)
            var pm25Status = checkCategory(pm25)

            return DustCheckResponse(pm10, pm25, pm10Status, pm25Status)
        }

    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = inflater.inflate(R.layout.dust_page_fragment, container, false)

        statusImage = view.findViewById(R.id.dust_status_icon)
        pm25StatusText = view.findViewById(R.id.dust_pm25_status_text)
        pm25IntensityText = view.findViewById(R.id.dust_pm25_intensity_text)
        pm10StatusText = view.findViewById(R.id.dust_pm10_status_text)
        pm10IntensityText = view.findViewById(R.id.dust_pm10_intensity_text)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState)

        val lat = arguments!!.getDouble("lat") //위도
        val lon = arguments!!.getDouble("lon") //경도
        val url = "https://api.waqi.info/feed/geo:${lat};${lon}/?token=${APIKey.WaqiAPIKey}"

        val apiCallback = object : APICall.APICallback{
            override fun onComplete(result: String) {
                val mapper = jacksonObjectMapper()
                val data = mapper?.readValue<DustCheckResponse>(result)

                statusImage.setImageResource(when(data.pm25Status){
                    "매우 좋음" -> R.drawable.very_good
                    "좋음" -> R.drawable.good
                    "보통" -> R.drawable.normal
                    "나쁨" -> R.drawable.bad
                    else -> R.drawable.very_bad //매우 나쁨
                })
                pm25IntensityText.text = data.pm25?.toString() ?: "알 수 없음"
                pm10IntensityText.text = data.pm10?.toString() ?: "알 수 없음"
                pm25StatusText.text = "초미세먼지 ${data.pm25Status}"
                pm10StatusText.text = "미세먼지 ${data.pm10Status}"
            }
        }
        val call = APICall(apiCallback)
        call.execute(URL(url))
    }

    companion object{
        fun newInstnace(lat:Double, lng:Double):DustPageFragment{
            val fragment = DustPageFragment()

            val args = Bundle()
            args.putDouble("lat", lat)
            args.putDouble("lng", lng)
            fragment.arguments = args

            return fragment
        }
    }
}
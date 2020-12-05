package com.example.weatherdustchecker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import java.lang.Exception

class WeatherDustMainActivity : AppCompatActivity() {
    private lateinit var mPager: ViewPager
    private var lat :Double = 37.58
    private var lon :Double = 126.98


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather_dust_main)

        supportActionBar?.hide()

        mPager = findViewById(R.id.pager)


    }

    inner class MyPagerAdapter(fm:FragmentManager):FragmentStatePagerAdapter(fm){
        override fun getCount(): Int = 2
        override fun getItem(position: Int): Fragment {
            return when(position){
                0 -> WeatherPageFragment.newInstnace(lat, lon)
                1 -> DustPageFragment.newInstnace(lat, lon)
                else -> {
                    throw Exception("페이지가 존재하지 않음.")
                }
            }
        }
    }
}
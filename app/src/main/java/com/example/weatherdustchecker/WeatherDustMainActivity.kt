package com.example.weatherdustchecker

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.PixelCopy
import android.view.View
import android.widget.Toast
import androidx.annotation.Px
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import java.lang.Exception
import java.util.jar.Manifest

class WeatherDustMainActivity : AppCompatActivity() {
    private lateinit var mPager: ViewPager
    private var lat :Double = 0.0
    private var lon :Double = 0.0

    private var PERMISSION_REQUEST_CODE : Int = 1
    private lateinit var locationListener: LocationListener
    private lateinit var locationManager: LocationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather_dust_main)

        supportActionBar?.hide()

        mPager = findViewById<ViewPager>(R.id.pager)

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        locationListener = object : LocationListener{
            override fun onLocationChanged(location: Location) { //새로운 위치 반환
                lat = location.latitude
                lon = location.longitude
                Log.d("mytag", "위-${lat}, 경-${lon}")

                locationManager.removeUpdates(this) //더이상 업데이트 하지 않는다.

                val pagerAdapter = MyPagerAdapter(supportFragmentManager)
                mPager.adapter = pagerAdapter

                mPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                    override fun onPageScrollStateChanged(state : Int){}
                    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
                    override fun onPageSelected(position: Int) { //페이지 전환 완료 시 콜백
                        if(position == 0){
                            val fragment = mPager.adapter?.instantiateItem(mPager, position) as WeatherPageFragment
                            fragment.startAnimation()
                        }else if(position ==1){
                            val fragment = mPager.adapter?.instantiateItem(mPager, position) as DustPageFragment
                            fragment.startAnimation()
                        }
                    }
                })

            }
        }


        if(ContextCompat.checkSelfPermission(
                this, android.Manifest.permission.ACCESS_FINE_LOCATION)
            ==PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
            ==PackageManager.PERMISSION_GRANTED){

                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0,0f, locationListener)
        }else{
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    android.Manifest.permission.ACCESS_COARSE_LOCATION,
                    android.Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSION_REQUEST_CODE)
        }
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int, //PERMISSION_REQUEST_CODE
        permissions: Array<String>, //ACCESS permissions array
        grantResults: IntArray //권한 요청 결과 배열
    ) {
        //권한 요청 결과에 대한 대응
        var allPermissionsGranted = true
        for(result in grantResults){ //전부 허용되었는지 비교
            allPermissionsGranted = (result == PackageManager.PERMISSION_GRANTED)
            if(!allPermissionsGranted) break //미허용시 반복 탈출
        }
        if(allPermissionsGranted){
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                0, //위치 업데이트 최소 텀
                0f, //이동거리가 해당 m수 내면 위치 정보 업데이트 X
                locationListener)
        }else{
            Toast.makeText(applicationContext, "위치 정보 제공 동의가 필요해요", Toast.LENGTH_LONG).show()
            this.finish()
        }
    }

    //프래그먼트 생성
    inner class MyPagerAdapter(fm:FragmentManager):FragmentStatePagerAdapter(fm){
        override fun getCount(): Int = 2 //프래그먼트 뷰 개수
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


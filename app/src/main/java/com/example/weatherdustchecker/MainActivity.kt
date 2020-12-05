package com.example.weatherdustchecker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

data class MyJSONDataClass(val data1: Int, val data2: String, val list: List<Int>)
data class MyJSONNestedDataClass(val nested: Map<String, Any>)

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.hide()

        val manager = supportFragmentManager
        val transaction = manager.beginTransaction()

        transaction.add(R.id.fragment_container,
                DustPageFragment.newInstnace(37.58, 127.0))
        transaction.commit()


    }

}
package com.example.test2.driver

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.test2.R
import com.example.test2.databinding.ActivityDriver1Binding
class DriverActivity1 : AppCompatActivity() {
    private lateinit var binding: ActivityDriver1Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDriver1Binding.inflate(layoutInflater)
        setContentView(binding.root)
        replaceFragment(DriverHome())

        binding.bottomNavigationView.setOnItemSelectedListener{
            when(it.itemId){
                R.id.DriverHome -> replaceFragment(DriverHome())
                R.id.DriverEmergency -> replaceFragment(DriverEmergency())
                R.id.DriverProfile -> replaceFragment(DriverProfile())
                else  -> {

                }
            }
            true
        }



    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.driver_frame_layout, fragment)
        fragmentTransaction.commit()
    }
}
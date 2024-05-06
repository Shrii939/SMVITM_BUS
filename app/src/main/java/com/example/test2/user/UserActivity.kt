package com.example.test2.user

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.test2.R
import com.example.test2.databinding.ActivityUserBinding


class UserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityUserBinding.inflate(layoutInflater)
        setContentView(binding.root)
        replaceFragment(MyBusFragment())

        binding.bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.UserHome -> replaceFragment(UserHome())
                R.id.UserProfile -> replaceFragment(UserProfile())
//                R.id.QRScanner -> replaceFragment((QRScanner()))
                R.id.MapsFragment -> replaceFragment(MapsFragmentUser())
                R.id.Bus -> replaceFragment(MyBusFragment())

                else -> {

                }
            }

            true

        }

    }

    private fun replaceFragment(fragment: Fragment){
        val fragmentManager = supportFragmentManager
        val fragmentTransition = fragmentManager.beginTransaction()
        fragmentTransition.replace(R.id.user_frame_layout, fragment)
        fragmentTransition.commit()
    }

}
package com.example.test2.admin

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.test2.R
import com.example.test2.databinding.ActivityAdminBinding

class AdminActivity : AppCompatActivity() {

    private lateinit var a_binding: ActivityAdminBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        a_binding = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(a_binding.root)
        replaceFragment(AdminHome())

        a_binding.bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.AdminHome -> replaceFragment(AdminHome())
                R.id.AdminProfile -> replaceFragment(AdminProfile())
                R.id.AdminAdd -> replaceFragment(AdminRegisterUser())

                else -> {

                }
            }

            true

        }
    }

    private fun replaceFragment(fragment: Fragment){
        val fragmentManager = supportFragmentManager
        val fragmentTransition = fragmentManager.beginTransaction()
        fragmentTransition.replace(R.id.admin_frame_layout, fragment)
        fragmentTransition.commit()
    }



}
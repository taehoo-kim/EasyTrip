package com.example.capstonedesign

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import com.example.capstonedesign.databinding.ActivityMainBinding
import com.example.capstonedesign.fragment.HomeFragment
import com.example.capstonedesign.fragment.ProfileFragment
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.Calendar
import java.util.StringTokenizer

class MainActivity : AppCompatActivity() {

    lateinit var binding : ActivityMainBinding

    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        // 첫 화면은 HomeFragment
        if(savedInstanceState == null){
            supportFragmentManager.beginTransaction().replace(R.id.main_content, HomeFragment()).commit()
        }

        binding.bottomNavigation.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.action_home -> {
                    var f = HomeFragment()
                    supportFragmentManager.beginTransaction().replace(R.id.main_content, f).commit()

                    return@setOnNavigationItemSelectedListener true

                }
                R.id.action_account -> {

                    var f = ProfileFragment()
                    supportFragmentManager.beginTransaction().replace(R.id.main_content, f).commit()

                    return@setOnNavigationItemSelectedListener true

                }

                R.id.action_search -> {
                    return@setOnNavigationItemSelectedListener true
                }
            }
            return@setOnNavigationItemSelectedListener false
        }

    }




}
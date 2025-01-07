package com.example.siagaarus

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Tentang_Aplikasi : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tentang_aplikasi)

        // Set toolbar as action bar (check if inflated first)
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar) // Replace with your toolbar id
        if (toolbar !=  null) {
            setSupportActionBar(toolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)

            // Handle back button click on toolbar
            toolbar.setNavigationOnClickListener {
                val intent = Intent(this, DashboardActivity::class.java)
                startActivity(intent)
            }
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.SAAbout)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}

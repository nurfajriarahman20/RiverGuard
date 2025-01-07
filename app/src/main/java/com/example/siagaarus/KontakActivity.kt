package com.example.siagaarus

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView  // Assuming your contact views are ImageViews
import android.widget.LinearLayout

class KontakActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kontak)

        // Set toolbar as action bar (check if inflated first)
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar) // Replace with your toolbar id
        if (toolbar != null) {
            setSupportActionBar(toolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }

        // Set click listeners for contact views
        val kepalaDesa = findViewById<LinearLayout>(R.id.KDesa) // Replace with your view id
        kepalaDesa.setOnClickListener {
            dialNumber("0882021026389") // Replace with real number
        }

        val timDarurat = findViewById<LinearLayout>(R.id.KTimDarurat) // Replace with your view id
        timDarurat.setOnClickListener {
            dialNumber("112")
        }

        val basarnas = findViewById<LinearLayout>(R.id.KBasarnas) // Replace with your view id
        basarnas.setOnClickListener {
            dialNumber("115")
        }

        val ambulance = findViewById<LinearLayout>(R.id.KAmbulance) // Replace with your view id
        ambulance.setOnClickListener {
            dialNumber("119") // Ambulance number may vary
        }
    }

    private fun dialNumber(phoneNumber: String) {
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:$phoneNumber")
        }
        startActivity(intent)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            // Add cases for other menu items (if any)
            else -> super.onOptionsItemSelected(item)
        }
    }
}
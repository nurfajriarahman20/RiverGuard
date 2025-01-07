package com.example.siagaarus

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class OnboardingActivity1 : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding1)

        // Inisialisasi SharedPreferences
        sharedPreferences = getSharedPreferences("app_preferences", MODE_PRIVATE)

        // Periksa apakah onboarding sudah selesai
        val onboardingCompleted = sharedPreferences.getBoolean("onboarding_completed", false)

        // Jika sudah selesai, langsung ke Dashboard Activity
        if (onboardingCompleted) {
            startActivity(Intent(this, DashboardActivity::class.java))
            finish()
            return
        }

        // Tombol untuk melanjutkan ke OnboardingActivity2
        val IVNext = findViewById<ImageView>(R.id.IVNext)
        IVNext.setOnClickListener {
            startActivity(Intent(this, OnboardingActivity2::class.java))
            finish()
        }
    }
}

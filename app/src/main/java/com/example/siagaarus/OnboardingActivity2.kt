package com.example.siagaarus


import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class OnboardingActivity2 : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding2)

        // Inisialisasi SharedPreferences
        sharedPreferences = getSharedPreferences("app_preferences", MODE_PRIVATE)

        // Periksa apakah onboarding sudah selesai
        val onboardingCompleted = sharedPreferences.getBoolean("onboarding_completed", false)

        // Tombol Mulai untuk menyelesaikan onboarding dan menuju halaman login
        val IVMulai = findViewById<ImageView>(R.id.IVMulai)
        IVMulai.setOnClickListener {

            // Tandai onboarding sebagai selesai
            sharedPreferences.edit().putBoolean("onboarding_completed", true).apply()
            startActivity(Intent(this, DashboardActivity::class.java))
            finish()
        }

        // Tombol Back untuk kembali ke OnboardingActivity1
        val IVBack = findViewById<ImageView>(R.id.IVBack)
        IVBack.setOnClickListener {
            startActivity(Intent(this, OnboardingActivity1::class.java))
            finish()
        }
    }
}

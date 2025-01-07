package com.example.siagaarus

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val IVLogo: ImageView = findViewById(R.id.IVLogo)
        val progressBar: ProgressBar = findViewById(R.id.progressBar)

        // Animasi zoom-in dengan durasi lebih pendek
        val zoomInAnimation = AnimationUtils.loadAnimation(this, R.anim.zoom_in)
        IVLogo.startAnimation(zoomInAnimation)

        // Menampilkan ProgressBar
        progressBar.visibility = android.view.View.VISIBLE

        // Delay 2 detik
        Handler(Looper.getMainLooper()).postDelayed({
            // Sembunyikan ProgressBar setelah 2 detik
            progressBar.visibility = android.view.View.GONE

            // Cek status Onboarding dan arahkan ke Activity yang sesuai
            val sharedPreferences: SharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE)
            val onboardingCompleted = sharedPreferences.getBoolean("OnboardingCompleted", false)

            if (onboardingCompleted) {
                startActivity(Intent(this, DashboardActivity::class.java))
            } else {
                startActivity(Intent(this, OnboardingActivity1::class.java))
            }
            finish()
        }, 2000) // Delay tetap 2 detik
    }
}

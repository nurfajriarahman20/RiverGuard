package com.example.siagaarus

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.widget.ImageView

class PanduanActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_panduan)

        // Menyambungkan toolbar dari layout
        val toolbar: Toolbar = findViewById(R.id.toolbar)

        // Menyeting toolbar sebagai ActionBar
        setSupportActionBar(toolbar)

        // Mengaktifkan tombol home di toolbar untuk kembali ke halaman sebelumnya
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        // Menambahkan animasi zoom-in pada setiap ImageView
        val imageViews = listOf(
            findViewById<ImageView>(R.id.IVSungai),
            findViewById<ImageView>(R.id.IVTTinggi),
            findViewById<ImageView>(R.id.IVKeluarga),
            findViewById<ImageView>(R.id.IVTas),
            findViewById<ImageView>(R.id.IVListrik),
            findViewById<ImageView>(R.id.IVJalur)
        )

        // Menambahkan animasi zoom-in selama 5 detik
        for (imageView in imageViews) {
            val animator = ObjectAnimator.ofFloat(imageView, "scaleX", 0f, 1f)
            animator.duration = 5000 // Durasi 5 detik
            animator.interpolator = DecelerateInterpolator() // Efek pelambatan saat animasi
            animator.start()

            val animatorY = ObjectAnimator.ofFloat(imageView, "scaleY", 0f, 1f)
            animatorY.duration = 5000 // Durasi 5 detik
            animatorY.interpolator = DecelerateInterpolator() // Efek pelambatan saat animasi
            animatorY.start()
        }
    }

    // Fungsi untuk menangani item yang dipilih di menu
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()  // Kembali ke activity sebelumnya
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}

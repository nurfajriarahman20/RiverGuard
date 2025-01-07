package com.example.siagaarus

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*

class CuacaActivity : AppCompatActivity() {

    // Declare views
    private lateinit var TVNilaiCuaca: TextView
    private lateinit var TVUpdateCuaca: TextView
    private lateinit var toolbar: Toolbar
    private lateinit var IVCuaca: ImageView

    // API Service
    private lateinit var apiService: ApiService
    private val handler = Handler(Looper.getMainLooper())
    private val refreshInterval = 1000L // Refresh every 1 second

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cuaca)

        // Initialize Toolbar
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Initialize Views
        TVNilaiCuaca = findViewById(R.id.TVNilaiCuaca)
        TVUpdateCuaca = findViewById(R.id.TVUpdateCuaca)
        IVCuaca = findViewById(R.id.IVCuaca)

        // Initialize API Service
        val retrofit = Retrofit.Builder()
            .baseUrl("http://22tkja.com/") // Replace with your API base URL
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        apiService = retrofit.create(ApiService::class.java)

        // Start periodic data refresh
        startDataRefresh()
    }

    private fun startDataRefresh() {
        handler.post(object : Runnable {
            override fun run() {
                fetchCuacaDataFromApi()
                handler.postDelayed(this, refreshInterval) // Refresh every 1 second
            }
        })
    }

    private fun fetchCuacaDataFromApi() {
        apiService.getMonitoringData().enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful && response.body()?.status == "success") {
                    val rainStatus = response.body()?.data?.get(0)?.rain_status?.trim()

                    if (rainStatus != null) {
                        // Update the rain status text
                        TVNilaiCuaca.text = rainStatus

                        // Cek apakah status cuaca adalah hujan atau tidak
                        if (rainStatus.equals("Hujan Terdeteksi", ignoreCase = true)) {
                            Log.d("CuacaActivity", "Set cuaca to hujan")
                            IVCuaca.setImageResource(R.drawable.hujan) // Ganti dengan ikon hujan
                        } else {
                            Log.d("CuacaActivity", "Set cuaca to cerah atau tidak ada hujan")
                            IVCuaca.setImageResource(R.drawable.cerah) // Ganti dengan ikon cerah
                        }

                        // Update the last update time
                        TVUpdateCuaca.text = "Diperbarui: ${getCurrentTime()}"
                    }

                } else {
                    TVNilaiCuaca.text = "Data tidak tersedia"
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                TVNilaiCuaca.text = "Gagal memuat data"
            }
        })
    }

    private fun getCurrentTime(): String {
        val dateFormat = SimpleDateFormat("d MMMM yyyy, HH:mm", Locale("id", "ID"))
        return dateFormat.format(Date())
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null) // Stop handler when activity is destroyed
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}

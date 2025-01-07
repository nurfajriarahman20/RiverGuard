package com.example.siagaarus

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.telecom.Call
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.github.anastr.speedviewlib.SpeedView
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*

class TinggiActivity : AppCompatActivity() {

    // Declare TextView to display data
    lateinit var TVNilaiTinggi: TextView
    lateinit var TVUpdateTinggi: TextView
    lateinit var toolbar: Toolbar
    lateinit var speedMeter: SpeedView

    // API Service (Jika Anda menggunakan API untuk mendapatkan data, jika tidak, bisa menggunakan data mock atau sumber lainnya)
    private lateinit var apiService: ApiService
    private val handler = Handler(Looper.getMainLooper())
    private val refreshInterval = 1000L // Refresh every 1 second

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tinggi)

        // Initialize Toolbar
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)  // Set the toolbar as the ActionBar

        // Enable back button on toolbar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Initialize TextViews
        TVNilaiTinggi = findViewById(R.id.TVNilaiTinggi)
        TVUpdateTinggi = findViewById(R.id.TVUpdateTinggi)

        // Initialize Speedometer
        speedMeter = findViewById(R.id.SpeedMeterTinggi)

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
                fetchTinggiDataFromApi() // Call your data fetch function here
                handler.postDelayed(this, refreshInterval) // Refresh every 1 second
            }
        })
    }

    private fun fetchTinggiDataFromApi() {
        apiService.getMonitoringData().enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: retrofit2.Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful && response.body()?.status == "success") {
                    val distance = response.body()?.data?.get(0)?.distance

                    if (distance != null) {
                        // Update the distance (Tinggi) and speedometer
                        TVNilaiTinggi.text = "$distance cm"
                        speedMeter.speedTo(distance.toFloat())

                        // Update the last update time
                        TVUpdateTinggi.text = "Diperbarui: ${getCurrentTime()}"
                    }
                } else {
                    TVNilaiTinggi.text = "Data tidak tersedia"
                }
            }

            override fun onFailure(call: retrofit2.Call<ApiResponse>, t: Throwable) {
                TVNilaiTinggi.text = "Gagal memuat data"
            }
        })
    }

    // Function to get the current time in a readable format
    private fun getCurrentTime(): String {
        val dateFormat = SimpleDateFormat("d MMMM yyyy, HH:mm", Locale("id", "ID"))
        return dateFormat.format(Date())
    }

    // Handle back button in toolbar
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()  // Go back to previous activity when back button on toolbar is clicked
        return true
    }

    // Stop handler when activity is destroyed
    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null) // Stop handler when activity is destroyed
    }
}

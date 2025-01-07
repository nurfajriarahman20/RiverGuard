package com.example.siagaarus

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.github.anastr.speedviewlib.SpeedView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*

class ArusActivity : AppCompatActivity() {

    // Declare UI elements
    lateinit var TVNilaiArus: TextView
    lateinit var TVUpdateArus: TextView
    lateinit var toolbar: Toolbar
    lateinit var speedMeter: SpeedView

    // Declare handler for periodic updates
    private val handler = Handler(Looper.getMainLooper())

    // Declare flow speed variable
    private var flowSpeed: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_arus)

        // Initialize Toolbar
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)  // Set the toolbar as the ActionBar

        // Enable back button on toolbar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Initialize UI elements
        TVNilaiArus = findViewById(R.id.TVNilaiArus)
        TVUpdateArus = findViewById(R.id.TVUpdateArus)
        speedMeter = findViewById(R.id.SpeedMeterArus)

        // Get flow speed (Arus) from Intent
        val initialFlowSpeed = intent.getDoubleExtra("FLOW_SPEED", -1.0)
        Log.d("ArusActivity", "Diterima FLOW_SPEED: $initialFlowSpeed")

        // Periksa apakah data awal diterima
        if (initialFlowSpeed != -1.0) {
            flowSpeed = initialFlowSpeed
            updateUI(flowSpeed)
        } else {
            TVNilaiArus.text = "Data tidak tersedia"
            Toast.makeText(this, "Gagal menerima data awal arus", Toast.LENGTH_SHORT).show()
        }

        // Start periodic data refresh
        startDataRefresh()
    }

    // Function to start periodic API refresh
    private fun startDataRefresh() {
        handler.post(object : Runnable {
            override fun run() {
                fetchData()
                handler.postDelayed(this, 1000)  // Refresh every 1 second
            }
        })
    }

    // Function to fetch data from API
    private fun fetchData() {
        // Retrofit initialization
        val retrofit = Retrofit.Builder()
            .baseUrl("http://22tkja.com/")  // Replace with your API base URL
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiService::class.java)

        apiService.getMonitoringData().enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful && response.body()?.status == "success") {
                    val data = response.body()?.data
                    if (data != null && data.isNotEmpty()) {
                        flowSpeed = data[0].flow_speed
                        updateUI(flowSpeed)  // Update UI with new data
                    } else {
                        Toast.makeText(this@ArusActivity, "Data kosong", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@ArusActivity, "Gagal mengambil data", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                Toast.makeText(this@ArusActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Function to update UI
    private fun updateUI(flowSpeed: Double) {
        TVNilaiArus.text = "$flowSpeed m/s"
        speedMeter.speedTo(flowSpeed.toFloat())
        TVUpdateArus.text = "Diperbarui: ${getCurrentTime()}"
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

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)  // Stop handler when activity is destroyed
    }
}

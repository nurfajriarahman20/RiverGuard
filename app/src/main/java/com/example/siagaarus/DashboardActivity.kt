package com.example.siagaarus

import android.animation.ObjectAnimator
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.TaskStackBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

class DashboardActivity : AppCompatActivity() {

    // Declare TextView to display data
    lateinit var TVArus1: TextView
    lateinit var TVCuaca1: TextView
    lateinit var TVTinggi1: TextView
    lateinit var TVKondisi1: TextView
    lateinit var TVKontak: TextView
    lateinit var TVPanduan: TextView

    private lateinit var apiService: ApiService  // Declare ApiService
    private val handler = Handler(Looper.getMainLooper())

    // Tambahkan variabel untuk menyimpan data
    private var flowSpeed: Double = 0.0
    private var distance: Double = 0.0
    private var rainStatus: String = ""
    private var notificationShown = false  // Flag to track if notification was already shown

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        createNotificationChannel()

        // Initialize TextViews
        TVArus1 = findViewById(R.id.TVArus1)
        TVCuaca1 = findViewById(R.id.TVCuaca1)
        TVTinggi1 = findViewById(R.id.TVTinggi1)
        TVKondisi1 = findViewById(R.id.TVKondisi1)
        TVKontak = findViewById(R.id.TVKontak)
        TVPanduan = findViewById(R.id.TVPanduan)

        // Initialize Retrofit and ApiService
        val retrofit = Retrofit.Builder()
            .baseUrl("http://22tkja.com/")  // Replace with correct API URL
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(ApiService::class.java)

        // Set greeting message
        val tvUcapan: TextView = findViewById(R.id.TVUcapan)
        tvUcapan.text = getGreetingMessage()

        // Set up listeners for ImageView and TextView buttons
        findViewById<ImageView>(R.id.IVAbout).setOnClickListener {
            startActivity(Intent(this, Tentang_Aplikasi::class.java))
        }

        findViewById<ImageView>(R.id.IVProfile).setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        findViewById<TextView>(R.id.TVKontak).setOnClickListener {
            startActivity(Intent(this, KontakActivity::class.java))
        }

        findViewById<TextView>(R.id.TVPanduan).setOnClickListener {
            startActivity(Intent(this, PanduanActivity::class.java))
        }

        // Set up listeners for LinearLayout buttons
        setupLinearLayoutListeners()

        // Start periodic data fetch
        startDataRefresh()

        // Tambahkan animasi pada gambar
        setupImageAnimations()
    }

    private fun setupImageAnimations() {
        val ivArus = findViewById<ImageView>(R.id.IVGArus)
        val ivTinggi = findViewById<ImageView>(R.id.IVGTinggi)
        val ivCuaca = findViewById<ImageView>(R.id.IVGCuaca)
        val ivKondisi = findViewById<ImageView>(R.id.IVGKondisi)

        val imageViews = listOf(ivArus, ivTinggi, ivCuaca, ivKondisi)

        imageViews.forEach { imageView ->
            // Animasi scaleX
            val animatorX = ObjectAnimator.ofFloat(imageView, "scaleX", 0f, 1f).apply {
                duration = 2000 // Durasi 5 detik
                interpolator = DecelerateInterpolator() // Efek pelambatan saat animasi
            }
            animatorX.start()

            // Animasi scaleY
            val animatorY = ObjectAnimator.ofFloat(imageView, "scaleY", 0f, 1f).apply {
                duration = 2000 // Durasi 5 detik
                interpolator = DecelerateInterpolator() // Efek pelambatan saat animasi
            }
            animatorY.start()
        }
    }

    val CHANNEL_ID = "bahaya_channel"
    val CHANNEL_NAME = "Siaga Arus Notifikasi"
    val CHANNEL_DESC = "Notifikasi terkait kondisi arus dan bahaya"

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESC
            }
            val notificationManager: NotificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun showNotification(title: String, message: String) {
        if (!notificationShown) {  // Only show notification if not already shown
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val notification = Notification.Builder(this, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(android.R.drawable.ic_dialog_alert)  // Ganti dengan ikon yang sesuai
                .setPriority(Notification.PRIORITY_HIGH)
                .setAutoCancel(true)  // Dismiss notification after click
                .setContentIntent(
                    TaskStackBuilder.create(this).apply {
                        addNextIntent(Intent(this@DashboardActivity, DashboardActivity::class.java))
                    }.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
                )
                .build()

            notificationManager.notify(1, notification)  // ID 1 untuk notifikasi
            notificationShown = true  // Set flag to true so it won't show again
        }
    }

    private fun setupLinearLayoutListeners() {
        findViewById<LinearLayout>(R.id.LLArus).setOnClickListener {
            val intent = Intent(this, ArusActivity::class.java)
            intent.putExtra("FLOW_SPEED", flowSpeed)
            startActivity(intent)
        }

        findViewById<LinearLayout>(R.id.LLTinggi).setOnClickListener {
            val intent = Intent(this, TinggiActivity::class.java)
            intent.putExtra("DISTANCE", distance)
            startActivity(intent)
        }

        findViewById<LinearLayout>(R.id.LLCuaca).setOnClickListener {
            val intent = Intent(this@DashboardActivity, CuacaActivity::class.java)
            intent.putExtra("RAIN_STATUS", rainStatus)  // Pastikan nilai rainStatus sesuai
            startActivity(intent)
        }

    }

    // Function to start periodic data refresh
    private fun startDataRefresh() {
        handler.post(object : Runnable {
            override fun run() {
                fetchDashboardData()
                handler.postDelayed(this, 1000)  // Refresh every 1 seconds
            }
        })
    }

    // Function to fetch dashboard data from the server using Retrofit
    private fun fetchDashboardData() {
        apiService.getMonitoringData().enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful && response.body()?.status == "success") {
                    val data = response.body()?.data
                    if (data != null && data.isNotEmpty()) {
                        // Get first data item
                        val firstData = data[0]
                        flowSpeed = firstData.flow_speed
                        distance = firstData.distance
                        rainStatus = firstData.rain_status

                        // Display data in TextViews
                        TVArus1.text = "$flowSpeed m/s"
                        TVCuaca1.text = rainStatus
                        TVTinggi1.text = "$distance cm"

                        // Check the condition
                        checkKondisi(rainStatus, flowSpeed, distance)
                    } else {
                        Toast.makeText(this@DashboardActivity, "Data kosong", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@DashboardActivity, "Gagal mengambil data", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                Toast.makeText(this@DashboardActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun checkKondisi(rainStatus: String, flowSpeed: Double, waterHeight: Double) {
        val isRainy = rainStatus.equals("Hujan", ignoreCase = true)

        when {
            // Kondisi Bahaya
            isRainy && flowSpeed > 4.0 && waterHeight > 3 -> {
                TVKondisi1.text = "Bahaya! Segera evakuasi."
                showNotification("Bahaya", "Segera evakuasi! Arus sangat kuat dan tinggi air sangat tinggi.")
            }

            // Kondisi Waspada
            isRainy && flowSpeed > 3.0 || (flowSpeed > 3.5 && waterHeight > 2) -> {
                TVKondisi1.text = "Waspada! Pantau situasi."
                showNotification("Waspada", "Pantau situasi! Arus cukup kuat dan tinggi air mengkhawatirkan.")
            }

            // Kondisi Aman
            else -> {
                TVKondisi1.text = "Aman"
                notificationShown = false  // Reset flag if condition is safe
            }
        }


    }

    private fun getGreetingMessage(): String {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return when (hour) {
            in 5..11 -> "Selamat Pagi"
            in 12..14 -> "Selamat Siang"
            in 15..17 -> "Selamat Sore"
            else -> "Selamat Malam"
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)  // Stop refreshing when activity is destroyed
    }
}

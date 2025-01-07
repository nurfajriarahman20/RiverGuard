package com.example.siagaarus

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {
    private val CHANNEL_ID = "bahaya_channel"
    private val CHANNEL_NAME = "Siaga Arus Notifikasi"
    private val CHANNEL_DESC = "Notifikasi terkait kondisi arus dan bahaya"

    // Membuat Notification Channel
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESC
            }
            val notificationManager: NotificationManager =
                getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    // Menampilkan notifikasi dengan PendingIntent untuk Activity yang bisa diklik
    private fun showNotification(title: String, message: String) {
        createNotificationChannel()  // Memastikan channel dibuat

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Intent untuk membuka Activity saat notifikasi diklik
        val intent = Intent(this, DashboardActivity::class.java) // Ganti dengan Activity tujuan
        val pendingIntent = TaskStackBuilder.create(this)
            .addNextIntentWithParentStack(intent)
            .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)

        // Membuat notifikasi
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)  // Ganti dengan ikon yang sesuai
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_SOUND or NotificationCompat.DEFAULT_VIBRATE)  // Suara dan getar
            .setAutoCancel(true)  // Menghapus notifikasi setelah di-click
            .setContentIntent(pendingIntent)  // Menambahkan PendingIntent
            .build()

        // Menampilkan notifikasi dengan ID 1
        notificationManager.notify(1, notification)
    }

    // Menangani pesan yang diterima
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        if (remoteMessage.data.isNotEmpty()) {
            // Menangani data yang dikirimkan
            val title = remoteMessage.data["title"] ?: "Peringatan"
            val message = remoteMessage.data["message"] ?: "Kondisi Arus Berbahaya"

            // Menampilkan notifikasi
            showNotification(title, message)
        }
    }
}

package com.example.siagaarus

import retrofit2.Call
import retrofit2.http.GET


interface ApiService {
    @GET("iot_data/monitoring_mobile.php") // Sesuaikan URL sesuai file PHP Anda
    fun getMonitoringData(): Call<ApiResponse>
}


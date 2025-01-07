package com.example.siagaarus

data class Monitoring(
    val rain_status: String,
    val flow_speed: Double,
    val distance: Double,
    val condition: String
)

data class ApiResponse(
    val status: String,
    val data: List<Monitoring>,
    val message: String? = null
)

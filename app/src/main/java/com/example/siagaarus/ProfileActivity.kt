package com.example.siagaarus

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

class ProfileActivity : AppCompatActivity() {

    private lateinit var etNama: EditText
    private lateinit var etEmail: EditText
    private lateinit var etKota: EditText
    private lateinit var etNohp: EditText
    private lateinit var tvSimpan: TextView
    private lateinit var toolbar: Toolbar

    private val sharedPref by lazy {
        getSharedPreferences("ProfileData", MODE_PRIVATE)
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // Inisialisasi komponen
        etNama = findViewById(R.id.ETNama)
        etEmail = findViewById(R.id.ETEmail)
        etKota = findViewById(R.id.ETKota)
        etNohp = findViewById(R.id.ETNohp)
        tvSimpan = findViewById(R.id.TVSimpan)

        // Inisialisasi Toolbar
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Menambahkan tombol kembali di toolbar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Load data dari SharedPreferences
        loadProfileData()

        // Set klik listener pada tombol simpan
        tvSimpan.setOnClickListener {
            // Ambil data dari EditText
            val nama = etNama.text.toString()
            val email = etEmail.text.toString()
            val kota = etKota.text.toString()
            val nohp = etNohp.text.toString()

            // Periksa apakah semua kolom sudah diisi
            if (nama.isNotEmpty() && email.isNotEmpty() && kota.isNotEmpty() && nohp.isNotEmpty()) {
                // Simpan data ke SharedPreferences
                saveProfileData(nama, email, kota, nohp)

                // Tampilkan Toast sebagai konfirmasi
                Toast.makeText(this, "Data Disimpan", Toast.LENGTH_SHORT).show()
            } else {
                // Jika ada kolom yang kosong
                Toast.makeText(this, "Harap isi semua kolom", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Fungsi untuk menyimpan data ke SharedPreferences
    private fun saveProfileData(nama: String, email: String, kota: String, nohp: String) {
        val editor = sharedPref.edit()
        editor.putString("NAMA", nama)
        editor.putString("EMAIL", email)
        editor.putString("KOTA", kota)
        editor.putString("NOHP", nohp)
        editor.apply()
    }

    // Fungsi untuk memuat data dari SharedPreferences
    private fun loadProfileData() {
        etNama.setText(sharedPref.getString("NAMA", ""))
        etEmail.setText(sharedPref.getString("EMAIL", ""))
        etKota.setText(sharedPref.getString("KOTA", ""))
        etNohp.setText(sharedPref.getString("NOHP", ""))
    }

    // Logika untuk tombol kembali di toolbar
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()  // Menangani aksi kembali
        return true
    }
}

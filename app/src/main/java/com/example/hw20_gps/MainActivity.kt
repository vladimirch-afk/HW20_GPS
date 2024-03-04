package com.example.hw20_gps;

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.location.LocationProvider
import android.os.Bundle
import android.provider.Settings
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var latitudeTextView: TextView
    private lateinit var longitudeTextView: TextView
    private lateinit var gpsStatusTextView: TextView

    private lateinit var locationManager: LocationManager


    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        latitudeTextView = findViewById(R.id.latitudeTextView)
        longitudeTextView = findViewById(R.id.longitudeTextView)
        gpsStatusTextView = findViewById(R.id.gpsStatusTextView)

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        // Проверка разрешения на доступ к местоположению
        if (isLocationEnabled()) {
            // Регистрация слушателя обновлений местоположения
            locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                1000L, // Минимальное время между обновлениями (в миллисекундах)
                1.0f,   // Минимальное расстояние между обновлениями (в метрах)
                locationListener
            )
            // Попытка получить последние известные координаты
            var lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            if (lastKnownLocation != null) {
                updateLocationViews(lastKnownLocation)
            }
            lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            updateLocationViews(lastKnownLocation)
            when (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                true -> gpsStatusTextView.text = "Статус GPS: GPS доступен"
                false-> gpsStatusTextView.text =
                    "Статус GPS: GPS недоступен"
            }
        } else {
            // Предложение пользователю включить GPS
            startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
        }

    }

    private val locationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            updateLocationViews(location)
        }

        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
            // При изменении статуса GPS
            when (status) {
                LocationProvider.AVAILABLE -> gpsStatusTextView.text = "Статус GPS: GPS доступен"
                LocationProvider.OUT_OF_SERVICE -> gpsStatusTextView.text =
                    "Статус GPS: GPS недоступен (вне службы)"
                LocationProvider.TEMPORARILY_UNAVAILABLE -> gpsStatusTextView.text =
                    "Статус GPS: GPS временно недоступен"
            }
        }

        override fun onProviderEnabled(provider: String) {
            gpsStatusTextView.text = "Статус GPS: GPS включен"
        }

        override fun onProviderDisabled(provider: String) {
            gpsStatusTextView.text = "Статус GPS: GPS выключен"
        }
    }

    private fun updateLocationViews(location: Location?) {
        // Обновление TextView с координатами
        latitudeTextView.text = "Широта: ${location?.latitude}"
        longitudeTextView.text = "Долгота: ${location?.longitude}"
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }
}

package com.example.myapitest

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.location.Location
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.myapitest.api.RetrofitInstance
import com.example.myapitest.api.safeApiCall
import com.example.myapitest.databinding.ActivityCreateCarBinding
import com.example.myapitest.model.Car
import com.example.myapitest.model.Place
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

class CreateCarActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateCarBinding
    private val defaultImageUrl = "https://upload.wikimedia.org/wikipedia/commons/5/5a/Car_icon_alone.png"

    private var userLocation: Place? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationPermissionLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateCarBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupView()
        requestLocationPermission()
    }

    private fun setupView() {
        binding.btnSave.setOnClickListener { btnSaveOnClick() }
        requestLocationPermission()
    }

    private fun btnSaveOnClick() {
        if (validateFields()) {
            // Verifica se a localização foi obtida
            if (userLocation == null) {
                Toast.makeText(
                    this,
                    getString(R.string.a_localizacao_nao_foi_obtida_tente_novamente),
                    Toast.LENGTH_SHORT
                ).show()
                return
            }
            saveCar()
        } else {
            Toast.makeText(
                this,
                getString(R.string.por_favor_preencha_todos_os_campos),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun validateFields(): Boolean {
        return binding.etName.text.isNotEmpty() &&
                binding.etYear.text.isNotEmpty() &&
                binding.etLicence.text.isNotEmpty()
    }

    private fun requestLocationPermission() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->

            if (isGranted) {
                getCurrentLocation()
            } else {
                Toast.makeText(
                    this,
                    getString(R.string.nao_foi_possivel_obter_permissao_para_localizacao),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        checkLocationEnabled()
    }

    private fun checkLocationEnabled() {
        when {
            ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) == PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION) == PERMISSION_GRANTED -> {
                getCurrentLocation()
            }
            shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION) -> {
                locationPermissionLauncher.launch(ACCESS_FINE_LOCATION)
            }
            shouldShowRequestPermissionRationale(ACCESS_COARSE_LOCATION) -> {
                locationPermissionLauncher.launch(ACCESS_COARSE_LOCATION)
            }
            else -> {
                locationPermissionLauncher.launch(ACCESS_FINE_LOCATION)
            }
        }
    }

    private fun getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            requestLocationPermission()
            return
        }
        fusedLocationClient.lastLocation.addOnCompleteListener { task: Task<Location> ->
            if (task.isSuccessful) {
                val location = task.result
                if (location != null) {
                    userLocation = Place(
                        lat = location.latitude.toFloat(),
                        long = location.longitude.toFloat()
                    )
                } else {
                    Toast.makeText(
                        this,
                        getString(R.string.nao_foi_possivel_obter_a_localizacao),
                        Toast.LENGTH_LONG
                    ).show()
                }
            } else {
                Toast.makeText(
                    this,
                    getString(R.string.erro_desconhecido_ao_obter_localizacao),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun saveCar() {
        // Verifica novamente se a localização foi obtida
        if (userLocation == null) {
            Toast.makeText(
                this,
                getString(R.string.nao_foi_possivel_obter_a_localizacao),
                Toast.LENGTH_SHORT).show()
            return
        }

        val inputImageUrl = binding.etImageUrl.text.toString()
        val finalImageUrl = if (isValidUrl(inputImageUrl)) inputImageUrl else defaultImageUrl

        //** MODIFICAR: URL fixo da imagem
        val car = Car(
            id = UUID.randomUUID().toString(),
            name = binding.etName.text.toString(),
            year = binding.etYear.text.toString(),
            licence = binding.etLicence.text.toString(),
            imageUrl = finalImageUrl,
            place = userLocation!!
        )

        CoroutineScope(Dispatchers.IO).launch {
            val result = safeApiCall { RetrofitInstance.apiService.createCar(car) }
            withContext(Dispatchers.Main) {
                when (result) {
                    is com.example.myapitest.api.Result.Error -> {
                        Toast.makeText(
                            this@CreateCarActivity,
                            getString(R.string.erro_desconhecido),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    is com.example.myapitest.api.Result.Success -> {
                        Toast.makeText(
                            this@CreateCarActivity,
                            getString(R.string.carro_salvo_com_sucesso),
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    }
                }
            }
        }
    }

    private fun isValidUrl(url: String): Boolean {
        return try {
            val uri = java.net.URL(url)
            uri.toURI()
            true
        } catch (e: Exception) {
            false
        }
    }

    companion object {
        private const val REQUEST_LOCATION_PERMISSION = 100
        fun newIntent(context: Context) = Intent(context, CreateCarActivity::class.java)
    }
}

package com.example.myapitest

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.myapitest.api.RetrofitInstance
import com.example.myapitest.databinding.ActivityEditCarBinding
import com.example.myapitest.model.Car
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EditCarActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditCarBinding
    private lateinit var car: Car
    private val defaultImageUrl = "https://upload.wikimedia.org/wikipedia/commons/5/5a/Car_icon_alone.png"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditCarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        car = intent.getParcelableExtra("car") ?: run {
            Toast.makeText(
                this,
                getString(R.string.carro_nao_encontrado_no_servidor),
                Toast.LENGTH_SHORT
            ).show()
            finish()
            return
        }

        binding.etName.setText(car.name)
        binding.etYear.setText(car.year)
        binding.etLicence.setText(car.licence)
        Glide.with(this)
            .load(car.imageUrl)
            .placeholder(R.drawable.ic_placeholder)
            .error(R.drawable.ic_error)
            .into(binding.imageView)

        if(car.imageUrl != defaultImageUrl)
            binding.etImageUrl.setText(car.imageUrl)

        binding.btnSave.setOnClickListener { saveCar() }
        binding.btnDelete.setOnClickListener { deleteCar() }
    }

    private fun saveCar() {

        val inputImageUrl = binding.etImageUrl.text.toString()
        val finalImageUrl = if (isValidUrl(inputImageUrl)) inputImageUrl else defaultImageUrl

        val updatedCar = Car(
            id = car.id,
            name = binding.etName.text.toString(),
            year = binding.etYear.text.toString(),
            licence = binding.etLicence.text.toString(),
            imageUrl = finalImageUrl,
            place = car.place
        )

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val result = RetrofitInstance.apiService.updateCar(car.id.toString(), updatedCar)
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@EditCarActivity,
                        getString(R.string.carro_salvo_com_sucesso),
                        Toast.LENGTH_SHORT
                    ).show()
                    val intent = Intent().apply {
                        putExtra("updatedCar", updatedCar)
                    }
                    setResult(RESULT_OK, intent)
                    finish()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@EditCarActivity,
                        getString(R.string.erro_ao_salvar_carro),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun deleteCar() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                RetrofitInstance.apiService.deleteCar(car.id.toString())
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@EditCarActivity,
                        getString(R.string.carro_deletado_com_sucesso),
                        Toast.LENGTH_SHORT
                    ).show()
                    val intent = Intent().apply {
                        putExtra("deletedCarId", car.id)
                    }
                    setResult(RESULT_DELETED, intent)
                    finish()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@EditCarActivity,
                        getString(R.string.erro_ao_deletar_carro),
                        Toast.LENGTH_SHORT
                    ).show()
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
        const val RESULT_DELETED = 2
    }
}
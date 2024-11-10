package com.example.myapitest

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapitest.adapter.CarAdapter
import com.example.myapitest.api.RetrofitInstance
import com.example.myapitest.databinding.ActivityMainBinding
import com.example.myapitest.model.Car
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var carAdapter: CarAdapter
    private val carList = mutableListOf<Car>() // Lista de carros

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupView()

        getCarsFromApi()
    }

    override fun onResume() {
        super.onResume()
        getCarsFromApi()
    }

    private fun setupView() {
        carAdapter = CarAdapter(carList)
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = carAdapter
        }

        binding.addCta.setOnClickListener {
            val intent = Intent(this, CreateCarActivity::class.java)
            startActivity(intent)
        }
    }

    private fun getCarsFromApi() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val cars = RetrofitInstance.apiService.getCars()
                withContext(Dispatchers.Main) {
                    carList.clear()
                    carList.addAll(cars)
                    carAdapter.notifyDataSetChanged() // Atualiza a RecyclerView
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@MainActivity,
                    getString(R.string.falha_na_comunicacao_com_o_servidor_ao_tentar_obter_a_lista_de_carros),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    @Deprecated("Use ActivityResultContracts instead")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CREATE_CAR_REQUEST_CODE && resultCode == RESULT_OK) {
            data?.getParcelableExtra<Car>("new_car")?.let { newCar ->
                carList.add(newCar)
                carAdapter.notifyItemInserted(carList.size - 1)
            }
        } else if (requestCode == EDIT_CAR_REQUEST_CODE) {
            when (resultCode) {
                RESULT_OK -> {
                    data?.getParcelableExtra<Car>("updatedCar")?.let { updatedCar ->
                        val index = carList.indexOfFirst { it.id == updatedCar.id }
                        if (index != -1) {
                            carList[index] = updatedCar
                            carAdapter.notifyItemChanged(index)
                        }
                    }
                }
                EditCarActivity.RESULT_DELETED -> {
                    val deletedCarId = data?.getStringExtra("deletedCarId")
                    deletedCarId?.let { id ->
                        val index = carList.indexOfFirst { it.id == id }
                        if (index != -1) {
                            carList.removeAt(index)
                            carAdapter.notifyItemRemoved(index)
                        }
                    }
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                logout()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun logout() {
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    companion object {
        const val CREATE_CAR_REQUEST_CODE = 100
        const val EDIT_CAR_REQUEST_CODE = 100
        fun newIntent(context: Context) = Intent(context, MainActivity::class.java)
    }
}

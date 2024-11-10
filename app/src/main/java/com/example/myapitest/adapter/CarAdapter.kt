package com.example.myapitest.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapitest.EditCarActivity
import com.example.myapitest.R
import com.example.myapitest.model.Car

class CarAdapter(
    private val carList: List<Car>
) : RecyclerView.Adapter<CarAdapter.CarViewHolder>() {

    class CarViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.image)
        val model: TextView = view.findViewById(R.id.model)
        val year: TextView = view.findViewById(R.id.year)
        val license: TextView = view.findViewById(R.id.license)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_car_layout, parent, false)
        return CarViewHolder(view)
    }

    override fun onBindViewHolder(holder: CarViewHolder, position: Int) {
        val car = carList[position]
        holder.model.text = car.name
        holder.year.text = car.year
        holder.license.text = car.licence

        Glide.with(holder.image.context)
            .load(car.imageUrl)
            .into(holder.image)

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, EditCarActivity::class.java)
            intent.putExtra("car", car) // Passando o objeto completo
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = carList.size
}
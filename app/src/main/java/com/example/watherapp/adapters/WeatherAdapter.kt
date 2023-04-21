package com.example.watherapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.watherapp.R
import com.example.watherapp.WeatherModel
import com.example.watherapp.databinding.ListItemBinding
import com.squareup.picasso.Picasso

class WeatherAdapter(private val listener: Listener?) : ListAdapter<WeatherModel, WeatherAdapter.Holder>(Comparator()) {

    class Holder(view: View, private val listener: Listener?) : RecyclerView.ViewHolder(view) { // Шаблон формирования элемента
        val binding = ListItemBinding.bind(view)
        var itemTemp: WeatherModel? = null

        init {
            itemView.setOnClickListener() {
                itemTemp?.let { it1 -> listener?.onClick(it1) }
            }
        }

        fun bind(item: WeatherModel) {
            itemTemp = item
            binding.tvDate.text = item.time
            binding.tvCondition.text = item.condition
            binding.tvTemp.text = item.currentTemp.ifEmpty {"${item.maxTemp}C / ${item.minTemp}C"}
            Picasso.get().load("https:" + item.imageUrl).into(binding.im)
        }
    }

    class Comparator :
        DiffUtil.ItemCallback<WeatherModel>() { // Сравнивает элементы и перерисовывает если они обновились
        override fun areItemsTheSame(oldItem: WeatherModel, newItem: WeatherModel): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: WeatherModel, newItem: WeatherModel): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item, parent, false) // Создание item
        return Holder(view, listener)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(getItem(position))
    }

    interface Listener{
        fun onClick(item: WeatherModel)
    }
}
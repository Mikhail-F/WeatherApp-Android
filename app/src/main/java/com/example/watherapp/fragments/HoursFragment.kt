package com.example.watherapp.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.watherapp.MainViewModel
import com.example.watherapp.R
import com.example.watherapp.WeatherModel
import com.example.watherapp.adapters.WeatherAdapter
import com.example.watherapp.databinding.FragmentHoursBinding
import org.json.JSONArray
import org.json.JSONObject

class HoursFragment : Fragment() {
    private lateinit var binding: FragmentHoursBinding
    private lateinit var adapter: WeatherAdapter
    private val model: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHoursBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initRcView()
    }

    private fun initRcView() = with(binding) {
        rcView.layoutManager =
            LinearLayoutManager(activity) // Горизонтальный иль вертикальный вывод
        adapter = WeatherAdapter(null)
        rcView.adapter = adapter
        model.liveDataCurrent.observe(viewLifecycleOwner) {
            val list = getHoursList(it)
            adapter.submitList(list)
        }

    }

    private fun getHoursList(item: WeatherModel): List<WeatherModel> {
        val hoursArray = JSONArray(item.hours)
        val list = ArrayList<WeatherModel>()
        for (i in 0 until hoursArray.length()) {
            val currentDay = hoursArray[i] as JSONObject
            val newItem = WeatherModel(
                item.city,
                currentDay.getString("time"),
                currentDay.getJSONObject("condition").getString("text"),
                currentDay.getString("temp_c"),
                item.maxTemp,
                item.minTemp,
                currentDay.getJSONObject("condition").getString("icon"),
                ""
            )
            list.add(newItem)
        }
        return list
    }

    companion object {
        @JvmStatic
        fun newInstance() = HoursFragment()
    }
}
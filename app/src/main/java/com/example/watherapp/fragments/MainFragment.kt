package com.example.watherapp.fragments

import android.Manifest
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.watherapp.*
import com.example.watherapp.adapters.VpAdapter
import com.example.watherapp.databinding.FragmentMainBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.tabs.TabLayoutMediator
import com.squareup.picasso.Picasso
import org.json.JSONObject

class MainFragment : Fragment() {
    private lateinit var pLauncher: ActivityResultLauncher<String> // Для разрешений
    private lateinit var fLocationClient: FusedLocationProviderClient

    lateinit var binding: FragmentMainBinding
    private val model: MainViewModel by activityViewModels()

    private val fList = listOf(
        HoursFragment.newInstance(),
        DaysFragment.newInstance()
    )
    private val tList = listOf("Hours", "Days")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        checkPermission()
        init()
        updateCurrentCard()
    }

    override fun onResume() {
        super.onResume()
        LocationService().checkLocation( ::requestWeatherData, requireContext(), activity)
    }

    private fun init() = with(binding) {
        fLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        val adapter = VpAdapter(activity as FragmentActivity, fList)
        vp.adapter = adapter
        TabLayoutMediator(tabLayout, vp) { tab, position ->
            tab.text = tList[position]
        }.attach()
        binding.ibSync.setOnClickListener {
            tabLayout.selectTab(tabLayout.getTabAt(0))
            LocationService().checkLocation( ::requestWeatherData, requireContext(), activity)
        }
        binding.ibSearch.setOnClickListener {
            DialogManager.searchCityDialog(requireContext(), object : DialogManager.Listener{
                override fun onClick(name: String?) {
                    if (name != null) {
                        requestWeatherData(name)
                    }
                }

            })
        }
    }

    private fun updateCurrentCard() = with(binding) {
        model.liveDataCurrent.observe(viewLifecycleOwner) {
            tvData.text = it.time
            tvCity.text = it.city
            tvCurrentTemp.text = it.currentTemp.ifEmpty { "${it.maxTemp}C / ${it.minTemp}C" }
            tvCondition.text = it.condition
            tvMaxMinTemp.text =
                if (it.currentTemp.isEmpty()) "" else "${it.maxTemp}C / ${it.minTemp}C"
            Picasso.get().load("https:" + it.imageUrl).into(imWather)
        }
    }

    private fun requestWeatherData(cityName: String) {
        val url = "http://api.weatherapi.com/v1/forecast.json?" +
                "key=$API_KEY&q=$cityName&days=3&aqi=no"

        val queue = Volley.newRequestQueue(context)
        val request = StringRequest(
            Request.Method.GET, url, { response ->
                parseWeatherData(response)
            },
            { error -> Log.d("Log", "Volley error $error") }
        )
        queue.add(request)
    }

    private fun parseWeatherData(result: String) {
        val mainObject = JSONObject(result)
        Log.d("Log", mainObject.toString())
        val list = WeatherModel.parseDays(mainObject)
        model.liveDataList.value = list
        model.liveDataCurrent.value = WeatherModel.parseCurrentData(mainObject, list[0])
    }

    private fun permissionListener() {
        pLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            Toast.makeText(activity, "Permission is $it", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkPermission() {
        if (!isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION)) {
            permissionListener()
            pLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION) // Делаем запрос за разрешением
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = MainFragment()
    }
}
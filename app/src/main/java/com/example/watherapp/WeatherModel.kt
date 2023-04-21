package com.example.watherapp

import org.json.JSONObject

data class WeatherModel(
    val city: String,
    val time: String,
    val condition: String,
    val currentTemp: String,
    val maxTemp: String,
    val minTemp: String,
    val imageUrl: String,
    val hours: String
) {
    companion object {
        fun parseDays(mainObject: JSONObject): List<WeatherModel> {
            val list = ArrayList<WeatherModel>()
            val daysArray = mainObject.getJSONObject("forecast").getJSONArray("forecastday")

            for (i in 0 until daysArray.length()) {
                val day = daysArray[i] as JSONObject
                val item = WeatherModel(
                    mainObject.getJSONObject("location").getString("name"),
                    day.getString("date"),
                    day.getJSONObject("day").getJSONObject("condition").getString("text"),
                    "",
                    day.getJSONObject("day").getString("maxtemp_c").toFloat().toInt().toString(),
                    day.getJSONObject("day").getString("mintemp_c").toFloat().toInt().toString(),
                    day.getJSONObject("day").getJSONObject("condition").getString("icon"),
                    day.getJSONArray("hour").toString(),
                )
                list.add(item)
            }
            return list
        }

        fun parseCurrentData(mainObject: JSONObject, weatherItem: WeatherModel): WeatherModel {
            return WeatherModel(
                mainObject.getJSONObject("location").getString("name"),
                mainObject.getJSONObject("current").getString("last_updated"),
                mainObject.getJSONObject("current").getJSONObject("condition").getString("text"),
                mainObject.getJSONObject("current").getString("temp_c").toFloat().toInt().toString(),
                weatherItem.maxTemp,
                weatherItem.minTemp,
                mainObject.getJSONObject("current").getJSONObject("condition").getString("icon"),
                weatherItem.hours
            )
        }
    }
}

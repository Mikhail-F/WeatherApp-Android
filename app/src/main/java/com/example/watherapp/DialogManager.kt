package com.example.watherapp

import android.app.AlertDialog
import android.content.Context
import android.widget.EditText

object DialogManager {
    fun locationSettingsDialog(context: Context, listener: Listener) {
        val builder = AlertDialog.Builder(context)
        val dialog = builder.create()
        dialog.setTitle("Enable location ?")
        dialog.setMessage("Do you want enable location ?")
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes") {_, _ ->
            listener.onClick(null)
            dialog.dismiss()
        }
        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No") {_, _ ->
            dialog.dismiss()
        }
        dialog.show()
    }

    fun searchCityDialog(context: Context, listener: Listener) {
        val builder = AlertDialog.Builder(context)
        val edName = EditText(context)
        builder.setView(edName)
        val dialog = builder.create()
        dialog.setTitle("City name")
        dialog.setMessage("Do you want enable location ?")
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes") {_, _ ->
            listener.onClick(edName.text.toString())
            dialog.dismiss()
        }
        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No") {_, _ ->
            dialog.dismiss()
        }
        dialog.show()
    }

    interface Listener {
        fun onClick(name: String?)
    }
}
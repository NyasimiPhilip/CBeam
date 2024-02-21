package com.chaquo.myapplication.alertDialog

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import com.chaquo.myapplication.R
import com.chaquo.myapplication.model.DistributedLoadV
import com.chaquo.myapplication.model.Load
import com.chaquo.myapplication.model.PointLoadV
import com.chaquo.myapplication.model.PointTorque

object LoadDialog {

    interface LoadDialogListener {
        fun onLoadAdded(load: Load)
    }

    fun show(context: Context?, listener: LoadDialogListener) {
        val builder = AlertDialog.Builder(context)
        val inflater = LayoutInflater.from(context)
        val dialogView: View = inflater.inflate(R.layout.load_dialog_layout, null)
        builder.setView(dialogView)

        val loadTypeSpinner = dialogView.findViewById<Spinner>(R.id.loadTypeSpinner)
        val loadMagnitudeEditText = dialogView.findViewById<EditText>(R.id.loadMagnitudeEditText)
        val loadPositionEditText = dialogView.findViewById<EditText>(R.id.loadPositionEditText)
        val loadRangeFrame = dialogView.findViewById<LinearLayout>(R.id.loadRangeFrame)
        val loadStartEditText = dialogView.findViewById<EditText>(R.id.loadStartEditText)
        val loadEndEditText = dialogView.findViewById<EditText>(R.id.loadEndEditText)

        val loadPositionLabelTv = dialogView.findViewById<TextView>(R.id.loadPositionLabel)

        val loadTypeAdapter = ArrayAdapter.createFromResource(context!!, R.array.load_types, android.R.layout.simple_spinner_item)
        loadTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        loadTypeSpinner.adapter = loadTypeAdapter

        loadTypeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val isDistributedLoad = loadTypeSpinner.selectedItem == "Distributed Load"
                loadRangeFrame.visibility = if (isDistributedLoad) View.VISIBLE else View.GONE
                loadPositionEditText.visibility = if (isDistributedLoad) View.GONE else View.VISIBLE
                loadPositionLabelTv.visibility = if (isDistributedLoad) View.GONE else View.VISIBLE
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        builder.setTitle("Add Load")

        builder.setPositiveButton("Add") { dialog, _ ->
            val type = loadTypeSpinner.selectedItem.toString()
            val magnitudeStr = loadMagnitudeEditText.text.toString()


            if (magnitudeStr.isBlank()) {
                Toast.makeText(context, "Please enter valid magnitude and position", Toast.LENGTH_SHORT).show()
                return@setPositiveButton
            }

            val magnitude = magnitudeStr.toInt()


            val load: Load = when (type) {
                "Distributed Load" -> {
                    val startStr = loadStartEditText.text.toString()
                    val endStr = loadEndEditText.text.toString()

                    if (startStr.isBlank() || endStr.isBlank()) {
                        Toast.makeText(context, "Please enter valid start and end positions", Toast.LENGTH_SHORT).show()
                        return@setPositiveButton
                    }

                    val start = startStr.toDouble()
                    val end = endStr.toDouble()
                    DistributedLoadV(magnitude, doubleArrayOf(start, end))
                }
                "Point Load" -> {
                    val positionStr = loadPositionEditText.text.toString()
                    if (positionStr.isBlank()) {
                        Toast.makeText(context, "Please enter valid position", Toast.LENGTH_SHORT).show()
                        return@setPositiveButton
                    }

                    val position = positionStr.toDouble()
                    PointLoadV(magnitude, position)
                }
                "Point Torque" -> {
                    val positionStr = loadPositionEditText.text.toString()
                    if (positionStr.isBlank()) {
                        Toast.makeText(context, "Please enter valid position", Toast.LENGTH_SHORT).show()
                        return@setPositiveButton
                    }

                    val position = positionStr.toDouble()
                    PointTorque(magnitude, position)
                }
                else -> {
                    val positionStr = loadPositionEditText.text.toString()
                    if (positionStr.isBlank()) {
                        Toast.makeText(context, "Please enter valid position", Toast.LENGTH_SHORT).show()
                        return@setPositiveButton
                    }

                    val position = positionStr.toDouble()
                    PointLoadV(magnitude, position)
                }
            }

            listener.onLoadAdded(load)
            dialog.dismiss()
        }

        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }

}

package com.android.cbeam.alertDialog


import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.android.cbeam.model.Load

object LoadDialog {

    interface LoadDialogListener {
        fun onLoadAdded(load: Load, callback: () -> Unit)
    }

    fun show(context: Context?, listener: LoadDialogListener) {
        Log.d("LoadDialog", "Context: $context")
        val builder = AlertDialog.Builder(context)
        val inflater = LayoutInflater.from(context)
        val binding = LoadDialogLayoutBinding.inflate(inflater)
        builder.setView(binding.root)

        binding.apply {
            loadTypeSpinner.adapter = ArrayAdapter.createFromResource(context!!, R.array.load_types, android.R.layout.simple_spinner_item)
            loadTypeSpinner.setSelection(0)

            loadTypeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    val isDistributedLoad = loadTypeSpinner.selectedItem == "Distributed Load"
                    loadRangeTextView.visibility = if(isDistributedLoad) View.VISIBLE else View.GONE
                    loadRangeFrame.visibility = if (isDistributedLoad) View.VISIBLE else View.GONE
                    loadPositionEditText.visibility = if (isDistributedLoad) View.GONE else View.VISIBLE
                    loadPositionLabel.visibility = if (isDistributedLoad) View.GONE else View.VISIBLE

                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        }

        builder.setTitle("Add Load")

        builder.setPositiveButton("Add") { dialog, _ ->
            val type = binding.loadTypeSpinner.selectedItem.toString()
            val magnitudeStr = binding.loadMagnitudeEditText.text.toString()

            if (magnitudeStr.isBlank()) {
                Toast.makeText(context, "Please enter valid magnitude and position", Toast.LENGTH_SHORT).show()
                return@setPositiveButton
            }

            val magnitude = magnitudeStr.toInt()

            val load: Load = when (type) {
                "Distributed Load" -> {
                    val startStr = binding.loadStartEditText.text.toString()
                    val endStr = binding.loadEndEditText.text.toString()

                    if (startStr.isBlank() || endStr.isBlank()) {
                        Toast.makeText(context, "Please enter valid start and end positions", Toast.LENGTH_SHORT).show()
                        return@setPositiveButton
                    }

                    val start = startStr.toDouble()
                    val end = endStr.toDouble()
                    DistributedLoadV(magnitude, doubleArrayOf(start, end))
                }
                "Point Load" -> {
                    val positionStr = binding.loadPositionEditText.text.toString()
                    if (positionStr.isBlank()) {
                        Toast.makeText(context, "Please enter valid position", Toast.LENGTH_SHORT).show()
                        return@setPositiveButton
                    }

                    val position = positionStr.toDouble()
                    PointLoadV(magnitude, position)
                }
                "Point Torque" -> {
                    val positionStr = binding.loadPositionEditText.text.toString()
                    if (positionStr.isBlank()) {
                        Toast.makeText(context, "Please enter valid position", Toast.LENGTH_SHORT).show()
                        return@setPositiveButton
                    }

                    val position = positionStr.toDouble()
                    PointTorque(magnitude, position)
                }
                else -> {
                    val positionStr = binding.loadPositionEditText.text.toString()
                    if (positionStr.isBlank()) {
                        Toast.makeText(context, "Please enter valid position", Toast.LENGTH_SHORT).show()
                        return@setPositiveButton
                    }

                    val position = positionStr.toDouble()
                    PointLoadV(magnitude, position)
                }
            }
            listener.onLoadAdded(load) {
                // Hide keyboard after load is added
                val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(binding.root.windowToken, 0)

            }
            dialog.dismiss()
            // Hide keyboard after dialog is dismissed
            val currentFocus = (context as? Activity)?.currentFocus
            currentFocus?.let {
                (context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                    .hideSoftInputFromWindow(it.windowToken, 0)
            }
        }

        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }
}

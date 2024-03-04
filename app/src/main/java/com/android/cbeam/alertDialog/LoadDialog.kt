package com.android.cbeam.alertDialog

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.android.cbeam.R
import com.android.cbeam.databinding.LoadDialogLayoutBinding
import com.android.cbeam.model.DistributedLoadV
import com.android.cbeam.model.Load
import com.android.cbeam.model.PointLoadV
import com.android.cbeam.model.PointTorque

object LoadDialog {

    interface LoadDialogListener {
        fun onLoadAdded(load: Load, callback: () -> Unit)
    }
    fun show(context: Context?, originalLoad: Load? = null, listener: LoadDialogListener) {
        if (context == null) return

        val builder = AlertDialog.Builder(context)
        val inflater = LayoutInflater.from(context)
        val binding = LoadDialogLayoutBinding.inflate(inflater)
        builder.setView(binding.root)

        val loadTypes = context.resources.getStringArray(R.array.load_types)

        // Set up spinner
        val adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, loadTypes)
        binding.loadTypeSpinner.adapter = adapter


        // Set original load data if provided
        originalLoad?.let { load ->
            // Update spinner selection based on the original load type
            val selectedPosition = when (load) {
                is DistributedLoadV -> 1
                is PointLoadV -> 0
                is PointTorque -> 2
                else -> 0 // Default to the first item if the load type is not recognized
            }
            binding.loadTypeSpinner.setSelection(selectedPosition)

            // Handle each load type correctly
            when (load) {
                is DistributedLoadV -> {
                    binding.loadMagnitudeEditText.setText(load.magnitude.toString())
                    binding.loadStartEditText.setText(load.positionRange[0].toString())
                    binding.loadEndEditText.setText(load.positionRange[1].toString())
                }
                is PointLoadV -> {
                    binding.loadMagnitudeEditText.setText(load.magnitude.toString())
                    binding.loadPositionEditText.setText(load.position.toString())
                }
                is PointTorque -> {
                    binding.loadMagnitudeEditText.setText(load.magnitude.toString())
                    binding.loadPositionEditText.setText(load.position.toString())
                }
                else -> {
                    // Handle default case or any other load types
                }
            }
        }
        // Set up spinner listener
        binding.loadTypeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val isDistributedLoad = position == 1
                binding.loadRangeTextView.visibility = if (isDistributedLoad) View.VISIBLE else View.GONE
                binding.loadRangeFrame.visibility = if (isDistributedLoad) View.VISIBLE else View.GONE
                binding.loadPositionEditText.visibility = if (isDistributedLoad) View.GONE else View.VISIBLE
                binding.loadPositionLabel.visibility = if (isDistributedLoad) View.GONE else View.VISIBLE
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        builder.setTitle("Add Load")

        builder.setPositiveButton("Add") { dialog, _ ->
            val type = binding.loadTypeSpinner.selectedItem.toString()
            val magnitudeStr = binding.loadMagnitudeEditText.text.toString()

            if (magnitudeStr.isBlank()) {
                Toast.makeText(context, "Please enter valid magnitude", Toast.LENGTH_SHORT).show()
                return@setPositiveButton
            }

            val magnitude = magnitudeStr.toInt()

            val load = when (type) {
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
                val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(binding.root.windowToken, 0)
            }
            dialog.dismiss()

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

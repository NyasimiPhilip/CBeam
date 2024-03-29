package com.android.cbeam.alertDialog


import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.widget.Toast
import com.android.cbeam.databinding.SupportDialogLayoutBinding
import com.android.cbeam.model.Support

object SupportDialog {

    interface SupportDialogListener {
        fun onSupportAdded(updatedSupport: Support?)
    }

    fun show(context: Context?, originalSupport: Support? = null, listener: SupportDialogListener) {
        val builder = AlertDialog.Builder(context)
        val inflater = LayoutInflater.from(context)
        val binding = SupportDialogLayoutBinding.inflate(inflater)
        builder.setView(binding.root)

        builder.setTitle("Add Support")

        originalSupport?.let { support ->
            // Populate the dialog fields with original support data if available
            binding.editTextPosition.setText(support.position.toString())
            val (xChecked, yChecked, mChecked) = support.supportType
            binding.checkBoxX.isChecked = xChecked == 1
            binding.checkBoxY.isChecked = yChecked == 1
            binding.checkBoxM.isChecked = mChecked == 1
        }


        builder.setPositiveButton("Add") { dialog: DialogInterface?, which: Int ->
            val positionStr = binding.editTextPosition.text.toString()
            if (positionStr.isBlank()) {
                showToast(context, "Please enter a position")
                return@setPositiveButton
            }

            val position = try {
                positionStr.toDouble()
            } catch (e: NumberFormatException) {
                showToast(context, "Invalid position format")
                return@setPositiveButton
            }

            val xChecked = binding.checkBoxX.isChecked
            val yChecked = binding.checkBoxY.isChecked
            val mChecked = binding.checkBoxM.isChecked

            if ((xChecked && !yChecked) || (!xChecked && mChecked) || (!yChecked && mChecked)) {
                showToast(context, "That support configuration is not supported")
                return@setPositiveButton
            }

            val supportType = Triple(if (xChecked) 1 else 0, if (yChecked) 1 else 0, if (mChecked) 1 else 0)

            val support = Support(position, supportType)
            listener.onSupportAdded(support)
        }

        builder.setNegativeButton("Cancel") { dialog: DialogInterface, which: Int ->
            dialog.cancel()
        }

        val dialog = builder.create()
        dialog.show()
    }

    private fun showToast(context: Context?, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}

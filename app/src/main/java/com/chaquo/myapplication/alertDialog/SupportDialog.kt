package com.chaquo.myapplication.alertDialog

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.widget.CheckBox
import android.widget.EditText
import com.chaquo.myapplication.R
import com.chaquo.myapplication.model.Support
object SupportDialog {
    interface SupportDialogListener {
        fun onSupportAdded(support: Support?)
    }
    fun show(context: Context?, listener: SupportDialogListener) {
        val builder = AlertDialog.Builder(context)
        val inflater = LayoutInflater.from(context)
        val dialogView: View = inflater.inflate(R.layout.support_dialog_layout, null)
        builder.setView(dialogView)
        val editTextPosition = dialogView.findViewById<EditText>(R.id.editTextPosition)
        val checkBoxX = dialogView.findViewById<CheckBox>(R.id.checkBoxX)
        val checkBoxY = dialogView.findViewById<CheckBox>(R.id.checkBoxY)
        val checkBoxM = dialogView.findViewById<CheckBox>(R.id.checkBoxM)
        builder.setTitle("Add Support")
        builder.setPositiveButton("Add") { dialog: DialogInterface?, which: Int ->
            // Get user input
            val position = editTextPosition.text.toString().toDouble()
            val isXSelected = checkBoxX.isChecked
            val isYSelected = checkBoxY.isChecked
            val isMSelected = checkBoxM.isChecked

            // Create Support object
            val support = Support(
                position,
                Triple(
                    if (isXSelected) 1 else 0,
                    if (isYSelected) 1 else 0,
                    if (isMSelected) 1 else 0
                )
            )

            // Pass the Support object to the listener
            listener.onSupportAdded(support)
        }
        builder.setNegativeButton(
            "Cancel"
        ) { dialog: DialogInterface, which: Int -> dialog.cancel() }
        val dialog = builder.create()
        dialog.show()
    }

}


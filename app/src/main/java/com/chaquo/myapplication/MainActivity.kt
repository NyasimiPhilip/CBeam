package com.chaquo.myapplication

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.chaquo.myapplication.alertDialog.LoadDialog
import com.chaquo.myapplication.alertDialog.SupportDialog
import com.chaquo.myapplication.model.Load
import com.chaquo.myapplication.model.Support
import com.chaquo.python.PyException
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform

class MainActivity : AppCompatActivity(), SupportDialog.SupportDialogListener, LoadDialog.LoadDialogListener {

    private val supports = mutableListOf<Support>()
    private val loads = mutableListOf<Load>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (!Python.isStarted()) {
            Python.start(AndroidPlatform(this))
        }
        val py = Python.getInstance()
        val module = py.getModule("Analyser")

        findViewById<Button>(R.id.btnAddSupport).setOnClickListener {
            SupportDialog.show(this, this)
        }
        findViewById<Button>(R.id.btnAddLoad).setOnClickListener{
            LoadDialog.show(this, this)
        }

        findViewById<Button>(R.id.btnPlot).setOnClickListener {
            val editTextBeamLength = findViewById<EditText>(R.id.editTextBeamLength)
            val beamLength: Double = editTextBeamLength.text.toString().toDoubleOrNull() ?: 0.0
            try {
                val bytes = module.callAttr("analyse", beamLength, supports.toTypedArray(), loads.toTypedArray())
                    .toJava(ByteArray::class.java)
                val intent = Intent(this, SecondActivity::class.java)
                intent.putExtra("bitmap", bytes)
                startActivity(intent)

                currentFocus?.let {
                    (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                        .hideSoftInputFromWindow(it.windowToken, 0)
                }
            } catch (e: PyException) {
                Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
                Log.d("MyTag", "Support objects: $supports")
                Log.d("MyTag", "Load objects: $loads")
                Log.e("Error", "this is the error ${e.message}")
            }
        }

    }

    override fun onSupportAdded(support: Support?) {
        support?.let {
            supports.add(it)
        }
    }

    override fun onLoadAdded(load: Load) {
        load.let{
            loads.add(load)
        }
    }

}

package com.chaquo.myapplication

import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class SecondActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.second_activity)

        // Retrieve bitmap data from intent extras
        val byteArray = intent?.getByteArrayExtra("bitmap")

        if (byteArray != null) {
            val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)

            // Display bitmap in ImageView
            findViewById<ImageView>(R.id.imageView)?.setImageBitmap(bitmap)
        } else {
            // Handle the case when byteArray is null
            // For example, display an error message or load a default image
        }
    }
}

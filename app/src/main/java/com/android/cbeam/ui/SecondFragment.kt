package com.android.cbeam.ui

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.android.cbeam.databinding.FragmentSecondBinding


class SecondFragment : Fragment() {
    private lateinit var binding: FragmentSecondBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSecondBinding.inflate(inflater, container, false)
        val view = binding.root
        // Set action bar title to an empty string
        (activity as? AppCompatActivity)?.supportActionBar?.title = ""

        // Retrieve byte array data from arguments
        val byteArray = arguments?.getByteArray("bytes")
        val modelByteArray = arguments?.getByteArray("model")

        if (byteArray != null && modelByteArray != null) {
            val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
            val bitmap2 = BitmapFactory.decodeByteArray(modelByteArray, 0, modelByteArray.size)

            binding.ivModel.setImageBitmap(bitmap2)
            // Set the bitmap to the ImageView using data binding
            binding.imageView.setImageBitmap(bitmap)
        } else {
            // Handle the case when byteArray is null
            // For example, display an error message or load a default image
        }
        return view
    }

}

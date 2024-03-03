package com.android.cbeam.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.android.cbeam.databinding.FragmentBeamDesignBinding

class BeamDesignFragment : Fragment() {
    private lateinit var binding: FragmentBeamDesignBinding
    private val viewModel: BeamDesignFragmentViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBeamDesignBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        // Set action bar title to an empty string
        (activity as? AppCompatActivity)?.supportActionBar?.title = ""

        binding.buttonCalculate.setOnClickListener {
            calculate()
        }
        viewModel.result.observe(viewLifecycleOwner) { result ->
            binding.textViewResult.text = result
        }

        return binding.root
    }

    private fun calculate() {
        val height = binding.editTextHeight.text.toString().toDoubleOrNull() ?: return
        val width = binding.editTextWidth.text.toString().toDoubleOrNull() ?: return
        val cover = binding.editTextCover.text.toString().toDoubleOrNull() ?: return
        val moment = binding.editTextMoment.text.toString().toDoubleOrNull() ?: return
        val strength = binding.editTextStrength.text.toString().toDoubleOrNull() ?: return
        val reinforcementStrength = binding.editTextReinforcement.text.toString().toDoubleOrNull() ?: return
        val barDiameter = binding.editTextBarDiameter.text.toString().toDoubleOrNull() ?: return
        val linkDiameter = binding.editTextLinkDiameter.text.toString().toDoubleOrNull() ?: return

        viewModel.calculate(height, width, cover, moment, strength, reinforcementStrength, barDiameter, linkDiameter)
    }
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("height", binding.editTextHeight.text.toString())
        outState.putString("width", binding.editTextWidth.text.toString())
        outState.putString("cover", binding.editTextCover.text.toString())
        outState.putString("moment", binding.editTextMoment.text.toString())
        outState.putString("strength", binding.editTextStrength.text.toString())
        outState.putString("reinforcementStrength", binding.editTextReinforcement.text.toString())
        outState.putString("barDiameter", binding.editTextBarDiameter.text.toString())
        outState.putString("linkDiameter", binding.editTextLinkDiameter.text.toString())

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (savedInstanceState != null) {
            binding.editTextHeight.setText(savedInstanceState.getString("height", ""))
            binding.editTextWidth.setText(savedInstanceState.getString("width", ""))
            binding.editTextCover.setText(savedInstanceState.getString("cover", ""))
            binding.editTextMoment.setText(savedInstanceState.getString("moment", ""))
            binding.editTextStrength.setText(savedInstanceState.getString("strength", ""))
            binding.editTextReinforcement.setText(savedInstanceState.getString("reinforcementStrength", ""))
            binding.editTextBarDiameter.setText(savedInstanceState.getString("barDiameter", ""))
            binding.editTextLinkDiameter.setText(savedInstanceState.getString("linkDiameter", ""))
        }
    }

}

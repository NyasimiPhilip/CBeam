package com.android.cbeam.ui

import android.widget.EditText
import androidx.databinding.BindingAdapter
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlin.math.pow
import kotlin.math.sqrt

class BeamDesignFragmentViewModel : ViewModel() {

    private val _result = MutableLiveData<String>()
    val result: LiveData<String>
        get() = _result

    // LiveData properties for EditText fields
    private val _height = MutableLiveData<Double>()
    val height: LiveData<Double>
        get() = _height

    private val _width = MutableLiveData<Double>()
    val width: LiveData<Double>
        get() = _width

    private val _cover = MutableLiveData<Double>()
    val cover: LiveData<Double>
        get() = _cover

    private val _moment = MutableLiveData<Double>()
    val moment: LiveData<Double>
        get() = _moment

    private val _strength = MutableLiveData<Double>()
    val strength: LiveData<Double>
        get() = _strength

    private val _reinforcementStrength = MutableLiveData<Double>()
    val reinforcementStrength: LiveData<Double>
        get() = _reinforcementStrength

    private val _barDiameter = MutableLiveData<Double>()
    val barDiameter: LiveData<Double>
        get() = _barDiameter

    private val _linkDiameter = MutableLiveData<Double>()
    val linkDiameter: LiveData<Double>
        get() = _linkDiameter

    fun calculate(
        height: Double, width: Double, cover: Double, moment: Double,
        strength: Double, reinforcementStrength: Double, barDiameter: Double,
        linkDiameter: Double
    ) {
        val d = height - cover - barDiameter / 2 - linkDiameter
        val K = moment / (width * d.pow(2) * strength)


        val steelArea = if (K < 0.167) {
            val innerSqrtValue = 0.25 - K / 1.134
            var Z = d * (0.5 + sqrt(innerSqrtValue))
            println("0.95 * d= ${0.95 * d}")
            if (Z < 0.95 * d) {
                val As = moment / (0.87 * reinforcementStrength * Z)
                println(As)
                Pair(As, "Singly Reinforced")
            } else {
                val As = moment / (0.87 * reinforcementStrength * (0.95 * d))
                println(As)
                Pair(As, "Singly Reinforced")
            }
        } else {
            val K_prime = 0.167
            val d_prime = cover + linkDiameter + barDiameter / 2
            val As_prime = (K - K_prime) * strength * width * d.pow(2) / (0.87 * reinforcementStrength * (d - d_prime))
            val innerSqrtValue = 0.25 - K / 1.134
            if (innerSqrtValue < 0) {
                println("Error: Negative value under the square root. Beam fails.")
                _result.value = "Beam Fails"
                return
            }
            var Z = d * (0.5 + sqrt(innerSqrtValue))
            val As = if (Z < 0.95 * d) {
                (K_prime * strength * width * d.pow(2)) / (0.87 * reinforcementStrength * Z) + As_prime
            } else {
                Z = 0.95 * d
                (K_prime * strength * width * d.pow(2)) / (0.87 * reinforcementStrength * Z) + As_prime
            }
            Triple(As_prime, As, "Doubly Reinforced (Compression and Tension)")
        }

        steelArea.let {
            val resultString = when (it) {
                is Pair<*, *> -> "Result: ${it.first}, ${it.second}"
                is Triple<*, *, *> -> "Result: ${it.first}, ${it.second}, ${it.third}"
                else -> "Invalid result"
            }
            _result.value = resultString
        }

    }

    companion object {
        @JvmStatic
        @BindingAdapter("app:heightWithSuffix")
        fun setHeightWithSuffix(editText: EditText, height: Double?) {
            height?.let {
                val formattedHeight = "$height mm"
                if (editText.text.toString() != formattedHeight) {
                    editText.setText(formattedHeight)
                }
            }
        }

        @JvmStatic
        @BindingAdapter("app:widthWithSuffix")
        fun setWidthWithSuffix(editText: EditText, width: Double?) {
            width?.let {
                val formattedWidth = "$width mm"
                if (editText.text.toString() != formattedWidth) {
                    editText.setText(formattedWidth)
                }
            }
        }

        // Define other BindingAdapters for other EditText fields similarly...
    }
}

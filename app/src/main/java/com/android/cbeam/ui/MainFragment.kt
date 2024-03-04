package com.android.cbeam.ui

import android.content.Context
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.cbeam.R
import com.android.cbeam.adapter.LoadAdapter
import com.android.cbeam.adapter.SupportAdapter
import com.android.cbeam.alertDialog.LoadDialog
import com.android.cbeam.alertDialog.SupportDialog
import com.android.cbeam.databinding.FragmentMainBinding
import com.android.cbeam.model.Load
import com.android.cbeam.model.Support
import com.chaquo.python.PyException
import com.chaquo.python.PyObject
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class MainFragment : Fragment(), SupportAdapter.ItemDeleteListener,
    LoadAdapter.ItemDeleteListener, SupportAdapter.ItemEditListener, LoadAdapter.ItemEditListener {

    private lateinit var binding: FragmentMainBinding
    private lateinit var supportAdapter: SupportAdapter
    private lateinit var loadAdapter: LoadAdapter
    private lateinit var module: PyObject
    private var isAnalysisPerformed: Boolean = false
    private lateinit var progressBar: ProgressBar

    private val viewModel: MainFragmentViewModel by activityViewModels {
        SavedStateViewModelFactory(requireActivity().application, this)
    }
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        val view = binding.root

        // Set action bar title to an empty string
        (activity as? AppCompatActivity)?.supportActionBar?.title = ""

        if (!Python.isStarted()) {
            Python.start(AndroidPlatform(requireContext()))
        }
        val py = Python.getInstance()
        module = py.getModule("Analyser")

        with(binding) {
            lifecycleOwner = viewLifecycleOwner

            this@MainFragment.progressBar = progressBar
            supportAdapter = SupportAdapter(viewModel.supportsLiveData.value ?: mutableListOf(), this@MainFragment, this@MainFragment)
            loadAdapter = LoadAdapter(viewModel.loadsLiveData.value ?: mutableListOf(), this@MainFragment, this@MainFragment)
            rvLoads.adapter = loadAdapter
            rvSupports.adapter = supportAdapter
            rvLoads.layoutManager = LinearLayoutManager(requireContext())
            rvSupports.layoutManager = LinearLayoutManager(requireContext())

            if (isAnalysisPerformed) {
                analysisCV.visibility = View.VISIBLE
            }
            viewModel.supportReaction.observe(viewLifecycleOwner) { supportReaction ->
                // Update support reaction TextView
                binding.supportReactionTV.text = supportReaction
            }

            viewModel.bendingMoment.observe(viewLifecycleOwner) { bendingMoment ->
                // Update bending moment TextView
                binding.bendingMomentTV.text = bendingMoment
            }
            btnAddSupport.setOnClickListener {
                SupportDialog.show(requireContext(), null, object : SupportDialog.SupportDialogListener {
                    override fun onSupportAdded(updatedSupport: Support?) {
                        updatedSupport?.let {
                            viewModel.addSupport(it)
                        }
                    }
                })
            }
            btnAddLoad.setOnClickListener {
                LoadDialog.show(requireContext(),null,  object : LoadDialog.LoadDialogListener {
                    override fun onLoadAdded(load: Load, callback: () -> Unit) {
                        viewModel.addLoad(load)
                    }
                })
            }
            deleteCV.setOnClickListener {
                editTextBeamLength.setText("")
                viewModel.clearData()
                supportAdapter.notifyDataSetChanged()
                loadAdapter.notifyDataSetChanged()
                analysisCV.visibility = View.GONE
                isAnalysisPerformed = false
            }
            btnAnalyse.setOnClickListener {
                val beamLength: Double = editTextBeamLength.text.toString().toDoubleOrNull() ?: 0.0

                // Show loading screen
                showLoadingScreen()

                // Perform heavy computation in a background thread
                lifecycleScope.launch {
                    delay(700)
                    try {
                        val supportReaction = module.callAttr("getSupportReaction", beamLength, viewModel.supportsLiveData.value?.toTypedArray(), viewModel.loadsLiveData.value?.toTypedArray())
                        val bendingMoment = module.callAttr("getMaximumBendingMoment", beamLength, viewModel.supportsLiveData.value?.toTypedArray(), viewModel.loadsLiveData.value?.toTypedArray())

                        // Update ViewModel with new values
                        viewModel.updateSupportReaction("Support Reactions : $supportReaction")
                        viewModel.updateBendingMoment("Bending Moment : $bendingMoment")

                        analysisCV.visibility = View.VISIBLE
                        isAnalysisPerformed = true

                        Log.d("Output", "$supportReaction \n $bendingMoment")

                    } catch (e: PyException) {                        // Show error message
                        Toast.makeText(requireContext(), e.message, Toast.LENGTH_LONG).show()
                        Log.d("MyTag", "Support objects: ${viewModel.supportsLiveData.value}")
                        Log.d("MyTag", "Load objects: ${viewModel.loadsLiveData.value}")
                        Log.e("Error", "this is the error ${e.message}")
                    } finally {
                        // Hide loading screen
                        hideLoadingScreen()
                    }
                }
            }

            btnPlot.setOnClickListener {
                val beamLength: Double = editTextBeamLength.text.toString().toDoubleOrNull() ?: 0.0

                // Show loading screen
                showLoadingScreen()

                // Perform heavy computation in a background thread
                lifecycleScope.launch {
                    delay(700)
                    try {
                        val result = module.callAttr("analyse", beamLength, viewModel.supportsLiveData.value?.toTypedArray(), viewModel.loadsLiveData.value?.toTypedArray())
                        val byteArray = result.toJava(ByteArray::class.java)

                        val model = module.callAttr("draw_beam", beamLength, viewModel.supportsLiveData.value?.toTypedArray(), viewModel.loadsLiveData.value?.toTypedArray())
                        val modelByteArray = model.toJava(ByteArray::class.java)

                        // Navigate to the next fragment
                        val bundle = Bundle().apply {
                            putByteArray("bytes", byteArray)
                            putByteArray("model", modelByteArray)
                        }
                        val secondFragment = SecondFragment().apply {
                            arguments = bundle
                        }
                        requireActivity().supportFragmentManager.beginTransaction()
                            .replace(R.id.fragmentContainerView, secondFragment)
                            .addToBackStack("MainFragment")
                            .commit()
                    } catch (e: PyException) {
                        // Show error message
                        Toast.makeText(requireContext(), e.message, Toast.LENGTH_LONG).show()
                        Log.d("MyTag", "Support objects: ${viewModel.supportsLiveData.value}")
                        Log.d("MyTag", "Load objects: ${viewModel.loadsLiveData.value}")
                        Log.e("Error", "this is the error ${e.message}")
                    } finally {
                        // Hide loading screen
                        hideLoadingScreen()
                    }
                }
            }

            editTextBeamLength.setOnClickListener {
                editTextBeamLength.showKeyboard()
            }

            view.setOnTouchListener { _, event ->
                if (event.action == MotionEvent.ACTION_DOWN) {
                    val outRect = Rect()
                    editTextBeamLength.getGlobalVisibleRect(outRect)
                    if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                        editTextBeamLength.hideKeyboard()
                        editTextBeamLength.isFocusableInTouchMode = false
                        editTextBeamLength.isFocusable = false
                    }
                } else if (event.action == MotionEvent.ACTION_UP && view.isPressed) {
                    // Call performClick when MotionEvent.ACTION_UP is detected and the view is pressed
                    view.performClick()
                }
                false
            }
            // Swipe-to-delete functionality for supports
            val supportItemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
                0,
                ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
            ) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val position = viewHolder.adapterPosition
                    viewModel.removeSupport(position)
                    supportAdapter.notifyItemRemoved(position)
                }
            })

            supportItemTouchHelper.attachToRecyclerView(rvSupports)

            // Swipe-to-delete functionality for loads
            val loadItemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
                0,
                ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
            ) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val position = viewHolder.adapterPosition
                    viewModel.removeLoad(position)
                    loadAdapter.notifyItemRemoved(position)
                }
            })

            loadItemTouchHelper.attachToRecyclerView(rvLoads)

        }
        return view
    }
    override fun onEditItem(recyclerViewType: Int, position: Int) {
        when (recyclerViewType) {
            LOAD_RECYCLERVIEW_TYPE -> {
                val loadList = viewModel.loadsLiveData.value
                if (loadList.isNullOrEmpty() || position >= loadList.size) {
                    Toast.makeText(context, "Invalid position", Toast.LENGTH_SHORT).show()
                    return
                }
                val load = loadList[position]
                LoadDialog.show(requireContext(), load, object : LoadDialog.LoadDialogListener {
                    override fun onLoadAdded(updatedLoad: Load, callback: () -> Unit) {
                        // Update the load item in the ViewModel if needed
                        viewModel.updateLoad(position, updatedLoad)
                        loadAdapter.notifyDataSetChanged()
                    }
                })
            }
            SUPPORT_RECYCLERVIEW_TYPE -> {
                val supportList = viewModel.supportsLiveData.value
                if (supportList.isNullOrEmpty() || position >= supportList.size) {
                    Toast.makeText(context, "Invalid position", Toast.LENGTH_SHORT).show()
                    return
                }
                val support = supportList[position]
                SupportDialog.show(requireContext(), support, object : SupportDialog.SupportDialogListener {
                    override fun onSupportAdded(updatedSupport: Support?) {
                        // Update the support item in the ViewModel if needed
                        updatedSupport?.let {
                            viewModel.updateSupport(position, it)
                            supportAdapter.notifyDataSetChanged()
                        }
                    }
                })
            }
            else -> {
                Toast.makeText(context, "Invalid RecyclerView type", Toast.LENGTH_SHORT).show()
            }
        }
    }




    override fun onDeleteItem(position: Int) {
        // This method is called when an item is swiped and deleted
        // Implement your logic here if needed
    }
    private fun showLoadingScreen() {
        progressBar.visibility = View.VISIBLE
    }
    private fun hideLoadingScreen() {
        progressBar.visibility = View.GONE
    }

    private fun EditText.showKeyboard() {
        isFocusableInTouchMode = true
        isFocusable = true
        requestFocus()
        val inputMethodManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun EditText.hideKeyboard() {
        clearFocus()
        val inputMethodManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
    }
    // Define constants for RecyclerView types
    private companion object {
        const val SUPPORT_RECYCLERVIEW_TYPE = 1
        const val LOAD_RECYCLERVIEW_TYPE = 2
    }
}
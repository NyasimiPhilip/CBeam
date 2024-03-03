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
    LoadAdapter.ItemDeleteListener {

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

        Log.d("Creation","onCreateView")
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
            supportAdapter = SupportAdapter(viewModel.supportsLiveData.value ?: mutableListOf())
            supportAdapter.itemDeleteListener
            loadAdapter = LoadAdapter(viewModel.loadsLiveData.value ?: mutableListOf())
            loadAdapter.itemDeleteListener
            rvLoads.adapter = loadAdapter
            rvSupports.adapter = supportAdapter
            rvLoads.layoutManager = LinearLayoutManager(requireContext())
            rvSupports.layoutManager = LinearLayoutManager(requireContext())

            if (isAnalysisPerformed) {
                analysisCV.visibility = View.VISIBLE
            }


            btnAddSupport.setOnClickListener {
                SupportDialog.show(requireContext(), object : SupportDialog.SupportDialogListener {
                    override fun onSupportAdded(support: Support?) {
                        support?.let {
                            viewModel.addSupport(it)
                        }
                    }
                })
            }

            btnAddLoad.setOnClickListener {
                LoadDialog.show(requireContext(), object : LoadDialog.LoadDialogListener {
                    override fun onLoadAdded(load: Load, callback: () -> Unit) {
                        viewModel.addLoad(load)
                    }
                })
            }
            deleteCV.setOnClickListener{
                editTextBeamLength.setText("");
                viewModel.clearData() // Call a method in ViewModel to clear the data
                supportAdapter.notifyDataSetChanged() // Notify adapters to update UI
                loadAdapter.notifyDataSetChanged()
                analysisCV.visibility = View.GONE // Hide any visible analysis
                isAnalysisPerformed = false // Reset the analysis flag
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
                        isAnalysisPerformed = true // Set flag to true when analysis is performed

                        Log.d("Output", "$supportReaction \n $bendingMoment")

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


            // Observe LiveData for support reaction and bending moment
            viewModel.supportReaction.observe(viewLifecycleOwner) { reaction ->
                supportReactionTV.text = reaction
            }

            viewModel.bendingMoment.observe(viewLifecycleOwner) { moment ->
                bendingMomentTV.text = moment
            }
            fun EditText.showKeyboard() {
                isFocusableInTouchMode = true
                isFocusable = true
                requestFocus()
                val inputMethodManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
            }

            fun EditText.hideKeyboard() {
                clearFocus()
                val inputMethodManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
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


}

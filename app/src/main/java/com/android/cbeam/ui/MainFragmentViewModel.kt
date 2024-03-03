package com.android.cbeam.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.android.cbeam.model.Load
import com.android.cbeam.model.Support

class MainFragmentViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {
    // Define keys for saving and retrieving values from SavedStateHandle
    private val SUPPORT_REACTION_KEY = "support_reaction"
    private val BENDING_MOMENT_KEY = "bending_moment"

    // Initialize LiveData for support reaction and bending moment
    private val _supportReaction = MutableLiveData<String>()
    val supportReaction: LiveData<String> = _supportReaction

    private val _bendingMoment = MutableLiveData<String>()
    val bendingMoment: LiveData<String> = _bendingMoment

    init {
        // Retrieve values from SavedStateHandle if available
        savedStateHandle.get<String>(SUPPORT_REACTION_KEY)?.let { reaction ->
            _supportReaction.value = reaction
        }
        savedStateHandle.get<String>(BENDING_MOMENT_KEY)?.let { moment ->
            _bendingMoment.value = moment
        }
    }

    // Method to update support reaction value
    fun updateSupportReaction(reaction: String) {
        _supportReaction.value = reaction
        // Save the value to SavedStateHandle
        savedStateHandle.set(SUPPORT_REACTION_KEY, reaction)
    }

    // Method to update bending moment value
    fun updateBendingMoment(moment: String) {
        _bendingMoment.value = moment
        // Save the value to SavedStateHandle
        savedStateHandle.set(BENDING_MOMENT_KEY, moment)
    }
    val supportsLiveData: MutableLiveData<MutableList<Support>> by lazy {
        MutableLiveData<MutableList<Support>>()
    }
    val loadsLiveData: MutableLiveData<MutableList<Load>> by lazy {
        MutableLiveData<MutableList<Load>>()
    }

    init {
        supportsLiveData.value = mutableListOf()
        loadsLiveData.value = mutableListOf()
    }

    fun addSupport(support: Support) {
        val currentList = supportsLiveData.value
        currentList?.add(support)
        supportsLiveData.value = currentList
    }

    fun addLoad(load: Load) {
        val currentList = loadsLiveData.value
        currentList?.add(load)
        loadsLiveData.value = currentList
    }
    fun removeSupport(position: Int) {
        val currentList = supportsLiveData.value
        if (currentList != null && position >= 0 && position < currentList.size) {
            currentList.removeAt(position)
            supportsLiveData.value = currentList
        }
    }

    fun removeLoad(position: Int) {
        val currentList = loadsLiveData.value
        if (currentList != null && position >= 0 && position < currentList.size) {
            currentList.removeAt(position)
            loadsLiveData.value = currentList
        }
    }
    fun clearData() {
        supportsLiveData.value?.clear()
        loadsLiveData.value?.clear()
        updateSupportReaction("") // Clear support reaction
        updateBendingMoment("") // Clear bending moment
    }

}

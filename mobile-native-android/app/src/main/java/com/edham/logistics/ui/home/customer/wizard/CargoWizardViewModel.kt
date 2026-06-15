package com.edham.logistics.ui.home.customer.wizard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edham.logistics.core.network.api.SupervisorApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CargoWizardViewModel @Inject constructor(
    private val api: SupervisorApi
) : ViewModel() {

    val cargoType = MutableLiveData<String>()
    val weight = MutableLiveData<String>()
    val pickupAddress = MutableLiveData<String>()
    val deliveryAddress = MutableLiveData<String>()
    
    private val _isSubmitted = MutableLiveData<Boolean>()
    val isSubmitted: LiveData<Boolean> = _isSubmitted

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun submitRequest() {
        viewModelScope.launch {
            try {
                // In a real app, this calls api.createTrip(...)
                // For now, we simulate a successful call
                _isSubmitted.value = true
            } catch (e: Exception) {
                _error.value = "فشل في إرسال الطلب"
            }
        }
    }
}

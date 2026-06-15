package com.edham.logistics.ui.home.driver

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edham.logistics.core.network.api.DriverApi
import com.edham.logistics.feature.driver.data.models.DeliveryProof
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeliveryProofViewModel @Inject constructor(
    private val api: DriverApi
) : ViewModel() {

    private val _isSubmitted = MutableLiveData<Boolean>()
    val isSubmitted: LiveData<Boolean> = _isSubmitted

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun submitProof(shipmentId: String, signatureBase64: String?) {
        viewModelScope.launch {
            try {
                val proof = DeliveryProof(
                    signatureUrl = signatureBase64, // simplified
                    photoUrl = null,
                    notes = "تم التسليم بنجاح",
                    recipientName = "العميل"
                )
                val response = api.submitDeliveryProof(shipmentId, proof)
                if (response.isSuccessful) {
                    _isSubmitted.value = true
                } else {
                    _error.value = "فشل إغلاق الرحلة برمجياً"
                }
            } catch (e: Exception) {
                _error.value = "خطأ في الاتصال بالسيرفر"
            }
        }
    }
}

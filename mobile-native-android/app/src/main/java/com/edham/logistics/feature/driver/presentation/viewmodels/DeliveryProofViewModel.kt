package com.edham.logistics.feature.driver.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edham.logistics.core.network.api.DriverApi
import com.edham.logistics.core.utils.Resource
import com.edham.logistics.feature.driver.data.models.DeliveryProof
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeliveryProofViewModel @Inject constructor(
    private val api: DriverApi
) : ViewModel() {

    private val _submissionStatus = MutableSharedFlow<Resource<Unit>>()
    val submissionStatus = _submissionStatus.asSharedFlow()

    private val images = mutableListOf<String>()
    private var signature: String? = null

    fun addImage(base64: String) {
        if (images.size < 3) {
            images.add(base64)
        }
    }

    fun setSignature(base64: String) {
        signature = base64
    }

    fun submit(shipmentId: String, rating: Int, notes: String) {
        if (images.isEmpty()) {
            viewModelScope.launch { _submissionStatus.emit(Resource.Error("At least one photo is required")) }
            return
        }
        if (signature == null) {
            viewModelScope.launch { _submissionStatus.emit(Resource.Error("Signature is required")) }
            return
        }

        val proof = DeliveryProof(
            images = images,
            signature = signature!!,
            rating = rating,
            notes = notes
        )

        viewModelScope.launch {
            _submissionStatus.emit(Resource.Loading())
            try {
                val response = api.submitDeliveryProof(shipmentId, proof)
                if (response.isSuccessful) {
                    _submissionStatus.emit(Resource.Success(Unit))
                    // Trigger WebSocket event if needed (skipped for brevity unless I find WebSocketManager)
                } else {
                    _submissionStatus.emit(Resource.Error(response.message()))
                }
            } catch (e: Exception) {
                _submissionStatus.emit(Resource.Error(e.message ?: "Network error"))
            }
        }
    }
}

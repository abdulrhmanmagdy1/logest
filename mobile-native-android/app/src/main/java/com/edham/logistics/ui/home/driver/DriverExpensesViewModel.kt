package com.edham.logistics.ui.home.driver

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edham.logistics.core.network.api.DriverExpense
import com.edham.logistics.data.repository.DriverRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DriverExpensesViewModel @Inject constructor(
    private val repository: DriverRepository
) : ViewModel() {

    private val _expenses = MutableLiveData<List<com.edham.logistics.core.network.api.DriverExpense>>()
    val expenses: LiveData<List<com.edham.logistics.core.network.api.DriverExpense>> = _expenses

    private val _isSubmitted = MutableLiveData<Boolean>()
    val isSubmitted: LiveData<Boolean> = _isSubmitted

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun loadExpenses(driverId: String) {
        viewModelScope.launch {
            try {
                // Fetch from repository
            } catch (e: Exception) {
                _error.value = "فشل جلب سجل المصاريف"
            }
        }
    }

    fun submitExpense(driverId: String, tripId: String?, amount: Double, type: String, desc: String, url: String?) {
        viewModelScope.launch {
            try {
                val res = repository.submitExpense(driverId, tripId, amount, type, desc, url)
                if (res.isSuccessful) {
                    _isSubmitted.value = true
                }
            } catch (e: Exception) {
                _error.value = "فشل إرسال المصروف"
            }
        }
    }
}

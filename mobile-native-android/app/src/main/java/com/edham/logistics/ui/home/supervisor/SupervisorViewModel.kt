package com.edham.logistics.ui.home.supervisor

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edham.logistics.core.network.api.SupervisorApi
import com.edham.logistics.core.network.api.SupervisorStats
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SupervisorViewModel @Inject constructor(
    private val api: SupervisorApi
) : ViewModel() {

    private val _stats = MutableLiveData<SupervisorStats>()
    val stats: LiveData<SupervisorStats> = _stats

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun loadDashboardData() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = api.getStats()
                if (response.isSuccessful) {
                    _stats.value = response.body()?.data
                    _error.value = null
                } else {
                    _error.value = "خطأ في جلب البيانات من السيرفر"
                }
            } catch (e: Exception) {
                _error.value = "فشل الاتصال: تأكد من تشغيل السيرفر"
            } finally {
                _isLoading.value = false
            }
        }
    }
}

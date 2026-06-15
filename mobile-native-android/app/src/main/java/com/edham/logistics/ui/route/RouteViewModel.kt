package com.edham.logistics.ui.route

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edham.logistics.data.local.entity.RouteEntity
import com.edham.logistics.data.local.entity.RouteStopEntity
import com.edham.logistics.data.repository.RouteRepository
// import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
// import javax.inject.Inject

// @HiltViewModel
class RouteViewModel(
    private val repository: RouteRepository
) : ViewModel() {

    private val _routes = MutableStateFlow<List<RouteEntity>>(emptyList())
    val routes: StateFlow<List<RouteEntity>> = _routes

    fun loadRoutes() {

        viewModelScope.launch {
            _routes.value = repository.getAllRoutes()
        }
    }

    fun createRoute(
        route: RouteEntity,
        stops: List<RouteStopEntity>
    ) {

        viewModelScope.launch {
            repository.createRoute(route, stops)
            loadRoutes()
        }
    }
}

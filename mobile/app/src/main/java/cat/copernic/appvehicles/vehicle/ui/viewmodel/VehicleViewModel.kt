package cat.copernic.appvehicles.vehicle.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cat.copernic.appvehicles.model.Vehicle
import cat.copernic.appvehicles.vehicle.data.repository.VehicleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class VehicleViewModel(
    private val repository: VehicleRepository
) : ViewModel() {

    private val _vehicles = MutableStateFlow<List<Vehicle>>(emptyList())
    val vehicles: StateFlow<List<Vehicle>> = _vehicles

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun loadVehicles() {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.getVehicles()

            result.fold(
                onSuccess = { vehicleResponseList ->
                    _vehicles.value = vehicleResponseList.map { response ->
                        Vehicle(
                            id = response.matricula,
                            marca = response.marca,
                            model = response.model,
                            variant = response.variant,
                            preuHora = response.preuHora,
                            fotoBase64 = response.fotoBase64 // <-- ¡AQUÍ ESTÁ LA MAGIA!
                        )
                    }
                },
                onFailure = {}
            )
            _isLoading.value = false
        }
    }

    fun loadVehiclesDisponibles(inici: String, fi: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.getVehiclesDisponibles(inici, fi)

            result.fold(
                onSuccess = { vehicleResponseList ->
                    _vehicles.value = vehicleResponseList.map { response ->
                        Vehicle(
                            id = response.matricula,
                            marca = response.marca,
                            model = response.model,
                            variant = response.variant,
                            preuHora = response.preuHora,
                            fotoBase64 = response.fotoBase64 // <-- ¡Y AQUÍ TAMBIÉN!
                        )
                    }
                },
                onFailure = {}
            )
            _isLoading.value = false
        }
    }
}
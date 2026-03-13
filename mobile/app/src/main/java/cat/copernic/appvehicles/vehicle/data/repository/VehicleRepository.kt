package cat.copernic.appvehicles.vehicle.data.repository

import cat.copernic.appvehicles.model.VehicleResponse
import cat.copernic.appvehicles.vehicle.data.api.remote.VehicleApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class VehicleRepository(
    private val api: VehicleApiService
) {

    suspend fun getVehicles(): Result<List<VehicleResponse>> {

        return withContext(Dispatchers.IO) {
            try {

                val response = api.getVehicles()

                if (response.isSuccessful) {

                    val body = response.body()

                    if (body != null) {
                        Result.success(body)
                    } else {
                        Result.failure(Exception("Respuesta vacía del servidor"))
                    }

                } else {

                    val errorBody = response.errorBody()?.string()

                    val errorMessage = if (!errorBody.isNullOrEmpty()) {
                        errorBody
                    } else {
                        "Error al obtener vehículos: Código ${response.code()}"
                    }

                    Result.failure(Exception(errorMessage))
                }

            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun getVehiclesDisponibles(
        inici: String,
        fi: String
    ): Result<List<VehicleResponse>> {

        return withContext(Dispatchers.IO) {
            try {

                val response = api.getVehiclesDisponibles(inici, fi)

                if (response.isSuccessful) {

                    val body = response.body()

                    if (body != null) {
                        Result.success(body)
                    } else {
                        Result.failure(Exception("Respuesta vacía del servidor"))
                    }

                } else {

                    val errorBody = response.errorBody()?.string()

                    val errorMessage = if (!errorBody.isNullOrEmpty()) {
                        errorBody
                    } else {
                        "Error al obtener vehículos disponibles: Código ${response.code()}"
                    }

                    Result.failure(Exception(errorMessage))
                }

            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}
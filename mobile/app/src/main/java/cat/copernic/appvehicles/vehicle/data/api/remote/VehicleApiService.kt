package cat.copernic.appvehicles.vehicle.data.api.remote

import cat.copernic.appvehicles.model.VehicleResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface VehicleApiService {

    // Lista completa de vehículos
    @GET("api/vehicles")
    suspend fun getVehicles(): Response<List<VehicleResponse>>

    // Vehículos disponibles en rango de fechas
    @GET("api/vehicles/disponibles")
    suspend fun getVehiclesDisponibles(
        @Query("inici") inici: String,
        @Query("fi") fi: String
    ): Response<List<VehicleResponse>>

}
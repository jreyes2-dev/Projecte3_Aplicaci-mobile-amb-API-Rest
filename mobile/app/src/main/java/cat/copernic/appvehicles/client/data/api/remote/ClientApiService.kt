package cat.copernic.appvehicles.client.data.api.remote

import cat.copernic.appvehicles.client.data.model.ClientProfileDto
import cat.copernic.appvehicles.client.data.model.ClientUpdateRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface ClientApiService {

    // Cambiamos {dni} por {email}
    @GET("clients/{email}")
    suspend fun getClient(@Path("email") email: String): Response<ClientProfileDto>

    @PUT("clients/{email}")
    suspend fun updateClient(
        @Path("email") email: String,
        @Body request: ClientUpdateRequest
    ): Response<ClientProfileDto>
}
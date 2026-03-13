package cat.copernic.appvehicles.client.data.repository

import cat.copernic.appvehicles.client.data.api.remote.ClientApiService
import cat.copernic.appvehicles.client.data.model.ClientProfileDto
import cat.copernic.appvehicles.client.data.model.ClientUpdateRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

class ClientRepository(private val api: ClientApiService) {

    suspend fun getClient(dni: String): Response<ClientProfileDto> {
        return withContext(Dispatchers.IO) { api.getClient(dni) }
    }

    suspend fun updateClient(dni: String, request: ClientUpdateRequest): Response<ClientProfileDto> {
        return withContext(Dispatchers.IO) { api.updateClient(dni, request) }
    }
}
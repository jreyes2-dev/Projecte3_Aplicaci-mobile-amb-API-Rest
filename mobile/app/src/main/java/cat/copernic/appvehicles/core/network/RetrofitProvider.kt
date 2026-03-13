package cat.copernic.appvehicles.core.network

import cat.copernic.appvehicles.reserva.data.api.remote.ReservaApi
import cat.copernic.appvehicles.vehicle.data.api.remote.VehicleApiService
import cat.copernic.appvehicles.usuariAnonim.data.api.remote.AuthApiService

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitProvider {

    // PON AQUÍ TU IP ACTUAL (Terminadas con la barra / al final)
    private const val BASE_URL_SENSE_API = "http://192.168.1.210:8080/"
    private const val BASE_URL_AMB_API = "http://192.168.1.210:8080/api/"

    // Cliente OkHttp COMPARTIDO (Optimiza la batería y la red)
    private val client: OkHttpClient by lazy {
        val log = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
        OkHttpClient.Builder()
            .addInterceptor(log)
            .build()
    }

    // 1. Retrofit para Vehículos y Reservas (Raíz)
    // 1. Retrofit PRINCIPAL (Raíz) -> Le devolvemos el nombre original "retrofit" y lo hacemos público
    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL_SENSE_API)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // 2. Retrofit para Auth (Con /api/) -> Lo mantenemos para el login
    val retrofitApi: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL_AMB_API)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // --- LLISTA DE TOTES LES APIs DE L'APLICACIÓ ---

    val reservaApi: ReservaApi by lazy {
        retrofit.create(ReservaApi::class.java) // <-- Ahora usa "retrofit"
    }

    val vehicleApi: VehicleApiService by lazy {
        retrofit.create(VehicleApiService::class.java) // <-- Ahora usa "retrofit"
    }

    val authApi: AuthApiService by lazy {
        retrofitApi.create(AuthApiService::class.java) // <-- Usa el de la API
    }
}
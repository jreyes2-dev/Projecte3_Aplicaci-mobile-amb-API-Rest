package cat.copernic.appvehicles.core.navigation

// Archivo: core/navigation/AppRoutes.kt

sealed class AppRoutes(val route: String) {
    object Inici : AppRoutes("inici")
    object Reserves : AppRoutes("reserves")
    object Perfil : AppRoutes("perfil")

    object Vehicles : AppRoutes("vehicles")

    object Register : AppRoutes("register")

    object VehicleDetail : AppRoutes("vehicle_detail")
}
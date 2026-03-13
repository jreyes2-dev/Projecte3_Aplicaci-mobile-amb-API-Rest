package cat.copernic.appvehicles.model

import cat.copernic.appvehicles.vehicle.data.remote.VehicleDto

fun VehicleDto.toDomain(): Vehicle {
    return Vehicle(
        id = matricula,  // Nota: en tu DTO es matricula, en Vehicle es id
        marca = marca,
        model = model,
        variant = variant,
        preuHora = preuHora,
        fotoBase64 = fotoBase64  // NUEVO
    )
}
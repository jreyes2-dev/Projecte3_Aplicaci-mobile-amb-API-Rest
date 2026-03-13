package cat.copernic.appvehicles.core.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
// IMPORTANTE: Asegúrate de importar tu modelo real de Vehículo y no el Mock
import cat.copernic.appvehicles.model.Vehicle

@Composable
fun VehicleCard(vehicle: Vehicle, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // --- INICIO MAGIA DE LA FOTO ---
            // Asegúrate de que tu data class Vehicle tenga el campo "fotoBase64"
            val base64String = vehicle.fotoBase64
            val uriSimulada = base64String?.let { "data:image/jpeg;base64,$it" }
            val fotoCocheBitmap = rememberBase64Bitmap(imageUri = uriSimulada)

            if (fotoCocheBitmap != null) {
                // Pinta la foto real del coche si el backend nos la manda
                Image(
                    bitmap = fotoCocheBitmap,
                    contentDescription = "Foto de ${vehicle.marca}",
                    modifier = Modifier
                        .size(80.dp)
                        .clip(MaterialTheme.shapes.medium),
                    contentScale = ContentScale.Crop
                )
            } else {
                // Pinta el icono del coche gris si no hay foto
                Surface(
                    modifier = Modifier.size(80.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = MaterialTheme.shapes.medium
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                        Icon(
                            imageVector = Icons.Default.DirectionsCar,
                            contentDescription = "Sense imatge",
                            modifier = Modifier.size(40.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        )
                    }
                }
            }
            // --- FIN MAGIA DE LA FOTO ---

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${vehicle.marca} ${vehicle.model}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = vehicle.variant,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Text(
                text = "${vehicle.preuHora}€/h",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
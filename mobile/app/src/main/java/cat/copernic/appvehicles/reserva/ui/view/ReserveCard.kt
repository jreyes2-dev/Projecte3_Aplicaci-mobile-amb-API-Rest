package cat.copernic.appvehicles.reserva.ui.view

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cat.copernic.appvehicles.R
import cat.copernic.appvehicles.core.composables.rememberBase64Bitmap
import cat.copernic.appvehicles.model.ReservaResponse

/**
 * Component visual reutilitzable (Composable) que representa una targeta de resum d'una reserva.
 * Mostra de manera compacta la informació essencial: miniatura del vehicle, codi de reserva,
 * dates d'inici i finalització, estat actual i import total de l'operació.
 *
 * @param reserve Objecte de transferència de dades (ReservaResponse) procedent del backend.
 * @param onClick Funció callback executada quan l'usuari interacciona (fa clic) sobre la targeta,
 * utilitzada generalment per navegar a la vista de detall de la reserva.
 */
@Composable
fun ReserveCard(
    reserve: ReservaResponse,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // 1. Processament i renderització de la miniatura de la imatge
            val base64String = reserve.vehicleFotoBase64
            val uriSimulada = base64String?.let { "data:image/jpeg;base64,$it" }
            val fotoCocheBitmap = rememberBase64Bitmap(imageUri = uriSimulada)

            if (fotoCocheBitmap != null) {
                Image(
                    bitmap = fotoCocheBitmap,
                    contentDescription = stringResource(R.string.vehicle_photo_description),
                    modifier = Modifier
                        .size(64.dp)
                        .clip(MaterialTheme.shapes.small),
                    contentScale = ContentScale.Crop
                )
            } else {
                // Placeholder visual en cas que el registre no disposi de fotografia (Base64 corrupte o nul)
                Surface(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(MaterialTheme.shapes.small),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                        Icon(
                            imageVector = Icons.Default.DirectionsCar,
                            contentDescription = null, // L'icona és purament decorativa
                            modifier = Modifier.size(32.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // 2. Bloc central d'informació textual de la reserva
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // Identificador únic de la reserva
                Text(
                    text = stringResource(R.string.reservation_code, "RES-${reserve.idReserva}"),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Rang de dates sol·licitades
                Text(
                    text = stringResource(
                        R.string.reservation_date_range,
                        reserve.dataInici,
                        reserve.dataFi
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(8.dp))

                // 3. Fila inferior: Estat operatiu i balanç econòmic
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${stringResource(R.string.reservation_status_label)}: ${reserve.estat}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Text(
                        text = "${reserve.importTotal} €",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
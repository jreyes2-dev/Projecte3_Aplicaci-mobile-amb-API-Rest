package cat.copernic.appvehicles.vehicle.ui.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.ui.res.stringResource
import cat.copernic.appvehicles.R
import cat.copernic.appvehicles.vehicle.ui.viewmodel.VehicleViewModel
import androidx.compose.foundation.Image
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.material.icons.filled.DirectionsCar
import cat.copernic.appvehicles.core.composables.rememberBase64Bitmap

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehicleDetailScreen(
    matricula: String,
    viewModel: VehicleViewModel,
    onBackClick: () -> Unit = {},
    onReservarClick: () -> Unit = {}
) {

    // Cerquem el vehicle real a la llista
    val vehicles by viewModel.vehicles.collectAsState()

    // LA SOLUCIÓ A L'ERROR: Fem .trim() per netejar els espais invisibles de la base de dades
    val vehicleReal = vehicles.find { it.id.trim().equals(matricula.trim(), ignoreCase = true) }

    val potencia = "283 HP"
    val color = "White"
    val limitKm = "300 km/day"
    val minDies = "1 day"
    val maxDies = "30 days"
    val fianca = "500€"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.vehicle_details)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = stringResource(R.string.go_back)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->

        if (vehicleReal == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                // He afegit això per poder investigar si hi hagués un altre error
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Carregant o vehicle no trobat...")
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(stringResource(R.string.vehicle_searching_plate, matricula), color = MaterialTheme.colorScheme.error)
                    Text(stringResource(R.string.vehicles_in_memory, vehicles.size), color = MaterialTheme.colorScheme.error)
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {

                // --- INICIO IMAGEN DEL VEHÍCULO ---
                val base64String = vehicleReal.fotoBase64
                val uriSimulada = base64String?.let { "data:image/jpeg;base64,$it" }
                val fotoCocheBitmap = rememberBase64Bitmap(imageUri = uriSimulada)

                if (fotoCocheBitmap != null) {
                    // Si la foto llega bien, la ponemos en grande
                    Image(
                        bitmap = fotoCocheBitmap,
                        contentDescription = "Foto de ${vehicleReal.marca} ${vehicleReal.model}",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                            .clip(MaterialTheme.shapes.medium),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    // Si no tiene foto, dejamos el recuadro gris con un icono de coche
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                            Icon(
                                imageVector = Icons.Default.DirectionsCar,
                                contentDescription = "Sense imatge",
                                modifier = Modifier.size(80.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                            )
                        }
                    }
                }
                // --- FIN IMAGEN DEL VEHÍCULO ---

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "${vehicleReal.marca} ${vehicleReal.model}",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = stringResource(
                        R.string.vehicle_plate_variant,
                        vehicleReal.id.trim(),
                        vehicleReal.variant
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "${vehicleReal.preuHora}€/hour",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(24.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(stringResource(R.string.power, potencia))
                        Text(stringResource(R.string.color, color))
                        Text(stringResource(R.string.mileage_limit, limitKm))
                        Text(stringResource(R.string.minimum_rental_days, minDies))
                        Text(stringResource(R.string.maximum_rental_days, maxDies))
                        Text(stringResource(R.string.standard_deposit, fianca))
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { onReservarClick() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.book_vehicle))
                }
            }
        }
    }
}

// Preview només per defecte
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun VehicleDetailScreenPreview() {
    MaterialTheme {
        // En preview no passem res per no complicar-ho
    }
}
package cat.copernic.appvehicles.usuariAnonim.ui.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import cat.copernic.appvehicles.ui.theme.AppVehiclesTheme

// IMPORT DEL MOCK CORRECTE I DEL VIEWMODEL
import cat.copernic.appvehicles.model.VehicleMock
import cat.copernic.appvehicles.vehicle.ui.viewmodel.VehicleViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: VehicleViewModel? = null, // ARA ACCEPTA EL VIEWMODEL
    onVehicleClick: (String) -> Unit     // ARA ACCEPTA LA MATRÍCULA (String)
) {
    // Obtenim les dades reals del teu Spring Boot
    val vehiclesReal = viewModel?.vehicles?.collectAsState()?.value ?: emptyList()
    val isLoading = viewModel?.isLoading?.collectAsState()?.value ?: false

    // Cridem a la base de dades a l'obrir la pantalla
    LaunchedEffect(viewModel) {
        viewModel?.loadVehicles()
    }

    // Transformem els vehicles reals per mostrar-los a la UI
    val llistaVehicles = vehiclesReal.map { v ->
        VehicleMock(
            id = v.id, // Guardem la matrícula real
            marca = v.marca,
            model = v.model,
            variant = v.variant,
            preuHora = v.preuHora
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("AppVehicles") }, // RN30
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {

            // RF56: Sección visual para el filtro de fechas
            OutlinedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.DateRange, contentDescription = "Filtre dates")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Seleccionar dates de disponibilitat...",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            // MENTRE CARREGA
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            // SI LA BASE DE DADES ESTÀ BUIDA
            else if (llistaVehicles.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hi ha vehicles a la BD.", color = MaterialTheme.colorScheme.error)
                }
            }
            // LLISTA DE VEHICLES REALS
            else {
                LazyColumn {
                    items(llistaVehicles) { vehicle ->
                        // Creem la targeta directament aquí per evitar problemes d'imports
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                                .clickable { onVehicleClick(vehicle.id) }, // PASSEM LA MATRÍCULA
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("${vehicle.marca} ${vehicle.model}", style = MaterialTheme.typography.titleMedium)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(vehicle.variant, style = MaterialTheme.typography.bodyMedium)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("${vehicle.preuHora} €/h", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun HomeScreenPreview() {
    AppVehiclesTheme {
        HomeScreen(onVehicleClick = {})
    }
}
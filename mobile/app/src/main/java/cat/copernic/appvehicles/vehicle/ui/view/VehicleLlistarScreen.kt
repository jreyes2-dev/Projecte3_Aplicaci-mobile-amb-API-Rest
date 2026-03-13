package cat.copernic.appvehicles.vehicle.ui.view

import android.app.DatePickerDialog
import android.widget.DatePicker
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cat.copernic.appvehicles.R
import cat.copernic.appvehicles.model.Vehicle
import cat.copernic.appvehicles.vehicle.ui.viewmodel.VehicleViewModel
import cat.copernic.appvehicles.core.composables.rememberBase64Bitmap
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehicleLlistarScreen(
    onVehicleClick: (String) -> Unit,
    onLoginClick: () -> Unit = {},
    onRegisterClick: () -> Unit = {},
    viewModel: VehicleViewModel = viewModel()
) {

    var fechaInicio by remember { mutableStateOf("") }
    var fechaFin by remember { mutableStateOf("") }

    var ordenAscendente by remember { mutableStateOf(true) }
    var expandedPrice by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    val sessionManager = remember { cat.copernic.appvehicles.core.auth.SessionManager(context) }
    val userEmail by sessionManager.userEmailFlow.collectAsState(initial = null)
    val isUserLoggedIn = !userEmail.isNullOrBlank()

    val vehicles by viewModel.vehicles.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadVehicles()
    }

    val vehiculosOrdenados =
        if (ordenAscendente) vehicles.sortedBy { it.preuHora }
        else vehicles.sortedByDescending { it.preuHora }

    val dateButtonText =
        if (fechaInicio.isNotBlank() && fechaFin.isNotBlank()) {
            "${fechaInicio.substring(5)} to ${fechaFin.substring(5)}"
        } else {
            stringResource(R.string.select_dates)
        }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "MobileCat",
                        fontWeight = FontWeight.ExtraBold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                actions = {

                    if (!isUserLoggedIn) {

                        TextButton(onClick = onLoginClick) {
                            Text(stringResource(R.string.login))
                        }

                        Button(
                            onClick = onRegisterClick,
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Text(stringResource(R.string.register))
                        }

                    } else {

                        Surface(
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.padding(end = 12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {

                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "User",
                                    modifier = Modifier.size(16.dp)
                                )

                                Spacer(modifier = Modifier.width(6.dp))

                                Text(
                                    text = userEmail ?: "",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {

            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
                return@Column
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                OutlinedButton(
                    modifier = Modifier
                        .weight(1.2f)
                        .height(56.dp),
                    onClick = {

                        DatePickerDialog(
                            context,
                            { _: DatePicker, year: Int, month: Int, day: Int ->

                                fechaInicio = "%04d-%02d-%02d".format(year, month + 1, day)

                                DatePickerDialog(
                                    context,
                                    { _: DatePicker, year2: Int, month2: Int, day2: Int ->

                                        fechaFin = "%04d-%02d-%02d".format(year2, month2 + 1, day2)

                                        if (fechaInicio.isNotBlank() && fechaFin.isNotBlank()) {

                                            val start = java.time.LocalDate.parse(fechaInicio)
                                            val end = java.time.LocalDate.parse(fechaFin)

                                            val days = java.time.temporal.ChronoUnit.DAYS.between(start, end)

                                            if (days < 2 || days > 15) {

                                                Toast.makeText(
                                                    context,
                                                    "Reservation must be between 2 and 15 days",
                                                    Toast.LENGTH_LONG
                                                ).show()

                                            } else {

                                                viewModel.loadVehiclesDisponibles(
                                                    fechaInicio,
                                                    fechaFin
                                                )
                                            }
                                        }

                                    },
                                    calendar.get(Calendar.YEAR),
                                    calendar.get(Calendar.MONTH),
                                    calendar.get(Calendar.DAY_OF_MONTH)
                                ).show()
                            },
                            calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH)
                        ).show()
                    }
                ) {

                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Dates"
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(dateButtonText, maxLines = 1)
                }

                ExposedDropdownMenuBox(
                    expanded = expandedPrice,
                    onExpandedChange = { expandedPrice = !expandedPrice },
                    modifier = Modifier.weight(1f)
                ) {

                    OutlinedTextField(
                        value = if (ordenAscendente)
                            stringResource(R.string.lowest_price)
                        else
                            stringResource(R.string.highest_price),

                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expandedPrice)
                        },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                            .height(56.dp)
                    )

                    ExposedDropdownMenu(
                        expanded = expandedPrice,
                        onDismissRequest = { expandedPrice = false }
                    ) {

                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.lowest_price)) },
                            onClick = {
                                ordenAscendente = true
                                expandedPrice = false
                            }
                        )

                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.highest_price)) },
                            onClick = {
                                ordenAscendente = false
                                expandedPrice = false
                            }
                        )
                    }
                }

                IconButton(
                    onClick = {

                        fechaInicio = ""
                        fechaFin = ""
                        ordenAscendente = true

                        viewModel.loadVehicles()

                        Toast.makeText(
                            context,
                            "Filters cleared",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                ) {

                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Clear filters"
                    )
                }
            }

            if (vehiculosOrdenados.isEmpty()) {

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {

                        Icon(
                            imageVector = Icons.Default.DirectionsCar,
                            contentDescription = "No vehicles",
                            modifier = Modifier.size(64.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text("No vehicles available for these dates")
                    }
                }

            } else {

                LazyColumn {

                    items(vehiculosOrdenados) { vehicle ->

                        VehicleCard(
                            vehicle = vehicle,
                            onClick = { onVehicleClick(vehicle.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun VehicleCard(
    vehicle: Vehicle,
    onClick: () -> Unit
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        onClick = onClick
    ) {

        Column {

            val base64String = vehicle.fotoBase64
            val uriSimulada = base64String?.let { "data:image/jpeg;base64,$it" }
            val fotoCocheBitmap = rememberBase64Bitmap(uriSimulada)

            if (fotoCocheBitmap != null) {

                Image(
                    bitmap = fotoCocheBitmap,
                    contentDescription = "Foto de ${vehicle.marca} ${vehicle.model}",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentScale = ContentScale.Crop
                )

            } else {

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {

                        Icon(
                            imageVector = Icons.Default.DirectionsCar,
                            contentDescription = "No image",
                            modifier = Modifier.size(64.dp)
                        )
                    }
                }
            }

            Column(
                modifier = Modifier.padding(16.dp)
            ) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        text = "${vehicle.marca} ${vehicle.model}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(8.dp)
                    ) {

                        Text(
                            text = "${vehicle.preuHora} €/h",
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = vehicle.variant,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

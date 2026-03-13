package cat.copernic.appvehicles.reserva.ui.view

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cat.copernic.appvehicles.R
import cat.copernic.appvehicles.model.CreateReservaRequest
import cat.copernic.appvehicles.reserva.viewmodel.ReservaViewModel
import cat.copernic.appvehicles.vehicle.ui.viewmodel.VehicleViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit

/**
 * Component modal que renderitza un calendari interactiu seguint els estàndards de Material 3.
 *
 * @param onDateSelected Callback executat quan l'usuari confirma la selecció. Retorna la data en format "YYYY-MM-DD".
 * @param onDismiss Callback executat si l'usuari cancel·la o tanca el diàleg.
 */
@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DatePickerModal(onDateSelected: (String) -> Unit, onDismiss: () -> Unit) {
    val datePickerState = rememberDatePickerState()
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                datePickerState.selectedDateMillis?.let { millis ->
                    val date = Instant.ofEpochMilli(millis).atZone(ZoneId.of("UTC")).toLocalDate()
                    onDateSelected(date.toString())
                }
                onDismiss()
            }) { Text(stringResource(R.string.ok)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}

/**
 * Pantalla principal per a la creació d'una reserva de vehicle.
 * Gestiona l'entrada de dates, calcula els costos associats de forma dinàmica i
 * processa la sol·licitud de reserva mitjançant comunicació amb el backend.
 *
 * @param matriculaFixa Identificador (matrícula) del vehicle prèviament seleccionat.
 * @param iniciFix Data d'inici heretada de la pantalla de filtres per mantenir l'estat.
 * @param fiFix Data de finalització heretada de la pantalla de filtres per mantenir l'estat.
 * @param onNavigateBack Callback per tornar a la pantalla anterior.
 * @param viewModel ViewModel responsable de la lògica de negoci de les reserves.
 * @param vehicleViewModel ViewModel responsable de proveir les dades del catàleg de vehicles.
 * @param userEmail Correu electrònic de l'usuari autenticat; requerit per a l'autorització.
 * @param onReservaCreada Callback d'èxit que rep l'ID de la nova reserva per redirigir al detall.
 */
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateReservationScreen(
    matriculaFixa: String = "",
    iniciFix: String = "",
    fiFix: String = "",
    onNavigateBack: () -> Unit = {},
    viewModel: ReservaViewModel? = null,
    vehicleViewModel: VehicleViewModel? = null,
    userEmail: String = "",
    onReservaCreada: (Long) -> Unit = {}
) {
    // 1. Obtenció de dades del vehicle seleccionat
    val vehiclesReals = vehicleViewModel?.vehicles?.collectAsState()?.value ?: emptyList()
    val vehicleSeleccionat = vehiclesReals.find { it.id == matriculaFixa }

    val preuHora = vehicleSeleccionat?.preuHora ?: 0.0
    val nomVehicle = if (vehicleSeleccionat != null) "${vehicleSeleccionat.marca} ${vehicleSeleccionat.model}" else matriculaFixa

    // 2. Definició de l'estat de la interfície d'usuari
    var startDate by remember { mutableStateOf(iniciFix) }
    var endDate by remember { mutableStateOf(fiFix) }
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }

    var costCalculat by remember { mutableStateOf(0.0) }
    val fiancaFixa = 300.0 // Constants de negoci predeterminades

    val isLoggedIn = userEmail.isNotBlank()

    // Constants de text precarregades per ser utilitzades de forma segura en l'àmbit de la corrutina
    val selectDatesError = stringResource(R.string.error_dates_required)
    val invalidDatesError = stringResource(R.string.error_invalid_date_range)

    // 3. Reactor de càlcul dinàmic del pressupost
    LaunchedEffect(startDate, endDate) {
        try {
            if (startDate.isNotBlank() && endDate.isNotBlank()) {
                val inici = LocalDate.parse(startDate)
                val fi = LocalDate.parse(endDate)
                var dies = ChronoUnit.DAYS.between(inici, fi)
                // Es comptabilitza un mínim d'1 dia per defecte segons la lògica de negoci
                if (dies <= 0) dies = 1L
                costCalculat = dies * 24 * preuHora
            } else {
                costCalculat = 0.0
            }
        } catch (e: Exception) {
            costCalculat = 0.0
        }
    }

    // 4. Observadors de l'estat de la petició asíncrona (Backend)
    val loading = viewModel?.loading?.collectAsState()?.value ?: false
    val creationResult = viewModel?.creationResult?.collectAsState()?.value
    var errorMsg by remember { mutableStateOf<String?>(null) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var reservaId by remember { mutableStateOf<Long?>(null) }
    var importConfirmat by remember { mutableStateOf("0.00") }

    LaunchedEffect(creationResult) {
        creationResult?.fold(
            onSuccess = { reserva ->
                errorMsg = null
                reservaId = reserva.idReserva
                importConfirmat = reserva.importTotal
                showSuccessDialog = true
            },
            onFailure = { e ->
                // Es mostra l'error tècnic emès pel backend (ex: "Vehicle no disponible en aquestes dates")
                errorMsg = e.message
            }
        )
    }

    // 5. Gestió de Modals i Diàlegs de confirmació
    if (showStartDatePicker) DatePickerModal(onDateSelected = { startDate = it }, onDismiss = { showStartDatePicker = false })
    if (showEndDatePicker) DatePickerModal(onDateSelected = { endDate = it }, onDismiss = { showEndDatePicker = false })

    if (showSuccessDialog && reservaId != null) {
        AlertDialog(
            onDismissRequest = { },
            title = { Text(stringResource(R.string.reservation_created_title)) },
            text = {
                Column {
                    Text(stringResource(R.string.reservation_code, reservaId!!))
                    Spacer(Modifier.height(8.dp))
                    Text(stringResource(R.string.reservation_amount, importConfirmat))
                }
            },
            confirmButton = {
                Button(onClick = {
                    showSuccessDialog = false
                    viewModel?.clearCreationResult()
                    onReservaCreada(reservaId!!)
                }) { Text(stringResource(R.string.view_detail)) }
            },
            dismissButton = {
                TextButton(onClick = {
                    showSuccessDialog = false
                    viewModel?.clearCreationResult()
                    onNavigateBack()
                }) { Text(stringResource(R.string.close)) }
            }
        )
    }

    // 6. Construcció principal de la interfície gràfica (UI)
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.create_reservation_title)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {

            // Bloc de prevenció per a usuaris no autenticats
            if (!isLoggedIn) {
                OutlinedCard(
                    colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.Info, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.login_required_desc),
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Secció: Selecció de dates
            Text(stringResource(R.string.select_dates), style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(modifier = Modifier.weight(1f).clickable { showStartDatePicker = true }) {
                    OutlinedTextField(
                        value = startDate,
                        onValueChange = {},
                        enabled = false,
                        label = { Text(stringResource(R.string.start_date)) },
                        trailingIcon = { Icon(Icons.Default.CalendarMonth, null) },
                        colors = OutlinedTextFieldDefaults.colors(disabledTextColor = MaterialTheme.colorScheme.onSurface)
                    )
                }
                Box(modifier = Modifier.weight(1f).clickable { showEndDatePicker = true }) {
                    OutlinedTextField(
                        value = endDate,
                        onValueChange = {},
                        enabled = false,
                        label = { Text(stringResource(R.string.end_date)) },
                        trailingIcon = { Icon(Icons.Default.CalendarMonth, null) },
                        colors = OutlinedTextFieldDefaults.colors(disabledTextColor = MaterialTheme.colorScheme.onSurface)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Secció: Dades del vehicle
            Text(stringResource(R.string.reservation_vehicle), style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = nomVehicle,
                onValueChange = {},
                readOnly = true,
                enabled = false,
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledBorderColor = MaterialTheme.colorScheme.outline
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Secció: Resum econòmic
            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(stringResource(R.string.cost_summary), fontWeight = FontWeight.Bold)
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    CostRow(stringResource(R.string.rental_cost), String.format("%.2f €", costCalculat))
                    CostRow(stringResource(R.string.deposit), String.format("%.2f €", fiancaFixa))
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    CostRow(stringResource(R.string.total_to_pay), String.format("%.2f €", costCalculat + fiancaFixa))
                }
            }

            // Gestió d'errors visuals
            if (errorMsg != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(errorMsg!!, color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.SemiBold)
            }

            Spacer(modifier = Modifier.weight(1f))

            // Acció: Confirmació i enviament al backend
            Button(
                onClick = {
                    errorMsg = null

                    // Validacions prèvies en el client
                    if (startDate.isBlank() || endDate.isBlank()) {
                        errorMsg = selectDatesError
                        return@Button
                    }

                    val i = LocalDate.parse(startDate)
                    val f = LocalDate.parse(endDate)

                    if (i.isAfter(f)) {
                        errorMsg = invalidDatesError
                        return@Button
                    }

                    // Petició de creació de reserva
                    viewModel?.crearReserva(CreateReservaRequest(userEmail, matriculaFixa, startDate, endDate, userEmail))
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                enabled = !loading && isLoggedIn
            ) {
                if (loading) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                } else {
                    Text(stringResource(R.string.confirm_reservation))
                }
            }
        }
    }
}

/**
 * Component estructural per facilitar la presentació semàntica de parells "Etiqueta-Valor"
 * orientats a imports econòmics.
 * * @param label Text descriptiu a l'esquerra (ex: "Cost de lloguer").
 * @param value Valor numèric o text a la dreta (ex: "150.00 €").
 */
@Composable
fun CostRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label)
        Text(value, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
    }
}
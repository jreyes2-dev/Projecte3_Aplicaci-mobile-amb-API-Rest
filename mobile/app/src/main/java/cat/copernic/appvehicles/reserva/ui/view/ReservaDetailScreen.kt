package cat.copernic.appvehicles.reserva.ui.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cat.copernic.appvehicles.R
import cat.copernic.appvehicles.core.composables.rememberBase64Bitmap
import cat.copernic.appvehicles.reserva.viewmodel.ReservaViewModel

/**
 * Component visual que mostra els detalls complets d'una reserva específica.
 * Proporciona el desglossament de costos, les dates, l'estat actual i permet
 * executar l'acció de cancel·lació si les regles de negoci ho permeten.
 *
 * @param reservaId Identificador únic de la reserva a la base de dades.
 * @param viewModel Instància del ViewModel per gestionar l'estat i les crides a l'API.
 * @param onNavigateBack Funció de retorn per tornar a la pantalla anterior en la pila de navegació.
 * @param userEmail Correu de l'usuari actualment autenticat, utilitzat per seguretat en l'anul·lació.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservationDetailScreen(
    reservaId: Long,
    viewModel: ReservaViewModel = viewModel(),
    onNavigateBack: () -> Unit,
    userEmail: String
) {
    // 1. Observació reactiva dels estats
    val reserva by viewModel.reservaDetail.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val cancelResult by viewModel.cancelResult.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    var showConfirmDialog by remember { mutableStateOf(false) }

    // Constants de text precarregades per ser segures dins de corrutines
    val errorCancelPrefix = stringResource(R.string.error_cancel_prefix)
    val statusActive = stringResource(R.string.status_active)
    val statusCancelled = stringResource(R.string.status_cancelled)
    val statusFinished = stringResource(R.string.status_finished)

    // 2. Càrrega inicial de dades
    LaunchedEffect(reservaId) {
        if (reservaId != 0L) {
            viewModel.loadReservaDetalle(reservaId)
        }
    }

    // 3. Gestió asíncrona del resultat de la cancel·lació
    LaunchedEffect(cancelResult) {
        cancelResult?.onSuccess {
            snackbarHostState.showSnackbar(it.message)
            viewModel.clearCancelResult()
        }
        cancelResult?.onFailure { exception ->
            snackbarHostState.showSnackbar("$errorCancelPrefix: ${exception.message}")
            viewModel.clearCancelResult()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.reservation_detail_title)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->

        // 4. Renderització condicional segons l'estat de càrrega
        if (loading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (reserva == null) {
            // Pantalla d'error si la reserva no es pot obtenir
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(Icons.Rounded.Warning, contentDescription = stringResource(R.string.error_icon), modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.error)
                Spacer(modifier = Modifier.height(16.dp))
                Text(stringResource(R.string.error_generic_title), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.error_load_reservation_details),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            // Pantalla de detalls un cop tenim les dades correctes
            reserva?.let { dadesReserva ->

                // Format i càlcul de dades econòmiques
                val estatReserva = dadesReserva.estat ?: "ACTIVA"
                val fiancaDouble = dadesReserva.fiancaPagada.toDoubleOrNull() ?: 0.0
                val importDouble = dadesReserva.importTotal.toDoubleOrNull() ?: 0.0
                val totalSumat = fiancaDouble + importDouble

                // Processament de la imatge
                val base64String = dadesReserva.vehicleFotoBase64
                val uriSimulada = base64String?.let { "data:image/jpeg;base64,$it" }
                val fotoCocheBitmap = rememberBase64Bitmap(imageUri = uriSimulada)

                // Traducció de l'estat per a la UI
                val displayStatus = when (estatReserva.uppercase()) {
                    "ACTIVA" -> statusActive
                    "CANCELADA", "CANCEL·LADA" -> statusCancelled
                    "FINALITZADA", "FINALIZADA" -> statusFinished
                    else -> estatReserva
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(paddingValues)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    // Capçalera: Imatge del vehicle
                    if (fotoCocheBitmap != null) {
                        Image(
                            bitmap = fotoCocheBitmap,
                            contentDescription = stringResource(R.string.vehicle_photo_description),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clip(RoundedCornerShape(16.dp)),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clip(RoundedCornerShape(16.dp)),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                                Icon(
                                    imageVector = Icons.Default.DirectionsCar,
                                    contentDescription = stringResource(R.string.no_image),
                                    modifier = Modifier.size(80.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Cos: Targeta d'informació principal
                    ElevatedCard(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = stringResource(R.string.reservation_code, "RES-${dadesReserva.idReserva}"),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )

                                Surface(
                                    color = if (estatReserva == "ACTIVA") MaterialTheme.colorScheme.primaryContainer
                                    else if (estatReserva == "CANCELADA") MaterialTheme.colorScheme.errorContainer
                                    else MaterialTheme.colorScheme.secondaryContainer,
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = displayStatus,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.labelMedium
                                    )
                                }
                            }

                            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                            DetailRow(label = stringResource(R.string.reservation_vehicle), value = dadesReserva.vehicleMatricula)
                            DetailRow(label = stringResource(R.string.start_date), value = dadesReserva.dataInici)
                            DetailRow(label = stringResource(R.string.end_date), value = dadesReserva.dataFi)

                            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                            DetailRow(label = stringResource(R.string.deposit), value = "${dadesReserva.fiancaPagada} €")
                            DetailRow(label = stringResource(R.string.rental_cost), value = "${dadesReserva.importTotal} €")

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = stringResource(R.string.reservation_total, String.format("%.2f €", totalSumat)),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier.align(Alignment.End)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // 5. Bloc inferior: Accions de l'usuari segons l'estat de la reserva
                    if (estatReserva == "ACTIVA") {
                        Button(
                            onClick = { showConfirmDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                        ) {
                            Text(text = stringResource(R.string.cancel_reservation), color = MaterialTheme.colorScheme.onError)
                        }
                    } else if (estatReserva == "CANCELADA") {
                        // Resum informatiu simplificat post-cancel·lació
                        OutlinedCard(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Rounded.Email, contentDescription = stringResource(R.string.notification), tint = MaterialTheme.colorScheme.error)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = stringResource(R.string.cancellation_notification_title),
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                                Spacer(modifier = Modifier.height(12.dp))

                                val emailVehicle = stringResource(R.string.email_body_cancelled_vehicle, dadesReserva.vehicleMatricula)
                                val emailRefund = stringResource(R.string.email_body_refund, String.format("%.2f", totalSumat))

                                Text(
                                    text = "$emailVehicle\n$emailRefund",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    } else {
                        Text(
                            text = stringResource(R.string.cannot_cancel_started_finished),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }

    // 6. Diàleg de confirmació de seguretat abans de procedir a la cancel·lació
    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text(stringResource(R.string.cancel_reservation)) },
            text = { Text(stringResource(R.string.confirm_cancel_question)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showConfirmDialog = false
                        viewModel.cancelReserva(reservaId, userEmail)
                    }
                ) {
                    Text(stringResource(R.string.ok))
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) { Text(stringResource(R.string.cancel)) }
            }
        )
    }
}

/**
 * Component estructural per facilitar la presentació de parells "Etiqueta - Valor".
 *
 * @param label El text descriptiu a l'esquerra.
 * @param value El valor associat alineat a la dreta i remarcat.
 */
@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label)
        Text(value, fontWeight = FontWeight.SemiBold)
    }
}
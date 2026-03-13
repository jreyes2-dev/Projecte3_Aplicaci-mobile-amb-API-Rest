package cat.copernic.appvehicles.reserva.ui.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cat.copernic.appvehicles.R
import cat.copernic.appvehicles.model.ReservaResponse
import cat.copernic.appvehicles.reserva.viewmodel.ReservaViewModel

/**
 * Pantalla principal per llistar l'historial de reserves del client autenticat.
 * Implementa gestió del cicle de vida per refrescar dades automàticament, control
 * d'accés per a usuaris anònims i tractament d'errors de xarxa per assegurar l'estabilitat visual.
 *
 * @param userEmail Adreça de correu de l'usuari actiu (injectada per a les consultes a l'API).
 * @param viewModel Instància del ViewModel per obtenir les llistes de dades i gestionar l'estat remot.
 * @param onBackClick Callback executat en prémer el botó de retorn a la barra de navegació superior.
 * @param onReservaSelected Callback executat en seleccionar una targeta de reserva per veure'n el detall.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReserveListScreen(
    userEmail: String,
    viewModel: ReservaViewModel,
    onBackClick: () -> Unit = {},
    onReservaSelected: (Long) -> Unit = {}
) {
    val isPreview = LocalInspectionMode.current

    // 1. Observació reactiva dels estats estructurals del ViewModel
    val loading by viewModel.loading.collectAsState()
    val reserves by viewModel.reserves.collectAsState()
    val errorMsg by viewModel.errorMsg.collectAsState()

    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current

    // 2. Gestió del cicle de vida: Refresc automàtic en retornar a la pantalla (ON_RESUME)
    DisposableEffect(lifecycleOwner, userEmail) {
        val observer = androidx.lifecycle.LifecycleEventObserver { _, event ->
            if (event == androidx.lifecycle.Lifecycle.Event.ON_RESUME && !isPreview) {
                if (userEmail.isNotBlank()) {
                    viewModel.load(userEmail)
                } else {
                    viewModel.clearReserves()
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    // 3. Internacionalització de la lògica de negoci (Estats de reserva)
    val statusActive = stringResource(R.string.status_active)
    val statusCancelled = stringResource(R.string.status_cancelled)
    val statusFinished = stringResource(R.string.status_finished)

    fun localizeStatus(raw: String): String {
        return when (raw.trim().uppercase()) {
            "ACTIVA", "ACTIVE" -> statusActive
            "CANCELADA", "CANCELLED", "CANCELED", "CANCEL·LADA" -> statusCancelled
            "FINALITZADA", "FINALIZADA", "FINISHED" -> statusFinished
            else -> raw
        }
    }

    // 4. Preparació de la col·lecció de dades (Traducció dinàmica d'estats per a la UI)
    val listToShow: List<ReservaResponse> = if (isPreview || userEmail.isBlank()) {
        emptyList()
    } else {
        // Mapegem l'entitat real i en modifiquem només el camp "estat" per ser traduït
        reserves.map { reserva ->
            reserva.copy(estat = localizeStatus(reserva.estat ?: "ACTIVA"))
        }
    }

    // 5. Construcció de la interfície gràfica (Scaffold principal)
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.reservations_title)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            if (!isPreview && userEmail.isNotBlank()) viewModel.toggleOrder(userEmail)
                        }
                    ) {
                        Icon(Icons.Default.SwapVert, contentDescription = stringResource(R.string.sort_list))
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {

            // Lògica de renderització condicional centralitzada
            if (!isPreview && loading) {
                // Estat 1: Transició de càrrega
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))

            } else if (userEmail.isBlank()) {
                // Estat 2: Manca de sessió activa (Control d'accés)
                Column(
                    modifier = Modifier.align(Alignment.Center).padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Rounded.Info, contentDescription = null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = stringResource(R.string.login_required_title),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.login_required_desc),
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

            } else if (errorMsg != null) {
                // Estat 3: Fallada operativa de xarxa o base de dades
                Column(
                    modifier = Modifier.align(Alignment.Center).padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Rounded.Warning, contentDescription = "Error_Icon", modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(stringResource(R.string.error_generic_title), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "${stringResource(R.string.error_load_reservations)}\n$errorMsg",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

            } else if (listToShow.isEmpty()) {
                // Estat 4: Safata de reserves buida per a l'usuari validat
                Text(
                    text = stringResource(R.string.no_reservations_found),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.align(Alignment.Center)
                )

            } else {
                // Estat 5: Llistat iteratiu de components gràfics (Targetes)
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 8.dp, horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(items = listToShow, key = { it.idReserva }) { reserva ->
                        ReserveCard(
                            reserve = reserva,
                            onClick = { onReservaSelected(reserva.idReserva) }
                        )
                    }
                }
            }
        }
    }
}
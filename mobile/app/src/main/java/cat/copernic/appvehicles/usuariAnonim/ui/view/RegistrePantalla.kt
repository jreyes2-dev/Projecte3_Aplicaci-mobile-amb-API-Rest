package cat.copernic.appvehicles.usuariAnonim.ui.view

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cat.copernic.appvehicles.R
import cat.copernic.appvehicles.usuariAnonim.ui.viewmodel.RegisterViewModel

/**
 * Representa l'estat de la interfície d'usuari per a la pantalla de registre.
 * * @property nomComplet Nom complet de l'usuari.
 * @property numeroIdentificacio DNI, NIE o passaport.
 * @property dataCaducitatId Data de caducitat del document d'identitat.
 * @property tipusLlicencia Categoria del carnet de conduir.
 * @property dataCaducitatLlicencia Data de caducitat del carnet de conduir.
 * @property numeroTargetaCredit Número de la targeta de crèdit per als pagaments.
 * @property adreca Adreça de residència.
 * @property nacionalitat País de nacionalitat.
 * @property email Correu electrònic de contacte.
 * @property password Contrasenya de la cuenta.
 * @property isLoading Indica si hi ha una operació de registre en curs.
 * @property errorMessage Missatge d'error a mostrar, si n'hi ha.
 * @property isSuccess Indica si el registre s'ha completat correctament.
 * @property fotoIdentificacioUri URI de la imatge del document d'identitat.
 * @property fotoLlicenciaUri URI de la imatge del carnet de conduir.
 * @property fotoPerfilUri URI de la imatge de perfil de l'usuari.
 */
data class RegisterUiState(
    val nomComplet: String = "",
    val numeroIdentificacio: String = "",
    val dataCaducitatId: String = "",
    val tipusLlicencia: String = "",
    val dataCaducitatLlicencia: String = "",
    val numeroTargetaCredit: String = "",
    val adreca: String = "",
    val nacionalitat: String = "",
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false,
    val fotoIdentificacioUri: String? = null,
    val fotoLlicenciaUri: String? = null,
    val fotoPerfilUri: String? = null
)

/**
 * Pantalla principal del flux de registre organitzada en diversos passos.
 * * @param viewModel El ViewModel que gestiona la lògica i l'estat del registre.
 * @param onNavigateBack Funció a executar per tornar a la pantalla anterior.
 * @param onRegisterSuccess Funció a executar quan el registre s'ha realitzat correctament.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel,
    onNavigateBack: () -> Unit,
    onRegisterSuccess: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    val context = androidx.compose.ui.platform.LocalContext.current

    var currentStep by remember { mutableIntStateOf(1) }
    val totalSteps = 3

    val errNomCompletBuit = stringResource(R.string.err_nom_complet_buit)
    val errNomFormat = stringResource(R.string.err_nom_format)
    val errNumeroIdBuit = stringResource(R.string.err_numero_id_buit)
    val errFormatData = stringResource(R.string.err_format_data)
    val errDataPassada = stringResource(R.string.err_data_passada)
    val errDataInvalida = stringResource(R.string.err_data_invalida)

    val errAdrecaBuida = stringResource(R.string.err_adreca_buida)
    val errNacionalitatBuida = stringResource(R.string.err_nacionalitat_buida)
    val errEmailBuit = stringResource(R.string.err_email_buit)
    val errEmailFormat = stringResource(R.string.err_email_format)
    val errPasswordBuida = stringResource(R.string.err_password_buida)

    val errTipusLlicenciaBuit = stringResource(R.string.err_tipus_llicencia_buit)
    val errLlicenciaCaducada = stringResource(R.string.err_llicencia_caducada)
    val errTargetaBuida = stringResource(R.string.err_targeta_buida)
    val errTargetaFormat = stringResource(R.string.err_targeta_format)

    val errFotoLlicencia = stringResource(R.string.err_foto_llicencia)
    val errFotoIdentificacio = stringResource(R.string.err_foto_identificacio)

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            onRegisterSuccess()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.registre_pas_de, currentStep, totalSteps)) },
                navigationIcon = {
                    IconButton(onClick = {
                        if (currentStep > 1) currentStep-- else onNavigateBack()
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.tornar_enrere))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        bottomBar = {
            BottomAppBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    if (currentStep > 1) {
                        OutlinedButton(onClick = { currentStep-- }, enabled = !uiState.isLoading) {
                            Text(stringResource(R.string.enrere))
                        }
                    } else {
                        Spacer(modifier = Modifier.width(8.dp))
                    }

                    if (currentStep < totalSteps) {

                        Button(
                            onClick = {
                                val llistaErrors = mutableListOf<String>()
                                val regexNom = "^[a-zA-ZÀ-ÿ\\s]+$".toRegex()
                                val regexData = "^\\d{4}-\\d{2}-\\d{2}$".toRegex()

                                if (currentStep == 1) {
                                    if (uiState.nomComplet.isBlank())
                                        llistaErrors.add("• $errNomCompletBuit")
                                    else if (!uiState.nomComplet.matches(regexNom))
                                        llistaErrors.add("• $errNomFormat")

                                    if (uiState.numeroIdentificacio.isBlank())
                                        llistaErrors.add("• $errNumeroIdBuit")

                                    if (!uiState.dataCaducitatId.matches(regexData)) {
                                        llistaErrors.add("• $errFormatData")
                                    }

                                    if (uiState.fotoIdentificacioUri == null) {
                                        llistaErrors.add("• $errFotoIdentificacio")
                                    } else {
                                        try {
                                            val dataParsed = java.time.LocalDate.parse(uiState.dataCaducitatId)
                                            if (dataParsed.isBefore(java.time.LocalDate.now()))
                                                llistaErrors.add("• $errDataPassada")
                                        } catch (e: Exception) {
                                            llistaErrors.add("• $errDataInvalida")
                                        }
                                    }
                                } else if (currentStep == 2) {
                                    if (uiState.adreca.isBlank())
                                        llistaErrors.add("• $errAdrecaBuida")

                                    if (uiState.nacionalitat.isBlank())
                                        llistaErrors.add("• $errNacionalitatBuida")

                                    if (uiState.email.isBlank())
                                        llistaErrors.add("• $errEmailBuit")
                                    else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(uiState.email).matches())
                                        llistaErrors.add("• $errEmailFormat")

                                    if (uiState.password.isBlank())
                                        llistaErrors.add("• $errPasswordBuida")
                                }

                                if (llistaErrors.isNotEmpty()) {
                                    val missatgeFinal = llistaErrors.joinToString(separator = "\n")
                                    viewModel.updateState(uiState.copy(errorMessage = missatgeFinal))
                                } else {
                                    viewModel.updateState(uiState.copy(errorMessage = null))
                                    currentStep++
                                }
                            },
                            enabled = !uiState.isLoading
                        ) {
                            Text(stringResource(R.string.seg_ent))
                        }
                    } else {
                        Button(
                            onClick = {
                                val llistaErrors = mutableListOf<String>()
                                val regexData = "^\\d{4}-\\d{2}-\\d{2}$".toRegex()

                                if (uiState.tipusLlicencia.isBlank())
                                    llistaErrors.add("• $errTipusLlicenciaBuit")

                                if (!uiState.dataCaducitatLlicencia.matches(regexData)) {
                                    llistaErrors.add("• $errFormatData")
                                } else {
                                    try {
                                        val dataParsed = java.time.LocalDate.parse(uiState.dataCaducitatLlicencia)
                                        if (dataParsed.isBefore(java.time.LocalDate.now()))
                                            llistaErrors.add("• $errLlicenciaCaducada")
                                    } catch (e: Exception) {
                                        llistaErrors.add("• $errDataInvalida")
                                    }
                                }

                                if (uiState.fotoLlicenciaUri == null) {
                                    llistaErrors.add("• $errFotoLlicencia")
                                }

                                val regexTargeta = "^[0-9]{13,19}$".toRegex()
                                if (uiState.numeroTargetaCredit.isBlank()) {
                                    llistaErrors.add("• $errTargetaBuida")
                                } else if (!uiState.numeroTargetaCredit.matches(regexTargeta)) {
                                    llistaErrors.add("• $errTargetaFormat")
                                }

                                if (llistaErrors.isNotEmpty()) {
                                    val missatgeFinal = llistaErrors.joinToString(separator = "\n")
                                    viewModel.updateState(uiState.copy(errorMessage = missatgeFinal))
                                } else {
                                    viewModel.updateState(uiState.copy(errorMessage = null))
                                    viewModel.register(context)
                                }
                            },
                            enabled = !uiState.isLoading
                        ) {
                            Text(stringResource(R.string.finalitzar))
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            if (uiState.errorMessage != null) {
                Text(
                    text = uiState.errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            when (currentStep) {
                1 -> Pas1DadesPersonals(uiState) { viewModel.updateState(it) }
                2 -> Pas3DadesContacte(uiState) { viewModel.updateState(it) }
                3 -> Pas2DadesConduccio(uiState) { viewModel.updateState(it) }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
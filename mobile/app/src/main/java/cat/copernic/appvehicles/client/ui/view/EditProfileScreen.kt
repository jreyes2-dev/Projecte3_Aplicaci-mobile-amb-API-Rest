package cat.copernic.appvehicles.client.ui.view

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cat.copernic.appvehicles.R
import cat.copernic.appvehicles.client.ui.viewmodel.EditProfileViewModel
import coil.compose.AsyncImage
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import cat.copernic.appvehicles.core.composables.ImageUploadOrPreview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    onLoggedOut: () -> Unit = {} // Lo dejamos por si lo usas, aunque la navegación va por State
) {
    val vm: EditProfileViewModel = viewModel()
    val state by vm.uiState.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.messageKey) {
        state.messageKey?.let { key ->
            // Mostramos el mensaje (traducido con la función que ya tenemos)
            snackbarHostState.showSnackbar(
                message = "Cambios guardados correctamente" // O usa stringResource(messageKeyToRes(key))
            )
            // Opcional: Podrías limpiar el mensaje en el VM aquí si quisieras que no se repita
        }
    }

    var showLogoutDialog by remember { mutableStateOf(false) }

    // Controladores de los DatePickers
    var showDatePickerDni by remember { mutableStateOf(false) }
    val datePickerStateDni = rememberDatePickerState()
    var showDatePickerLicense by remember { mutableStateOf(false) }
    val datePickerStateLicense = rememberDatePickerState()

    // Controladores de los Desplegables
    var expaditNacionalitat by remember { mutableStateOf(false) }
    val llistaPaisos = remember { java.util.Locale.getISOCountries().map { isoCode -> java.util.Locale("", isoCode).displayCountry }.sorted() }

    var expaditLlicencia by remember { mutableStateOf(false) }
    val llistatLlicencies = listOf("AM", "A1", "A2", "A", "B1", "B", "C1", "C", "D1", "D")

    val pickPhoto = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri -> vm.onPhotoPicked(uri?.toString()) }
    val pickDniImage = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri -> vm.onDniImagePicked(uri?.toString()) }
    val pickLicenseImage = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri -> vm.onLicenseImagePicked(uri?.toString()) }

    LaunchedEffect(Unit) { vm.loadProfile() }

    // --- DIÁLOGOS DE FECHA ---
    if (showDatePickerDni) {
        DatePickerDialog(
            onDismissRequest = { showDatePickerDni = false },
            confirmButton = {
                TextButton(onClick = {
                    showDatePickerDni = false
                    datePickerStateDni.selectedDateMillis?.let { millis ->
                        val date = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate()
                        vm.onFieldChange(dataCaducitatDni = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                    }
                }) { Text(stringResource(R.string.acceptar)) }
            },
            dismissButton = { TextButton(onClick = { showDatePickerDni = false }) { Text(stringResource(R.string.cancel_lar)) } }
        ) { DatePicker(state = datePickerStateDni) }
    }

    if (showDatePickerLicense) {
        DatePickerDialog(
            onDismissRequest = { showDatePickerLicense = false },
            confirmButton = {
                TextButton(onClick = {
                    showDatePickerLicense = false
                    datePickerStateLicense.selectedDateMillis?.let { millis ->
                        val date = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate()
                        vm.onFieldChange(dataCaducitatCarnet = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                    }
                }) { Text(stringResource(R.string.acceptar)) }
            },
            dismissButton = { TextButton(onClick = { showDatePickerLicense = false }) { Text(stringResource(R.string.cancel_lar)) } }
        ) { DatePicker(state = datePickerStateLicense) }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.edit_profile_title)) },
                actions = {
                    IconButton(onClick = { showLogoutDialog = true }) { Icon(Icons.Default.Logout, stringResource(R.string.logout)) }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(padding).padding(16.dp)
        ) {

            if (state.isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(12.dp))
            }

            // --- MOSTRAR LISTA DE ERRORES (Como en Registro) ---
            if (state.errorKeys.isNotEmpty()) {
                val missatgeFinal = state.errorKeys.map { "• " + stringResource(errorKeyToRes(it)) }.joinToString(separator = "\n")
                Text(
                    text = missatgeFinal,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(16.dp))
            }

            state.messageKey?.let {
                Text(text = stringResource(messageKeyToRes(it)), color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
            }

            // Extra: componente imagen (foto cliente)
            Spacer(Modifier.height(8.dp))
            Text(stringResource(R.string.profile_photo), style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(8.dp))

            ImageUploadOrPreview(
                label = stringResource(R.string.profile_photo),
                imageUri = state.photoUri,
                onUploadClick = { pickPhoto.launch("image/*") },
                onDeleteClick = { vm.onPhotoPicked(null) }
            )
            Spacer(Modifier.height(24.dp)) // Gran espacio antes de los campos de texto

            // --- CAMPOS DE TEXTO ---
            OutlinedTextField(value = state.nomComplet, onValueChange = { vm.onFieldChange(nomComplet = it) }, label = { Text(stringResource(R.string.full_name_label)) }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(value = state.email, onValueChange = { }, label = { Text(stringResource(R.string.email_readonly)) }, enabled = false, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(value = state.telefon, onValueChange = { vm.onFieldChange(telefon = it) }, label = { Text(stringResource(R.string.phone_label)) }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(value = state.adreca, onValueChange = { vm.onFieldChange(adreca = it) }, label = { Text(stringResource(R.string.address_label)) }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))

            // --- DESPLEGABLE NACIONALIDAD ---
            ExposedDropdownMenuBox(expanded = expaditNacionalitat, onExpandedChange = { expaditNacionalitat = !expaditNacionalitat }) {
                OutlinedTextField(
                    value = state.nacionalitat, onValueChange = {}, readOnly = true, label = { Text(stringResource(R.string.nationality_label)) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expaditNacionalitat) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(expanded = expaditNacionalitat, onDismissRequest = { expaditNacionalitat = false }) {
                    llistaPaisos.forEach { pais ->
                        DropdownMenuItem(
                            text = { Text(pais) },
                            onClick = { vm.onFieldChange(nacionalitat = pais); expaditNacionalitat = false }
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            Text(stringResource(R.string.documentation), style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))

            // --- FECHA DNI (CALENDARIO) ---
            OutlinedTextField(
                value = state.dataCaducitatDni, onValueChange = { }, readOnly = true, label = { Text(stringResource(R.string.id_expiry_label)) }, modifier = Modifier.fillMaxWidth(),
                trailingIcon = { IconButton(onClick = { showDatePickerDni = true }) { Icon(Icons.Default.DateRange, stringResource(R.string.seleccionar_data)) } }
            )

            Spacer(Modifier.height(20.dp)) // Espacio extra para separar del campo de arriba
            Text(stringResource(R.string.id_image), style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(8.dp))

            ImageUploadOrPreview(
                label = stringResource(R.string.id_image),
                imageUri = state.dniImageUri,
                onUploadClick = { pickDniImage.launch("image/*") },
                onDeleteClick = { vm.onDniImagePicked(null) }
            )
            Spacer(Modifier.height(24.dp)) // Gran espacio antes del siguiente desplegable

            // --- DESPLEGABLE TIPO LICENCIA ---
            ExposedDropdownMenuBox(expanded = expaditLlicencia, onExpandedChange = { expaditLlicencia = !expaditLlicencia }) {
                OutlinedTextField(
                    value = state.tipusCarnetConduir, onValueChange = {}, readOnly = true, label = { Text(stringResource(R.string.license_type_label)) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expaditLlicencia) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(expanded = expaditLlicencia, onDismissRequest = { expaditLlicencia = false }) {
                    llistatLlicencies.forEach { llicencia ->
                        DropdownMenuItem(
                            text = { Text(llicencia) },
                            onClick = { vm.onFieldChange(tipusCarnetConduir = llicencia); expaditLlicencia = false }
                        )
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            // --- FECHA LICENCIA (CALENDARIO) ---
            OutlinedTextField(
                value = state.dataCaducitatCarnet, onValueChange = { }, readOnly = true, label = { Text(stringResource(R.string.license_expiry_label)) }, modifier = Modifier.fillMaxWidth(),
                trailingIcon = { IconButton(onClick = { showDatePickerLicense = true }) { Icon(Icons.Default.DateRange, stringResource(R.string.seleccionar_data)) } }
            )

            Spacer(Modifier.height(20.dp)) // Espacio extra para separar del campo de arriba
            Text(stringResource(R.string.license_image), style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(8.dp))

            ImageUploadOrPreview(
                label = stringResource(R.string.license_image),
                imageUri = state.licenseImageUri,
                onUploadClick = { pickLicenseImage.launch("image/*") },
                onDeleteClick = { vm.onLicenseImagePicked(null) }
            )
            Spacer(Modifier.height(32.dp)) // Mucho espacio antes de la sección de pago

            Text(stringResource(R.string.payment), style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))

            // --- TARGETA DE CRÉDITO (Filtro numérico) ---
            OutlinedTextField(
                value = state.numeroTargetaCredit,
                onValueChange = { text ->
                    val nomesNumeros = text.filter { it.isDigit() }
                    if (nomesNumeros.length <= 19) vm.onFieldChange(numeroTargetaCredit = nomesNumeros)
                },
                label = { Text(stringResource(R.string.credit_card_label)) },
                modifier = Modifier.fillMaxWidth(), singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Spacer(Modifier.height(16.dp))

            Button(onClick = { vm.saveChanges() }, modifier = Modifier.fillMaxWidth(), enabled = !state.isLoading) {
                Text(stringResource(R.string.save_changes))
            }
        }
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text(stringResource(R.string.logout_confirm_title)) },
            text = { Text(stringResource(R.string.logout_confirm_text)) },
            confirmButton = {
                TextButton(onClick = { showLogoutDialog = false; vm.logout(onSuccess = { onLoggedOut() }) }) { Text(stringResource(R.string.logout)) }
            },
            dismissButton = { TextButton(onClick = { showLogoutDialog = false }) { Text(stringResource(R.string.cancel)) } }
        )
    }
}

private fun errorKeyToRes(key: String): Int = when (key) {
    "session_missing_dni", "session_missing_email" -> R.string.error_session_missing_dni
    "full_name_required" -> R.string.err_nom_complet_buit
    "invalid_name_format" -> R.string.err_nom_format
    "invalid_card_format" -> R.string.err_targeta_format
    "profile_load_error" -> R.string.error_profile_load
    "profile_save_error" -> R.string.error_profile_save
    "invalid_date_format", "invalid_date_format_dni" -> R.string.err_format_data
    "license_expired" -> R.string.err_llicencia_caducada
    "dni_expired" -> R.string.err_data_passada
    "invalid_date", "invalid_date_dni" -> R.string.err_data_invalida
    else -> R.string.error_generic
}

private fun messageKeyToRes(key: String): Int = when (key) {
    "profile_saved" -> R.string.profile_saved
    else -> R.string.profile_saved
}
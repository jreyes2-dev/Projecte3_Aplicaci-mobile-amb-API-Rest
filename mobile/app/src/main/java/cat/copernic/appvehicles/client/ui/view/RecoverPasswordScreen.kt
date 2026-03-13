package cat.copernic.appvehicles.client.ui.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cat.copernic.appvehicles.R
import cat.copernic.appvehicles.client.ui.viewmodel.RecoverPasswordViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecoverPasswordScreen(
    onBackClick: () -> Unit = {}
) {
    val vm: RecoverPasswordViewModel = viewModel()
    val state by vm.uiState.collectAsState()
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.recover_title)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {

                    Text(
                        text = stringResource(R.string.recover_subtitle),
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = stringResource(R.string.recover_hint),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = state.email,
                        onValueChange = vm::onEmailChanged,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text(stringResource(R.string.email_label)) },
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = vm::sendRecoveryEmail,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !state.isLoading
                    ) {
                        Text(stringResource(R.string.recover_action))
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = stringResource(R.string.reset_password_title),
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = state.token,
                        onValueChange = vm::onTokenChanged,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text(stringResource(R.string.recovery_token_label)) },
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = state.newPassword,
                        onValueChange = vm::onNewPasswordChanged,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text(stringResource(R.string.new_password_label)) },
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = state.confirmPassword,
                        onValueChange = vm::onConfirmPasswordChanged,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text(stringResource(R.string.confirm_password_label)) },
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    state.errorKey?.let {
                        Text(
                            text = stringResource(errorKeyToRes(it)),
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    state.successKey?.let {
                        Text(
                            text = stringResource(successKeyToRes(it)),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    Button(
                        onClick = vm::resetPassword,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !state.isLoading
                    ) {
                        Text(stringResource(R.string.reset_password_action))
                    }

                    if (state.isLoading) {
                        Spacer(modifier = Modifier.height(12.dp))
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    }
                }
            }
        }
    }
}

private fun errorKeyToRes(key: String): Int = when (key) {
    "email_required" -> R.string.error_email_required
    "email_invalid" -> R.string.error_email_invalid
    "recover_failed" -> R.string.error_recover_failed
    "token_required" -> R.string.error_token_required
    "password_required" -> R.string.error_password_required
    "password_too_short" -> R.string.error_password_too_short
    "password_mismatch" -> R.string.error_password_mismatch
    "reset_failed" -> R.string.error_reset_failed
    else -> R.string.error_generic
}

private fun successKeyToRes(key: String): Int = when (key) {
    "recover_sent" -> R.string.recover_success
    "password_reset_ok" -> R.string.password_reset_success
    else -> R.string.recover_success
}